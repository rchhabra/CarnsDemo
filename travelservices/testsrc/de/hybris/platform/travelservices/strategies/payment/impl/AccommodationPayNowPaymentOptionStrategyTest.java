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
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link AccommodationPayNowPaymentOptionStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationPayNowPaymentOptionStrategyTest
{

	@InjectMocks
	AccommodationPayNowPaymentOptionStrategy accommodationPayNowPaymentOptionStrategy;

	@Mock
	private BookingService bookingService;

	@Test
	public void testCreateForEmptyEntryGroups()
	{
		final OrderModel order = new OrderModel();
		when(bookingService.getAccommodationOrderEntryGroups(order)).thenReturn(null);
		Assert.assertTrue(CollectionUtils.isEmpty(accommodationPayNowPaymentOptionStrategy.create(order).getEntryTypeInfos()));
	}

	@Test
	public void testCreate()
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();
		final AccommodationOrderEntryGroupModel entryGroup1 = createEntryGroup(true, true, AmendStatus.NEW);
		final AccommodationOrderEntryGroupModel entryGroup2 = createEntryGroup(true, true, AmendStatus.NEW);
		entryGroups.add(entryGroup1);
		entryGroups.add(entryGroup2);

		final OrderModel order = new OrderModel();
		order.setOriginalOrder(order);
		when(bookingService.getAccommodationOrderEntryGroups(order)).thenReturn(entryGroups);
		when(bookingService.getOrderTotalPaidByEntryGroup(Matchers.any(OrderModel.class),
				Matchers.any(AccommodationOrderEntryGroupModel.class))).thenReturn(BigDecimal.valueOf(100d));
		Assert.assertTrue(CollectionUtils.isNotEmpty(accommodationPayNowPaymentOptionStrategy.create(order).getEntryTypeInfos()));

	}

	private AccommodationOrderEntryGroupModel createEntryGroup(final boolean hasEntries, final boolean hasTaxes,
			final AmendStatus amendStatus)
	{
		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		if (hasEntries)
		{
			final List<AbstractOrderEntryModel> entries = new ArrayList<>();
			entries.add(createAbstractOrderEntryModel(100d, hasTaxes, amendStatus));
			entryGroup.setEntries(entries);
		}
		return entryGroup;
	}

	private AbstractOrderEntryModel createAbstractOrderEntryModel(final double value, final boolean hasTaxes,
			final AmendStatus amendStatus)
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
		return entry;
	}
}
