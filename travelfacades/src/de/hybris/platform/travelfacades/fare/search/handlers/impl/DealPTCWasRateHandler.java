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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. This handler is responsible for populating the
 * wasRate value of each {@link PTCFareBreakdownData} for every {@link ItineraryPricingInfoData}.
 */
public class DealPTCWasRateHandler extends TaxHandler implements FareSearchHandler
{
	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		if (Objects.isNull(fareSelectionData) || CollectionUtils.isEmpty(fareSelectionData.getPricedItineraries()))
		{
			return;
		}
		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			if (!pricedItinerary.isAvailable())
			{
				continue;
			}
			final List<String> transportFacilityCodes = new ArrayList<>();
			final List<String> countryCodes = new ArrayList<>();
			populateTransportFacilityInfoFromItinerary(pricedItinerary, transportFacilityCodes, countryCodes);
			for (final ItineraryPricingInfoData itineraryPricingInfoData : pricedItinerary.getItineraryPricingInfos())
			{
				if (!itineraryPricingInfoData.isAvailable())
				{
					continue;
				}
				calculateWasPriceForPTCs(transportFacilityCodes, countryCodes, itineraryPricingInfoData);
			}
		}
	}

	/**
	 * Calculates the wasFare for each ptcFareBreakdownData, based on the full price of the products included.
	 *
	 * @param transportFacilityCodes
	 *           as the transportFacilityCodes
	 * @param countryCodes
	 *           as the countryCodes
	 * @param itineraryPricingInfoData
	 *           as the itineraryPricingInfoData
	 */
	protected void calculateWasPriceForPTCs(final List<String> transportFacilityCodes, final List<String> countryCodes,
			final ItineraryPricingInfoData itineraryPricingInfoData)
	{
		for (final PTCFareBreakdownData ptcFareBreakdownData : itineraryPricingInfoData.getPtcFareBreakdownDatas())
		{
			double wasRate = 0d;
			final String currencyIso = ptcFareBreakdownData.getPassengerFare().getBaseFare().getCurrencyIso();

			final List<ProductData> productDatas = new ArrayList<>();
			itineraryPricingInfoData.getBundleTemplates()
					.forEach(bundleTemplate -> bundleTemplate.getTransportOfferings().forEach(transportOffering -> {
						bundleTemplate.getNonFareProducts().values().forEach(productDatas::addAll);
						bundleTemplate.getIncludedAncillaries()
								.forEach(includedAncillaryData -> productDatas.addAll(includedAncillaryData.getProducts()));
					}));
			productDatas.add(ptcFareBreakdownData.getFareInfos().get(0).getFareDetails().get(0).getFareProduct());

			final String passengerType = ptcFareBreakdownData.getPassengerTypeQuantity().getPassengerType().getCode();

			for (final ProductData productData : productDatas)
			{
				final ProductModel product = getProductService().getProductForCode(productData.getCode());
				final PriceInformation priceInfo = getTravelCommercePriceService().getPriceInformation(product, null, null);
				final double basePrice = priceInfo.getPriceValue().getValue();

				final List<TaxData> taxes = getTaxesForProduct(productData, basePrice, transportFacilityCodes, countryCodes,
						passengerType, currencyIso);
				wasRate += basePrice + taxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
			}

			final int noOfTravellers = ptcFareBreakdownData.getPassengerTypeQuantity().getQuantity();
			final double totalWasFare = noOfTravellers * wasRate;
			ptcFareBreakdownData.getPassengerFare()
					.setWasRate(getTravelCommercePriceFacade().createPriceData(totalWasFare, currencyIso));
		}
	}

	/**
	 * Returns the list of TaxData for the given parameters
	 *
	 * @param productData
	 *           as the productData
	 * @param basePrice
	 *           as the basePrice
	 * @param transportFacilityCodes
	 *           as the transportFacilityCodes
	 * @param countryCodes
	 *           as the countryCodes
	 * @param passengerType
	 *           as the passengerType
	 * @param currencyIso
	 *           as the currencyIso
	 *
	 * @return the list of TaxData
	 */
	protected List<TaxData> getTaxesForProduct(final ProductData productData, final double basePrice,
			final List<String> transportFacilityCodes, final List<String> countryCodes, final String passengerType,
			final String currencyIso)
	{
		setTaxSearchCriteriaInContext(transportFacilityCodes, countryCodes, passengerType);
		final ProductModel product = getProductService().getProductForCode(productData.getCode());
		final List<TaxInformation> taxInfos = getTravelCommercePriceService().getProductTaxInformations(product);
		return createTaxData(getTravelCommercePriceFacade().createPriceData(basePrice, currencyIso), taxInfos);
	}

}
