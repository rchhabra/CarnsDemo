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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.RoomPreferenceType;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationReservationReservedRoomStayBasicDetailsHandlerTest
{
	@InjectMocks
	AccommodationReservationReservedRoomStayBasicDetailsHandler handler;

	@Mock
	private AbstractOrderModel orderModel;

	@Mock
	private BookingService bookingService;

	@Mock
	private Converter<RatePlanModel, RatePlanData> ratePlanConverter;

	@Mock
	private Converter<AccommodationModel, RoomTypeData> roomTypeConverter;

	private TestDataSetup testSetUp;


	@Before
	public void setUp()
	{
		testSetUp = new TestDataSetup();
	}

	@Test
	public void testHandle()
	{
		given(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.willReturn(testSetUp.createAccommodationOrderEntryGroups());
		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setCode("TEST_RATE_PLAN_CODE");
		given(ratePlanConverter.convert(Matchers.any(RatePlanModel.class))).willReturn(ratePlanData);
		final RoomTypeData roomData = new RoomTypeData();
		given(roomTypeConverter.convert(testSetUp.createAccommodationOrderEntryGroups().get(0).getAccommodation()))
				.willReturn(roomData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		handler.handle(orderModel, accommodationReservationData);
		assertTrue(CollectionUtils.isNotEmpty(accommodationReservationData.getRoomStays()));

	}

	@Test
	public void testHandleForNullReservationData()
	{
		given(bookingService.getAccommodationOrderEntryGroups(orderModel)).willReturn(Collections.emptyList());
		handler.handle(orderModel, null);
		Mockito.verify(bookingService, Mockito.times(0)).getAccommodationOrderEntryGroups(orderModel);
	}

	private class TestDataSetup
	{
		private List<AccommodationOrderEntryGroupModel> createAccommodationOrderEntryGroups()
		{
			final AccommodationOrderEntryGroupModel accOrderEntryGroup = new AccommodationOrderEntryGroupModel();
			final SimpleDateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			try
			{
				accOrderEntryGroup.setStartingDate(dateFormat.parse("09/12/2016"));
				accOrderEntryGroup.setEndingDate(dateFormat.parse("11/12/2016"));
				accOrderEntryGroup.setRoomStayRefNumber(0);

				final RatePlanModel ratePlanModel = new RatePlanModel();
				accOrderEntryGroup.setRatePlan(ratePlanModel);
				final AccommodationModel accommodationModel = new AccommodationModel();
				accOrderEntryGroup.setAccommodation(accommodationModel);

				final RoomPreferenceModel roomPrefModel = new RoomPreferenceModel();
				roomPrefModel.setPreferenceType(RoomPreferenceType.BED_PREFERENCE);
				accOrderEntryGroup.setRoomPreferences(Stream.of(roomPrefModel).collect(Collectors.toList()));
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
			return Stream.of(accOrderEntryGroup).collect(Collectors.toList());

		}
	}
}
