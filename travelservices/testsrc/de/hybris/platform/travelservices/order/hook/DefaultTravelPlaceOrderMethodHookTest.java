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

package de.hybris.platform.travelservices.order.hook;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.ConsignmentsStatusUpdateStrategy;
import de.hybris.platform.travelservices.strategies.TravelOrderCodeGenerationStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultTravelPlaceOrderMethodHook}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelPlaceOrderMethodHookTest
{
	@InjectMocks
	private DefaultTravelPlaceOrderMethodHook travelPlaceOrderMethodHook;

	@Mock
	private ModelService modelService;

	@Mock
	private CalculationService calculationService;

	@Mock
	private ExternalTaxesService externalTaxesService;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private OrderHistoryService orderHistoryService;

	@Mock
	private ConsignmentsStatusUpdateStrategy consignmentsStatusUpdateStrategy;

	@Mock
	private TravelCommerceStockService commerceStockService;

	@Mock
	private BookingService bookingService;

	@Mock
	private TravelOrderCodeGenerationStrategy travelOrderCodeGenerationStrategy;


	@Before
	public void setUp()
	{
		travelPlaceOrderMethodHook.setBookingService(bookingService);
	}

	@Test
	public void testBeforeSubmitOrder() throws CalculationException, InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel order = testDataSetUp.createOrder(testDataSetUp.createPaymentInfoModel(testDataSetUp.createAddressModel()),
				null, null, BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION, null);
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(order);

		doNothing().when(modelService).save(order);

		doNothing().when(calculationService).calculateTotals(order, false);
		when(externalTaxesService.calculateExternalTaxes(order)).thenReturn(Boolean.TRUE);

		doNothing().when(modelService).refresh(order);

		travelPlaceOrderMethodHook
				.beforeSubmitOrder(testDataSetUp.createCommerceCheckoutParameter(testDataSetUp.createCartModel(order)), result);

		verify(modelService, times(1)).save(order);
	}

	@Test
	public void testBeforeSubmitOrderForCalculationException() throws CalculationException, InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final OrderModel order = testDataSetUp.createOrder(testDataSetUp.createPaymentInfoModel(testDataSetUp.createAddressModel()),
				null, null, BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION, null);
		final CartModel cartModel = testDataSetUp.createCartModel(order);
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(order);

		doNothing().when(modelService).save(order);

		doThrow(new CalculationException("Exception")).when(calculationService).calculateTotals(order, false);
		when(externalTaxesService.calculateExternalTaxes(order)).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(order))
				.thenReturn(Collections.singletonList(testDataSetUp.createAccommodationOrderEntryGroupModel()));
		doNothing().when(modelService).refresh(order);

		travelPlaceOrderMethodHook.beforeSubmitOrder(testDataSetUp.createCommerceCheckoutParameter(cartModel), result);

		verify(modelService, times(1)).save(order);
	}

	@Test
	public void testBeforeSubmitOrderNullOrder() throws CalculationException, InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(null);
		final OrderModel order = testDataSetUp.createOrder(testDataSetUp.createPaymentInfoModel(null), null, null,
				BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION, null);
		travelPlaceOrderMethodHook
				.beforeSubmitOrder(testDataSetUp.createCommerceCheckoutParameter(testDataSetUp.createCartModel(order)), result);
	}

	@Test
	public void testBeforeSubmitOrderNullPaymentInfo() throws CalculationException, InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel order = testDataSetUp.createOrder(null, null, null, BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION,
				null);
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(order);
		travelPlaceOrderMethodHook
				.beforeSubmitOrder(testDataSetUp.createCommerceCheckoutParameter(testDataSetUp.createCartModel(order)), result);
	}

	@Test
	public void testBeforeSubmitOrderNullBillingAddress() throws CalculationException, InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel order = testDataSetUp.createOrder(testDataSetUp.createPaymentInfoModel(null), null, null,
				BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION, null);
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(order);
		travelPlaceOrderMethodHook
				.beforeSubmitOrder(testDataSetUp.createCommerceCheckoutParameter(testDataSetUp.createCartModel(order)), result);
	}

	@Test
	public void testAfterPlaceOrderNullOrder() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(null);
		travelPlaceOrderMethodHook.afterPlaceOrder(testDataSetUp.createCommerceCheckoutParameter(null), result);
	}

	@Test
	public void testAfterPlaceOrderNullOriginalOrder() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel order = testDataSetUp.createOrder(null, null, null, BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION,
				null);
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(order);
		travelPlaceOrderMethodHook.afterPlaceOrder(testDataSetUp.createCommerceCheckoutParameter(null), result);
	}

	@Test
	public void testAfterPlaceOrderSameUser() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final UserModel user = testDataSetUp.createUserModel("abc@xyz.com");
		final OrderModel newOrder = testDataSetUp.createOrder(null, "0002", "0001",
				BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION, user);
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(newOrder);

		final OrderModel oldOrder = testDataSetUp.createOrder(null, "0001", null,
				BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION, user);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderForCode("0001", baseStoreModel)).thenReturn(oldOrder);
		final OrderModel orderSnapshot = new OrderModel();
		when(orderHistoryService.createHistorySnapshot(oldOrder)).thenReturn(orderSnapshot);
		final OrderHistoryEntryModel entry = new OrderHistoryEntryModel();
		when(modelService.create(OrderHistoryEntryModel.class)).thenReturn(entry);
		doNothing().when(consignmentsStatusUpdateStrategy).updateConsignmentsStatus(newOrder, oldOrder);
		doNothing().when(modelService).remove(oldOrder);
		doNothing().when(modelService).saveAll(newOrder, entry);
		travelPlaceOrderMethodHook.afterPlaceOrder(testDataSetUp.createCommerceCheckoutParameter(null), result);
		assertEquals("0002", newOrder.getCode());
	}

	@Test
	public void testAfterPlaceOrderDifferentUser() throws InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final UserModel newUser = testDataSetUp.createUserModel("abc@xyz.com");
		final OrderModel newOrder = testDataSetUp.createOrder(null, "0002", "0001",
				BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION, newUser);
		final CommerceOrderResult result = testDataSetUp.createCommerceOrderResult(newOrder);

		final UserModel oldUser = testDataSetUp.createUserModel("123@456.com");
		final OrderModel oldOrder = testDataSetUp.createOrder(null, "0001", null,
				BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION, oldUser);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderForCode("0001", baseStoreModel)).thenReturn(oldOrder);
		final OrderModel orderSnapshot = new OrderModel();
		when(orderHistoryService.createHistorySnapshot(oldOrder)).thenReturn(orderSnapshot);
		final OrderHistoryEntryModel entry = new OrderHistoryEntryModel();
		when(modelService.create(OrderHistoryEntryModel.class)).thenReturn(entry);
		doNothing().when(consignmentsStatusUpdateStrategy).updateConsignmentsStatus(newOrder, oldOrder);
		doNothing().when(modelService).remove(oldOrder);
		doNothing().when(modelService).saveAll(newOrder, entry);
		travelPlaceOrderMethodHook.afterPlaceOrder(testDataSetUp.createCommerceCheckoutParameter(null), result);
		assertEquals("0002", newOrder.getCode());
		assertEquals("abc@xyz.com", newOrder.getUser().getUid());
	}

	@Test
	public void testBeforePlaceOrderAmendment() throws InsufficientStockLevelException, InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderModel originalOrder = new OrderModel();
		originalOrder.setCode("0001");
		final CartModel cart = testDataSetUp.createCartModel(originalOrder);
		final CommerceCheckoutParameter param = testDataSetUp.createCommerceCheckoutParameter(cart);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderForCode("0001", baseStoreModel)).thenReturn(originalOrder);
		doNothing().when(commerceStockService).adjustStockReservationForAmmendment(cart, originalOrder);
		travelPlaceOrderMethodHook.beforePlaceOrder(param);
		verify(commerceStockService).adjustStockReservationForAmmendment(cart, originalOrder);
	}

	@Test
	public void testBeforePlaceOrderPurchase() throws InsufficientStockLevelException, InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null);
		final CommerceCheckoutParameter param = testDataSetUp.createCommerceCheckoutParameter(cart);
		doNothing().when(commerceStockService).reserve(cart);
		travelPlaceOrderMethodHook.beforePlaceOrder(param);
		verify(commerceStockService).reserve(cart);
	}

	@Test(expected = InvalidCartException.class)
	public void testBeforePlaceOrderException() throws InsufficientStockLevelException, InvalidCartException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null);
		final CommerceCheckoutParameter param = testDataSetUp.createCommerceCheckoutParameter(cart);
		doThrow(InsufficientStockLevelException.class).when(commerceStockService).reserve(cart);
		travelPlaceOrderMethodHook.beforePlaceOrder(param);
	}

	private class TestDataSetUp
	{
		private CommerceOrderResult createCommerceOrderResult(final OrderModel order)
		{
			final CommerceOrderResult result = new CommerceOrderResult();
			result.setOrder(order);
			return result;
		}

		private OrderModel createOrder(final PaymentInfoModel payInfo, final String code, final String originalOrderCode,
				final BookingJourneyType bookingJourneyType, final UserModel user)
		{
			OrderModel originalOrder = null;
			if (originalOrderCode != null)
			{
				originalOrder = new OrderModel();
				originalOrder.setCode(originalOrderCode);
			}

			final OrderModel order = new OrderModel();
			order.setPaymentInfo(payInfo);
			order.setCode(code);
			order.setOriginalOrder(originalOrder);
			order.setUser(user);
			order.setBookingJourneyType(bookingJourneyType);
			return order;
		}

		private PaymentInfoModel createPaymentInfoModel(final AddressModel address)
		{
			final PaymentInfoModel payInfo = new PaymentInfoModel();
			payInfo.setBillingAddress(address);
			return payInfo;
		}

		private AddressModel createAddressModel()
		{
			final AddressModel add = new AddressModel();
			return add;
		}

		private CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart)
		{
			final CommerceCheckoutParameter checkoutParam = new CommerceCheckoutParameter();
			checkoutParam.setCart(cart);
			return checkoutParam;
		}

		private UserModel createUserModel(final String id)
		{
			final UserModel user = new UserModel();
			user.setUid(id);
			return user;
		}

		private CartModel createCartModel(final OrderModel orginalOrder)
		{
			final CartModel cart = new CartModel();
			cart.setOriginalOrder(orginalOrder);
			return cart;
		}

		private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroupModel()
		{
			final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();

			final AbstractOrderEntryModel entry1 = new OrderEntryModel();
			final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
			final List<AbstractOrderEntryModel> entries = new ArrayList<>();
			entries.add(entry1);
			entries.add(entry2);

			accommodationOrderEntryGroupModel.setEntries(entries);
			return accommodationOrderEntryGroupModel;
		}

	}

}
