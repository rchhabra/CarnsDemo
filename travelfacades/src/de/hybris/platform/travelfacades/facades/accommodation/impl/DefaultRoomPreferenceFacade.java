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

package de.hybris.platform.travelfacades.facades.accommodation.impl;

import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.accommodation.RoomPreferenceFacade;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.services.RoomPreferenceService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link RoomPreferenceFacade}
 */
public class DefaultRoomPreferenceFacade implements RoomPreferenceFacade
{
	private RoomPreferenceService roomPreferenceService;

	private Converter<RoomPreferenceModel, RoomPreferenceData> roomPreferenceConverter;


	@Override
	public List<RoomPreferenceData> getRoomPreferences(final String roomPreferenceType)
	{
		final List<RoomPreferenceModel> results = getRoomPreferenceService().getRoomPreferences(roomPreferenceType);
		return Converters.convertAll(results, getRoomPreferenceConverter());
	}

	@Override
	public List<RoomPreferenceData> getRoomPreferences(final List<String> roomPreferenceCode)
	{
		final List<RoomPreferenceModel> results = getRoomPreferenceService().getRoomPreferences(roomPreferenceCode);
		return CollectionUtils.isNotEmpty(results) ? Converters.convertAll(results, getRoomPreferenceConverter())
				: Collections.emptyList();
	}

	@Override
	public Boolean saveRoomPreference(final int roomStayRefNum, final List<String> roomPreferenceCodes)
	{
		return getRoomPreferenceService().saveRoomPreference(roomStayRefNum, roomPreferenceCodes);
	}

	@Override
	public List<RoomPreferenceData> getRoomPreferencesForTypeAndAccommodation(final String roomPreferenceType,
			final List<RoomTypeData> roomTypes)
	{
		final List<String> roomTypeCodes = roomTypes.stream().map(RoomTypeData::getCode).collect(Collectors.toList());
		final List<RoomPreferenceModel> roomPreferenceModels = getRoomPreferenceService()
				.getRoomPreferencesForTypeAndAccommodation(roomPreferenceType, roomTypeCodes);

		return Converters.convertAll(roomPreferenceModels, getRoomPreferenceConverter());
	}

	/**
	 * @return the roomPreferenceService
	 */
	protected RoomPreferenceService getRoomPreferenceService()
	{
		return roomPreferenceService;
	}

	/**
	 * @param roomPreferenceService
	 *           the roomPreferenceService to set
	 */
	@Required
	public void setRoomPreferenceService(final RoomPreferenceService roomPreferenceService)
	{
		this.roomPreferenceService = roomPreferenceService;
	}

	/**
	 * @return the roomPreferenceConverter
	 */
	protected Converter<RoomPreferenceModel, RoomPreferenceData> getRoomPreferenceConverter()
	{
		return roomPreferenceConverter;
	}

	/**
	 * @param roomPreferenceConverter
	 *           the roomPreferenceConverter to set
	 */
	@Required
	public void setRoomPreferenceConverter(final Converter<RoomPreferenceModel, RoomPreferenceData> roomPreferenceConverter)
	{
		this.roomPreferenceConverter = roomPreferenceConverter;
	}

}
