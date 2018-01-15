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

package de.hybris.platform.travelfacades.facades.customer;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.Collection;
import java.util.List;


/**
 * TravelCustomerFacade interface.
 */
public interface TravelCustomerFacade extends CustomerFacade
{
	/**
	 * This method creates a GuestCustomer and returns the Uid.
	 *
	 * @param email
	 *           String representing the email id of the customer.
	 * @param lastName
	 *           String representing the lastName of the guest customer.
	 * @return a String
	 * @throws DuplicateUidException
	 */
	String createGuestCustomer(String email, String lastName) throws DuplicateUidException;

	/**
	 * This method saves search made by Customer.
	 *
	 * @param savedSearchData
	 *           SavedSearchData holding information about the Fare-Search made by customer.
	 *
	 * @return a boolean true(SUCCESS)/ false(FAILURE).
	 */
	boolean saveCustomerSearch(SavedSearchData savedSearchData);

	/**
	 * This method is used to get the list of searches saved by current customer.
	 *
	 * @return a List of SavedSearchData.
	 */
	List<SavedSearchData> getCustomerSearches();

	/**
	 * This method removes search saved by current customer based on search reference ID.
	 *
	 * @param savedSearchID
	 *
	 * @return a boolean true(SUCCESS)/ false(FAILURE).
	 */
	boolean removeSavedSearch(String savedSearchID);

	/**
	 * This method removes n-number of old searches, based on creation time, saved by current customer.
	 *
	 * @param savedSearches
	 *           Collection of SavedSearchModel- representation of all the searches saved against Customer.
	 *
	 * @param customerModel
	 *           Object of CustomerModel representing current customer.
	 *
	 * @param number
	 *           Number of saved searches to be removed.
	 *
	 * @return void
	 */
	void removeOldSavedSearches(Collection<SavedSearchModel> savedSearches, CustomerModel customerModel, int number);

	/**
	 * Checks if current user is a B2b customer.
	 *
	 * @return true, if current user is B2b customer
	 */
	boolean isCurrentUserB2bCustomer();

}
