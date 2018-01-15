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

package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BookingReferenceType;
import de.hybris.platform.ndcfacades.ndc.BookingReferenceType.OtherID;
import de.hybris.platform.ndcfacades.ndc.BookingReferences;
import de.hybris.platform.ndcfacades.ndc.CurrencyMetadata;
import de.hybris.platform.ndcfacades.ndc.CurrencyMetadatas;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.OrdCreateMetadataType;
import de.hybris.platform.ndcfacades.ndc.OrdCreateMetadataType.Other;
import de.hybris.platform.ndcfacades.ndc.OrdCreateMetadataType.Other.OtherMetadata;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Order;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments.Payment;
import de.hybris.platform.ndcfacades.ndc.OrderCoreChangeType.ActionType;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.Payments;
import de.hybris.platform.ndcfacades.ndc.OrderIDType;
import de.hybris.platform.ndcfacades.ndc.OrderItemAssociationType;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ.Query.Filters;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ.Query.Filters.Passengers;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS;
import de.hybris.platform.ndcfacades.ndc.SimpleCurrencyPriceType;
import de.hybris.platform.ndcfacades.order.NDCOrderEntryFacade;
import de.hybris.platform.ndcfacades.order.NDCPaymentAddressFacade;
import de.hybris.platform.ndcfacades.order.NDCPaymentInfoFacade;
import de.hybris.platform.ndcfacades.order.NDCPaymentTransactionFacade;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcfacades.strategies.AmendOrderOfferFilterStrategy;
import de.hybris.platform.ndcfacades.strategies.AmendOrderStrategy;
import de.hybris.platform.ndcservices.enums.NDCActionType;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCOrderService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionStrategy;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


