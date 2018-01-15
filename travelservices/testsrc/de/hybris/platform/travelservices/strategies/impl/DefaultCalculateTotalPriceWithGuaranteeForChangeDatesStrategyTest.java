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

package de.hybris.platform.travelservices.strategies.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.RatePlanService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategyTest
{
	@InjectMocks
	DefaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy defaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy;

	@Mock
	BookingService bookingService;

	@Mock
	RatePlanService ratePlanService;

	@Mock
	TimeService timeService;

	@Test
	public void testCalculateForEmptyArguments()
	{
		Assert.assertNull(defaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy.calculate(null, StringUtils.EMPTY));
		Assert.assertNull(defaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy
				.calculate(new AccommodationAvailabilityResponseData(), StringUtils.EMPTY));
		Assert.assertNull(defaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy
				.calculate(new AccommodationAvailabilityResponseData(), "TEST_ORDER_CODE"));
	}

	@Test
	public void testCalculateForInvalidOrderCode()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(Arrays.asList(new RoomStayData()));
		when(bookingService.getOrder(Matchers.anyString())).thenReturn(null);
		Assert.assertNull(defaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy
				.calculate(accommodationAvailabilityResponseData, "TEST_ORDER_CODE"));
	}

	@Test
	public void testCalculateForEmptyAccommodaitonOrderEntryGroupsInOrder()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(Arrays.asList(new RoomStayData()));
		final OrderModel order = new OrderModel();
		when(bookingService.getOrder(Matchers.anyString())).thenReturn(order);
		when(bookingService.getAccommodationOrderEntryGroups(order)).thenReturn(Collections.emptyList());
		Assert.assertNull(defaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy
				.calculate(accommodationAvailabilityResponseData, "TEST_ORDER_CODE"));
	}

	@Test
	public void testCalculateForEmptyReservedRoomStayData()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final RoomStayData roomStayData = new RoomStayData();
		roomStayData.setRoomStayRefNumber(0);
		accommodationAvailabilityResponseData.setRoomStays(Arrays.asList(roomStayData));
		final OrderModel order = new OrderModel();
		when(bookingService.getOrder(Matchers.anyString())).thenReturn(order);

		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroup.setRoomStayRefNumber(0);
		when(bookingService.getAccommodationOrderEntryGroups(order)).thenReturn(Arrays.asList(accommodationOrderEntryGroup));
		when(timeService.getCurrentTime()).thenReturn(new Date());
		final GuaranteeModel guaranteeToApply = new GuaranteeModel();
		when(ratePlanService.getGuaranteeToApply(Matchers.any(AccommodationOrderEntryGroupModel.class), Matchers.any(Date.class),
				Matchers.any(Date.class))).thenReturn(guaranteeToApply);

		Assert.assertNull(defaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy
				.calculate(accommodationAvailabilityResponseData, "TEST_ORDER_CODE"));
	}

	@Test
	public void testCalculate()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final ReservedRoomStayData roomStayData = new ReservedRoomStayData();
		roomStayData.setRoomStayRefNumber(0);
		final RateData rateData = new RateData();
		final PriceData priceData = new PriceData();
		final Date date = new Date();
		priceData.setValue(BigDecimal.valueOf(200d));
		rateData.setActualRate(priceData);
		roomStayData.setTotalRate(rateData);
		roomStayData.setCheckInDate(date);
		accommodationAvailabilityResponseData.setRoomStays(Arrays.asList(roomStayData));
		final OrderModel order = new OrderModel();
		when(bookingService.getOrder(Matchers.anyString())).thenReturn(order);

		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroup.setRoomStayRefNumber(0);
		final AbstractOrderEntryModel productModel = new AbstractOrderEntryModel();
		productModel.setProduct(new ProductModel());
		productModel.setTotalPrice(10d);

		final AbstractOrderEntryModel productModel1 = new AbstractOrderEntryModel();
		productModel1.setProduct(new RoomRateProductModel());
		productModel1.setTotalPrice(10d);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(productModel, productModel1));


		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup1 = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroup1.setRoomStayRefNumber(1);
		final AbstractOrderEntryModel productModel2 = new AbstractOrderEntryModel();
		productModel2.setProduct(new RoomRateProductModel());
		productModel2.setTotalPrice(10d);
		accommodationOrderEntryGroup1.setEntries(Arrays.asList(productModel2));

		when(bookingService.getAccommodationOrderEntryGroups(order))
				.thenReturn(Arrays.asList(accommodationOrderEntryGroup, accommodationOrderEntryGroup1));

		when(timeService.getCurrentTime()).thenReturn(date);
		final GuaranteeModel guaranteeToApply = new GuaranteeModel();
		when(ratePlanService.getGuaranteeToApply(accommodationOrderEntryGroup, date, date)).thenReturn(guaranteeToApply);
		when(ratePlanService.getGuaranteeToApply(accommodationOrderEntryGroup1, date, date)).thenReturn(null);

		when(ratePlanService.getAppliedGuaranteeAmount(Matchers.any(GuaranteeModel.class), Matchers.any(BigDecimal.class)))
				.thenReturn(38d);
		Assert.assertEquals(38, defaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy
				.calculate(accommodationAvailabilityResponseData, "TEST_ORDER_CODE").intValue());
	}

}
