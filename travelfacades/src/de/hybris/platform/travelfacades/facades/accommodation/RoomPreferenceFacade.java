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

package de.hybris.platform.travelfacades.facades.accommodation;

import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;

import java.util.List;


/**
 * Facade that exposes the methods for the accommodation room preferences functionality
 */
public interface RoomPreferenceFacade
{

	/**
	 * Fetches the room preferences for the accommodation based on the roomPreferenceType.
	 *
	 * @param roomPreferenceType
	 * 		the room preference type
	 * @return roomPreferences list of RoomPreferenceData
	 */
	List<RoomPreferenceData> getRoomPreferences(String roomPreferenceType);

	/**
	 * Fetches the room preferences for the accommodation based on the roomPreferenceCode.
	 *
	 * @param roomPreferenceCode
	 * 		list of String
	 * @return roomPreferences list of RoomPreferenceData
	 */
	List<RoomPreferenceData> getRoomPreferences(List<String> roomPreferenceCode);

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
	 * Fetches the room preferences for the accommodation based on the roomPreferenceType and the list of room types.
	 *
	 * @param roomPreferenceType
	 * 		the room preference type
	 * @param roomTypes
	 * 		the list of room types
	 *
	 * @return roomPreferences list of RoomPreferenceData
	 */
	List<RoomPreferenceData> getRoomPreferencesForTypeAndAccommodation(String roomPreferenceType, List<RoomTypeData> roomTypes);
}
