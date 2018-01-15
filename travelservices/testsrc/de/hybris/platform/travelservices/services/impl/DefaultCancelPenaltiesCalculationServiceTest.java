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
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultCancelPenaltiesCalculationService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCancelPenaltiesCalculationServiceTest
{

	@InjectMocks
	private DefaultCancelPenaltiesCalculationService defaultCancelPenaltiesCalculationService;

	@Mock
	private TimeService timeService;

	@Test
	public void testGetActiveCancelPenalty()
	{

		final Date currentDate = new Date();

		final Date checkInDate = TravelDateUtils.addDays(currentDate, 4);

		final long relativeDeadLine1 = checkInDate.getTime() - TravelDateUtils.addDays(checkInDate, -2).getTime();
		final long relativeDeadLine2 = checkInDate.getTime() - TravelDateUtils.addDays(checkInDate, -5).getTime();
		final TestSetup testSetup = new TestSetup();
		final List<CancelPenaltyModel> cancelPenalties = new ArrayList<>();
		Mockito.when(timeService.getCurrentTime()).thenReturn(currentDate);
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_1", relativeDeadLine1,
				TravelDateUtils.addDays(currentDate, 1), 50d, 10d));
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_2", relativeDeadLine2,
				TravelDateUtils.addDays(currentDate, 1), 50d, 10d));
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_2", relativeDeadLine2,
				TravelDateUtils.addDays(currentDate, 1), 50d, 10d));
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_2", relativeDeadLine2,
				TravelDateUtils.addDays(currentDate, 1), 50d, null));
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_2", relativeDeadLine2,
				TravelDateUtils.addDays(currentDate, 1), null, 10d));

		cancelPenalties
				.add(testSetup.createCancelPenaltyModel("TEST_CODE_3", null, TravelDateUtils.addDays(currentDate, 1), 50d, 10d));
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_4", currentDate.getTime(), null, 50d, 10d));
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_5", currentDate.getTime(), null, 5d, 10d));
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_6", currentDate.getTime(), null, null, 10d));


		final CancelPenaltyModel result = defaultCancelPenaltiesCalculationService.getActiveCancelPenalty(cancelPenalties,
				checkInDate, BigDecimal.valueOf(100));
		Assert.assertEquals("TEST_CODE_2", result.getCode());
	}

	@Test
	public void testGetActiveCancelPenaltyForNullScenarios()
	{

		final Date currentDate = new Date();

		final Date checkInDate = TravelDateUtils.addDays(currentDate, 4);

		final long relativeDeadLine1 = checkInDate.getTime() - TravelDateUtils.addDays(checkInDate, -2).getTime();
		final TestSetup testSetup = new TestSetup();
		final List<CancelPenaltyModel> cancelPenalties = new ArrayList<>();
		Mockito.when(timeService.getCurrentTime()).thenReturn(currentDate);
		cancelPenalties.add(testSetup.createCancelPenaltyModel("TEST_CODE_1", relativeDeadLine1, currentDate, 50d, 10d));

		final CancelPenaltyModel result = defaultCancelPenaltiesCalculationService.getActiveCancelPenalty(Collections.emptyList(),
				currentDate, BigDecimal.valueOf(100));
		Assert.assertEquals(null, result);

		final CancelPenaltyModel result2 = defaultCancelPenaltiesCalculationService.getActiveCancelPenalty(cancelPenalties, null,
				BigDecimal.valueOf(100));
		Assert.assertEquals(null, result2);

		final CancelPenaltyModel result3 = defaultCancelPenaltiesCalculationService.getActiveCancelPenalty(cancelPenalties,
				currentDate, null);
		Assert.assertEquals(null, result3);

		final CancelPenaltyModel result4 = defaultCancelPenaltiesCalculationService.getActiveCancelPenalty(cancelPenalties,
				TravelDateUtils.addDays(currentDate, 5), null);
		Assert.assertEquals(null, result4);

		final CancelPenaltyModel result5 = defaultCancelPenaltiesCalculationService.getActiveCancelPenalty(cancelPenalties,
				checkInDate, BigDecimal.valueOf(100));
		Assert.assertEquals(null, result5);


	}


	private class TestSetup
	{
		public CancelPenaltyModel createCancelPenaltyModel(final String code, final Long relativeDeadLine,
				final Date absouluteDeadLine, final Double fixedAmount, final Double percentageAmount)
		{
			final CancelPenaltyModel cancelPenaltyModel = new CancelPenaltyModel();
			cancelPenaltyModel.setCode(code);
			cancelPenaltyModel.setRelativeDeadline(relativeDeadLine);
			cancelPenaltyModel.setAbsoluteDeadline(absouluteDeadLine);
			cancelPenaltyModel.setFixedAmount(fixedAmount);
			cancelPenaltyModel.setPercentageAmount(percentageAmount);
			return cancelPenaltyModel;
		}
	}
}
