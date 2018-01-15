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
package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.platform.commerceservices.order.CommerceCardTypeService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.ndcfacades.order.NDCPaymentInfoFacade;
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
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link NDCPaymentInfoFacade}
 */
public class DefaultNDCPaymentInfoFacade implements NDCPaymentInfoFacade
{
	private CommerceCheckoutService commerceCheckoutService;
	private CommerceCardTypeService commerceCardTypeService;
	private PaymentService paymentService;
	private ConfigurationService configurationService;
	private NDCCreditCardTypeMappingService ndcCreditCardTypeMappingService;
	private ModelService modelService;

	@Override
	public void createPaymentInfo(final OrderCreateRQ orderCreateRQ, final OrderModel orderModel)
	{
		if (Objects.isNull(orderCreateRQ.getQuery().getPayments()) || orderCreateRQ.getQuery().getPayments().getPayment().isEmpty())
		{
			return;
		}

		createOrderPaymentFormType(orderCreateRQ.getQuery().getPayments().getPayment().get(0), orderModel);
	}

	@Override
	public void createPaymentInfo(final OrderChangeRQ orderChangeRQ, final OrderModel orderModel)
	{
		if (Objects.isNull(orderChangeRQ.getQuery().getPayments()) || orderChangeRQ.getQuery().getPayments().getPayment().isEmpty())
		{
			return;
		}

		createOrderPaymentFormType(orderChangeRQ.getQuery().getPayments().getPayment().get(0), orderModel);
	}

	/**
	 * Creates the {@link OrderPaymentFormType} based on the information contained in the {@link OrderModel}
	 *
	 * @param orderPayment
	 * 		the order payment
	 * @param orderModel
	 * 		the order model
	 */
	protected void createOrderPaymentFormType(final OrderPaymentFormType orderPayment, final OrderModel orderModel)
	{
		final CreditCardPaymentInfoModel creditCardPaymentInfo = new CreditCardPaymentInfoModel();

		createCreditCardPaymentInfoModel(creditCardPaymentInfo, orderModel, orderPayment);

		getModelService().save(creditCardPaymentInfo);

		orderModel.setPaymentInfo(creditCardPaymentInfo);
	}

