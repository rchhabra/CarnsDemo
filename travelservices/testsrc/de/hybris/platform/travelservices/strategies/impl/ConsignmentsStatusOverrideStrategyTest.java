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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsignmentsStatusOverrideStrategyTest
{

	@InjectMocks
	private ConsignmentsStatusOverrideStrategy consignmentsStatusOverrideStrategy;

	@Mock
	private ModelService modelService;

	@Test
	public void testUpdateConsignmentsStatusWithActiveEntry()
	{
		final Set<ConsignmentModel> newOrderConsignments = new HashSet<ConsignmentModel>(2);
		final Set<ConsignmentModel> originalOrderConsignments = new HashSet<ConsignmentModel>(1);

		final OrderModel newOrder = new OrderModel();
		final OrderModel originalOrder = new OrderModel();

		final ConsignmentModel consignmentNewOrderCheckIn = new ConsignmentModel();
		final ConsignmentModel consignmentNewOrderNonCheckIn = new ConsignmentModel();

		newOrderConsignments.add(consignmentNewOrderCheckIn);
		newOrderConsignments.add(consignmentNewOrderNonCheckIn);

		final ConsignmentEntryModel entry = new ConsignmentEntryModel();
		final AbstractOrderEntryModel entryOrder = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		entryOrder.setActive(true);
		entryOrder.setTravelOrderEntryInfo(orderEntryInfo);
		entry.setOrderEntry(entryOrder);


		final Set<ConsignmentEntryModel> ConsignmentEntryModels = new HashSet<ConsignmentEntryModel>(1);
		ConsignmentEntryModels.add(entry);
		consignmentNewOrderNonCheckIn.setConsignmentEntries(ConsignmentEntryModels);


		newOrder.setConsignments(newOrderConsignments);

		final ConsignmentModel consignmentOriginalOrder = new ConsignmentModel();
		originalOrderConsignments.add(consignmentOriginalOrder);
		originalOrder.setConsignments(originalOrderConsignments);

		consignmentNewOrderCheckIn.setCode("CONSIGNMENT_CODE");
		consignmentOriginalOrder.setCode("CONSIGNMENT_CODE");
		consignmentOriginalOrder.setStatus(ConsignmentStatus.CHECKED_IN);

		consignmentsStatusOverrideStrategy.updateConsignmentsStatus(newOrder, originalOrder);

	}

	@Test
	public void testUpdateConsignmentsStatusWithNotActiveEntry()
	{
		final Set<ConsignmentModel> newOrderConsignments = new HashSet<ConsignmentModel>(2);
		final Set<ConsignmentModel> originalOrderConsignments = new HashSet<ConsignmentModel>(1);

		final OrderModel newOrder = new OrderModel();
		final OrderModel originalOrder = new OrderModel();

		final ConsignmentModel consignmentNewOrderCheckIn = new ConsignmentModel();
		final ConsignmentModel consignmentNewOrderNonCheckIn = new ConsignmentModel();

		newOrderConsignments.add(consignmentNewOrderCheckIn);
		newOrderConsignments.add(consignmentNewOrderNonCheckIn);

		final ConsignmentEntryModel entry = new ConsignmentEntryModel();
		final AbstractOrderEntryModel entryOrder = new AbstractOrderEntryModel();
		entryOrder.setActive(false);
		entry.setOrderEntry(entryOrder);

		final Set<ConsignmentEntryModel> ConsignmentEntryModels = new HashSet<ConsignmentEntryModel>(1);
		ConsignmentEntryModels.add(entry);
		consignmentNewOrderNonCheckIn.setConsignmentEntries(ConsignmentEntryModels);


		newOrder.setConsignments(newOrderConsignments);

		final ConsignmentModel consignmentOriginalOrder = new ConsignmentModel();
		originalOrderConsignments.add(consignmentOriginalOrder);
		originalOrder.setConsignments(originalOrderConsignments);

		consignmentNewOrderCheckIn.setCode("CONSIGNMENT_CODE");
		consignmentOriginalOrder.setCode("CONSIGNMENT_CODE");
		consignmentOriginalOrder.setStatus(ConsignmentStatus.CHECKED_IN);

		consignmentsStatusOverrideStrategy.updateConsignmentsStatus(newOrder, originalOrder);
	}
}
