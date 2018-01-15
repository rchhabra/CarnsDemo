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

package de.hybris.platform.travelfulfilmentprocess.warehouse;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.WarehouseConsignmentState;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehouse.WarehouseConsignmentStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultWarehouse2ProcessAdapter}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWarehouse2ProcessAdapterTest
{

	@InjectMocks
	private DefaultWarehouse2ProcessAdapter defaultWarehouse2ProcessAdapter;

	private Map<WarehouseConsignmentStatus, WarehouseConsignmentState> statusMap;

	@Mock
	private ModelService modelService;

	@Mock
	private BusinessProcessService businessProcessService;

	private ConsignmentModel consignment;

	@Before
	public void setUp()
	{
		statusMap = new HashMap<>(3);
		statusMap.put(WarehouseConsignmentStatus.COMPLETE, WarehouseConsignmentState.COMPLETE);
		statusMap.put(WarehouseConsignmentStatus.CANCEL, WarehouseConsignmentState.CANCEL);
		statusMap.put(WarehouseConsignmentStatus.PARTIAL, WarehouseConsignmentState.PARTIAL);

		final ConsignmentProcessModel process1 = new ConsignmentProcessModel();
		consignment = new ConsignmentModel();
		consignment.setConsignmentProcesses(Arrays.asList(process1));
		defaultWarehouse2ProcessAdapter.setStatusMap(statusMap);
		Mockito.doNothing().when(businessProcessService).triggerEvent(Matchers.anyString());
	}

	@Test
	public void testReceiveConsignmentStatus()
	{
		defaultWarehouse2ProcessAdapter.receiveConsignmentStatus(consignment, WarehouseConsignmentStatus.COMPLETE);
		verify(businessProcessService, times(1)).triggerEvent(Matchers.anyString());
	}

	@Test(expected = IllegalStateException.class)
	public void testReceiveConsignmentStatusForIllegalStateException()
	{
		defaultWarehouse2ProcessAdapter.receiveConsignmentStatus(consignment, null);
	}
}
