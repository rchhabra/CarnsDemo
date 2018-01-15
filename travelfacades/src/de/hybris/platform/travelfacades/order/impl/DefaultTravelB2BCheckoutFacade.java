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

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.travelfacades.order.TravelB2BCheckoutFacade;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Default travel B2B checkout facade.
 */
public class DefaultTravelB2BCheckoutFacade extends DefaultB2BCheckoutFacade implements TravelB2BCheckoutFacade
{
	private static final String CART_CHECKOUT_PAYMENTINFO_EMPTY = "cart.paymentInfo.empty";
	private static final String CART_CHECKOUT_NOT_CALCULATED = "cart.not.calculated";
	private static final String CART_CHECKOUT_QUOTE_REQUIREMENTS_NOT_SATISFIED = "cart.quote.requirements.not.satisfied";

	private CheckoutFacade travelCheckoutFacade;

	@Override
	protected boolean isValidCheckoutCart(final PlaceOrderData placeOrderData)
	{
		final CartData cartData = getCheckoutCart();
		final boolean valid = true;

		if (!cartData.isCalculated())
		{
			throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_NOT_CALCULATED));
		}

		final boolean accountPaymentType = CheckoutPaymentType.ACCOUNT.getCode().equals(cartData.getPaymentType().getCode());
		if (!accountPaymentType && cartData.getPaymentInfo() == null)
		{
			throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_PAYMENTINFO_EMPTY));
		}

		if (Boolean.TRUE.equals(placeOrderData.getNegotiateQuote()) && !cartData.getQuoteAllowed())
		{
			throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_QUOTE_REQUIREMENTS_NOT_SATISFIED));
		}

		return valid;
	}

	@Override
	public boolean authorizePayment(final String securityCode)
	{
		final CartModel cart = getCart();
		if (cart == null)
		{
			return false;
		}
		if (cart.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
		{
			return getTravelCheckoutFacade().authorizePayment(securityCode);
		}
		else if (cart.getPaymentInfo() instanceof InvoicePaymentInfoModel)
		{
			final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cart);
			final PaymentTransactionEntryModel paymentTranEntry = getCommerceCheckoutService().authorizePayment(parameter);
			return paymentTranEntry != null && (TransactionStatus.ACCEPTED.name().equals(paymentTranEntry.getTransactionStatus()));
		}
		return true;
	}

	/**
	 * Gets travel checkout facade.
	 *
	 * @return the travel checkout facade
	 */
	protected CheckoutFacade getTravelCheckoutFacade()
	{
		return travelCheckoutFacade;
	}

	/**
	 * Sets travel checkout facade.
	 *
	 * @param travelCheckoutFacade
	 * 		the travel checkout facade
	 */
	@Required
	public void setTravelCheckoutFacade(final CheckoutFacade travelCheckoutFacade)
	{
		this.travelCheckoutFacade = travelCheckoutFacade;
	}



}
