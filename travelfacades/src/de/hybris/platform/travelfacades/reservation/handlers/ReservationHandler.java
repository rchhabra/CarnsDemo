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
package de.hybris.platform.travelfacades.reservation.handlers;

import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Interface for handlers classes that will be updating the {@link ReservationData} based on the {@link AbstractOrderModel}
 * given in input
 */
public interface ReservationHandler
{
	/**
	 * Handle method.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @param reservationData
	 * 		the reservation data
	 */
	void handle(AbstractOrderModel abstractOrderModel, ReservationData reservationData);
}
