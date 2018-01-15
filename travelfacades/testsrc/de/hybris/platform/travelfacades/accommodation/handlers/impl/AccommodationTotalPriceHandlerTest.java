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
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.accommodation.ServiceRateData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.AccommodationTotalPriceHandler;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link AccommodationTotalPriceHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationTotalPriceHandlerTest
{
	@InjectMocks
	private AccommodationTotalPriceHandler handler;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void testPopulateWithServices()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		final PriceData fromPrice = testData.createFromPrice(20d, "GBP");
		final ServiceRateData price = testData.createServicePrice();
		final ServiceData service = testData.createService(price);

		final RateData rate = testData.createRateData(20d, 30d, 35d, 5d);
		final RoomRateData roomRate = testData.createRoomRate(rate);
		final RatePlanData ratePlan = testData.createRatePlanData("ratePlan1", roomRate);
		final ReservedRoomStayData roomStay = testData.createRoomStays(fromPrice, service, ratePlan);
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));

		final PriceData totalPriceDataForRoomStay = new PriceData();
		totalPriceDataForRoomStay.setValue(BigDecimal.valueOf(40d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(40d), "GBP"))
				.willReturn(totalPriceDataForRoomStay);


		final PriceData basePriceData = new PriceData();
		basePriceData.setValue(BigDecimal.valueOf(20d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20d), "GBP"))
				.willReturn(basePriceData);
		final PriceData actualPriceData = new PriceData();
		actualPriceData.setValue(BigDecimal.valueOf(30d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(30d), "GBP"))
				.willReturn(actualPriceData);
		final PriceData wasPriceData = new PriceData();
		wasPriceData.setValue(BigDecimal.valueOf(35d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(35d), "GBP"))
				.willReturn(wasPriceData);
		final PriceData discountPriceData = new PriceData();
		discountPriceData.setValue(BigDecimal.valueOf(5d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(5d), "GBP"))
				.willReturn(discountPriceData);
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		final ReservedRoomStayData reservedRoomStay = (ReservedRoomStayData) accommodationAvailabilityResponseData.getRoomStays()
				.get(0);
		Assert.assertEquals(BigDecimal.valueOf(40d), reservedRoomStay.getTotalRate().getActualRate().getValue());
		Assert.assertEquals(BigDecimal.valueOf(20d), reservedRoomStay.getBaseRate().getBasePrice().getValue());
	}

	@Test
	public void testPopulateWithoutServices()
	{
		final TestDataSetUp testData = new TestDataSetUp();
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();

		final RateData rate = testData.createRateDataWithTaxes(20d, 30d, 35d, 5d, 5d);
		final RoomRateData roomRate = testData.createRoomRate(rate);
		final RatePlanData ratePlan = testData.createRatePlanData("ratePlan1", roomRate);
		final PriceData fromPrice = testData.createFromPrice(20d, "GBP");

		final ReservedRoomStayData roomStay = testData.createRoomStaysWithoutServies(fromPrice, ratePlan);
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));

		final PriceData totalPriceDataForRoomStay = new PriceData();
		totalPriceDataForRoomStay.setValue(BigDecimal.valueOf(20d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20d), "GBP"))
				.willReturn(totalPriceDataForRoomStay);

		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		final ReservedRoomStayData reservedRoomStay = (ReservedRoomStayData) accommodationAvailabilityResponseData.getRoomStays()
				.get(0);
		Assert.assertEquals(BigDecimal.valueOf(20d), reservedRoomStay.getTotalRate().getActualRate().getValue());
		Assert.assertEquals(BigDecimal.valueOf(20d), reservedRoomStay.getBaseRate().getBasePrice().getValue());
	}

	private class TestDataSetUp
	{
		public PriceData createFromPrice(final Double fromValue, final String iso)
		{
			final PriceData fromPrice = new PriceData();
			fromPrice.setValue(BigDecimal.valueOf(fromValue));
			fromPrice.setCurrencyIso(iso);
			return fromPrice;
		}

		public ServiceRateData createServicePrice()
		{
			final ServiceRateData price = new ServiceRateData();
			final PriceData total = new PriceData();
			total.setValue(BigDecimal.valueOf(20d));
			price.setTotal(total);
			return price;
		}

		public ServiceData createService(final ServiceRateData price)
		{
			final ServiceData service = new ServiceData();
			service.setPrice(price);
			return service;
		}

		public RateData createRateData(final Double basePrice, final Double actualPrice, final Double wasPrice,
				final Double discount)
		{
			final RateData rate = new RateData();
			final PriceData basePriceData = new PriceData();
			basePriceData.setValue(BigDecimal.valueOf(basePrice));
			rate.setBasePrice(basePriceData);
			final PriceData actualPriceData = new PriceData();
			actualPriceData.setValue(BigDecimal.valueOf(actualPrice));
			rate.setActualRate(actualPriceData);
			final PriceData wasPriceData = new PriceData();
			wasPriceData.setValue(BigDecimal.valueOf(wasPrice));
			rate.setWasRate(wasPriceData);
			final PriceData discountPriceData = new PriceData();
			discountPriceData.setValue(BigDecimal.valueOf(discount));
			rate.setTotalDiscount(discountPriceData);

			return rate;
		}

		public RateData createRateDataWithTaxes(final Double basePrice, final Double actualPrice, final Double wasPrice,
				final Double discount, final Double taxValue)
		{
			final RateData rate = new RateData();
			final PriceData basePriceData = new PriceData();
			basePriceData.setValue(BigDecimal.valueOf(basePrice));
			rate.setBasePrice(basePriceData);
			final PriceData actualPriceData = new PriceData();
			actualPriceData.setValue(BigDecimal.valueOf(actualPrice));
			rate.setActualRate(actualPriceData);
			final PriceData wasPriceData = new PriceData();
			wasPriceData.setValue(BigDecimal.valueOf(wasPrice));
			rate.setWasRate(wasPriceData);
			final PriceData discountPriceData = new PriceData();
			discountPriceData.setValue(BigDecimal.valueOf(discount));
			rate.setTotalDiscount(discountPriceData);

			final TaxData tax = new TaxData();
			final PriceData taxPriceData = new PriceData();
			taxPriceData.setValue(BigDecimal.valueOf(taxValue));

			return rate;
		}

		public RoomRateData createRoomRate(final RateData rate)
		{
			final RoomRateData roomRate = new RoomRateData();
			roomRate.setRate(rate);
			return roomRate;
		}

		public RatePlanData createRatePlanData(final String code, final RoomRateData roomRate)
		{
			final RatePlanData ratePlanData = new RatePlanData();
			ratePlanData.setRoomRates(Stream.of(roomRate).collect(Collectors.toList()));
			return ratePlanData;
		}

		public ReservedRoomStayData createRoomStays(final PriceData fromPrice, final ServiceData service,
				final RatePlanData ratePlan)
		{
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			roomStay.setFromPrice(fromPrice);
			roomStay.setServices(Stream.of(service).collect(Collectors.toList()));
			roomStay.setRatePlans(Stream.of(ratePlan).collect(Collectors.toList()));
			return roomStay;
		}

		public ReservedRoomStayData createRoomStaysWithoutServies(final PriceData fromPrice,
				final RatePlanData ratePlan)
		{
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			roomStay.setFromPrice(fromPrice);
			roomStay.setServices(Collections.emptyList());
			roomStay.setRatePlans(Stream.of(ratePlan).collect(Collectors.toList()));
			return roomStay;
		}

	}


}
