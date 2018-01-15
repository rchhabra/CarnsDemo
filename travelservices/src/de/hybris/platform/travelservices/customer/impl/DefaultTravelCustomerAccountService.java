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

import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;
import de.hybris.platform.travelservices.customer.dao.TravelCustomerAccountDao;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.List;


/**
 * Default implementation of {@link TravelCustomerAccountService}
 * 
 */
public class DefaultTravelCustomerAccountService extends DefaultCustomerAccountService implements TravelCustomerAccountService
{

	@Override
	public OrderModel getOrderModelByOriginalOrderCode(final String bookingReference, final BaseStoreModel baseStoreModel)
	{
		final TravelCustomerAccountDao customerAccountDao = (TravelCustomerAccountDao) getCustomerAccountDao();
		return customerAccountDao.findOrderModelByOriginalOrderCode(bookingReference, baseStoreModel);
	}

	@Override
	public List<OrderModel> getOrdersFromOrderUserMapping(final CustomerModel customerModel)
	{
		final TravelCustomerAccountDao customerAccountDao = (TravelCustomerAccountDao) getCustomerAccountDao();
		return customerAccountDao.findOrdersByOrderUserMapping(customerModel);
	}

	@Override
	public OrderUserAccountMappingModel getOrderUserMapping(final String orderCode, final CustomerModel customerModel)
	{
		final TravelCustomerAccountDao customerAccountDao = (TravelCustomerAccountDao) getCustomerAccountDao();
		return customerAccountDao.findOrderUserMapping(orderCode, customerModel);
	}

	@Override
	public SavedSearchModel findSavedSearch(final String savedSearchID)
	{
		final TravelCustomerAccountDao customerAccountDao = (TravelCustomerAccountDao) getCustomerAccountDao();

		return customerAccountDao.findSavedSearch(savedSearchID);
	}


}
