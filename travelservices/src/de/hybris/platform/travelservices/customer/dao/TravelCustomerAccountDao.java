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

package de.hybris.platform.travelservices.customer.dao;

import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.List;


/**
 * Extension of CustomerAccountDao to expose custom APIs to retrieve customer/order specific data.
 */
public interface TravelCustomerAccountDao extends CustomerAccountDao
{
	/**
	 * Gets the order where original order code is equal to booking reference, store is equal to baseStoreModel passed as
	 * parameter, version if equals Null and status is NOT equal to amendment in progress.
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @param baseStoreModel
	 * 		the base store model
	 * @return order model
	 */
	OrderModel findOrderModelByOriginalOrderCode(String bookingReference, BaseStoreModel baseStoreModel);

	/**
	 * Fetches all bookings for the customer where the customer is not an owner but traveling
	 *
	 * @param customerModel
	 * 		the customer model
	 * @return list
	 */
	List<OrderModel> findOrdersByOrderUserMapping(CustomerModel customerModel);

	/**
	 * Returns a particular Order-UserAccount Mapping for the customer
	 *
	 * @param orderCode
	 * 		the order code
	 * @param customerModel
	 * 		the customer model
	 * @return order user account mapping model
	 */
	OrderUserAccountMappingModel findOrderUserMapping(String orderCode, CustomerModel customerModel);

	/**
	 * Finds search saved by current customer based on search reference ID.
	 *
	 * @param savedSearchID
	 * 		the saved search id
	 * @return savedSearchModel saved search model
	 */
	SavedSearchModel findSavedSearch(String savedSearchID);

}
