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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.IncludedAncillaryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.fare.search.strategies.AncillaryAvailabilityStrategy;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for checking the
 * availability of all the {@link PricedItineraryData} on the {@link FareSelectionData}
 */
public class FareAvailabilityHandler implements FareSearchHandler
{
	private static final Logger LOG = Logger.getLogger(FareAvailabilityHandler.class);

	private ProductService productService;
	private TravelCommerceStockService commerceStockService;
	private TransportOfferingService transportOfferingService;
	private Map<String, AncillaryAvailabilityStrategy> ancillaryAvailabilityStrategiesMap;

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
					LOG.debug("Priced Itinerary Data Info " + itineraryPricingInfoData.getBundleTypeName() + " not available");
					continue;
				}
				checkAvailability(itineraryPricingInfoData, fareSearchRequestData);
			}
			pricedItinerary.setAvailable(isAvailable(pricedItinerary));
		}
	}

	protected void checkAvailability(final ItineraryPricingInfoData itineraryPricingInfoData,
			final FareSearchRequestData fareSearchRequestData)
	{
		final int nPax = getTotalNumberOfPassengers(fareSearchRequestData.getPassengerTypes());

		checkBundleAvailabilityByFareProducts(itineraryPricingInfoData, nPax);

		if (itineraryPricingInfoData.isAvailable())
		{
			checkBundleAvailabilityByAncillaryProducts(itineraryPricingInfoData, nPax);
		}
	}

	/**
	 * This methods works out the total number of passenger for the given list of Passenger Type Quantity
	 *
	 * @param passengerTypes
	 *           the list of Passenger Type Quantity for the current Search Request
	 */
	protected int getTotalNumberOfPassengers(final List<PassengerTypeQuantityData> passengerTypes)
	{
		int nPax = 0;
		for (final PassengerTypeQuantityData passengerTypeQuantity : passengerTypes)
		{
			nPax += passengerTypeQuantity.getQuantity();
		}
		return nPax;
	}

	/**
	 * Method to set the availability based on the stockLevel of the FareProducts.
	 *
	 * @param itineraryPricingInfoData
	 * @param passengerNumber
	 */
	protected void checkBundleAvailabilityByFareProducts(final ItineraryPricingInfoData itineraryPricingInfoData,
			final int passengerNumber)
	{

		for (final TravelBundleTemplateData bundleTemplate : itineraryPricingInfoData.getBundleTemplates())
		{
			if (CollectionUtils.isEmpty(bundleTemplate.getFareProducts()))
			{
				bundleTemplate.setAvailable(false);
			}
			else
			{
				bundleTemplate.setFareProducts(
						filterFareProducts(bundleTemplate.getFareProducts(), bundleTemplate.getTransportOfferings(), passengerNumber));
				bundleTemplate.setAvailable(!bundleTemplate.getFareProducts().isEmpty());
			}
		}

		itineraryPricingInfoData
				.setAvailable(itineraryPricingInfoData.getBundleTemplates().stream().allMatch(TravelBundleTemplateData::isAvailable));

	}

	/**
	 * Method to filter the FareProducts list based on the stockLevel and the number of passengers.
	 *
	 * @param fareProducts
	 *           as the list of FareProducts to be filtered
	 * @param transportOfferingsList
	 *           as the list of codes of the TransportOfferings where the stock level of the FareProduct should be
	 *           checked
	 * @param passengerNumber
	 *           as the number of passengers
	 *
	 * @return List<FareProductData> the list of products filtered
	 */
	protected List<FareProductData> filterFareProducts(final List<FareProductData> fareProducts,
			final List<TransportOfferingData> transportOfferingsList, final int passengerNumber)
	{

		if (CollectionUtils.isEmpty(transportOfferingsList))
		{
			return Collections.emptyList();
		}

		final List<String> transportOfferingCodes = transportOfferingsList.stream().map(TransportOfferingData::getCode)
				.collect(Collectors.toList());

		final Map<String, TransportOfferingModel> transportOfferingModelMap = getTransportOfferingService()
				.getTransportOfferingsMap(transportOfferingCodes);

		final List<FareProductData> filteredFares = new ArrayList<>();
		for (final FareProductData fareProduct : fareProducts)
		{
			final ProductModel productModel = getProductService().getProductForCode(fareProduct.getCode());
			checkFareProductAvailability(transportOfferingCodes, passengerNumber, transportOfferingModelMap, filteredFares,
					fareProduct, productModel);
		}
		return filteredFares;
	}

	protected void checkFareProductAvailability(final List<String> transportOfferingCodes, final int passengerNumber,
			final Map<String, TransportOfferingModel> transportOfferingModelMap, final List<FareProductData> filteredFares,
			final FareProductData fareProduct, final ProductModel productModel)
	{
		Long finalStockLevel = Long.valueOf(-1);

		boolean available = true;
		for (final String transportOfferingCode : transportOfferingCodes)
		{
			try
			{
				final List<WarehouseModel> warehouses = new ArrayList<>();
				warehouses.add(transportOfferingModelMap.get(transportOfferingCode));

				final Long stockLevel = getCommerceStockService().getStockLevelQuantity(productModel, warehouses);
				if (stockLevel != null && stockLevel.intValue() < passengerNumber)
				{
					LOG.debug("Insufficient stock for Fare Product (code: " + fareProduct.getCode()
							+ ") and Transport Offering (code: " + transportOfferingCode);
					available = false;
					break;
				}
				if (stockLevel < finalStockLevel || finalStockLevel.longValue() == -1)
				{
					finalStockLevel = stockLevel;
				}
			}
			catch (final StockLevelNotFoundException ex)
			{
				LOG.debug("Stocklevel not found for FareProduct with code: " + fareProduct.getCode() + " and TransportOffering: "
						+ transportOfferingCode, ex);
				available = false;
			}
		}

		if (available)
		{
			filteredFares.add(fareProduct);

			final StockData stock = new StockData();
			stock.setStockLevel(finalStockLevel);
			fareProduct.setStock(stock);
		}
	}

	/**
	 * Method to set the availability based on the stockLevel of the ancillary Products.
	 *
	 * @param itineraryPricingInfoData
	 * @param passengerNumber
	 */
	protected void checkBundleAvailabilityByAncillaryProducts(final ItineraryPricingInfoData itineraryPricingInfoData,
			final int passengerNumber)
	{
		for (final TravelBundleTemplateData bundleTemplate : itineraryPricingInfoData.getBundleTemplates())
		{
			bundleTemplate.setAvailable(checkAncillaryProductsAvailability(bundleTemplate, passengerNumber));
		}

		itineraryPricingInfoData
				.setAvailable(itineraryPricingInfoData.getBundleTemplates().stream().allMatch(TravelBundleTemplateData::isAvailable));
	}

	/**
	 * Method to check the availability of a BundleTemplate based on the list of Products and the number of passengers.
	 *
	 * @param bundleTemplate
	 * @param passengerNumber
	 * @return boolean value based on the availability of the bundleTemplate
	 */
	protected boolean checkAncillaryProductsAvailability(final TravelBundleTemplateData bundleTemplate, final int passengerNumber)
	{
		return checkNonFareProductsAvailability(bundleTemplate, passengerNumber)
				&& checkIncludedAncillariesAvailability(bundleTemplate, passengerNumber);
	}

	/**
	 * Method to check the availability of a bundleTemplate based on the availability of its nonFareProducts and the
	 * number of passengers. The method will return true if all the nonFareProducts are available, false otherwise.
	 *
	 * @param bundleTemplate
	 * @param passengerNumber
	 *
	 * @return true if all the nonFareProducts are available, false otherwise.
	 */
	protected boolean checkNonFareProductsAvailability(final TravelBundleTemplateData bundleTemplate, final int passengerNumber)
	{
		if (MapUtils.isEmpty(bundleTemplate.getNonFareProducts()))
		{
			return true;
		}

		final List<String> transportOfferingCodes = bundleTemplate.getTransportOfferings().stream()
				.map(TransportOfferingData::getCode).collect(Collectors.toList());

		final Map<String, TransportOfferingModel> transportOfferingModelMap = getTransportOfferingService()
				.getTransportOfferingsMap(transportOfferingCodes);

		for (final List<ProductData> productDataList : bundleTemplate.getNonFareProducts().values())
		{
			for (final ProductData productData : productDataList)
			{
				final ProductModel productModel = getProductService().getProductForCode(productData.getCode());
				final boolean isProductAvailable = isProductAvailable(passengerNumber, transportOfferingCodes,
						transportOfferingModelMap, productModel);
				if (!isProductAvailable)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Method to check the availability of a bundleTemplate based on the availability of its includedAncillaries products
	 * and the number of passengers. The method will return true if all the includedAncillaries are available, false
	 * otherwise.
	 *
	 * @param bundleTemplate
	 * @param passengerNumber
	 *
	 * @return true if all the includedAncillaries are available, false otherwise.
	 */
	protected boolean checkIncludedAncillariesAvailability(final TravelBundleTemplateData bundleTemplate,
			final int passengerNumber)
	{
		if (CollectionUtils.isEmpty(bundleTemplate.getIncludedAncillaries()))
		{
			return true;
		}

		boolean isAvailable = true;
		for (final IncludedAncillaryData includedAncillaryData : bundleTemplate.getIncludedAncillaries())
		{
			final AncillaryAvailabilityStrategy strategy = getAncillaryAvailabilityStrategiesMap()
					.get(includedAncillaryData.getCriteria());
			isAvailable = strategy.checkIncludedAncillariesAvailability(includedAncillaryData.getProducts(), passengerNumber);
			if (!isAvailable)
			{
				return false;
			}
		}

		return isAvailable;
	}

	protected boolean isProductAvailable(final int passengerNumber, final List<String> transportOfferingCodes,
			final Map<String, TransportOfferingModel> transportOfferingModelMap, final ProductModel productModel)
	{
		for (final String transportOfferingCode : transportOfferingCodes)
		{
			try
			{
				final List<WarehouseModel> warehouses = new ArrayList<>();
				warehouses.add(transportOfferingModelMap.get(transportOfferingCode));

				final Long stockLevel = getCommerceStockService().getStockLevelQuantity(productModel, warehouses);
				if (stockLevel != null && stockLevel.intValue() < passengerNumber)
				{
					LOG.debug("Insufficient stock for Product (code: " + productModel.getCode() + ") and Transport Offering (code: "
							+ transportOfferingCode);
					return false;
				}
			}
			catch (final StockLevelNotFoundException ex)
			{
				LOG.debug("Stocklevel not found for Product with code: " + productModel.getCode() + " and TransportOffering: "
						+ transportOfferingCode, ex);
				return false;
			}
		}
		return true;
	}

	/**
	 * Method to check the availability of a priceItinerary based on the availability of its bundleTemplates
	 *
	 * @param pricedItinerary as the pricedItinerary to be checked
	 * @return boolean value based on the availability of the pricedItinerary
	 */
	protected boolean isAvailable(final PricedItineraryData pricedItinerary)
	{

		boolean isAvailable = false;

		for (final ItineraryPricingInfoData itineraryPricingInfoData : pricedItinerary.getItineraryPricingInfos())
		{
			isAvailable |= itineraryPricingInfoData.getBundleTemplates().stream()
					.allMatch(TravelBundleTemplateData::isAvailable);

		}

		return isAvailable;
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
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService
	 *           the commerceStockService to set
	 */
	@Required
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
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
	 * @return the ancillaryAvailabilityStrategiesMap
	 */
	protected Map<String, AncillaryAvailabilityStrategy> getAncillaryAvailabilityStrategiesMap()
	{
		return ancillaryAvailabilityStrategiesMap;
	}

	/**
	 * @param ancillaryAvailabilityStrategiesMap
	 *           the setAncillaryAvailabilityStrategiesMap to set
	 */
	@Required
	public void setAncillaryAvailabilityStrategiesMap(
			final Map<String, AncillaryAvailabilityStrategy> ancillaryAvailabilityStrategiesMap)
	{
		this.ancillaryAvailabilityStrategiesMap = ancillaryAvailabilityStrategiesMap;
	}
}
