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
package de.hybris.platform.travelfacades.ancillary.search.manager;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;


/**
 * Pipeline Manager class that will return a {@link OfferRequestData} after executing a list of handlers
 * on the {@link ReservationData} given as input
 */
public interface AncillarySearchRequestPipelineManager
{
	/**
	 * Execute pipeline offer request data.
	 *
	 * @param reservationData
	 * 		the reservation data
	 * @return the offer request data
	 */
	OfferRequestData executePipeline(ReservationData reservationData);
}
