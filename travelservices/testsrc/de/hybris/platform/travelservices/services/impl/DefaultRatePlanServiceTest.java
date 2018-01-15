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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.GuaranteeType;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultRatePlanService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRatePlanServiceTest
{
	@InjectMocks
	DefaultRatePlanService ratePlanService;
	@Mock
	private Comparator<GuaranteeModel> guaranteeComparator;
	@Mock
	private Map<GuaranteeType, Integer> guaranteesPriorityMap;

	@Test
	public void testGetGuaranteeToApply()
	{
		final AccommodationOrderEntryGroupModel group = new AccommodationOrderEntryGroupModel();
		final RatePlanModel ratePlan = new RatePlanModel();
		final GuaranteeModel guarantee = new GuaranteeModel();
		ratePlan.setGuarantee(Stream.of(guarantee).collect(Collectors.toList()));
		group.setRatePlan(ratePlan);
		guarantee.setAbsoluteDeadline(TravelDateUtils.getDate("31/12/2016", TravelservicesConstants.DATE_PATTERN));
		group.setStartingDate(TravelDateUtils.getDate("28/12/2016", TravelservicesConstants.DATE_PATTERN));
		ratePlanService.setGuaranteeComparator(guaranteeComparator);
		final Comparator<GuaranteeModel> reversedComparator = new Comparator<GuaranteeModel>()
		{

			@Override
			public int compare(final GuaranteeModel guarantee1, final GuaranteeModel guarantee2)
			{
				return comparePriority(guarantee1.getType(), guarantee2.getType());
			}

			protected int comparePriority(final GuaranteeType guaranteeType1, final GuaranteeType guaranteeType2)
			{
				return guaranteesPriorityMap.get(guaranteeType1).intValue() < guaranteesPriorityMap.get(guaranteeType2).intValue()
						? -1 : 1;
			}
		};
		Mockito.when(guaranteeComparator.reversed()).thenReturn(reversedComparator);
		Assert.assertNotNull(ratePlanService.getGuaranteeToApply(group,
				TravelDateUtils.getDate("27/12/2016", TravelservicesConstants.DATE_PATTERN)));
	}

	@Test
	public void testGetGuaranteeToApplyWithStartingDate()
	{
		final AccommodationOrderEntryGroupModel group = new AccommodationOrderEntryGroupModel();
		final RatePlanModel ratePlan = new RatePlanModel();
		final GuaranteeModel guarantee = new GuaranteeModel();
		ratePlan.setGuarantee(Stream.of(guarantee).collect(Collectors.toList()));
		group.setRatePlan(ratePlan);
		guarantee.setAbsoluteDeadline(TravelDateUtils.getDate("31/12/2016", TravelservicesConstants.DATE_PATTERN));
		ratePlanService.setGuaranteeComparator(guaranteeComparator);
		final Comparator<GuaranteeModel> reversedComparator = new Comparator<GuaranteeModel>()
		{

			@Override
			public int compare(final GuaranteeModel guarantee1, final GuaranteeModel guarantee2)
			{
				return comparePriority(guarantee1.getType(), guarantee2.getType());
			}

			protected int comparePriority(final GuaranteeType guaranteeType1, final GuaranteeType guaranteeType2)
			{
				return guaranteesPriorityMap.get(guaranteeType1).intValue() < guaranteesPriorityMap.get(guaranteeType2).intValue()
						? -1 : 1;
			}
		};
		Mockito.when(guaranteeComparator.reversed()).thenReturn(reversedComparator);
		Assert.assertNotNull(ratePlanService.getGuaranteeToApply(group,
				TravelDateUtils.getDate("28/12/2016", TravelservicesConstants.DATE_PATTERN),
				TravelDateUtils.getDate("27/12/2016", TravelservicesConstants.DATE_PATTERN)));
	}

	@Test
	public void testGetGuaranteeToApplyWithNullRatePlan()
	{
		final AccommodationOrderEntryGroupModel group = new AccommodationOrderEntryGroupModel();
		group.setRatePlan(null);
		ratePlanService.setGuaranteeComparator(guaranteeComparator);
		final Comparator<GuaranteeModel> reversedComparator = new Comparator<GuaranteeModel>()
		{

			@Override
			public int compare(final GuaranteeModel guarantee1, final GuaranteeModel guarantee2)
			{
				return comparePriority(guarantee1.getType(), guarantee2.getType());
			}

			protected int comparePriority(final GuaranteeType guaranteeType1, final GuaranteeType guaranteeType2)
			{
				return guaranteesPriorityMap.get(guaranteeType1).intValue() < guaranteesPriorityMap.get(guaranteeType2).intValue()
						? -1 : 1;
			}
		};
		Mockito.when(guaranteeComparator.reversed()).thenReturn(reversedComparator);
		Assert.assertNull(ratePlanService.getGuaranteeToApply(group,
				TravelDateUtils.getDate("28/12/2016", TravelservicesConstants.DATE_PATTERN),
				TravelDateUtils.getDate("27/12/2016", TravelservicesConstants.DATE_PATTERN)));
	}

	@Test
	public void testGetGuaranteeToApplyWithNullGuarantee()
	{
		final AccommodationOrderEntryGroupModel group = new AccommodationOrderEntryGroupModel();
		final RatePlanModel ratePlan = new RatePlanModel();
		group.setRatePlan(ratePlan);
		ratePlanService.setGuaranteeComparator(guaranteeComparator);
		final Comparator<GuaranteeModel> reversedComparator = new Comparator<GuaranteeModel>()
		{

			@Override
			public int compare(final GuaranteeModel guarantee1, final GuaranteeModel guarantee2)
			{
				return comparePriority(guarantee1.getType(), guarantee2.getType());
			}

			protected int comparePriority(final GuaranteeType guaranteeType1, final GuaranteeType guaranteeType2)
			{
				return guaranteesPriorityMap.get(guaranteeType1).intValue() < guaranteesPriorityMap.get(guaranteeType2).intValue()
						? -1 : 1;
			}
		};
		Mockito.when(guaranteeComparator.reversed()).thenReturn(reversedComparator);
		Assert.assertNull(ratePlanService.getGuaranteeToApply(group,
				TravelDateUtils.getDate("28/12/2016", TravelservicesConstants.DATE_PATTERN),
				TravelDateUtils.getDate("27/12/2016", TravelservicesConstants.DATE_PATTERN)));
	}

	@Test
	public void testGetGuaranteeToApplyWithNullStartingAndCurrentDate()
	{
		final AccommodationOrderEntryGroupModel group = new AccommodationOrderEntryGroupModel();
		final RatePlanModel ratePlan = new RatePlanModel();
		group.setRatePlan(ratePlan);
		ratePlanService.setGuaranteeComparator(guaranteeComparator);
		final Comparator<GuaranteeModel> reversedComparator = new Comparator<GuaranteeModel>()
		{

			@Override
			public int compare(final GuaranteeModel guarantee1, final GuaranteeModel guarantee2)
			{
				return comparePriority(guarantee1.getType(), guarantee2.getType());
			}

			protected int comparePriority(final GuaranteeType guaranteeType1, final GuaranteeType guaranteeType2)
			{
				return guaranteesPriorityMap.get(guaranteeType1).intValue() < guaranteesPriorityMap.get(guaranteeType2).intValue()
						? -1 : 1;
			}
		};
		Mockito.when(guaranteeComparator.reversed()).thenReturn(reversedComparator);
		Assert.assertNull(ratePlanService.getGuaranteeToApply(group, null, null));
	}

	@Test
	public void testGetAppliedGuaranteeAmount()
	{
		final GuaranteeModel guaranteeToApply = new GuaranteeModel();
		guaranteeToApply.setFixedAmount(50d);
		assertEquals(Double.valueOf(50), ratePlanService.getAppliedGuaranteeAmount(guaranteeToApply, null));
	}

	@Test
	public void testGetAppliedGuaranteeAmountWithNullFixedAmount()
	{
		final GuaranteeModel guaranteeToApply = new GuaranteeModel();
		guaranteeToApply.setPercentageAmount(30d);
		assertEquals(Double.valueOf(30), ratePlanService.getAppliedGuaranteeAmount(guaranteeToApply, BigDecimal.valueOf(100d)));
	}

	@Test
	public void testGetAppliedGuaranteeAmountWithNullPercentageAmount()
	{
		final GuaranteeModel guaranteeToApply = new GuaranteeModel();
		assertEquals(Double.valueOf(0), ratePlanService.getAppliedGuaranteeAmount(guaranteeToApply, BigDecimal.valueOf(100d)));
	}

}
