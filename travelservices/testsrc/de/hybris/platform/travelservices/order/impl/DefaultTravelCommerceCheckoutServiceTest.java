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

package de.hybris.platform.travelservices.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommercePaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultTravelCommerceCheckoutService}
 */

@UnitTest
public class DefaultTravelCommerceCheckoutServiceTest
{
	private DefaultTravelCommerceCheckoutService travelCommerceCheckoutService;
	private CartModel cartModel;
	private PaymentInfoModel paymentInfo;
	private CommerceCheckoutParameter parameter;
	private PaymentTransactionEntryModel paymentTransactionModel;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private CommercePaymentAuthorizationStrategy commercePaymentAuthorizationStrategy;

	@Mock
	private ModelService modelService;

	@Mock
	private UserService userService;

	@Mock
	private TimeService timeService;

	@Mock
	private PaymentService paymentService;

	@Mock
	private SessionService sessionService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		travelCommerceCheckoutService = new DefaultTravelCommerceCheckoutService()
		{
			@Override
			public String getPaymentProvider()
			{
				return "TEST_PAYMENT_PROVIDER";
			}
		};
		travelCommerceCheckoutService.setBaseStoreService(baseStoreService);
		travelCommerceCheckoutService.setCustomerAccountService(customerAccountService);
		travelCommerceCheckoutService.setCommercePaymentAuthorizationStrategy(commercePaymentAuthorizationStrategy);
		travelCommerceCheckoutService.setModelService(modelService);
		travelCommerceCheckoutService.setUserService(userService);
		travelCommerceCheckoutService.setTimeService(timeService);
		travelCommerceCheckoutService.setPaymentService(paymentService);
		travelCommerceCheckoutService.setSessionService(sessionService);

		parameter = new CommerceCheckoutParameter();
		paymentTransactionModel = new PaymentTransactionEntryModel();
		paymentInfo = new PaymentInfoModel();

