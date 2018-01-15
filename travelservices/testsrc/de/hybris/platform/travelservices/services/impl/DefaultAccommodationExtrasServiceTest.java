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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.dao.AccommodationExtrasDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 *
 * Unit Test for the DefaultAccommodationExtrasService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationExtrasServiceTest
{
	@InjectMocks
	private DefaultAccommodationExtrasService accommodationExtrasService;
	@Mock
	private AccommodationExtrasDao accommodationExtrasDao;

	@Test
	public void getExtrasForAccommodationOfferingTest()
	{
		final ProductModel extra = new ProductModel();
		Mockito.when(accommodationExtrasDao.findExtras(Matchers.anyString()))
				.thenReturn(Stream.of(extra).collect(Collectors.toList()));

		final List<ProductModel> result = accommodationExtrasService.getExtrasForAccommodationOffering("acc1");
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}
}
