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
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;

import java.util.Collections;
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
 * Unit Test for the OptimizedAccommodationService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OptimizedAccommodationServiceTest
{

	@InjectMocks
	private OptimizedAccommodationService optimizedAccommodationService;
	@Mock
	private AccommodationOfferingService accommodationOfferingService;
	@Mock
	private ProductService productService;
	@Mock
	private AccommodationOfferingModel accommodationOfferingModel;
	@Mock
	private AccommodationModel accommodation;

	@Test
	public void getAccommodationForAccommodationOfferingTest()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(accommodationOfferingModel);
		Mockito.when(accommodationOfferingModel.getAccommodations()).thenReturn(Stream.of("acc1").collect(Collectors.toList()));
		Mockito.when(productService.getProductForCode("acc1")).thenReturn(accommodation);
		Assert.assertNotNull(optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1"));
		Assert.assertEquals(1, optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1").size());
	}

	@Test
	public void getAccommodationForAccommodationOfferingWithEmptyAccommodationOfferingTest()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(null);
		Assert.assertEquals(0, optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1").size());
	}

	@Test
	public void getAccommodationForAccommodationOfferingWithEmptyAccommodationsTest()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(accommodationOfferingModel);
		Mockito.when(accommodationOfferingModel.getAccommodations()).thenReturn(Collections.emptyList());
		Assert.assertEquals(0, optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1").size());
	}

	@Test
	public void getAccommodationForAccommodationOfferingWithNullProductTest()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(accommodationOfferingModel);
		Mockito.when(accommodationOfferingModel.getAccommodations()).thenReturn(Stream.of("acc1").collect(Collectors.toList()));
		Mockito.when(productService.getProductForCode("acc1")).thenReturn(null);
		Assert.assertEquals(0, optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1").size());
	}

	@Test
	public void getAccommodationForAccommodationOfferingWithProductNotInstanceOfAccommodationTest()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(accommodationOfferingModel);
		Mockito.when(accommodationOfferingModel.getAccommodations()).thenReturn(Stream.of("acc1").collect(Collectors.toList()));
		Mockito.when(productService.getProductForCode("acc1")).thenReturn(Mockito.mock(FareProductModel.class));
		Assert.assertEquals(0, optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1").size());
	}

	@Test
	public void getAccommodationForAccommodationOfferingUsingBothCodesTest()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(accommodationOfferingModel);
		Mockito.when(accommodationOfferingModel.getAccommodations()).thenReturn(Stream.of("acc1").collect(Collectors.toList()));
		Mockito.when(productService.getProductForCode("acc1")).thenReturn(accommodation);
		Assert.assertNotNull(optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1", "acc1"));
	}

	@Test
	public void getAccommodationForAccommodationOfferingUsingBothCodesWithNullProductTest()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(accommodationOfferingModel);
		Mockito.when(accommodationOfferingModel.getAccommodations()).thenReturn(Stream.of("acc1").collect(Collectors.toList()));
		Mockito.when(productService.getProductForCode("acc1")).thenReturn(null);
		Assert.assertNull(optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1", "acc1"));
	}

	@Test
	public void getAccommodationForAccommodationOfferingUsingBothCodesWithADifferentProductTest()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(accommodationOfferingModel);
		Mockito.when(accommodationOfferingModel.getAccommodations()).thenReturn(Stream.of("acc1").collect(Collectors.toList()));
		Mockito.when(productService.getProductForCode("acc1")).thenReturn(Mockito.mock(FareProductModel.class));
		Assert.assertNull(optimizedAccommodationService.getAccommodationForAccommodationOffering("acco1", "acc1"));
	}
}
