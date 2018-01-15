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

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;


/**
 * Strategy to filter out originDestinationOfferInfos
 */
public interface OfferResponseFilterStrategy
{

	/**
	 * Method to filter out the originDestinationOfferInfos.
	 *
	 * @param offerResponseData
	 * 		as the offerResponseData with the originDestinationOfferInfos to be filtered.
	 */
	void filterOfferResponseData(OfferResponseData offerResponseData);

	/**
	 * Method to filter out the seatMapData.
	 *
	 * @param seatMapResponseData
	 * 		as the seatMapResponseData with the seatMapData to be filtered.
	 */
	void filterSeatMapData(SeatMapResponseData seatMapResponseData);

}
