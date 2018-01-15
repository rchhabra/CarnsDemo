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
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.ExtraServiceDetailsHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;


/**
 * Unit test class for {@link ExtraServiceDetailsHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExtraServiceDetailsHandlerTest
{
	@InjectMocks
	private ExtraServiceDetailsHandler handler;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private ProductModel productModel;

	@Mock
	private AccommodationReservationData accommodationReservationData;

	@Test
	public void testPopulate()
	{
		final ProductData product = new ProductData();
		given(productConverter.convert(productModel)).willReturn(product);
		final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerTypeQuantityData.setPassengerType(passengerType);
		reservedRoomStayData.setGuestCounts(Stream.of(passengerTypeQuantityData).collect(Collectors.toList()));
		final ServiceData serviceData = new ServiceData();
		handler.handle(productModel, reservedRoomStayData, serviceData, accommodationReservationData);
		Assert.assertNotNull(serviceData.getServiceDetails());

	}
}