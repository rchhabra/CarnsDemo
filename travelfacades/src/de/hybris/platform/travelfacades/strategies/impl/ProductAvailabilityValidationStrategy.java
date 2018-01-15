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
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.AbstractAddToCartValidationStrategy;

import java.util.Arrays;
import java.util.List;


/**
 * Strategy that extends the {@link de.hybris.platform.travelfacades.strategies.AbstractAddToCartValidationStrategy}.
 * The strategy is used to validate the addToCart of a product based on its availability.
 */
public class ProductAvailabilityValidationStrategy extends AbstractAddToCartValidationStrategy
{

	private static final String ADD_TO_CART_VALIDATION_ERROR_PRODUCT_AVAILABILITY = "add.to.cart.validation.error.product.availability";

	private TravelCartFacade cartFacade;

	@Override
	public AddToCartResponseData validateAddToCart(final String productCode, final long qty, final String travellerCode,
			final List<String> transportOfferingCodes, final String travelRouteCode)
	{
		final boolean isAvailable = getCartFacade().isProductAvailable(productCode, transportOfferingCodes, qty);

		return getAddToCartResponse(isAvailable, Arrays.asList(ADD_TO_CART_VALIDATION_ERROR_PRODUCT_AVAILABILITY));
	}

	/**
	 * Gets cart facade.
	 *
	 * @return the cartFacade
	 */
	protected TravelCartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * Sets cart facade.
	 *
	 * @param cartFacade
	 * 		the cartFacade to set
	 */
	public void setCartFacade(final TravelCartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

}
