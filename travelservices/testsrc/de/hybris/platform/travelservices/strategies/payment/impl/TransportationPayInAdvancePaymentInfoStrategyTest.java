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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TransportationPayInAdvancePaymentInfoStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportationPayInAdvancePaymentInfoStrategyTest
{
	@InjectMocks
	TransportationPayInAdvancePaymentInfoStrategy transportationPayInAdvancePaymentInfoStrategy;

	@Test
	public void testCreateForEntriesOfAccommodationOnly()
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(100d, true, true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION));
		entries.add(createAbstractOrderEntryModel(100d, true, true, AmendStatus.SAME, OrderEntryType.ACCOMMODATION));

		final AccommodationOrderEntryGroupModel entryGroup1 = createEntryGroup(true, true, true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION);
		final AccommodationOrderEntryGroupModel entryGroup2 = createEntryGroup(true, true, true, AmendStatus.SAME,
				OrderEntryType.ACCOMMODATION);
		entryGroups.add(entryGroup1);
		entryGroups.add(entryGroup2);
		final OrderModel order = new OrderModel();
		order.setOriginalOrder(order);
		order.setEntries(entries);
		Assert.assertTrue(CollectionUtils.isEmpty(transportationPayInAdvancePaymentInfoStrategy.create(order)));

	}

	@Test
	public void testCreateForEntriesOfSameAmendStatusOnly()
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(100d, true, true, AmendStatus.SAME, OrderEntryType.TRANSPORT));
		entries.add(createAbstractOrderEntryModel(100d, true, false, AmendStatus.SAME, OrderEntryType.TRANSPORT));

		final AccommodationOrderEntryGroupModel entryGroup1 = createEntryGroup(true, true, true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION);
		final AccommodationOrderEntryGroupModel entryGroup2 = createEntryGroup(true, true, true, AmendStatus.SAME,
				OrderEntryType.ACCOMMODATION);
		entryGroups.add(entryGroup1);
		entryGroups.add(entryGroup2);
		final OrderModel order = new OrderModel();
		order.setOriginalOrder(createOriginalOrder());
		order.setTotalDiscounts(5.00);
		order.setEntries(entries);
		Assert.assertTrue(CollectionUtils.isEmpty(transportationPayInAdvancePaymentInfoStrategy.create(order)));

	}

	@Test
	public void testCreate()
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(100d, true, true, AmendStatus.NEW, OrderEntryType.TRANSPORT));
		entries.add(createAbstractOrderEntryModel(100d, true, false, AmendStatus.SAME, OrderEntryType.TRANSPORT));

		final AccommodationOrderEntryGroupModel entryGroup1 = createEntryGroup(true, true, true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT);
		final AccommodationOrderEntryGroupModel entryGroup2 = createEntryGroup(true, true, true, AmendStatus.SAME,
				OrderEntryType.TRANSPORT);
		entryGroups.add(entryGroup1);
		entryGroups.add(entryGroup2);
		final OrderModel order = new OrderModel();
		order.setOriginalOrder(createOriginalOrder());
		order.setTotalDiscounts(5.00);
		order.setEntries(entries);
		Assert.assertTrue(CollectionUtils.isNotEmpty(transportationPayInAdvancePaymentInfoStrategy.create(order)));

	}

	private AccommodationOrderEntryGroupModel createEntryGroup(final boolean hasEntries, final boolean hasTaxes,
			final boolean hasGurantees, final AmendStatus amendStatus, final OrderEntryType type)
	{
		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		if (hasEntries)
		{
			final List<AbstractOrderEntryModel> entries = new ArrayList<>();
			entries.add(createAbstractOrderEntryModel(100d, true, hasTaxes, amendStatus, type));
			entryGroup.setEntries(entries);
		}
		return entryGroup;
	}

	private AbstractOrderEntryModel createAbstractOrderEntryModel(final double value, final boolean hasTaxes,
			final boolean isActive, final AmendStatus amendStatus, final OrderEntryType type)
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel()
		{
			@Override
			public Collection<TaxValue> getTaxValues()
			{
				return hasTaxes ? Arrays.asList(new TaxValue("APD", 10d, true, "TEST_CURRENCY_ISO_CODE")) : new ArrayList<>();
			}
		};
		entry.setActive(isActive);
		entry.setType(type);
		entry.setProduct(new RoomRateProductModel());
		entry.setAmendStatus(amendStatus);
		entry.setTotalPrice(value);
		return entry;
	}
	
	private OrderModel createOriginalOrder(){

		final OrderModel originalOrder = new OrderModel();

		final List<AbstractOrderEntryModel> originalEntries = new ArrayList<>();
		originalEntries.add(createAbstractOrderEntryModel(100d, true, true, AmendStatus.NEW, OrderEntryType.TRANSPORT));
		originalEntries.add(createAbstractOrderEntryModel(100d, true, false, AmendStatus.SAME, OrderEntryType.TRANSPORT));
		originalOrder.setTotalDiscounts(10.00);
		originalOrder.setEntries(originalEntries);
		return originalOrder;
		
	}
}
