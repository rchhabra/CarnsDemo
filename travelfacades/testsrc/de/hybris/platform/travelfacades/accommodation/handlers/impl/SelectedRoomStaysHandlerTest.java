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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.SelectedRoomStaysHandler;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.AccommodationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link SelectedRoomStaysHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SelectedRoomStaysHandlerTest
{
	@InjectMocks
	private SelectedRoomStaysHandler handler;
	@Mock
	private AccommodationService accommodationService;
	@Mock
	private AccommodationModel accommodationModel;
	@Mock
	private AbstractPopulatingConverter<AccommodationModel, RoomTypeData> roomTypeConverter;

	@Test
	public void testPopulate()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PropertyData accommodationReference = testDataSetUp.createPropertData("to1");
		final StayDateRangeData stayDateRange = testDataSetUp.createStayDateRangeData("19/12/2016", "21/12/2016");
		final RoomStayCandidateData roomStayCandidateData = testDataSetUp.createRoomStayCandidatesWithoutAccommodationCode();
		final CriterionData criterionData = testDataSetUp.createCriterionData(accommodationReference, stayDateRange,
				roomStayCandidateData);
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		availabilityRequestData.setCriterion(criterionData);
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		given(accommodationService.getAccommodationForAccommodationOffering(Matchers.anyString(), Matchers.anyString()))
				.willReturn(accommodationModel);
		final RoomTypeData roomtype = new RoomTypeData();
		given(roomTypeConverter.convertAll(Stream.of(accommodationModel).collect(Collectors.toList())))
				.willReturn(Stream.of(roomtype).collect(Collectors.toList()));
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertNotNull(accommodationAvailabilityResponseData.getRoomStays());
		Assert.assertEquals(2, accommodationAvailabilityResponseData.getRoomStays().size());
	}

	@Test
	public void testPopulateWithAccCode()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PropertyData accommodationReference = testDataSetUp.createPropertData("to1");
		final StayDateRangeData stayDateRange = testDataSetUp.createStayDateRangeData("19/12/2016", "21/12/2016");
		final RoomStayCandidateData roomStayCandidateData = testDataSetUp.createRoomStayCandidatesWithAccommodationCode();
		final CriterionData criterionData = testDataSetUp.createCriterionData(accommodationReference, stayDateRange,
				roomStayCandidateData);
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		availabilityRequestData.setCriterion(criterionData);
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		given(accommodationService.getAccommodationForAccommodationOffering(Matchers.anyString(), Matchers.anyString()))
				.willReturn(accommodationModel);
		final RoomTypeData roomtype = new RoomTypeData();
		given(roomTypeConverter.convertAll(Stream.of(accommodationModel).collect(Collectors.toList())))
				.willReturn(Stream.of(roomtype).collect(Collectors.toList()));
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertNotNull(accommodationAvailabilityResponseData.getRoomStays());
		Assert.assertEquals(1, accommodationAvailabilityResponseData.getRoomStays().size());
	}

	private class TestDataSetUp
	{

		public RoomStayCandidateData createRoomStayCandidatesWithAccommodationCode()
		{
			final RoomStayCandidateData roomStayCandidate = new RoomStayCandidateData();
			roomStayCandidate.setRoomStayCandidateRefNumber(0);
			roomStayCandidate.setRatePlanCode("ratePlan2");
			roomStayCandidate.setAccommodationCode("acc1");
			return roomStayCandidate;
		}

		public RoomStayCandidateData createRoomStayCandidatesWithoutAccommodationCode()
		{
			final RoomStayCandidateData roomStayCandidate = new RoomStayCandidateData();
			roomStayCandidate.setRoomStayCandidateRefNumber(0);
			roomStayCandidate.setRatePlanCode("ratePlan1");
			return roomStayCandidate;
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

		public CriterionData createCriterionData(final PropertyData accommodationReference, final StayDateRangeData stayDateRange,
				final RoomStayCandidateData roomStayCandidates)
		{
			final CriterionData criterion = new CriterionData();
			criterion.setAccommodationReference(accommodationReference);
			criterion.setStayDateRange(stayDateRange);
			criterion.setRoomStayCandidates(Stream.of(roomStayCandidates).collect(Collectors.toList()));
			return criterion;
		}
	}
}
