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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.ReasonForTravelData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerPreferenceData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;

import java.util.List;
import java.util.Map;


/**
 * Facade that exposes Traveller specific services
 */
public interface TravellerFacade
{

	/**
	 * Creates a new instance of TravellerData with relevant details populated
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
	 * @return newly created instance of Traveller
	 */
	TravellerData createTraveller(String travellerType, String passengerType, String travellerCode,
			int passengerNumber, String travellerUidPrefix);

	/**
	 * Creates a new instance of TravellerData with relevant details populated
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
	 * @param cartOrOrderCode
	 * 		the cartOrOrderCode
	 * @return newly created instance of Traveller
	 */
	TravellerData createTraveller(String travellerType, String passengerType, String travellerCode,
			int passengerNumber, String travellerUidPrefix, String cartOrOrderCode);

	/**
	 * Facade which returns a list of ReasonForTravel types
	 *
	 * @return List<ReasonForTravelData> reason for travel types
	 */
	List<ReasonForTravelData> getReasonForTravelTypes();

	/**
	 * Method returns a unique list of travellers from the Cart Entries
	 *
	 * @return List<TravellerData> travellers for cart entries
	 */
	List<TravellerData> getTravellersForCartEntries();

	/**
	 * Method returns a list of saved travellers that are assigned to the current logged in User
	 *
	 * @return List<TravellerData> saved travellers for current user
	 */
	List<TravellerData> getSavedTravellersForCurrentUser();

	/**
	 * Update traveller details.
	 *
	 * @param travellers
	 * 		the travellers
	 */
	void updateTravellerDetails(List<TravellerData> travellers);

	/**
	 * Update customer saved travellers.
	 *
	 * @param travellers
	 * 		the travellers
	 */
	void updateCustomerSavedTravellers(List<TravellerData> travellers);

	/**
	 * Method returns the CustomerTravellerInstance details from the current User
	 *
	 * @return TravellerData current user details
	 */
	TravellerData getCurrentUserDetails();

	/**
	 * Method checks to see if the current user is a logged in User or not
	 *
	 * @return boolean boolean
	 */
	boolean isAnonymousUser();

	/**
	 * Method returns the TravellerInstance details for the traveller id
	 *
	 * @param travellerId
	 * 		the traveller id
	 * @return TravellerData traveller
	 */
	TravellerData getTraveller(String travellerId);

	/**
	 * Method returns the TravellerInstance details for the traveller id and bookingInfo
	 *
	 * @param travellerId
	 * 		the traveller id
	 * @param bookingInfo
	 * 		the bookingInfo
	 * @return TravellerData traveller
	 */
	TravellerData getTraveller(String travellerId, String bookingInfo);

	/**
	 * Method returns the TravellerInstance identified by the travellerCode from the current Cart Session
	 *
	 * @param travellerCode
	 * 		the traveller code
	 * @return TravellerData traveller from current cart
	 */
	TravellerData getTravellerFromCurrentCart(String travellerCode);

	/**
	 * Method removes the traveller with the uid from the current users list of saved travellers. If successful, the
	 * method will return the TravellerData for the traveller that has been removed otherwise it will return null if
	 * remove was unsuccessful.
	 *
	 * @param uid
	 * 		the uid
	 * @return TravellerData traveller data
	 */
	TravellerData removeSavedTraveller(String uid);

	/**
	 * Method retrieves a list of SpecialServiceRequestModels from the database based on the list of
	 * specialServiceRequestCode and then sets the list of SpecialServiceRequestModels against the Current Users account
	 *
	 * @param specialServiceRequestCode
	 * 		the special service request code
	 */
	void updateCurrentUserSpecialRequestDetails(List<String> specialServiceRequestCode);

	/**
	 * This method will return the list of travellers for the given originDestinationRefNumber in the given
	 * ReservationData. Empty list if there's no leg found with the given originDestinationRefNumber in the
	 * ReservationData
	 *
	 * @param reservationData
	 * 		the ReservationData representing a booking
	 * @param originDestinationRefNumber
	 * 		the identifier of a leg
	 * @return the List of travellers
	 */
	List<TravellerData> retrieveTravellers(ReservationData reservationData, int originDestinationRefNumber);

	/**
	 * Method get a list of traveller preference for the currently logged in user
	 *
	 * @return List<TravellerPreferenceData> traveller preferences
	 */
	List<TravellerPreferenceData> getTravellerPreferences();

	/**
	 * Method take a list of traveller preferences and saves them against the current users account
	 *
	 * @param selectedTravellerPreferences
	 * 		the selected traveller preferences
	 */
	void getSaveTravellerPreferences(List<TravellerPreferenceData> selectedTravellerPreferences);

	/**
	 * Method saves the Passenger Information against the current user
	 *
	 * @param travellerData
	 * 		the traveller data
	 */
	void updatePassengerInformation(TravellerData travellerData);

	/**
	 * Method gets the Passenger Information from the current user
	 *
	 * @return current user's passenger information
	 */
	PassengerInformationData getPassengerInformation();

	/**
	 * This method returns the traveller data for the currentLogged in customer.
	 *
	 * @return TravellerData instance of current customer
	 */
	TravellerData getCustomerTravellerInstanceData();

	/**
	 * This method populates a Map of Traveller Code with their display names for a given Passenger Type code
	 *
	 * @param travellerDatas
	 * 		the traveller datas
	 * @return Map<String MapStringString>> object
	 */
	Map<String, Map<String, String>> populateTravellersNamesMap(List<TravellerData> travellerDatas);

	/**
	 * Retrieves a list of travelers using first name text against the current customer
	 *
	 * @param text
	 * @param passengerType
	 * @return
	 */
	List<TravellerData> findSavedTravellersUsingFirstName(String text, String passengerType);

	/**
	 * Retrieves a list of travelers using last name text against the current customer
	 *
	 * @param text
	 * @param passengerType
	 * @return
	 */
	List<TravellerData> findSavedTravellersUsingSurname(String text, String passengerType);
}
