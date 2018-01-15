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

package de.hybris.platform.travelfulfilmentprocess.test.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelfulfilmentprocess.strategy.impl.DefaultTravelSplittingStrategy;
import de.hybris.platform.travelfulfilmentprocess.strategy.impl.SplittingStrategyByAccommodationType;
import de.hybris.platform.travelfulfilmentprocess.strategy.impl.SplittingStrategyByTransportType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultTravelSplittingStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelSplittingStrategyTest
{
	@InjectMocks
	DefaultTravelSplittingStrategy defaultTravelSplittingStrategy;

	@Mock
	private SplittingStrategyByTransportType splittingStrategyByTransportType;

	@Mock
	private SplittingStrategyByAccommodationType splittingStrategyByAccommodationType;

	@Before
	public void test()
	{
		defaultTravelSplittingStrategy.setStrategiesByTypeList(
				Stream.of(splittingStrategyByAccommodationType, splittingStrategyByTransportType).collect(Collectors.toList()));
	}

	@Test
	public void testPerform()
	{
		final OrderEntryGroup orderEntryGroup = new OrderEntryGroup();

		final List<OrderEntryGroup> orderEntryGroups = new ArrayList<>();
		orderEntryGroups.add(orderEntryGroup);
		Mockito.when(splittingStrategyByTransportType.perform(orderEntryGroups)).thenReturn(orderEntryGroups);
		Mockito.when(splittingStrategyByAccommodationType.perform(orderEntryGroups)).thenReturn(orderEntryGroups);

		final List<OrderEntryGroup> results = defaultTravelSplittingStrategy.perform(orderEntryGroups);
		Assert.assertTrue(CollectionUtils.containsAny(orderEntryGroups, results));
	}

	@Test
	public void testAfterSplitting()
	{
		final OrderEntryGroup group = new OrderEntryGroup();
		final ConsignmentModel createdOne = new ConsignmentModel();

		Mockito.doNothing().when(splittingStrategyByTransportType).afterSplitting(group, createdOne);
		Mockito.doNothing().when(splittingStrategyByAccommodationType).afterSplitting(group, createdOne);
		defaultTravelSplittingStrategy.afterSplitting(group, createdOne);
		Mockito.verify(splittingStrategyByTransportType, Mockito.times(1)).afterSplitting(group, createdOne);
	}

}
