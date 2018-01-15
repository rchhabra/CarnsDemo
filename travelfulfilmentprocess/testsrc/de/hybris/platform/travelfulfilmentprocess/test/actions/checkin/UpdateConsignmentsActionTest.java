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

package de.hybris.platform.travelfulfilmentprocess.test.actions.checkin;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.CheckInProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelfulfilmentprocess.actions.checkin.UpdateConsignmentsAction;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.ordersplitting.impl.DefaultTravelConsignmentService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit test for the implementation {@link UpdateConsignmentsAction}
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateConsignmentsActionTest
{
	@InjectMocks
	UpdateConsignmentsAction updateConsignmentsAction;

	@Mock
	private TravellerService travellerService;

	@Mock
	private DefaultTravelConsignmentService travelConsignmentService;

	@Mock
	private ModelService mockModelService;

	private CheckInProcessModel testCheckInProcessModel;

	@Before
	public void setUp()
	{
		final int originDestinationRefNum = 0;

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setCode("testTransportOffering");

		final Collection<TransportOfferingModel> transportOfferings = new ArrayList<TransportOfferingModel>();
		transportOfferings.add(transportOffering);

		final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
		travelOrderEntryInfoModel.setOriginDestinationRefNumber(originDestinationRefNum);
		travelOrderEntryInfoModel.setTransportOfferings(transportOfferings);

		final OrderEntryModel orderEntry = new OrderEntryModel();
		orderEntry.setTravelOrderEntryInfo(travelOrderEntryInfoModel);

		final OrderEntryModel orderEntry1 = new OrderEntryModel();
		final OrderEntryModel orderEntry2 = new OrderEntryModel();
		final TravelOrderEntryInfoModel travelOrderEntryInfoModel1 = new TravelOrderEntryInfoModel();
		orderEntry2.setTravelOrderEntryInfo(travelOrderEntryInfoModel1);

		final TravelOrderEntryInfoModel travelOrderEntryInfoModel2 = new TravelOrderEntryInfoModel();
		travelOrderEntryInfoModel2.setOriginDestinationRefNumber(1);
		travelOrderEntryInfoModel2.setTransportOfferings(transportOfferings);

		final OrderEntryModel orderEntry3 = new OrderEntryModel();
		orderEntry3.setTravelOrderEntryInfo(travelOrderEntryInfoModel2);


		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<AbstractOrderEntryModel>();
		orderEntries.add(orderEntry);
		orderEntries.add(orderEntry1);
		orderEntries.add(orderEntry2);
		orderEntries.add(orderEntry3);

		final OrderModel orderModel = new OrderModel();
		orderModel.setEntries(orderEntries);

		final List<String> travellers = new ArrayList<String>();
		travellers.add("testTraveller");

		testCheckInProcessModel = new CheckInProcessModel();
		testCheckInProcessModel.setOrder(orderModel);
		testCheckInProcessModel.setOriginDestinationRefNumber(originDestinationRefNum);
		testCheckInProcessModel.setTravellers(travellers);

		updateConsignmentsAction.setModelService(mockModelService);
		given(travellerService.getExistingTraveller(Matchers.anyString())).willReturn(new TravellerModel());
	}

	@Test
	public void testExecuteAction_NoOrder() throws RetryLaterException, Exception
	{
		Assert.assertEquals(Transition.NOK, updateConsignmentsAction.executeAction(new CheckInProcessModel()));
	}

	@Test
	public void testExecuteAction_NoConsignment() throws RetryLaterException, Exception
	{
		given(travelConsignmentService.getConsignment(Matchers.any(TransportOfferingModel.class), Matchers.any(OrderModel.class),
				Matchers.any(TravellerModel.class))).willReturn(null);

		Assert.assertEquals(Transition.NOK, updateConsignmentsAction.executeAction(testCheckInProcessModel));

		given(travellerService.getExistingTraveller(Matchers.anyString())).willReturn(null);
		Assert.assertEquals(Transition.NOK, updateConsignmentsAction.executeAction(testCheckInProcessModel));
	}

	@Test
	public void testExecuteAction_ConsignmentWithReadyStatus() throws RetryLaterException, Exception
	{
		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setStatus(ConsignmentStatus.READY);

		given(travelConsignmentService.getConsignment(Matchers.any(TransportOfferingModel.class), Matchers.any(OrderModel.class),
				Matchers.any(TravellerModel.class))).willReturn(consignment);

		Assert.assertEquals(Transition.OK, updateConsignmentsAction.executeAction(testCheckInProcessModel));
	}

	@Test
	public void testExecuteAction_ConsignmentWithCheckInStatus() throws RetryLaterException, Exception
	{
		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setStatus(ConsignmentStatus.CHECKED_IN);

		given(travelConsignmentService.getConsignment(Matchers.any(TransportOfferingModel.class), Matchers.any(OrderModel.class),
				Matchers.any(TravellerModel.class))).willReturn(consignment);

		Assert.assertEquals(Transition.NOK, updateConsignmentsAction.executeAction(testCheckInProcessModel));
	}
}
