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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelfulfilmentprocess.constants.TravelfulfilmentprocessConstants;
import de.hybris.platform.travelfulfilmentprocess.strategy.impl.SplittingStrategyByTransportType;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link SplittingStrategyByTransportType}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SplittingStrategyByTransportTypeTest
{
	@InjectMocks
	private SplittingStrategyByTransportType splittingStrategyByTransportType;

	@Mock
	private ConsignmentModel createdOne;

	@Test
	public void testafterSplittingForNullParameter()
	{
		final OrderEntryGroup group = new OrderEntryGroup();
		splittingStrategyByTransportType.afterSplitting(group, createdOne);
		verify(createdOne, times(0)).setCode(Matchers.anyString());
	}

	@Test
	public void testafterSplitting()
	{
		final OrderEntryGroup group = new OrderEntryGroup();

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("TEST_TRANSPORT_OFFERING_CODE");
		group.setParameter(TravelfulfilmentprocessConstants.TRANSPORT_OFFERING, transportOfferingModel);

		final TravellerModel travellerModel = new TravellerModel();
		group.setParameter(TravelfulfilmentprocessConstants.TRAVELLER, travellerModel);
		final ConsignmentModel createdOne1 = new ConsignmentModel();
		splittingStrategyByTransportType.afterSplitting(group, createdOne1);
		Assert.assertEquals(transportOfferingModel, createdOne1.getWarehouse());
	}
}
