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
package de.hybris.platform.ndcfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelfacades.facades.impl.DefaultActionFacade;

import java.util.List;
import java.util.Map;


/**
 * NDC implementation of the {@link DefaultActionFacade}
 */
public class DefaultNDCActionFacade extends DefaultActionFacade
{

	/**
	 * Instantiates a new Default ndc action facade.
	 *
	 * @param ndcBookingActionEnabledCalculationStrategiesMap
	 * 		the ndc booking action enabled calculation strategies map
	 * @param bookingActionEnabledCalculationStrategiesMap
	 * 		the booking action enabled calculation strategies map
	 */
	public DefaultNDCActionFacade(
			final Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> ndcBookingActionEnabledCalculationStrategiesMap,
			final Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> bookingActionEnabledCalculationStrategiesMap)
	{
		for (final Map.Entry<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> entry : bookingActionEnabledCalculationStrategiesMap
				.entrySet())
		{
			if (ndcBookingActionEnabledCalculationStrategiesMap.containsKey(entry.getKey()))
			{
				entry.getValue().addAll(ndcBookingActionEnabledCalculationStrategiesMap.get(entry.getKey()));
			}
		}
		setBookingActionEnabledCalculationStrategiesMap(bookingActionEnabledCalculationStrategiesMap);
	}
}
