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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.CancelPenaltyData;
import de.hybris.platform.commercefacades.accommodation.GuaranteeData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RatePlanInclusionData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.enums.MealType;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanInclusionModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for RatePlanBasicPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RatePlanBasicPopulatorTest
{
	@InjectMocks
	RatePlanBasicPopulator<RatePlanModel, RatePlanData> ratePlanBasicPopulator;

	@Mock
	private Converter<CancelPenaltyModel, CancelPenaltyData> cancelPenaltyConverter;

	@Mock
	private Converter<GuaranteeModel, GuaranteeData> guaranteeConverter;

	@Mock
	private Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter;

	@Mock
	private Converter<RatePlanInclusionModel, RatePlanInclusionData> ratePlanInclusionConverter;

	@Mock
	private EnumerationService enumerationService;

	private final String TEST_RATE_PLAN_CODE = "RATE_PLAN_CODE_TEST";

	@Test
	public void populateTest()
	{
		final RatePlanModel source = Mockito.mock(RatePlanModel.class);
		given(source.getCode()).willReturn(TEST_RATE_PLAN_CODE);
		given(cancelPenaltyConverter.convertAll(Collections.emptyList())).willReturn(Collections.emptyList());
		given(guaranteeConverter.convertAll(Collections.emptyList())).willReturn(Collections.emptyList());
		given(guestOccupancyConverter.convertAll(Collections.emptyList())).willReturn(Collections.emptyList());
		given(ratePlanInclusionConverter.convertAll(Collections.emptyList())).willReturn(Collections.emptyList());

		final List<MealType> testMeals = Arrays.asList(MealType.BED_BREAKFAST);
		given(source.getMealType()).willReturn(testMeals);

		given(enumerationService.getEnumerationName(Matchers.any(MealType.class))).willReturn(StringUtils.EMPTY);

		final RatePlanData target = new RatePlanData();

		ratePlanBasicPopulator.populate(source, target);
		Assert.assertEquals(TEST_RATE_PLAN_CODE, target.getCode());
	}

}
