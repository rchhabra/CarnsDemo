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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.services.RoomPreferenceService;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Default implementation of {@link DefaultRoomPreferenceFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRoomPreferenceFacadeTest
{
	@InjectMocks
	DefaultRoomPreferenceFacade defaultRoomPreferenceFacade;
	@Mock
	private RoomPreferenceService roomPreferenceService;
	@Mock
	private Converter<RoomPreferenceModel, RoomPreferenceData> roomPreferenceConverter;

	@Test
	public void testGetRoomPreferences()
	{
		final RoomPreferenceModel roomPreferenceModel=new RoomPreferenceModel();
		final List<RoomPreferenceModel> results=Collections.singletonList(roomPreferenceModel);
		final RoomPreferenceData roomPreferenceData = new RoomPreferenceData();

		given(roomPreferenceConverter.convert(roomPreferenceModel)).willReturn(roomPreferenceData);
		given(roomPreferenceService.getRoomPreferences("roomPreferenceType")).willReturn(results);

		Assert.assertTrue(defaultRoomPreferenceFacade.getRoomPreferences("roomPreferenceType").contains(roomPreferenceData));

		final String roomPreferenceCode = "roomPreferenceCode";
		final List<String> roomPreferenceCodes = Collections.singletonList(roomPreferenceCode);
		given(roomPreferenceService.getRoomPreferences(roomPreferenceCodes)).willReturn(results);
		Assert.assertTrue(defaultRoomPreferenceFacade.getRoomPreferences(roomPreferenceCodes).contains(roomPreferenceData));

		given(roomPreferenceService.getRoomPreferences(roomPreferenceCodes)).willReturn(Collections.emptyList());
		Assert.assertTrue(CollectionUtils.isEmpty(defaultRoomPreferenceFacade.getRoomPreferences(roomPreferenceCodes)));
	}

}
