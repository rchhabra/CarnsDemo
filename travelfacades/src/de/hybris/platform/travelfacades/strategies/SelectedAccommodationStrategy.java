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


/**
 * Strategy to validate the amendment of the selected accommodation
 */
public interface SelectedAccommodationStrategy
{

	/**
	 * Method to validate the amendment of a selected accommodation
	 *
	 * @param accommodationUid
	 * 		the accommodation Uid
	 * @param previousSelectedAccommodation
	 * 		the previous selected accommodation
	 * @param transportOfferingCode
	 * 		the transport offering code
	 * @param travellerCode
	 * 		the traveller code
	 * @param originDestinationRefNo
	 * 		the origin destination ref no
	 * @param travelRoute
	 * 		the travel route
	 * @return the addToCartResponseData, where valid is true if the addAccommodationToCart is valid, false otherwise.
	 */
	AddToCartResponseData validateSelectedAccommodation(String accommodationUid, String previousSelectedAccommodation,
			String transportOfferingCode, String travellerCode, String originDestinationRefNo, String travelRoute);

}
