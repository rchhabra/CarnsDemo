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

package de.hybris.platform.travelfacades.facades.packages.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageDetailsPipelineManager;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;

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
 * unit test for {@link DefaultPackageFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPackageFacadeTest
{

	@InjectMocks
	private DefaultPackageFacade packageFacade;

	@Mock
	private PackageDetailsPipelineManager packageDetailsPipelineManager;

	@Mock
	private PackageDetailsPipelineManager amendPackageDetailsPipelineManager;

	@Mock
	private TravelCartFacade travelCartFacade;

	@Mock
	private BookingFacade bookingFacade;

	@Mock
	private AccommodationCartFacade accommodationCartFacade;


	@Test
	public void testGetPackageResponse()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();
		given(packageDetailsPipelineManager.executePipeline(packageRequestData)).willReturn(packageResponseData);

		Assert.assertEquals(packageResponseData, packageFacade.getPackageResponse(packageRequestData));
	}

	@Test
	public void testGetAmendPackageResponse()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();
		given(amendPackageDetailsPipelineManager.executePipeline(packageRequestData)).willReturn(packageResponseData);

		Assert.assertEquals(packageResponseData, packageFacade.getAmendPackageResponse(packageRequestData));
	}

	@Test
	public void testIsPackageInCartForEmptyCart()
	{
		given(travelCartFacade.hasSessionCart()).willReturn(Boolean.FALSE);
		Assert.assertFalse(packageFacade.isPackageInCart());
	}

	@Test
	public void testIsPackageInCartForAccommodationCartOnly()
	{
		given(travelCartFacade.hasSessionCart()).willReturn(Boolean.TRUE);
		given(bookingFacade.isCurrentCartOfType(OrderEntryType.TRANSPORT.getCode())).willReturn(Boolean.FALSE);
		given(bookingFacade.isCurrentCartOfType(OrderEntryType.ACCOMMODATION.getCode())).willReturn(Boolean.FALSE);
		Assert.assertFalse(packageFacade.isPackageInCart());
	}

	@Test
	public void testIsPackageInCartForTransportCartOnly()
	{
		given(travelCartFacade.hasSessionCart()).willReturn(Boolean.TRUE);
		given(bookingFacade.isCurrentCartOfType(OrderEntryType.TRANSPORT.getCode())).willReturn(Boolean.TRUE);
		given(bookingFacade.isCurrentCartOfType(OrderEntryType.ACCOMMODATION.getCode())).willReturn(Boolean.FALSE);
		Assert.assertFalse(packageFacade.isPackageInCart());
	}

	@Test
	public void testIsPackageInCart()
	{
		given(travelCartFacade.hasSessionCart()).willReturn(Boolean.TRUE);
		given(bookingFacade.isCurrentCartOfType(OrderEntryType.TRANSPORT.getCode())).willReturn(Boolean.TRUE);
		given(bookingFacade.isCurrentCartOfType(OrderEntryType.ACCOMMODATION.getCode())).willReturn(Boolean.TRUE);
		Assert.assertTrue(packageFacade.isPackageInCart());
	}

	@Test
	public void testIsPackageInOrderForAccommodationCartOnly()
	{
		final String bookingReference = "TEST_BOOKING_REFERENCE";
		given(travelCartFacade.hasSessionCart()).willReturn(Boolean.TRUE);
		given(bookingFacade.isOrderOfType(bookingReference, OrderEntryType.TRANSPORT.getCode())).willReturn(Boolean.FALSE);
		given(bookingFacade.isOrderOfType(bookingReference, OrderEntryType.ACCOMMODATION.getCode())).willReturn(Boolean.FALSE);
		Assert.assertFalse(packageFacade.isPackageInOrder(bookingReference));
	}

	@Test
	public void testIsPackageInOrderForTransportCartOnly()
	{
		final String bookingReference = "TEST_BOOKING_REFERENCE";
		given(travelCartFacade.hasSessionCart()).willReturn(Boolean.TRUE);
		given(bookingFacade.isOrderOfType(bookingReference, OrderEntryType.TRANSPORT.getCode())).willReturn(Boolean.TRUE);
		given(bookingFacade.isOrderOfType(bookingReference, OrderEntryType.ACCOMMODATION.getCode())).willReturn(Boolean.FALSE);
		Assert.assertFalse(packageFacade.isPackageInOrder(bookingReference));
	}

	@Test
	public void testIsPackageInOrder()
	{
		final String bookingReference = "TEST_BOOKING_REFERENCE";
		given(travelCartFacade.hasSessionCart()).willReturn(Boolean.TRUE);
		given(bookingFacade.isOrderOfType(bookingReference, OrderEntryType.TRANSPORT.getCode())).willReturn(Boolean.TRUE);
		given(bookingFacade.isOrderOfType(bookingReference, OrderEntryType.ACCOMMODATION.getCode())).willReturn(Boolean.TRUE);
		Assert.assertTrue(packageFacade.isPackageInOrder(bookingReference));
	}


	@Test
	public void testCleanUpCartBeforeAddition()
	{
		Mockito.doNothing().when(travelCartFacade).validateCart(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString());
		Mockito.doNothing().when(accommodationCartFacade).cleanUpCartBeforeAddition(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyList());
		packageFacade.cleanUpCartBeforeAddition("TEST_DEPARTURE_LOCATION", "TEST_ARRIVAL_LOCATION", "TEST_DEPARTURE_DATE",
				"TEST_RETURN_DATE", "TEST_ACCOMMODATION_OFFERING_CODE", "TEST_CHECK_IN_DATE", "TEST_CHECK_OUT_DATE",
				Collections.emptyList());

		Mockito.verify(accommodationCartFacade, Mockito.times(1)).cleanUpCartBeforeAddition(Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString(), Matchers.anyList());
	}

}
