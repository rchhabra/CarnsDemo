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

package de.hybris.platform.travelfacades.reservation.manager.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.impl.AccommodationReservationBasicHandler;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
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
public class DefaultAccommodationReservationPipelineManagerTest
{
	private DefaultAccommodationReservationPipelineManager defaultAccommodationReservationPipelineManager;

	@Mock
	private AccommodationReservationPipelineManager accommodationReservationPipelineManager;

	@Mock
	private AbstractOrderModel abstractOrderModel;

	@Mock
	private AbstractOrderEntryModel abstractOrderEntryModel;

	@Mock
	private AccommodationReservationBasicHandler accommodationReservationBasicHandler;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultAccommodationReservationPipelineManager = new DefaultAccommodationReservationPipelineManager();
		defaultAccommodationReservationPipelineManager.setHandlers(Collections.EMPTY_LIST);
	}

	@Test
	public void testExecutePipelineWithNonAccommodationAsOrderEntries()
	{
		given(abstractOrderModel.getEntries()).willReturn(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));
		given(abstractOrderEntryModel.getType()).willReturn(OrderEntryType.TRANSPORT);
		final AccommodationReservationData reservationData = defaultAccommodationReservationPipelineManager
				.executePipeline(abstractOrderModel);
		Assert.assertNull(reservationData);
	}

	@Test
	public void testExecutePipelineWithAccommodationAsOrderEntries()
	{
		given(abstractOrderModel.getEntries()).willReturn(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));
		given(abstractOrderEntryModel.getType()).willReturn(OrderEntryType.ACCOMMODATION);
		final AccommodationReservationData reservationData = defaultAccommodationReservationPipelineManager
				.executePipeline(abstractOrderModel);
		Assert.assertNotNull(reservationData);
	}

	@Test
	public void testExecutePipelineWithoutOrderEntries()
	{
		given(abstractOrderModel.getEntries()).willReturn(Collections.emptyList());
		final AccommodationReservationData reservationData = defaultAccommodationReservationPipelineManager
				.executePipeline(abstractOrderModel);
		Assert.assertNull(reservationData);
	}

	@Test
	public void testExecutePipelineWithoutOrder()
	{
		final AccommodationReservationData reservationData = defaultAccommodationReservationPipelineManager.executePipeline(null);
		Assert.assertNull(reservationData);
	}

	@Test
	public void testExecutePipelineWithHandlers()
	{
		given(abstractOrderModel.getEntries()).willReturn(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));
		given(abstractOrderEntryModel.getType()).willReturn(OrderEntryType.ACCOMMODATION);
		defaultAccommodationReservationPipelineManager
				.setHandlers(Stream.of(new AccommodationReservationBasicHandler()).collect(Collectors.toList()));
		final AccommodationReservationData reservationData = defaultAccommodationReservationPipelineManager
				.executePipeline(abstractOrderModel);
		Assert.assertNotNull(reservationData);
	}

}
