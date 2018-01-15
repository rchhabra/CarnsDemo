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

package de.hybris.platform.travelservices.customer;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.List;


/**
 * This extension of CustomerAccountService provides custom APIs to get customer/order details
 */
public interface TravelCustomerAccountService extends CustomerAccountService
{

	/**
	 * Gets booking based on booking reference and base store by calling respective Dao method
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @param baseStoreModel
	 * 		the base store model
	 * @return order model by original order code
	 */
	OrderModel getOrderModelByOriginalOrderCode(String bookingReference, BaseStoreModel baseStoreModel);

	/**
	 * Fetches all bookings for the customer where the customer is not an owner but traveling
	 *
	 * @param customerModel
	 * 		the customer model
	 * @return orders from order user mapping
	 */
	List<OrderModel> getOrdersFromOrderUserMapping(CustomerModel customerModel);

	/**
	 * Returns a particular Order-UserAccount Mapping for the customer
	 *
	 * @param orderCode
	 * 		the order code
	 * @param customerModel
	 * 		the customer model
	 * @return order user mapping
	 */
	OrderUserAccountMappingModel getOrderUserMapping(String orderCode, CustomerModel customerModel);

	/**
	 * Finds search saved by current customer based on search reference ID.
	 *
	 * @param savedSearchID
	 * 		the saved search id
	 * @return savedSearchModel saved search model
	 */
	SavedSearchModel findSavedSearch(String savedSearchID);


}
