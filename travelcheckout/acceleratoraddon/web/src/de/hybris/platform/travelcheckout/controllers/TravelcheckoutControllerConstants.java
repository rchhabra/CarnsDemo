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
package de.hybris.platform.travelcheckout.controllers;

/**
 */
public interface TravelcheckoutControllerConstants
{

	final String ADDON_PREFIX = "addon:/travelcheckout/";
	final String STOREFRONT_PREFIX = "/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{

		interface Pages
		{

			interface Checkout
			{

				String PaymentDetailsPage = ADDON_PREFIX + "pages/checkout/paymentDetailsPage";
				String CheckoutLoginPage = STOREFRONT_PREFIX + "pages/checkout/checkoutLoginPage";
				String ReadOnlyExpandedOrderForm = STOREFRONT_PREFIX + "fragments/checkout/readOnlyExpandedOrderForm";
			}

			interface MultiStepCheckout
			{
				String AddPaymentMethodPage = ADDON_PREFIX + "pages/checkout/multi/addPaymentMethodPage";
				String HostedOrderPageErrorPage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderPageErrorPage";
				String HostedOrderPostPage =  "pages/checkout/multi/hostedOrderPostPage";
				String SilentOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/silentOrderPostPage";
				String CheckoutSummaryPage = ADDON_PREFIX + "pages/checkout/multi/checkoutSummaryPage";
				String ChoosePaymentTypePage = ADDON_PREFIX + "pages/checkout/multi/choosePaymentTypePage";
			}
		}

		interface Fragments
		{

			interface Checkout
			{
				String BillingAddressForm = "fragments/checkout/billingAddressForm";
			}
		}
	}
}
