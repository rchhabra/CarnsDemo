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

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;


/**
 * Abstract Strategy implementing {@link de.hybris.platform.travelfacades.strategies.OfferResponseFilterStrategy}
 */
public abstract class AbstractOfferResponseFilterStrategy implements OfferResponseFilterStrategy
{

	/**
	 * Sets origin destination status.
	 *
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 * @param offerResponseData
	 * 		the offer response data
	 * @param active
	 * 		the active
	 */
	protected void setOriginDestinationStatus(final int originDestinationRefNumber, final OfferResponseData offerResponseData,
			final boolean active)
	{
		for (final ItineraryData itineraryData : offerResponseData.getItineraries())
		{
			for (final OriginDestinationOptionData originDestinationOption : itineraryData.getOriginDestinationOptions())
			{
				if (originDestinationOption.getOriginDestinationRefNumber() == originDestinationRefNumber)
				{
					originDestinationOption.setActive(active);
				}
			}
		}
	}

}
