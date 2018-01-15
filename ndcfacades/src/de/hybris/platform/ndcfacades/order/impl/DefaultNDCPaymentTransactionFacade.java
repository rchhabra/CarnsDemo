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
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.order.NDCPaymentTransactionFacade;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link NDCPaymentTransactionFacade}
 */
public class DefaultNDCPaymentTransactionFacade implements NDCPaymentTransactionFacade
{
	private static final String CC_OWNER = "John Smith";
	private static final String CC_NUMBER = "1111111111111111";
	private static final String CC_TYPE = "visa";
	private static final String CC_CV2 = "123";
	private static final String VALIDITY_MONTH = "01";
	private static final String VALIDITY_YEAR = "2030";

	private CommerceCheckoutService commerceCheckoutService;
	private CommerceCardTypeService commerceCardTypeService;
	private ModelService modelService;

	@Override
	public void createPaymentTransaction(final OrderCreateRQ orderCreateRQ, final OrderModel orderModel)
	{
		final PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
		final List<PaymentTransactionModel> paymentTransactions = new LinkedList<>(orderModel.getPaymentTransactions());

		createPaymentTransactionModel(paymentTransactionModel, isPayLater(orderCreateRQ), orderModel, orderModel.getEntries(),
				BigDecimal.valueOf(orderModel.getTotalPrice() + orderModel.getTotalTax()));

		paymentTransactions.add(paymentTransactionModel);
		orderModel.setPaymentTransactions(paymentTransactions);
	}

	@Override
	public void createPaymentTransaction(final BigDecimal totalToPay, final OrderModel orderModel,
			final List<AbstractOrderEntryModel> orderEntries)
	{
		final PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
		final List<PaymentTransactionModel> paymentTransactions = new LinkedList<>(orderModel.getPaymentTransactions());

		createPaymentTransactionModel(paymentTransactionModel, false, orderModel, orderEntries, totalToPay);

		paymentTransactions.add(paymentTransactionModel);
		orderModel.setPaymentTransactions(paymentTransactions);
	}

	@Override
	public void createPayLaterTransaction(final OrderCreateRQ orderCreateRQ, final OrderModel orderModel)
	{
		if (!Objects.isNull(orderCreateRQ.getQuery().getPayments()) && !orderCreateRQ.getQuery().getPayments().getPayment()
				.isEmpty())
		{
			return;
		}

		final AddressModel paymentAddress = new AddressModel();
		final CreditCardPaymentInfoModel creditCardPaymentInfo = new CreditCardPaymentInfoModel();

		orderModel.setPaymentAddress(paymentAddress);
		populateCreditCardPaymentInfoModel(creditCardPaymentInfo, orderModel);

		paymentAddress.setOwner(creditCardPaymentInfo);
		orderModel.setPaymentInfo(creditCardPaymentInfo);
		orderModel.setDeliveryAddress(paymentAddress);
	}

	/**
	 * Creates the {@link PaymentTransactionModel} based on the information contained in the {@link OrderModel}
	 *
	 * @param paymentTransactionModel
	 * 		the payment transaction model
	 * @param isPayLater
	 * 		the is pay later
	 * @param order
	 * 		the order
	 * @param orderEntries
	 * 		the order entries
	 * @param totalToPay
	 * 		the total to pay
	 */
	protected void createPaymentTransactionModel(final PaymentTransactionModel paymentTransactionModel,
			final boolean isPayLater, final OrderModel order, final List<AbstractOrderEntryModel> orderEntries,
			final BigDecimal totalToPay)
	{
		paymentTransactionModel.setAbstractOrderEntries(orderEntries);
		paymentTransactionModel.setPaymentProvider(getCommerceCheckoutService().getPaymentProvider());
		paymentTransactionModel.setCurrency(order.getCurrency());
		paymentTransactionModel.setPlannedAmount(totalToPay);
		paymentTransactionModel.setOrder(order);
		paymentTransactionModel.setCode(UUID.randomUUID().toString().replaceAll("-", ""));

		createPaymentTransactionEntryModel(paymentTransactionModel, isPayLater, order, totalToPay);

		getModelService().save(paymentTransactionModel);
	}

