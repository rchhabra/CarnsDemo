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

package de.hybris.platform.travelfacades.order.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link PerLegPopulatePropertyMapStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PerLegPopulatePropertyMapStrategyTest
{
	@Mock
	private TravelRouteService travelRouteService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private TransportOfferingModel transportOfferingModel;

	@Mock
	private TravelRouteModel travelRouteModel;

	@InjectMocks
	PerLegPopulatePropertyMapStrategy perLegPopulatePropertyMapStrategy;

	private final String TEST_TRANSPORT_OFFERING_CODE = "TEST_TRANSPORT_OFFERING_CODE";

	private final String TEST_TRAVEL_ROUTE_CODE = "TEST_TRAVEL_ROUTE_CODE";

	@Before
	public void setUp()
	{
		given(transportOfferingService.getTransportOffering(TEST_TRANSPORT_OFFERING_CODE)).willReturn(transportOfferingModel);
		given(travelRouteService.getTravelRoute(TEST_TRAVEL_ROUTE_CODE)).willReturn(travelRouteModel);
	}

	@Test
	public void testPopulatePropertiesMapForEmptyScenarios()
	{
		final Map<String, Object> results = perLegPopulatePropertyMapStrategy.populatePropertiesMap(null, 0, null, null,
				Boolean.TRUE, AmendStatus.NEW);

		Assert.assertEquals(AmendStatus.NEW, results.get(AbstractOrderEntryModel.AMENDSTATUS));
	}

	@Test
	public void testPopulatePropertiesMap()
	{
		final Map<String, Object> results = perLegPopulatePropertyMapStrategy.populatePropertiesMap(TEST_TRAVEL_ROUTE_CODE, 0,
				Arrays.asList(TEST_TRANSPORT_OFFERING_CODE), null, Boolean.TRUE, AmendStatus.NEW);

		Assert.assertEquals(AmendStatus.NEW, results.get(AbstractOrderEntryModel.AMENDSTATUS));
		Assert.assertSame(travelRouteModel, results.get(TravelOrderEntryInfoModel.TRAVELROUTE));
	}
}
