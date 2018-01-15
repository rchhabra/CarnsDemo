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

package de.hybris.platform.travelfacades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.facades.impl.DefaultBookingListFacade;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the BookingListFacade implementation
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBookingListFacadeTest
{

	@InjectMocks
	private DefaultBookingListFacade bookingListFacade;

	@Mock
	private Map<String, Integer> orderStatusValueMap;

	@Mock
	private ReservationPipelineManager transportBookingListPipelineManager;

	@Mock
	private AccommodationReservationPipelineManager accommodationBookingListPipelineManager;

	@Mock
	private GlobalTravelReservationPipelineManager travelBookingListPipelineManager;

	@Mock
	private UserService userService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private TravelCustomerAccountService customerAccountService;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Test
	public void testGetCurrentCustomerBookingsCurrentCustomerNull()
	{
		when(userService.getCurrentUser()).thenReturn(null);
		assertEquals(Collections.EMPTY_LIST, bookingListFacade.getCurrentCustomerBookings());
	}

	@Test
	public void testGetCurrentCustomerBookingsAnonymousCustomer()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final UserModel user = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);
		when(userService.isAnonymousUser(user)).thenReturn(true);
		when(userService.getCurrentUser()).thenReturn(user);
		assertEquals(Collections.EMPTY_LIST, bookingListFacade.getCurrentCustomerBookings());
	}

	@Test
	public void testGetCurrentCustomerBookingsNoOrders()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);
		when(userService.isAnonymousUser(customer)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(customer);
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null)).thenReturn(Collections.EMPTY_LIST);
		assertEquals(Collections.EMPTY_LIST, bookingListFacade.getCurrentCustomerBookings());
	}

	@Test
	public void testGetCurrentCustomerBookings() throws ParseException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);
		when(userService.isAnonymousUser(customer)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(customer);
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);

		//Create OrderModels
		final OrderModel order1 = testDataSetUp.createOrderModel("0001", customer);
		final OrderModel order2 = testDataSetUp.createOrderModel("0002", customer);
		final OrderModel order3 = testDataSetUp.createOrderModel("0003", customer);
		final OrderModel order4 = testDataSetUp.createOrderModel("0004", customer);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null))
				.thenReturn(Stream.of(order1, order2, order3, order4).collect(Collectors.toList()));

		//Reservation Data
		final TransportOfferingData to1 = testDataSetUp.createTransportOfferingData("EZY1111070320160735", "07/03/2016 07:35:00");
		final TransportOfferingData to2 = testDataSetUp.createTransportOfferingData("EZY2222070320161135", "07/03/2016 11:35:00");
		final TransportOfferingData to3 = testDataSetUp.createTransportOfferingData("EZY3333080320160735", "08/03/2016 07:35:00");
		final TransportOfferingData to4 = testDataSetUp.createTransportOfferingData("EZY4444080320160735", "08/03/2016 11:35:00");

		final OriginDestinationOptionData odOptions1 = testDataSetUp
				.createOriginDestinationOptionData(Stream.of(to1).collect(Collectors.toList()));
		final OriginDestinationOptionData odOptions2 = testDataSetUp
				.createOriginDestinationOptionData(Stream.of(to2).collect(Collectors.toList()));
		final OriginDestinationOptionData odOptions3 = testDataSetUp
				.createOriginDestinationOptionData(Stream.of(to3).collect(Collectors.toList()));
		final OriginDestinationOptionData odOptions4 = testDataSetUp
				.createOriginDestinationOptionData(Stream.of(to4).collect(Collectors.toList()));

		final ItineraryData itinerary1 = testDataSetUp.createItineraryData(null,
				Stream.of(odOptions1).collect(Collectors.toList()));
		final ItineraryData itinerary2 = testDataSetUp.createItineraryData(null,
				Stream.of(odOptions2).collect(Collectors.toList()));
		final ItineraryData itinerary3 = testDataSetUp.createItineraryData(null,
				Stream.of(odOptions3).collect(Collectors.toList()));
		final ItineraryData itinerary4 = testDataSetUp.createItineraryData(null,
				Stream.of(odOptions4).collect(Collectors.toList()));

		final ReservationItemData resItemData1 = testDataSetUp.createReservationItemData(1, itinerary1);
		final ReservationItemData resItemData2 = testDataSetUp.createReservationItemData(1, itinerary2);
		final ReservationItemData resItemData3 = testDataSetUp.createReservationItemData(1, itinerary3);
		final ReservationItemData resItemData4 = testDataSetUp.createReservationItemData(1, itinerary4);

		final ReservationData resData1 = testDataSetUp.createReservationData("0001",
				Stream.of(resItemData1).collect(Collectors.toList()), OrderStatus.ACTIVE.getCode());
		final ReservationData resData2 = testDataSetUp.createReservationData("0002",
				Stream.of(resItemData2).collect(Collectors.toList()), OrderStatus.ACTIVE.getCode());
		final ReservationData resData3 = testDataSetUp.createReservationData("0003",
				Stream.of(resItemData3).collect(Collectors.toList()), OrderStatus.PAST.getCode());
		final ReservationData resData4 = testDataSetUp.createReservationData("0004",
				Stream.of(resItemData4).collect(Collectors.toList()), OrderStatus.CANCELLED.getCode());

		when(transportBookingListPipelineManager.executePipeline(order1)).thenReturn(resData1);
		when(transportBookingListPipelineManager.executePipeline(order2)).thenReturn(resData2);
		when(transportBookingListPipelineManager.executePipeline(order3)).thenReturn(resData3);
		when(transportBookingListPipelineManager.executePipeline(order4)).thenReturn(resData4);

		orderStatusValueMap.put(OrderStatus.CANCELLED.getCode(), Integer.parseInt("0004"));
		orderStatusValueMap.put(OrderStatus.PAST.getCode(), Integer.parseInt("0003"));
		orderStatusValueMap.put(OrderStatus.ACTIVE.getCode(), Integer.parseInt("0002"));
		orderStatusValueMap.put(OrderStatus.ACTIVE.getCode(), Integer.parseInt("0001"));

		when(orderStatusValueMap.containsKey(OrderStatus.CANCELLED.getCode())).thenReturn(true);
		when(orderStatusValueMap.containsKey(OrderStatus.PAST.getCode())).thenReturn(true);
		when(orderStatusValueMap.containsKey(OrderStatus.ACTIVE.getCode())).thenReturn(true);
		when(orderStatusValueMap.containsKey(OrderStatus.ACTIVE.getCode())).thenReturn(true);

		when(orderStatusValueMap.get(OrderStatus.CANCELLED.getCode())).thenReturn(Integer.parseInt("0004"));
		when(orderStatusValueMap.get(OrderStatus.PAST.getCode())).thenReturn(Integer.parseInt("0003"));
		when(orderStatusValueMap.get(OrderStatus.ACTIVE.getCode())).thenReturn(Integer.parseInt("0002"));
		when(orderStatusValueMap.get(OrderStatus.ACTIVE.getCode())).thenReturn(Integer.parseInt("0001"));

		bookingListFacade.getCurrentCustomerBookings();

	}

	@Test
	public void testGetCurrentCustomerAccommodationBookings()
	{
		assertEquals(Collections.EMPTY_LIST, bookingListFacade.getCurrentCustomerAccommodationBookings());


		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);
		final AccommodationReservationData reservationDataExp = new AccommodationReservationData();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);

		when(userService.isAnonymousUser(customer)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(customer);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null)).thenReturn(Collections.singletonList(orderModel));
		when(accommodationBookingListPipelineManager.executePipeline(orderModel)).thenReturn(reservationDataExp);

		assertTrue(bookingListFacade.getCurrentCustomerAccommodationBookings().contains(reservationDataExp));

	}

	@Test
	public void testGetVisibleCurrentCustomerAccommodationBookings()
	{
		assertEquals(Collections.EMPTY_LIST, bookingListFacade.getVisibleCurrentCustomerAccommodationBookings());


		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);
		final AccommodationReservationData reservationDataExp = new AccommodationReservationData();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		orderModel.setVisibleToOwner(true);

		when(userService.isAnonymousUser(customer)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(customer);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null)).thenReturn(Collections.singletonList(orderModel));
		when(accommodationBookingListPipelineManager.executePipeline(orderModel)).thenReturn(reservationDataExp);

		assertTrue(bookingListFacade.getVisibleCurrentCustomerAccommodationBookings().contains(reservationDataExp));

	}

	@Test
	public void testGetCurrentCustomerTravelBookings()
	{
		assertEquals(Collections.EMPTY_LIST, bookingListFacade.getCurrentCustomerTravelBookings());


		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);
		final GlobalTravelReservationData reservationDataExp = new GlobalTravelReservationData();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		orderModel.setEntries(Collections.singletonList(new AbstractOrderEntryModel()));

		when(userService.isAnonymousUser(customer)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(customer);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null)).thenReturn(Collections.singletonList(orderModel));
		when(travelBookingListPipelineManager.executePipeline(orderModel)).thenReturn(reservationDataExp);

		assertTrue(bookingListFacade.getCurrentCustomerTravelBookings().contains(reservationDataExp));
	}

	@Test
	public void testGetVisibleCurrentCustomerTravelBookings()
	{
		assertEquals(Collections.EMPTY_LIST, bookingListFacade.getVisibleCurrentCustomerTravelBookings());


		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);
		final GlobalTravelReservationData reservationDataExp = new GlobalTravelReservationData();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		orderModel.setVisibleToOwner(true);
		orderModel.setEntries(Collections.singletonList(new AbstractOrderEntryModel()));

		when(userService.isAnonymousUser(customer)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(customer);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null)).thenReturn(Collections.singletonList(orderModel));
		when(travelBookingListPipelineManager.executePipeline(orderModel)).thenReturn(reservationDataExp);

		assertTrue(bookingListFacade.getVisibleCurrentCustomerTravelBookings().contains(reservationDataExp));
	}

	/* Data setup for Test */
	private class TestDataSetUp
	{
		private OrderModel createOrderModel(final String bookingReference, final UserModel userModel)
		{
			final OrderModel orderModel = new OrderModel();
			orderModel.setCode(bookingReference);
			orderModel.setUser(userModel);
			final CurrencyModel currency = new CurrencyModel();
			currency.setIsocode("GBP");
			orderModel.setCurrency(currency);
			return orderModel;
		}

		private CustomerModel createCustomerModel(final String uid, final CustomerType type)
		{
			final CustomerModel customerModel = new CustomerModel();
			customerModel.setUid(uid);
			customerModel.setType(type);
			return customerModel;
		}

		private ReservationData createReservationData(final String bookingReference,
				final List<ReservationItemData> reservationItems, final String bookingStatusCode)
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setCode(bookingReference);
			reservationData.setReservationItems(reservationItems);
			reservationData.setBookingStatusCode(bookingStatusCode);
			return reservationData;
		}

		private ReservationItemData createReservationItemData(final int odRefNumber, final ItineraryData reservationItinerary)
		{
			final ReservationItemData reservationItemData = new ReservationItemData();
			reservationItemData.setOriginDestinationRefNumber(odRefNumber);
			reservationItemData.setReservationItinerary(reservationItinerary);
			return reservationItemData;
		}

		private ItineraryData createItineraryData(final List<TravellerData> travellers,
				final List<OriginDestinationOptionData> odOptions)
		{
			final ItineraryData itineraryData = new ItineraryData();
			itineraryData.setTravellers(travellers);
			itineraryData.setOriginDestinationOptions(odOptions);
			return itineraryData;
		}

		private ReservedRoomStayData createRoomStayData(final List<GuestData> reservedGuests,
				final List<OriginDestinationOptionData> odOptions)
		{
			final ReservedRoomStayData roomStayData = new ReservedRoomStayData();
			roomStayData.setReservedGuests(reservedGuests);
			return roomStayData;
		}

		private TravellerData createTravellerData(final String uid, final String type, final String firstName,
				final String lastName, final String travellerType)
		{
			final TravellerData travellerData = new TravellerData();
			travellerData.setUid(uid);
			travellerData.setTravellerType(travellerType);
			travellerData.setTravellerInfo(createPassengerInformationData(firstName, lastName, type));
			travellerData.setLabel(type);
			return travellerData;
		}

		private PassengerInformationData createPassengerInformationData(final String firstName, final String lastName,
				final String type)
		{
			final PassengerInformationData paxInfo = new PassengerInformationData();
			paxInfo.setFirstName(firstName);
			paxInfo.setSurname(lastName);
			final PassengerTypeData paxTypeData = new PassengerTypeData();
			paxTypeData.setCode(type);
			paxInfo.setPassengerType(paxTypeData);
			return paxInfo;
		}

		private PriceData createPriceData(final double value)
		{
			final PriceData priceData = new PriceData();
			priceData.setValue(BigDecimal.valueOf(value));
			return priceData;
		}

		private TransportOfferingData createTransportOfferingData(final String code, final String departureDate)
				throws ParseException
		{
			final TransportOfferingData toData = new TransportOfferingData();
			toData.setCode(code);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_TIME_PATTERN);
			toData.setDepartureTime(dateFormat.parse(departureDate));
			toData.setDepartureTimeZoneId(ZoneId.systemDefault());
			return toData;
		}

		private OriginDestinationOptionData createOriginDestinationOptionData(final List<TransportOfferingData> transportOfferings)
		{
			final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
			odOptionData.setTransportOfferings(transportOfferings);
			return odOptionData;
		}

		private AccommodationAvailabilityResponseData createAccommodationAvailabilityResponse()
		{
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
			accommodationAvailabilityResponseData.setRoomStays(Arrays.asList(createRoomStayData(0, new Date(), new Date())));
			accommodationAvailabilityResponseData.setAccommodationReference(createPropertyData("TEST_ACCOMODATION_OFFERING_CODE"));
			return accommodationAvailabilityResponseData;
		}

		private RoomStayData createRoomStayData(final int roomStayRefNumber, final Date checkInDate, final Date checkOutDate)
		{
			final RoomStayData roomStayData = new RoomStayData();
			roomStayData.setRoomStayRefNumber(roomStayRefNumber);
			roomStayData.setCheckInDate(checkInDate);
			roomStayData.setCheckOutDate(checkOutDate);
			roomStayData.setRoomTypes(Arrays.asList(createRoomTypeData("TEST_ROOM_TYPE_CODE")));
			roomStayData.setRatePlans(
					Arrays.asList(createRatePlanData("TEST_RATE_PLAN_CODE", "TEST_ROOM_RATE_CODE", checkInDate, checkOutDate)));
			return roomStayData;
		}

		private RoomTypeData createRoomTypeData(final String code)
		{
			final RoomTypeData roomTypeData = new RoomTypeData();
			roomTypeData.setCode(code);
			return roomTypeData;
		}

		private RatePlanData createRatePlanData(final String code, final String roomRateCode, final Date startTime,
				final Date endTime)
		{
			final RatePlanData ratePlanData = new RatePlanData();
			ratePlanData.setRoomRates(Arrays.asList(createRoomRateData("TEST_ROOM_RATE_CODE", startTime, endTime)));
			return ratePlanData;
		}

		private RoomRateData createRoomRateData(final String code, final Date startTime, final Date endTime)
		{
			final RoomRateData roomRateData = new RoomRateData();
			roomRateData.setCode(code);
			roomRateData.setStayDateRange(createStayDateRangeData(startTime, endTime));
			return roomRateData;

		}

		private StayDateRangeData createStayDateRangeData(final Date startTime, final Date endTime)
		{
			final StayDateRangeData stayDateRange = new StayDateRangeData();
			stayDateRange.setStartTime(startTime);
			stayDateRange.setEndTime(endTime);
			return stayDateRange;
		}

		private PropertyData createPropertyData(final String accommodationOfferingCode)
		{
			final PropertyData propertyData = new PropertyData();
			propertyData.setAccommodationOfferingCode(accommodationOfferingCode);
			return propertyData;
		}

	}

}
