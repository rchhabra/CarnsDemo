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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.strategies.ProductsSortStrategy;
import de.hybris.platform.travelservices.enums.ProductType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler responsible for populating the Fare Prices for scheduled route and populates the Passenger breakdown
 * prices on {@link FareSelectionData}
 */
public abstract class AbstractFareInfoHandler implements FareSearchHandler
{
	private static final Logger LOG = Logger.getLogger(AbstractFareInfoHandler.class);

	private TravelCommercePriceFacade travelCommercePriceFacade;
	private BundleTemplateService bundleTemplateService;
	private ProductsSortStrategy<ProductData> productsSortStrategy;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private CommonI18NService commonI18NService;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			if (!pricedItinerary.isAvailable())
			{
				continue;
			}
			for (final ItineraryPricingInfoData itineraryPricingInfoData : pricedItinerary.getItineraryPricingInfos())
			{
				if (!itineraryPricingInfoData.isAvailable())
				{
					continue;
				}

				populateProductPricesAndSort(pricedItinerary, itineraryPricingInfoData);

				if (isItineraryPricingInfoAvailable(itineraryPricingInfoData))
				{
					populatePTCFareBreakDownData(itineraryPricingInfoData, fareSearchRequestData);
				}
				else
				{
					itineraryPricingInfoData.setAvailable(Boolean.FALSE);
				}
				removeUnusedFareProducts(itineraryPricingInfoData);
			}
		}
	}

	/**
	 * This method populates the price information for all the fare products and ancillary products. The prices are
	 * searched in the order of transportoffering, sector, route and default price. The first available price will be set
	 * to the product.
	 *
	 * @param productList
	 *           the product list
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param sectorCode
	 *           the sector code
	 * @param routeCode
	 *           the route code
	 * @param bundleTemplate
	 *           the bundle template
	 */
	protected abstract void populateProductPricesForOptions(final List<? extends ProductData> productList,
			final String transportOfferingCode, final String sectorCode, final String routeCode,
			final BundleTemplateModel bundleTemplate);

	/**
	 * This method populates the price information for all the fare products and ancillary products. The prices are
	 * searched for the search key and search value.
	 *
	 * @param productList
	 *           the product list
	 * @param searchKey
	 *           the search key
	 * @param searchValue
	 *           the search value
	 * @param bundleTemplate
	 *           the bundle template
	 */
	protected void populateProductPrices(final List<? extends ProductData> productList, final String searchKey,
			final String searchValue, final BundleTemplateModel bundleTemplate)
	{
		for (final ProductData productData : productList)
		{
			PriceInformation priceInfo = null;
			//if bundleTemplate(fareProductBundleTemplate/ancillaryProductBundleTemplate) is not null, first check
			// if ChangeProductPriceBundleRule is found for this bundleTemplate
			//and current product in the loop, if found, set that as price of product
			if (bundleTemplate != null
					&& !StringUtils.equalsIgnoreCase(ProductType.FARE_PRODUCT.getCode(), productData.getProductType()))
			{
				priceInfo = getTravelCommercePriceFacade().getPriceInformationByProductPriceBundleRule(bundleTemplate,
						productData.getCode());
			}
			if (priceInfo == null)
			{
				priceInfo = getTravelCommercePriceFacade().getPriceInformation(productData.getCode(), searchKey, searchValue);
				if (priceInfo == null)
				{
					priceInfo = getTravelCommercePriceFacade().getPriceInformation(productData.getCode(), null, null);
				}
				if (priceInfo == null)
				{
					logNoPriceForSearchKey(searchKey, searchValue, productData);
					continue;
				}

			}
			productData.setPrice(createPriceData(priceInfo));
		}
	}

	/**
	 * Method to populate the price information for all the fare products and ancillary products. The retrieved products
	 * are then sorted using strategy.
	 *
	 * @param pricedItinerary
	 *           the priced itinerary
	 * @param itineraryPricingInfoData
	 *           the itinerary pricing info data
	 */
	protected abstract void populateProductPricesAndSort(final PricedItineraryData pricedItinerary,
			final ItineraryPricingInfoData itineraryPricingInfoData);

	/**
	 * This method checks if the ItineraryPricingInfo is available or not by checking if the price is setup for the fare
	 * products and ancillary products.
	 *
	 * @param itineraryPricingInfoData
	 *           the itinerary pricing info data
	 * @return boolean boolean
	 */
	protected boolean isItineraryPricingInfoAvailable(final ItineraryPricingInfoData itineraryPricingInfoData)
	{
		for (final TravelBundleTemplateData bundleTemplateData : itineraryPricingInfoData.getBundleTemplates())
		{
			final FareProductData selectedFareProductData = getFareProductFromBundleTemplate(bundleTemplateData);

			final boolean validAncillaryProducts = checkValidAncillaryProducts(bundleTemplateData);

			if (selectedFareProductData == null || selectedFareProductData.getPrice() == null || !validAncillaryProducts)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Priced Itinerary Data Info " + itineraryPricingInfoData.getBundleTypeName() + " is not available");
				}
				return false;
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Check valid ancillary products boolean.
	 *
	 * @param bundleTemplateData
	 *           the bundle template data
	 * @return the boolean
	 */
	protected boolean checkValidAncillaryProducts(final TravelBundleTemplateData bundleTemplateData)
	{
		boolean validAncillaryProducts = Boolean.TRUE;

		for (final List<ProductData> productDataList : bundleTemplateData.getNonFareProducts().values())
		{
			for (final ProductData productData : productDataList)
			{
				if (productData.getPrice() == null)
				{
					validAncillaryProducts = Boolean.FALSE;
					break;
				}
			}
		}
		return validAncillaryProducts;
	}

	/**
	 * This method populates the fare information (base fare) per passenger, for every itinerary and pricing point, e.g.
	 * economy, economy plus.
	 *
	 * @param itineraryPricingInfoData
	 *           the itinerary pricing info data
	 * @param fareSearchRequestData
	 *           the fare search request data
	 */
	protected void populatePTCFareBreakDownData(final ItineraryPricingInfoData itineraryPricingInfoData,
			final FareSearchRequestData fareSearchRequestData)
	{
		if (Objects.isNull(itineraryPricingInfoData))
		{
			return;
		}
		double baseFare = 0;
		// Calculate the Total Base Fare from all the Fare Products
		final List<FareDetailsData> fareDetailsDataList = new ArrayList<FareDetailsData>();
		for (final TravelBundleTemplateData bundleTemplateData : itineraryPricingInfoData.getBundleTemplates())
		{
			final FareProductData selectedFareProductData = getFareProductFromBundleTemplate(bundleTemplateData);
			if (Objects.isNull(selectedFareProductData))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Fare Products not available for this bundle");
				}
				continue;
			}
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Fare: " + bundleTemplateData.getBundleType() + "-" + selectedFareProductData.getCode() + "-"
						+ selectedFareProductData.getPrice().getValue());
			}
			final FareDetailsData fareDetailsData = createFareDetails(selectedFareProductData);
			fareDetailsDataList.add(fareDetailsData);

			final double totalBundleFare = getTotalFromFareAndAncillaryProducts(bundleTemplateData, selectedFareProductData);
			baseFare = baseFare + totalBundleFare;
			selectedFareProductData.setSelected(true);
		}

		final FareInfoData fareInfoData = createFareInfo(fareDetailsDataList);

		final List<FareInfoData> fareInfoDataList = new ArrayList<FareInfoData>();
		fareInfoDataList.add(fareInfoData);

		final List<PTCFareBreakdownData> ptcFareBreakdownDataList = createPTCFareBreakDownList(fareInfoDataList, baseFare,
				fareSearchRequestData);

		itineraryPricingInfoData.setPtcFareBreakdownDatas(ptcFareBreakdownDataList);
	}

	/**
	 * This method removes all un-selected fare products from bundleTemplateData
	 *
	 * @param itineraryPricingInfoData
	 *           ItineraryPricingInfoData object.
	 */
	protected void removeUnusedFareProducts(final ItineraryPricingInfoData itineraryPricingInfoData)
	{
		itineraryPricingInfoData.getBundleTemplates().stream()
				.filter(bundleTemplateData -> CollectionUtils.isNotEmpty(bundleTemplateData.getFareProducts())).forEach(
						bundleTemplateData -> bundleTemplateData.getFareProducts().removeIf(fareProduct -> !fareProduct.isSelected()));

	}

	/**
	 * This method creates the List of breakdowndata {@link PTCFareBreakdownData}. The following information is set here:
	 * Passengers base price, total price for all travelers and fare products information.
	 *
	 * @param fareInfoDataList
	 *           the fare info data list
	 * @param baseFare
	 *           the base fare
	 * @param fareSearchRequestData
	 *           the fare search request data
	 * @return List<PTCFareBreakdownData> list
	 */
	protected List<PTCFareBreakdownData> createPTCFareBreakDownList(final List<FareInfoData> fareInfoDataList,
			final double baseFare, final FareSearchRequestData fareSearchRequestData)
	{
		final List<PTCFareBreakdownData> ptcFareBreakdownDataList = new ArrayList<PTCFareBreakdownData>();

		for (final PassengerTypeQuantityData passengerTypeQuantityData : fareSearchRequestData.getPassengerTypes())
		{
			final int noOfTravellers = passengerTypeQuantityData.getQuantity();
			final double totalBaseFare = noOfTravellers * baseFare;
			final PassengerFareData passengerFareData = new PassengerFareData();
			passengerFareData.setBaseFare(createPriceData(baseFare));
			// The total fare is set temporarily here, this will be populated in FareTotalPopulater later.
			passengerFareData.setTotalFare(createPriceData(totalBaseFare));

			final PTCFareBreakdownData pTCFareBreakdownData = new PTCFareBreakdownData();
			pTCFareBreakdownData.setFareInfos(fareInfoDataList);
			pTCFareBreakdownData.setPassengerTypeQuantity(passengerTypeQuantityData);
			pTCFareBreakdownData.setPassengerFare(passengerFareData);

			ptcFareBreakdownDataList.add(pTCFareBreakdownData);
		}
		return ptcFareBreakdownDataList;
	}

	/**
	 * This method returns a fare product from the bundle template. The fare products in the bundle template are in
	 * sorted ascending order and the lowest fare price i.e. the first object will be picked up.
	 *
	 * @param bundleTemplateData
	 *           the bundle template data
	 * @return FareProductData fare product from bundle template
	 */
	protected FareProductData getFareProductFromBundleTemplate(final TravelBundleTemplateData bundleTemplateData)
	{
		if (CollectionUtils.isNotEmpty(bundleTemplateData.getFareProducts()))
		{
			return bundleTemplateData.getFareProducts().get(0);
		}
		return null;
	}

	/**
	 * Method returns a new instance of FareInfoData with the list of FareDetailsData added
	 *
	 * @param fareDetailsDataList
	 *           the fare details data list
	 * @return FareInfoData fare info data
	 */
	protected FareInfoData createFareInfo(final List<FareDetailsData> fareDetailsDataList)
	{
		final FareInfoData fareInfoData = new FareInfoData();
		fareInfoData.setFareDetails(fareDetailsDataList);
		return fareInfoData;
	}

	/**
	 * Method returns a new instance of FareDetailsData with the FareProductData added
	 *
	 * @param fareProductData
	 *           the fare product data
	 * @return FareDetailsData fare details data
	 */
	protected FareDetailsData createFareDetails(final FareProductData fareProductData)
	{
		final FareDetailsData fareDetailsData = new FareDetailsData();
		fareDetailsData.setFareProduct(fareProductData);
		return fareDetailsData;
	}

	/**
	 * This method returns the total price for a fare product and ancillary products.
	 *
	 * @param bundleTemplateData
	 *           the bundle template data
	 * @param fareProductData
	 *           the fare product data
	 * @return double total from fare and ancillary products
	 */
	protected double getTotalFromFareAndAncillaryProducts(final TravelBundleTemplateData bundleTemplateData,
			final FareProductData fareProductData)
	{
		BigDecimal ancillaryProductsTotal = BigDecimal.ZERO;

		for (final Map.Entry<String, List<ProductData>> nonFareProductEntry : bundleTemplateData.getNonFareProducts().entrySet())
		{
			final String bundleTemplateId = nonFareProductEntry.getKey();
			for (final ProductData productData : nonFareProductEntry.getValue())
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Ancillary: " + bundleTemplateData.getBundleType() + "-" + productData.getCode() + "-"
							+ productData.getPrice().getValue());
				}

				final PriceInformation priceInfo = getTravelCommercePriceFacade().getPriceInformationByProductPriceBundleRule(
						getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId), productData.getCode());
				if (priceInfo != null)
				{
					ancillaryProductsTotal = ancillaryProductsTotal.add(BigDecimal.valueOf(priceInfo.getPriceValue().getValue()));
				}
				else
				{
					ancillaryProductsTotal = ancillaryProductsTotal
							.add(BigDecimal.valueOf(productData.getPrice().getValue().doubleValue()));
				}

			}

		}
		return fareProductData.getPrice().getValue().doubleValue() + ancillaryProductsTotal.doubleValue();
	}

	/**
	 * Method takes a PriceInformation object and performs a null check. If the object a is null then null will be
	 * returned otherwise a new PriceData object is created and returned using the PriceValue on the PriceInformation
	 * object
	 *
	 * @param priceInfo
	 *           the price info
	 * @return PriceData price data
	 */
	protected PriceData createPriceData(final PriceInformation priceInfo)
	{
		if (priceInfo == null)
		{
			return null;
		}
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(priceInfo.getPriceValue().getValue()), priceInfo.getPriceValue().getCurrencyIso());
	}

	/**
	 * Method returns a new PriceData using the PriceDataFactory and set sets the priceValue on the new PriceData object
	 *
	 * @param priceValue
	 *           the price value
	 * @return PriceData price data
	 */
	protected PriceData createPriceData(final double priceValue)
	{
		return getTravelCommercePriceFacade().createPriceData(priceValue);
	}



	/**
	 * This method gets the child fareproductbundletemplate of the current bundleTemplate
	 *
	 * @param bundleTemplateData
	 *           the bundle template data
	 * @return BundleTemplateModel child fare product bundle template
	 */
	protected BundleTemplateModel getChildFareProductBundleTemplate(final TravelBundleTemplateData bundleTemplateData)
	{
		final String fareProductBundleTemplateId = bundleTemplateData.getFareProductBundleTemplateId();
		if (StringUtils.isNotBlank(fareProductBundleTemplateId))
		{
			return getBundleTemplateService().getBundleTemplateForCode(fareProductBundleTemplateId);
		}
		return null;
	}

	/**
	 * Log no price for transport offering.
	 *
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param productData
	 *           the product data
	 */
	protected void logNoPriceForTransportOffering(final String transportOfferingCode, final ProductData productData)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Price not set for the product:" + productData.getCode() + " TransportOfferingCode:" + transportOfferingCode);
		}
	}

	/**
	 * Log no price for search key.
	 *
	 * @param searchKey
	 *           the search key
	 * @param searchValue
	 *           the search value
	 * @param productData
	 *           the product data
	 */
	protected void logNoPriceForSearchKey(final String searchKey, final String searchValue, final ProductData productData)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Price not set for the " + searchKey + " and product:" + productData.getCode() + " with " + searchKey
					+ " value:" + searchValue);
		}
	}

	/**
	 * Gets travel commerce price facade.
	 *
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * Sets travel commerce price facade.
	 *
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * Gets bundle template service.
	 *
	 * @return the bundle template service
	 */
	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	/**
	 * Sets bundle template service.
	 *
	 * @param bundleTemplateService
	 *           the bundle template service
	 */
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	/**
	 * Gets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 * @deprecated Deprecated since version 3.0.
	 *
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Gets common i 18 n service.
	 * @deprecated Deprecated since version 3.0.
	 * @return the commonI18NService
	 */
	@Deprecated
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 *           the commonI18NService to set
	 *
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Gets products sort strategy.
	 *
	 * @return the productsSortStrategy
	 */
	protected ProductsSortStrategy<ProductData> getProductsSortStrategy()
	{
		return productsSortStrategy;
	}

	/**
	 * Sets products sort strategy.
	 *
	 * @param productsSortStrategy
	 *           the productsSortStrategy to set
	 */
	@Required
	public void setProductsSortStrategy(final ProductsSortStrategy<ProductData> productsSortStrategy)
	{
		this.productsSortStrategy = productsSortStrategy;
	}

}
