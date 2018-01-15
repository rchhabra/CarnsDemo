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

package de.hybris.platform.travelfacades.reservation.manager.impl;


import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelfacades.reservation.handlers.impl.ReservationBasicHandler;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultReservationPipelineManagerTest
{
	private DefaultReservationPipelineManager defaultReservationPipelineManager;
	@Mock
	private ReservationPipelineManager reservationPipelineManager;

	@Mock
	private AbstractOrderModel abstractOrderModel;

	@Mock
	private AbstractOrderEntryModel abstractOrderEntryModel;

	@Mock
	private CartModel cartModel;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultReservationPipelineManager = new DefaultReservationPipelineManager();
		defaultReservationPipelineManager.setHandlers(Collections.EMPTY_LIST);
	}

	@Test
	public void testExecutePipelineWithTransportAsOrderEntries()
	{
		given(abstractOrderModel.getEntries()).willReturn(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));
		given(abstractOrderEntryModel.getType()).willReturn(OrderEntryType.TRANSPORT);
		final ReservationData reservationData = defaultReservationPipelineManager.executePipeline(abstractOrderModel);
		Assert.assertNotNull(reservationData);
	}

	@Test
	public void testExecutePipelineWithNonTransportAsOrderEntries()
	{
		given(abstractOrderModel.getEntries()).willReturn(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));
		given(abstractOrderEntryModel.getType()).willReturn(OrderEntryType.ACCOMMODATION);
		final ReservationData reservationData = defaultReservationPipelineManager.executePipeline(abstractOrderModel);
		Assert.assertNull(reservationData);
	}

	@Test
	public void testExecutePipelineWithoutOrderEntries()
	{
		given(abstractOrderModel.getEntries()).willReturn(Collections.emptyList());
		final ReservationData reservationData = defaultReservationPipelineManager.executePipeline(abstractOrderModel);
		Assert.assertNull(reservationData);
	}

	@Test
	public void testExecutePipelineWithoutOrder()
	{
		final ReservationData reservationData = defaultReservationPipelineManager.executePipeline(null);
		Assert.assertNull(reservationData);
	}

	@Test
	public void testExecutePipelineWithHandlers()
	{
		given(abstractOrderModel.getEntries()).willReturn(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));
		given(abstractOrderEntryModel.getType()).willReturn(OrderEntryType.TRANSPORT);
		defaultReservationPipelineManager.setHandlers(Stream.of(new ReservationBasicHandler()).collect(Collectors.toList()));
		final ReservationData reservationData = defaultReservationPipelineManager.executePipeline(abstractOrderModel);
		Assert.assertNotNull(reservationData);
	}


}
