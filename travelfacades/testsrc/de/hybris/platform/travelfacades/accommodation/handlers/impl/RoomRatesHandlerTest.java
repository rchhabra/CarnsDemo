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
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.RoomRatesHandler;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link RoomRatesHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RoomRatesHandlerTest
{
	@InjectMocks
	private RoomRatesHandler handler;

	@Mock
	private RatePlanModel ratePlanModel;
	@Mock
	private CategoryService categoryService;
	@Mock
	private RoomRateProductModel productModel;
	@Mock
	private DateRangeModel dateRangeModel;
	@Mock
	private EnumerationService enumerationService;

	@Test
	public void testPopulate() throws ParseException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PropertyData accommodationReference = testDataSetUp.createPropertData("to1");
		final StayDateRangeData stayDateRange = testDataSetUp.createStayDateRangeData("15/12/2016", "18/12/2016");
		final CriterionData criterionData = testDataSetUp.createCriterionData(accommodationReference, stayDateRange);
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		availabilityRequestData.setCriterion(criterionData);
		final RatePlanData ratePlanData = testDataSetUp.createRatePlanData("ratePlan1");
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final RoomStayData roomStay = testDataSetUp.createRoomStays(ratePlanData);
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		given(categoryService.getCategoryForCode("ratePlan1")).willReturn(ratePlanModel);
		given(ratePlanModel.getProducts()).willReturn(Stream.of(productModel).collect(Collectors.toList()));
		given(productModel.getDateRanges()).willReturn(Stream.of(dateRangeModel).collect(Collectors.toList()));
		given(productModel.getDaysOfWeek()).willReturn(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
		given(dateRangeModel.getStartingDate()).willReturn(testDataSetUp.createDate("15/12/2016"));
		given(dateRangeModel.getEndingDate()).willReturn(testDataSetUp.createDate("18/12/2016"));
		final LocalDate localDate1 = Instant.ofEpochMilli(testDataSetUp.createDate("15/12/2016").getTime())
				.atZone(ZoneId.systemDefault()).toLocalDate();
		given(enumerationService.getEnumerationValue(DayOfWeek.class, localDate1.getDayOfWeek().toString()))
				.willReturn(DayOfWeek.THURSDAY);

		final LocalDate localDate2 = Instant.ofEpochMilli(testDataSetUp.createDate("18/12/2016").getTime())
				.atZone(ZoneId.systemDefault()).toLocalDate();
		given(enumerationService.getEnumerationValue(DayOfWeek.class, localDate2.getDayOfWeek().toString()))
				.willReturn(DayOfWeek.SUNDAY);

		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertNotNull(accommodationAvailabilityResponseData.getRoomStays().get(0).getRatePlans().get(0).getRoomRates());
	}

	private class TestDataSetUp
	{

		public RatePlanData createRatePlanData(final String code)
		{
			final RatePlanData ratePlanData = new RatePlanData();
			ratePlanData.setCode(code);
			return ratePlanData;
		}

		public RoomStayData createRoomStays(final RatePlanData ratePlan)
		{
			final RoomStayData roomStay = new RoomStayData();
			roomStay.setRatePlans(Stream.of(ratePlan).collect(Collectors.toList()));
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

		public Date createDate(final String date) throws ParseException
		{
			final SimpleDateFormat format = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			final Date obj = format.parse(date);
			return obj;
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
