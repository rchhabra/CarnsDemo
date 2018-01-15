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
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments.Payment;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.Payments;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType.Method;
import de.hybris.platform.ndcfacades.ndc.PaymentCardType;
import de.hybris.platform.ndcfacades.ndc.PaymentCardType.CardNumber;
import de.hybris.platform.ndcfacades.ndc.PaymentCardType.EffectiveExpireDate;
import de.hybris.platform.ndcfacades.ndc.PaymentCardType.SeriesCode;
import de.hybris.platform.ndcservices.model.NDCCreditCardTypeMappingModel;
import de.hybris.platform.ndcservices.services.NDCCreditCardTypeMappingService;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.dto.NewSubscription;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Currency;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNDCPaymentInfoFacadeTest
{
	@InjectMocks
	DefaultNDCPaymentInfoFacade defaultNDCPaymentInfoFacade;

	@Mock
	private CommerceCheckoutService commerceCheckoutService;
	@Mock
	private CommerceCardTypeService commerceCardTypeService;
	@Mock
	private PaymentService paymentService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;
	@Mock
	private NDCCreditCardTypeMappingService ndcCreditCardTypeMappingService;
	@Mock
	private ModelService modelService;

	OrderModel orderModel;
	OrderPaymentFormType orderPayment;

	@Before
	public void setup()
	{
		orderModel = new OrderModel();
		final UserModel user = new UserModel();
		user.setUid("uid");
		orderModel.setUser(user);

		final AddressModel address = new AddressModel();
		address.setFirstname("First");
		address.setLastname("Last");
		address.setOwner(user);
		orderModel.setPaymentAddress(address);
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("GBP");
		orderModel.setCurrency(currency);

		orderPayment = new OrderPaymentFormType();
		final Method method=new Method();
		final PaymentCardType paymentCard=new PaymentCardType();
		final CardNumber cardNumber=new CardNumber();
		cardNumber.setValue("4111111111111111");
		paymentCard.setCardNumber(cardNumber);
		paymentCard.setCardType("Credit");
		final EffectiveExpireDate effectiveExpireDate = new EffectiveExpireDate();
		effectiveExpireDate.setEffective("1016");
		effectiveExpireDate.setExpiration("1020");
		paymentCard.setEffectiveExpireDate(effectiveExpireDate);
		final SeriesCode seriesCode = new SeriesCode();
		seriesCode.setValue("seriesCodeValue");
		paymentCard.setSeriesCode(seriesCode);
		method.setPaymentCard(paymentCard);
		orderPayment.setMethod(method);

		final NDCCreditCardTypeMappingModel ndcCreditCardTypeMappingModel = new NDCCreditCardTypeMappingModel();
		ndcCreditCardTypeMappingModel.setCreditCardType("Visa");

		Mockito.when(ndcCreditCardTypeMappingService.getNDCCreditCardTypeMapping(Mockito.anyString()))
				.thenReturn(ndcCreditCardTypeMappingModel);
		final CreditCardType creditCardType = CreditCardType.VISA;
		final CardType cardType = new CardType("id", creditCardType, "description");
		Mockito.when(commerceCardTypeService.getCardTypeForCode(ndcCreditCardTypeMappingModel.getCreditCardType()))
				.thenReturn(cardType);
		Mockito.when(commerceCheckoutService.getPaymentProvider()).thenReturn("CCAvenue");
		final NewSubscription newSubscription = new NewSubscription();
		newSubscription.setSubscriptionID("newSubscriptionId");
		Mockito.when(paymentService.createSubscription(Mockito.anyString(), Mockito.anyString(), Mockito.any(Currency.class),
				Mockito.any(AddressModel.class), Mockito.any(CardInfo.class))).thenReturn(newSubscription);
		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.INVALID_CARD_TYPE))
				.thenReturn("ConversionException");
	}

	@Test(expected = ConversionException.class)
	public void testCreatePaymentInfoWithOrderCreateRQWithExecption()
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final Query orderCreateQuery = new Query();
		final Payments payments = new Payments();
		orderCreateRQ.setQuery(orderCreateQuery);
		payments.getPayment().add(orderPayment);
		orderCreateQuery.setPayments(payments);

		Mockito.when(ndcCreditCardTypeMappingService.getNDCCreditCardTypeMapping(Mockito.anyString())).thenReturn(null);
		defaultNDCPaymentInfoFacade.createPaymentInfo(orderCreateRQ, orderModel);
	}

	@Test(expected = ConversionException.class)
	public void testCreatePaymentInfoWithOrderChangeRQWithExecption()
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final Query orderCreateQuery = new Query();
		final Payments payments = new Payments();
		orderCreateRQ.setQuery(orderCreateQuery);
		payments.getPayment().add(orderPayment);
		orderCreateQuery.setPayments(payments);

		Mockito.when(commerceCardTypeService.getCardTypeForCode(Mockito.anyString())).thenReturn(null);
		defaultNDCPaymentInfoFacade.createPaymentInfo(orderCreateRQ, orderModel);
	}

	@Test
	public void testCreatePaymentInfoWithOrderCreateRQ()
	{
		final OrderCreateRQ orderCreateRQ = new OrderCreateRQ();
		final Query orderCreateQuery = new Query();
		final Payments payments = new Payments();
		orderCreateRQ.setQuery(orderCreateQuery);
		payments.getPayment().add(orderPayment);
		orderCreateQuery.setPayments(payments);

		defaultNDCPaymentInfoFacade.createPaymentInfo(orderCreateRQ, orderModel);
		Assert.assertEquals("newSubscriptionId", ((CreditCardPaymentInfoModel) orderModel.getPaymentInfo()).getSubscriptionId());
	}

	@Test
	public void testCreatePaymentInfoWithOrderChangeRQ()
	{
		final OrderChangeRQ orderChangeRQ = new OrderChangeRQ();
		final de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query orderChangeQuery = new de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query();
		final de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments payments = new de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Payments();
		final Payment payment = new Payment();
		payment.setMethod(orderPayment.getMethod());
		payments.getPayment().add(payment);
		orderChangeRQ.setQuery(orderChangeQuery);
		orderChangeQuery.setPayments(payments);

		defaultNDCPaymentInfoFacade.createPaymentInfo(orderChangeRQ, orderModel);
		Assert.assertEquals("newSubscriptionId", ((CreditCardPaymentInfoModel) orderModel.getPaymentInfo()).getSubscriptionId());
	}
}
