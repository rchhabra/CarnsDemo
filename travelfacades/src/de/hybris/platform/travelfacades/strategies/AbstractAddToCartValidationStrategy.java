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

import java.util.List;


/**
 * Abstract Strategy implementing {@link de.hybris.platform.travelfacades.strategies.AddToCartValidationStrategy}
 */
public abstract class AbstractAddToCartValidationStrategy implements AddToCartValidationStrategy
{

	/**
	 * Creates a AddToCartResponseData
	 *
	 * @param valid
	 * 		as a boolean flag
	 * @param errorMessages
	 * 		as a list of error messages
	 * @return the AddToCartResponseData
	 */
	protected AddToCartResponseData getAddToCartResponse(final boolean valid, final List<String> errorMessages)
	{
		final AddToCartResponseData response = new AddToCartResponseData();

		response.setValid(valid);
		if (!valid)
		{
			response.setErrors(errorMessages);
		}

		return response;
	}

}
