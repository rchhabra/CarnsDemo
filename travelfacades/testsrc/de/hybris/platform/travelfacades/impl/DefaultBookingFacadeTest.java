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
 */

package de.hybris.platform.travelfacades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.ProfileData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomRateCartData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.booking.action.strategies.CalculatePaymentTypeForChangeDatesStrategy;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.AccommodationBookingFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.forms.AccommodationAddToCartForm;
import de.hybris.platform.travelfacades.facades.impl.DefaultBookingFacade;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.exceptions.RequestKeyGeneratorException;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.travel.DisruptionModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.GuestCountService;
import de.hybris.platform.travelservices.services.RoomPreferenceService;
import de.hybris.platform.travelservices.strategies.AutoAccommodationAllocationStrategy;
import de.hybris.platform.travelservices.strategies.TravelCheckoutCustomerStrategy;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the BookingFacade implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBookingFacadeTest
{
	@InjectMocks
	private DefaultBookingFacade bookingFacade;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private AccommodationCartFacade accommodationCartFacade;

	@Mock
	private BookingService bookingService;

	@Mock
	private TravelCustomerAccountService customerAccountService;

	@Mock
	private ReservationFacade reservationFacade;

	@Mock
	private TravelCartService travelCartService;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private UserService userService;

	@Mock
	private CheckoutFacade checkoutFacade;

	@Mock
	private SessionService sessionService;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private AutoAccommodationAllocationStrategy autoAccommodationAllocationStrategy;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private ReservationPipelineManager reservationItemPipelineManager;

	@Mock
	private Map<String, Integer> orderStatusValueMap;

	@Mock
	private AccommodationReservationPipelineManager fullAccommodationReservationPipelineManager;

	@Mock
	private AccommodationReservationPipelineManager basicAccommodationReservationPipelineManager;

	@Mock
	private Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter;

	@Mock
	private Converter<PassengerTypeQuantityData, GuestCountModel> guestCountReverseConverter;

	@Mock
	private RoomPreferenceService roomPreferenceService;

	@Mock
	private ModelService modelService;

	@Mock
	private TravelCheckoutCustomerStrategy travelCheckoutCustomerStrategy;

	@Mock
	private GlobalTravelReservationPipelineManager basicGlobalTravelReservationPipelineManager;

	@Mock
	private TimeService timeService;

	@Mock
	private CalculatePaymentTypeForChangeDatesStrategy calculatePaymentTypeForChangeDatesStrategy;

	@Mock
	private GuestCountService guestCountService;

	@Mock
	private List<OrderStatus> notAllowedStatuses;

	@Mock
	private AccommodationBookingFacade accommodationBookingFacade;

	@Test
	public void testIsCancelPossible()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);

		when(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any(BaseStoreModel.class)))
				.thenReturn(orderModel);

		bookingFacade.isCancelPossible("0001");
		verify(bookingService, times(1)).isCancelPossible(Matchers.any(OrderModel.class));
	}

	@Test
	public void testIsCancelPossibleOrderModelNotFound()
	{
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);

		when(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any(BaseStoreModel.class)))
				.thenThrow(ModelNotFoundException.class);

		final boolean isCancelPossible = bookingFacade.isCancelPossible("0001");
		assertFalse(isCancelPossible);
	}

	@Test
	public void testGetRefundTotal()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any(BaseStoreModel.class)))
				.thenReturn(orderModel);
		when(bookingService.getTotalToRefund(orderModel)).thenReturn(BigDecimal.TEN);
		final PriceData priceData = testDataSetUp.createPriceData(10d);
		when(travelCommercePriceFacade.createPriceData(Matchers.eq(PriceDataType.BUY), Matchers.eq(BigDecimal.valueOf(10d)),
				Matchers.anyString())).thenReturn(priceData);
		bookingFacade.getRefundTotal("0001");
		verify(bookingService, times(1)).getTotalToRefund(Matchers.any(OrderModel.class));
	}

	@Test
	public void testCancelOrder()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any(BaseStoreModel.class)))
				.thenReturn(orderModel);
		when(bookingService.cancelOrder(orderModel)).thenReturn(true);
		final boolean cancelOrder = bookingFacade.cancelOrder("0001");
		assertTrue(cancelOrder);
		verify(bookingService, times(1)).cancelOrder(Matchers.any(OrderModel.class));
	}

	@Test
	public void testCancelOrderNoOrderFound()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any(BaseStoreModel.class)))
				.thenThrow(ModelNotFoundException.class);
		final boolean cancelOrder = bookingFacade.cancelOrder("0001");
		assertFalse(cancelOrder);
	}

	@Test
	public void testGetBookingByBookingReference()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel order = testDataSetUp.createOrderModel("0001", null);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);
		when(reservationFacade.getReservationData(order)).thenReturn(testDataSetUp.createReservationData("0001", null, null));
		final ReservationData reservationData = bookingFacade.getBookingByBookingReference("0001");
		assertEquals("0001", reservationData.getCode());
	}

	@Test
	public void testRetrieveGlobalReservationData()
	{
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();
		final OrderModel orderModel = new OrderModel();
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(globalTravelReservationData);

		assertNotNull(globalTravelReservationData);
	}

	@Test
	public void testGetBookerEmailIDWithEmptyGlobalReservationData()
	{
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();
		assertNull(bookingFacade.getBookerEmailID(globalTravelReservationData, "Smith", "JHDF7Q"));
	}

	@Test
	public void testGetBookerEmailIDWithReservationData()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();

		final List<TravellerData> travellerDataList = new LinkedList<>();
		travellerDataList.add(testDataSetUp.createSimpleTravellerData("872JSD", "Smith"));
		travellerDataList.add(testDataSetUp.createSimpleTravellerData("JKFHS4", "White"));
		final ReservationItemData reservationItemData = testDataSetUp
				.createReservationItemData(0, testDataSetUp.createItineraryData(travellerDataList, null));
		globalTravelReservationData.setReservationData(testDataSetUp
				.createReservationData("0001", Collections.singletonList(reservationItemData), null));
		globalTravelReservationData.getReservationData().setAdditionalSecurity(Boolean.FALSE);
		globalTravelReservationData.setCustomerData(testDataSetUp.createCustomerData("john.smith@abc.com", CustomerType.GUEST));
		when(reservationFacade
				.getBookerEmailIDFromReservationData(globalTravelReservationData, "Smith", "JKFHS4"))
				.thenReturn("john.smith@abc.com");
		assertNotNull(bookingFacade.getBookerEmailID(globalTravelReservationData, "Smith", "JKFHS4"));
	}

	@Test
	public void testGetBookerEmailIDWithReservationDataWrongCredentials()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();

		final List<TravellerData> travellerDataList = new LinkedList<>();
		travellerDataList.add(testDataSetUp.createSimpleTravellerData("872JSD", "Smith"));
		travellerDataList.add(testDataSetUp.createSimpleTravellerData("JKFHS4", "White"));
		final ReservationItemData reservationItemData = testDataSetUp
				.createReservationItemData(0, testDataSetUp.createItineraryData(travellerDataList, null));
		globalTravelReservationData.setReservationData(testDataSetUp
				.createReservationData("0001", Collections.singletonList(reservationItemData), null));
		globalTravelReservationData.getReservationData().setAdditionalSecurity(Boolean.FALSE);
		globalTravelReservationData.setCustomerData(testDataSetUp.createCustomerData("john.smith@abc.com", CustomerType.GUEST));

		assertNull(bookingFacade.getBookerEmailID(globalTravelReservationData, "John", "JKFHS4"));
	}

	@Test
	public void testGetBookerEmailIDWithAccommodationReservationData()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();

		final List<GuestData> guestDataList = new LinkedList<>();
		guestDataList.add(testDataSetUp.createGuestData("Smith"));
		guestDataList.add(testDataSetUp.createGuestData("White"));

		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(guestDataList, null);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalTravelReservationData.setAccommodationReservationData(accommodationReservationData);
		globalTravelReservationData.setCustomerData(testDataSetUp.createCustomerData("john.smith@abc.com", CustomerType.GUEST));
		when(accommodationBookingFacade
				.getBookerEmailIDFromAccommodationReservationData(globalTravelReservationData, "Smith"))
				.thenReturn("john.smith@abc.com");
		assertNotNull(bookingFacade.getBookerEmailID(globalTravelReservationData, "Smith", null));
	}

	@Test
	public void testGetBookerEmailIDWithAccommodationReservationDataWrongCredentials()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();

		final List<GuestData> guestDataList = new LinkedList<>();
		guestDataList.add(testDataSetUp.createGuestData("Smith"));
		guestDataList.add(testDataSetUp.createGuestData("White"));

		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(guestDataList, null);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalTravelReservationData.setAccommodationReservationData(accommodationReservationData);
		globalTravelReservationData.setCustomerData(testDataSetUp.createCustomerData("john.smith@abc.com", CustomerType.GUEST));

		when(accommodationBookingFacade
				.getBookerEmailIDFromAccommodationReservationData(globalTravelReservationData, "John"))
				.thenReturn(null);

		assertNull(bookingFacade.getBookerEmailID(globalTravelReservationData, "John", null));
	}

	@Test
	public void testValidateAndReturnBookerEmailIdForInvalidBookingReference()
	{
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(null);
		final String emailId = bookingFacade.validateAndReturnBookerEmailId("0001", "Smith");
		assertNull(emailId);
	}

	@Test
	public void testValidateAndReturnBookerEmailIdNoReservationItems()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel order = testDataSetUp.createOrderModel("0001", null);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);
		when(reservationFacade.getReservationData(order))
				.thenReturn(testDataSetUp.createReservationData("0001", Collections.EMPTY_LIST, null));

		//prepare AccommodationreservationData
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		globalReservationData.setReservationData(testDataSetUp.createReservationData("0001", Collections.EMPTY_LIST, null));
		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(Collections.EMPTY_LIST, null);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalReservationData.setAccommodationReservationData(accommodationReservationData);

		when(reservationFacade.getGlobalTravelReservationData(order)).thenReturn(globalReservationData);

		final String emailId = bookingFacade.validateAndReturnBookerEmailId("0001", "Smith");
		assertNull(emailId);
	}

	@Test
	public void testValidateAndReturnBookerEmailIdNoTravellerData()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ItineraryData itineraryData = testDataSetUp.createItineraryData(Collections.EMPTY_LIST, null);
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, itineraryData);
		final ReservationData reservationData = testDataSetUp.createReservationData("0001",
				Stream.of(reservationItem).collect(Collectors.toList()), null);
		final OrderModel order = testDataSetUp.createOrderModel("0001", null);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		globalReservationData.setReservationData(reservationData);
		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(Collections.EMPTY_LIST, null);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalReservationData.setAccommodationReservationData(accommodationReservationData);
		when(reservationFacade.getGlobalTravelReservationData(order)).thenReturn(globalReservationData);
		final String emailId = bookingFacade.validateAndReturnBookerEmailId("0001", "Smith");
		assertNull(emailId);
	}

	@Test
	public void testValidateAndReturnBookerEmailIdAnonymousUser()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//prepare ReservationData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT", "John", "Smith",
				TravellerType.PASSENGER.getCode());
		final TravellerData travellerDataChild1 = testDataSetUp.createTravellerData("5678", "CHILD", "Jade", "Smith",
				TravellerType.PASSENGER.getCode());
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1, travellerDataChild1).collect(Collectors.toList()), null);
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, itineraryData);
		final ReservationData reservationData = testDataSetUp.createReservationData("0001",
				Stream.of(reservationItem).collect(Collectors.toList()), null);

		//prepare AccommodationreservationData
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		globalReservationData.setReservationData(reservationData);
		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(Collections.EMPTY_LIST, null);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalReservationData.setAccommodationReservationData(accommodationReservationData);

		//Create OrderModel
		final UserModel user = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", CustomerType.GUEST);
		final OrderModel order = testDataSetUp.createOrderModel("0001", user);

		when(reservationFacade.getGlobalTravelReservationData(order)).thenReturn(globalReservationData);

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);
		when(reservationFacade.getReservationData(order)).thenReturn(reservationData);
		when(userService.isAnonymousUser(user)).thenReturn(true);
		final String emailId = bookingFacade.validateAndReturnBookerEmailId("0001", "Smith");
		assertEquals("john.smith@abc.com", emailId);
	}

	@Test
	public void testValidateAndReturnBookerEmailIdCustomer()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//prepare ReservationData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT", "John", "Smith",
				TravellerType.PASSENGER.getCode());
		final TravellerData travellerDataChild1 = testDataSetUp.createTravellerData("5678", "CHILD", "Jade", "Smith",
				TravellerType.PASSENGER.getCode());
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1, travellerDataChild1).collect(Collectors.toList()), null);
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, itineraryData);
		final ReservationData reservationData = testDataSetUp.createReservationData("0001",
				Stream.of(reservationItem).collect(Collectors.toList()), null);

		//prepare AccommodationreservationData
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		globalReservationData.setReservationData(reservationData);
		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(Collections.EMPTY_LIST, null);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalReservationData.setAccommodationReservationData(accommodationReservationData);

		//Create OrderModel
		final UserModel user = testDataSetUp.createCustomerModel("john.smith@abc.com", null);
		final OrderModel order = testDataSetUp.createOrderModel("0001", user);

		when(reservationFacade.getGlobalTravelReservationData(order)).thenReturn(globalReservationData);


		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);
		when(reservationFacade.getReservationData(order)).thenReturn(reservationData);
		when(userService.isAnonymousUser(user)).thenReturn(false);
		final String emailId = bookingFacade.validateAndReturnBookerEmailId("0001", "Smith");
		assertEquals("john.smith@abc.com", emailId);
	}

	@Test
	public void testValidateAndReturnBookerEmailIdNameDontMatch()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//prepare ReservationData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT", "John", "Smith",
				TravellerType.PASSENGER.getCode());
		final TravellerData travellerDataChild1 = testDataSetUp.createTravellerData("5678", "CHILD", "Jade", "Smith",
				TravellerType.PASSENGER.getCode());
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1, travellerDataChild1).collect(Collectors.toList()), null);
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, itineraryData);
		final ReservationData reservationData = testDataSetUp.createReservationData("0001",
				Stream.of(reservationItem).collect(Collectors.toList()), null);

		//prepare AccommodationreservationData
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		globalReservationData.setReservationData(reservationData);
		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(Collections.EMPTY_LIST, null);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalReservationData.setAccommodationReservationData(accommodationReservationData);

		//Create OrderModel
		final UserModel user = testDataSetUp.createUserModel("123456|john.smith@abc.com");
		final OrderModel order = testDataSetUp.createOrderModel("0001", user);

		when(reservationFacade.getGlobalTravelReservationData(order)).thenReturn(globalReservationData);

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);
		when(reservationFacade.getReservationData(order)).thenReturn(reservationData);
		when(userService.isAnonymousUser(user)).thenReturn(true);
		final String emailId = bookingFacade.validateAndReturnBookerEmailId("0001", "Brown");
		assertNull(emailId);
	}

	@Test
	public void testValidateAndReturnBookerEmailIdNonPassenger()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//prepare ReservationData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", null, null, null,
				TravellerType.VEHICLE.getCode());
		final TravellerData travellerDataChild1 = testDataSetUp.createTravellerData("5678", null, null, null,
				TravellerType.PET.getCode());
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1, travellerDataChild1).collect(Collectors.toList()), null);
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, itineraryData);
		final ReservationData reservationData = testDataSetUp.createReservationData("0001",
				Stream.of(reservationItem).collect(Collectors.toList()), null);

		//prepare AccommodationreservationData
		final GlobalTravelReservationData globalReservationData = new GlobalTravelReservationData();
		globalReservationData.setReservationData(reservationData);
		final ReservedRoomStayData reservedRoomStayData = testDataSetUp.createRoomStayData(Collections.EMPTY_LIST, null);
		final AccommodationReservationData accommodationReservationData = testDataSetUp.getAccommodationReservationData("0001",
				Stream.of(reservedRoomStayData).collect(Collectors.toList()), null);
		globalReservationData.setAccommodationReservationData(accommodationReservationData);


		//Create OrderModel
		final UserModel user = testDataSetUp.createUserModel("123456|john.smith@abc.com");
		final OrderModel order = testDataSetUp.createOrderModel("0001", user);

		when(reservationFacade.getGlobalTravelReservationData(order)).thenReturn(globalReservationData);

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);
		when(reservationFacade.getReservationData(order)).thenReturn(reservationData);
		when(userService.isAnonymousUser(user)).thenReturn(true);
		final String emailId = bookingFacade.validateAndReturnBookerEmailId("0001", "Brown");
		assertNull(emailId);
	}

	@Test
	public void testCreateRefundPaymentTransaction()
	{
		//TODO : mock a list of abstract Order entries to make thist test more meaningful
		final CartModel cart = new CartModel();
		final CartEntryModel cartEntry = new CartEntryModel();
		cart.setEntries(Stream.of(cartEntry).collect(Collectors.toList()));
		when(travelCartService.getSessionCart()).thenReturn(cart);
		when(bookingService.createRefundPaymentTransaction(Matchers.any(AbstractOrderModel.class), Matchers.any(BigDecimal.class),
				Stream.of(Matchers.any(AbstractOrderEntryModel.class)).collect(Collectors.toList()))).thenReturn(true);
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.TEN);
		final boolean refund = bookingFacade.createRefundPaymentTransaction(priceData);
		assertTrue(refund);
	}

	@Test
	public void testAmendOrder()
	{
		final CartModel cart = new CartModel();
		when(travelCartService.createCartFromOrder(Matchers.anyString(), Matchers.anyString())).thenReturn(cart);
		Mockito.doNothing().when(travelCartService).setSessionCart(cart);
		final boolean amended = bookingFacade.amendOrder("0001", "123456|john.smith@abc.com");
		assertTrue(amended);
	}

	@Test
	public void testAmendOrderErrorCreatingCart()
	{
		when(travelCartService.createCartFromOrder(Matchers.anyString(), Matchers.anyString())).thenReturn(null);
		final boolean amended = bookingFacade.amendOrder("0001", "123456|john.smith@abc.com");
		assertFalse(amended);
	}

	@Test
	public void testBeginTravellerCancellation()
	{
		final CartModel cart = new CartModel();
		when(travelCartService.cancelTraveller("0001", "123456", "11111", "123456789")).thenReturn(cart);
		Mockito.doNothing().when(travelCartService).setSessionCart(cart);
		final boolean cancelTraveller = bookingFacade.beginTravellerCancellation("0001", "123456", "11111", "123456789");
		assertTrue(cancelTraveller);
	}

	@Test
	public void testBeginTravellerCancellationNoCartCreated()
	{
		when(travelCartService.cancelTraveller("0001", "123456", "11111", "123456789")).thenReturn(null);
		Mockito.doNothing().when(travelCartService).setSessionCart(null);
		final boolean cancelTraveller = bookingFacade.beginTravellerCancellation("0001", "123456", "11111", "123456789");
		assertFalse(cancelTraveller);
	}

	@Test
	public void testGetTotalToPay()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PriceData priceData = testDataSetUp.createPriceData(5d);
		final ReservationData reservationData = testDataSetUp.createReservationData("0001", null, null);
		reservationData.setTotalToPay(priceData);
		when(reservationFacade.getCurrentReservationData()).thenReturn(reservationData);
		final PriceData priceDataReturned = bookingFacade.getTotalToPay();
		assertEquals(priceData, priceDataReturned);
	}

	@Test
	public void testCancelTravellerTotalToPayMoreThanZero()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PriceData priceData = testDataSetUp.createPriceData(5d);
		final boolean cancelTraveller = bookingFacade.cancelTraveller(priceData);
		assertFalse(cancelTraveller);
	}

	@Test
	public void testCancelTravellerNoTotalToPay()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PriceData priceData = testDataSetUp.createPriceData(0d);
		when(bookingService.cancelTraveller(priceData.getValue())).thenReturn(false);
		final boolean cancelTraveller = bookingFacade.cancelTraveller(priceData);
		assertTrue(cancelTraveller);
	}

	@Test
	public void testCancelTravellerValidCart() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PriceData priceData = testDataSetUp.createPriceData(0d);
		when(bookingService.cancelTraveller(priceData.getValue())).thenReturn(true);
		when(checkoutFacade.placeOrder()).thenReturn(new OrderData());
		final boolean cancelTraveller = bookingFacade.cancelTraveller(priceData);
		assertTrue(cancelTraveller);
	}

	@Test
	public void testCancelTravellerInValidCart() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PriceData priceData = testDataSetUp.createPriceData(0d);
		when(bookingService.cancelTraveller(priceData.getValue())).thenReturn(true);
		when(checkoutFacade.placeOrder()).thenThrow(InvalidCartException.class);
		final boolean cancelTraveller = bookingFacade.cancelTraveller(priceData);
		assertFalse(cancelTraveller);
	}

	@Test
	public void testNewCancelTravellerTotalToPayMoreThanZero()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravellerData travellerData = new TravellerData();
		final PriceData totalToPay = testDataSetUp.createPriceData(10d);
		final PriceData priceData = testDataSetUp.createPriceData(100d);
		final boolean cancelTraveller = bookingFacade.cancelTraveller(totalToPay, priceData, travellerData);
		assertFalse(cancelTraveller);
	}

	@Test
	public void testNewCancelTravellerTotalToPayMoreThanZeroAndNullTravellerData()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PriceData totalToPay = testDataSetUp.createPriceData(10d);
		final PriceData priceData = testDataSetUp.createPriceData(100d);
		final boolean cancelTraveller = bookingFacade.cancelTraveller(totalToPay, priceData, null);
		assertFalse(cancelTraveller);
	}

	@Test
	public void testNewCancelTravellerNoTotalToPay()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravellerData travellerData = new TravellerData();
		final PriceData totalToPay = testDataSetUp.createPriceData(0d);
		final PriceData priceData = testDataSetUp.createPriceData(10d);
		when(bookingService.cancelTraveller(priceData.getValue(), travellerData)).thenReturn(false);
		final boolean cancelTraveller = bookingFacade.cancelTraveller(totalToPay, priceData, travellerData);
		assertTrue(cancelTraveller);
	}

	@Test
	public void testNewCancelTravellerValidCart() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravellerData travellerData = new TravellerData();
		final PriceData totalToPay = testDataSetUp.createPriceData(0d);
		final PriceData priceData = testDataSetUp.createPriceData(100d);
		when(bookingService.cancelTraveller(priceData.getValue(), travellerData)).thenReturn(true);
		when(checkoutFacade.placeOrder()).thenReturn(new OrderData());
		final boolean cancelTraveller = bookingFacade.cancelTraveller(totalToPay, priceData, travellerData);
		assertTrue(cancelTraveller);
	}

	@Test
	public void testNewCancelTravellerInValidCart() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravellerData travellerData = new TravellerData();
		final PriceData totalToPay = testDataSetUp.createPriceData(0d);
		final PriceData priceData = testDataSetUp.createPriceData(100d);
		when(bookingService.cancelTraveller(priceData.getValue(), travellerData)).thenReturn(true);
		when(checkoutFacade.placeOrder()).thenThrow(InvalidCartException.class);
		final boolean cancelTraveller = bookingFacade.cancelTraveller(totalToPay, priceData, travellerData);
		assertFalse(cancelTraveller);
	}



	@Test
	public void testGetCurrentUserUidLoggedInCustomer()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final UserModel user = testDataSetUp.createUserModel("123456|john.smith@abc.com");
		when(userService.isAnonymousUser(user)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(user);
		final String uid = bookingFacade.getCurrentUserUid();
		assertEquals("123456|john.smith@abc.com", uid);
	}

	@Test
	public void testGetCurrentUserUidAnonymousUser()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final UserModel user = testDataSetUp.createUserModel("123456|john.smith@abc.com");
		when(userService.isAnonymousUser(user)).thenReturn(true);
		when(userService.getCurrentUser()).thenReturn(user);
		when(sessionService.getAttribute("manage_my_booking_guest_uid")).thenReturn(user.getUid());
		final String uid = bookingFacade.getCurrentUserUid();
		assertEquals("123456|john.smith@abc.com", uid);
	}

	@Test
	public void testAtleastOneAdultTravellerRemaining()
	{
		when(bookingService.atleastOneAdultTravellerRemaining("0001", "12345")).thenReturn(true);
		final boolean atleastOneTravellerRemaining = bookingFacade.atleastOneAdultTravellerRemaining("0001", "12345");
		assertTrue(atleastOneTravellerRemaining);
	}

	@Test
	public void testIsAmendment()
	{
		final OrderModel originalOrder = new OrderModel();
		originalOrder.setCode("0001");

		final OrderModel order = new OrderModel();
		order.setOriginalOrder(originalOrder);
		order.setStatus(OrderStatus.AMENDMENTINPROGRESS);
		when(bookingService.getOrder("0001")).thenReturn(order);

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);

		assertTrue(bookingFacade.isAmendment("0001"));
	}

	@Test
	public void testIsAmendmentForActiveStatus()
	{
		final OrderModel originalOrder = new OrderModel();
		originalOrder.setCode("0001");
		originalOrder.setStatus(OrderStatus.AMENDED);
		final OrderModel order = new OrderModel();
		order.setStatus(OrderStatus.ACTIVE);
		final OrderHistoryEntryModel historyEntry = new OrderHistoryEntryModel();
		historyEntry.setPreviousOrderVersion(originalOrder);
		order.setHistoryEntries(Collections.emptyList());
		when(bookingService.getOrder("0001")).thenReturn(order);

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);

		assertFalse(bookingFacade.isAmendment("0001"));


		final OrderModel originalOrder2 = new OrderModel();
		originalOrder2.setCode("0002");

		final OrderModel order2 = new OrderModel();
		order2.setOriginalOrder(originalOrder2);
		order2.setStatus(OrderStatus.ACTIVE);
		final OrderHistoryEntryModel orderHistoryModel = new OrderHistoryEntryModel();
		orderHistoryModel.setPreviousOrderVersion(order);
		order2.setHistoryEntries(Arrays.asList(orderHistoryModel));
		when(bookingService.getOrder("0002")).thenReturn(order2);

		when(bookingService.getOrderModelFromStore("0002")).thenReturn(order2);

		assertTrue(bookingFacade.isAmendment("0002"));

		final OrderModel originalOrder3 = new OrderModel();
		originalOrder3.setCode("0003");

		final OrderModel order3 = new OrderModel();
		order3.setOriginalOrder(originalOrder3);
		order3.setStatus(OrderStatus.ACTIVE);
		order3.setHistoryEntries(Arrays.asList(new OrderHistoryEntryModel()));
		when(bookingService.getOrder("0003")).thenReturn(order3);

		when(bookingService.getOrderModelFromStore("0003")).thenReturn(order3);

		assertFalse(bookingFacade.isAmendment("0003"));
	}

	@Test
	public void testValidateUserForBookingAnonymousUser()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final UserModel user = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);
		final CustomerModel customer = testDataSetUp.createCustomerModel("john.smith@abc.com", null);
		when(userService.isAnonymousUser(user)).thenReturn(true);
		when(userService.getCurrentUser()).thenReturn(user);
		final OrderModel originalOrder = new OrderModel();
		originalOrder.setCode("0001");
		final OrderModel order = new OrderModel();
		order.setOriginalOrder(originalOrder);
		order.setCode("0001");
		when(bookingService.getOrderModelFromStore("00001")).thenReturn(order);
		final OrderUserAccountMappingModel orderUserMap = new OrderUserAccountMappingModel();
		orderUserMap.setUser(customer);
		orderUserMap.setOrderCode(order.getCode());
		when(userService.getCurrentUser()).thenReturn(customer);
		when(customerAccountService.getOrderUserMapping(order.getCode(), customer)).thenReturn(orderUserMap);
		assertTrue(bookingFacade.validateUserForBooking("00001"));
	}

	@Test
	public void testValidateUserForBookingInvalidBooking() throws ParseException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("john.smith@abc.com", null);
		when(userService.isAnonymousUser(customer)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(customer);
		final OrderModel orderModel = testDataSetUp.createOrderModel("00002", null);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null))
				.thenReturn(Stream.of(orderModel).collect(Collectors.toList()));
		final TransportOfferingData to1 = testDataSetUp.createTransportOfferingData("EZY1111070320160735", "07/03/2016 07:35:00");
		final OriginDestinationOptionData odOptions1 = testDataSetUp
				.createOriginDestinationOptionData(Stream.of(to1).collect(Collectors.toList()));
		final ItineraryData itinerary1 = testDataSetUp.createItineraryData(null,
				Stream.of(odOptions1).collect(Collectors.toList()));
		final ReservationItemData resItemData1 = testDataSetUp.createReservationItemData(1, itinerary1);
		final ReservationData resData1 = testDataSetUp.createReservationData("00002",
				Stream.of(resItemData1).collect(Collectors.toList()), OrderStatus.ACTIVE.getCode());
		when(reservationItemPipelineManager.executePipeline(orderModel)).thenReturn(resData1);
		assertFalse(bookingFacade.validateUserForBooking("00001"));
	}

	@Test
	public void testValidateUserForBookingValidBooking() throws ParseException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("john.smith@abc.com", null);
		when(userService.isAnonymousUser(customer)).thenReturn(false);
		when(userService.getCurrentUser()).thenReturn(customer);
		final OrderModel orderModel = testDataSetUp.createOrderModel("00001", null);
		when(bookingService.getOrderModelFromStore("00001")).thenReturn(orderModel);

		final OrderUserAccountMappingModel orderUserMap = new OrderUserAccountMappingModel();
		orderUserMap.setUser(customer);
		orderUserMap.setOrderCode(orderModel.getCode());
		when(customerAccountService.getOrderUserMapping(orderModel.getCode(), customer)).thenReturn(orderUserMap);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null))
				.thenReturn(Stream.of(orderModel).collect(Collectors.toList()));
		final TransportOfferingData to1 = testDataSetUp.createTransportOfferingData("EZY1111070320160735", "07/03/2016 07:35:00");
		final OriginDestinationOptionData odOptions1 = testDataSetUp
				.createOriginDestinationOptionData(Stream.of(to1).collect(Collectors.toList()));
		final ItineraryData itinerary1 = testDataSetUp.createItineraryData(null,
				Stream.of(odOptions1).collect(Collectors.toList()));
		final ReservationItemData resItemData1 = testDataSetUp.createReservationItemData(1, itinerary1);
		final ReservationData resData1 = testDataSetUp.createReservationData("00001",
				Stream.of(resItemData1).collect(Collectors.toList()), OrderStatus.ACTIVE.getCode());
		when(reservationItemPipelineManager.executePipeline(orderModel)).thenReturn(resData1);
		assertTrue(bookingFacade.validateUserForBooking("00001"));
	}

	@Test
	public void testGetFullAccommodationBooking()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		final AccommodationReservationData accommodationReservationDataExp = new AccommodationReservationData();
		bookingFacade.setFullAccommodationReservationPipelineManager(fullAccommodationReservationPipelineManager);

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(fullAccommodationReservationPipelineManager.executePipeline(orderModel)).thenReturn(accommodationReservationDataExp);
		final AccommodationReservationData accommodationReservationDataAct = bookingFacade.getFullAccommodationBooking("0001");

		assertEquals(accommodationReservationDataExp, accommodationReservationDataAct);

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(null);
		final AccommodationReservationData accommodationReservationDataNull = bookingFacade.getFullAccommodationBooking("0001");
		assertNull(accommodationReservationDataNull);
	}

	@Test
	public void testGetGlobalTravelReservationData()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(globalTravelReservationData);

		final GlobalTravelReservationData globalTravelReservationDataAct = bookingFacade.getGlobalTravelReservationData("0001");
		assertEquals(globalTravelReservationData, globalTravelReservationDataAct);
	}

	@Test
	public void testGetBasicAccommodationBookingFromCart()
	{
		bookingFacade.getBasicAccommodationBookingFromCart();

		when(travelCartService.hasSessionCart()).thenReturn(true);
		final AccommodationReservationData accommodationReservationData = bookingFacade.getBasicAccommodationBookingFromCart();
		assertNull(accommodationReservationData);
	}

	//	getBookerEmailID

	@Test
	public void testGetGuestOccupanciesFromCart()
	{
		bookingFacade.setGuestOccupancyConverter(guestOccupancyConverter);
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = Mockito
				.mock(AccommodationOrderEntryGroupModel.class);
		final AccommodationModel accommodation = Mockito.mock(AccommodationModel.class);

		final GuestOccupancyModel guestOccupancyModel = new GuestOccupancyModel();
		final List<GuestOccupancyModel> guestOccupancyModels = new ArrayList<GuestOccupancyModel>();
		guestOccupancyModels.add(guestOccupancyModel);
		final RatePlanModel ratePlan = Mockito.mock(RatePlanModel.class);

		when(bookingService.getAccommodationOrderEntryGroup(Matchers.anyInt(), Matchers.anyObject()))
				.thenReturn(accommodationOrderEntryGroup);
		when(accommodationOrderEntryGroup.getAccommodation()).thenReturn(accommodation);
		when(accommodationOrderEntryGroup.getRatePlan()).thenReturn(ratePlan);
		when(accommodation.getGuestOccupancies()).thenReturn(guestOccupancyModels);
		when(ratePlan.getGuestOccupancies()).thenReturn(null);

		assertNotNull(bookingFacade.getGuestOccupanciesFromCart(1));
	}

	@Test
	public void testGetGuestOccupanciesFromCartForNullOrderEntryGroup()
	{
		when(bookingService.getAccommodationOrderEntryGroup(Matchers.anyInt(), Matchers.anyObject())).thenReturn(null);
		assertTrue(CollectionUtils.isEmpty(bookingFacade.getGuestOccupanciesFromCart(1)));
	}

	@Test
	public void testAmendAddRoom()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		when(travelCartService.createCartFromOrder(Matchers.anyString(), Matchers.anyString())).thenReturn(cart);
		final UserService us = Mockito.mock(UserService.class, Mockito.RETURNS_DEEP_STUBS);
		bookingFacade.setUserService(us);
		when(us.getCurrentUser().getUid()).thenReturn("deep");
		when(bookingFacade.getCurrentUserUid()).thenReturn("testUid");

		final boolean testAmendAddRoom = bookingFacade.amendAddRoom("0001");
		assertTrue(testAmendAddRoom);
	}

	@Test
	public void testAmendAddRoomForNullCart()
	{
		when(travelCartService.createCartFromOrder(Matchers.anyString(), Matchers.anyString())).thenReturn(null);
		final UserService us = Mockito.mock(UserService.class, Mockito.RETURNS_DEEP_STUBS);
		bookingFacade.setUserService(us);
		when(us.getCurrentUser().getUid()).thenReturn("deep");
		when(bookingFacade.getCurrentUserUid()).thenReturn("testUid");

		final boolean testAmendAddRoom = bookingFacade.amendAddRoom("0001");

		assertFalse(testAmendAddRoom);
	}

	@Test
	public void testBuildAccommodationDetailsQueryFromCart()
	{
		final Map<String, String> params = new HashMap<String, String>();
		params.put(TravelservicesConstants.ACCOMMODATION_OFFERING_CODE, "HOTEL_TEST_CODE");
		params.put(TravelservicesConstants.CHECK_IN_DATE_TIME, "22/12/2016");
		params.put(TravelservicesConstants.CHECK_OUT_DATE_TIME, "24/12/2016");

		final CartModel cart = new CartModel();
		when(travelCartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationDetailsParameters(cart)).thenReturn(params);

		final String accommodationDetailsQueryFromcart = bookingFacade.buildAccommodationDetailsQueryFromCart();

		final String accommodationDetailsQueryFromcartExp = "HOTEL_TEST_CODE" + "?" + TravelservicesConstants.CHECK_IN_DATE_TIME
				+ "=" + "22/12/2016" + "&" + TravelservicesConstants.CHECK_OUT_DATE_TIME + "=" + "24/12/2016" + "&numberOfRooms=1"
				+ "&r1=1-adult,0-child," + "0-infant";

		assertEquals(accommodationDetailsQueryFromcartExp, accommodationDetailsQueryFromcart);
	}

	@Test
	public void testGetNewReservedRoomStays()
	{
		final AccommodationReservationData accommodationReservationdata = new AccommodationReservationData();
		when(travelCartService.hasSessionCart()).thenReturn(true);
		final List<ReservedRoomStayData> reservedRoomStayDataEmpty = bookingFacade.getNewReservedRoomStays();
		assertTrue(CollectionUtils.isEmpty(reservedRoomStayDataEmpty));

		final CartModel cart = new CartModel();
		when(travelCartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getNewAccommodationOrderEntryGroupRefs(cart)).thenReturn(Collections.emptyList());
		final List<ReservedRoomStayData> reservedRoomStayDataEmpty1 = bookingFacade.getNewReservedRoomStays();
		assertTrue(CollectionUtils.isEmpty(reservedRoomStayDataEmpty1));

		final List<Integer> newAccommodationOrderEntryGroupRefs = new ArrayList<>();
		newAccommodationOrderEntryGroupRefs.add(1);
		final List<ReservedRoomStayData> roomStays = new ArrayList<>();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(1);
		roomStays.add(roomStay);

		accommodationReservationdata.setRoomStays(roomStays);
		when(bookingService.getNewAccommodationOrderEntryGroupRefs(cart)).thenReturn(newAccommodationOrderEntryGroupRefs);
		when(basicAccommodationReservationPipelineManager.executePipeline(cart)).thenReturn(accommodationReservationdata);

		final List<ReservedRoomStayData> reservedRoomStayDataExp = bookingFacade.getNewReservedRoomStays();
		assertNotNull(reservedRoomStayDataExp);
	}

	@Test
	public void testGetOldReservedRoomStays()
	{
		final AccommodationReservationData accommodationReservationdata = new AccommodationReservationData();
		when(travelCartService.hasSessionCart()).thenReturn(true);
		final List<ReservedRoomStayData> reservedRoomStayDataEmpty = bookingFacade.getOldReservedRoomStays();
		assertTrue(CollectionUtils.isEmpty(reservedRoomStayDataEmpty));

		final CartModel cart = new CartModel();
		when(travelCartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getOldAccommodationOrderEntryGroupRefs(cart)).thenReturn(Collections.emptyList());
		final List<ReservedRoomStayData> reservedRoomStayDataEmpty1 = bookingFacade.getOldReservedRoomStays();
		assertTrue(CollectionUtils.isEmpty(reservedRoomStayDataEmpty1));

		final List<Integer> oldAccommodationOrderEntryGroupRefs = new ArrayList<>();
		oldAccommodationOrderEntryGroupRefs.add(1);
		final List<ReservedRoomStayData> roomStays = new ArrayList<>();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(1);
		roomStays.add(roomStay);

		accommodationReservationdata.setRoomStays(roomStays);
		when(bookingService.getOldAccommodationOrderEntryGroupRefs(cart)).thenReturn(oldAccommodationOrderEntryGroupRefs);
		when(basicAccommodationReservationPipelineManager.executePipeline(cart)).thenReturn(accommodationReservationdata);
		List<ReservedRoomStayData> reservedRoomStayDataExp = bookingFacade.getOldReservedRoomStays();
		assertTrue(CollectionUtils.isNotEmpty(reservedRoomStayDataExp));
		when(bookingService.getOldAccommodationOrderEntryGroupRefs(cart)).thenReturn(Collections.emptyList());
		when(basicAccommodationReservationPipelineManager.executePipeline(cart)).thenReturn(accommodationReservationdata);

		reservedRoomStayDataExp = bookingFacade.getOldReservedRoomStays();
		assertTrue(CollectionUtils.isEmpty(reservedRoomStayDataExp));
	}

	@Test
	public void testUpdateAccommodationOrderEntryGroup()
	{
		final GuestData guestData = Mockito.mock(GuestData.class);
		final ProfileData pd = new ProfileData();
		pd.setContactNumber("353646");
		pd.setEmail("test@test.com");
		pd.setFirstName("first");
		pd.setLastName("last");

		final PassengerTypeQuantityData psqd = new PassengerTypeQuantityData();
		psqd.setQuantity(1);
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT);
		psqd.setPassengerType(passengerType);
		final List<PassengerTypeQuantityData> passengerTypeQuantityData = Collections.singletonList(psqd);
		final List<String> roomPreferenceCodes = Collections.singletonList("ROOM_PREF_CODE");
		final String checkInTime = "16/12/2016 12:00:00";
		final CartModel cart = new CartModel();
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		final GuestCountModel guestCountModel = new GuestCountModel();
		final RoomPreferenceModel roomPreference = Mockito.mock(RoomPreferenceModel.class);
		final List<RoomPreferenceModel> roomPreferences = Collections.singletonList(roomPreference);

		when(guestData.getProfile()).thenReturn(pd);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationOrderEntryGroup(0, cart)).thenReturn(accommodationOrderEntryGroupModel);
		when(guestCountService.getGuestCount(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT, 1)).thenReturn(guestCountModel);
		when(guestCountReverseConverter.convert(psqd)).thenReturn(guestCountModel);
		when(roomPreferenceService.getRoomPreferences("ROOM_PREF_CODE")).thenReturn(roomPreferences);

		Mockito.doThrow(ModelSavingException.class).when(modelService).save(accommodationOrderEntryGroupModel);

		final boolean updateAccOEFalse = bookingFacade.updateAccommodationOrderEntryGroup(0, guestData, passengerTypeQuantityData,
				roomPreferenceCodes, checkInTime);
		assertFalse(updateAccOEFalse);


		Mockito.doNothing().when(modelService).save(accommodationOrderEntryGroupModel);

		final boolean updateAccOE = bookingFacade.updateAccommodationOrderEntryGroup(0, guestData, passengerTypeQuantityData,
				roomPreferenceCodes, checkInTime);
		assertTrue(updateAccOE);
	}

	@Test
	public void testValidateB2BUser()
	{
		final CustomerModel cm = new CustomerModel();
		final OrderModel om = new OrderModel();

		assertFalse(bookingFacade.validateB2BUser(cm, om));
		final B2BCustomerModel b2bcm = new B2BCustomerModel();
		assertFalse(bookingFacade.validateB2BUser(b2bcm, om));

		final B2BUnitModel b2BUnitModel = Mockito.spy(new B2BUnitModel());
		b2BUnitModel.setUid("b2badmingroup");
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		groups.add(b2BUnitModel);
		b2bcm.setGroups(groups);
		final B2BUnitModel um = Mockito.spy(new B2BUnitModel());
		om.setUnit(um);

		when(b2BUnitModel.getPk()).thenReturn(PK.fromLong(00001l));
		when(um.getPk()).thenReturn(PK.fromLong(00001l));
		assertTrue(bookingFacade.validateB2BUser(b2bcm, om));
	}

	@Test
	public void testValidateUserForCheckout()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		orderModel.setAdditionalSecurity(false);
		final CustomerModel user = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", CustomerType.GUEST);

		when(userService.getCurrentUser()).thenReturn(user);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		assertFalse(bookingFacade.validateUserForCheckout("0001"));

		when(travelCheckoutCustomerStrategy.isAnonymousCheckout()).thenReturn(true);
		assertFalse(bookingFacade.validateUserForCheckout("0001"));

		when(customerAccountService.getOrderUserMapping("0001", user)).thenReturn(new OrderUserAccountMappingModel());
		assertTrue(bookingFacade.validateUserForCheckout("0001"));
	}

	@Test
	public void testValidateUserForAdditionalSecurityAmendmentCheckout()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		orderModel.setAdditionalSecurity(true);
		when(sessionService.getAttribute(Matchers.anyString())).thenReturn("KJHDS7");
		when(bookingService.isValidPassengerReference(Matchers.any(), Matchers.anyString())).thenReturn(true);
		final CustomerModel user = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", CustomerType.GUEST);

		when(userService.getCurrentUser()).thenReturn(user);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		assertTrue(bookingFacade.validateUserForCheckout("0001"));
	}

	@Test
	public void testGetBookingByBookingReferenceAndAmendingOrder()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		orderModel.setStatus(OrderStatus.AMENDMENTINPROGRESS);
		final ReservationData reservationDataExp = new ReservationData();

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(bookingService.getOrderModelByOriginalOrderCode("0001")).thenReturn(orderModel);
		when(reservationFacade.getReservationData(orderModel)).thenReturn(reservationDataExp);
		final ReservationData reservationData = bookingFacade.getBookingByBookingReferenceAndAmendingOrder("0001");
		assertEquals(reservationDataExp, reservationData);
	}

	@Test
	public void testGetFullAccommodationBookingForAmendOrder()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		orderModel.setStatus(OrderStatus.AMENDMENTINPROGRESS);
		final AccommodationReservationData reservationDataExp = new AccommodationReservationData();
		assertNull(bookingFacade.getFullAccommodationBookingForAmendOrder("0001"));

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(bookingService.getOrderModelByOriginalOrderCode("0001")).thenReturn(orderModel);
		when(fullAccommodationReservationPipelineManager.executePipeline(orderModel)).thenReturn(reservationDataExp);
		final AccommodationReservationData reservationData = bookingFacade.getFullAccommodationBookingForAmendOrder("0001");
		assertEquals(reservationDataExp, reservationData);
	}

	@Test
	public void testGetBookingTotal()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		orderModel.setStatus(OrderStatus.AMENDMENTINPROGRESS);
		orderModel.setTotalPrice(100.00);
		orderModel.setNet(true);
		orderModel.setTotalTax(50.0);
		final CurrencyModel cm = new CurrencyModel();
		cm.setIsocode("GBP");
		orderModel.setCurrency(cm);
		final PriceData pd = new PriceData();
		pd.setValue(BigDecimal.valueOf(150.0));

		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(bookingService.getOrderModelByOriginalOrderCode("0001")).thenReturn(orderModel);
		when(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).thenReturn(pd);

		assertTrue(bookingFacade.getBookingTotal("0001").equals(pd));
	}

	@Test
	public void testGetNextScheduledTransportOfferingData()
	{
		when(timeService.getCurrentTime()).thenReturn(new Date());
		assertNull(bookingFacade.getNextScheduledTransportOfferingData());

		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CustomerModel customer = testDataSetUp.createCustomerModel("123456|john.smith@abc.com", null);

		when(userService.getCurrentUser()).thenReturn(customer);
		assertNull(bookingFacade.getNextScheduledTransportOfferingData());

		final OrderModel orderModel = testDataSetUp.createOrderModel("0001", null);
		final GlobalTravelReservationData reservationDataExp = new GlobalTravelReservationData();
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderList(customer, baseStoreModel, null)).thenReturn(Collections.singletonList(orderModel));
		when(basicGlobalTravelReservationPipelineManager.executePipeline(orderModel)).thenReturn(reservationDataExp);
		orderModel.setStatus(OrderStatus.CANCELLED);
		assertNull(bookingFacade.getNextScheduledTransportOfferingData());

		orderModel.setStatus(OrderStatus.ACTIVE);
		assertNull(bookingFacade.getNextScheduledTransportOfferingData());

		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItem = new ReservationItemData();
		final ItineraryData itineraryData = new ItineraryData();
		final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setStatus("SCHEDULED");
		final Date departureDateTime = new Date();
		departureDateTime.setDate(departureDateTime.getDate() + 1);
		transportOffering.setDepartureTime(departureDateTime);
		transportOffering.setDepartureTimeZoneId(ZoneId.systemDefault());

		originDestinationOption.setTransportOfferings(Collections.singletonList(transportOffering));
		itineraryData.setOriginDestinationOptions(Collections.singletonList(originDestinationOption));
		reservationItem.setReservationItinerary(itineraryData);
		reservationData.setReservationItems(Collections.singletonList(reservationItem));
		reservationDataExp.setReservationData(reservationData);

		final TransportOfferingData td = bookingFacade.getNextScheduledTransportOfferingData();
		assertEquals(transportOffering, td);
	}

	@Test
	public void testGetDisruptedReservation()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel orderModel = Mockito.spy(testDataSetUp.createOrderModel("0001", null));
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		assertNull(bookingFacade.getDisruptedReservation("0001"));

		final OrderHistoryEntryModel orderHistoryEntryModel = new OrderHistoryEntryModel();
		orderModel.setHistoryEntries(Collections.singletonList(orderHistoryEntryModel));
		assertNull(bookingFacade.getDisruptedReservation("0001"));


		final OrderModel previousOrderModel = new OrderModel();
		orderHistoryEntryModel.setPreviousOrderVersion(previousOrderModel);
		final ReservationData disruptedReservation = new ReservationData();
		final DisruptionModel disruption = new DisruptionModel();
		disruption.setOriginDestinationRefNumber(0);

		when(reservationFacade.getReservationData(previousOrderModel)).thenReturn(disruptedReservation);
		assertNull(bookingFacade.getDisruptedReservation("0001"));


		final ReservationItemData reservationItem = new ReservationItemData();
		final ItineraryData itineraryData = new ItineraryData();
		final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
		final TransportOfferingData transportOffering = new TransportOfferingData();
		originDestinationOption.setTransportOfferings(Collections.singletonList(transportOffering));
		itineraryData.setOriginDestinationOptions(Collections.singletonList(originDestinationOption));
		reservationItem.setReservationItinerary(itineraryData);
		reservationItem.setOriginDestinationRefNumber(0);
		disruptedReservation.setReservationItems(Collections.singletonList(reservationItem));

		when(orderModel.getDisruptions()).thenReturn(Collections.singletonList(disruption));
		assertEquals(disruptedReservation, bookingFacade.getDisruptedReservation("0001"));

	}

	@Test
	public void testChangeDatesForAccommodationBookingForEmptyRoomStays()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponse.setRoomStays(Collections.emptyList());
		assertFalse(bookingFacade.changeDatesForAccommodationBooking(StringUtils.EMPTY, StringUtils.EMPTY,
				accommodationAvailabilityResponse, null));
		accommodationAvailabilityResponse.setRoomStays(Collections.emptyList());
		assertFalse(bookingFacade.changeDatesForAccommodationBooking(StringUtils.EMPTY, StringUtils.EMPTY, null, null));

	}

	@Test
	public void testChangeDatesForAccommodationBookingForDeletingEntriesWithNoCart()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = testDataSetUp
				.createAccommodationAvailabilityResponse();

		when(travelCartService.getSessionCart()).thenReturn(null);
		assertFalse(bookingFacade.changeDatesForAccommodationBooking(StringUtils.EMPTY, StringUtils.EMPTY,
				accommodationAvailabilityResponse, null));
	}

	@Test
	public void testChangeDatesForAccommodationBookingForDeletingEntriesWithCartHavingNoEntries()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = testDataSetUp
				.createAccommodationAvailabilityResponse();

		final CartModel cart = new CartModel();
		when(travelCartService.getSessionCart()).thenReturn(cart);
		assertFalse(bookingFacade.changeDatesForAccommodationBooking(StringUtils.EMPTY, StringUtils.EMPTY,
				accommodationAvailabilityResponse, null));
	}

	@Test
	public void testChangeDatesForAccommodationBookingForEmptyChangeDatePaymentResults()
	{
		final String checkInDate = "15/02/2017";
		final String checkOutDate = "17/02/2017";

		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = testDataSetUp
				.createAccommodationAvailabilityResponse();

		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel entryActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryActiveRoomRateProduct.setActive(Boolean.TRUE);

		final AbstractOrderEntryModel entryInActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryInActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryInActiveRoomRateProduct.setActive(Boolean.FALSE);

		final AbstractOrderEntryModel entryActiveProduct = new AbstractOrderEntryModel();
		entryActiveProduct.setProduct(new ProductModel());
		entryActiveProduct.setActive(Boolean.TRUE);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entryActiveRoomRateProduct);
		entries.add(entryInActiveRoomRateProduct);
		entries.add(entryActiveProduct);
		cart.setEntries(entries);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.doNothing().when(modelService).save(Matchers.any());
		Mockito.doNothing().when(modelService).refresh(Matchers.any());
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModel.setCheckInTime(new Date());
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels = new ArrayList<>();
		accommodationOrderEntryGroupModels.add(accommodationOrderEntryGroupModel);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(CartModel.class)))
				.thenReturn(accommodationOrderEntryGroupModels);
		when(calculatePaymentTypeForChangeDatesStrategy.calculate(Matchers.any(AccommodationReservationData.class),
				Matchers.any(AccommodationAvailabilityResponseData.class))).thenReturn(Collections.emptyMap());
		assertFalse(bookingFacade.changeDatesForAccommodationBooking(checkInDate, checkOutDate, accommodationAvailabilityResponse,
				new AccommodationReservationData()));
	}

	@Test
	public void testChangeDatesForAccommodationBookingForAccommodationOrderEntryGroupWithDifferentRoomRefNum()
	{
		final String checkInDate = "15/02/2017";
		final String checkOutDate = "17/02/2017";

		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = testDataSetUp
				.createAccommodationAvailabilityResponse();

		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel entryActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryActiveRoomRateProduct.setActive(Boolean.TRUE);

		final AbstractOrderEntryModel entryInActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryInActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryInActiveRoomRateProduct.setActive(Boolean.FALSE);

		final AbstractOrderEntryModel entryActiveProduct = new AbstractOrderEntryModel();
		entryActiveProduct.setProduct(new ProductModel());
		entryActiveProduct.setActive(Boolean.TRUE);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entryActiveRoomRateProduct);
		entries.add(entryInActiveRoomRateProduct);
		entries.add(entryActiveProduct);
		cart.setEntries(entries);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.doNothing().when(modelService).save(Matchers.any());
		Mockito.doNothing().when(modelService).refresh(Matchers.any());
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModel.setCheckInTime(new Date());
		accommodationOrderEntryGroupModel.setRoomStayRefNumber(1);
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels = new ArrayList<>();
		accommodationOrderEntryGroupModels.add(accommodationOrderEntryGroupModel);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(CartModel.class)))
				.thenReturn(accommodationOrderEntryGroupModels);
		final Map<String, String> changeDatePaymentResults = new HashMap<>();
		changeDatePaymentResults.put(TravelservicesConstants.BOOKING_PAYABLE_STATUS,
				TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE);
		when(calculatePaymentTypeForChangeDatesStrategy.calculate(Matchers.any(AccommodationReservationData.class),
				Matchers.any(AccommodationAvailabilityResponseData.class))).thenReturn(changeDatePaymentResults);
		final List<RoomRateCartData> roomRates = new ArrayList<>();
		roomRates.add(new RoomRateCartData());
		when(accommodationCartFacade.collectRoomRates(Matchers.any(AccommodationAddToCartForm.class))).thenReturn(roomRates);
		assertFalse(bookingFacade.changeDatesForAccommodationBooking(checkInDate, checkOutDate, accommodationAvailabilityResponse,
				new AccommodationReservationData()));
	}

	@Test
	public void testChangeDatesForAccommodationBookingForErrorToAddRoomToCart()
	{
		final String checkInDate = "15/02/2017";
		final String checkOutDate = "17/02/2017";

		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = testDataSetUp
				.createAccommodationAvailabilityResponse();

		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel entryActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryActiveRoomRateProduct.setActive(Boolean.TRUE);

		final AbstractOrderEntryModel entryInActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryInActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryInActiveRoomRateProduct.setActive(Boolean.FALSE);

		final AbstractOrderEntryModel entryActiveProduct = new AbstractOrderEntryModel();
		entryActiveProduct.setProduct(new ProductModel());
		entryActiveProduct.setActive(Boolean.TRUE);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entryActiveRoomRateProduct);
		entries.add(entryInActiveRoomRateProduct);
		entries.add(entryActiveProduct);
		cart.setEntries(entries);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.doNothing().when(modelService).save(Matchers.any());
		Mockito.doNothing().when(modelService).refresh(Matchers.any());
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModel.setCheckInTime(new Date());
		accommodationOrderEntryGroupModel.setRoomStayRefNumber(0);
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels = new ArrayList<>();
		accommodationOrderEntryGroupModels.add(accommodationOrderEntryGroupModel);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(CartModel.class)))
				.thenReturn(accommodationOrderEntryGroupModels);
		final Map<String, String> changeDatePaymentResults = new HashMap<>();
		changeDatePaymentResults.put(TravelservicesConstants.BOOKING_PAYABLE_STATUS,
				TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE);
		when(calculatePaymentTypeForChangeDatesStrategy.calculate(Matchers.any(AccommodationReservationData.class),
				Matchers.any(AccommodationAvailabilityResponseData.class))).thenReturn(changeDatePaymentResults);
		final List<RoomRateCartData> roomRates = new ArrayList<>();
		roomRates.add(new RoomRateCartData());
		when(accommodationCartFacade.collectRoomRates(Matchers.any(AccommodationAddToCartForm.class))).thenReturn(roomRates);
		try
		{
			when(accommodationCartFacade.addAccommodationsToCart(Matchers.any(AccommodationOrderEntryGroupModel.class),
					Matchers.anyString(), Matchers.anyString(), Matchers.any(), Matchers.anyInt(), Matchers.anyString(),
					Matchers.anyString())).thenReturn(Boolean.FALSE);
		}
		catch (final CommerceCartModificationException e)
		{
			e.printStackTrace();
		}
		assertFalse(bookingFacade.changeDatesForAccommodationBooking(checkInDate, checkOutDate, accommodationAvailabilityResponse,
				new AccommodationReservationData()));
	}

	@Test
	public void testChangeDatesForAccommodationBookingForExcptionToAddRoomToCart() throws CommerceCartModificationException
	{
		final String checkInDate = "15/02/2017";
		final String checkOutDate = "17/02/2017";

		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = testDataSetUp
				.createAccommodationAvailabilityResponse();

		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel entryActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryActiveRoomRateProduct.setActive(Boolean.TRUE);

		final AbstractOrderEntryModel entryInActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryInActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryInActiveRoomRateProduct.setActive(Boolean.FALSE);

		final AbstractOrderEntryModel entryActiveProduct = new AbstractOrderEntryModel();
		entryActiveProduct.setProduct(new ProductModel());
		entryActiveProduct.setActive(Boolean.TRUE);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entryActiveRoomRateProduct);
		entries.add(entryInActiveRoomRateProduct);
		entries.add(entryActiveProduct);
		cart.setEntries(entries);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.doNothing().when(modelService).save(Matchers.any());
		Mockito.doNothing().when(modelService).refresh(Matchers.any());
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModel.setCheckInTime(new Date());
		accommodationOrderEntryGroupModel.setRoomStayRefNumber(0);
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels = new ArrayList<>();
		accommodationOrderEntryGroupModels.add(accommodationOrderEntryGroupModel);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(CartModel.class)))
				.thenReturn(accommodationOrderEntryGroupModels);
		final Map<String, String> changeDatePaymentResults = new HashMap<>();
		changeDatePaymentResults.put(TravelservicesConstants.BOOKING_PAYABLE_STATUS,
				TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE);
		when(calculatePaymentTypeForChangeDatesStrategy.calculate(Matchers.any(AccommodationReservationData.class),
				Matchers.any(AccommodationAvailabilityResponseData.class))).thenReturn(changeDatePaymentResults);
		final List<RoomRateCartData> roomRates = new ArrayList<>();
		roomRates.add(new RoomRateCartData());
		when(accommodationCartFacade.collectRoomRates(Matchers.any(AccommodationAddToCartForm.class))).thenReturn(roomRates);
		when(accommodationCartFacade.addAccommodationsToCart(Matchers.any(AccommodationOrderEntryGroupModel.class),
				Matchers.anyString(), Matchers.anyString(), Matchers.any(), Matchers.anyInt(), Matchers.anyString(),
				Matchers.anyString())).thenThrow(new CommerceCartModificationException("Exception Occured"));
		assertFalse(bookingFacade.changeDatesForAccommodationBooking(checkInDate, checkOutDate, accommodationAvailabilityResponse,
				new AccommodationReservationData()));
	}

	@Test
	public void testChangeDatesForAccommodationBooking() throws CommerceCartModificationException
	{
		final String checkInDate = "15/02/2017";
		final String checkOutDate = "17/02/2017";

		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = testDataSetUp
				.createAccommodationAvailabilityResponse();

		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel entryActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryActiveRoomRateProduct.setActive(Boolean.TRUE);

		final AbstractOrderEntryModel entryInActiveRoomRateProduct = new AbstractOrderEntryModel();
		entryInActiveRoomRateProduct.setProduct(new RoomRateProductModel());
		entryInActiveRoomRateProduct.setActive(Boolean.FALSE);

		final AbstractOrderEntryModel entryActiveProduct = new AbstractOrderEntryModel();
		entryActiveProduct.setProduct(new ProductModel());
		entryActiveProduct.setActive(Boolean.TRUE);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entryActiveRoomRateProduct);
		entries.add(entryInActiveRoomRateProduct);
		entries.add(entryActiveProduct);
		cart.setEntries(entries);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.doNothing().when(modelService).save(Matchers.any());
		Mockito.doNothing().when(modelService).refresh(Matchers.any());
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModel.setCheckInTime(new Date());
		accommodationOrderEntryGroupModel.setRoomStayRefNumber(0);
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels = new ArrayList<>();
		accommodationOrderEntryGroupModels.add(accommodationOrderEntryGroupModel);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(CartModel.class)))
				.thenReturn(accommodationOrderEntryGroupModels);
		final Map<String, String> changeDatePaymentResults = new HashMap<>();
		changeDatePaymentResults.put(TravelservicesConstants.BOOKING_PAYABLE_STATUS,
				TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE);
		when(calculatePaymentTypeForChangeDatesStrategy.calculate(Matchers.any(AccommodationReservationData.class),
				Matchers.any(AccommodationAvailabilityResponseData.class))).thenReturn(changeDatePaymentResults);
		final List<RoomRateCartData> roomRates = new ArrayList<>();
		roomRates.add(new RoomRateCartData());
		when(accommodationCartFacade.collectRoomRates(Matchers.any(AccommodationAddToCartForm.class))).thenReturn(roomRates);
		when(accommodationCartFacade.addAccommodationsToCart(Matchers.any(AccommodationOrderEntryGroupModel.class),
				Matchers.anyString(), Matchers.anyString(), Matchers.any(), Matchers.anyInt(), Matchers.anyString(),
				Matchers.anyString())).thenReturn(Boolean.TRUE);
		assertTrue(bookingFacade.changeDatesForAccommodationBooking(checkInDate, checkOutDate, accommodationAvailabilityResponse,
				new AccommodationReservationData()));
	}

	@Test
	public void testAcceptOrder()
	{
		final OrderModel order = Mockito.mock(OrderModel.class);
		when(order.getTransportationOrderStatus()).thenReturn(OrderStatus.ACTIVE);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(order);
		assertFalse(bookingFacade.acceptOrder("0001"));

		final OrderModel order2 = Mockito.mock(OrderModel.class);
		when(order2.getTransportationOrderStatus()).thenReturn(OrderStatus.ACTIVE_DISRUPTED_PENDING);
		when(bookingService.getOrderModelFromStore("0002")).thenReturn(order2);
		Mockito.doNothing().when(bookingService).updateOrderStatus(order2, OrderStatus.ACTIVE_DISRUPTED);
		assertTrue(bookingFacade.acceptOrder("0002"));
	}

	@Test
	public void testUnlinkBooking()
	{
		final OrderModel orderModel = Mockito.mock(OrderModel.class);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(bookingService.unlinkBooking(Matchers.any(UserModel.class), Matchers.any(OrderModel.class))).thenReturn(Boolean.TRUE);
		assertTrue(bookingFacade.unlinkBooking("0001"));
	}

	@Test
	public void testMapOrderToUserAccount()
	{
		final CustomerModel customer = new CustomerModel();
		when(userService.getCurrentUser()).thenReturn(customer);
		final OrderModel orderModel = Mockito.mock(OrderModel.class);
		when(orderModel.getUser()).thenReturn(new CustomerModel());
		when(orderModel.getCode()).thenReturn("0001");
		when(customerAccountService.getOrderUserMapping("0001", customer)).thenReturn(null);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		Mockito.doNothing().when(modelService).save(Matchers.any(OrderUserAccountMappingModel.class));
		bookingFacade.mapOrderToUserAccount("0001");
	}

	@Test
	public void testAddRequestToRoomStayBookingForException()
	{
		Mockito.doThrow(new ModelSavingException("Exception")).when(bookingService).addRequestToRoomStayBooking("001", 0, "00001");

		Mockito.doThrow(new RequestKeyGeneratorException("Exception")).when(bookingService).addRequestToRoomStayBooking("001", 0,
				"00002");
		assertFalse(bookingFacade.addRequestToRoomStayBooking("001", 0, "00001"));
		assertFalse(bookingFacade.addRequestToRoomStayBooking("001", 0, "00002"));
	}

	@Test
	public void testAddRequestToRoomStayBooking()
	{
		Mockito.doNothing().when(bookingService).addRequestToRoomStayBooking("001", 0, "00001");
		assertTrue(bookingFacade.addRequestToRoomStayBooking("001", 0, "00001"));
	}

	@Test
	public void testRemoveRequestFromRoomStayBookingForException()
	{
		Mockito.doThrow(new ModelRemovalException("Exception", Mockito.mock(Throwable.class))).when(bookingService)
				.removeRequestFromRoomStayBooking("001", 0, "00001");

		Mockito.doThrow(new ModelNotFoundException("Exception")).when(bookingService).removeRequestFromRoomStayBooking("001", 0,
				"00002");
		assertFalse(bookingFacade.removeRequestFromRoomStayBooking("001", 0, "00001"));
		assertFalse(bookingFacade.removeRequestFromRoomStayBooking("001", 0, "00002"));
	}

	@Test
	public void testRemoveRequestFromRoomStayBooking()
	{
		Mockito.doNothing().when(bookingService).removeRequestFromRoomStayBooking("001", 0, "00001");
		assertTrue(bookingFacade.removeRequestFromRoomStayBooking("001", 0, "00001"));
	}

	@Test
	public void testBeginPartialOrderCancellation()
	{
		when(travelCartService.cancelPartialOrder("0001", OrderEntryType.ACCOMMODATION, "ABC")).thenReturn(null);
		assertFalse(bookingFacade.beginPartialOrderCancellation("0001", OrderEntryType.ACCOMMODATION, "ABC"));

		final CartModel cartModel = Mockito.mock(CartModel.class);
		when(travelCartService.cancelPartialOrder("0002", OrderEntryType.ACCOMMODATION, "ABC")).thenReturn(cartModel);
		Mockito.doNothing().when(travelCartService).setSessionCart(cartModel);
		assertTrue(bookingFacade.beginPartialOrderCancellation("0002", OrderEntryType.ACCOMMODATION, "ABC"));

	}

	@Test
	public void testCancelPartialOrder() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PriceData price = testDataSetUp.createPriceData(-20d);
		assertFalse(bookingFacade.cancelPartialOrder(price, OrderEntryType.ACCOMMODATION));

		final PriceData price2 = testDataSetUp.createPriceData(20d);
		when(bookingService.cancelPartialOrder(price2.getValue().abs(), OrderEntryType.ACCOMMODATION)).thenReturn(Boolean.FALSE);
		assertTrue(bookingFacade.cancelPartialOrder(price2, OrderEntryType.ACCOMMODATION));

		final PriceData price3 = testDataSetUp.createPriceData(400d);
		when(bookingService.cancelPartialOrder(price3.getValue().abs(), OrderEntryType.ACCOMMODATION)).thenReturn(Boolean.TRUE);
		when(checkoutFacade.placeOrder()).thenReturn(new OrderData());
		assertTrue(bookingFacade.cancelPartialOrder(price3, OrderEntryType.ACCOMMODATION));
	}

	@Test
	public void testCancelPartialOrderForInvalidCartException() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PriceData price = testDataSetUp.createPriceData(200d);
		when(bookingService.cancelPartialOrder(price.getValue().abs(), OrderEntryType.ACCOMMODATION)).thenReturn(Boolean.TRUE);
		when(checkoutFacade.placeOrder()).thenThrow(new InvalidCartException("Exception"));
		assertFalse(bookingFacade.cancelPartialOrder(price, OrderEntryType.ACCOMMODATION));
	}

	@Test
	public void testIsCurrentCartOfTypeForEmptySessionCart()
	{
		when(travelCartService.hasSessionCart()).thenReturn(Boolean.FALSE);
		assertFalse(bookingFacade.isCurrentCartOfType(StringUtils.EMPTY));
	}

	@Test
	public void testIsCurrentCartOfType()
	{
		when(travelCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(enumerationService.getEnumerationValue(OrderEntryType.class, OrderEntryType.ACCOMMODATION.toString()))
				.thenReturn(OrderEntryType.ACCOMMODATION);
		final CartModel cartModel = new CartModel();
		when(travelCartService.getSessionCart()).thenReturn(cartModel);
		when(bookingService.isAbstractOrderOfType(cartModel, OrderEntryType.ACCOMMODATION.toString())).thenReturn(Boolean.TRUE);
		assertTrue(bookingFacade.isCurrentCartOfType(OrderEntryType.ACCOMMODATION.toString()));
	}

	@Test
	public void testIsCurrentCartOfTypeForNullOrderEntryType()
	{
		when(travelCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(enumerationService.getEnumerationValue(OrderEntryType.class, "TEST")).thenReturn(null);
		assertFalse(bookingFacade.isCurrentCartOfType("TEST"));
	}

	@Test
	public void testGetOrderTotalPaidForOrderEntryType()
	{
		final OrderModel orderModel = new OrderModel();
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(bookingService.getOrderTotalPaidForOrderEntryType(orderModel, OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.valueOf(100d));
		assertEquals(BigDecimal.valueOf(100d),
				bookingFacade.getOrderTotalPaidForOrderEntryType("0001", OrderEntryType.ACCOMMODATION));

	}

	@Test
	public void testPlaceOrderForInvalidCartException() throws InvalidCartException
	{
		when(checkoutFacade.placeOrder()).thenThrow(new InvalidCartException("Exception"));
		assertFalse(bookingFacade.placeOrder());
	}

	@Test
	public void testPlaceOrder() throws InvalidCartException
	{
		when(checkoutFacade.placeOrder()).thenReturn(new OrderData());
		assertTrue(bookingFacade.placeOrder());
	}

	@Test
	public void testGetOrderTotalToPayForOrderEntryType()
	{
		final OrderModel orderModel = new OrderModel();
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		when(bookingService.getOrderTotalPaidForOrderEntryType(orderModel, OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.valueOf(100d));

		when(bookingService.getOrderTotalPriceByType(orderModel, OrderEntryType.ACCOMMODATION)).thenReturn(200d);
		assertEquals(100, bookingFacade.getOrderTotalToPayForOrderEntryType("0001", OrderEntryType.ACCOMMODATION).intValue());
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

		private UserModel createUserModel(final String uid)
		{
			final UserModel userModel = new UserModel();
			userModel.setUid(uid);
			return userModel;
		}

		private CustomerModel createCustomerModel(final String uid, final CustomerType type)
		{
			final CustomerModel customerModel = new CustomerModel();
			customerModel.setUid(uid);
			customerModel.setType(type);
			return customerModel;
		}

		private CustomerData createCustomerData(final String uid, final CustomerType type)
		{
			final CustomerData customerData = new CustomerData();
			customerData.setUid(uid);
			customerData.setType(type);
			return customerData;
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

		private AccommodationReservationData getAccommodationReservationData(final String bookingReference,
				final List<ReservedRoomStayData> reservedRoomStayData, final String bookingStatusCode)
		{
			final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
			accommodationReservationData.setRoomStays(reservedRoomStayData);
			accommodationReservationData.setCode(bookingReference);
			accommodationReservationData.setBookingStatusCode(bookingStatusCode);
			return accommodationReservationData;
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

		private TravellerData createSimpleTravellerData(final String simpleUID, final String lastName)
		{
			final TravellerData travellerData = new TravellerData();
			travellerData.setSimpleUID(simpleUID);
			travellerData.setTravellerType(TravellerType.PASSENGER.toString());
			travellerData.setTravellerInfo(createSimplePassengerInformationData(lastName));
			return travellerData;
		}

		private GuestData createGuestData(final String lastName)
		{
			final GuestData guestData = new GuestData();
			final ProfileData profileData = new ProfileData();
			profileData.setLastName(lastName);
			guestData.setProfile(profileData);
			return guestData;
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

		private PassengerInformationData createSimplePassengerInformationData(final String lastName)
		{
			final PassengerInformationData paxInfo = new PassengerInformationData();
			paxInfo.setSurname(lastName);
			return paxInfo;
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
