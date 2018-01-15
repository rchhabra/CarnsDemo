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
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelfulfilmentprocess.constants.TravelfulfilmentprocessConstants;
import de.hybris.platform.travelfulfilmentprocess.strategy.impl.SplitByAccommodationEntryGroup;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link SplitByAccommodationEntryGroup}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SplitByAccommodationEntryGroupTest
{
	@InjectMocks
	SplitByAccommodationEntryGroup splitByAccommodationEntryGroup;

	@Mock
	private BookingService bookingService;

	@Test
	public void testPerform()
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

		Assert.assertTrue(splitByAccommodationEntryGroup.perform(Arrays.asList(orderEntryGroup)).get(0).getParameter(
				TravelfulfilmentprocessConstants.REF_NUMBER) == (accommodationOrderEntryGroups.get(0).getRoomStayRefNumber()));
	}

}
