/*
* [y] hybris Platform
*
* Copyright (c) 2000-2015 hybris AG
* All rights reserved.
*
* This software is the confidential and proprietary information of hybris
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with hybris.
*
*/

package de.hybris.platform.travelfacades.reservation.handlers;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;


/**
 * The interface Accommodation reservation handler.
 */
public interface AccommodationReservationHandler
{
	/**
	 * Handle method to populate {@link AccommodationReservationData} with details from {@link AbstractOrderModel}.
	 *
	 * @param abstractOrder
	 * 		the abstract order
	 * @param accommodationReservationData
	 * 		the accommodation reservation data
	 * @throws AccommodationPipelineException
	 * 		the accommodation pipeline exception
	 */
	void handle(AbstractOrderModel abstractOrder, AccommodationReservationData accommodationReservationData)
			throws AccommodationPipelineException;
}