	/**
	 * Creates the list of {@link PaymentTransactionEntryModel} based on the information contained in the {@link OrderModel}
	 *
	 * @param paymentTransactionModel
	 * 		the payment transaction model
	 * @param isPayLater
	 * 		the is pay later
	 * @param order
	 * 		the order
	 * @param totalToPay
	 * 		the total to pay
	 */
	protected void createPaymentTransactionEntryModel(final PaymentTransactionModel paymentTransactionModel,
			final boolean isPayLater, final OrderModel order, final BigDecimal totalToPay)
	{
		final List<PaymentTransactionEntryModel> entries = new LinkedList<>();
		final PaymentTransactionEntryModel paymentTransactionEntry = new PaymentTransactionEntryModel();

		setPaymentTransactionEntryType(paymentTransactionEntry, isPayLater);

		paymentTransactionEntry.setAmount(totalToPay);
		paymentTransactionEntry.setCurrency(order.getCurrency());
		paymentTransactionEntry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		paymentTransactionEntry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		paymentTransactionEntry.setCode(UUID.randomUUID().toString().replaceAll("-", ""));

		getModelService().save(paymentTransactionEntry);

		entries.add(paymentTransactionEntry);
		paymentTransactionModel.setEntries(entries);
	}

	/**
	 * Sets the {@link PaymentTransactionType} in the {@link PaymentTransactionEntryModel} based on the presence of the Payments
	 * element in the {@link OrderCreateRQ}
	 *
	 * @param paymentTransactionEntry
	 * 		the payment transaction entry
	 * @param isPayLater
	 * 		the is pay later
	 */
	protected void setPaymentTransactionEntryType(final PaymentTransactionEntryModel paymentTransactionEntry,
			final boolean isPayLater)
	{
		if (isPayLater)
		{
			paymentTransactionEntry.setType(PaymentTransactionType.PAY_LATER);
		}
		else
		{
			paymentTransactionEntry.setType(PaymentTransactionType.AUTHORIZATION);
		}
	}

	/**
	 * Checks if the {@link OrderCreateRQ} contains payment information
	 *
	 * @param orderCreateRQ
	 * 		the order create rq
	 *
	 * @return boolean
	 */
	public static boolean isPayLater(final OrderCreateRQ orderCreateRQ)
	{
		return Objects.isNull(orderCreateRQ.getQuery().getPayments()) || orderCreateRQ.getQuery().getPayments().getPayment()
				.isEmpty();
	}

	/**
	 * Checks if the {@link OrderChangeRQ} contains payment information
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 *
	 * @return boolean
	 */
	public static boolean isPayLater(final OrderChangeRQ orderChangeRQ)
	{
		return Objects.isNull(orderChangeRQ.getQuery().getPayments()) || orderChangeRQ.getQuery().getPayments().getPayment()
				.isEmpty();
	}

	/**
	 * Populates the {@link CreditCardPaymentInfoModel} based on the information contained in the {@link OrderModel}
	 *
	 * @param creditCardPaymentInfo
	 * 		the credit card payment info
	 * @param orderModel
	 * 		the order model
	 */
	protected void populateCreditCardPaymentInfoModel(final CreditCardPaymentInfoModel creditCardPaymentInfo,
			final OrderModel orderModel)
	{
		final CardType cardType = getCommerceCardTypeService().getCardTypeForCode(CC_TYPE);

		creditCardPaymentInfo.setUser(orderModel.getUser());
		creditCardPaymentInfo.setCcOwner(CC_OWNER);
		creditCardPaymentInfo.setCode(orderModel.getUser().getUid() + "_" + UUID.randomUUID());
		creditCardPaymentInfo.setNumber(CC_NUMBER);
		creditCardPaymentInfo.setValidToMonth(VALIDITY_MONTH);
		creditCardPaymentInfo.setValidToYear(VALIDITY_YEAR);
		creditCardPaymentInfo.setSubscriptionId(UUID.randomUUID().toString());
		creditCardPaymentInfo.setType(cardType.getCode());
		creditCardPaymentInfo.setBillingAddress(orderModel.getPaymentAddress());
		orderModel.getPaymentAddress().setOwner(creditCardPaymentInfo);

		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardHolderFullName(creditCardPaymentInfo.getCcOwner());
		cardInfo.setCardNumber(creditCardPaymentInfo.getNumber());
		cardInfo.setCardType(cardType.getCode());
		cardInfo.setExpirationMonth(Integer.valueOf(creditCardPaymentInfo.getValidToMonth()));
		cardInfo.setExpirationYear(Integer.valueOf(creditCardPaymentInfo.getValidToYear()));
		cardInfo.setCv2Number(CC_CV2);

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
