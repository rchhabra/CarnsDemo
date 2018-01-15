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
package de.hybris.platform.travelacceleratorstorefront.constants;

import de.hybris.platform.travelacceleratorstorefront.model.components.BookingTotalComponentModel;
import de.hybris.platform.travelacceleratorstorefront.model.components.ManageMyBookingComponentModel;
import de.hybris.platform.travelacceleratorstorefront.model.components.PaymentOptionsComponentModel;
import de.hybris.platform.travelacceleratorstorefront.model.components.ReservationOverlayTotalsComponentModel;
import de.hybris.platform.travelacceleratorstorefront.model.components.ReservationTotalsComponentModel;


/**
 */
public interface TravelacceleratorstorefrontControllerConstants
{

	String ADDON_PREFIX = "addon:/travelacceleratorstorefront/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{

		interface Pages
		{

			interface Account
			{
				String AccountPage = ADDON_PREFIX + "pages/account/accountLayoutPage";

				String ProfilePage = ADDON_PREFIX + "pages/account/myProfile";
			}

			interface Checkout
			{
				String CheckoutLoginPage = ADDON_PREFIX + "pages/checkout/checkoutLoginPage";
				String SelectPaymentResponse = ADDON_PREFIX + "pages/checkout/selectPaymentResponse";
			}

			interface Order
			{
				String OrderConfirmationPage = ADDON_PREFIX + "pages/order/bookingConfirmationPage";
			}

			interface BookingDetails
			{
				String cancelOrderResponse = ADDON_PREFIX + "pages/managebooking/cancelOrderResponse";
				String additionalSecurityResponse = ADDON_PREFIX + "pages/managebooking/loginResponse";
			}
		}
	}

	interface Actions
	{

		interface Cms
		{

			String _Prefix = "/view/";

			String _Suffix = "Controller";

			/**
			 * CMS components that have specific handlers
			 */
			String ManageMyBookingComponent = _Prefix + ManageMyBookingComponentModel._TYPECODE + _Suffix;
			String ReservationTotalsComponent = _Prefix + ReservationTotalsComponentModel._TYPECODE + _Suffix;
			String ReservationOverlayTotalsComponent = _Prefix + ReservationOverlayTotalsComponentModel._TYPECODE + _Suffix;
			String PaymentOptionsComponent = _Prefix + PaymentOptionsComponentModel._TYPECODE + _Suffix;
			String BookingTotalComponent = _Prefix + BookingTotalComponentModel._TYPECODE + _Suffix;
		}
	}
}
