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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.OrderSplittingService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelfulfilmentprocess.actions.order.SplitOrderAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class SplitOrderActionTest
{
	@InjectMocks
	private SplitOrderAction action;

	@Mock
	private OrderProcessModel orderProcess;

	private OrderModel order;

	@Mock
	private AbstractOrderEntryModel entry;

	@Mock
	private ConsignmentModel consignment;

	@Mock
	private ConsignmentEntryModel consignmentEntry;

	@Mock
	private OrderSplittingService orderSplittingService;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private ConsignmentProcessModel consignmentProcess;

	@Mock
	private ModelService modelService;

	@Before
	public void prepare()
	{
		order = new OrderModel();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecute() throws Exception
	{
		final List<AbstractOrderEntryModel> entriesToSplit = new ArrayList<AbstractOrderEntryModel>();
		entriesToSplit.add(entry);
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		order.setEntries(Stream.of(entry).collect(Collectors.toList()));
		Mockito.when(entry.getConsignmentEntries()).thenReturn(Collections.emptySet());
		Mockito.when(orderSplittingService.splitOrderForConsignment(order, entriesToSplit))
				.thenReturn(Stream.of(consignment).collect(Collectors.toList()));
		Mockito.when(businessProcessService.createProcess(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(consignmentProcess);
		Mockito.doNothing().when(businessProcessService).startProcess(consignmentProcess);
		action.executeAction(orderProcess);
		assertEquals(OrderStatus.ORDER_SPLIT, order.getStatus());
	}

	@Test
	public void testExecuteWithConsignments() throws Exception
	{
		Mockito.when(orderProcess.getOrder()).thenReturn(order);
		order.setEntries(Stream.of(entry).collect(Collectors.toList()));
		Mockito.when(entry.getConsignmentEntries()).thenReturn(Stream.of(consignmentEntry).collect(Collectors.toSet()));
		Mockito.when(orderSplittingService.splitOrderForConsignment(order, Collections.emptyList()))
				.thenReturn(Stream.of(consignment).collect(Collectors.toList()));
		Mockito.when(businessProcessService.createProcess(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(consignmentProcess);
		Mockito.doNothing().when(businessProcessService).startProcess(consignmentProcess);
		action.executeAction(orderProcess);
		assertEquals(OrderStatus.ORDER_SPLIT, order.getStatus());
	}
}
