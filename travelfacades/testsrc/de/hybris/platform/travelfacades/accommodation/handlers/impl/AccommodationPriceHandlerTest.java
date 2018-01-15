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
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.europe1.model.TaxRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.AccommodationPriceHandler;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.AccommodationDiscountEvaluationStrategy;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.Map;
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
 * Unit test class for {@link AccommodationPriceHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationPriceHandlerTest
{

	@InjectMocks
	private AccommodationPriceHandler handler;

	@Mock
	private RoomRateProductModel roomRateProduct;

	@Mock
	private ProductService productService;

	@Mock
	private PriceService priceService;

	@Mock
	private ProductPromotionModel promotion;

	@Mock
	private TaxRowModel tax;

	@Mock
	private CurrencyModel currency;

	@Mock
	private Map<String, AccommodationDiscountEvaluationStrategy> accommodationDiscountEvaluationStrategyMap;

	@Mock
	private AccommodationDiscountEvaluationStrategy strategy;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private CommonI18NService commonI18NService;

	@Test
	public void testPopulate()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		availabilityRequestData.setCriterion(testDataSetUp.createCriterionDataWithCurrencyIso("GBP"));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final RoomRateData roomRate = testDataSetUp.createRoomRates("roomRate1");
		final RatePlanData ratePlan = testDataSetUp.createRatePlanData("ratePlan1", roomRate);
		final RoomStayData roomStay = testDataSetUp.createRoomStays(ratePlan);
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));

		given(productService.getProductForCode("roomRate1")).willReturn(roomRateProduct);
		final PriceValue price = new PriceValue("GBP", 5d, false);
		final PriceInformation priceInfo = new PriceInformation(price);
		given(priceService.getPriceInformationsForProduct(roomRateProduct))
				.willReturn(Stream.of(priceInfo).collect(Collectors.toList()));
		given(roomRateProduct.getPromotions()).willReturn(Stream.of(promotion).collect(Collectors.toList()));
		given(roomRateProduct.getEurope1Taxes()).willReturn(Stream.of(tax).collect(Collectors.toList()));
		given(promotion.getEnabled()).willReturn(true);
		given(tax.getCurrency()).willReturn(currency);
		given(currency.getIsocode()).willReturn("GBP");
		given(tax.getValue()).willReturn(5d);
		final PriceData taxPriceData = new PriceData();
		taxPriceData.setValue(BigDecimal.valueOf(5d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(5d), "GBP")).willReturn(taxPriceData);
		final PriceData actualPriceData = new PriceData();
		actualPriceData.setValue(BigDecimal.valueOf(8d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(8d), "GBP")).willReturn(actualPriceData);
		final PriceData wasPriceData = new PriceData();
		wasPriceData.setValue(BigDecimal.valueOf(10d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(10d), "GBP")).willReturn(wasPriceData);
		final PriceData discountPriceData = new PriceData();
		discountPriceData.setValue(BigDecimal.valueOf(2d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(2d), "GBP")).willReturn(discountPriceData);
		given(accommodationDiscountEvaluationStrategyMap.get(Matchers.anyString())).willReturn(strategy);
		given(strategy.evaluateDiscount(Matchers.anyDouble(), Matchers.any(ProductPromotionModel.class), Matchers.anyString()))
				.willReturn(2d);
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertNotNull(accommodationAvailabilityResponseData.getRoomStays().get(0).getRatePlans());
		Assert.assertEquals(BigDecimal.valueOf(8d),
				accommodationAvailabilityResponseData.getRoomStays().get(0).getRatePlans().get(0).getActualRate().getValue());
	}


	@Test
	public void testPopulateWithIsoCode()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		availabilityRequestData.setCriterion(testDataSetUp.createCriterionDataWithoutCurrencyIso());
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		final RoomRateData roomRate = testDataSetUp.createRoomRates("roomRate1");
		final RatePlanData ratePlan = testDataSetUp.createRatePlanData("ratePlan1", roomRate);
		final RoomStayData roomStay = testDataSetUp.createRoomStays(ratePlan);
		accommodationAvailabilityResponseData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));

		given(commonI18NService.getCurrentCurrency()).willReturn(currency);
		given(currency.getIsocode()).willReturn("GBP");
		given(productService.getProductForCode("roomRate1")).willReturn(roomRateProduct);
		final PriceValue price = new PriceValue("GBP", 5d, false);
		final PriceInformation priceInfo = new PriceInformation(price);
		given(priceService.getPriceInformationsForProduct(roomRateProduct))
				.willReturn(Stream.of(priceInfo).collect(Collectors.toList()));
		given(roomRateProduct.getPromotions()).willReturn(Stream.of(promotion).collect(Collectors.toList()));
		given(roomRateProduct.getEurope1Taxes()).willReturn(Stream.of(tax).collect(Collectors.toList()));
		given(promotion.getEnabled()).willReturn(true);
		given(tax.getCurrency()).willReturn(currency);
		given(tax.getValue()).willReturn(5d);
		final PriceData taxPriceData = new PriceData();
		taxPriceData.setValue(BigDecimal.valueOf(5d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(5d), "GBP")).willReturn(taxPriceData);
		final PriceData actualPriceData = new PriceData();
		actualPriceData.setValue(BigDecimal.valueOf(8d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(8d), "GBP")).willReturn(actualPriceData);
		final PriceData wasPriceData = new PriceData();
		wasPriceData.setValue(BigDecimal.valueOf(10d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(10d), "GBP")).willReturn(wasPriceData);
		final PriceData discountPriceData = new PriceData();
		discountPriceData.setValue(BigDecimal.valueOf(2d));
		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(2d), "GBP")).willReturn(discountPriceData);
		given(accommodationDiscountEvaluationStrategyMap.get(Matchers.anyString())).willReturn(strategy);
		given(strategy.evaluateDiscount(Matchers.anyDouble(), Matchers.any(ProductPromotionModel.class), Matchers.anyString()))
				.willReturn(2d);
		handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		Assert.assertNotNull(accommodationAvailabilityResponseData.getRoomStays().get(0).getRatePlans());
		Assert.assertEquals(BigDecimal.valueOf(8d),
				accommodationAvailabilityResponseData.getRoomStays().get(0).getRatePlans().get(0).getActualRate().getValue());
	}

	private class TestDataSetUp
	{

		public RoomRateData createRoomRates(final String code)
		{
			final RoomRateData roomRate = new RoomRateData();
			roomRate.setCode(code);
			return roomRate;
		}

		public RatePlanData createRatePlanData(final String code, final RoomRateData roomRate)
		{
			final RatePlanData ratePlanData = new RatePlanData();
			ratePlanData.setCode(code);
			ratePlanData.setRoomRates(Stream.of(roomRate).collect(Collectors.toList()));
			ratePlanData.setAvailableQuantity(10);
			return ratePlanData;
		}

		public RoomStayData createRoomStays(final RatePlanData ratePlan)
		{
			final RoomStayData roomStay = new RoomStayData();
			roomStay.setRatePlans(Stream.of(ratePlan).collect(Collectors.toList()));
			return roomStay;
		}

		public CriterionData createCriterionDataWithCurrencyIso(final String currencyIso)
		{
			final CriterionData criterion = new CriterionData();
			criterion.setCurrencyIso(currencyIso);
			return criterion;
		}

		public CriterionData createCriterionDataWithoutCurrencyIso()
		{
			final CriterionData criterion = new CriterionData();
			return criterion;
		}

	}

}
