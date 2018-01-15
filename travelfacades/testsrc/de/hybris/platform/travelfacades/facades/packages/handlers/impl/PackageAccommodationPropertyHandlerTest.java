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
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.travelfacades.facades.packages.manager.PackagePipelineManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageAccommodationPropertyHandlerTest
{
	@InjectMocks
	PackageAccommodationPropertyHandler packageAccommodationPropertyHandler;

	@Mock
	PackagePipelineManager packagePipelineManager;

	@Test
	public void testHandle()
	{
		final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates = new ArrayList<>();
		final AccommodationSearchRequestData accommodationSearchRequest = new AccommodationSearchRequestData();
		final AccommodationSearchResponseData accommodationSearchResponse = new AccommodationSearchResponseData();

		final AccommodationOfferingDayRateData accommodationOfferingDayRateData = new AccommodationOfferingDayRateData();
		accommodationOfferingDayRateData.setAccommodationOfferingCode("accommodationOfferingCode");
		accommodationOfferingDayRates.add(accommodationOfferingDayRateData);
		final PackageData packageData = new PackageData();
		Mockito.when(packagePipelineManager.executePipeline(Mockito.any(Map.Entry.class),
				Mockito.any(AccommodationSearchRequestData.class))).thenReturn(packageData);

		packageAccommodationPropertyHandler.handle(accommodationOfferingDayRates, accommodationSearchRequest,
				accommodationSearchResponse);
		Assert.assertTrue(accommodationSearchResponse.getProperties().contains(packageData));
	}
}
