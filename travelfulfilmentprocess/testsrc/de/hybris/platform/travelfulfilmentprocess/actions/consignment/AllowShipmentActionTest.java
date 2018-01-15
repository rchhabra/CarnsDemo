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

package de.hybris.platform.travelfulfilmentprocess.actions.consignment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.warehouse.Process2WarehouseAdapter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AllowShipmentActionTest
{
	@InjectMocks
	AllowShipmentAction allowShipmentAction;

	@Mock
	private Process2WarehouseAdapter process2WarehouseAdapter;

	@Test
	public void testExecute()
	{
		final ConsignmentProcessModel process = new ConsignmentProcessModel();
		Assert.assertEquals(allowShipmentAction.execute(process), AllowShipmentAction.Transition.ERROR.toString());

		final ConsignmentModel consignment = new ConsignmentModel();
		process.setConsignment(consignment);
		final OrderModel order = new OrderModel();
		order.setStatus(OrderStatus.CANCELLING);
		consignment.setOrder(order);
		Assert.assertEquals(allowShipmentAction.execute(process), AllowShipmentAction.Transition.CANCEL.toString());

		order.setStatus(OrderStatus.ACTIVE);
		Mockito.doNothing().when(process2WarehouseAdapter).shipConsignment(consignment);
		Assert.assertEquals(allowShipmentAction.execute(process), AllowShipmentAction.Transition.DELIVERY.toString());

		final DeliveryModeModel deliveryMode = new PickUpDeliveryModeModel();
		consignment.setDeliveryMode(deliveryMode);
		Assert.assertEquals(allowShipmentAction.execute(process), AllowShipmentAction.Transition.PICKUP.toString());
	}

}
