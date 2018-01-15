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

package de.hybris.platform.travelfacades.facades.accommodation.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.*;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;
import de.hybris.platform.travelservices.model.travel.AccommodationRestrictionModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.BDDMockito.given;



/**
 * unit test for {@link AddFreeQuantityExtrasStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddFreeQuantityExtrasStrategyTest
{
	@InjectMocks
	AddFreeQuantityExtrasStrategy addFreeQuantityExtrasStrategy;

	@Mock
	private AccommodationOfferingService accommodationOfferingService;
	@Mock
	private TravelCommerceStockService commerceStockService;

	@Mock
	private ProductModel productModel;

	@Mock
	private AccommodationRestrictionModel accommodationRestrictionModel;

	private final String TEST_ACCCOMMODATION_OFFERING_CODE = "TEST_ACCCOMMODATION_OFFERING_CODE";
	private final String TEST_PRODUCT_CODE = "TEST_PRODUCT_CODE";

	@Mock
	private AccommodationOfferingModel accommodationOfferingModel;

	Date date = new Date();

	@Before
	public void setUp()
	{
		given(productModel.getCode()).willReturn(TEST_PRODUCT_CODE);
		given(accommodationOfferingService.getAccommodationOffering(TEST_ACCCOMMODATION_OFFERING_CODE))
				.willReturn(accommodationOfferingModel);
	}

	@Test(expected = AccommodationPipelineException.class)
	public void testApplyStrategyForRoomStayDataOfSameDate()
	{
		given(commerceStockService.getStockForDate(productModel, date, Arrays.asList(accommodationOfferingModel)))
				.willReturn(new Integer(0));
		final ReservedRoomStayData reservedRoomStayData = createReservedRoomStayData(1, 1);
		final RoomTypeData roomTypeData = new RoomTypeData();
		roomTypeData.setCode("TEST_ROOM_TYPE_CODE");
		reservedRoomStayData.setRoomTypes(Arrays.asList(roomTypeData));
		final AccommodationReservationData accommodationReservationData = createAccommodationReservationData();
		addFreeQuantityExtrasStrategy.applyStrategy(productModel, reservedRoomStayData, accommodationReservationData);
	}

	@Test
	public void testApplyStrategyForNullRestriction()
	{
		given(productModel.getTravelRestriction()).willReturn(null);
		given(commerceStockService.getStockForDate(productModel, date, Arrays.asList(accommodationOfferingModel)))
				.willReturn(new Integer(1));
		final ReservedRoomStayData reservedRoomStayData = createReservedRoomStayData(1, 1);

		final AccommodationReservationData accommodationReservationData = createAccommodationReservationData();
		Assert.assertEquals(null, addFreeQuantityExtrasStrategy
				.applyStrategy(productModel, reservedRoomStayData, accommodationReservationData).getAddToCartCriteria());
	}

	@Test
	public void testApplyStrategyForNullAddToCriteria()
	{
		given(productModel.getTravelRestriction()).willReturn(accommodationRestrictionModel);
		given(accommodationRestrictionModel.getAddToCartCriteria()).willReturn(null);
		given(commerceStockService.getStockForDate(productModel, date, Arrays.asList(accommodationOfferingModel)))
				.willReturn(new Integer(1));
		final ReservedRoomStayData reservedRoomStayData = createReservedRoomStayData(1, 1);

		final AccommodationReservationData accommodationReservationData = createAccommodationReservationData();
		Assert.assertEquals(null, addFreeQuantityExtrasStrategy
				.applyStrategy(productModel, reservedRoomStayData, accommodationReservationData).getAddToCartCriteria());
	}

	@Test
	public void testApplyStrategy()
	{
		given(productModel.getTravelRestriction()).willReturn(accommodationRestrictionModel);
		given(accommodationRestrictionModel.getAddToCartCriteria()).willReturn(AddToCartCriteriaType.PER_LEG);
		given(commerceStockService.getStockForDate(productModel, date, Arrays.asList(accommodationOfferingModel)))
				.willReturn(new Integer(1));
		final ReservedRoomStayData reservedRoomStayData = createReservedRoomStayData(1, 1);

		final AccommodationReservationData accommodationReservationData = createAccommodationReservationData();
		Assert.assertEquals(AddToCartCriteriaType.PER_LEG.getCode(), addFreeQuantityExtrasStrategy
				.applyStrategy(productModel, reservedRoomStayData, accommodationReservationData).getAddToCartCriteria());
	}

	private PropertyData createPropertyData()
	{
		final PropertyData propertyData = new PropertyData();
		propertyData.setAccommodationOfferingCode(TEST_ACCCOMMODATION_OFFERING_CODE);

		return propertyData;
	}

	private ReservedRoomStayData createReservedRoomStayData(final int roomStayRefNumber, final int differencInCheckInAndCheckOut)
	{
		final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
		reservedRoomStayData.setRoomStayRefNumber(roomStayRefNumber);
		reservedRoomStayData.setCheckOutDate(
				Date.from(LocalDateTime.now().plusDays(differencInCheckInAndCheckOut).atZone(ZoneId.systemDefault()).toInstant()));
		reservedRoomStayData.setCheckInDate(date);
		final List<ServiceData> services = new ArrayList<>();
		final ServiceData service1 = new ServiceData();
		final ServiceData service2 = new ServiceData();
		service1.setCode(TEST_PRODUCT_CODE);
		service1.setQuantity(0);
		service2.setCode(StringUtils.EMPTY);
		service2.setQuantity(1);
		services.add(service1);
		services.add(service2);
		reservedRoomStayData.setServices(services);
		return reservedRoomStayData;
	}

	private AccommodationReservationData createAccommodationReservationData()
	{
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		final List<ReservedRoomStayData> roomStays = new ArrayList<>();
		roomStays.add(createReservedRoomStayData(0, 1));
		roomStays.add(createReservedRoomStayData(1, 1));
		accommodationReservationData.setAccommodationReference(createPropertyData());
		accommodationReservationData.setRoomStays(roomStays);

		return accommodationReservationData;
	}
}
