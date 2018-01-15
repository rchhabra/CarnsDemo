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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.CalculateTotalPriceForChangeDatesStrategy;
import de.hybris.platform.travelservices.strategies.impl.DefaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy;
import de.hybris.platform.travelservices.strategies.impl.DefaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultCalculatePaymentTypeForChangeDatesStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCalculatePaymentTypeForChangeDatesStrategyTest
{
	@InjectMocks
	DefaultCalculatePaymentTypeForChangeDatesStrategy defaultCalculatePaymentTypeForChangeDatesStrategy;
	@Mock
	private BookingService bookingService;
	@Mock
	private PriceData priceData;
	@Mock
	OrderModel orderModel;
	@Mock
	private DefaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy calculateTotalPriceWithGuaranteeForChangeDatesStrategy;
	@Mock
	private DefaultCalculateTotalPriceWithoutGuaranteeForChangeDatesStrategy calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy;
	@Mock
	AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelFC;
	@Mock
	AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelNF;
	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	private Map<String, CalculateTotalPriceForChangeDatesStrategy> calculateTotalPriceForChangeDatesStrategyMap;

	private final String TEST_CURRENCY_ISO_CODE = "TEST_CURRENCY_ISO_CODE";



	@Before
	public void setUp()
	{
		calculateTotalPriceForChangeDatesStrategyMap = new HashMap<>();
		calculateTotalPriceForChangeDatesStrategyMap.put("NO_GUARANTEES",
				calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy);
		calculateTotalPriceForChangeDatesStrategyMap.put("GUARANTEES", calculateTotalPriceWithGuaranteeForChangeDatesStrategy);
		defaultCalculatePaymentTypeForChangeDatesStrategy
				.setCalculateTotalPriceForChangeDatesStrategyMap(calculateTotalPriceForChangeDatesStrategyMap);
	}

	@Test
	public void testCalculateForNullAccommodationReservationData()
	{
		Assert.assertTrue(MapUtils.isEmpty(
				defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(null, new AccommodationAvailabilityResponseData())));
	}

	@Test
	public void testCalculateForNullAccommodationAvailabilityResponse()
	{
		Assert.assertTrue(MapUtils
				.isEmpty(defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(new AccommodationReservationData(), null)));
	}

	@Test
	public void testCalculateForNullTotalToPay()
	{
		Assert.assertTrue(MapUtils.isEmpty(defaultCalculatePaymentTypeForChangeDatesStrategy
				.calculate(new AccommodationReservationData(), new AccommodationAvailabilityResponseData())));
	}

	@Test
	public void testCalculateForZeroActualOrderAmoutPaid()
	{
		final PriceData expectedResult = createPriceData(0d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(100d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();

		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(accommodationAvailabilityResponseData,
				StringUtils.EMPTY)).willReturn(BigDecimal.valueOf(200d));

		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForTotalAmountForActualOrderPaidWithSamePaymentType()
	{
		final PriceData expectedResult = createPriceData(0d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(100d).getValue());
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(0d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();

		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForTotalAmountForActualOrderPaidWithPayablePaymentType()
	{
		final PriceData expectedResult = createPriceData(100d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(200d).getValue());
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(0d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();

		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForTotalAmountForActualOrderPaidWithRefundPaymentType()
	{
		final PriceData expectedResult = createPriceData(50d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(50d).getValue());
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(0d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();

		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForChangeDatePriceEqualsActualOrderPriceAndPartialPaymentForRefund()
	{
		final PriceData expectedResult = createPriceData(100d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(50d).getValue());
		given(bookingService.getOrder(Matchers.anyString())).willReturn(orderModel);
		given(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.willReturn(Arrays.asList(accommodationOrderEntryGroupModelNF, accommodationOrderEntryGroupModelFC));
		given(accommodationOrderEntryGroupModelNF.getRoomStayRefNumber()).willReturn(0);
		given(accommodationOrderEntryGroupModelFC.getRoomStayRefNumber()).willReturn(1);
		given(bookingService.getOrderTotalPaidByEntryGroup(orderModel, accommodationOrderEntryGroupModelNF))
				.willReturn(BigDecimal.valueOf(50d));
		given(bookingService.getOrderTotalPaidByEntryGroup(orderModel, accommodationOrderEntryGroupModelFC))
				.willReturn(BigDecimal.valueOf(300d));

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(50d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(createRoomStays());
		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForChangeDatePriceEqualsActualOrderPriceAndPartialPaymentForSame()
	{
		final PriceData expectedResult = createPriceData(0d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(50d).getValue());
		given(bookingService.getOrder(Matchers.anyString())).willReturn(orderModel);
		given(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.willReturn(Arrays.asList(accommodationOrderEntryGroupModelNF, accommodationOrderEntryGroupModelFC));
		given(accommodationOrderEntryGroupModelNF.getRoomStayRefNumber()).willReturn(0);
		given(accommodationOrderEntryGroupModelFC.getRoomStayRefNumber()).willReturn(1);
		given(bookingService.getOrderTotalPaidByEntryGroup(orderModel, accommodationOrderEntryGroupModelNF))
				.willReturn(BigDecimal.valueOf(100d));
		given(bookingService.getOrderTotalPaidByEntryGroup(orderModel, accommodationOrderEntryGroupModelFC))
				.willReturn(BigDecimal.valueOf(200d));

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(50d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(createRoomStays());
		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForChangeDatePriceMoreThanActualOrderPriceAndPartialPaymentForPayable()
	{
		final PriceData expectedResult = createPriceData(100d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(200d).getValue());
		given(calculateTotalPriceWithGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(150d).getValue());
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(50d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();

		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForChangeDatePriceMoreThanActualOrderPriceAndPartialPaymentForRefund()
	{
		final PriceData expectedResult = createPriceData(100d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(200d).getValue());
		given(calculateTotalPriceWithGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(40d).getValue());

		given(bookingService.getOrder(Matchers.anyString())).willReturn(orderModel);
		given(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.willReturn(Arrays.asList(accommodationOrderEntryGroupModelNF, accommodationOrderEntryGroupModelFC));
		given(accommodationOrderEntryGroupModelNF.getRoomStayRefNumber()).willReturn(0);
		given(accommodationOrderEntryGroupModelFC.getRoomStayRefNumber()).willReturn(1);
		given(bookingService.getOrderTotalPaidByEntryGroup(orderModel, accommodationOrderEntryGroupModelNF))
				.willReturn(BigDecimal.valueOf(100d));
		given(bookingService.getOrderTotalPaidByEntryGroup(orderModel, accommodationOrderEntryGroupModelFC))
				.willReturn(BigDecimal.valueOf(300d));


		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(20d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(createRoomStays());
		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForChangeDatePriceMoreThanActualOrderPriceAndPartialPaymentForSame()
	{
		final PriceData expectedResult = createPriceData(0d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(200d).getValue());
		given(calculateTotalPriceWithGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(40d).getValue());

		given(bookingService.getOrder(Matchers.anyString())).willReturn(orderModel);
		given(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.willReturn(Arrays.asList(accommodationOrderEntryGroupModelNF, accommodationOrderEntryGroupModelFC));
		given(accommodationOrderEntryGroupModelNF.getRoomStayRefNumber()).willReturn(0);
		given(accommodationOrderEntryGroupModelFC.getRoomStayRefNumber()).willReturn(1);
		given(bookingService.getOrderTotalPaidByEntryGroup(orderModel, accommodationOrderEntryGroupModelNF))
				.willReturn(BigDecimal.valueOf(100d));
		given(bookingService.getOrderTotalPaidByEntryGroup(orderModel, accommodationOrderEntryGroupModelFC))
				.willReturn(BigDecimal.valueOf(200d));


		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(20d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(createRoomStays());
		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	@Test
	public void testCalculateForChangeDatePriceLessThanActualOrderPrice()
	{
		final PriceData expectedResult = createPriceData(0d);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).willReturn(expectedResult);
		given(calculateTotalPriceWithoutGuaranteeForChangeDatesStrategy.calculate(Matchers.any(), Matchers.anyString()))
				.willReturn(createPriceData(40d).getValue());

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setTotalRate(createRateData(100d));
		accommodationReservationData.setTotalToPay(createPriceData(20d));
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setRoomStays(createRoomStays());
		final Map<String, String> result = defaultCalculatePaymentTypeForChangeDatesStrategy.calculate(accommodationReservationData,
				accommodationAvailabilityResponseData);

		Assert.assertEquals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND,
				result.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
		Assert.assertEquals(expectedResult.getFormattedValue(), result.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));
	}

	private RateData createRateData(final double value)
	{
		final RateData rateData = new RateData();
		rateData.setActualRate(createPriceData(value));
		return rateData;
	}

	private PriceData createPriceData(final double value)
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(value));
		priceData.setFormattedValue("" + value);
		priceData.setCurrencyIso(TEST_CURRENCY_ISO_CODE);
		return priceData;
	}

	private List<RoomStayData> createRoomStays()
	{
		final List<RoomStayData> roomStayData = new ArrayList<>();
		roomStayData.add(createReservedRoomStay(0, 100d));
		roomStayData.add(createReservedRoomStay(1, 200d));
		return roomStayData;
	}

	private RoomStayData createReservedRoomStay(final int roomStayRefNumber, final double value)
	{
		final ReservedRoomStayData roomStayData = new ReservedRoomStayData();
		roomStayData.setRoomStayRefNumber(roomStayRefNumber);
		roomStayData.setTotalRate(createRateData(value));
		return roomStayData;
	}
}
