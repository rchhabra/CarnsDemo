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

import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;


/**
 * Strategy to validate a quantity against the TravelRestriction.
 */
public class TravelRestrictionStrategy
{

	/**
	 * This method performs a validation on the quantity against the TravelRestriction. The quantity can be 0 or it has
	 * to fulfil the TravelRestriction.
	 *
	 * @param travelRestrictionModel
	 * 		as the travelRestriction that should be fulfilled by the quantity
	 * @param quantity
	 * 		as the quantity to validate
	 * @return true if the quantity to be added/removed is 0 or fulfils the TravelRestriction, false otherwise
	 */
	public boolean checkQuantityForTravelRestriction(final TravelRestrictionModel travelRestrictionModel, final long quantity)
	{
		return quantity == 0 || checkRestrictions(travelRestrictionModel, quantity);
	}

	/**
	 * This method performs a validation on the quantity against the TravelRestriction. The quantity has to fulfil the
	 * TravelRestriction.
	 *
	 * @param travelRestrictionModel
	 * 		as the travelRestriction that should be fulfilled by the quantity
	 * @param quantity
	 * 		as the quantity to validate
	 * @return true if the quantity to be added/removed or fulfils the TravelRestriction, false otherwise
	 */
	public boolean checkQuantityForMandatoryTravelRestriction(final TravelRestrictionModel travelRestrictionModel,
			final long quantity)
	{
		return checkRestrictions(travelRestrictionModel, quantity);
	}

	/**
	 * Check restrictions validity.
	 *
	 * @param travelRestrictionModel
	 * 		the travel restriction model
	 * @param quantity
	 * 		the quantity
	 * @return the boolean
	 */
	protected boolean checkRestrictions(final TravelRestrictionModel travelRestrictionModel, final long quantity)
	{
		final Integer travellerMinOfferQty = travelRestrictionModel != null
				&& travelRestrictionModel.getTravellerMinOfferQty() != null ? travelRestrictionModel.getTravellerMinOfferQty()
						: TravelfacadesConstants.DEFAULT_MIN_QUANTITY_RESTRICTION;
		final Integer travellerMaxOfferQty = travelRestrictionModel != null
				&& travelRestrictionModel.getTravellerMaxOfferQty() != null ? travelRestrictionModel.getTravellerMaxOfferQty()
						: TravelfacadesConstants.DEFAULT_MAX_QUANTITY_RESTRICTION;

		boolean isValid = travellerMinOfferQty <= quantity;
		if (travellerMaxOfferQty >= 0)
		{
			isValid &= quantity <= travellerMaxOfferQty;
		}

		return isValid;
	}

}
