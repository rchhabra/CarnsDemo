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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for DefaultAccommodationProductManageStockStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationProductManageStockStrategyTest
{
	@InjectMocks
	private DefaultAccommodationProductManageStockStrategy strategy;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private AccommodationOrderEntryGroupModel entryGroup;
	@Mock
	private TravelCommerceStockService commerceStockService;

	@Test
	public void testrReserve()
	{
		try
		{
			Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
			Mockito.when(entryGroup.getStartingDate())
					.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
			Mockito.when(entryGroup.getEndingDate())
					.thenReturn(TravelDateUtils.convertStringDateToDate("05/01/2018", TravelservicesConstants.DATE_PATTERN));
			Mockito.doNothing().when(
					commerceStockService).reservePerDateProduct(Matchers.any(ProductModel.class), Matchers.any(Date.class),
							Matchers.anyInt(), Matchers.anyList());
			strategy.reserve(orderEntry);
		}
		catch (final InsufficientStockLevelException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testRelease()
	{
		Mockito.when(orderEntry.getQuantity()).thenReturn(1L);
		Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
		Mockito.when(entryGroup.getStartingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(entryGroup.getEndingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("05/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.doNothing().when(commerceStockService).releasePerDateProduct(Matchers.any(ProductModel.class),
				Matchers.any(Date.class), Matchers.anyInt(), Matchers.anyList());
		strategy.release(orderEntry);
	}

	@Test
	public void testReleaseWithZeroQuantity()
	{
		Mockito.when(orderEntry.getQuantity()).thenReturn(0L);
		strategy.release(orderEntry);
	}

	@Test
	public void testReleaseOnSameDates()
	{
		Mockito.when(orderEntry.getQuantity()).thenReturn(1L);
		Mockito.when(orderEntry.getEntryGroup()).thenReturn(entryGroup);
		Mockito.when(entryGroup.getStartingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		Mockito.when(entryGroup.getEndingDate())
				.thenReturn(TravelDateUtils.convertStringDateToDate("03/01/2018", TravelservicesConstants.DATE_PATTERN));
		strategy.release(orderEntry);
	}

}
