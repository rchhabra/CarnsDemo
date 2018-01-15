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
 */

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This handler is a concrete implementation of {@link AccommodationSearchHandler} and is responsible for calculating a total
 * price of a package. This price is a sum of total prices for accommodation and transportation parts of the package.
 */
public class PackagePriceHandler implements AccommodationSearchHandler
{
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Override
	public void handle(final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final AccommodationSearchRequestData accommodationSearchRequest,
			final AccommodationSearchResponseData accommodationSearchResponse)
	{
		if (CollectionUtils.isEmpty(accommodationSearchResponse.getProperties()))
		{
			return;
		}

		final List<PropertyData> packages = accommodationSearchResponse.getProperties().stream()
				.filter(propertyData -> propertyData instanceof PackageData).collect(Collectors.toList());

		final BigDecimal bookingFees = getTravelCommercePriceFacade().getBookingFeesAndTaxes();
		packages.forEach(propertyData ->
		{
			final PackageData packageData = (PackageData) propertyData;
			final BigDecimal accommodationPrice = getAccommodationPrice(packageData);
			final BigDecimal farePrice = getFarePrice(packageData);
			if (Objects.nonNull(accommodationPrice) && Objects.nonNull(farePrice))
			{
				final BigDecimal totalPrice = accommodationPrice.add(farePrice).add(bookingFees);
				packageData.setTotalPackagePrice(getTravelCommercePriceFacade().createPriceData(totalPrice.doubleValue()));
			}
		});
	}

	/**
	 * Gets fare price.
	 *
	 * @param packageData
	 * 		the package data
	 *
	 * @return the fare price
	 */
	protected BigDecimal getFarePrice(final PackageData packageData)
	{
		if (Objects.isNull(packageData.getFareSelectionData()) || CollectionUtils
				.isEmpty(packageData.getFareSelectionData().getPricedItineraries()))
		{
			return null;
		}

		BigDecimal result = BigDecimal.ZERO;

		final List<PricedItineraryData> pricedItineraries = packageData.getFareSelectionData().getPricedItineraries().stream()
				.filter(pricedItineraryData -> CollectionUtils.isNotEmpty(pricedItineraryData.getItineraryPricingInfos()))
				.collect(Collectors.toList());

		for (final PricedItineraryData pricedItinerary : pricedItineraries)
		{
			final ItineraryPricingInfoData itineraryPricingInfoData = pricedItinerary.getItineraryPricingInfos().get(0);
			if (Objects.nonNull(itineraryPricingInfoData.getTotalFare()) && Objects
					.nonNull(itineraryPricingInfoData.getTotalFare().getTotalPrice()))
			{
				result = result.add(itineraryPricingInfoData.getTotalFare().getTotalPrice().getValue());
			}
		}

		return result;
	}

	/**
	 * Gets accommodation price.
	 *
	 * @param packageData
	 * 		the package data
	 *
	 * @return the accommodation price
	 */
	protected BigDecimal getAccommodationPrice(final PackageData packageData)
	{
		return packageData.getRateRange() != null ? packageData.getRateRange().getActualRate().getValue() : null;
	}

	/**
	 * Gets travel commerce price facade.
	 *
	 * @return the travel commerce price facade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * Sets travel commerce price facade.
	 *
	 * @param travelCommercePriceFacade
	 * 		the travel commerce price facade
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
