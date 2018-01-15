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

package de.hybris.platform.travelfacades.strategies;

import de.hybris.platform.commercefacades.travel.AddToCartResponseData;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;


/**
 * Abstract Strategy implementing {@link AddBundleToCartValidationStrategy}
 */
public abstract class AbstractAddBundleToCartValidationStrategy implements AddBundleToCartValidationStrategy
{
	protected static final String ADD_BUNDLE_TO_CART_VALIDATION_ERROR_NO_SESSION_CART = "add.bundle.to.cart.validation.error.no.session.cart";

	/**
	 * Creates a AddToCartResponseData
	 *
	 * @param valid
	 * 		as a boolean flag
	 * @param errorMessage
	 * 		as the error message
	 *	@param minOriginDestinationRefNumber
	 *			as teh min origin destination ref number
	 *
	 * @return the AddToCartResponseData
	 */
	protected AddToCartResponseData createAddToCartResponse(final boolean valid, final String errorMessage,
			final Integer minOriginDestinationRefNumber)
	{
		final AddToCartResponseData response = new AddToCartResponseData();
		response.setValid(valid);
		if (StringUtils.isNotEmpty(errorMessage))
		{
			response.setErrors(Collections.singletonList(errorMessage));
		}
		if(minOriginDestinationRefNumber != null)
		{
			response.setMinOriginDestinationRefNumber(minOriginDestinationRefNumber);
		}
		return response;
	}

}
