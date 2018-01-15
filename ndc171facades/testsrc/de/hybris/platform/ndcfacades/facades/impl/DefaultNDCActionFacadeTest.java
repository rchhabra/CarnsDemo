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

package de.hybris.platform.ndcfacades.facades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNDCActionFacadeTest
{
	DefaultNDCActionFacade defaultNDCActionFacade;

	@Test
	public void testDefaultNDCActionFacade()
	{
		final Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> ndcBookingActionEnabledCalculationStrategiesMap=new HashMap<>();
		final Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> bookingActionEnabledCalculationStrategiesMap=new HashMap<>();

		final ActionTypeOption actionTypeOption=ActionTypeOption.ACCEPT_BOOKING;
		final List<BookingActionEnabledEvaluatorStrategy> bookingActionEnabledEvaluatorStrategies=new ArrayList<>();

		ndcBookingActionEnabledCalculationStrategiesMap.put(actionTypeOption, bookingActionEnabledEvaluatorStrategies);

		bookingActionEnabledCalculationStrategiesMap.put(actionTypeOption, bookingActionEnabledEvaluatorStrategies);

		defaultNDCActionFacade = new DefaultNDCActionFacade(ndcBookingActionEnabledCalculationStrategiesMap,
				bookingActionEnabledCalculationStrategiesMap);
	}

}
