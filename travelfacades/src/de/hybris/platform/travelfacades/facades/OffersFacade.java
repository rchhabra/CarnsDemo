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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;


/**
 * Facade exposing methods related to Offers/Ancillaries
 */
public interface OffersFacade
{

	/**
	 * Returns offer request data create from session cart entries
	 *
	 * @return OfferRequestData offers request
	 */
	OfferRequestData getOffersRequest();

	/**
	 * Returns an OfferResponseData based on the given OfferRequestData
	 *
	 * @param offerRequestData
	 *           the Offer Request Data
	 * @return OfferResponseData offers
	 */
	OfferResponseData getOffers(OfferRequestData offerRequestData);

	/**
	 * Returns an OfferResponseData(containing accommodation details) based on the given OfferRequestData
	 *
	 * @param offerRequestData
	 *           the Offer Request Data
	 * @return OfferResponseData accommodations
	 */
	OfferResponseData getAccommodations(OfferRequestData offerRequestData);
}
