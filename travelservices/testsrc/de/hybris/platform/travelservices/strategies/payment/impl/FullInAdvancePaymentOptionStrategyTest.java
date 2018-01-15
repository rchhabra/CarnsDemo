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
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.strategies.payment.EntryTypePaymentInfoCreationStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link FullInAdvancePaymentOptionStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FullInAdvancePaymentOptionStrategyTest
{
	@InjectMocks
	FullInAdvancePaymentOptionStrategy fullInAdvancePaymentOptionStrategy;
	private List<EntryTypePaymentInfoCreationStrategy> entryTypePaymentInfoCreationStrategies;
	@Mock
	TransportationPayInAdvancePaymentInfoStrategy transportationPayInAdvancePaymentInfoStrategy;

	@Before
	public void setUp()
	{
		fullInAdvancePaymentOptionStrategy
				.setEntryTypePaymentInfoCreationStrategies(Arrays.asList(transportationPayInAdvancePaymentInfoStrategy));

	}
	@Test
	public void testCreate()
	{
		final List<EntryTypePaymentInfo> entryTypePaymentInfo = new ArrayList<>();
		entryTypePaymentInfo.add(new EntryTypePaymentInfo());
		when(transportationPayInAdvancePaymentInfoStrategy.create(Matchers.any(AbstractOrderModel.class)))
				.thenReturn(entryTypePaymentInfo);
		final OrderModel order = new OrderModel();
		Assert.assertNotNull(fullInAdvancePaymentOptionStrategy.create(order));
	}

	@Test
	public void testCreateForNull()
	{
		when(transportationPayInAdvancePaymentInfoStrategy.create(Matchers.any(AbstractOrderModel.class)))
				.thenReturn(null);
		final OrderModel order = new OrderModel();
		Assert.assertNotNull(fullInAdvancePaymentOptionStrategy.create(order));
	}
}
