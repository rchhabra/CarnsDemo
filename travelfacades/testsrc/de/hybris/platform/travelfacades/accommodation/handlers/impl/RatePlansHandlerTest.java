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

package de.hybris.platform.travelfacades.accommodation.handlers.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.RatePlansHandler;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.CancelPenaltiesDescriptionCreationStrategy;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.services.AccommodationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link RatePlansHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RatePlansHandlerTest
{
	@InjectMocks
	private RatePlansHandler handler;

	@Mock
	private AccommodationService accommodationService;

	@Mock
	private AccommodationModel accommodationModel;

	@Mock
	private RatePlanModel ratePlanModel;

	@Mock
	private Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter;

	@Mock
	private CancelPenaltiesDescriptionCreationStrategy cancelPenaltiesDescriptionCreationStrategy;

	@Mock
	private Converter<RatePlanModel, RatePlanData> ratePlanConverter;

	@Before
	public void setUp()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final List<GuestOccupancyModel> guestOccupancies = new ArrayList<>();
		guestOccupancies.add(testDataSetUp.createGuestOccupancy("adult", 2, 1));
		guestOccupancies.add(testDataSetUp.createGuestOccupancy("child", 2, 0));
		guestOccupancies.add(testDataSetUp.createGuestOccupancy("infant", 2, 0));
		accommodationModel.setGuestOccupancies(guestOccupancies);
	}

	@Test
	public void testPopulate()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PropertyData accommodationReference = testDataSetUp.createPropertData("to1");
		final StayDateRangeData stayDateRange = testDataSetUp.createStayDateRangeData("15/12/2016", "18/12/2016");
		final CriterionData criterionData = testDataSetUp.createCriterionData(accommodationReference, stayDateRange);
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		availabilityRequestData.setCriterion(criterionData);
		final RoomTypeData roomtype = testDataSetUp.createRoomTypes();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final RoomStayData roomStay = testDataSetUp.createRoomStays(roomtype);
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		given(accommodationService.getAccommodationForAccommodationOffering(Matchers.anyString(), Matchers.anyString()))
				.willReturn(accommodationModel);
		final RatePlanData ratePlan = new RatePlanData();
		roomStay.setRatePlans(Arrays.asList(ratePlan));
		given(guestOccupancyConverter.convertAll(accommodationModel.getGuestOccupancies()))
				.willReturn(testDataSetUp.createGuestOccupancies());
		given(accommodationModel.getRatePlan()).willReturn(Stream.of(ratePlanModel).collect(Collectors.toList()));
		given(ratePlanConverter.convertAll(accommodationModel.getRatePlan()))
				.willReturn(Stream.of(ratePlan).collect(Collectors.toList()));
		doNothing().when(cancelPenaltiesDescriptionCreationStrategy).updateCancelPenaltiesDescription(ratePlanModel, roomStay);
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertNotNull(accommodationAvailabilityResponseData.getRoomStays().get(0).getRatePlans());
		Assert.assertNotNull(accommodationAvailabilityResponseData.getRoomStays().get(0).getRatePlans().get(0).getOccupancies());
	}

	private class TestDataSetUp
	{

		public RoomTypeData createRoomTypes()
		{
			final RoomTypeData roomtypeData = new RoomTypeData();
			roomtypeData.setBedType("Single Bed");
			return roomtypeData;
		}

		public RoomStayData createRoomStays(final RoomTypeData roomType)
		{
			final RoomStayData roomStay = new RoomStayData();
			roomStay.setRoomTypes(Stream.of(roomType).collect(Collectors.toList()));
			return roomStay;
		}

		public PropertyData createPropertData(final String accommodationOfferingCode)
		{
			final PropertyData property = new PropertyData();
			property.setAccommodationOfferingCode(accommodationOfferingCode);
			return property;
		}

		public StayDateRangeData createStayDateRangeData(final String startTime, final String endTime)
		{
			final StayDateRangeData stayDateRange = new StayDateRangeData();
			final SimpleDateFormat format = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			try
			{
				stayDateRange.setStartTime(format.parse(startTime));
				stayDateRange.setEndTime(format.parse(endTime));
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
			return stayDateRange;
		}

		public CriterionData createCriterionData(final PropertyData accommodationReference, final StayDateRangeData stayDateRange)
		{
			final CriterionData criterion = new CriterionData();
			criterion.setAccommodationReference(accommodationReference);
			criterion.setStayDateRange(stayDateRange);
			return criterion;
		}

		public GuestOccupancyModel createGuestOccupancy(final String passengerType, final int maxQty, final int minQty)
		{
			final GuestOccupancyModel guestOccupancy = new GuestOccupancyModel();
			final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
			passengerTypeModel.setCode(passengerType);
			guestOccupancy.setPassengerType(passengerTypeModel);
			guestOccupancy.setQuantityMax(maxQty);
			guestOccupancy.setQuantityMin(minQty);
			return guestOccupancy;
		}

		public List<GuestOccupancyData> createGuestOccupancies()
		{
			final GuestOccupancyData guestOccupancy1 = new GuestOccupancyData();
			final PassengerTypeData passengerTypeData1 = new PassengerTypeData();
			passengerTypeData1.setCode("adult");
			guestOccupancy1.setPassengerType(passengerTypeData1);
			guestOccupancy1.setQuantityMax(2);
			guestOccupancy1.setQuantityMin(1);
			final GuestOccupancyData guestOccupancy2 = new GuestOccupancyData();
			final PassengerTypeData passengerTypeData2 = new PassengerTypeData();
			passengerTypeData2.setCode("child");
			guestOccupancy2.setPassengerType(passengerTypeData2);
			guestOccupancy2.setQuantityMax(2);
			guestOccupancy2.setQuantityMin(0);
			final GuestOccupancyData guestOccupancy3 = new GuestOccupancyData();
			final PassengerTypeData passengerTypeData3 = new PassengerTypeData();
			passengerTypeData3.setCode("infant");
			guestOccupancy3.setPassengerType(passengerTypeData3);
			guestOccupancy3.setQuantityMax(2);
			guestOccupancy3.setQuantityMin(0);
			return Arrays.asList(guestOccupancy1, guestOccupancy2, guestOccupancy3);
		}

	}
}
