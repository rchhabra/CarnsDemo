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
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.accommodation.ServiceDetailData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.SelectedExtraServicePriceHandler;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link SelectedExtraServicePriceHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SelectedExtraServicePriceHandlerTest
{
	@InjectMocks
	private SelectedExtraServicePriceHandler handler;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;


	@Test
	public void testPopulateEmptyServices()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final RoomStayData roomStay = testData.createRoomStaysEmptyServices();
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertTrue(CollectionUtils
				.isEmpty(((ReservedRoomStayData) accommodationAvailabilityResponseData.getRoomStays().get(0)).getServices()));

	}

	@Test
	public void testPopulate()
	{
		final PriceData totalPriceData = new PriceData();
		totalPriceData.setValue(BigDecimal.valueOf(20d));
		totalPriceData.setCurrencyIso("GBP");
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20d), "GBP"))
				.willReturn(totalPriceData);
		final TestDataSetUp testData = new TestDataSetUp();
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final Date checkInDate = testData.createDate("19/12/2016");
		final Date checkOutDate = testData.createDate("20/12/2016");
		final ServiceDetailData serviceDetails = testData.createServiceDetails();
		final ServiceData serviceData = testData.createService(serviceDetails);
		final RoomStayData roomStay = testData.createRoomStays(checkInDate, checkOutDate, serviceData);
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertTrue(CollectionUtils
				.isNotEmpty(((ReservedRoomStayData) accommodationAvailabilityResponseData.getRoomStays().get(0)).getServices()));

	}

	private class TestDataSetUp
	{

		public ReservedRoomStayData createRoomStaysEmptyServices()
		{
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			roomStay.setServices(Collections.emptyList());
			return roomStay;
		}

		public ServiceDetailData createServiceDetails()
		{
			final ServiceDetailData serviceDetails = new ServiceDetailData();
			final ProductData product = new ProductData();
			final PriceData price = new PriceData();
			price.setValue(BigDecimal.valueOf(10d));
			price.setCurrencyIso("GBP");
			product.setPrice(price);
			serviceDetails.setProduct(product);
			return serviceDetails;
		}

		public ServiceData createService(final ServiceDetailData serviceDetails)
		{
			final ServiceData serviceData = new ServiceData();
			serviceData.setQuantity(2);
			serviceData.setServiceDetails(serviceDetails);
			return serviceData;
		}

		public Date createDate(final String date)
		{
			Date obj = null;
			try
			{
				final SimpleDateFormat format = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
				obj = format.parse(date);
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
			return obj;
		}

		public ReservedRoomStayData createRoomStays(final Date checkInDate, final Date checkOutDate, final ServiceData serviceData)
		{
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			roomStay.setCheckInDate(checkInDate);
			roomStay.setCheckOutDate(checkOutDate);
			roomStay.setServices(Stream.of(serviceData).collect(Collectors.toList()));
			return roomStay;
		}
	}
}
