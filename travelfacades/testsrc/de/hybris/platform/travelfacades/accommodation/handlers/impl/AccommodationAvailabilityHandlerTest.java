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
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.AccommodationAvailabilityHandler;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
 * Unit test class for {@link AccommodationAvailabilityHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationAvailabilityHandlerTest
{
	@InjectMocks
	private AccommodationAvailabilityHandler handler;

	@Mock
	private AccommodationOfferingService accommodationOfferingService;

	@Mock
	private AccommodationOfferingModel accommodationOfferingModel;

	@Mock
	private ProductService productService;

	@Mock
	private AccommodationModel accommodationModel;

	@Mock
	private RatePlanModel ratePlanModel;

	@Mock
	private RoomRateProductModel productModel;

	@Mock
	private DateRangeModel dateRangeModel;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private TravelCommerceStockService commerceStockService;

	@Test
	public void testPopulate() throws ParseException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PropertyData accommodationReference = testDataSetUp.createPropertData("to1");
		final StayDateRangeData stayDateRange = testDataSetUp.createStayDateRangeData("15/12/2016", "18/12/2016");
		final CriterionData criterionData = testDataSetUp.createCriterionData(accommodationReference, stayDateRange);
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		availabilityRequestData.setCriterion(criterionData);
		given(accommodationOfferingService.getAccommodationOffering("to1")).willReturn(accommodationOfferingModel);
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final RoomTypeData roomtype = testDataSetUp.createRoomTypes("roomType1");
		final RatePlanData ratePlan = testDataSetUp.createRatePlanData("rateplan1");
		final RoomStayData roomStay = testDataSetUp.createRoomStays(roomtype, ratePlan);
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		given(productService.getProductForCode("roomType1")).willReturn(accommodationModel);
		given(accommodationModel.getRatePlan()).willReturn(Stream.of(ratePlanModel).collect(Collectors.toList()));
		given(ratePlanModel.getProducts()).willReturn(Stream.of(productModel).collect(Collectors.toList()));
		given(productModel.getDateRanges()).willReturn(Stream.of(dateRangeModel).collect(Collectors.toList()));
		given(dateRangeModel.getStartingDate()).willReturn(testDataSetUp.createDate("15/12/2016"));
		given(dateRangeModel.getEndingDate()).willReturn(testDataSetUp.createDate("18/12/2016"));
		final LocalDate localDate1 = Instant.ofEpochMilli(testDataSetUp.createDate("15/12/2016").getTime())
				.atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate localDate2 = Instant.ofEpochMilli(testDataSetUp.createDate("16/12/2016").getTime())
				.atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate localDate3 = Instant.ofEpochMilli(testDataSetUp.createDate("17/12/2016").getTime())
				.atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate localDate4 = Instant.ofEpochMilli(testDataSetUp.createDate("18/12/2016").getTime())
				.atZone(ZoneId.systemDefault()).toLocalDate();
		given(enumerationService.getEnumerationValue(DayOfWeek.class, localDate1.getDayOfWeek().toString()))
				.willReturn(DayOfWeek.THURSDAY);
		given(enumerationService.getEnumerationValue(DayOfWeek.class, localDate2.getDayOfWeek().toString()))
				.willReturn(DayOfWeek.FRIDAY);
		given(enumerationService.getEnumerationValue(DayOfWeek.class, localDate3.getDayOfWeek().toString()))
				.willReturn(DayOfWeek.SATURDAY);
		given(enumerationService.getEnumerationValue(DayOfWeek.class, localDate4.getDayOfWeek().toString()))
				.willReturn(DayOfWeek.SUNDAY);

		given(productModel.getDaysOfWeek()).willReturn(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));

		given(commerceStockService.getStockForDate(Matchers.any(AccommodationModel.class), Matchers.any(Date.class),
				Collections.singletonList(Matchers.any(AccommodationOfferingModel.class)))).willReturn(10);

		given(commerceStockService.getStockLevelQuantity(productModel,
				Collections.singletonList(accommodationOfferingModel))).willReturn(10L);
		given(ratePlanModel.getCode()).willReturn("rateplan1");
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertTrue(
				accommodationAvailabilityResponseData.getRoomStays().get(0).getRatePlans().get(0).getAvailableQuantity() > 0);
	}

	private class TestDataSetUp
	{

		public RoomTypeData createRoomTypes(final String code)
		{
			final RoomTypeData roomtypeData = new RoomTypeData();
			roomtypeData.setCode(code);
			roomtypeData.setBedType("Single Bed");
			return roomtypeData;
		}

		public RatePlanData createRatePlanData(final String code)
		{
			final RatePlanData ratePlanData = new RatePlanData();
			ratePlanData.setCode(code);
			return ratePlanData;
		}

		public RoomStayData createRoomStays(final RoomTypeData roomType, final RatePlanData ratePlan)
		{
			final RoomStayData roomStay = new RoomStayData();
			roomStay.setRoomTypes(Stream.of(roomType).collect(Collectors.toList()));
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
