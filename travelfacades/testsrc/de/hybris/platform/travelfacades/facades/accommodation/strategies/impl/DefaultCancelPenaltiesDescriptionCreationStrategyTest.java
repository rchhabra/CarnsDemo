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

package de.hybris.platform.travelfacades.facades.accommodation.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.CancelPenaltyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultCancelPenaltiesDescriptionCreationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCancelPenaltiesDescriptionCreationStrategyTest
{
	@InjectMocks
	DefaultCancelPenaltiesDescriptionCreationStrategy defaultCancelPenaltiesDescriptionCreationStrategy;

	@Mock
	CancelPenaltyModel cancelPenaltyModel;

	@Mock
	RatePlanModel ratePlan;

	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private final String TEST_CANCEL_PENALTY_CODE = "TEST_CANCEL_PENALTY_CODE";

	@Test
	public void testUpdateCancelPenaltiesDescriptionForNullDescription()
	{
		final Date checkInDate = new Date();
		given(ratePlan.getCancelPenalty()).willReturn(Arrays.asList(cancelPenaltyModel));
		given(cancelPenaltyModel.getCode()).willReturn(TEST_CANCEL_PENALTY_CODE);
		final CancelPenaltyData cancelPenalty = new CancelPenaltyData();
		cancelPenalty.setCode(TEST_CANCEL_PENALTY_CODE);
		final List<CancelPenaltyData> cancelPenalties = new ArrayList<>();
		cancelPenalties.add(cancelPenalty);

		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setCancelPenalties(cancelPenalties);
		final List<RatePlanData> ratePlanDatas = new ArrayList<>();
		ratePlanDatas.add(ratePlanData);
		final RoomStayData roomStay = new RoomStayData();

		roomStay.setCheckInDate(checkInDate);
		roomStay.setRatePlans(ratePlanDatas);
		ratePlanData.setCancelPenalties(Arrays.asList(cancelPenalty));
		defaultCancelPenaltiesDescriptionCreationStrategy.updateCancelPenaltiesDescription(ratePlan, roomStay);
		Assert.assertNull(roomStay.getRatePlans().get(0).getCancelPenalties().get(0).getFormattedDescription());
	}

	@Test
	public void testUpdateCancelPenaltiesDescriptionForNullDeadline()
	{
		final Date checkInDate = new Date();
		given(ratePlan.getCancelPenalty()).willReturn(Arrays.asList(cancelPenaltyModel));
		given(cancelPenaltyModel.getCode()).willReturn(TEST_CANCEL_PENALTY_CODE);
		final CancelPenaltyData cancelPenalty = new CancelPenaltyData();
		cancelPenalty.setCode(TEST_CANCEL_PENALTY_CODE);
		final List<CancelPenaltyData> cancelPenalties = new ArrayList<>();
		cancelPenalties.add(cancelPenalty);

		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setCancelPenalties(cancelPenalties);
		final List<RatePlanData> ratePlanDatas = new ArrayList<>();
		ratePlanDatas.add(ratePlanData);
		final RoomStayData roomStay = new RoomStayData();

		roomStay.setCheckInDate(checkInDate);
		roomStay.setRatePlans(ratePlanDatas);
		ratePlanData.setCancelPenalties(Arrays.asList(cancelPenalty));

		given(cancelPenaltyModel.getDescription()).willReturn("TEST_DESCRIPTION");
		given(cancelPenaltyModel.getRelativeDeadline()).willReturn(new Long(1));
		final Date deadLine = new Date(checkInDate.getTime() - cancelPenaltyModel.getRelativeDeadline());
		final String expectedResult = TravelDateUtils.convertDateToStringDate(deadLine, DATE_PATTERN);
		defaultCancelPenaltiesDescriptionCreationStrategy.updateCancelPenaltiesDescription(ratePlan, roomStay);
		Assert.assertTrue(StringUtils.contains(roomStay.getRatePlans().get(0).getCancelPenalties().get(0).getFormattedDescription(),
				expectedResult));
	}
}
