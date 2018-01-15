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

package de.hybris.platform.travelfulfilmentprocess.test;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.travelfulfilmentprocess.actions.order.SubprocessesCompletedAction;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SubprocessesCompletedActionTest
{
	@InjectMocks
	private SubprocessesCompletedAction action;

	@Mock
	private OrderProcessModel process;

	@Mock
	private ConsignmentProcessModel consignMentProcess;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteActionOK()
	{
		Mockito.when(process.getConsignmentProcesses()).thenReturn(Stream.of(consignMentProcess).collect(Collectors.toList()));
		Mockito.when(consignMentProcess.isDone()).thenReturn(Boolean.TRUE);
		assertEquals(Transition.OK, action.executeAction(process));
	}

	@Test
	public void testExecuteActionNOK()
	{
		Mockito.when(process.getConsignmentProcesses()).thenReturn(Stream.of(consignMentProcess).collect(Collectors.toList()));
		Mockito.when(consignMentProcess.isDone()).thenReturn(Boolean.FALSE);
		assertEquals(Transition.NOK, action.executeAction(process));
	}

}
