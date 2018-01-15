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

package de.hybris.platform.travelservices.strategies.stock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link StockReservationForDefaultWarehouseCreationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StockReservationForDefaultWarehouseCreationStrategyTest
{
	@InjectMocks
	StockReservationForDefaultWarehouseCreationStrategy stockReservationForDefaultWarehouseCreationStrategy;

	@Test
	public void testCreate()
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		Assert.assertTrue(CollectionUtils.isEmpty(stockReservationForDefaultWarehouseCreationStrategy.create(entry)));
	}
}
