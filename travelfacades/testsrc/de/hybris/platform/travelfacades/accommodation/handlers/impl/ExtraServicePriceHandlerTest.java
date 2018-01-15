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
import de.hybris.platform.commercefacades.accommodation.ServiceDetailData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.ExtraServicePriceHandler;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;


/**
 * Unit test class for {@link ExtraServicePriceHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExtraServicePriceHandlerTest
{
	@InjectMocks
	private ExtraServicePriceHandler handler;

	@Mock
	private ProductModel productModel;

	@Mock
	private AccommodationReservationData accommodationReservationData;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void testPopulate()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
		reservedRoomStayData.setCheckInDate(testData.createDate("19/12/2016"));
		reservedRoomStayData.setCheckOutDate(testData.createDate("20/12/2016"));
		final ServiceData serviceData = new ServiceData();
		serviceData.setQuantity(2);
		final ServiceDetailData serviceDetails = new ServiceDetailData();
		final ProductData product = new ProductData();
		final PriceData price = new PriceData();
		price.setValue(BigDecimal.valueOf(10d));
		price.setCurrencyIso("GBP");
		product.setPrice(price);
		serviceDetails.setProduct(product);
		serviceData.setServiceDetails(serviceDetails);
		final PriceData totalPriceData = new PriceData();
		totalPriceData.setValue(BigDecimal.valueOf(20d));
		totalPriceData.setCurrencyIso("GBP");
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20d), "GBP"))
				.willReturn(totalPriceData);
		handler.handle(productModel, reservedRoomStayData, serviceData, accommodationReservationData);
		assertEquals(BigDecimal.valueOf(10d), serviceData.getPrice().getBasePrice().getValue());
		assertEquals(BigDecimal.valueOf(20d), serviceData.getPrice().getTotal().getValue());
	}

	private class TestDataSetUp
	{
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
	}
}