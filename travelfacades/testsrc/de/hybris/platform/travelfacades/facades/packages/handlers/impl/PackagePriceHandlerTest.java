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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.RateRangeData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackagePriceHandlerTest
{
	@InjectMocks
	PackagePriceHandler packagePriceHandler;

	@Mock
	TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void testHandle()
	{
		final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates = new ArrayList<>();
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();
		final AccommodationSearchResponseData accommodationSearchResponse = new AccommodationSearchResponseData();

		packagePriceHandler.handle(accommodationOfferingDayRates, packageSearchRequestData, accommodationSearchResponse);

		final List<PropertyData> properties = new ArrayList<>();
		final PackageData packageData = new PackageData();
		final RateRangeData rateRange = new RateRangeData();
		final PriceData actualRate = new PriceData();
		actualRate.setValue(BigDecimal.valueOf(100));
		rateRange.setActualRate(actualRate);
		packageData.setRateRange(rateRange);

		final FareSelectionData fareSelectionData = new FareSelectionData();
		final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		final TotalFareData totalFare = new TotalFareData();
		final PriceData totalPackagePrice = new PriceData();
		totalPackagePrice.setValue(BigDecimal.valueOf(110));
		totalFare.setTotalPrice(totalPackagePrice);
		itineraryPricingInfoData.setTotalFare(totalFare);
		itineraryPricingInfos.add(itineraryPricingInfoData);
		pricedItineraryData.setItineraryPricingInfos(itineraryPricingInfos);
		pricedItineraries.add(pricedItineraryData);
		fareSelectionData.setPricedItineraries(pricedItineraries);
		packageData.setFareSelectionData(fareSelectionData);

		properties.add(packageData);
		accommodationSearchResponse.setProperties(properties);

		Mockito.when(travelCommercePriceFacade.getBookingFeesAndTaxes()).thenReturn(BigDecimal.valueOf(15));
		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.anyDouble())).thenReturn(totalPackagePrice);

		packagePriceHandler.handle(accommodationOfferingDayRates, packageSearchRequestData,
				accommodationSearchResponse);
		Assert.assertEquals(totalPackagePrice, packageData.getTotalPackagePrice());
	}
}
