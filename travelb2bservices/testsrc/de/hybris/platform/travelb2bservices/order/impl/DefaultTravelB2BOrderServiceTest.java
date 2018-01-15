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

package de.hybris.platform.travelb2bservices.order.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelb2bservices.dao.B2BTravelOrderDao;
import de.hybris.platform.travelb2bservices.dao.PagedB2BTravelOrderDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * unit test for {@link DefaultTravelB2BOrderService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelB2BOrderServiceTest
{
	@InjectMocks
	DefaultTravelB2BOrderService defaultTravelB2BOrderService;

	@Mock
	private PagedB2BTravelOrderDao<OrderModel> pagedB2BTravelOrderDao;

	@Mock
	private B2BTravelOrderDao b2bTravelOrderDao;

	@Mock
	private PageableData pageableData;

	@Mock
	private SearchPageData<OrderModel> searchPageData;

	private final List<String> b2bUnitCodes = new ArrayList<String>(Arrays.asList("TEST_BTB_UNIT_CODE"));
	private final String email = "TEST_EMAIL";
	private final Date date = new Date();
	private final String costCenterUid = "TEST_COST_CENTER_UID";
	private final String currency = "TEST_CURRENCY";

	@Test
	public void testGetPagedOrders()
	{
		when(pagedB2BTravelOrderDao.findPagedOrders(b2bUnitCodes, email, date, date, costCenterUid, currency, pageableData))
				.thenReturn(searchPageData);

		Assert.assertEquals(searchPageData,
				defaultTravelB2BOrderService.getPagedOrders(pageableData, b2bUnitCodes, email, date, date, costCenterUid, currency));
	}

	@Test
	public void testFindTotal()
	{
		final OrderModel order = new OrderModel();
		order.setTotalPrice(100d);
		order.setTotalTax(10d);
		when(b2bTravelOrderDao.findOrders(b2bUnitCodes, email, date, date, costCenterUid, currency))
				.thenReturn(Arrays.asList(order));

		Assert.assertEquals(110d,
				defaultTravelB2BOrderService.findTotal(b2bUnitCodes, email, date, date, costCenterUid, currency).doubleValue(),
				0.001);
	}
}
