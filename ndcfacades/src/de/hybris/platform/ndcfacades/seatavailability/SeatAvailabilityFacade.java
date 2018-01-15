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

package de.hybris.platform.ndcfacades.seatavailability;

import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS;


/**
 * An interface for {@link SeatAvailabilityRQ}
 */
public interface SeatAvailabilityFacade
{

	/**
	 * This method returns an instance of {@link SeatAvailabilityRS} having seat map information for given
	 * {@link SeatAvailabilityRQ}
	 *
	 * @param seatAvailabilityRQ
	 * 		the seat availability rq
	 *
	 * @return the seat map
	 */
	SeatAvailabilityRS getSeatMap(SeatAvailabilityRQ seatAvailabilityRQ);

}
