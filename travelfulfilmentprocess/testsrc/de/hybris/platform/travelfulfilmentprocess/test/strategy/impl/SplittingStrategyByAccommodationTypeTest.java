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

package de.hybris.platform.travelfulfilmentprocess.test.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelfulfilmentprocess.constants.TravelfulfilmentprocessConstants;
import de.hybris.platform.travelfulfilmentprocess.strategy.impl.SplittingStrategyByAccommodationType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link SplittingStrategyByAccommodationType}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SplittingStrategyByAccommodationTypeTest
{

	@InjectMocks
	SplittingStrategyByAccommodationType splittingStrategyByAccommodationType;

	@Mock
	private BookingService bookingService;

	@Mock
	private ConsignmentModel consignment;

	@Before
	public void setUp()
	{
		final OrderEntryGroup orderEntryGroup = new OrderEntryGroup();
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		final AbstractOrderModel order = new OrderModel();
		orderEntry.setOrder(order);
		orderEntryGroup.add(orderEntry);
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModel.setAccommodation(new AccommodationModel());
		accommodationOrderEntryGroupModel.setRoomStayRefNumber(0);
		accommodationOrderEntryGroupModel.setEntries(Collections.emptyList());
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = Arrays
				.asList(accommodationOrderEntryGroupModel);
		Mockito.when(bookingService.getAccommodationOrderEntryGroups(order)).thenReturn(accommodationOrderEntryGroups);

	}

	@Test
	public void testAfterSplittingForNull()
	{
		final OrderEntryGroup orderEntryGroup = new OrderEntryGroup();
		splittingStrategyByAccommodationType.afterSplitting(orderEntryGroup, consignment);
		Mockito.verify(consignment, Mockito.times(0)).setCode(Matchers.anyString());
	}

	@Test
	public void testAfterSplitting()
	{

		final OrderEntryGroup orderEntryGroup = new OrderEntryGroup();
		final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
		orderEntryGroup.setParameter(TravelfulfilmentprocessConstants.ACCOMMODATION_OFFERING, accommodationOfferingModel);
		final AccommodationModel accommodation = new AccommodationModel();
		accommodation.setCode("TEST_CODE");
		orderEntryGroup.setParameter(TravelfulfilmentprocessConstants.ACCOMMODATION, accommodation);
		orderEntryGroup.setParameter(TravelfulfilmentprocessConstants.REF_NUMBER, 0);

		final ConsignmentModel consignmentModel = new ConsignmentModel();

		splittingStrategyByAccommodationType.afterSplitting(orderEntryGroup, consignmentModel);
		Assert.assertEquals(consignmentModel.getWarehouse(), accommodationOfferingModel);
	}


}
