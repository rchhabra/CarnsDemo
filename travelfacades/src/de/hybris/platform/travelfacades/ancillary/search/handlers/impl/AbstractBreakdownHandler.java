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

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler with common methods for all the handlers for the breakdown DTOs in the OfferResponseData
 */
public abstract class AbstractBreakdownHandler
{
	private static final Logger LOG = Logger.getLogger(AbstractBreakdownHandler.class);

	private ProductService productService;
	private TravelCommercePriceService travelCommercePriceService;
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * This method gets the price information for product code, search key and search value.
	 *
	 * @param productCode
	 * @param searchKey
	 * @param searchValue
	 * @return PriceInformation
	 */
	protected PriceInformation getPriceInformation(final String productCode, final String searchKey, final String searchValue)
	{
		Map<String, String> searchCriteria = new HashMap<String, String>();
		final ProductModel product = getProductService().getProductForCode(productCode);
		if (searchKey == null)
		{
			searchCriteria = null;
		}
		else
		{
			searchCriteria.put(searchKey, searchValue);
		}
		LOG.debug("Getting price information for productData (code: " + productCode + ") and searchKey (key: " + searchKey + ")");
		final PriceInformation priceInfo = getTravelCommercePriceService().getProductWebPrice(product, searchCriteria);

		if (priceInfo == null)
		{
			LOG.debug("No price information for productData (code: " + productCode + ") and searchKey (key: " + searchKey + ")");
		}
		return priceInfo;
	}

	/**
	 * If the Product category (offerGroupCode) is configured at TransportOfferingLevel, check if there is a priceRow at
	 * TransportOffering and offer. if not, check if there is a priceRow for travelSector and offer.
	 *
	 * @param odOfferInfo
	 * @param productCode
	 * @return
	 */
	protected PriceInformation getPriceInformationFromTransportOfferingOrSector(final OriginDestinationOfferInfoData odOfferInfo,
			final String productCode)
	{
		PriceInformation priceInfo = null;
		if (Objects.isNull(odOfferInfo) || CollectionUtils.isEmpty(odOfferInfo.getTransportOfferings()))
		{
			LOG.debug("No price information for productData (code: " + productCode + ")");
			return priceInfo;
		}
		final Optional<TransportOfferingData> transportOffering = odOfferInfo.getTransportOfferings().stream().findFirst();
		if (!transportOffering.isPresent())
		{
			LOG.debug("No price information for productData (code: " + productCode + ")");
			return priceInfo;
		}
		priceInfo = getPriceInformation(productCode, PriceRowModel.TRANSPORTOFFERINGCODE, transportOffering.get().getCode());
		if (!Optional.ofNullable(priceInfo).isPresent())
		{
			priceInfo = getPriceInformation(productCode, PriceRowModel.TRAVELSECTORCODE,
					odOfferInfo.getTransportOfferings().stream().findFirst().get().getSector().getCode());
		}
		return priceInfo;
	}

	/**
	 * Method takes a PriceInformation object and performs a null check. If the object a is null then null will be
	 * returned otherwise a new PriceData object is created and returned using the PriceValue on the PriceInformation
	 * object
	 *
	 * @param priceInfo
	 * @return PriceData
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
	 * This method creates PassengerFareData.
	 *
	 * @param priceData
	 *           price of the product in offer
	 * @param quantity
	 *           Integer, quantity of products already offered.
	 * @return PassengerFareData
	 */
	protected PassengerFareData getPassengerFareData(final PriceData priceData, final Integer quantity)
	{
		final PassengerFareData paxFareData = new PassengerFareData();
		paxFareData.setBaseFare(priceData);
		if (quantity == 0)
		{
			paxFareData.setTotalFare(priceData);
		}
		else
		{
			paxFareData.setTotalFare(getTravelCommercePriceFacade().createPriceData(priceData.getPriceType(),
					priceData.getValue().multiply(BigDecimal.valueOf(quantity)), priceData.getCurrencyIso()));
		}
		return paxFareData;
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
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the travelCommercePriceService
	 */
	protected TravelCommercePriceService getTravelCommercePriceService()
	{
		return travelCommercePriceService;
	}

	/**
	 * @param travelCommercePriceService
	 *           the travelCommercePriceService to set
	 */
	public void setTravelCommercePriceService(final TravelCommercePriceService travelCommercePriceService)
	{
		this.travelCommercePriceService = travelCommercePriceService;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

}
