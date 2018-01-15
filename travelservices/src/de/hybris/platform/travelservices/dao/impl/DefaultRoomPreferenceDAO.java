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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.RoomPreferenceDAO;
import de.hybris.platform.travelservices.enums.RoomPreferenceType;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link RoomPreferenceDAO}
 */
public class DefaultRoomPreferenceDAO extends AbstractItemDao implements RoomPreferenceDAO
{
	private static final String FIND_ROOM_PREFERENCES_BY_PREFERENCE_TYPE_QUERY = "select { rp : " + RoomPreferenceModel.PK
			+ " } from { "
			+ RoomPreferenceModel._TYPECODE + " as rp join " + RoomPreferenceType._TYPECODE + " as rpt on { rp : "
			+ RoomPreferenceModel.PREFERENCETYPE + " } = { rpt : pk } } where { rpt : code } = ?roomPreferenceType ";

	private static final String FIND_ROOM_PREFERENCES_BY_PK_QUERY = "select { rp : " + RoomPreferenceModel.PK + " } from { "
			+ RoomPreferenceModel._TYPECODE + " as rp } where { rp : " + RoomPreferenceModel.PK + " } in ( ?roomPreferenceCode )";

	@Override
	public List<RoomPreferenceModel> getRoomPreferences(final String roomPreferenceType)
	{
		validateParameterNotNull(roomPreferenceType, "RoomPreferenceType must not be null");
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("roomPreferenceType", roomPreferenceType);
		final SearchResult<RoomPreferenceModel> roomPreferenceModel = getFlexibleSearchService()
				.search(new FlexibleSearchQuery(FIND_ROOM_PREFERENCES_BY_PREFERENCE_TYPE_QUERY, queryParams));
		return roomPreferenceModel.getResult();
	}

	@Override
	public List<RoomPreferenceModel> getRoomPreferences(final List<String> roomPreferenceCode)
	{
		validateParameterNotNull(roomPreferenceCode, "RoomPreferenceType must not be null");
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("roomPreferenceCode", roomPreferenceCode);
		final SearchResult<RoomPreferenceModel> roomPreferenceModel = getFlexibleSearchService()
				.search(new FlexibleSearchQuery(FIND_ROOM_PREFERENCES_BY_PK_QUERY, queryParams));
		return roomPreferenceModel.getResult();
	}
}
