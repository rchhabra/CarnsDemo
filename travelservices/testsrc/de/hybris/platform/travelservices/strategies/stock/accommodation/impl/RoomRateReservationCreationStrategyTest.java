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
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for RoomRateReservationCreationStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RoomRateReservationCreationStrategyTest
{
	@InjectMocks
	RoomRateReservationCreationStrategy strategy;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private AccommodationOrderEntryGroupModel entryGroup;
	@Mock
	private RoomRateProductModel roomRateProduct;
	@Mock
	private AccommodationOrderEntryInfoModel orderEntryInfo;
	@Mock
	private AccommodationOfferingModel accommodationOffering;
	@Mock
	private AccommodationModel accommodation;

	@Test
	public void testCreate()
	{
		Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(orderEntry.getQuantity()).thenReturn(1L);
		Mockito.when(orderEntry.getAccommodationOrderEntryInfo()).thenReturn(orderEntryInfo);
		Mockito.when(orderEntryInfo.getDates())
				.thenReturn(Arrays.asList(TravelDateUtils.convertStringDateToDate("02/01/2018", TravelservicesConstants.DATE_PATTERN),
						TravelDateUtils.convertStringDateToDate("04/01/2018", TravelservicesConstants.DATE_PATTERN)));
		Mockito.when(entryGroup.getAccommodationOffering()).thenReturn(accommodationOffering);
		Mockito.when(entryGroup.getAccommodation()).thenReturn(accommodation);
		assertEquals(3, strategy.create(orderEntry).size());
	}

	@Test
	public void testCreateWithZeroQuatity()
	{
		Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
		Mockito.when(orderEntry.getProduct()).thenReturn(roomRateProduct);
		Mockito.when(orderEntry.getQuantity()).thenReturn(0L);
		Mockito.when(orderEntry.getAccommodationOrderEntryInfo()).thenReturn(orderEntryInfo);
		Mockito.when(orderEntryInfo.getDates())
				.thenReturn(Arrays.asList(TravelDateUtils.convertStringDateToDate("02/01/2018", TravelservicesConstants.DATE_PATTERN),
						TravelDateUtils.convertStringDateToDate("04/01/2018", TravelservicesConstants.DATE_PATTERN)));
		Mockito.when(entryGroup.getAccommodationOffering()).thenReturn(accommodationOffering);
		Mockito.when(entryGroup.getAccommodation()).thenReturn(accommodation);
		assertEquals(3, strategy.create(orderEntry).size());
	}
}
