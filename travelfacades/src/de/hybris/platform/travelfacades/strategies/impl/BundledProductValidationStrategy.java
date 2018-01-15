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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.strategies.AbstractAddToCartValidationStrategy;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * Strategy that extends the {@link de.hybris.platform.travelfacades.strategies.AbstractAddToCartValidationStrategy}.
 * The strategy is used to validate the addToCart of a product. The addToCart is not valid if the product is included in
 * a bundle.
 */
public class BundledProductValidationStrategy extends AbstractAddToCartValidationStrategy
{

	private static final String ADD_TO_CART_VALIDATION_ERROR_BUNDLED_PRODUCT = "add.to.cart.validation.error.bundled.product";
	private BookingService bookingService;
	private CartService cartService;

	@Override
	public AddToCartResponseData validateAddToCart(final String productCode, final long qty, final String travellerCode,
			final List<String> transportOfferingCodes, final String travelRouteCode)
	{
		final List<String> travellerCodes = new ArrayList<>();
		if (StringUtils.isNotBlank(travellerCode))
		{
			travellerCodes.add(travellerCode);
		}

		final boolean isValid = getBookingService().checkBundleToAmendProduct(getCartService().getSessionCart(), productCode, qty,
				travelRouteCode, transportOfferingCodes, travellerCodes);

		return getAddToCartResponse(isValid, Arrays.asList(ADD_TO_CART_VALIDATION_ERROR_BUNDLED_PRODUCT));
	}

	/**
	 * Gets booking service.
	 *
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * Sets booking service.
	 *
	 * @param bookingService
	 * 		the bookingService to set
	 */
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * Gets cart service.
	 *
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * Sets cart service.
	 *
	 * @param cartService
	 * 		the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

}
