/*
* [y] hybris Platform
*
* Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* Hybris ("Confidential Information"). You shall not disclose such
* Confidential Information and shall use it only in accordance with the
* terms of the license agreement you entered into with SAP Hybris.
*
*/

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelfacades.promotion.TravelPromotionsFacade;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.TransportOfferingService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler class to populate OfferPricingInfoData for each category of products available to offer as ancillaries.
 */
public class OfferPricingInfoHandler implements AncillarySearchHandler
{
	private static final Logger LOGGER = Logger.getLogger(OfferPricingInfoHandler.class);

	private TravelPromotionsFacade travelPromotionsFacade;
	private ProductService productService;
	private CategoryService categoryService;
	private TransportOfferingService transportOfferingService;
	private Converter<ProductModel, ProductData> productConverter;
	private TravelCartService travelCartService;
	private TravelRulesService travelRulesService;

	/**
	 * Creates and populates OfferPricingInfoData for products of each category.
	 *
	 * Retrieve list of products for each categoryCode (in this case its offerGroupData.getCode())
	 *
	 * Collect common products across all transportOfferings, whose product code matches with the products from above
	 * list and stock level is more than zero.
	 *
	 * For each of those collected products create OfferPricingInfoData and set the productData.
	 */
	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		final boolean applyPromotion = getTravelPromotionsFacade().isCurrentUserEligibleForTravelPromotions();
		final List<OfferGroupData> offerGroups = offerResponseData.getOfferGroups();
		for (final OfferGroupData offerGroupData : offerGroups)
		{
			final List<ProductModel> productsForCategory = getProductService()
					.getProductsForCategory(getCategoryService().getCategoryForCode(offerGroupData.getCode()));
			if (CollectionUtils.isEmpty(offerGroupData.getOriginDestinationOfferInfos()))
			{
				continue;
			}
			for (final OriginDestinationOfferInfoData originDestinationOfferInfo : offerGroupData.getOriginDestinationOfferInfos())
			{
				createOfferPricingInfos(applyPromotion, productsForCategory, originDestinationOfferInfo);
			}
		}
		showProducts(offerRequestData, offerResponseData);
	}

	/**
	 * This method shows products that are evaluated using rules
	 *
	 * @param offerRequestData
	 * @param offerResponseData
	 */
	protected void showProducts(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		try
		{
			final List<String> returnedProducts = getTravelRulesService().showProducts(offerRequestData);
			discardProducts(returnedProducts, offerResponseData);
		}
		catch (final NullPointerException e)
		{
			LOGGER.debug("No offerRequest Data: " + offerRequestData + "provided");
		}
	}

	/**
	 * This method filters out the products that should be evaluated as part of the rules but not returned after rule evaluation
	 *
	 * @param returnedProducts
	 * @param offerResponseData
	 */
	protected void discardProducts(final List<String> returnedProducts, final OfferResponseData offerResponseData)
	{
		final List<OfferGroupData> offerGroups = offerResponseData.getOfferGroups();
		for (final OfferGroupData offerGroupData : offerGroups)
		{
			if (CollectionUtils.isEmpty(offerGroupData.getOriginDestinationOfferInfos()))
			{
				continue;
			}
			for (final OriginDestinationOfferInfoData originDestinationOfferInfo : offerGroupData.getOriginDestinationOfferInfos())
			{
				final List<OfferPricingInfoData> pricedOffersToRemove = originDestinationOfferInfo.getOfferPricingInfos().stream()
						.filter(offerPricingInfo -> !offerPricingInfo.getProduct().isIgnoreRules()
								&& !returnedProducts.contains(offerPricingInfo.getProduct().getCode()))
						.collect(Collectors.toList());
				originDestinationOfferInfo.getOfferPricingInfos().removeAll(pricedOffersToRemove);
			}
		}
	}

	protected void createOfferPricingInfos(final boolean applyPromotion, final List<ProductModel> productsForCategory,
			final OriginDestinationOfferInfoData originDestinationOfferInfo)
	{
		final List<ProductModel> qualifiedProductsInOffer = getQualifiedProductsForOfferGroup(productsForCategory,
				originDestinationOfferInfo);
		final List<OfferPricingInfoData> pricedOffers = new ArrayList<OfferPricingInfoData>();
		for (final ProductModel productModel : qualifiedProductsInOffer)
		{
			final OfferPricingInfoData offerPricingInfoData = new OfferPricingInfoData();
			final ProductData productData = getProductConverter().convert(productModel);
			if (applyPromotion)
			{
				getTravelPromotionsFacade().populatePotentialPromotions(productModel, productData);
			}
			offerPricingInfoData.setProduct(productData);
			pricedOffers.add(offerPricingInfoData);
		}
		originDestinationOfferInfo.setOfferPricingInfos(pricedOffers);
	}

	/**
	 * This method collects the products that belong to a category(offerGroupData.getCode()), common across all
	 * transportOfferings and stockLevel more than zero.
	 *
	 * @param productsForCategory
	 *           List of ProductModels which belong to a category
	 * @param originDestinationOfferInfo
	 *           OriginDestinationOfferInfoData object, holds list of TransportOfferings.
	 * @return List of ProductModel.
	 */
	protected List<ProductModel> getQualifiedProductsForOfferGroup(final List<ProductModel> productsForCategory,
			final OriginDestinationOfferInfoData originDestinationOfferInfo)
	{
		final Map<String, List<ProductModel>> productsPerTransportOffering = getAvailableProductsForTransportOffering(
				productsForCategory, originDestinationOfferInfo);

		return getCommonProducts(productsPerTransportOffering);
	}

	/**
	 * This method collects the common products across transportOfferings in case of multisectors or returns the
	 * available products if single sector.
	 *
	 * @param productsPerTransportOffering
	 * @return
	 */
	protected List<ProductModel> getCommonProducts(final Map<String, List<ProductModel>> productsPerTransportOffering)
	{

		if (productsPerTransportOffering.entrySet().stream().anyMatch(entry -> entry.getValue().isEmpty()))
		{
			return Collections.emptyList();
		}

		final List<ProductModel> qualifiedProductsInOffer = new ArrayList<ProductModel>();
		for (final Entry<String, List<ProductModel>> entry : productsPerTransportOffering.entrySet())
		{
			if (qualifiedProductsInOffer.isEmpty())
			{
				qualifiedProductsInOffer.addAll(entry.getValue());
			}
			else
			{
				final List<ProductModel> commonProducts = entry.getValue().stream().filter(qualifiedProductsInOffer::contains)
						.collect(Collectors.<ProductModel> toList());
				qualifiedProductsInOffer.clear();
				qualifiedProductsInOffer.addAll(commonProducts);
			}
		}
		return qualifiedProductsInOffer;
	}

	/**
	 * This method returns available products for each transportOffering. The product in offer should be of category of
	 * products as retrieved using the category code(offerGroupData.getCode()) and the available stockLevel of the
	 * product should be more than zero.
	 *
	 * @param productsForCategory
	 * @param originDestinationOfferInfo
	 * @return
	 */
	protected Map<String, List<ProductModel>> getAvailableProductsForTransportOffering(
			final List<ProductModel> productsForCategory, final OriginDestinationOfferInfoData originDestinationOfferInfo)
	{
		final Map<String, List<ProductModel>> productsPerTransportOffering = new HashMap<>();

		for (final TransportOfferingData transportOfferingData : originDestinationOfferInfo.getTransportOfferings())
		{
			final TransportOfferingModel transportOfferingModel = getTransportOfferingService()
					.getTransportOffering(transportOfferingData.getCode());
			final List<ProductModel> availableProductsInOffer = getAvailableProductsInOffer(productsForCategory,
					transportOfferingModel);
			productsPerTransportOffering.put(transportOfferingModel.getCode(), availableProductsInOffer);
		}
		return productsPerTransportOffering;
	}

	protected List<ProductModel> getAvailableProductsInOffer(final List<ProductModel> productsForCategory,
			final TransportOfferingModel transportOfferingModel)
	{
		final List<ProductModel> availableProductsInOffer = new ArrayList<>();

		for (final ProductModel productModel : productsForCategory)
		{
			try
			{
				final Long stockLevel = getTravelCartService().getAvailableStock(productModel, transportOfferingModel);
				if ((stockLevel == null || stockLevel.intValue() > 0) && !availableProductsInOffer.contains(productModel))
				{
					availableProductsInOffer.add(productModel);
				}
			}
			catch (final StockLevelNotFoundException slNotFoundExec)
			{
				LOGGER.debug("No Stock configured for Product: " + productModel.getCode() + " in TransportOffering: "
						+ transportOfferingModel.getCode(), slNotFoundExec);
			}

		}
		return availableProductsInOffer;
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the categoryService
	 */
	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	/**
	 * @param categoryService
	 *           the categoryService to set
	 */
	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	/**
	 * @return the transportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * @param transportOfferingService
	 *           the transportOfferingService to set
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	/**
	 * @return the productConverter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * @param productConverter
	 *           the productConverter to set
	 */
	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	/**
	 * @return the travelPromotionsFacade
	 */
	protected TravelPromotionsFacade getTravelPromotionsFacade()
	{
		return travelPromotionsFacade;
	}

	/**
	 * @param travelPromotionsFacade
	 *           the travelPromotionsFacade to set
	 */
	@Required
	public void setTravelPromotionsFacade(final TravelPromotionsFacade travelPromotionsFacade)
	{
		this.travelPromotionsFacade = travelPromotionsFacade;
	}

	/**
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * @param travelCartService
	 *           the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * @return the travelRulesService
	 */
	protected TravelRulesService getTravelRulesService()
	{
		return travelRulesService;
	}

	/**
	 * @param travelRulesService
	 *           the travelRulesService to set
	 */
	@Required
	public void setTravelRulesService(final TravelRulesService travelRulesService)
	{
		this.travelRulesService = travelRulesService;
	}

}
