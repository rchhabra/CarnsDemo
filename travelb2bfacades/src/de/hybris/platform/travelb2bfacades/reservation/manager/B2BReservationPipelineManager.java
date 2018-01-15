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
 */
package de.hybris.platform.travelb2bfacades.reservation.manager;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelb2bfacades.data.B2BReservationData;


/**
 * Interface for the B2B Reservation Pipeline Manager.
 */
public interface B2BReservationPipelineManager
{

	/**
	 * Execute the pipeline.
	 *
	 * @param abstractOrderModel
	 * 		the abstractOrder
	 *
	 * @return B2BReservationData the b2b reservation
	 */
	B2BReservationData executePipeline(AbstractOrderModel abstractOrderModel);

}
