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

package de.hybris.platform.travelservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordercancel.CancelDecision;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;
import de.hybris.platform.travelservices.dao.AbstractOrderEntryGroupDao;
import de.hybris.platform.travelservices.dao.OrderUserAccountMappingDao;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.exceptions.RequestKeyGeneratorException;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.RemarkModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.order.TravelCommerceCheckoutService;
import de.hybris.platform.travelservices.order.daos.TravelOrderDao;
import de.hybris.platform.travelservices.ordercancel.TotalRefundCalculationStrategy;
import de.hybris.platform.travelservices.ordercancel.impl.DefaultTotalRefundCalculationStrategy;
import de.hybris.platform.travelservices.service.keygenerator.TravelKeyGeneratorService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForEntryGroupCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForOrderEntryTypeCalculationStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultBookingService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBookingServiceTest
{
	@InjectMocks
	DefaultBookingService bookingService;

	@Mock
	private OrderModel order;

	@Mock
	private CartModel cart;

	@Mock
	private PaymentTransactionModel paymentTransactionModel;
	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private CustomerModel customer;

	@Mock
	private TravelCommerceCheckoutService travelCommerceCheckoutService;
	@Mock
	private CurrencyModel currency;
	@Mock
	private PaymentInfoModel paymentInfoModel;
	@Mock
	private PaymentTransactionEntryModel paymentTransactionEntryModel;
	@Mock
	private TimeService timeService;
	@Mock
	private PaymentService paymentService;
	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private TravelCustomerAccountService customerAccountService;
	@Mock
	private TravelOrderDao travelOrderDao;
	@Mock
	private OrderCancelService orderCancelService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private TravelCommerceStockService commerceStockService;
	@Mock
	private Map<OrderEntryType, TotalRefundCalculationStrategy> totalRefundCalculationStrategyMap;
	@Mock
	private DefaultTotalRefundCalculationStrategy totalRefundCalculationStrategy;
	@Mock
	private TravelCartService travelCartService;
	@Mock
	private TravelOrderEntryInfoModel travelOrderEntryInfoModel;
	@Mock
	private TravellerModel traveller;
	@Mock
	private TravelRouteModel travelRoute;
	@Mock
	private TransportOfferingModel transportOffering;
	@Mock
	private ProductModel product;
	@Mock
	private OrderService orderService;
	@Mock
	private PassengerInformationModel passengerInfo;
	@Mock
	private PassengerTypeModel passengerType;
	@Mock
	private AccommodationOrderEntryGroupModel orderEntryGroupModel;
	@Mock
	private AbstractOrderEntryGroupDao abstractOrderEntryGroupDao;
	@Mock
	private AccommodationOfferingModel accommodationOffering;
	@Mock
	private SpecialRequestDetailModel specialRequest;
	@Mock
	private RemarkModel remarks;
	@Mock
	private TravelKeyGeneratorService travelKeyGeneratorService;
	@Mock
	private OrderHistoryService orderHistoryService;
	@Mock
	private OrderUserAccountMappingDao orderUserAccountMappingDao;
	@Mock
	private OrderTotalPaidCalculationStrategy orderTotalPaidCalculationStrategy;
	@Mock
	private OrderTotalPaidForEntryGroupCalculationStrategy orderTotalPaidForAccommodationGroupCalculationStrategy;
	@Mock
	private OrderTotalPaidForOrderEntryTypeCalculationStrategy orderTotalPaidForOrderEntryTypeCalculationStrategy;

	@Before
	public void prepare()
	{
		Mockito.when(travelCartService.hasSessionCart()).thenReturn(true);
	}

	@Test
	public void createRefundPaymentTransactionTestWithoutPaymentTransactions()
	{
		assertFalse(bookingService.createRefundPaymentTransaction(order, BigDecimal.valueOf(20d)));
	}

	@Test
	public void createRefundPaymentTransactionTest()
	{
		final TestData testData = new TestData();
		Mockito.when(order.getPaymentTransactions())
				.thenReturn(Stream.of(paymentTransactionModel).collect(Collectors.toList()));
		Mockito.when(modelService.create(PaymentTransactionModel.class)).thenReturn(paymentTransactionModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.when(customer.getUid()).thenReturn("uid");
		Mockito.when(travelCommerceCheckoutService.getPaymentProvider()).thenReturn("paymentProvider");
		Mockito.when(paymentTransactionModel.getCurrency()).thenReturn(currency);
		Mockito.when(paymentTransactionModel.getRequestId()).thenReturn("request1");
		Mockito.when(paymentTransactionModel.getRequestToken()).thenReturn("requestToken1");
		Mockito.when(paymentTransactionModel.getInfo()).thenReturn(paymentInfoModel);
		Mockito.doNothing().when(modelService).save(paymentTransactionModel);
		Mockito.when(modelService.create(PaymentTransactionEntryModel.class)).thenReturn(paymentTransactionEntryModel);
		Mockito.when(timeService.getCurrentTime())
				.thenReturn(testData.createTodaysDate());
		Mockito.when(
				paymentService.getNewPaymentTransactionEntryCode(paymentTransactionModel, PaymentTransactionType.REFUND_STANDALONE))
				.thenReturn("paymentEntry");
		Mockito.doNothing().when(modelService).saveAll(paymentTransactionEntryModel);
		Mockito.doNothing().when(modelService).refresh(paymentTransactionModel);
		Mockito.doNothing().when(modelService).refresh(order);
		assertTrue(bookingService.createRefundPaymentTransaction(order, BigDecimal.valueOf(20d)));
	}

	@Test
	public void createRefundPaymentTransactionTestForNonDeprecatedMethod()
	{
		Mockito.when(travelCommerceCheckoutService.createRefundPaymentTransactionEntries(order, Arrays.asList(orderEntryModel)))
				.thenReturn(true);
		assertTrue(bookingService.createRefundPaymentTransaction(order, BigDecimal.valueOf(20d),
				Arrays.asList(orderEntryModel)));
	}

	@Test
	public void createRefundPaymentTransactionTestForNonDeprecatedMethodWithNullObject()
	{
		Mockito.when(travelCommerceCheckoutService.createRefundPaymentTransaction(order, BigDecimal.valueOf(20d),
				Arrays.asList(orderEntryModel))).thenReturn(null);
		assertFalse(bookingService.createRefundPaymentTransaction(order, BigDecimal.valueOf(20d),
				Arrays.asList(orderEntryModel)));
	}

	@Test
	public void getOrderModelFromStoreTestWithCancelledStatus()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.CANCELLED);
		final OrderHistoryEntryModel entry1 = new OrderHistoryEntryModel();
		final OrderHistoryEntryModel entry2 = new OrderHistoryEntryModel();
		entry2.setPreviousOrderVersion(order);
		Mockito.when(order.getHistoryEntries()).thenReturn(Arrays.asList(entry1, entry2));
		assertNotNull(bookingService.getOrderModelFromStore("0001"));
	}

	@Test
	public void getOrderModelFromStoreTestWithActiveStatus()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		assertNotNull(bookingService.getOrderModelFromStore("0001"));
	}

	@Test
	public void getOrderModelFromStoreTestWithNullOrder()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(null);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		assertNull(bookingService.getOrderModelFromStore("0001"));
	}

	@Test
	public void getOrderModelFromStoreTestWithThatThrowModelNotfoundException()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore))
				.thenThrow(new ModelNotFoundException("Model Not Found"));
		assertNull(bookingService.getOrderModelFromStore("0001"));
	}

	@Test
	public void getAllOrdersTest()
	{
		Mockito.when(travelOrderDao.findOrdersByCode("code", baseStore)).thenReturn(Stream.of(order).collect(Collectors.toList()));
		assertEquals(1, bookingService.getAllOrders("code", baseStore).size());
	}

	@Test
	public void isCancelPossibleTest()
	{
		final CancelDecision cancelDecision = new CancelDecision(true, new ArrayList<OrderCancelDenialReason>());
		Mockito.when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.when(orderCancelService.isCancelPossible(order, customer, false, false))
				.thenReturn(cancelDecision);
		assertTrue(bookingService.isCancelPossible(order));
	}

	@Test
	public void cancelOrderTest()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(Matchers.anyString())).thenReturn("notes");
		Mockito.when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.doNothing().when(commerceStockService).release(order);
		assertTrue(bookingService.cancelOrder(order));
	}

	@Test
	public void getTotalToRefundTestWithCancelledOrder()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.CANCELLED);
		final OrderHistoryEntryModel entry1 = new OrderHistoryEntryModel();
		final OrderHistoryEntryModel entry2 = new OrderHistoryEntryModel();
		entry2.setPreviousOrderVersion(order);
		Mockito.when(order.getHistoryEntries()).thenReturn(Arrays.asList(entry1, entry2));
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		bookingService.setTotalRefundCalculationStrategyMap(totalRefundCalculationStrategyMap);
		Mockito.when(totalRefundCalculationStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(totalRefundCalculationStrategy);
		Mockito.when(totalRefundCalculationStrategy.getTotalToRefund(order))
				.thenReturn(BigDecimal.valueOf(20d));
		assertEquals(BigDecimal.valueOf(20d), bookingService.getTotalToRefund(order));
	}

	@Test
	public void getTotalToRefundTestWithCancelledOrderAndAmendStatusChanged()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.CANCELLED);
		final OrderHistoryEntryModel entry1 = new OrderHistoryEntryModel();
		final OrderHistoryEntryModel entry2 = new OrderHistoryEntryModel();
		entry2.setPreviousOrderVersion(order);
		Mockito.when(order.getHistoryEntries()).thenReturn(Arrays.asList(entry1, entry2));
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		assertEquals(BigDecimal.ZERO, bookingService.getTotalToRefund(order));
	}

	@Test
	public void getTotalToRefundTestWithCancelledOrderAndQuantityZero()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.CANCELLED);
		final OrderHistoryEntryModel entry1 = new OrderHistoryEntryModel();
		final OrderHistoryEntryModel entry2 = new OrderHistoryEntryModel();
		entry2.setPreviousOrderVersion(order);
		Mockito.when(order.getHistoryEntries()).thenReturn(Arrays.asList(entry1, entry2));
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(0L);
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		assertEquals(BigDecimal.ZERO, bookingService.getTotalToRefund(order));
	}

	@Test
	public void getTotalToRefundTestWithCancelledOrderAndInActiveEntry()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.CANCELLED);
		final OrderHistoryEntryModel entry1 = new OrderHistoryEntryModel();
		final OrderHistoryEntryModel entry2 = new OrderHistoryEntryModel();
		entry2.setPreviousOrderVersion(order);
		Mockito.when(order.getHistoryEntries()).thenReturn(Arrays.asList(entry1, entry2));
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getActive()).thenReturn(false);
		assertEquals(BigDecimal.ZERO, bookingService.getTotalToRefund(order));
	}

	@Test
	public void getTotalToRefundTestWithActiveOrder()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		bookingService.setTotalRefundCalculationStrategyMap(totalRefundCalculationStrategyMap);
		Mockito.when(totalRefundCalculationStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(totalRefundCalculationStrategy);
		Mockito.when(totalRefundCalculationStrategy.getTotalToRefund(order))
				.thenReturn(BigDecimal.valueOf(20d));
		assertEquals(BigDecimal.valueOf(20d), bookingService.getTotalToRefund(order));
	}

	@Test
	public void getTotalToRefundTestWithActiveOrderAndInactiveEntry()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getActive()).thenReturn(false);
		assertEquals(BigDecimal.ZERO, bookingService.getTotalToRefund(order));
	}

	@Test
	public void getTotalToRefundTestWithActiveOrderWithAmendStatusChanged()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		assertEquals(BigDecimal.ZERO, bookingService.getTotalToRefund(order));
	}

	@Test
	public void getTotalToRefundTestWithActiveOrderWithQuantityZero()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(0L);
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		assertEquals(BigDecimal.ZERO, bookingService.getTotalToRefund(order));
	}

	@Test
	public void getOverloadedTotalToRefundTestWithCancelledOrder()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.CANCELLED);
		final OrderHistoryEntryModel entry1 = new OrderHistoryEntryModel();
		final OrderHistoryEntryModel entry2 = new OrderHistoryEntryModel();
		entry2.setPreviousOrderVersion(order);
		Mockito.when(order.getHistoryEntries()).thenReturn(Arrays.asList(entry1, entry2));
		bookingService.setTotalRefundCalculationStrategyMap(totalRefundCalculationStrategyMap);
		Mockito.when(totalRefundCalculationStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(totalRefundCalculationStrategy);
		Mockito.when(totalRefundCalculationStrategy.getTotalToRefund(order))
				.thenReturn(BigDecimal.valueOf(20d));
		assertEquals(BigDecimal.valueOf(20d), bookingService.getTotalToRefund(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void getOverloadedTotalToRefundTestWithActiveOrder()
	{
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		bookingService.setTotalRefundCalculationStrategyMap(totalRefundCalculationStrategyMap);
		Mockito.when(totalRefundCalculationStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(totalRefundCalculationStrategy);
		Mockito.when(totalRefundCalculationStrategy.getTotalToRefund(order))
				.thenReturn(BigDecimal.valueOf(20d));
		assertEquals(BigDecimal.valueOf(20d), bookingService.getTotalToRefund(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void cancelTravellerTestDepricated()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(travelCommerceCheckoutService.createRefundPaymentTransactionEntries(cart, Collections.emptyList()))
				.thenReturn(true);
		assertTrue(bookingService.cancelTraveller(BigDecimal.valueOf(20d)));
	}

	@Test
	public void cancelTravellerTestDepricatedWithZeroAmount()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		assertTrue(bookingService.cancelTraveller(BigDecimal.valueOf(0d)));
	}

	@Test
	public void cancelTravellerTest()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("traveller");
		Mockito.when(
				travelCommerceCheckoutService.createRefundPaymentTransactionEntries(cart,
						Stream.of(orderEntryModel).collect(Collectors.toList())))
				.thenReturn(true);
		final TravellerData travellerData = new TravellerData();
		travellerData.setLabel("traveller");
		assertTrue(bookingService.cancelTraveller(BigDecimal.valueOf(20d), travellerData));
	}

	@Test
	public void cancelTravellerTestWithEmptyTravellers()
	{
		assertFalse(bookingService.cancelTraveller(BigDecimal.valueOf(20d), null));
	}

	@Test
	public void cancelTravellerTestWithZeroRefundAmount()
	{
		final TravellerData travellerData = new TravellerData();
		travellerData.setLabel("traveller");
		assertTrue(bookingService.cancelTraveller(BigDecimal.valueOf(0d), travellerData));
	}

	@Test
	public void cancelTravellerTestWithNullTravelOrderEntryInfo()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(null);
		Mockito.when(travelCommerceCheckoutService.createRefundPaymentTransaction(cart, BigDecimal.valueOf(20d),
				Stream.of(orderEntryModel).collect(Collectors.toList()))).thenReturn(null);
		final TravellerData travellerData = new TravellerData();
		travellerData.setLabel("traveller");
		assertFalse(bookingService.cancelTraveller(BigDecimal.valueOf(20d), travellerData));
	}

	@Test
	public void cancelTravellerTestWithMoreThanOneTravellers()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		final TravellerModel traveller1 = new TravellerModel();
		final TravellerModel traveller2 = new TravellerModel();
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Arrays.asList(traveller1, traveller2));
		Mockito.when(traveller.getLabel()).thenReturn("traveller");
		Mockito.when(travelCommerceCheckoutService.createRefundPaymentTransaction(cart, BigDecimal.valueOf(20d),
				Stream.of(orderEntryModel).collect(Collectors.toList()))).thenReturn(null);
		final TravellerData travellerData = new TravellerData();
		travellerData.setLabel("traveller");
		assertFalse(bookingService.cancelTraveller(BigDecimal.valueOf(20d), travellerData));
	}

	@Test
	public void cancelTravellerTestWithDifferentLabels()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("travellerDifferent");
		Mockito.when(travelCommerceCheckoutService.createRefundPaymentTransaction(cart, BigDecimal.valueOf(20d),
				Stream.of(orderEntryModel).collect(Collectors.toList()))).thenReturn(null);
		final TravellerData travellerData = new TravellerData();
		travellerData.setLabel("traveller");
		assertFalse(bookingService.cancelTraveller(BigDecimal.valueOf(20d), travellerData));
	}

	@Test
	public void getOrderEntryTest()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNotNull(
				bookingService.getOrderEntry(order, "product1", "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("adult"), true));
	}

	@Test
	public void getOrderEntryTestWithNonZeroBundleNumber()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(1);
		assertNotNull(
				bookingService.getOrderEntry(order, "product1", "LGW-CDG", Arrays.asList("HY21"), Arrays.asList("adult"), false));
	}

	@Test
	public void getOrderEntryTestWithNonZeroBundleNumberAndRequiredBundleCheck()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(1);
		assertNull(
				bookingService.getOrderEntry(order, "product1", "LGW-CDG", Arrays.asList("HY21"), Arrays.asList("adult"), true));
	}

	@Test
	public void getOrderEntryTestWithADifferentProduct()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product2");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNull(
				bookingService.getOrderEntry(order, "product1", "LGW-CDG", Arrays.asList("HY21"), Arrays.asList("adult"), true));
	}

	@Test
	public void getOrderEntryTestWithADifferentTravellers()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNull(bookingService.getOrderEntry(order, "product1", "LGW-CDG", Arrays.asList("HY21"), Arrays.asList("child"), true));
	}

	@Test
	public void getOrderEntryTestWithNullTravelOrderEntryInfo()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(null);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNull(bookingService.getOrderEntry(order, "product1", "LGW-CDG", Arrays.asList("HY21"), Arrays.asList("child"), true));
	}

	@Test
	public void getOrderEntryTestWithEmptyTravellers()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(null);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Collections.emptyList());
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNull(bookingService.getOrderEntry(order, "product1", "LGW-CDG", Arrays.asList("HY21"), Arrays.asList("child"), true));
	}

	@Test
	public void getOrderEntryTestWithDifferentTravellersSize()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(null);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNull(bookingService.getOrderEntry(order, "product1", "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("adult", "child"), true));
	}

	@Test
	public void getOrderEntryTestWithDifferentRoutes()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNull(
				bookingService.getOrderEntry(order, "product1", "CDG-LGW", Arrays.asList("HY21"), Arrays.asList("adult"), true));
	}

	@Test
	public void getOrderEntryTestWithoutTravelRouteCode()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(null);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNotNull(
				bookingService.getOrderEntry(order, "product1", null, Arrays.asList("HY21"), Arrays.asList("adult"), true));
	}

	@Test
	public void getOrderEntryTestWithEmptyTOCodes()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(null);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Collections.emptyList());
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNotNull(bookingService.getOrderEntry(order, "product1", null, Collections.emptyList(), Arrays.asList("adult"), true));
	}

	@Test
	public void getOrderEntryTestWithEmptyTOCodeInAnOrderEntry()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(null);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings()).thenReturn(Collections.emptyList());
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNull(bookingService.getOrderEntry(order, "product1", null, Arrays.asList("HY21"), Arrays.asList("adult"), true));
	}

	@Test
	public void getOrderEntryTestWithDifferentTOCodes()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(null);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings()).thenReturn(Collections.emptyList());
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNull(bookingService.getOrderEntry(order, "product1", null, Arrays.asList("HY31"), Arrays.asList("adult"), true));
	}

	@Test
	public void checkBundleToAmendProductTest()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);

		assertTrue(bookingService.checkBundleToAmendProduct(cart, "product1", 1, "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("adult")));
	}


	@Test
	public void checkBundleToAmendProductTestWithoutBundleNumber()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(bookingService.checkBundleToAmendProduct(cart, "product1", 1, "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("adult")));
	}

	@Test
	public void checkBundleToAmendProductTestWithoutOrderEntry()
	{
		Mockito.when(cart.getEntries()).thenReturn(Collections.emptyList());
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(bookingService.checkBundleToAmendProduct(cart, "product1", 1, "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("adult")));
	}

	@Test
	public void checkBundleToAmendProductTestWithDifferentTravellers()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(bookingService.checkBundleToAmendProduct(cart, "product1", 1, "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("child")));
	}

	@Test
	public void checkBundleToAmendProductTestWithDifferentRoutes()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(bookingService.checkBundleToAmendProduct(cart, "product1", 1, "CDG-LGW", Arrays.asList("HY21"),
				Arrays.asList("adult")));
	}

	@Test
	public void checkBundleToAmendProductTestWithoutRoutes()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn(null);
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(
				bookingService.checkBundleToAmendProduct(cart, "product1", 1, null, Arrays.asList("HY21"),
						Arrays.asList("adult")));
	}

	@Test
	public void checkBundleToAmendProductTestWithDifferentTOCodes()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(
				bookingService.checkBundleToAmendProduct(cart, "product1", 1, "LGW-CDG", Arrays.asList("HY31"),
						Arrays.asList("adult")));
	}

	@Test
	public void checkBundleToAmendProductTestWithDifferentProducts()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(bookingService.checkBundleToAmendProduct(cart, "product2", 1, "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("adult")));
	}

	@Test
	public void checkBundleToAmendProductTestWithZeroQuantity()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(-1);
		assertFalse(bookingService.checkBundleToAmendProduct(cart, "product1", 0, "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("adult")));
	}

	@Test
	public void checkBundleToAmendProductTestWithNullRouteCode()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(
				bookingService.checkBundleToAmendProduct(cart, "product1", 0, null, Arrays.asList("HY21"),
				Arrays.asList("adult")));
	}

	@Test
	public void checkBundleToAmendProductTestWithNullRouteCodeInAnEntry()
	{
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn(null);
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");

		assertTrue(
				bookingService.checkBundleToAmendProduct(cart, "product1", 0, "LGW-CDG", Arrays.asList("HY21"),
						Arrays.asList("adult")));
	}

	@Test
	public void getProductQuantityInOrderForTransportOfferingTest()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		final OrderEntryModel orderEntry = Mockito.mock(OrderEntryModel.class);
		Mockito.when(orderService.getEntriesForProduct(order, product))
				.thenReturn(Stream.of(orderEntry).collect(Collectors.toList()));
		Mockito.when(orderEntry.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(orderEntry.getQuantity()).thenReturn(Long.valueOf(1));
		assertEquals(Long.valueOf(1),
				bookingService.getProductQuantityInOrderForTransportOffering("0001", product, transportOffering));
	}

	@Test
	public void getProductQuantityInOrderForTransportOfferingTestWithDifferentTOs()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		final OrderEntryModel orderEntry = Mockito.mock(OrderEntryModel.class);
		Mockito.when(orderService.getEntriesForProduct(order, product))
				.thenReturn(Stream.of(orderEntry).collect(Collectors.toList()));
		Mockito.when(orderEntry.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		final TransportOfferingModel to = new TransportOfferingModel();
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(to).collect(Collectors.toList()));
		Mockito.when(orderEntry.getQuantity()).thenReturn(Long.valueOf(1));
		assertEquals(Long.valueOf(0),
				bookingService.getProductQuantityInOrderForTransportOffering("0001", product, transportOffering));
	}

	@Test
	public void atleastOneAdultTravellerRemainingTest()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(1));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(traveller.getInfo()).thenReturn(passengerInfo);
		Mockito.when(passengerInfo.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		assertTrue(bookingService.atleastOneAdultTravellerRemaining("0001", "child"));
	}

	@Test
	public void atleastOneAdultTravellerRemainingTestWithAdultAsCancelledTraveller()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(1));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(traveller.getInfo()).thenReturn(passengerInfo);
		Mockito.when(passengerInfo.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		assertFalse(bookingService.atleastOneAdultTravellerRemaining("0001", "adult"));
	}

	@Test
	public void atleastOneAdultTravellerRemainingTestWithNullTravelOrderEntryInfo()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(null);
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(1));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(traveller.getInfo()).thenReturn(passengerInfo);
		Mockito.when(passengerInfo.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		assertFalse(bookingService.atleastOneAdultTravellerRemaining("0001", "adult"));
	}

	@Test
	public void atleastOneAdultTravellerRemainingTestWithTravellersSizeMoreThanOne()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		final TravellerModel travellerModel = Mockito.mock(TravellerModel.class);
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Arrays.asList(traveller, travellerModel));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(1));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(traveller.getInfo()).thenReturn(passengerInfo);
		Mockito.when(passengerInfo.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		assertFalse(bookingService.atleastOneAdultTravellerRemaining("0001", "adult"));
	}

	@Test
	public void atleastOneAdultTravellerRemainingTestWithZeroQuantity()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Arrays.asList(traveller));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(0));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(traveller.getInfo()).thenReturn(passengerInfo);
		Mockito.when(passengerInfo.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		assertFalse(bookingService.atleastOneAdultTravellerRemaining("0001", "adult"));
	}

	@Test
	public void atleastOneAdultTravellerRemainingTestWithDifferentPassengerType()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(1));
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(traveller.getInfo()).thenReturn(passengerInfo);
		Mockito.when(passengerInfo.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("child");
		assertFalse(bookingService.atleastOneAdultTravellerRemaining("0001", "child"));
	}

	@Test
	public void getOrderModelByOriginalOrderCodeTest()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderModelByOriginalOrderCode("0001", baseStore)).thenReturn(order);
		assertNotNull(bookingService.getOrderModelByOriginalOrderCode("0001"));
	}

	@Test
	public void updateOrderStatusTest()
	{
		final OrderModel orderModel = new OrderModel();
		Mockito.doNothing().when(modelService).save(orderModel);
		bookingService.updateOrderStatus(orderModel, OrderStatus.AMENDED);
		verify(modelService).save(orderModel);
		assertEquals(OrderStatus.AMENDED, orderModel.getStatus());
	}

	@Test
	public void getAccommodationOrderEntryGroupsTest()
	{
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		assertNotNull(bookingService.getAccommodationOrderEntryGroups(order));
		assertEquals(1, bookingService.getAccommodationOrderEntryGroups(order).size());
	}

	@Test
	public void getAccommodationOrderEntryGroupsTestThatReturnsNull()
	{
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Collections.emptyList());
		assertEquals(0, bookingService.getAccommodationOrderEntryGroups(order).size());
	}

	@Test
	public void getAccommodationOrderEntryGroupsTestThatisNotAnInstanceOfAccommodationOrderEntryGroupModel()
	{
		final AbstractOrderEntryGroupModel entryGroup = new AbstractOrderEntryGroupModel();
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(entryGroup).collect(Collectors.toList()));
		assertEquals(0, bookingService.getAccommodationOrderEntryGroups(order).size());
	}

	@Test
	public void getOriginalOrderEntryTest()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfoModel);
		Mockito.when(travelOrderEntryInfoModel.getTravelRoute()).thenReturn(travelRoute);
		Mockito.when(travelRoute.getCode()).thenReturn("LGW-CDG");
		Mockito.when(travelOrderEntryInfoModel.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(transportOffering.getCode()).thenReturn("HY21");
		Mockito.when(travelOrderEntryInfoModel.getTravellers()).thenReturn(Stream.of(traveller).collect(Collectors.toList()));
		Mockito.when(traveller.getLabel()).thenReturn("adult");
		Mockito.when(orderEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(orderEntryModel.getBundleNo()).thenReturn(0);
		assertNotNull(bookingService.getOriginalOrderEntry("0001", "product1", "LGW-CDG", Arrays.asList("HY21"),
				Arrays.asList("adult"), true));
	}

	@Test
	public void hasCartBeenAmendedTestWithNullCart()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(null);
		assertFalse(bookingService.hasCartBeenAmended());
	}

	@Test
	public void hasCartBeenAmendedTest()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		assertTrue(bookingService.hasCartBeenAmended());
	}

	@Test
	public void hasCartBeenAmendedTestWithAmendStatusSame()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		assertFalse(bookingService.hasCartBeenAmended());
	}

	@Test
	public void hasCartBeenAmendedTestWithTransportOrderWithDifferentConfiguredAccommodation()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(cart.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		final SelectedAccommodationModel oldSelectedAccomodation = new SelectedAccommodationModel();
		oldSelectedAccomodation.setTransportOffering(transportOffering);
		oldSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel oldConfiguredAccommodation = new ConfiguredAccommodationModel();
		oldConfiguredAccommodation.setIdentifier("oldIdentifier");
		oldSelectedAccomodation.setConfiguredAccommodation(oldConfiguredAccommodation);
		Mockito.when(order.getSelectedAccommodations()).thenReturn(Stream.of(oldSelectedAccomodation).collect(Collectors.toList()));
		final SelectedAccommodationModel newSelectedAccomodation = new SelectedAccommodationModel();
		newSelectedAccomodation.setTransportOffering(transportOffering);
		newSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel newConfiguredAccommodation = new ConfiguredAccommodationModel();
		newConfiguredAccommodation.setIdentifier("newIdentifier");
		newSelectedAccomodation.setConfiguredAccommodation(newConfiguredAccommodation);
		Mockito.when(cart.getSelectedAccommodations()).thenReturn(Stream.of(newSelectedAccomodation).collect(Collectors.toList()));
		assertTrue(bookingService.hasCartBeenAmended());
	}


	@Test
	public void hasCartBeenAmendedTestWithTransportOrderWithSameConfiguredAccommodation()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(cart.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		final SelectedAccommodationModel oldSelectedAccomodation = new SelectedAccommodationModel();
		oldSelectedAccomodation.setTransportOffering(transportOffering);
		oldSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel oldConfiguredAccommodation = new ConfiguredAccommodationModel();
		oldConfiguredAccommodation.setIdentifier("oldIdentifier");
		oldSelectedAccomodation.setConfiguredAccommodation(oldConfiguredAccommodation);
		Mockito.when(order.getSelectedAccommodations()).thenReturn(Stream.of(oldSelectedAccomodation).collect(Collectors.toList()));
		final SelectedAccommodationModel newSelectedAccomodation = new SelectedAccommodationModel();
		newSelectedAccomodation.setTransportOffering(transportOffering);
		newSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel newConfiguredAccommodation = new ConfiguredAccommodationModel();
		newConfiguredAccommodation.setIdentifier("oldIdentifier");
		newSelectedAccomodation.setConfiguredAccommodation(newConfiguredAccommodation);
		Mockito.when(cart.getSelectedAccommodations()).thenReturn(Stream.of(newSelectedAccomodation).collect(Collectors.toList()));
		assertFalse(bookingService.hasCartBeenAmended());
	}

	@Test
	public void hasCartBeenAmendedTestWithTransportOrderWithEmptySelectedAccommodations()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(cart.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		final SelectedAccommodationModel oldSelectedAccomodation = new SelectedAccommodationModel();
		oldSelectedAccomodation.setTransportOffering(transportOffering);
		oldSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel oldConfiguredAccommodation = new ConfiguredAccommodationModel();
		oldConfiguredAccommodation.setIdentifier("oldIdentifier");
		oldSelectedAccomodation.setConfiguredAccommodation(oldConfiguredAccommodation);
		Mockito.when(order.getSelectedAccommodations()).thenReturn(Collections.emptyList());
		final SelectedAccommodationModel newSelectedAccomodation = new SelectedAccommodationModel();
		newSelectedAccomodation.setTransportOffering(transportOffering);
		newSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel newConfiguredAccommodation = new ConfiguredAccommodationModel();
		newConfiguredAccommodation.setIdentifier("newIdentifier");
		newSelectedAccomodation.setConfiguredAccommodation(newConfiguredAccommodation);
		Mockito.when(cart.getSelectedAccommodations()).thenReturn(Collections.emptyList());
		assertFalse(bookingService.hasCartBeenAmended());
	}

	@Test
	public void hasCartBeenAmendedTestWithTransportOrderWithNewSelectedAccommodations()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(cart.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		final SelectedAccommodationModel oldSelectedAccomodation = new SelectedAccommodationModel();
		oldSelectedAccomodation.setTransportOffering(transportOffering);
		oldSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel oldConfiguredAccommodation = new ConfiguredAccommodationModel();
		oldConfiguredAccommodation.setIdentifier("oldIdentifier");
		oldSelectedAccomodation.setConfiguredAccommodation(oldConfiguredAccommodation);
		Mockito.when(order.getSelectedAccommodations()).thenReturn(Collections.emptyList());
		final SelectedAccommodationModel newSelectedAccomodation = new SelectedAccommodationModel();
		newSelectedAccomodation.setTransportOffering(transportOffering);
		newSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel newConfiguredAccommodation = new ConfiguredAccommodationModel();
		newConfiguredAccommodation.setIdentifier("newIdentifier");
		newSelectedAccomodation.setConfiguredAccommodation(newConfiguredAccommodation);
		Mockito.when(cart.getSelectedAccommodations()).thenReturn(Stream.of(newSelectedAccomodation).collect(Collectors.toList()));
		assertTrue(bookingService.hasCartBeenAmended());
	}

	@Test
	public void hasCartBeenAmendedTestWithTransportOrderWithOldSelectedAccommodations()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(cart.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		final SelectedAccommodationModel oldSelectedAccomodation = new SelectedAccommodationModel();
		oldSelectedAccomodation.setTransportOffering(transportOffering);
		oldSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel oldConfiguredAccommodation = new ConfiguredAccommodationModel();
		oldConfiguredAccommodation.setIdentifier("oldIdentifier");
		oldSelectedAccomodation.setConfiguredAccommodation(oldConfiguredAccommodation);
		Mockito.when(order.getSelectedAccommodations()).thenReturn(Stream.of(oldSelectedAccomodation).collect(Collectors.toList()));
		final SelectedAccommodationModel newSelectedAccomodation = new SelectedAccommodationModel();
		newSelectedAccomodation.setTransportOffering(transportOffering);
		newSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel newConfiguredAccommodation = new ConfiguredAccommodationModel();
		newConfiguredAccommodation.setIdentifier("newIdentifier");
		newSelectedAccomodation.setConfiguredAccommodation(newConfiguredAccommodation);
		Mockito.when(cart.getSelectedAccommodations()).thenReturn(Collections.emptyList());
		assertTrue(bookingService.hasCartBeenAmended());
	}

	@Test
	public void hasCartBeenAmendedTestWithTransportOrderWithDifferentTOs()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(cart.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		final SelectedAccommodationModel oldSelectedAccomodation = new SelectedAccommodationModel();
		oldSelectedAccomodation.setTransportOffering(transportOffering);
		oldSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel oldConfiguredAccommodation = new ConfiguredAccommodationModel();
		oldConfiguredAccommodation.setIdentifier("oldIdentifier");
		oldSelectedAccomodation.setConfiguredAccommodation(oldConfiguredAccommodation);
		Mockito.when(order.getSelectedAccommodations()).thenReturn(Stream.of(oldSelectedAccomodation).collect(Collectors.toList()));
		final SelectedAccommodationModel newSelectedAccomodation = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		newSelectedAccomodation.setTransportOffering(transportOfferingModel);
		newSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel newConfiguredAccommodation = new ConfiguredAccommodationModel();
		newConfiguredAccommodation.setIdentifier("newIdentifier");
		newSelectedAccomodation.setConfiguredAccommodation(newConfiguredAccommodation);
		Mockito.when(cart.getSelectedAccommodations()).thenReturn(Stream.of(newSelectedAccomodation).collect(Collectors.toList()));
		assertTrue(bookingService.hasCartBeenAmended());
	}

	@Test
	public void hasCartBeenAmendedTestWithTransportOrderWithDifferentTravellers()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(cart.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		final SelectedAccommodationModel oldSelectedAccomodation = new SelectedAccommodationModel();
		oldSelectedAccomodation.setTransportOffering(transportOffering);
		oldSelectedAccomodation.setTraveller(traveller);
		final ConfiguredAccommodationModel oldConfiguredAccommodation = new ConfiguredAccommodationModel();
		oldConfiguredAccommodation.setIdentifier("oldIdentifier");
		oldSelectedAccomodation.setConfiguredAccommodation(oldConfiguredAccommodation);
		Mockito.when(order.getSelectedAccommodations()).thenReturn(Stream.of(oldSelectedAccomodation).collect(Collectors.toList()));
		final SelectedAccommodationModel newSelectedAccomodation = new SelectedAccommodationModel();
		newSelectedAccomodation.setTransportOffering(transportOffering);
		final TravellerModel travellerModel = new TravellerModel();
		newSelectedAccomodation.setTraveller(travellerModel);
		final ConfiguredAccommodationModel newConfiguredAccommodation = new ConfiguredAccommodationModel();
		newConfiguredAccommodation.setIdentifier("newIdentifier");
		newSelectedAccomodation.setConfiguredAccommodation(newConfiguredAccommodation);
		Mockito.when(cart.getSelectedAccommodations()).thenReturn(Stream.of(newSelectedAccomodation).collect(Collectors.toList()));
		assertTrue(bookingService.hasCartBeenAmended());
	}

	@Test
	public void getAccommodationOrderEntryGroupTest()
	{
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		assertNotNull(bookingService.getAccommodationOrderEntryGroup(0, order));
	}

	@Test
	public void getAccommodationOrderEntryGroupTestThatReturnsNull()
	{
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(null);
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		assertNull(bookingService.getAccommodationOrderEntryGroup(0, order));
	}

	@Test
	public void getAccommodationOrderEntryGroupTestThatReturnsAnotherInstance()
	{
		final AbstractOrderEntryGroupModel entryGroup = new AbstractOrderEntryGroupModel();
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(entryGroup).collect(Collectors.toList()));
		assertNull(bookingService.getAccommodationOrderEntryGroup(0, order));
	}

	@Test
	public void getAccommodationOrderEntryGroupTestWithDifferentRefNumber()
	{
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		assertNull(bookingService.getAccommodationOrderEntryGroup(1, order));
	}

	@Test
	public void isReservationCancelledTest()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(orderEntryModel.getActive()).thenReturn(false);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(0));
		assertTrue(bookingService.isReservationCancelled(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void isReservationCancelledTestWithDifferentOrderEntryTypes()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(orderEntryModel.getActive()).thenReturn(false);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(0));
		assertTrue(bookingService.isReservationCancelled(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void isReservationCancelledTestWithActiveOrderEntry()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(0));
		assertFalse(bookingService.isReservationCancelled(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void checkIfAnyOrderEntryByTypeTestWithSameEntryType()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		assertTrue(bookingService.checkIfAnyOrderEntryByType(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void checkIfAnyOrderEntryByTypeTestWithDifferentEntryType()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		assertFalse(bookingService.checkIfAnyOrderEntryByType(order, OrderEntryType.ACCOMMODATION));
	}

	@Test
	public void getAccommodationDetailsParametersTest()
	{
		final TestData testData = new TestData();
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getAccommodationOffering())
				.thenReturn(accommodationOffering);
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		Mockito.when(orderEntryGroupModel.getStartingDate()).thenReturn(testData.createDate("30/12/2016"));
		Mockito.when(orderEntryGroupModel.getEndingDate()).thenReturn(testData.createDate("31/12/2016"));
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		assertFalse(bookingService.getAccommodationDetailsParameters(order).isEmpty());
	}

	@Test
	public void getAccommodationDetailsParametersTestWithEmptyEntryGroup()
	{
		final TestData testData = new TestData();
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order)).thenReturn(Collections.emptyList());
		Mockito.when(orderEntryGroupModel.getAccommodationOffering()).thenReturn(accommodationOffering);
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		Mockito.when(orderEntryGroupModel.getStartingDate()).thenReturn(testData.createDate("30/12/2016"));
		Mockito.when(orderEntryGroupModel.getEndingDate()).thenReturn(testData.createDate("31/12/2016"));
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		assertTrue(bookingService.getAccommodationDetailsParameters(order).isEmpty());
	}

	@Test
	public void getNewAccommodationOrderEntryGroupRefsTest()
	{
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.NEW);
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		assertEquals(1, bookingService.getNewAccommodationOrderEntryGroupRefs(order).size());
	}

	@Test
	public void getNewAccommodationOrderEntryGroupRefsTestWithChangedStatus()
	{
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		assertEquals(0, bookingService.getNewAccommodationOrderEntryGroupRefs(order).size());
	}

	@Test
	public void addRequestToRoomStayBookingTestWithAllDetails()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		Mockito.when(orderEntryGroupModel.getSpecialRequestDetail()).thenReturn(specialRequest);
		Mockito.when(specialRequest.getRemarks()).thenReturn(Stream.of(remarks).collect(Collectors.toList()));
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getInt(Matchers.anyString(), Matchers.anyInt())).willReturn(5);
		Mockito.when(modelService.create(RemarkModel.class)).thenReturn(remarks);
		Mockito.when(travelKeyGeneratorService.generateAccommodationRequestCode(0, "0001")).thenReturn("_0001_0_123456");
		Mockito.doNothing().when(modelService).save(remarks);
		Mockito.doNothing().when(modelService).save(specialRequest);
		Mockito.doNothing().when(modelService).save(orderEntryGroupModel);
		bookingService.addRequestToRoomStayBooking("request", 0, "0001");
	}

	@Test
	public void addRequestToRoomStayBookingTestWithEmptySpecialRequests()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		Mockito.when(orderEntryGroupModel.getSpecialRequestDetail()).thenReturn(null);
		Mockito.when(modelService.create(SpecialRequestDetailModel.class)).thenReturn(specialRequest);
		Mockito.when(specialRequest.getRemarks()).thenReturn(Stream.of(remarks).collect(Collectors.toList()));
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getInt(Matchers.anyString(), Matchers.anyInt())).willReturn(5);
		Mockito.when(modelService.create(RemarkModel.class)).thenReturn(remarks);
		Mockito.when(travelKeyGeneratorService.generateAccommodationRequestCode(0, "0001")).thenReturn("_0001_0_123456");
		Mockito.doNothing().when(modelService).save(remarks);
		Mockito.doNothing().when(modelService).save(specialRequest);
		Mockito.doNothing().when(modelService).save(orderEntryGroupModel);
		bookingService.addRequestToRoomStayBooking("request", 0, "0001");
	}

	@Test(expected = ModelSavingException.class)
	public void addRequestToRoomStayBookingTestWithModelSavingException()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		Mockito.when(orderEntryGroupModel.getSpecialRequestDetail()).thenReturn(null);
		Mockito.when(modelService.create(SpecialRequestDetailModel.class)).thenReturn(specialRequest);
		Mockito.when(specialRequest.getRemarks()).thenReturn(Stream.of(remarks).collect(Collectors.toList()));
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getInt(Matchers.anyString(), Matchers.anyInt())).willReturn(1);
		Mockito.when(modelService.create(RemarkModel.class)).thenReturn(remarks);
		Mockito.when(travelKeyGeneratorService.generateAccommodationRequestCode(0, "0001")).thenReturn("_0001_0_123456");
		Mockito.doNothing().when(modelService).save(remarks);
		Mockito.doNothing().when(modelService).save(specialRequest);
		Mockito.doNothing().when(modelService).save(orderEntryGroupModel);
		bookingService.addRequestToRoomStayBooking("request", 0, "0001");
	}

	@Test(expected = RequestKeyGeneratorException.class)
	public void addRequestToRoomStayBookingTestWithModelSavingExceptionWhileSavingRemarks()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getRoomStayRefNumber()).thenReturn(0);
		Mockito.when(orderEntryGroupModel.getSpecialRequestDetail()).thenReturn(null);
		Mockito.when(modelService.create(SpecialRequestDetailModel.class)).thenReturn(specialRequest);
		Mockito.when(specialRequest.getRemarks()).thenReturn(Stream.of(remarks).collect(Collectors.toList()));
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getInt(Matchers.anyString(), Matchers.anyInt())).willReturn(5);
		Mockito.when(modelService.create(RemarkModel.class)).thenReturn(remarks);
		Mockito.when(travelKeyGeneratorService.generateAccommodationRequestCode(0, "0001")).thenReturn("_0001_0_123456");
		Mockito.doThrow(new ModelSavingException("Max number of attempts to create request reached")).when(modelService)
				.save(remarks);
		Mockito.doNothing().when(modelService).save(specialRequest);
		Mockito.doNothing().when(modelService).save(orderEntryGroupModel);
		bookingService.addRequestToRoomStayBooking("request", 0, "0001");
	}

	@Test
	public void removeRequestFromRoomStayBookingTestWithAllDetails()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getSpecialRequestDetail()).thenReturn(specialRequest);
		Mockito.when(specialRequest.getRemarks()).thenReturn(Stream.of(remarks).collect(Collectors.toList()));
		Mockito.when(remarks.getCode()).thenReturn("request");
		Mockito.doNothing().when(modelService).remove(remarks);
		Mockito.doNothing().when(modelService).refresh(orderEntryGroupModel);
		bookingService.removeRequestFromRoomStayBooking("request", 0, "0001");
	}

	@Test(expected = ModelNotFoundException.class)
	public void removeRequestFromRoomStayBookingTestWithNullSpecialRequest()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getSpecialRequestDetail()).thenReturn(null);
		Mockito.when(specialRequest.getRemarks()).thenReturn(Stream.of(remarks).collect(Collectors.toList()));
		Mockito.when(remarks.getCode()).thenReturn("request");
		Mockito.doNothing().when(modelService).remove(remarks);
		Mockito.doNothing().when(modelService).refresh(orderEntryGroupModel);
		bookingService.removeRequestFromRoomStayBooking("request", 0, "0001");
	}

	@Test(expected = ModelNotFoundException.class)
	public void removeRequestFromRoomStayBookingTestWithNullRemarks()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getSpecialRequestDetail()).thenReturn(specialRequest);
		Mockito.when(specialRequest.getRemarks()).thenReturn(Collections.emptyList());
		Mockito.when(remarks.getCode()).thenReturn("request");
		Mockito.doNothing().when(modelService).remove(remarks);
		Mockito.doNothing().when(modelService).refresh(orderEntryGroupModel);
		bookingService.removeRequestFromRoomStayBooking("request", 0, "0001");
	}

	@Test(expected = ModelNotFoundException.class)
	public void removeRequestFromRoomStayBookingTestWithDifferentRequests()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(order.getStatus()).thenReturn(OrderStatus.ACTIVE);
		Mockito.when(abstractOrderEntryGroupDao.findAbstractOrderEntryGroups(order))
				.thenReturn(Stream.of(orderEntryGroupModel).collect(Collectors.toList()));
		Mockito.when(orderEntryGroupModel.getSpecialRequestDetail()).thenReturn(specialRequest);
		Mockito.when(specialRequest.getRemarks()).thenReturn(Stream.of(remarks).collect(Collectors.toList()));
		Mockito.when(remarks.getCode()).thenReturn("request");
		Mockito.doNothing().when(modelService).remove(remarks);
		Mockito.doNothing().when(modelService).refresh(orderEntryGroupModel);
		bookingService.removeRequestFromRoomStayBooking("remarks", 0, "0001");
	}

	@Test
	public void getOrderTotalPriceByTypeTestWithNet()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(order.getNet()).thenReturn(true);
		Mockito.when(order.getTotalPrice()).thenReturn(50d);
		Mockito.when(order.getTotalTax()).thenReturn(30d);
		assertEquals(Double.valueOf(80), bookingService.getOrderTotalPriceByType(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void getOrderTotalPriceByTypeTestWithoutNet()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(order.getNet()).thenReturn(false);
		Mockito.when(order.getTotalPrice()).thenReturn(50d);
		assertEquals(Double.valueOf(50), bookingService.getOrderTotalPriceByType(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void getOrderTotalPriceByTypeTestWithDifferentOrderEntryType()
	{
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		assertEquals(Double.valueOf(0), bookingService.getOrderTotalPriceByType(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void getOrderTotalPriceByTypeTestWithTaxValues()
	{
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		orderEntry.setType(OrderEntryType.ACCOMMODATION);
		Mockito.when(order.getEntries()).thenReturn(Arrays.asList(orderEntry, orderEntryModel));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(orderEntryModel.getTotalPrice()).thenReturn(50d);
		final TaxValue tax = new TaxValue("tax", 30d, true, 30d, "GBP");
		Mockito.when(orderEntryModel.getTaxValues()).thenReturn(Stream.of(tax).collect(Collectors.toList()));
		assertEquals(Double.valueOf(80), bookingService.getOrderTotalPriceByType(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void getOrderTotalPriceByTypeTestWithEmptyTaxValues()
	{
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		orderEntry.setType(OrderEntryType.ACCOMMODATION);
		Mockito.when(order.getEntries()).thenReturn(Arrays.asList(orderEntry, orderEntryModel));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(orderEntryModel.getTotalPrice()).thenReturn(50d);
		Mockito.when(orderEntryModel.getTaxValues()).thenReturn(Collections.emptyList());
		assertEquals(Double.valueOf(50), bookingService.getOrderTotalPriceByType(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void unlinkBookingTest()
	{
		Mockito.when(order.getUser()).thenReturn(customer);
		Mockito.doNothing().when(modelService).save(order);
		Mockito.when(order.getVersionID()).thenReturn("0.01");
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(orderHistoryService.getHistorySnapshots(order)).thenReturn(Stream.of(order).collect(Collectors.toList()));
		assertTrue(bookingService.unlinkBooking(customer, order));
	}

	@Test
	public void unlinkBookingTestThrowsException()
	{
		Mockito.when(order.getUser()).thenReturn(customer);
		Mockito.doThrow(new ModelSavingException("cannot save this model")).when(modelService).save(order);
		Mockito.when(order.getVersionID()).thenReturn("0.01");
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(customerAccountService.getOrderForCode("0001", baseStore)).thenReturn(order);
		Mockito.when(orderHistoryService.getHistorySnapshots(order)).thenReturn(Stream.of(order).collect(Collectors.toList()));
		assertFalse(bookingService.unlinkBooking(customer, order));
	}

	@Test
	public void unlinkBookingTestWithDifferentUser()
	{
		final CustomerModel customerModel = new CustomerModel();
		Mockito.when(order.getUser()).thenReturn(customer);
		final OrderUserAccountMappingModel mapping = Mockito.mock(OrderUserAccountMappingModel.class);
		final List<OrderUserAccountMappingModel> mappings = new ArrayList<>();
		mappings.add(mapping);
		Mockito.when(orderUserAccountMappingDao.findMappings(customerModel, order))
				.thenReturn(mappings);
		Mockito.doNothing().when(modelService).removeAll(mappings);
		assertTrue(bookingService.unlinkBooking(customerModel, order));
	}

	@Test
	public void unlinkBookingTestWithDifferentUserAndEmptyMappings()
	{
		final CustomerModel customerModel = new CustomerModel();
		Mockito.when(order.getUser()).thenReturn(customer);
		final OrderUserAccountMappingModel mapping = Mockito.mock(OrderUserAccountMappingModel.class);
		final List<OrderUserAccountMappingModel> mappings = new ArrayList<>();
		mappings.add(mapping);
		Mockito.when(orderUserAccountMappingDao.findMappings(customerModel, order)).thenReturn(Collections.emptyList());
		assertTrue(bookingService.unlinkBooking(customerModel, order));
	}

	@Test
	public void cancelPartialOrderTestWithRefundAmout()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);

		given(travelCommerceCheckoutService.createRefundPaymentTransactionEntries(cart, Arrays.asList(orderEntryModel)))
				.willReturn(true);

		assertTrue(bookingService.cancelPartialOrder(BigDecimal.valueOf(20d), OrderEntryType.TRANSPORT));
	}

	@Test
	public void cancelPartialOrderTestWithDifferentOrderEntryTypes()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);

		given(travelCommerceCheckoutService.createRefundPaymentTransactionEntries(cart, Arrays.asList(orderEntryModel)))
				.willReturn(false);

		assertFalse(bookingService.cancelPartialOrder(BigDecimal.valueOf(20d), OrderEntryType.ACCOMMODATION));
	}

	@Test
	public void cancelPartialOrderTestWithoutRefundAmout()
	{
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cart);
		Mockito.when(cart.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);

		assertTrue(bookingService.cancelPartialOrder(BigDecimal.valueOf(0d), OrderEntryType.TRANSPORT));
	}

	@Test
	public void getLastActiveOrderForTypeTest()
	{
		final TestData test = new TestData();
		Mockito.when(order.getCode()).thenReturn("0001");
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		final OrderModel order2 = new OrderModel();
		order2.setCreationtime(test.createDate("29/12/2016"));
		final OrderEntryModel orderEntry2 = new OrderEntryModel();
		orderEntry2.setActive(true);
		orderEntry2.setType(OrderEntryType.TRANSPORT);
		orderEntry2.setQuantity(Long.valueOf(0));
		order2.setEntries(Arrays.asList(orderEntry2));
		Mockito.when(travelOrderDao.findOrdersByCode("code", baseStore)).thenReturn(Arrays.asList(order, order2));
		Mockito.when(order.getEntries()).thenReturn(Stream.of(orderEntryModel).collect(Collectors.toList()));
		Mockito.when(orderEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(orderEntryModel.getActive()).thenReturn(true);
		Mockito.when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(0));
		Mockito.when(order.getCreationtime()).thenReturn(test.createDate("30/12/2016"));
		assertNotNull(bookingService.getLastActiveOrderForType(order, OrderEntryType.TRANSPORT));
	}

	@Test
	public void getOrderTotalPaidTest()
	{
		Mockito.when(orderTotalPaidCalculationStrategy.calculate(order)).thenReturn(BigDecimal.valueOf(20d));
		assertEquals(BigDecimal.valueOf(20d), bookingService.getOrderTotalPaid(order));
	}

	@Test
	public void getOrderTotalPaidByEntryGroupTest()
	{
		Mockito.when(orderTotalPaidForAccommodationGroupCalculationStrategy.calculate(order, orderEntryGroupModel))
				.thenReturn(BigDecimal.valueOf(20d));
		assertEquals(BigDecimal.valueOf(20d), bookingService.getOrderTotalPaidByEntryGroup(order, orderEntryGroupModel));
	}

	@Test
	public void getOrderTotalPaidForOrderEntryTypeTest()
	{
		Mockito.when(orderTotalPaidForOrderEntryTypeCalculationStrategy.calculate(order, OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.valueOf(20d));
		assertEquals(BigDecimal.valueOf(20d),
				bookingService.getOrderTotalPaidForOrderEntryType(order, OrderEntryType.ACCOMMODATION));

	}

	@Test
	public void linkEntriesToOldPaymentTransactionsTest()
	{
		given(travelCommerceCheckoutService.linkEntriesToOldPaymentTransactions(orderEntryGroupModel,
				Arrays.asList(orderEntryModel))).willReturn(true);
		assertTrue(bookingService.linkEntriesToOldPaymentTransactions(orderEntryGroupModel, Arrays.asList(orderEntryModel)));
	}

	class TestData
	{
		public Date createTodaysDate()
		{
			final Date input = new Date();
			final Instant instant = input.toInstant();
			final Date output = Date.from(instant);
			final SimpleDateFormat format = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			return TravelDateUtils.getDate(format.format(output), TravelservicesConstants.DATE_PATTERN);
		}

		public Date createDate(final String date)
		{
			Date obj = null;
			final SimpleDateFormat format = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			try
			{
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
