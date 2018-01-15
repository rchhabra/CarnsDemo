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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.CheckInProcessModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelfulfilmentprocess.actions.checkin.PerformAutoSeatAllocationAction;
import de.hybris.platform.travelservices.strategies.AutoAccommodationAllocationStrategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PerformAutoSeatAllocationActionTest
{
	@InjectMocks
	private PerformAutoSeatAllocationAction action;
	@Mock
	private CheckInProcessModel process;
	@Mock
	private AutoAccommodationAllocationStrategy autoAccommodationAllocationStrategy;
	@Mock
	private OrderModel orderModel;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CatalogModel catalog;
	@Mock
	private BaseSiteService baseSiteService;

	@Test
	public void executeActionTest() throws RetryLaterException, Exception
	{
		Mockito.when(process.getOriginDestinationRefNumber()).thenReturn(0);
		final List<String> travellers = Stream.of("adult").collect(Collectors.toList());
		Mockito.when(process.getTravellers()).thenReturn(travellers);
		Mockito.when(process.getOrder()).thenReturn(orderModel);
		Mockito.when(orderModel.getSite()).thenReturn(baseSite);
		Mockito.when(catalog.getActiveCatalogVersion()).thenReturn(catalogVersionModel);
		Mockito.when(baseSiteService.getProductCatalogs(baseSite)).thenReturn(Stream.of(catalog).collect(Collectors.toList()));
		Mockito.doNothing().when(autoAccommodationAllocationStrategy).autoAllocateSeat(orderModel, 0, travellers);
		action.executeAction(process);
	}

}
