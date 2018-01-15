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
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.request.TransportPackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DealResponseAvailabilityHandlerTest
{
	@InjectMocks
	DealResponseAvailabilityHandler dealResponseAvailabilityHandler;

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();

		final TransportPackageResponseData transportPackageResponse = new TransportPackageResponseData();
		packageResponseData.setTransportPackageResponse(transportPackageResponse);

		final FareSelectionData fareSearchResponse = new FareSelectionData();
		transportPackageResponse.setFareSearchResponse(fareSearchResponse);

		final List<PricedItineraryData> pricedItineraries = new ArrayList<PricedItineraryData>();
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		pricedItineraries.add(pricedItineraryData);
		fareSearchResponse.setPricedItineraries(pricedItineraries);

		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		final ItineraryPricingInfoData itineraryPricingInfoData=new ItineraryPricingInfoData();
		itineraryPricingInfoData.setAvailable(true);
		itineraryPricingInfoData.setSelected(true);
		itineraryPricingInfos.add(itineraryPricingInfoData);
		pricedItineraryData.setItineraryPricingInfos(itineraryPricingInfos);

		final TransportPackageRequestData transportPackageRequest = new TransportPackageRequestData();
		final FareSearchRequestData fareSearchRequest = new FareSearchRequestData();
		final List<OriginDestinationInfoData> originDestinationInfos = new ArrayList<>();
		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfos.add(originDestinationInfo);
		fareSearchRequest.setOriginDestinationInfo(originDestinationInfos);
		transportPackageRequest.setFareSearchRequest(fareSearchRequest);
		packageRequestData.setTransportPackageRequest(transportPackageRequest);

		Assert.assertTrue(
				dealResponseAvailabilityHandler.isTransportPackageResponseAvailable(packageRequestData, packageResponseData));
	}

}
