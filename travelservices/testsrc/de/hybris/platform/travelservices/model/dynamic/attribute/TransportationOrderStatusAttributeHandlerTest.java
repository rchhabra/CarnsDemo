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

package de.hybris.platform.travelservices.model.dynamic.attribute;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.services.BookingService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportationOrderStatusAttributeHandlerTest
{
	@InjectMocks
	private TransportationOrderStatusAttributeHandler transportationOrderStatusAttributeHandler;

	@Mock
	private BookingService bookingService;

	@Test
	public void testGet()
	{
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setStatus(OrderStatus.ACTIVE);
		BDDMockito.given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(false);
		Assert.assertNull(transportationOrderStatusAttributeHandler.get(abstractOrderModel));

		BDDMockito.given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(true);
		BDDMockito.given(bookingService.isReservationCancelled(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(false);
		Assert.assertEquals(OrderStatus.ACTIVE, transportationOrderStatusAttributeHandler.get(abstractOrderModel));

	}

}
