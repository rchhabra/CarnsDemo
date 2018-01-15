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

import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.order.TravelCommerceCheckoutService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Facade to handle travel specific methods relative to checkout
 */
public class DefaultTravelCheckoutFacade extends DefaultAcceleratorCheckoutFacade
{
	private SessionService sessionService;
	private ConfigurationService configurationService;
	private CommerceCheckoutService commerceCheckoutService;

	private static final Logger LOG = Logger.getLogger(DefaultTravelCheckoutFacade.class);
	private static final String PAYMENT_TRANSACTIONS = "paymentTransactions";

	@Override
	public boolean authorizePayment(final String securityCode)
	{
		final CartModel cartModel = getCart();
		if (!checkIfCurrentUserIsTheCartUser())
		{
			return true;
		}
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) cartModel.getPaymentInfo();
		if (creditCardPaymentInfoModel != null && StringUtils.isNotBlank(creditCardPaymentInfoModel.getSubscriptionId()))
		{
			final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setSecurityCode(securityCode);
			parameter.setPaymentProvider(getPaymentProvider());
			final List<PaymentTransactionData> transactions = getSessionService().getAttribute(PAYMENT_TRANSACTIONS);
			if (CollectionUtils.isEmpty(transactions))
			{
				final PaymentTransactionEntryModel paymentTransactionEntryModel = getCommerceCheckoutService()
						.authorizePayment(parameter);

				return paymentTransactionEntryModel != null
						&& (TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus())
								|| TransactionStatus.REVIEW.name().equals(paymentTransactionEntryModel.getTransactionStatus()));

			}
			for (final PaymentTransactionData transactionData : transactions)
			{
				if (transactionData.getTransactionAmount() == 0
						&& Objects.nonNull(getExistingTransaction(transactionData, parameter)))
				{
					continue;
				}

				if (transactionData.getTransactionAmount() < 0)
				{
					LOG.error("Found transaction with negative amount. Aborting checkout.");
					return false;
				}

				parameter.setAuthorizationAmount(
						BigDecimal.valueOf(transactionData.getTransactionAmount() > 0 ? transactionData.getTransactionAmount()
								: getConfigurationService().getConfiguration()
										.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)));
				final PaymentTransactionEntryModel paymentTransactionEntryModel = getCommerceCheckoutService()
						.authorizePayment(parameter);
				if (Objects.isNull(paymentTransactionEntryModel))
				{
					return false;
				}
				setEntriesAgainstTransaction(paymentTransactionEntryModel, transactionData);
				if (!(TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus())
						|| TransactionStatus.REVIEW.name().equals(paymentTransactionEntryModel.getTransactionStatus())))
				{
					return false;
				}
			}
		}
		return true;
	}

	protected PaymentTransactionModel getExistingTransaction(final PaymentTransactionData transactionData,
			final CommerceCheckoutParameter parameter)
	{
		return ((TravelCommerceCheckoutService) getCommerceCheckoutService())
				.getExistingTransaction(transactionData.getEntryNumbers(), parameter);
	}

	protected void setEntriesAgainstTransaction(final PaymentTransactionEntryModel paymentTransactionEntryModel,
			final PaymentTransactionData transactionData)
	{
		((TravelCommerceCheckoutService) getCommerceCheckoutService()).setEntriesAgainstTransaction(
				paymentTransactionEntryModel.getPaymentTransaction(), transactionData.getEntryNumbers());
	}

	/**
	 *
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 *
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the commerceCheckoutService
	 */
	@Override
	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	/**
	 * @param commerceCheckoutService
	 *           the commerceCheckoutService to set
	 */
	@Override
	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}
}