		cartModel = new CartModel();
		cartModel.setPaymentInfo(paymentInfo);
		cartModel.setTotalPrice(80.0);
		cartModel.setTotalTax(17.5);
		cartModel.setNet(true);
		parameter.setCart(cartModel);

	}


	@Test
	public void testAuthorizePaymentForNotNull()
	{
		parameter.setAuthorizationAmount(new BigDecimal(80.0));
		final CartModel cartModel = mock(CartModel.class);
		parameter.setCart(cartModel);
		final PaymentInfoModel paymentInfo = mock(PaymentInfoModel.class);

		given(cartModel.getPaymentInfo()).willReturn(paymentInfo);
		given(commercePaymentAuthorizationStrategy.authorizePaymentAmount(parameter)).willReturn(paymentTransactionModel);

		final PaymentTransactionEntryModel paymentTransactionModelTest = travelCommerceCheckoutService.authorizePayment(parameter);
		assertNotNull(paymentTransactionModel);
		assertEquals(paymentTransactionModel, paymentTransactionModelTest);
	}


	@Test
	public void testAuthorizePaymentForNew()
	{
		cartModel.setOriginalOrder(null);

		final CartModel masterCartModel = mock(CartModel.class);

		given(masterCartModel.getNet().booleanValue() && masterCartModel.getStore() != null
				&& masterCartModel.getStore().getExternalTaxEnabled().booleanValue()).willReturn(true);
		given(commercePaymentAuthorizationStrategy.authorizePaymentAmount(parameter)).willReturn(paymentTransactionModel);

		final PaymentTransactionEntryModel paymentTransactionModelTest = travelCommerceCheckoutService.authorizePayment(parameter);
		assertNotNull(paymentTransactionModel);
		assertEquals(paymentTransactionModel, paymentTransactionModelTest);
	}


	@Test
	public void testAuthorizePaymentForAmendment()
	{
		final OrderModel originalOrder = mock(OrderModel.class);
		cartModel.setOriginalOrder(originalOrder);

		given(travelCommerceCheckoutService.getCustomerAccountService().getOrderForCode(cartModel.getOriginalOrder().getCode(),
				travelCommerceCheckoutService.getBaseStoreService().getCurrentBaseStore())).willReturn(originalOrder);
		given(commercePaymentAuthorizationStrategy.authorizePaymentAmount(parameter)).willReturn(paymentTransactionModel);

		given(originalOrder.getNet().booleanValue() && originalOrder.getStore() != null
				&& originalOrder.getStore().getExternalTaxEnabled().booleanValue()).willReturn(true);

		final PaymentTransactionEntryModel paymentTransactionModelTest = travelCommerceCheckoutService.authorizePayment(parameter);
		assertNotNull(paymentTransactionModel);
		assertEquals(paymentTransactionModel, paymentTransactionModelTest);
	}


	@Test
	public void testAuthorizePaymentForAmendmentWithExternalTax()
	{
		final OrderModel originalOrder = mock(OrderModel.class);
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		cartModel.setOriginalOrder(originalOrder);

		given(originalOrder.getNet()).willReturn(true);
		given(originalOrder.getStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getExternalTaxEnabled()).willReturn(true);

		given(travelCommerceCheckoutService.getCustomerAccountService().getOrderForCode(cartModel.getOriginalOrder().getCode(),
				travelCommerceCheckoutService.getBaseStoreService().getCurrentBaseStore())).willReturn(originalOrder);
		given(commercePaymentAuthorizationStrategy.authorizePaymentAmount(parameter)).willReturn(paymentTransactionModel);

		final PaymentTransactionEntryModel paymentTransactionModelTest = travelCommerceCheckoutService.authorizePayment(parameter);
		assertNotNull(paymentTransactionModel);
		assertEquals(paymentTransactionModel, paymentTransactionModelTest);
	}

	@Test
	public void testSetEntriesAgainstTransaction()
	{
		final OrderModel orderModel = new OrderModel();
		final List<AbstractOrderEntryModel> abstractOrderEntries = new ArrayList<>();
		abstractOrderEntries.add(createOrderEntry(OrderEntryType.TRANSPORT, 0));
		abstractOrderEntries.add(createOrderEntry(OrderEntryType.TRANSPORT, 1));
		orderModel.setEntries(abstractOrderEntries);
		final PaymentTransactionModel paymentModel = new PaymentTransactionModel();
		paymentModel.setAbstractOrderEntries(abstractOrderEntries);
		paymentModel.setOrder(orderModel);

		given(sessionService.getAttribute(Matchers.anyString())).willReturn(Matchers.any());

		travelCommerceCheckoutService.setEntriesAgainstTransaction(paymentModel, Arrays.asList(1, 3));
	}

	@Test
	public void testGetExistingTransactionForNullOriginalOrder()
	{
		final CartModel cartModel = Mockito.mock(CartModel.class);
		final List<AbstractOrderEntryModel> abstractOrderEntries = new ArrayList<>();
		abstractOrderEntries.add(createOrderEntry(OrderEntryType.TRANSPORT, 0));
		abstractOrderEntries.add(createOrderEntry(OrderEntryType.TRANSPORT, 1));
		cartModel.setEntries(abstractOrderEntries);
		final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
		commerceCheckoutParameter.setCart(cartModel);
		travelCommerceCheckoutService.getExistingTransaction(Arrays.asList(1, 3), commerceCheckoutParameter);
		Mockito.verify(cartModel, Mockito.times(0)).getEntries();
	}

	@Test
	public void testGetExistingTransaction()
	{
		final CartModel cartModel = new CartModel();
		final List<AbstractOrderEntryModel> abstractOrderEntries = new ArrayList<>();
		abstractOrderEntries.add(createOrderEntry(OrderEntryType.ACCOMMODATION, 0));
		cartModel.setEntries(abstractOrderEntries);

		final OrderModel orderModel = new OrderModel();
		final PaymentTransactionModel paymentTransactionModel = createPaymentTransaction(OrderEntryType.ACCOMMODATION,
				PaymentTransactionType.CAPTURE, 100d);
		paymentTransactionModel.setAbstractOrderEntries(abstractOrderEntries);
		orderModel.setPaymentTransactions(Stream.of(paymentTransactionModel).collect(Collectors.toList()));
		cartModel.setOriginalOrder(orderModel);

		final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
		commerceCheckoutParameter.setCart(cartModel);

		Assert.assertNotNull(travelCommerceCheckoutService.getExistingTransaction(Arrays.asList(0), commerceCheckoutParameter));
	}

	@Test
	public void testGetExistingTransactionForNull()
	{
		final CartModel cartModel = new CartModel();
		final List<AbstractOrderEntryModel> abstractOrderEntries = new ArrayList<>();
		abstractOrderEntries.add(createOrderEntry(OrderEntryType.ACCOMMODATION, 0));
		cartModel.setEntries(abstractOrderEntries);

		final OrderModel orderModel = new OrderModel();
		final PaymentTransactionModel paymentTransactionModel = createPaymentTransaction(OrderEntryType.ACCOMMODATION,
				PaymentTransactionType.CAPTURE, 100d);
		paymentTransactionModel.setAbstractOrderEntries(new ArrayList<>());
		orderModel.setPaymentTransactions(Stream.of(paymentTransactionModel).collect(Collectors.toList()));
		cartModel.setOriginalOrder(orderModel);

		final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
		commerceCheckoutParameter.setCart(cartModel);

		Assert.assertNull(travelCommerceCheckoutService.getExistingTransaction(Arrays.asList(0), commerceCheckoutParameter));
	}

	@Test
	public void testCreateRefundPaymentTransaction()
	{
		final OrderModel orderModel = new OrderModel();
		final PaymentTransactionModel paymentTransaction = new PaymentTransactionModel();
		paymentTransaction.setRequestId("TEST_REQUEST_ID");
		paymentTransaction.setCurrency(new CurrencyModel());
		paymentTransaction.setInfo(new PaymentInfoModel());
		paymentTransaction.setRequestToken("TEST_REQUEST_TOKEN");
		orderModel.setPaymentTransactions(Stream.of(paymentTransaction).collect(Collectors.toList()));

		final UserModel currentUser = new UserModel();
		currentUser.setUid("TEST_USER_ID");
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.when(modelService.create(PaymentTransactionModel.class)).thenReturn(new PaymentTransactionModel());
		Mockito.when(modelService.create(PaymentTransactionEntryModel.class)).thenReturn(new PaymentTransactionEntryModel());
		Mockito.when(timeService.getCurrentTime()).thenReturn(new Date());
		Mockito.when(paymentService.getNewPaymentTransactionEntryCode(Matchers.any(PaymentTransactionModel.class),
				Matchers.any(PaymentTransactionType.class))).thenReturn("TEST_NEW_PAYMENT_TRANSACTION_ENTRY_CODE");
		Assert.assertNotNull(travelCommerceCheckoutService.createRefundPaymentTransaction(orderModel, BigDecimal.valueOf(100d),
				Stream.of(createOrderEntry(OrderEntryType.ACCOMMODATION, 0)).collect(Collectors.toList())));
	}

	@Test
	public void testCreateRefundPaymentTransactionForNullPaymentTransactions()
	{
		final OrderModel orderModel = new OrderModel();

		orderModel.setPaymentTransactions(Collections.emptyList());
		final UserModel currentUser = new UserModel();
		currentUser.setUid("TEST_USER_ID");
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.when(modelService.create(PaymentTransactionModel.class)).thenReturn(new PaymentTransactionModel());
		Mockito.when(modelService.create(PaymentTransactionEntryModel.class)).thenReturn(new PaymentTransactionEntryModel());
		Mockito.when(timeService.getCurrentTime()).thenReturn(new Date());
		Mockito.when(paymentService.getNewPaymentTransactionEntryCode(Matchers.any(PaymentTransactionModel.class),
				Matchers.any(PaymentTransactionType.class))).thenReturn("TEST_NEW_PAYMENT_TRANSACTION_ENTRY_CODE");
		Assert.assertNull(travelCommerceCheckoutService.createRefundPaymentTransaction(orderModel, BigDecimal.valueOf(100d),
				Collections.emptyList()));
	}

	@Test
	public void testCreateRefundPaymentTransactionForEmptyEntries()
	{
		final OrderModel orderModel = new OrderModel();

		orderModel.setPaymentTransactions(Collections.emptyList());
		final UserModel currentUser = new UserModel();
		currentUser.setUid("TEST_USER_ID");
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.when(modelService.create(PaymentTransactionModel.class)).thenReturn(new PaymentTransactionModel());
		Mockito.when(modelService.create(PaymentTransactionEntryModel.class)).thenReturn(new PaymentTransactionEntryModel());
		Mockito.when(timeService.getCurrentTime()).thenReturn(new Date());
		Mockito.when(paymentService.getNewPaymentTransactionEntryCode(Matchers.any(PaymentTransactionModel.class),
				Matchers.any(PaymentTransactionType.class))).thenReturn("TEST_NEW_PAYMENT_TRANSACTION_ENTRY_CODE");
		Assert.assertNull(travelCommerceCheckoutService.createRefundPaymentTransaction(orderModel, BigDecimal.valueOf(100d),
				Collections.emptyList()));
	}

	@Test
	public void testLinkEntriesToOldPaymentTransactions()
	{
		final AbstractOrderEntryModel oldEntry1 = new AbstractOrderEntryModel();
		oldEntry1.setProduct(new RoomRateProductModel());
		final PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
		paymentTransactionModel.setAbstractOrderEntries(Stream.of(oldEntry1).collect(Collectors.toList()));
		oldEntry1.setPaymentTransaction(Stream.of(paymentTransactionModel).collect(Collectors.toList()));

		final AccommodationOrderEntryGroupModel orderEntryGroup = new AccommodationOrderEntryGroupModel();
		orderEntryGroup.setEntries(Stream.of(oldEntry1).collect(Collectors.toList()));
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		newEntry.setProduct(new RoomRateProductModel());
		Assert.assertTrue(travelCommerceCheckoutService.linkEntriesToOldPaymentTransactions(orderEntryGroup,
				Stream.of(newEntry).collect(Collectors.toList())));
	}

	@Test
	public void testLinkEntriesToOldPaymentTransactionsForNullScenarios()
	{
		final AbstractOrderEntryModel oldEntry1 = new AbstractOrderEntryModel();
		oldEntry1.setProduct(new RoomRateProductModel());
		final PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
		paymentTransactionModel.setAbstractOrderEntries(Stream.of(oldEntry1).collect(Collectors.toList()));
		oldEntry1.setPaymentTransaction(Stream.of(paymentTransactionModel).collect(Collectors.toList()));

		final AccommodationOrderEntryGroupModel orderEntryGroup = new AccommodationOrderEntryGroupModel();
		orderEntryGroup.setEntries(Stream.of(oldEntry1).collect(Collectors.toList()));
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		newEntry.setProduct(new RoomRateProductModel());
		Assert.assertFalse(travelCommerceCheckoutService.linkEntriesToOldPaymentTransactions(null,
				Stream.of(newEntry).collect(Collectors.toList())));
		Assert.assertFalse(
				travelCommerceCheckoutService.linkEntriesToOldPaymentTransactions(orderEntryGroup, Collections.emptyList()));
	}

	private PaymentTransactionModel createPaymentTransaction(final OrderEntryType orderType,
			final PaymentTransactionType paymentTransactionType, final double amount)
	{
		final List<AbstractOrderEntryModel> abstractOrderEntries = new ArrayList<>();
		abstractOrderEntries.add(createOrderEntry(orderType, 0));
		final PaymentTransactionModel paymentModel = new PaymentTransactionModel();
		paymentModel.setAbstractOrderEntries(abstractOrderEntries);

		final List<PaymentTransactionEntryModel> entries = new ArrayList<>();
		final PaymentTransactionEntryModel paymentTransactionEntryModel = createPaymentTransactionEntryModel(
				paymentTransactionType);
		entries.add(paymentTransactionEntryModel);
		paymentModel.setEntries(entries);
		paymentModel.setPlannedAmount(BigDecimal.valueOf(amount));
		return paymentModel;
	}

	private AbstractOrderEntryModel createOrderEntry(final OrderEntryType orderType, final int entryNum)
	{
		final AbstractOrderEntryModel order = new AbstractOrderEntryModel();
		order.setEntryNumber(entryNum);
		order.setType(orderType);

		return order;
	}

	private PaymentTransactionEntryModel createPaymentTransactionEntryModel(final PaymentTransactionType paymentTransactionType)
	{
		final PaymentTransactionEntryModel entryModel = new PaymentTransactionEntryModel();
		entryModel.setType(paymentTransactionType);
		return entryModel;
	}
}
