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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.*;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.ExtraServiceBasicHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;


/**
 * Unit test class for {@link ExtraServiceBasicHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExtraServiceBasicHandlerTest
{
	@InjectMocks
	private ExtraServiceBasicHandler handler;

	@Mock
	private ProductModel productModel;

	@Test
	public void testPopulateWithSameCodeForBothServiceAndReservedRoomStay() throws ParseException
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(productModel.getCode()).willReturn("product1");
		final ServiceDetailData serviceDetails = testData.createServiceDetails();
		final ServiceData serviceData = testData.createService(serviceDetails);
		final RatePlanData ratePlan = testData.createRatePlanData("ratePlan1");
		final ReservedRoomStayData reservedRoomStayData = testData.createRoomStays(serviceData, ratePlan);
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		handler.handle(productModel, reservedRoomStayData, serviceData, accommodationReservationData);
		assertEquals(Integer.valueOf(2), serviceData.getQuantity());
	}

	@Test
	public void testPopulateWithdifferentCodeForBothServiceAndReservedRoomStay() throws ParseException
	{
		final TestDataSetUp testData = new TestDataSetUp();
		given(productModel.getCode()).willReturn("product1");
		final ServiceDetailData serviceDetails = testData.createServiceDetails();
		final ServiceData sameServiceData = testData.createService(serviceDetails);
		final ServiceData defferentServiceData = testData.createServiceWithDifferentCode(serviceDetails);
		final RatePlanData ratePlan = testData.createRatePlanData("ratePlan1");
		final ReservedRoomStayData reservedRoomStayData = testData.createRoomStays(sameServiceData, ratePlan);
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		handler.handle(productModel, reservedRoomStayData, defferentServiceData, accommodationReservationData);
		assertEquals(Integer.valueOf(0), defferentServiceData.getQuantity());
	}

	private class TestDataSetUp
	{

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
			serviceData.setCode("service1");
			serviceData.setQuantity(2);
			serviceData.setServiceDetails(serviceDetails);
			return serviceData;
		}

		public ServiceData createServiceWithDifferentCode(final ServiceDetailData serviceDetails)
		{
			final ServiceData serviceData = new ServiceData();
			serviceData.setCode("service2");
			serviceData.setQuantity(2);
			serviceData.setServiceDetails(serviceDetails);
			return serviceData;
		}

		public RatePlanData createRatePlanData(final String code)
		{
			final RatePlanData ratePlanData = new RatePlanData();
			ratePlanData.setCode(code);
			return ratePlanData;
		}

		public ReservedRoomStayData createRoomStays(final ServiceData serviceData, final RatePlanData ratePlan)
		{
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			roomStay.setServices(Stream.of(serviceData).collect(Collectors.toList()));
			roomStay.setRatePlans(Stream.of(ratePlan).collect(Collectors.toList()));
			return roomStay;
		}
	}

}