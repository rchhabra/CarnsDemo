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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.List;


/**
 * Interface that exposes Traveler Type specific DAO services
 */
public interface TravellerDao extends Dao
{

	/**
	 * Find traveller traveller model based on the provided UID. The traveller linked to the last order created is returned.
	 *
	 * @param uid
	 * 		the uid
	 * @return TravellerModel traveller model
	 * @throws ModelNotFoundException
	 * 		the model not found exception
	 */
	TravellerModel findTraveller(String uid) throws ModelNotFoundException;

	/**
	 * Find traveller traveller model based on the provided UID and versionID.
	 *
	 * @param uid
	 * 		the uid
	 * @param versionID
	 * 		the versionID
	 * @return TravellerModel traveller model
	 * @throws ModelNotFoundException
	 * 		the model not found exception
	 */
	TravellerModel findTravellerByUIDAndVersionID(String uid, String versionID) throws ModelNotFoundException;

	/**
	 * Find saved travelers using the text inputed in first name field
	 *
	 * @param firstNameText
	 * @param passengerType
	 * @param customer
	 * @return
	 */
	List<TravellerModel> findSavedTravellersUsingFirstNameText(String firstNameText, String passengerType,
			CustomerModel customer);

	/**
	 * Find saved travelers using the text inputed in last name field
	 *
	 * @param surnameText
	 * @param passengerType
	 * @param customer
	 * @return
	 */
	List<TravellerModel> findSavedTravellersUsingSurnameText(String surnameText, String passengerType, CustomerModel customer);

}
