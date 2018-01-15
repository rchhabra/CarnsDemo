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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.SpecialServiceRequestData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.CancelPenaltiesDescriptionCreationStrategy;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.RoomPreferenceType;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationReservationReservedRoomStayHandlerTest
{
	@InjectMocks
	AccommodationReservationReservedRoomStayHandler accommodationReservationReservedRoomStayHandler = new AccommodationReservationReservedRoomStayHandler();

	@Mock
	private AbstractOrderModel orderModel;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private BookingService bookingService;

	@Mock
	private Converter<RatePlanModel, RatePlanData> ratePlanConverter;

	@Mock
	private Converter<AccommodationModel, RoomTypeData> roomTypeConverter;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

	@Mock
	private Converter<SpecialRequestDetailModel, SpecialRequestDetailData> specialRequestDetailsConverter;

	@Mock
	private Converter<RoomPreferenceModel, RoomPreferenceData> roomPreferenceConverter;

	@Mock
	private CancelPenaltiesDescriptionCreationStrategy cancelPenaltiesDescriptionCreationStrategy;

	private TestDataSetup testSetUp;


	@Before
	public void setUp()
	{
		testSetUp = new TestDataSetup();
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		given(orderModel.getCurrency()).willReturn(currencyModel);
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(10d), "GBP"))
				.thenReturn(testSetUp.createPriceData(10d));
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20d), "GBP"))
				.thenReturn(testSetUp.createPriceData(15d));
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(Double.valueOf(1.0d)), "GBP"))
				.thenReturn(testSetUp.createPriceData(Double.valueOf(1.0d)));
	}


	@Test
	public void testHandle()
	{
		given(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.willReturn(testSetUp.createAccommodationOrderEntryGroups());
		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setCode("TEST_RATE_PLAN_CODE");
		given(ratePlanConverter.convert(Matchers.any(RatePlanModel.class))).willReturn(ratePlanData);
		final RoomTypeData roomData = new RoomTypeData();
		given(roomTypeConverter.convert(testSetUp.createAccommodationOrderEntryGroups().get(0).getAccommodation()))
				.willReturn(roomData);

		doNothing().when(cancelPenaltiesDescriptionCreationStrategy).updateCancelPenaltiesDescription(
				testSetUp.createAccommodationOrderEntryGroups().get(0).getRatePlan(), new RoomStayData());

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationReservedRoomStayHandler.handle(orderModel, accommodationReservationData);
		assertNotNull(accommodationReservationData.getRoomStays());

	}

	@Test
	public void testSetTotalsPerRoom()
	{
		given(orderModel.getNet()).willReturn(true);

		final PriceData priceData1 = new PriceData();
		priceData1.setValue(BigDecimal.valueOf(20.00));
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20.00), "GBP"))
				.thenReturn(priceData1);

		final PriceData priceData2 = new PriceData();
		priceData1.setValue(BigDecimal.valueOf(30.00));
		Mockito.when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(30.00), "GBP"))
				.thenReturn(priceData2);

		final ReservedRoomStayData roomStayData = testSetUp.getReservedRoomStayData();
		accommodationReservationReservedRoomStayHandler.setTotalsPerRoom(testSetUp.createAccommodationOrderEntryGroups().get(0),
				orderModel, roomStayData);
		Assert.assertEquals(BigDecimal.valueOf(30.00), roomStayData.getBaseRate().getBasePrice().getValue());
	}



	@Test
	public void testSetServices()
	{
		final ProductData productData = new ProductData();
		final ReservedRoomStayData roomStayData = testSetUp.getReservedRoomStayData();
		given(productConverter.convert(Mockito.mock(ProductModel.class))).willReturn(productData);
		final PriceData priceData1 = new PriceData();
		given(accommodationReservationReservedRoomStayHandler.getBasePriceData(testSetUp.createOrderEntries(AmendStatus.NEW).get(0),
				orderModel)).willReturn(priceData1);
		final PriceData priceData2 = new PriceData();
		given(accommodationReservationReservedRoomStayHandler
				.getTotalPriceData(testSetUp.createOrderEntries(AmendStatus.NEW).get(0), orderModel)).willReturn(priceData2);
		accommodationReservationReservedRoomStayHandler.setServices(testSetUp.createAccommodationOrderEntryGroups().get(0),
				roomStayData, orderModel);
		assertNotNull(roomStayData.getServices());
	}

	@Test
	public void testSetGuestCounts()
	{
		final ReservedRoomStayData roomStayData = testSetUp.getReservedRoomStayData();
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		given(passengerTypeConverter
				.convert(testSetUp.createAccommodationOrderEntryGroups().get(0).getGuestCounts().get(0).getPassengerType()))
						.willReturn(passengerTypeData);
		accommodationReservationReservedRoomStayHandler.setGuestCounts(testSetUp.createAccommodationOrderEntryGroups().get(0),
				roomStayData);
		assertNotNull(roomStayData.getGuestCounts());
	}

	@Test
	public void testSetGuestData()
	{
		final ReservedRoomStayData roomStayData = testSetUp.getReservedRoomStayData();
		accommodationReservationReservedRoomStayHandler.setGuestData(testSetUp.createAccommodationOrderEntryGroups().get(0),
				roomStayData);
		assertNotNull(roomStayData.getReservedGuests());
		Assert.assertEquals("ABC", roomStayData.getReservedGuests().get(0).getProfile().getFirstName());
	}

	@Test
	public void testSetSpecialRequestDetails()
	{
		final ReservedRoomStayData roomStayData = testSetUp.getReservedRoomStayData();
		final SpecialRequestDetailData data = new SpecialRequestDetailData();
		final SpecialServiceRequestData reqData = new SpecialServiceRequestData();
		reqData.setCode("req1");
		data.setSpecialServiceRequests(Stream.of(reqData).collect(Collectors.toList()));
		given(specialRequestDetailsConverter.convert(Matchers.any(SpecialRequestDetailModel.class))).willReturn(data);
		accommodationReservationReservedRoomStayHandler
				.setSpecialRequestDetails(testSetUp.createAccommodationOrderEntryGroups().get(0), roomStayData);
		assertNotNull(roomStayData.getSpecialRequestDetail());
	}

	@Test
	public void testSetRoomPreferences()
	{
		final ReservedRoomStayData roomStayData = testSetUp.getReservedRoomStayData();
		final RoomPreferenceData roomPrefData = new RoomPreferenceData();
		roomPrefData.setRoomPreferenceType(RoomPreferenceType.BED_PREFERENCE.getCode());
		accommodationReservationReservedRoomStayHandler.setRoomPreferences(testSetUp.createAccommodationOrderEntryGroups().get(0),
				roomStayData);
		assertNotNull(roomStayData.getRoomPreferences());
	}



	private class TestDataSetup
	{
		public List<AbstractOrderEntryModel> createOrderEntries(final AmendStatus amendStatus)
		{
			final RoomRateProductModel product1 = new RoomRateProductModel();
			product1.setCode("product1");

			final RoomRateProductModel product2 = new RoomRateProductModel();
			product2.setCode("product2");

			final ProductModel product3 = new ProductModel();
			product3.setCode("product3");

			final TaxValue testTax1 = new TaxValue("testTax1", Double.valueOf(1.0d), true, Double.valueOf(1.0d), "GBP");
			final TaxValue testTax2 = new TaxValue("testTax2", Double.valueOf(1.0d), true, Double.valueOf(1.0d), "GBP");

			final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, amendStatus, OrderEntryStatus.LIVING,
					product1, 1, Double.valueOf(10), Double.valueOf(15), Stream.of(testTax1).collect(Collectors.toList()));
			final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, amendStatus, OrderEntryStatus.LIVING,
					product2, 1, Double.valueOf(10), Double.valueOf(15), Stream.of(testTax2).collect(Collectors.toList()));

			final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(true, amendStatus, OrderEntryStatus.LIVING,
					product3, 1, Double.valueOf(10), Double.valueOf(15), Stream.of(testTax2).collect(Collectors.toList()));
			return Arrays.asList(entry1, entry2, entry3);
		}

		private AbstractOrderEntryModel createAbstractOrderEntryModel(final boolean isActive, final AmendStatus amendStatus,
				final OrderEntryStatus orderEntryStatus, final ProductModel product, final int quantity, final double basePrice,
				final double totalPrice, final List<TaxValue> taxValues)
		{
			final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel()
			{
				@Override
				public Collection<TaxValue> getTaxValues()
				{
					return taxValues;
				}

			};
			abstractOrderEntryModel.setActive(isActive);
			abstractOrderEntryModel.setProduct(product);
			abstractOrderEntryModel.setQuantityStatus(orderEntryStatus);
			abstractOrderEntryModel.setQuantity(Long.valueOf(quantity));
			abstractOrderEntryModel.setBasePrice(basePrice);
			abstractOrderEntryModel.setTotalPrice(totalPrice);
			abstractOrderEntryModel.setAmendStatus(amendStatus);
			return abstractOrderEntryModel;
		}

		private List<AccommodationOrderEntryGroupModel> createAccommodationOrderEntryGroups()
		{
			final AccommodationOrderEntryGroupModel accOrderEntryGroup = new AccommodationOrderEntryGroupModel();
			final SimpleDateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			try
			{
				accOrderEntryGroup.setStartingDate(dateFormat.parse("09/12/2016"));
				accOrderEntryGroup.setEndingDate(dateFormat.parse("11/12/2016"));
				accOrderEntryGroup.setRoomStayRefNumber(0);

				final RatePlanModel ratePlanModel = new RatePlanModel();
				accOrderEntryGroup.setRatePlan(ratePlanModel);
				final AccommodationModel accommodationModel = new AccommodationModel();
				accOrderEntryGroup.setAccommodation(accommodationModel);
				accOrderEntryGroup.setEntries(createOrderEntries(AmendStatus.NEW));

				accOrderEntryGroup.setFirstName("ABC");
				accOrderEntryGroup.setLastName("ABC");


				final GuestCountModel guestCount = new GuestCountModel();
				final PassengerTypeModel passengerType = new PassengerTypeModel();
				passengerType.setCode("adult");
				guestCount.setQuantity(2);
				accOrderEntryGroup.setGuestCounts(Stream.of(guestCount).collect(Collectors.toList()));

				final SpecialRequestDetailModel splReq = new SpecialRequestDetailModel();
				final SpecialServiceRequestModel splReqModel = new SpecialServiceRequestModel();
				splReqModel.setCode("req1");
				splReq.setSpecialServiceRequest(Stream.of(splReqModel).collect(Collectors.toList()));
				accOrderEntryGroup.setSpecialRequestDetail(splReq);

				final RoomPreferenceModel roomPrefModel = new RoomPreferenceModel();
				roomPrefModel.setPreferenceType(RoomPreferenceType.BED_PREFERENCE);
				accOrderEntryGroup.setRoomPreferences(Stream.of(roomPrefModel).collect(Collectors.toList()));
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
			return Stream.of(accOrderEntryGroup).collect(Collectors.toList());

		}

		public ReservedRoomStayData getReservedRoomStayData()
		{
			final SimpleDateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			try
			{
				roomStay.setCheckInDate(dateFormat.parse("09/12/2016"));
				roomStay.setCheckOutDate(dateFormat.parse("11/12/2016"));
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
			final RatePlanData ratePlanData1 = new RatePlanData();
			ratePlanData1.setCode("ratePlan1");
			roomStay.setRatePlans(Stream.of(ratePlanData1).collect(Collectors.toList()));
			return roomStay;
		}

		private PriceData createPriceData(final double price)
		{
			final PriceData priceData = new PriceData();
			priceData.setValue(BigDecimal.valueOf(price));
			priceData.setCurrencyIso("GBP");
			priceData.setFormattedValue("GBP " + price);
			return priceData;
		}

	}

}
