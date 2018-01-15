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
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageSearchAvailabilityHandlerTest
{
	@InjectMocks
	PackageSearchAvailabilityHandler packageSearchAvailabilityHandler;

	@Test
	public void testHandle()
	{
		final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates = new ArrayList<>();
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();
		final AccommodationSearchResponseData accommodationSearchResponse = new AccommodationSearchResponseData();

		packageSearchAvailabilityHandler.handle(accommodationOfferingDayRates, packageSearchRequestData,
				accommodationSearchResponse);

		final List<PropertyData> properties = new ArrayList<>();
		final PackageData packageData = new PackageData();
		properties.add(packageData);
		accommodationSearchResponse.setProperties(properties);

		final FareSelectionData fareSelectionData = new FareSelectionData();
		final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setAvailable(true);
		itineraryPricingInfos.add(itineraryPricingInfoData);
		pricedItineraryData.setItineraryPricingInfos(itineraryPricingInfos);
		pricedItineraries.add(pricedItineraryData);
		fareSelectionData.setPricedItineraries(pricedItineraries);
		packageData.setFareSelectionData(fareSelectionData);

		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final OriginDestinationInfoData originDestinationInfoData = new OriginDestinationInfoData();
		final List<OriginDestinationInfoData> originDestinationInfo = Collections.singletonList(originDestinationInfoData);
		fareSearchRequestData.setOriginDestinationInfo(originDestinationInfo);
		packageSearchRequestData.setFareSearchRequestData(fareSearchRequestData);

		packageSearchAvailabilityHandler.handle(accommodationOfferingDayRates, packageSearchRequestData,
				accommodationSearchResponse);

		Assert.assertTrue(CollectionUtils.isNotEmpty(accommodationSearchResponse.getProperties()));
	}
}
