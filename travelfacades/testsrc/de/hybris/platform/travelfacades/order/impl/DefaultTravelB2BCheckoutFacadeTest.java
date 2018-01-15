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
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultTravelB2BCheckoutFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelB2BCheckoutFacadeTest
{
	@InjectMocks
	DefaultTravelB2BCheckoutFacade defaultTravelB2BCheckoutFacade;
	@Mock
	private CheckoutFacade travelCheckoutFacade;
	@Mock
	private CartService cartService;
	@Mock
	private CommerceCheckoutService commerceCheckoutService;
	@Mock
	CartFacade cartFacade;

	@Test
	public void testAuthorizePayment()
	{
		final CartModel cart = new CartModel();

		given(cartFacade.hasSessionCart()).willReturn(true);
		given(cartService.getSessionCart()).willReturn(null);
		Assert.assertFalse(defaultTravelB2BCheckoutFacade.authorizePayment("securityCode"));

		given(cartService.getSessionCart()).willReturn(cart);
		Assert.assertTrue(defaultTravelB2BCheckoutFacade.authorizePayment("securityCode"));

		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = new CreditCardPaymentInfoModel();
		cart.setPaymentInfo(creditCardPaymentInfoModel);
		given(travelCheckoutFacade.authorizePayment("securityCode")).willReturn(true);
		Assert.assertTrue(defaultTravelB2BCheckoutFacade.authorizePayment("securityCode"));

		final InvoicePaymentInfoModel invoicePaymentInfoModel = new InvoicePaymentInfoModel();
		cart.setPaymentInfo(invoicePaymentInfoModel);
		final PaymentTransactionEntryModel paymentTransactionEntryModel=new PaymentTransactionEntryModel();
		paymentTransactionEntryModel.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		given(commerceCheckoutService.authorizePayment(Matchers.any(CommerceCheckoutParameter.class)))
				.willReturn(paymentTransactionEntryModel);
		Assert.assertTrue(defaultTravelB2BCheckoutFacade.authorizePayment("securityCode"));

	}

}
