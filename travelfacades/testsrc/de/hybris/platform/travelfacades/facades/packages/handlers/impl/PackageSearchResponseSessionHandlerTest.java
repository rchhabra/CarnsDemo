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
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;
import de.hybris.platform.servicelayer.session.SessionService;

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
public class PackageSearchResponseSessionHandlerTest
{
	@InjectMocks
	PackageSearchResponseSessionHandler packageSearchResponseSessionHandler;

	@Mock
	SessionService sessionService;

	@Test
	public void testHandle()
	{
		final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates = new ArrayList<>();
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();
		final AccommodationSearchResponseData accommodationSearchResponse = new AccommodationSearchResponseData();

		final PackageSearchResponseData packageSearchResponse = new PackageSearchResponseData();
		final CriterionData criterion=new CriterionData();
		packageSearchResponse.setCriterion(criterion);
		Mockito.when(sessionService.getAttribute("packageSearchResponse")).thenReturn(packageSearchResponse);

		packageSearchResponseSessionHandler.handle(accommodationOfferingDayRates, packageSearchRequestData,
				accommodationSearchResponse);
		Assert.assertEquals(criterion, accommodationSearchResponse.getCriterion());
	}
}
