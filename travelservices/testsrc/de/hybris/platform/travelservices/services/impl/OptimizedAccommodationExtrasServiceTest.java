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
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;

import java.util.Arrays;
import java.util.Collections;

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
 * Unit Test for the OptimizedAccommodationExtrasService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OptimizedAccommodationExtrasServiceTest
{
	@InjectMocks
	OptimizedAccommodationExtrasService optimizedAccommodationExtrasService;
	@Mock
	private AccommodationOfferingService accommodationOfferingService;
	@Mock
	private ProductService productService;
	@Mock
	private ProductModel productModel;

	@Test
	public void testGetExtrasForAccommodationOffering()
	{
		final AccommodationOfferingModel accommodationOffering = new AccommodationOfferingModel();
		accommodationOffering.setExtras(Arrays.asList("extra1"));
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString())).thenReturn(accommodationOffering);
		Mockito.when(productService.getProductForCode(Matchers.anyString())).thenReturn(productModel);
		Assert.assertEquals(1, optimizedAccommodationExtrasService.getExtrasForAccommodationOffering("acc1").size());
	}

	@Test
	public void testGetExtrasForAccommodationOfferingWithEmptyExtras()
	{
		final AccommodationOfferingModel accommodationOffering = new AccommodationOfferingModel();
		accommodationOffering.setExtras(Collections.emptyList());
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString())).thenReturn(accommodationOffering);
		Assert.assertEquals(0, optimizedAccommodationExtrasService.getExtrasForAccommodationOffering("acc1").size());
	}

	@Test
	public void testGetExtrasForAccommodationOfferingWithNullAccommodationOffering()
	{
		final AccommodationOfferingModel accommodationOffering = new AccommodationOfferingModel();
		accommodationOffering.setExtras(Collections.emptyList());
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString())).thenReturn(null);
		Assert.assertEquals(0, optimizedAccommodationExtrasService.getExtrasForAccommodationOffering("acc1").size());
	}

	@Test
	public void testGetExtrasForAccommodationOfferingWithNullProduct()
	{
		final AccommodationOfferingModel accommodationOffering = new AccommodationOfferingModel();
		accommodationOffering.setExtras(Arrays.asList("extra1"));
		Mockito.when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString())).thenReturn(accommodationOffering);
		Mockito.when(productService.getProductForCode(Matchers.anyString())).thenReturn(null);
		Assert.assertEquals(0, optimizedAccommodationExtrasService.getExtrasForAccommodationOffering("acc1").size());
	}

}