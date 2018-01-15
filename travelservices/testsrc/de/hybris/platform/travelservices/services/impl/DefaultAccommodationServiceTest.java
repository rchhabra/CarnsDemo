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
import de.hybris.platform.travelservices.dao.AccommodationDao;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

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
 * Unit Test for the DefaultAccommodationService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationServiceTest
{
	@InjectMocks
	private DefaultAccommodationService accommodationService;
	@Mock
	private AccommodationDao accommodationDao;

	@Test
	public void getAccommodationForAccommodationOfferingTest()
	{
		final AccommodationModel accommodation = new AccommodationModel();

		Mockito.when(accommodationDao.findAccommodationForAccommodationOffering(Matchers.any()))
				.thenReturn(Stream.of(accommodation).collect(Collectors.toList()));

		final List<AccommodationModel> result = accommodationService.getAccommodationForAccommodationOffering("acc1");
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void getAccommodationForAccommodationOfferingUsingAccommodationCodeTest()
	{
		final AccommodationModel accommodation = new AccommodationModel();

		Mockito.when(accommodationDao.findAccommodationForAccommodationOffering(Matchers.any(), Matchers.any()))
				.thenReturn(accommodation);

		final AccommodationModel result = accommodationService.getAccommodationForAccommodationOffering("acco1", "acc1");
		Assert.assertNotNull(result);
	}
}
