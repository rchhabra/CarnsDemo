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
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelfacades.strategies.AbstractAddToCartValidationStrategy;

import java.util.Arrays;
import java.util.List;


/**
 * Strategy that extends the {@link de.hybris.platform.travelfacades.strategies.AbstractAddToCartValidationStrategy}.
 * The strategy is used to validate the addToCart of a product. The addToCart is not valid if the final quantity of the
 * product doesn't fulfil the travel restriction for that product.
 */
public class TravelRestrictionValidationStrategy extends AbstractAddToCartValidationStrategy
{

	private static final String ADD_TO_CART_VALIDATION_ERROR_TRAVEL_RESTRICTION = "add.to.cart.validation.error.travel.restriction";

	private TravelRestrictionFacade travelRestrictionFacade;

	@Override
	public AddToCartResponseData validateAddToCart(final String productCode, final long qty, final String travellerCode,
			final List<String> transportOfferingCodes, final String travelRouteCode)
	{
		final boolean productCanBeAdded = getTravelRestrictionFacade().checkIfProductCanBeAdded(productCode, qty, travelRouteCode,
				transportOfferingCodes, travellerCode);

		return getAddToCartResponse(productCanBeAdded, Arrays.asList(ADD_TO_CART_VALIDATION_ERROR_TRAVEL_RESTRICTION));
	}

	/**
	 * Gets travel restriction facade.
	 *
	 * @return the travelRestrictionFacade
	 */
	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	/**
	 * Sets travel restriction facade.
	 *
	 * @param travelRestrictionFacade
	 * 		the travelRestrictionFacade to set
	 */
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
	}

}
