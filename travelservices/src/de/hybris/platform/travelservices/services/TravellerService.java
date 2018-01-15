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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.List;
import java.util.Map;


/**
 * Traveller Service interface provides Traveller specific services
 */
public interface TravellerService
{

	/**
	 * Create traveller traveller model.
	 *
	 * @param travellerType
	 * 		the traveller type
	 * @param passengerType
	 * 		the passenger type
	 * @param travellerCode
	 * 		the traveller code
	 * @param passengerNumber
	 * 		the passenger number
	 * @param travellerUidPrefix
	 * 		the traveller uid prefix
	 * @return traveller model
	 */
	TravellerModel createTraveller(String travellerType, String passengerType, String travellerCode, int passengerNumber,
			String travellerUidPrefix);

	/**
	 * Create traveller traveller model.
	 *
	 * @param travellerType
	 * 		the traveller type
	 * @param passengerType
	 * 		the passenger type
	 * @param travellerCode
	 * 		the traveller code
	 * @param passengerNumber
	 * 		the passenger number
	 * @param travellerUidPrefix
	 * 		the traveller uid prefix
	 * @param orderOrCartCode
	 * 		the order or cart code that needs to be set in the versionID
	 * @return traveller model
	 */
	TravellerModel createTraveller(String travellerType, String passengerType, String travellerCode, int passengerNumber,
			String travellerUidPrefix, String orderOrCartCode);

	/**
	 * Returns the Traveller in the system identified by the given uid
	 *
	 * @param uid
	 * 		The traveller Uid
	 * @return TravellerModel existing traveller
	 * @throws ModelNotFoundException
	 * 		the model not found exception
	 */
	TravellerModel getExistingTraveller(String uid) throws ModelNotFoundException;

	/**
	 * Returns the Traveller in the system identified by the given uid and versionID
	 *
	 * @param uid
	 * 		The traveller Uid
	 * @param versionID
	 * 		The traveller versionID
	 * @return TravellerModel existing traveller
	 * @throws ModelNotFoundException
	 * 		the model not found exception
	 */
	TravellerModel getExistingTraveller(String uid, String versionID) throws ModelNotFoundException;

	/**
	 * Returns the Traveller identified by the code in the current Session Cart if present, null otherwise
	 *
	 * @param travellerCode
	 * 		The code that identifies a Traveller in the Cart
	 * @return TravellerModel or null
	 */
	TravellerModel getTravellerFromCurrentCart(String travellerCode);

	/**
	 * Returns the Traveller identified by the code in the current Session Cart if present, null otherwise
	 *
	 * @param travellerUID
	 * 		The code that identifies a Traveller in the Cart
	 * @param cartModel
	 * @return TravellerModel or null
	 */
	TravellerModel getTravellerFromCurrentCartByUID(String travellerUID, CartModel cartModel);

	/**
	 * Retrieves a list of travellers for each leg of the journey based on abstract order entries
	 *
	 * @param abstractOrderModel
	 * 		- given cart/order model
	 * @return list of travellers for each leg of the journey
	 */
	Map<Integer, List<TravellerModel>> getTravellersPerLeg(AbstractOrderModel abstractOrderModel);

	/**
	 * Method takes a list of AbstractOrderEntryModel and turns a unique list of traveller models
	 *
	 * @param abstractOrderEntryModels
	 * 		the abstract order entry models
	 * @return travellers
	 */
	List<TravellerModel> getTravellers(List<AbstractOrderEntryModel> abstractOrderEntryModels);

	/**
	 * Retrieves a list of saved travelers against a current customer using a text inputed as part of first name field
	 *
	 * @param nameText
	 * @param passengerType
	 * @param customer
	 * @return
	 */
	List<TravellerModel> findSavedTravellersUsingFirstNameText(final String nameText,
			final String passengerType, final CustomerModel customer);

	/**
	 * Retrieves a list of saved travelers against a current customer using a text inputed as part of last name field
	 *
	 * @param nameText
	 * @param passengerType
	 * @param customer
	 * @return
	 */
	List<TravellerModel> findSavedTravellersUsingLastNameText(final String nameText,
			final String passengerType, final CustomerModel customer);
}
