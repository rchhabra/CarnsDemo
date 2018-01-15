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
 *
 */

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelfacades.promotion.TravelPromotionsFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler class to populate OfferPricingInfoData for each category of products available to offer as ancillaries.
 */
public class OfferGroupOfferPricingInfoHandler implements AncillarySearchHandler
{

	private static final Logger LOGGER = Logger.getLogger(OfferGroupOfferPricingInfoHandler.class);

	private TravelPromotionsFacade travelPromotionsFacade;
	private ProductService productService;
	private CategoryService categoryService;
	private Converter<ProductModel, ProductData> productConverter;
	private TravelCartService travelCartService;
	private TravelCommerceStockService travelCommerceStockService;

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		final boolean applyPromotion = getTravelPromotionsFacade().isCurrentUserEligibleForTravelPromotions();
		final List<OfferGroupData> offerGroups = getFilteredOfferGroups(offerResponseData);

		for (final OfferGroupData offerGroupData : offerGroups)
		{
			final List<ProductModel> productsForCategory = getProductService()
					.getProductsForCategory(getCategoryService().getCategoryForCode(offerGroupData.getCode()));
			createOfferPricingInfos(applyPromotion, productsForCategory, offerGroupData);
		}
	}

	/**
	 * Returns the list of OfferGroups of {@link AddToCartCriteriaType} PER_BOOKING or PER_PAX
	 *
	 * @param offerResponseData
	 * @return
	 */
	protected List<OfferGroupData> getFilteredOfferGroups(final OfferResponseData offerResponseData)
	{
		return offerResponseData.getOfferGroups().stream()
				.filter(offerGroup -> offerGroup.getTravelRestriction() != null
						&& StringUtils.isNotBlank(offerGroup.getTravelRestriction().getAddToCartCriteria())
						&& (StringUtils.equalsIgnoreCase(offerGroup.getTravelRestriction().getAddToCartCriteria(),
								AddToCartCriteriaType.PER_BOOKING.getCode())
						|| StringUtils.equalsIgnoreCase(offerGroup.getTravelRestriction().getAddToCartCriteria(),
								AddToCartCriteriaType.PER_PAX.getCode())))
				.collect(Collectors.toList());
	}

	/**
	 * Creates the offerPricingInfos and set it on the offerGroupData for the given products
	 *
	 * @param applyPromotion
	 * @param productsForCategory
	 * @param offerGroupData
	 */
	protected void createOfferPricingInfos(final boolean applyPromotion, final List<ProductModel> productsForCategory,
			final OfferGroupData offerGroupData)
	{
		final List<ProductModel> qualifiedProductsInOffer = getAvailableProducts(productsForCategory, offerGroupData);

		final List<OfferPricingInfoData> offerPricingInfos = new ArrayList<OfferPricingInfoData>();
		for (final ProductModel productModel : qualifiedProductsInOffer)
		{
			final OfferPricingInfoData offerPricingInfoData = new OfferPricingInfoData();
			final ProductData productData = getProductConverter().convert(productModel);
			if (applyPromotion)
			{
				getTravelPromotionsFacade().populatePotentialPromotions(productModel, productData);
			}
			offerPricingInfoData.setProduct(productData);

			offerPricingInfos.add(offerPricingInfoData);
		}

		offerGroupData.setOfferPricingInfos(offerPricingInfos);

	}

	/**
	 * Returns the list of available products based on the stock level
	 *
	 * @param productsForCategory
	 * @param offerGroupData
	 * @return the list of available products
	 */
	protected List<ProductModel> getAvailableProducts(final List<ProductModel> productsForCategory,
			final OfferGroupData offerGroupData)
	{
		final List<ProductModel> availableProductsInOffer = new ArrayList<>();
		for (final ProductModel productModel : productsForCategory)
		{
			try
			{
				final Long stockLevel = getTravelCommerceStockService().getStockLevel(productModel, Collections.emptyList());
				if ((stockLevel == null || stockLevel.intValue() > 0) && !availableProductsInOffer.contains(productModel))
				{
					availableProductsInOffer.add(productModel);
				}
			}
			catch (final StockLevelNotFoundException slNotFoundExec)
			{
				LOGGER.debug("No Stock configured for Product: " + productModel.getCode() + " in default warehouse", slNotFoundExec);
			}
		}
		return availableProductsInOffer;
	}

	/**
	 * @return the commonI18NService
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
	public void setTravelPromotionsFacade(final TravelPromotionsFacade travelPromotionsFacade)
	{
		this.travelPromotionsFacade = travelPromotionsFacade;
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
	 * @return the travelCommerceStockService
	 */
	protected TravelCommerceStockService getTravelCommerceStockService()
	{
		return travelCommerceStockService;
	}

	/**
	 * @param travelCommerceStockService
	 *           the travelCommerceStockService to set
	 */
	public void setTravelCommerceStockService(final TravelCommerceStockService travelCommerceStockService)
	{
		this.travelCommerceStockService = travelCommerceStockService;
	}

}
