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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.AccommodationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageRoomStaysHandlerTest
{
	@InjectMocks
	PackageRoomStaysHandler packageRoomStaysHandler;

	@Mock
	AccommodationService accommodationService;

	@Mock
	AbstractPopulatingConverter<AccommodationModel, RoomTypeData> roomTypeConverter;


	@Test
	public void testHandle()
	{
		final AccommodationAvailabilityRequestData availabilityRequestData = Mockito
				.mock(AccommodationAvailabilityRequestData.class, Mockito.RETURNS_DEEP_STUBS);
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();


		final StayDateRangeData stayDateRangeData = new StayDateRangeData();
		stayDateRangeData.setStartTime(new GregorianCalendar(2017, 06, 10).getTime());
		stayDateRangeData.setEndTime(new GregorianCalendar(2017, 06, 12).getTime());
		final List<AccommodationModel> accommodations = new ArrayList<>();
		final AccommodationModel accommodation = new AccommodationModel();
		accommodations.add(accommodation);
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		final RoomStayCandidateData roomStayCandidate = new RoomStayCandidateData();
		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode("adult");
		passengerTypeQuantityData.setPassengerType(passengerType);
		passengerTypeQuantityData.setQuantity(2);
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = Collections.singletonList(passengerTypeQuantityData);
		roomStayCandidate.setPassengerTypeQuantityList(passengerTypeQuantityList);
		roomStayCandidate.setRoomStayCandidateRefNumber(1);
		roomStayCandidates.add(roomStayCandidate);

		Mockito.when(availabilityRequestData.getCriterion().getAccommodationReference().getAccommodationOfferingCode())
				.thenReturn("accommodationOfferingCode");
		Mockito.when(availabilityRequestData.getCriterion().getStayDateRange()).thenReturn(stayDateRangeData);
		Mockito.when(accommodationService.getAccommodationForAccommodationOffering("accommodationOfferingCode"))
				.thenReturn(accommodations);
		Mockito.when(availabilityRequestData.getCriterion().getRoomStayCandidates()).thenReturn(roomStayCandidates);
		final RoomTypeData roomType = new RoomTypeData();
		final GuestOccupancyData guestOccupancyData = new GuestOccupancyData();
		guestOccupancyData.setPassengerType(passengerType);
		guestOccupancyData.setQuantityMax(10);
		final List<GuestOccupancyData> occupancies = Collections.singletonList(guestOccupancyData);
		roomType.setOccupancies(occupancies);
		final List<RoomTypeData> roomTypes = Collections.singletonList(roomType);
		Mockito.when(roomTypeConverter.convertAll(Mockito.anyListOf(AccommodationModel.class))).thenReturn(roomTypes);

		packageRoomStaysHandler.handle(availabilityRequestData, accommodationAvailabilityResponseData);

		Assert.assertTrue(CollectionUtils.isNotEmpty(accommodationAvailabilityResponseData.getRoomStays()));
	}
}
