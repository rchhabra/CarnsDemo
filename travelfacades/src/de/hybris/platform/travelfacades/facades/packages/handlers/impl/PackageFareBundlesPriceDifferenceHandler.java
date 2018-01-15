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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Compares the price of bundles to the selected ones for given OD and calculates a differential price such as +20, -10
 */
public class PackageFareBundlesPriceDifferenceHandler implements PackageResponseHandler
{
	private static final String PLUS_SIGN = "+";

	private PriceDataFactory priceDataFactory;

	@Override
	public void handle(final PackageRequestData packageRequestData, final PackageResponseData packageResponseData)
	{
		if (!packageResponseData.isAvailable())
		{
			return;
		}

		final FareSelectionData fareSearchResponse = packageResponseData.getTransportPackageResponse().getFareSearchResponse();

		if (fareSearchResponse == null || CollectionUtils.isEmpty(fareSearchResponse.getPricedItineraries()))
		{
			return;
		}

		final List<PricedItineraryData> selectedPricedItineraries = fareSearchResponse.getPricedItineraries().stream().filter(
				pricedItineraryData -> pricedItineraryData.getItineraryPricingInfos().stream()
						.anyMatch(ItineraryPricingInfoData::isSelected)).collect(Collectors.toList());

		final Map<Integer, ItineraryPricingInfoData> selectedItineraryPricingInfos = new HashMap<>();
		selectedPricedItineraries.forEach(
				pricedItinerary -> pricedItinerary.getItineraryPricingInfos().stream().filter(ItineraryPricingInfoData::isSelected)
						.findAny().ifPresent(itineraryPricingInfo -> selectedItineraryPricingInfos
								.put(pricedItinerary.getOriginDestinationRefNumber(), itineraryPricingInfo)));

		fareSearchResponse.getPricedItineraries().forEach(pricedItineraryData ->
		{
			final ItineraryPricingInfoData selectedItineraryPricingInfo = selectedItineraryPricingInfos
					.get(pricedItineraryData.getOriginDestinationRefNumber());
			final BigDecimal selectedPrice = selectedItineraryPricingInfo.getTotalFare().getTotalPrice().getValue();
			pricedItineraryData.getItineraryPricingInfos().stream().filter(ItineraryPricingInfoData::isAvailable)
					.forEach(itineraryPricingInfo ->
					{
						final BigDecimal priceDifference = itineraryPricingInfo.getTotalFare().getTotalPrice().getValue()
								.subtract(selectedPrice);
						itineraryPricingInfo.getTotalFare().setPriceDifference(getPriceDataFactory()
								.create(PriceDataType.BUY, priceDifference,
										itineraryPricingInfo.getTotalFare().getTotalPrice().getCurrencyIso()));
						updateFormattedValue(itineraryPricingInfo.getTotalFare().getPriceDifference());
					});
		});
	}

	/**
	 * Updates the formattedValue property of the given {@link PriceData} to display a PLUS SIGN for non negative values
	 *
	 * @param priceData
	 * 		as the priceData to update
	 */
	protected void updateFormattedValue(final PriceData priceData)
	{
		if (priceData.getValue().compareTo(BigDecimal.ZERO) >= 0)
		{
			priceData.setFormattedValue(PLUS_SIGN + priceData.getFormattedValue());
		}
	}

	/**
	 * Gets price data factory.
	 *
	 * @return the price data factory
	 */
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @param priceDataFactory
	 * 		the price data factory
	 */
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}
}