//TODO This (or part of this class) has been commented out due to 17.1 upgrade and needs to be revisited
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNDCOrderFacadeTest
{
	@InjectMocks
	DefaultNDCOrderFacade defaultNDCOrderFacade = new DefaultNDCOrderFacade();

	@Mock
	CustomerAccountService customerAccountService;
	@Mock
	TravelCommerceStockService commerceStockService;
	@Mock
	CommonI18NService commonI18NService;
	@Mock
	CalculationService calculationService;
	@Mock
	StoreSessionFacade storeSessionFacade;
	@Mock
	BookingFacade bookingFacade;

	@Mock
	NDCOrderService ndcOrderService;
	@Mock
	NDCOrderEntryFacade ndcOrderEntryFacade;
	@Mock
	NDCPaymentAddressFacade ndcPaymentAddressFacade;
	@Mock
	NDCPaymentInfoFacade ndcPaymentInfoFacade;
	@Mock
	NDCPaymentTransactionFacade ndcPaymentTransactionFacade;
	@Mock
	NDCOfferItemIdResolver ndcOfferItemIdResolver;
	@Mock
	Map<NDCActionType, AmendOrderStrategy> amendOrderStrategyMap;

	@Mock
	Converter<OrderModel, OrderViewRS> ndcOrderViewRSConverter;

	@Mock
	Map<String, ActionTypeOption> orderChangeActionToBookingActionMapping;
	@Mock
	Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> bookingActionEnabledCalculationStrategiesMap;
	@Mock
	Map<ActionTypeOption, BookingActionStrategy> bookingActionStrategyMap;

	List<AmendOrderOfferFilterStrategy> amendOrderOfferFilterStrategyList = new ArrayList<>();
	@Mock
	UserService userService;
	@Mock
	ModelService modelService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	ConfigurationService configurationService;
	@Mock
	BookingService bookingService;
	@Mock
	ReservationFacade reservationFacade;
	@Mock
	BusinessProcessService businessProcessService;

	@Before
	public void setUp()
	{
		defaultNDCOrderFacade.setAmendOrderOfferFilterStrategyList(amendOrderOfferFilterStrategyList);
	}

	@Test(expected = NDCOrderException.class)
	public void testOrderCreateWithException() throws NDCOrderException
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final UserModel user = new CustomerModel();
		Mockito.when(userService.getCurrentUser()).thenReturn(user);

		final Query query = new Query();
		final OrdCreateMetadataType metaData = new OrdCreateMetadataType();
		final Other other = new Other();
		final OtherMetadata otherMetaData = new OtherMetadata();
		final CurrencyMetadatas currencyMetaDatas = new CurrencyMetadatas();
		final CurrencyMetadata currencyMetaData = new CurrencyMetadata();
		currencyMetaData.setMetadataKey("metaDatakey");
		currencyMetaDatas.getCurrencyMetadata().add(currencyMetaData);
		otherMetaData.setCurrencyMetadatas(currencyMetaDatas);
		other.getOtherMetadata().add(otherMetaData);
		metaData.setOther(other);
		query.setMetadata(metaData);

		final Payments payments = new Payments();
		final OrderPaymentFormType payment = new OrderPaymentFormType();
		final SimpleCurrencyPriceType amount = new SimpleCurrencyPriceType();
		amount.setValue(BigDecimal.valueOf(100));
		payment.setAmount(amount);
		payments.getPayment().add(payment);
		query.setPayments(payments);
		orderCreateRQ.setQuery(query);

		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.INSUFFICIENT_STOCK_LEVEL)).thenReturn(null);
		defaultNDCOrderFacade.orderCreate(orderCreateRQ);
	}

	@Test
	public void testOrderCreate() throws NDCOrderException, InsufficientStockLevelException
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final UserModel user = new CustomerModel();
		Mockito.when(userService.getCurrentUser()).thenReturn(user);

		final Query query = new Query();
		final OrdCreateMetadataType metaData = new OrdCreateMetadataType();
		final Other other = new Other();
		final OtherMetadata otherMetaData = new OtherMetadata();
		final CurrencyMetadatas currencyMetaDatas = new CurrencyMetadatas();
		final CurrencyMetadata currencyMetaData = new CurrencyMetadata();
		currencyMetaData.setMetadataKey("metaDatakey");
		currencyMetaDatas.getCurrencyMetadata().add(currencyMetaData);
		otherMetaData.setCurrencyMetadatas(currencyMetaDatas);
		other.getOtherMetadata().add(otherMetaData);
		metaData.setOther(other);
		query.setMetadata(metaData);

		orderCreateRQ.setQuery(query);

		defaultNDCOrderFacade.orderCreate(orderCreateRQ);
		Mockito.verify(commerceStockService).reserve(Mockito.any(AbstractOrderModel.class));
	}

	@Test(expected = NDCOrderException.class)
	public void testRetrieveOrderWithException() throws NDCOrderException
	{
		final OrderRetrieveRQ orderRetrieveRQ = new OrderRetrieveRQ();
		final de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ.Query query = new de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ.Query();
		final Filters filter = new Filters();
		final BookingReferences bookingReferences = new BookingReferences();
		final BookingReferenceType bookingReferenceType = new BookingReferenceType();
		final OtherID otherId = new OtherID();
		otherId.setValue("otherId");
		bookingReferenceType.setOtherID(otherId);
		bookingReferences.getBookingReference().add(bookingReferenceType);
		filter.setBookingReferences(bookingReferences);

		final Passengers passengers = new Passengers();
		/*
		 * final Name name = new Name(); final Surname surname = new Surname(); surname.setValue("Surname");
		 * name.setSurname(surname); passengers.setName(name);
		 */
		filter.setPassengers(passengers);

		query.setFilters(filter);
		orderRetrieveRQ.setQuery(query);

		defaultNDCOrderFacade.retrieveOrder(orderRetrieveRQ);
	}

	@Test
	public void testRetrieveOrder() throws NDCOrderException
	{
		final OrderRetrieveRQ orderRetrieveRQ = new OrderRetrieveRQ();
		final de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ.Query query = new de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ.Query();
		final Filters filter = new Filters();
		final BookingReferences bookingReferences = new BookingReferences();
		final BookingReferenceType bookingReferenceType = new BookingReferenceType();
		final OtherID otherId = new OtherID();
		otherId.setValue("otherId");
		bookingReferenceType.setOtherID(otherId);
		bookingReferences.getBookingReference().add(bookingReferenceType);
		filter.setBookingReferences(bookingReferences);

		final Passengers passengers = new Passengers();
		/*
		 * final Name name = new Name(); final Surname surname = new Surname(); surname.setValue("Surname");
		 * name.setSurname(surname); passengers.setName(name);
		 */
		filter.setPassengers(passengers);

		query.setFilters(filter);
		orderRetrieveRQ.setQuery(query);

		final OrderModel orderModel = new OrderModel();
		Mockito.when(bookingService.getOrderModelFromStore(Mockito.anyString())).thenReturn(orderModel);

		final OrderViewRS orderViewRS = defaultNDCOrderFacade.retrieveOrder(orderRetrieveRQ);
		Assert.assertNotNull(orderViewRS);
	}

	@Test(expected = NDCOrderException.class)
	public void testPayOrderWithException() throws NDCOrderException
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final Query query = new Query();
		final BookingReferences bookingReferences = new BookingReferences();
		final BookingReferenceType bookingReferenceType = new BookingReferenceType();
		final OtherID otherId = new OtherID();
		otherId.setValue("otherId");
		bookingReferenceType.setOtherID(otherId);
		bookingReferences.getBookingReference().add(bookingReferenceType);
		query.setBookingReferences(bookingReferences);
		orderCreateRQ.setQuery(query);

		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.INVALID_MANAGE_BOOKING_INFORMATION))
				.thenReturn("INVALID_MANAGE_BOOKING_INFORMATION");
		final OrderViewRS orderViewRS = defaultNDCOrderFacade.payOrder(orderCreateRQ);
	}

	@Test
	public void testPayOrder() throws NDCOrderException
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final Query query = new Query();
		final BookingReferences bookingReferences = new BookingReferences();
		final BookingReferenceType bookingReferenceType = new BookingReferenceType();
		final OtherID otherId = new OtherID();
		otherId.setValue("otherId");
		bookingReferenceType.setOtherID(otherId);
		bookingReferences.getBookingReference().add(bookingReferenceType);
		query.setBookingReferences(bookingReferences);

		final Payments payments = new Payments();
		final OrderPaymentFormType payment = new OrderPaymentFormType();
		final SimpleCurrencyPriceType amount = new SimpleCurrencyPriceType();
		amount.setValue(BigDecimal.valueOf(100));
		payment.setAmount(amount);
		payments.getPayment().add(payment);
		query.setPayments(payments);

		orderCreateRQ.setQuery(query);

		final OrderModel orderModel = new OrderModel();
		final UserModel user = new UserModel();
		orderModel.setUser(user);
		orderModel.setCode("1001");
		orderModel.setTotalPrice(90d);

		final PaymentTransactionModel paymentTransaction = new PaymentTransactionModel();
		final PaymentTransactionEntryModel paymentTransactionEntry = new PaymentTransactionEntryModel();
		paymentTransactionEntry.setType(PaymentTransactionType.PAY_LATER);
		final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(paymentTransactionEntry);
		paymentTransaction.setEntries(paymentTransactionEntries);
		final List<PaymentTransactionModel> paymentTransactions = Collections.singletonList(paymentTransaction);
		orderModel.setPaymentTransactions(paymentTransactions);

		Mockito.when(bookingService.getOrderModelFromStore(Mockito.anyString())).thenReturn(orderModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.NDC_ORDER_PROCESS))
				.thenReturn("fulfilmentProcessDefinitionName");
		final OrderProcessModel businessProcessModel = new OrderProcessModel();
		Mockito.when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(businessProcessModel);

		final OrderViewRS orderViewRS = defaultNDCOrderFacade.payOrder(orderCreateRQ);
		Assert.assertNotNull(orderViewRS);
	}

	@Test(expected = NDCOrderException.class)
	public void testChangeOrderWithException() throws NDCOrderException
	{
		final OrderChangeRQ orderChangeRQ = createOrderChangeRQ();
		prepareChangeOrderMock(orderChangeRQ);

		final AmendOrderOfferFilterStrategy amendOrderOfferFilterStrategy = Mockito.mock(AmendOrderOfferFilterStrategy.class);
		amendOrderOfferFilterStrategyList.add(amendOrderOfferFilterStrategy);
		Mockito.when(amendOrderOfferFilterStrategy.filterOffer(Mockito.any(OrderModel.class),
				Mockito.anyListOf(TransportOfferingModel.class), Mockito.anyListOf(String.class))).thenReturn(false);

		defaultNDCOrderFacade.changeOrder(orderChangeRQ);
	}

	@Test
	public void testChangeOrder() throws NDCOrderException
	{
		final OrderChangeRQ orderChangeRQ = createOrderChangeRQ();
		prepareChangeOrderMock(orderChangeRQ);
		orderChangeRQ.getQuery().getOrder().getActionType().setValue("ADD_ACCOMMODATION");

		final AmendOrderOfferFilterStrategy amendOrderOfferFilterStrategy = Mockito.mock(AmendOrderOfferFilterStrategy.class);
		amendOrderOfferFilterStrategyList.add(amendOrderOfferFilterStrategy);
		Mockito.when(amendOrderOfferFilterStrategy.filterOffer(Mockito.any(OrderModel.class),
				Mockito.anyListOf(TransportOfferingModel.class), Mockito.anyListOf(String.class))).thenReturn(true);
		Mockito.when(amendOrderStrategyMap.containsKey(Mockito.any(NDCActionType.class))).thenReturn(true);

		final AmendOrderStrategy amendOrderStrategy = Mockito.mock(AmendOrderStrategy.class);
		Mockito.when(amendOrderStrategyMap.get(Mockito.any(NDCActionType.class))).thenReturn(amendOrderStrategy);

		final OrderModel amendOrder = new OrderModel();
		Mockito.when(amendOrderStrategy.amendOrder(Mockito.any(OrderModel.class), Mockito.any(OrderChangeRQ.class)))
				.thenReturn(amendOrder);

		final OrderViewRS orderViewRS = defaultNDCOrderFacade.changeOrder(orderChangeRQ);
		Assert.assertNotNull(orderViewRS);
	}

	protected OrderChangeRQ createOrderChangeRQ() throws NDCOrderException
	{
		final OrderChangeRQ orderChangeRQ = new OrderChangeRQ();
		final de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query query = new de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query();
		final Order order = new Order();
		final OrderIDType orderId = new OrderIDType();
		orderId.setValue("OrderIdValue");
		order.setOrderID(orderId);
		final ActionType actionType = new ActionType();
		actionType.setValue("REMOVE_PASSENGER");
		order.setActionType(actionType);
		final OrderItemRepriceType orderItems = new OrderItemRepriceType();
		final OrderItem orderItem = new OrderItem();
		final OrderItemAssociationType associations = new OrderItemAssociationType();

		/*
		 * final ProfileID profileId = new ProfileID(); profileId.setValue("ProfileID");
		 */

		final de.hybris.platform.ndcfacades.ndc.OrderItemAssociationType.Passengers associationsPassengers = new de.hybris.platform.ndcfacades.ndc.OrderItemAssociationType.Passengers();
		/*
		 * final de.hybris.platform.ndcfacades.ndc.Passenger associationsPassenger = new
		 * de.hybris.platform.ndcfacades.ndc.Passenger(); associationsPassenger.setProfileID(profileId);
		 * associationsPassengers.getPassengerReferences().add(associationsPassenger);
		 */
		associations.setPassengers(associationsPassengers);

		orderItem.setAssociations(associations);
		final ItemIDType orderItemId = new ItemIDType();
		orderItemId.setValue("orderItemId");
		orderItem.setOrderItemID(orderItemId);
		orderItems.getOrderItem().add(orderItem);
		order.setOrderItems(orderItems);
		query.setOrder(order);

		final de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Passengers passengers = new de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Passengers();
		/*
		 * final Passenger passenger = new Passenger(); passenger.setProfileID(profileId);
		 * passengers.getPassenger().add(passenger);
		 */
		query.setPassengers(passengers);

		final de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments payments = new de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments();
		final Payment payment = new Payment();
		final SimpleCurrencyPriceType amount = new SimpleCurrencyPriceType();
		amount.setValue(BigDecimal.valueOf(100));
		payment.setAmount(amount);
		payments.getPayment().add(payment);
		query.setPayments(payments);

		orderChangeRQ.setQuery(query);

		return orderChangeRQ;
	}

	protected void prepareChangeOrderMock(final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		final OrderModel orderModel = new OrderModel();
		final UserModel user = new UserModel();
		orderModel.setUser(user);
		orderModel.setCode("1001");
		orderModel.setTotalPrice(90d);

		final PaymentTransactionModel paymentTransaction = new PaymentTransactionModel();
		final PaymentTransactionEntryModel paymentTransactionEntry = new PaymentTransactionEntryModel();
		paymentTransactionEntry.setType(PaymentTransactionType.CAPTURE);
		final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(paymentTransactionEntry);
		paymentTransaction.setEntries(paymentTransactionEntries);
		final List<PaymentTransactionModel> paymentTransactions = Collections.singletonList(paymentTransaction);
		orderModel.setPaymentTransactions(paymentTransactions);

		Mockito.when(bookingService.getOrderModelFromStore(Mockito.anyString())).thenReturn(orderModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);

		final ReservationData reservationData = new ReservationData();
		final PriceData totalToPay = new PriceData();
		totalToPay.setValue(BigDecimal.valueOf(100));
		reservationData.setTotalToPay(totalToPay);

		final ReservationItemData reservationItemData = new ReservationItemData();
		final ItineraryData reservationItinerary = new ItineraryData();
		final TravellerData traveller = new TravellerData();
		traveller.setUid("travellerUid");
		final List<TravellerData> travellers = Collections.singletonList(traveller);
		reservationItinerary.setTravellers(travellers);
		reservationItemData.setReservationItinerary(reservationItinerary);
		final List<ReservationItemData> reservationItems = Collections.singletonList(reservationItemData);
		reservationData.setReservationItems(reservationItems);

		Mockito.when(bookingFacade.getBookingByBookingReference(Mockito.anyString())).thenReturn(reservationData);
		final ActionTypeOption actionTypeOption = ActionTypeOption.ACCEPT_BOOKING;
		Mockito.when(orderChangeActionToBookingActionMapping.get(Mockito.anyString())).thenReturn(actionTypeOption);

		final BookingActionStrategy strategy = Mockito.mock(BookingActionStrategy.class);
		Mockito.when(bookingActionStrategyMap.get(actionTypeOption)).thenReturn(strategy);

		final BookingActionEnabledEvaluatorStrategy bookingActionEnabledEvaluatorStrategy = Mockito
				.mock(BookingActionEnabledEvaluatorStrategy.class);
		Mockito.when(bookingActionEnabledCalculationStrategiesMap.get(actionTypeOption))
				.thenReturn(Collections.singletonList(bookingActionEnabledEvaluatorStrategy));
		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.IMPOSSIBLE_TO_PERFORM_SPECIFIED_ACTION))
				.thenReturn("IMPOSSIBLE_TO_PERFORM_SPECIFIED_ACTION");

		Mockito.when(reservationFacade.getBasicReservationData(orderModel)).thenReturn(reservationData);
		Mockito.when(reservationFacade.getReservationData(Mockito.any(OrderModel.class))).thenReturn(reservationData);
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		final List<TransportOfferingModel> transportOfferings = Collections.singletonList(transportOffering);

		final OrderItem orderItem = orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem().stream().findFirst().get();
		Mockito.when(ndcOfferItemIdResolver.getTransportOfferingFromNDCOfferItemId(orderItem.getOrderItemID().getValue()))
				.thenReturn(transportOfferings);
	}

	@Test
	public void testIsAmendmentOrder()
	{
		OrderModel orderModel = null;
		Assert.assertFalse(defaultNDCOrderFacade.isAmendmentOrder(orderModel));

		orderModel = new OrderModel();
		orderModel.setOriginalOrder(new OrderModel());
		Assert.assertTrue(defaultNDCOrderFacade.isAmendmentOrder(orderModel));
	}

	@Test
	public void testGetOriginalOrderCode()
	{
		final OrderModel orderModel = new OrderModel();
		Assert.assertEquals(StringUtils.EMPTY, defaultNDCOrderFacade.getOriginalOrderCode(orderModel));

		final OrderModel originalOrder = new OrderModel();
		orderModel.setCode("10001");
		orderModel.setOriginalOrder(originalOrder);
		Assert.assertEquals(orderModel.getCode(), defaultNDCOrderFacade.getOriginalOrderCode(orderModel));
	}
}
