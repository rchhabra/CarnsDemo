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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.AccommodationOfferingDao;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultAccommodationOfferingService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationOfferingServiceTest
{

	@InjectMocks
	private DefaultAccommodationOfferingService accommodationOfferingService;
	@Mock
	private AccommodationOfferingDao accommodationOfferingDao;

	@Test
	public void getAccommodationOfferingTest()
	{
		final AccommodationOfferingModel accommodationOffering = new AccommodationOfferingModel();

		Mockito.when(accommodationOfferingDao.findAccommodationOffering(Matchers.any())).thenReturn(accommodationOffering);

		final AccommodationOfferingModel result = accommodationOfferingService.getAccommodationOffering("acco1");
		Assert.assertNotNull(result);
	}

	@Test(expected = ModelNotFoundException.class)
	public void getAccommodationOfferingThrowsExceptionTest()
	{
		Mockito.when(accommodationOfferingDao.findAccommodationOffering(Matchers.any()))
				.thenThrow(new ModelNotFoundException("Model Not Found"));

		accommodationOfferingService.getAccommodationOffering("acco1");
	}

	@Test
	public void getAccommodationOfferingsTest()
	{
		final AccommodationOfferingModel accommodationOffering = new AccommodationOfferingModel();

		Mockito.when(accommodationOfferingDao.findAccommodationOfferings())
				.thenReturn(Stream.of(accommodationOffering).collect(Collectors.toList()));

		final List<AccommodationOfferingModel> result = accommodationOfferingService.getAccommodationOfferings();
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void getAccommodationOfferingsUsingBatchAndOffsetTest()
	{
		Mockito.when(accommodationOfferingDao.findAccommodationOfferings(Matchers.anyInt(), Matchers.anyInt()))
				.thenReturn(Mockito.mock(SearchResult.class));

		final SearchResult<AccommodationOfferingModel> result = accommodationOfferingService.getAccommodationOfferings(1,1);
		Assert.assertNotNull(result);
	}
}
