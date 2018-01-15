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

package de.hybris.platform.travelservices.strategies.stock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultStockReservationCreationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultStockReservationCreationStrategyTest
{
	@InjectMocks
	DefaultStockReservationCreationStrategy defaultStockReservationCreationStrategy;

	@Test
	public void testCreate()
	{
		final List<TransportOfferingModel> transportOfferings=new ArrayList<>();
		transportOfferings.add(new TransportOfferingModel());
		final TravelOrderEntryInfoModel travelOrderEntryInfoModel=new TravelOrderEntryInfoModel();
		travelOrderEntryInfoModel.setTransportOfferings(transportOfferings);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setTravelOrderEntryInfo(travelOrderEntryInfoModel);
		entry.setQuantity(100l);
		Assert.assertNotNull(defaultStockReservationCreationStrategy.create(entry));

	}

}
