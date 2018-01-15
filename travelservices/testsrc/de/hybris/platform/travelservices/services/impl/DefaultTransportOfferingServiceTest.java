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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.dao.TransportOfferingDao;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultTransportOfferingService implementation
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTransportOfferingServiceTest
{
	@InjectMocks
	private DefaultTransportOfferingService transportOfferingService;
	@Mock
	private TransportOfferingDao transportOfferingDao;
	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private TravelOrderEntryInfoModel travelOrderEntryInfo;

	@Test
	public void getTransportOfferingsTest()
	{
		Mockito.when(transportOfferingDao.findTransportOfferings(Matchers.anyString(), Matchers.any(Date.class)))
				.thenReturn(Stream.of(Mockito.mock(TransportOfferingModel.class)).collect(Collectors.toList()));
		Assert.assertNotNull(transportOfferingService.getTransportOfferings("HY31",
				TravelDateUtils.getDate("31/12/2016", TravelservicesConstants.DATE_PATTERN)));
		Assert.assertEquals(1, transportOfferingService.getTransportOfferings("HY31",
				TravelDateUtils.getDate("31/12/2016", TravelservicesConstants.DATE_PATTERN)).size());
	}

	@Test
	public void getTransportOfferingsWithExceptionTest()
	{
		Mockito.when(transportOfferingDao.findTransportOfferings(Matchers.anyString(), Matchers.any(Date.class)))
				.thenThrow(new ModelNotFoundException("Model Not Found"));
		Assert.assertNull(transportOfferingService.getTransportOfferings("HY31",
				TravelDateUtils.getDate("31/12/2016", TravelservicesConstants.DATE_PATTERN)));
	}

	@Test
	public void getTransportOfferingTest()
	{
		Mockito.when(transportOfferingDao.findTransportOffering(Matchers.anyString()))
				.thenReturn(Mockito.mock(TransportOfferingModel.class));
		Assert.assertNotNull(transportOfferingService.getTransportOffering("HY31"));
	}

	@Test
	public void getTransportOfferingsWithoutAnyParamsTest()
	{
		Mockito.when(transportOfferingDao.findTransportOfferings())
				.thenReturn(Stream.of(Mockito.mock(TransportOfferingModel.class)).collect(Collectors.toList()));
		Assert.assertNotNull(transportOfferingService.getTransportOfferings());
		Assert.assertEquals(1, transportOfferingService.getTransportOfferings().size());
	}

	@Test
	public void getTransportOfferingsWithTOCodesTest()
	{
		Mockito.when(transportOfferingDao.getTransportOfferings(Stream.of("HY21").collect(Collectors.toList())))
				.thenReturn(Stream.of(Mockito.mock(TransportOfferingModel.class)).collect(Collectors.toList()));
		Assert.assertNotNull(transportOfferingService.getTransportOfferings(Stream.of("HY21").collect(Collectors.toList())));
		Assert.assertEquals(1,
				transportOfferingService.getTransportOfferings(Stream.of("HY21").collect(Collectors.toList())).size());
	}

	@Test
	public void getTransportOfferingsMapTest()
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setCode("HY21");
		Mockito.when(transportOfferingDao.getTransportOfferings(Stream.of("HY21").collect(Collectors.toList())))
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Assert.assertNotNull(transportOfferingService.getTransportOfferingsMap(Stream.of("HY21").collect(Collectors.toList())));
		Assert.assertEquals(1,
				transportOfferingService.getTransportOfferingsMap(Stream.of("HY21").collect(Collectors.toList())).size());
	}

	@Test
	public void getTransportOfferingsMapWithEmptyTOListTest()
	{
		Mockito.when(transportOfferingDao.getTransportOfferings(Stream.of("HY21").collect(Collectors.toList())))
				.thenReturn(Collections.emptyList());
		Assert.assertEquals(0,
				transportOfferingService.getTransportOfferingsMap(Stream.of("HY21").collect(Collectors.toList())).size());
	}

	@Test
	public void getTransportOfferingsFromOrderEntriesTest()
	{
		Mockito.when(orderEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfo);
		final TransportOfferingModel transportOffering1 = new TransportOfferingModel();
		transportOffering1.setCode("HY21");
		final TransportOfferingModel transportOffering2 = new TransportOfferingModel();
		transportOffering2.setCode("HY21");
		Mockito.when(travelOrderEntryInfo.getTransportOfferings())
				.thenReturn(Arrays.asList(transportOffering1, transportOffering2));
		Assert.assertEquals(2,
				transportOfferingService
						.getTransportOfferingsFromOrderEntries(Stream.of(orderEntryModel).collect(Collectors.toList())).size());
	}
}
