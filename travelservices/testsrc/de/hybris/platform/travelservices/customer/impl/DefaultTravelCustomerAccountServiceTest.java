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
*/

package de.hybris.platform.travelservices.customer.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.customer.dao.TravelCustomerAccountDao;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCustomerAccountServiceTest
{
	private DefaultTravelCustomerAccountService travelCustomerAccountService;

	@Mock
	private TravelCustomerAccountDao customerAccountDao;

	@Mock
	private BaseStoreModel baseStoreModel;

	/**
	 * Setup of data for the test case
	 */
	@Before
	public void setup()
	{
		travelCustomerAccountService = new DefaultTravelCustomerAccountService();
		travelCustomerAccountService.setCustomerAccountDao(customerAccountDao);
	}

	@Test
	public void testGetOrderModelByOriginalOrderCode()
	{
		final OrderModel originalOrder = new OrderModel();
		originalOrder.setCode("00010000001");

		final OrderModel order = new OrderModel();
		order.setOriginalOrder(originalOrder);
		when(customerAccountDao.findOrderModelByOriginalOrderCode(Matchers.anyString(), Matchers.any(BaseStoreModel.class)))
				.thenReturn(order);
		travelCustomerAccountService.getOrderModelByOriginalOrderCode("00010000001", baseStoreModel);
		verify(customerAccountDao, times(1)).findOrderModelByOriginalOrderCode(Matchers.anyString(),
				Matchers.any(BaseStoreModel.class));
	}

	@Test
	public void testFindSavedSearch()
	{
		final SavedSearchModel search = new SavedSearchModel();
		final String testSearchID = "testSearchID";
		search.setEncodedSearch(testSearchID);

		//Mocking call to method.
		when(customerAccountDao.findSavedSearch(Matchers.anyString())).thenReturn(search);

		//call to method being tested.
		travelCustomerAccountService.findSavedSearch(testSearchID);

		verify(customerAccountDao, times(1)).findSavedSearch(Matchers.anyString());
	}

	@Test
	public void testGetOrdersFromOrderUserMapping()
	{
		final List<OrderModel> orderModels = new ArrayList<>();
		orderModels.add(new OrderModel());
		final CustomerModel customer = new CustomerModel();
		when(customerAccountDao.findOrdersByOrderUserMapping(customer)).thenReturn(orderModels);
		Assert.assertEquals(orderModels, travelCustomerAccountService.getOrdersFromOrderUserMapping(customer));
	}

	@Test
	public void testGetOrderUserMapping()
	{
		final OrderUserAccountMappingModel orderUserAccountMappingModel = new OrderUserAccountMappingModel();
		final CustomerModel customer = new CustomerModel();
		final String orderCode = "0001";
		when(customerAccountDao.findOrderUserMapping(orderCode, customer)).thenReturn(orderUserAccountMappingModel);
		Assert.assertEquals(orderUserAccountMappingModel, travelCustomerAccountService.getOrderUserMapping(orderCode, customer));
	}
}
