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

package de.hybris.platform.travelfacades.order.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.order.impl.DefaultTravelCommerceCheckoutService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultTravelCheckoutFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCheckoutFacadeTest
{
	@InjectMocks
	DefaultTravelCheckoutFacade defaultTravelCheckoutFacade;
	@Mock
	private SessionService sessionService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;
	@Mock
	private final DefaultTravelCommerceCheckoutService commerceCheckoutService = new DefaultTravelCommerceCheckoutService();
	@Mock
	CartFacade cartFacade;
	@Mock
	private CartService cartService;

	@Test
	public void testAuthorizePayment()
	{
		defaultTravelCheckoutFacade = new DefaultTravelCheckoutFacade()
		{
			@Override
			protected boolean checkIfCurrentUserIsTheCartUser()
			{
				return false;
			}
		};
		defaultTravelCheckoutFacade.setSessionService(sessionService);
		defaultTravelCheckoutFacade.setConfigurationService(configurationService);
		defaultTravelCheckoutFacade.setCommerceCheckoutService(commerceCheckoutService);
		defaultTravelCheckoutFacade.setCartFacade(cartFacade);
		defaultTravelCheckoutFacade.setCartService(cartService);

		final CartModel cart = new CartModel();

		given(cartFacade.hasSessionCart()).willReturn(true);
		given(cartService.getSessionCart()).willReturn(cart);

		Assert.assertTrue(defaultTravelCheckoutFacade.authorizePayment("securityCode"));


		defaultTravelCheckoutFacade = new DefaultTravelCheckoutFacade()
		{
			@Override
			protected boolean checkIfCurrentUserIsTheCartUser()
			{
				return true;
			}
		};
		defaultTravelCheckoutFacade.setSessionService(sessionService);
		defaultTravelCheckoutFacade.setConfigurationService(configurationService);
		defaultTravelCheckoutFacade.setCommerceCheckoutService(commerceCheckoutService);
		defaultTravelCheckoutFacade.setCartFacade(cartFacade);
		defaultTravelCheckoutFacade.setCartService(cartService);

		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = new CreditCardPaymentInfoModel();
		creditCardPaymentInfoModel.setSubscriptionId("SubscriptionId");
		cart.setPaymentInfo(creditCardPaymentInfoModel);
		final PaymentTransactionData paymentTransactionData=new PaymentTransactionData();
		paymentTransactionData.setTransactionAmount(0d);
		final List<PaymentTransactionData> transactions = new ArrayList();
		transactions.add(paymentTransactionData);
		final PaymentTransactionEntryModel paymentTransactionEntryModel = new PaymentTransactionEntryModel();
		paymentTransactionEntryModel.setTransactionStatus(TransactionStatus.ACCEPTED.name());

		given(sessionService.getAttribute("paymentTransactions")).willReturn(Collections.EMPTY_LIST);
		given(commerceCheckoutService.authorizePayment(Matchers.any(CommerceCheckoutParameter.class)))
				.willReturn(paymentTransactionEntryModel);

		Assert.assertTrue(defaultTravelCheckoutFacade.authorizePayment("securityCode"));


		final PaymentTransactionData paymentTransactionData1 = new PaymentTransactionData();
		paymentTransactionData1.setTransactionAmount(-10d);
		transactions.add(paymentTransactionData1);
		paymentTransactionData1.setEntryNumbers(Arrays.asList(1));
		final PaymentTransactionModel refundTransaction=new PaymentTransactionModel();
		refundTransaction.setEntries(Collections.singletonList(paymentTransactionEntryModel));
		paymentTransactionEntryModel.setPaymentTransaction(refundTransaction);
		final PaymentTransactionData paymentTransactionData2 = new PaymentTransactionData();
		paymentTransactionData2.setTransactionAmount(10d);
		transactions.add(paymentTransactionData2);

		given(configurationService.getConfiguration().getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT))
				.willReturn(10d);
		given(sessionService.getAttribute("paymentTransactions")).willReturn(transactions);
		given(commerceCheckoutService.createRefundPaymentTransaction(cart,
									BigDecimal.valueOf(Math.abs(paymentTransactionData1.getTransactionAmount())), Collections.emptyList())).willReturn(refundTransaction);
		BDDMockito.willDoNothing().given(commerceCheckoutService).setEntriesAgainstTransaction(
				paymentTransactionEntryModel.getPaymentTransaction(), paymentTransactionData1.getEntryNumbers());

		/*
		 * a payment transaction with a negative amount is present which will return false
		 */
		Assert.assertFalse(defaultTravelCheckoutFacade.authorizePayment("securityCode"));

	}

}
