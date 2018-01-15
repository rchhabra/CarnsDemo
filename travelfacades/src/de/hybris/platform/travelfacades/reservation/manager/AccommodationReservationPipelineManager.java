/*
 *
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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * The interface Accommodation reservation pipeline manager.
 */
public interface AccommodationReservationPipelineManager
{

	/**
	 * Execute pipeline accommodation reservation data.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @return the accommodation reservation data
	 */
	AccommodationReservationData executePipeline(AbstractOrderModel abstractOrderModel);

}
