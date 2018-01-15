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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;

import java.util.List;


/**
 * RoomPreference Service interface which provides functionality to manage Room Preferences.
 */
public interface RoomPreferenceService
{
	/**
	 * Fetches the room preferences for the accommodation based on the roomPreferenceType.
	 *
	 * @param roomPreferenceType
	 * 		the room preference type
	 *
	 * @return roomPreferences list of RoomPreferenceModel
	 */
	List<RoomPreferenceModel> getRoomPreferences(String roomPreferenceType);

	/**
	 * Fetches the room preferences for the accommodation based on the roomPreferenceCode.
	 *
	 * @param roomPreferenceCode list of String
	 *
	 * @return roomPreferences list of RoomPreferenceModel
	 */
	List<RoomPreferenceModel> getRoomPreferences(List<String> roomPreferenceCode);

	/**
	 * Saves roomPreference against AccommodationOrderEntryGroup for the roomStayRefNum.
	 *
	 * @param roomStayRefNum
	 *           the room stay ref num
	 * @param roomPreferenceCodes
	 *           the roomPreferenceCodes
	 * @return boolean
	 */
	Boolean saveRoomPreference(int roomStayRefNum, List<String> roomPreferenceCodes);

	/**
	 * Returns the list of room preference of type roomPreferenceType for the given list of roomTypeCodes
	 *
	 * @param roomPreferenceType
	 * 		the room preference type
	 * @param roomTypeCodes
	 * 		the list of room type codes
	 *
	 * @return the list of room preferences
	 */
	List<RoomPreferenceModel> getRoomPreferencesForTypeAndAccommodation(String roomPreferenceType, List<String> roomTypeCodes);
}
