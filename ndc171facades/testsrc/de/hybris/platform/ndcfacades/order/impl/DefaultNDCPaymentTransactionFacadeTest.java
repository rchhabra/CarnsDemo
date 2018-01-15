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
import de.hybris.platform.commerceservices.order.CommerceCardTypeService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments.Payment;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.Payments;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNDCPaymentTransactionFacadeTest
{
	@InjectMocks
	DefaultNDCPaymentTransactionFacade defaultNDCPaymentTransactionFacade;
	@Mock
	private CommerceCheckoutService commerceCheckoutService;
	@Mock
	private CommerceCardTypeService commerceCardTypeService;
	@Mock
	private ModelService modelService;

	@Test
	public void testCreatePaymentTransactionWithOrderCreateRQ()
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final Query query = new Query();
		final Payments payments=new Payments();
		final OrderPaymentFormType orderPayment = new OrderPaymentFormType();
		payments.getPayment().add(orderPayment);
		query.setPayments(payments);
		orderCreateRQ.setQuery(query);

		final OrderModel orderModel=new OrderModel();
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		orderModel.setEntries(entries);
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		orderModel.setPaymentTransactions(paymentTransactions);
		orderModel.setTotalPrice(100d);
		orderModel.setTotalTax(10d);

		defaultNDCPaymentTransactionFacade.createPaymentTransaction(orderCreateRQ, orderModel);
		Assert.assertTrue(CollectionUtils.isNotEmpty(orderModel.getPaymentTransactions()));
	}

	@Test
	public void testCreatePaymentTransactionWithOrderEntries()
	{
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		final BigDecimal totalToPay = BigDecimal.valueOf(110);
		final OrderModel orderModel = new OrderModel();
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		orderModel.setPaymentTransactions(paymentTransactions);

		defaultNDCPaymentTransactionFacade.createPaymentTransaction(totalToPay, orderModel, orderEntries);
		Assert.assertTrue(CollectionUtils.isNotEmpty(orderModel.getPaymentTransactions()));
	}

	@Test
	public void testCreatePayLaterTransaction()
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final Query query = new Query();
		final Payments payments=new Payments();
		query.setPayments(payments);
		orderCreateRQ.setQuery(query);

		final OrderModel orderModel = new OrderModel();
		final UserModel user = new UserModel();
		orderModel.setUser(user);

		final CreditCardType code = CreditCardType.VISA;
		final CardType cartType = new CardType("id", code, "description");
		Mockito.when(commerceCardTypeService.getCardTypeForCode(Mockito.anyString())).thenReturn(cartType);

		defaultNDCPaymentTransactionFacade.createPayLaterTransaction(orderCreateRQ, orderModel);
		Assert.assertTrue(Objects.nonNull(orderModel.getDeliveryAddress()));

		final OrderPaymentFormType orderPayment = new OrderPaymentFormType();
		payments.getPayment().add(orderPayment);
		final AddressModel address = new AddressModel();
		orderModel.setPaymentAddress(address);

		orderModel.setDeliveryAddress(null);
		defaultNDCPaymentTransactionFacade.createPayLaterTransaction(orderCreateRQ, orderModel);
		Assert.assertTrue(Objects.isNull(orderModel.getDeliveryAddress()));
	}

	@Test
	public void testIsPayLater()
	{
		final OrderChangeRQ orderChangeRQ=new OrderChangeRQ();
		final de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query orderChangeRQQuery = new de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query();
		final de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments orderChangeRQPayments = new de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments();
		final Payment payment = new Payment();
		orderChangeRQPayments.getPayment().add(payment);
		orderChangeRQQuery.setPayments(orderChangeRQPayments);
		orderChangeRQ.setQuery(orderChangeRQQuery);

		Assert.assertFalse(DefaultNDCPaymentTransactionFacade.isPayLater(orderChangeRQ));
	}
}
