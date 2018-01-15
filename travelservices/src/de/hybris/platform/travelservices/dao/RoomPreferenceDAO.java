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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;

import java.util.List;


/**
 * RoomPreferenceDAO interface which provides functionality to manage Room Preferences.
 */
public interface RoomPreferenceDAO
{
	/**
	 * Gets room preferences.
	 *
	 * @param roomPreferenceType
	 * 		the room preference type
	 * @return the room preferences
	 */
/*
	 * Fetches the room preferences for the accommodation based on the roomPreferenceType.
	 *
	 * @param roomPreferenceType
	 *
	 * @return roomPreferences list of RoomPreferenceModel
	 */
	List<RoomPreferenceModel> getRoomPreferences(String roomPreferenceType);

	/**
	 * Gets room preferences.
	 *
	 * @param roomPreferenceCode
	 * 		the room preference code
	 * @return the room preferences
	 */
/*
	 * Fetches the room preferences for the accommodation based on the roomPreferenceCode.
	 *
	 * @param roomPreferenceCode list of String
	 *
	 * @return roomPreferencesModel
	 */
	List<RoomPreferenceModel> getRoomPreferences(List<String> roomPreferenceCode);

}