	/**
	 * Creates the {@link CreditCardPaymentInfoModel} based on the information contained in the {@link OrderPaymentFormType} and
	 * {@link OrderModel}
	 *
	 * @param creditCardPaymentInfo
	 * 		the credit card payment info
	 * @param orderModel
	 * 		the order model
	 * @param orderPayment
	 * 		the order payment
	 */
	protected void createCreditCardPaymentInfoModel(final CreditCardPaymentInfoModel creditCardPaymentInfo,
			final OrderModel orderModel, final OrderPaymentFormType orderPayment)
	{
		creditCardPaymentInfo.setUser(orderModel.getUser());
		creditCardPaymentInfo
				.setCcOwner(orderModel.getPaymentAddress().getFirstname() + " " + orderModel.getPaymentAddress().getLastname());
		creditCardPaymentInfo.setCode(orderModel.getUser().getUid() + "_" + UUID.randomUUID());
		creditCardPaymentInfo.setNumber(getMaskedCardNumber(orderPayment.getMethod().getPaymentCard().getCardNumber().getValue()));

		final NDCCreditCardTypeMappingModel ndcCreditCardTypeMapping = getNdcCreditCardTypeMappingService()
				.getNDCCreditCardTypeMapping(orderPayment.getMethod().getPaymentCard().getCardType());

		if (Objects.isNull(ndcCreditCardTypeMapping))
		{
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_CARD_TYPE));
		}

		final CardType cardType = getCommerceCardTypeService().getCardTypeForCode(ndcCreditCardTypeMapping.getCreditCardType());

		if (Objects.isNull(cardType))
		{
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_CARD_TYPE));
		}
		creditCardPaymentInfo.setType(cardType.getCode());

		if (!Objects.isNull(orderPayment.getMethod().getPaymentCard().getEffectiveExpireDate().getEffective()))
		{
			creditCardPaymentInfo.setValidFromMonth(
					orderPayment.getMethod().getPaymentCard().getEffectiveExpireDate().getEffective().substring(0, 2));
			creditCardPaymentInfo.setValidFromYear(
					"20" + orderPayment.getMethod().getPaymentCard().getEffectiveExpireDate().getEffective().substring(2, 4));
		}

		creditCardPaymentInfo
				.setValidToMonth(orderPayment.getMethod().getPaymentCard().getEffectiveExpireDate().getExpiration().substring(0, 2));
		creditCardPaymentInfo.setValidToYear(
				"20" + orderPayment.getMethod().getPaymentCard().getEffectiveExpireDate().getExpiration().substring(2, 4));
		creditCardPaymentInfo.setBillingAddress(orderModel.getPaymentAddress());
		orderModel.getPaymentAddress().setOwner(creditCardPaymentInfo);

		final String merchantTransactionCode = orderModel.getUser().getUid() + "-" + UUID.randomUUID();

		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardHolderFullName(creditCardPaymentInfo.getCcOwner());
		cardInfo.setCardNumber(creditCardPaymentInfo.getNumber());
		cardInfo.setCardType(cardType.getCode());
		cardInfo.setExpirationMonth(Integer.valueOf(creditCardPaymentInfo.getValidToMonth()));
		cardInfo.setExpirationYear(Integer.valueOf(creditCardPaymentInfo.getValidToYear()));
		cardInfo.setCv2Number(orderPayment.getMethod().getPaymentCard().getSeriesCode().getValue());

		final NewSubscription subscription = getPaymentService()
				.createSubscription(merchantTransactionCode, getCommerceCheckoutService().getPaymentProvider(),
						Currency.getInstance(orderModel.getCurrency().getIsocode()), orderModel.getPaymentAddress(), cardInfo);

		creditCardPaymentInfo.setSubscriptionId(subscription.getSubscriptionID());
	}

	/**
	 * Gets masked card number.
	 *
	 * @param cardNumber
	 * 		the card number
	 *
	 * @return masked card number
	 */
	protected String getMaskedCardNumber(final String cardNumber)
	{
		if (cardNumber != null && cardNumber.trim().length() > 4)
		{
			final String endPortion = cardNumber.trim().substring(cardNumber.length() - 4);
			return "************" + endPortion;
		}
		return null;
	}

	/**
	 * Gets commerce checkout service.
	 *
	 * @return the commerce checkout service
	 */
	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	/**
	 * Sets commerce checkout service.
	 *
	 * @param commerceCheckoutService
	 * 		the commerce checkout service
	 */
	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	/**
	 * Gets commerce card type service.
	 *
	 * @return the commerce card type service
	 */
	protected CommerceCardTypeService getCommerceCardTypeService()
	{
		return commerceCardTypeService;
	}

	/**
	 * Sets commerce card type service.
	 *
	 * @param commerceCardTypeService
	 * 		the commerce card type service
	 */
	@Required
	public void setCommerceCardTypeService(final CommerceCardTypeService commerceCardTypeService)
	{
		this.commerceCardTypeService = commerceCardTypeService;
	}

	/**
	 * Gets payment service.
	 *
	 * @return the payment service
	 */
	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	/**
	 * Sets payment service.
	 *
	 * @param paymentService
	 * 		the payment service
	 */
	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets ndc credit card type mapping service.
	 *
	 * @return the ndc credit card type mapping service
	 */
	protected NDCCreditCardTypeMappingService getNdcCreditCardTypeMappingService()
	{
		return ndcCreditCardTypeMappingService;
	}

	/**
	 * Sets ndc credit card type mapping service.
	 *
	 * @param ndcCreditCardTypeMappingService
	 * 		the ndc credit card type mapping service
	 */
	@Required
	public void setNdcCreditCardTypeMappingService(
			final NDCCreditCardTypeMappingService ndcCreditCardTypeMappingService)
	{
		this.ndcCreditCardTypeMappingService = ndcCreditCardTypeMappingService;
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
