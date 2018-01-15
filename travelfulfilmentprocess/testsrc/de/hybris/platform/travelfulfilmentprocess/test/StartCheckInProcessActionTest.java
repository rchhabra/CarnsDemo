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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.CheckInProcessModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelfulfilmentprocess.actions.order.StartCheckInProcessAction;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class StartCheckInProcessActionTest
{

	@InjectMocks
	private StartCheckInProcessAction startCheckInProcessAction;

	@Mock
	private OrderProcessModel orderProcessModel;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private OrderModel orderModel;

	@Mock
	private CheckInProcessModel checkInProcessModel;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteActionWithtravellers() throws RetryLaterException, Exception
	{
		Mockito.when(orderProcessModel.getTravellers()).thenReturn(Stream.of("adult").collect(Collectors.toList()));
		Mockito.when(orderProcessModel.getOrder()).thenReturn(orderModel);
		Mockito.when(businessProcessService.createProcess(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(checkInProcessModel);
		Mockito.when(orderProcessModel.getOriginDestinationRefNumber()).thenReturn(0);
		Mockito.doNothing().when(modelService).save(checkInProcessModel);
		Mockito.doNothing().when(businessProcessService).startProcess(checkInProcessModel);
		startCheckInProcessAction.executeAction(orderProcessModel);
	}

	@Test
	public void testExecuteActionWithouttravellers() throws RetryLaterException, Exception
	{
		Mockito.when(orderProcessModel.getTravellers()).thenReturn(Collections.emptyList());
		startCheckInProcessAction.executeAction(orderProcessModel);
	}
}
