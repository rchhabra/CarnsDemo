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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelservices.services.DealBundleTemplateService;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link TransportDealBundleRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportDealBundleRestrictionStrategyTest
{

	@InjectMocks
	TransportDealBundleRestrictionStrategy transportDealBundleRestrictionStrategy;

	@Mock
	private DealBundleTemplateService dealBundleTemplateService;


	private TestSetup testSetup;

	@Before
	public void setUp()
	{
		testSetup = new TestSetup();
	}

	@Test
	public void testApplyStrategyForBookingActionDataDisabled()
	{
		transportDealBundleRestrictionStrategy.applyStrategy(null, null);
		Mockito.verify(dealBundleTemplateService, Mockito.times(0)).isDealBundleOrder(Matchers.anyString());
		transportDealBundleRestrictionStrategy.applyStrategy(Collections.singletonList(testSetup.createBookingActionData(false)),
				null);
		Mockito.verify(dealBundleTemplateService, Mockito.times(0)).isDealBundleOrder(Matchers.anyString());

		transportDealBundleRestrictionStrategy.applyStrategy(Collections.singletonList(testSetup.createBookingActionData(false)),
				new ReservationData());
		Mockito.verify(dealBundleTemplateService, Mockito.times(0)).isDealBundleOrder(Matchers.anyString());

		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("TEST_RESERVATION_CODE");
		Mockito.when(dealBundleTemplateService.isDealBundleOrder(Matchers.anyString())).thenReturn(Boolean.FALSE);
		transportDealBundleRestrictionStrategy.applyStrategy(Collections.singletonList(testSetup.createBookingActionData(true)),
				reservationData);
		Mockito.verify(dealBundleTemplateService, Mockito.times(1)).isDealBundleOrder(Matchers.anyString());
	}

	@Test
	public void testApplyStrategy()
	{
		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("TEST_RESERVATION_CODE");
		Mockito.when(dealBundleTemplateService.isDealBundleOrder(Matchers.anyString())).thenReturn(Boolean.TRUE);

		final BookingActionData bookingActionData = testSetup.createBookingActionData(true);
		transportDealBundleRestrictionStrategy.applyStrategy(Collections.singletonList(bookingActionData), reservationData);
		Mockito.verify(dealBundleTemplateService, Mockito.times(1)).isDealBundleOrder(Matchers.anyString());
		Assert.assertFalse(bookingActionData.isEnabled());
	}

	private class TestSetup
	{
		private BookingActionData createBookingActionData(final boolean isEnabled)
		{
			final BookingActionData bookingActionData = new BookingActionData();
			bookingActionData.setEnabled(isEnabled);
			bookingActionData.setAlternativeMessages(new ArrayList<>());
			return bookingActionData;
		}
	}

}
