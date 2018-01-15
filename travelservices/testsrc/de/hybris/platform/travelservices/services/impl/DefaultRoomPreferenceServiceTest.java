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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.dao.RoomPreferenceDAO;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultRoomPreferenceService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRoomPreferenceServiceTest
{
	@InjectMocks
	private DefaultRoomPreferenceService roomPreferenceService;
	@Mock
	private RoomPreferenceDAO roomPreferenceDAO;
	@Mock
	private RoomPreferenceModel roomPreferenceModel;

	@Test
	public void testGetRoomPreferencesWithCodes()
	{
		Mockito.when(roomPreferenceDAO.getRoomPreferences(Matchers.anyList()))
				.thenReturn(Stream.of(roomPreferenceModel).collect(Collectors.toList()));
		Assert.assertEquals(1, roomPreferenceService.getRoomPreferences(Arrays.asList("roomPreference1")).size());
	}

	@Test
	public void testGetRoomPreferences()
	{
		Mockito.when(roomPreferenceDAO.getRoomPreferences(Matchers.anyString()))
				.thenReturn(Stream.of(roomPreferenceModel).collect(Collectors.toList()));
		Assert.assertEquals(1, roomPreferenceService.getRoomPreferences("roomPreference1").size());
	}

	@Test
	public void testGetRoomPreferencesWithEmptyCodes()
	{
		Mockito.when(roomPreferenceDAO.getRoomPreferences(Matchers.anyList()))
				.thenReturn(Stream.of(roomPreferenceModel).collect(Collectors.toList()));
		Assert.assertEquals(0, roomPreferenceService.getRoomPreferences(Arrays.asList("")).size());
	}

	@Test
	public void testGetRoomPreferencesWithEmptyCollection()
	{
		Assert.assertEquals(0, roomPreferenceService.getRoomPreferences(Collections.emptyList()).size());
	}
}
