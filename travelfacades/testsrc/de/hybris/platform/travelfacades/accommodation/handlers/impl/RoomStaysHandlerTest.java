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
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.RoomStaysHandler;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link RoomStaysHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RoomStaysHandlerTest
{
	@InjectMocks
	private RoomStaysHandler handler;

	@Mock
	private AccommodationService accommodationService;

	@Mock
	private AccommodationOfferingModel accommodationOfferingModel;

	@Mock
	private AccommodationModel accommodationModel;

	@Mock
	private AbstractPopulatingConverter<AccommodationModel, RoomTypeData> roomTypeConverter;

	@Test
	public void testPopulate()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PropertyData accommodationReference = testDataSetUp.createPropertData("to1");
		final StayDateRangeData stayDateRange = testDataSetUp.createStayDateRangeData("15/12/2016", "18/12/2016");
		final CriterionData criterionData = testDataSetUp.createCriterionData(accommodationReference, stayDateRange);
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		availabilityRequestData.setCriterion(criterionData);
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		given(accommodationService.getAccommodationForAccommodationOffering("to1"))
				.willReturn(Stream.of(accommodationModel).collect(Collectors.toList()));
		final RoomTypeData roomtype = new RoomTypeData();
		given(roomTypeConverter.convert(accommodationModel)).willReturn(roomtype);
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertNotNull(accommodationAvailabilityResponseData.getRoomStays());
	}

	private class TestDataSetUp
	{

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
	}

}
