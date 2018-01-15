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

package de.hybris.platform.travelservices.event;

import de.hybris.platform.orderprocessing.events.OrderProcessingEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;


/**
 * The type Amendment finished event.
 */
public class AmendmentFinishedEvent extends OrderProcessingEvent
{
	/**
	 * Instantiates a new Amendment finished event.
	 *
	 * @param process
	 * 		the process
	 */
	public AmendmentFinishedEvent(OrderProcessModel process)
	{
		super(process);
	}
}
