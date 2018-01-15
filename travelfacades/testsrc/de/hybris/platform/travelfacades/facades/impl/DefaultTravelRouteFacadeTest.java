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
*/

package de.hybris.platform.travelfacades.facades.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultTravelRouteFacadeTest
{
	private DefaultTravelRouteFacade travelRouteFacade;

	@Mock
	private TravelRouteService travelRouteService;

	@Mock
	private Converter<TravelRouteModel, TravelRouteData> travelRouteConverter;

	@Mock
	private TravelRouteData travelRouteData;

	@Mock
	private TravelRouteModel travelRouteModel;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		travelRouteFacade = new DefaultTravelRouteFacade();
		travelRouteFacade.setTravelRouteService(travelRouteService);
		travelRouteFacade.setTravelRouteConverter(travelRouteConverter);
	}

	@Test
	public void testGetTravelRoutesForOriginDestinationCode()
	{
		final List<TravelRouteModel> travelRouteList = new ArrayList<>();
		travelRouteList.add(travelRouteModel);
		when(travelRouteFacade.getTravelRouteService().getTravelRoutes(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(travelRouteList);
		final List<TravelRouteData> travelRoutes = travelRouteFacade.getTravelRoutes(Matchers.anyString(), Matchers.anyString());
		Assert.assertNotNull(travelRoutes);
	}

	@Test
	public void testEmptyTravelRoutesForOriginDestinationCode()
	{
		when(travelRouteFacade.getTravelRouteService().getTravelRoutes(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(Collections.emptyList());
		final List<TravelRouteData> travelRoutes = travelRouteFacade.getTravelRoutes(Matchers.anyString(), Matchers.anyString());
		Assert.assertEquals(travelRoutes, Collections.emptyList());
	}

	@Test
	public void testGetTravelRouteForRouteCode()
	{
		when(travelRouteFacade.getTravelRouteService().getTravelRoute(Matchers.anyString())).thenReturn(travelRouteModel);
		travelRouteFacade.getTravelRoute(Matchers.anyString());
		verify(travelRouteFacade.getTravelRouteConverter(), times(1)).convert(travelRouteModel);
	}

}
