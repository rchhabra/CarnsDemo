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
package de.hybris.platform.travelfacades.reservation.manager;


import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Pipeline Manager class that will return a {@link ReservationData} after executing a list of handlers
 * on the {@link AbstractOrderModel} given as input
 */
public interface ReservationPipelineManager
{
	/**
	 * Execute pipeline reservation data.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 *
	 * @return the reservation data
	 */
	ReservationData executePipeline(AbstractOrderModel abstractOrderModel);
}
