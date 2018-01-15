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
 *
 */

package de.hybris.platform.travelfacades.reservation.manager;

import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Pipeline Manager class that will return a {@link GlobalTravelReservationData} after executing a list of handlers on
 * the {@link AbstractOrderModel} given as input
 */
public interface GlobalTravelReservationPipelineManager
{
	/**
	 * Execute pipeline reservation data.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 *
	 * @return the global reservation data
	 */
	GlobalTravelReservationData executePipeline(AbstractOrderModel abstractOrderModel);

	/**
	 * Execute pipeline.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @param globalTravelReservationData
	 * 		the global travel reservation data
	 */
	void executePipeline(AbstractOrderModel abstractOrderModel, GlobalTravelReservationData globalTravelReservationData);
}
