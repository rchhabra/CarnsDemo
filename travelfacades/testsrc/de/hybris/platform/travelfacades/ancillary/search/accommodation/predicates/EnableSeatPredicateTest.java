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

package de.hybris.platform.travelfacades.ancillary.search.accommodation.predicates;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatAvailabilityData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatInfoData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link EnableSeatPredicate}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class EnableSeatPredicateTest
{
	@InjectMocks
	EnableSeatPredicate enableSeatPredicate;

	private SeatAvailabilityData seatAvailability;
	private SeatInfoData seatInfo;

	@Before
	public void setUp()
	{
		seatAvailability = new SeatAvailabilityData();
		seatInfo = new SeatInfoData();
	}

	@Test
	public void testForSameSeatNum()
	{
		seatAvailability.setSeatNumber("" + 0);
		seatInfo.setSeatNumber("" + 0);
		Assert.assertTrue(enableSeatPredicate.test(seatAvailability, seatInfo));
	}

	@Test
	public void testForDiffSeatNum()
	{
		seatAvailability.setSeatNumber("" + 1);
		seatInfo.setSeatNumber("" + 0);
		Assert.assertFalse(enableSeatPredicate.test(seatAvailability, seatInfo));
	}
}
