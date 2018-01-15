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

package de.hybris.platform.travelservices.strategies.stock.accommodation.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for DefaultAccommodationReservationCreationStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationReservationCreationStrategyTest
{
	@InjectMocks
	private DefaultAccommodationReservationCreationStrategy strategy;
	@Mock
	private AccommodationOrderEntryGroupModel entryGroup;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private AccommodationOfferingModel accommodationOffering;
	@Mock
	private RoomRateProductModel roomRateProduct;

	@Test
	public void testCreate()
	{
		Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
		Mockito.when(entryGroup.getStartingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(entryGroup.getEndingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("05/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(orderEntry.getQuantity()).thenReturn(1L);
		assertEquals(2, strategy.create(orderEntry).size());
	}

	@Test
	public void testCreateOnSameDate()
	{
		Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
		Mockito.when(entryGroup.getStartingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(entryGroup.getEndingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(orderEntry.getQuantity()).thenReturn(1L);
		assertEquals(0, strategy.create(orderEntry).size());
	}
}
