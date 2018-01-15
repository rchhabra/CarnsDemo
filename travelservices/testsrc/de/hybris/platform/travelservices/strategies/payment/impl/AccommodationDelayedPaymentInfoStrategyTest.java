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

package de.hybris.platform.travelservices.strategies.payment.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.GuaranteeType;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.RatePlanService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link AccommodationDelayedPaymentInfoStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationDelayedPaymentInfoStrategyTest
{

	@InjectMocks
	AccommodationDelayedPaymentInfoStrategy accommodationDelayedPaymentInfoStrategy;

	@Mock
	private BookingService bookingService;

	@Mock
	private RatePlanService ratePlanService;

	@Mock
	private TimeService timeService;

	@Test
	public void testCreateForEmptyEntryGroups()
	{
		final OrderModel order = new OrderModel();
		when(bookingService.getAccommodationOrderEntryGroups(order)).thenReturn(null);
		Assert.assertTrue(CollectionUtils.isEmpty(accommodationDelayedPaymentInfoStrategy.create(order)));

	}

	@Test
	public void testCreate()
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();
		final AccommodationOrderEntryGroupModel entryGroup1 = createEntryGroup(false, false, false, false, AmendStatus.SAME);
		final AccommodationOrderEntryGroupModel entryGroup2 = createEntryGroup(true, false, true, false, AmendStatus.SAME);
		final AccommodationOrderEntryGroupModel entryGroup3 = createEntryGroup(true, true, true, false, AmendStatus.SAME);
		final AccommodationOrderEntryGroupModel entryGroup4 = createEntryGroup(true, true, true, true, AmendStatus.SAME);
		final AccommodationOrderEntryGroupModel entryGroup5 = createEntryGroup(true, true, true, true, AmendStatus.NEW);
		final AccommodationOrderEntryGroupModel entryGroup6 = createEntryGroup(true, true, true, true, AmendStatus.NEW);
		entryGroup6.setEntries(Stream.of(createAbstractOrderEntryModel(200d, true, false, AmendStatus.CHANGED),
				createAbstractOrderEntryModel(200d, true, true, AmendStatus.NEW)).collect(Collectors.toList()));
		final AccommodationOrderEntryGroupModel entryGroup7 = createEntryGroup(true, true, true, true, AmendStatus.NEW);
		entryGroup7.getRatePlan().setGuarantee(Arrays.asList(createGuaranteeModel(true, true, 20d, GuaranteeType.DEPOSIT)));
		final AccommodationOrderEntryGroupModel entryGroup8 = createEntryGroup(true, true, true, true, AmendStatus.NEW);
		final AccommodationOrderEntryGroupModel entryGroup9 = createEntryGroup(true, true, false, true, AmendStatus.CHANGED);
		entryGroups.add(entryGroup1);
		entryGroups.add(entryGroup2);
		entryGroups.add(entryGroup3);
		entryGroups.add(entryGroup4);
		entryGroups.add(entryGroup5);
		entryGroups.add(entryGroup6);
		entryGroups.add(entryGroup7);
		entryGroups.add(entryGroup8);
		entryGroups.add(entryGroup9);

		final OrderModel order = new OrderModel();
		order.setOriginalOrder(order);
		when(bookingService.getAccommodationOrderEntryGroups(order)).thenReturn(entryGroups);
		when(bookingService.getOrderTotalPaidByEntryGroup(Matchers.any(OrderModel.class),
				Matchers.any(AccommodationOrderEntryGroupModel.class))).thenReturn(BigDecimal.valueOf(100d));
		final Date date = new Date();
		when(timeService.getCurrentTime()).thenReturn(date);
		final GuaranteeModel guarantee1 = createGuaranteeModel(false, true, 80d, GuaranteeType.PREPAYMENT);
		final GuaranteeModel guarantee3 = createGuaranteeModel(false, false, 80d, GuaranteeType.PREPAYMENT);
		final GuaranteeModel guarantee2 = createGuaranteeModel(true, true, 80d, GuaranteeType.DEPOSIT);
		when(ratePlanService.getGuaranteeToApply(entryGroup4, date)).thenReturn(guarantee1);
		when(ratePlanService.getGuaranteeToApply(entryGroup5, date)).thenReturn(guarantee3);
		when(ratePlanService.getGuaranteeToApply(entryGroup6, date)).thenReturn(guarantee1);
		when(ratePlanService.getGuaranteeToApply(entryGroup7, date)).thenReturn(guarantee2);
		when(ratePlanService.getGuaranteeToApply(entryGroup8, date)).thenReturn(guarantee2);
		when(ratePlanService.getGuaranteeToApply(entryGroup9, date)).thenReturn(guarantee2);
		Assert.assertTrue(CollectionUtils.isNotEmpty(accommodationDelayedPaymentInfoStrategy.create(order)));

	}

	@Test
	public void testCreateForNonAmendOrder()
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();
		final AccommodationOrderEntryGroupModel entryGroup1 = createEntryGroup(false, false, false, false, AmendStatus.SAME);
		final AccommodationOrderEntryGroupModel entryGroup2 = createEntryGroup(true, false, true, false, AmendStatus.SAME);
		final AccommodationOrderEntryGroupModel entryGroup3 = createEntryGroup(true, true, true, false, AmendStatus.SAME);
		final AccommodationOrderEntryGroupModel entryGroup4 = createEntryGroup(true, true, true, true, AmendStatus.SAME);
		final AccommodationOrderEntryGroupModel entryGroup5 = createEntryGroup(true, true, true, true, AmendStatus.NEW);
		final AccommodationOrderEntryGroupModel entryGroup6 = createEntryGroup(true, true, true, true, AmendStatus.NEW);
		entryGroup6
				.setEntries(Stream.of(createAbstractOrderEntryModel(200d, true, true, AmendStatus.NEW)).collect(Collectors.toList()));
		final AccommodationOrderEntryGroupModel entryGroup7 = createEntryGroup(true, true, true, true, AmendStatus.NEW);
		entryGroup7.getRatePlan().setGuarantee(Arrays.asList(createGuaranteeModel(true, true, 20d, GuaranteeType.DEPOSIT)));
		final AccommodationOrderEntryGroupModel entryGroup8 = createEntryGroup(true, true, true, true, AmendStatus.NEW);
		final AccommodationOrderEntryGroupModel entryGroup9 = createEntryGroup(true, true, false, true, AmendStatus.NEW);
		entryGroups.add(entryGroup1);
		entryGroups.add(entryGroup2);
		entryGroups.add(entryGroup3);
		entryGroups.add(entryGroup4);
		entryGroups.add(entryGroup5);
		entryGroups.add(entryGroup6);
		entryGroups.add(entryGroup7);
		entryGroups.add(entryGroup8);
		entryGroups.add(entryGroup9);

		final OrderModel order = new OrderModel();
		when(bookingService.getAccommodationOrderEntryGroups(order)).thenReturn(entryGroups);
		when(bookingService.getOrderTotalPaidByEntryGroup(Matchers.any(OrderModel.class),
				Matchers.any(AccommodationOrderEntryGroupModel.class))).thenReturn(BigDecimal.valueOf(100d));
		final Date date = new Date();
		when(timeService.getCurrentTime()).thenReturn(date);
		final GuaranteeModel guarantee1 = createGuaranteeModel(false, true, 80d, GuaranteeType.PREPAYMENT);
		final GuaranteeModel guarantee3 = createGuaranteeModel(false, false, 80d, GuaranteeType.PREPAYMENT);
		final GuaranteeModel guarantee2 = createGuaranteeModel(true, true, 80d, GuaranteeType.DEPOSIT);
		when(ratePlanService.getGuaranteeToApply(entryGroup4, date)).thenReturn(guarantee1);
		when(ratePlanService.getGuaranteeToApply(entryGroup5, date)).thenReturn(guarantee3);
		when(ratePlanService.getGuaranteeToApply(entryGroup6, date)).thenReturn(guarantee1);
		when(ratePlanService.getGuaranteeToApply(entryGroup7, date)).thenReturn(guarantee2);
		when(ratePlanService.getGuaranteeToApply(entryGroup8, date)).thenReturn(guarantee2);
		when(ratePlanService.getGuaranteeToApply(entryGroup9, date)).thenReturn(guarantee2);
		Assert.assertTrue(CollectionUtils.isNotEmpty(accommodationDelayedPaymentInfoStrategy.create(order)));

	}

	private GuaranteeModel createGuaranteeModel(final boolean hasFixedAmount, final boolean hasPercentageAmount,
			final double value, final GuaranteeType type)
	{
		final GuaranteeModel guarantee = new GuaranteeModel();
		if (hasFixedAmount)
		{
			guarantee.setFixedAmount(value);
		}
		if (hasPercentageAmount)
		{
			guarantee.setPercentageAmount(value);
		}
		guarantee.setType(type);
		return guarantee;
	}

	private AccommodationOrderEntryGroupModel createEntryGroup(final boolean hasEntries, final boolean hasTaxes,
			final boolean isActive, final boolean hasGurantees, final AmendStatus amendStatus)
	{
		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		final RatePlanModel ratePlan = new RatePlanModel();
		if (hasGurantees)
		{
			ratePlan.setGuarantee(Arrays.asList(createGuaranteeModel(true, true, 20d, GuaranteeType.PREPAYMENT)));
		}
		entryGroup.setRatePlan(ratePlan);
		if (hasEntries)
		{
			final List<AbstractOrderEntryModel> entries = new ArrayList<>();
			entries.add(createAbstractOrderEntryModel(100d, hasTaxes, true, amendStatus));
			entries.add(createAbstractOrderEntryModel(100d, hasTaxes, false, amendStatus));
			entryGroup.setEntries(entries);
		}
		return entryGroup;
	}

	private AbstractOrderEntryModel createAbstractOrderEntryModel(final double value, final boolean hasTaxes,
			final boolean isActive, final AmendStatus amendStatus)
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel()
		{
			@Override
			public Collection<TaxValue> getTaxValues()
			{
				return hasTaxes ? Arrays.asList(new TaxValue("APD", 10d, true, "TEST_CURRENCY_ISO_CODE")) : new ArrayList<>();
			}
		};
		entry.setProduct(new RoomRateProductModel());
		entry.setAmendStatus(amendStatus);
		entry.setTotalPrice(value);
		entry.setActive(isActive);
		return entry;
	}
}
