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
import de.hybris.platform.travelfacades.facades.CheckInFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.AbstractAddToCartValidationStrategy;

import java.util.Arrays;
import java.util.List;


/**
 * Strategy that extends the {@link de.hybris.platform.travelfacades.strategies.AbstractAddToCartValidationStrategy}.
 * The strategy is used to validate the addToCart of a product. The addToCart is not valid if during an amendment, the
 * product to be changed is related to a traveller that has been already checked in.
 */
public class TravellerStatusValidationStrategy extends AbstractAddToCartValidationStrategy
{

	private static final String ADD_TO_CART_VALIDATION_ERROR_TRAVELLER_STATUS = "add.to.cart.validation.error.traveller.status";
	private TravelCartFacade cartFacade;
	private CheckInFacade checkInFacade;

	@Override
	public AddToCartResponseData validateAddToCart(final String productCode, final long qty, final String travellerCode,
			final List<String> transportOfferingCodes, final String travelRouteCode)
	{
		if (getCartFacade().isAmendmentCart())
		{
			final boolean isAmendPossible = getCheckInFacade().checkTravellerEligibility(travellerCode, transportOfferingCodes,
					getCartFacade().getOriginalOrderCode());
			return getAddToCartResponse(isAmendPossible, Arrays.asList(ADD_TO_CART_VALIDATION_ERROR_TRAVELLER_STATUS));
		}
		return getAddToCartResponse(true, null);
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

	/**
	 * Gets check in facade.
	 *
	 * @return the checkInFacade
	 */
	protected CheckInFacade getCheckInFacade()
	{
		return checkInFacade;
	}

	/**
	 * Sets check in facade.
	 *
	 * @param checkInFacade
	 * 		the checkInFacade to set
	 */
	public void setCheckInFacade(final CheckInFacade checkInFacade)
	{
		this.checkInFacade = checkInFacade;
	}

}
