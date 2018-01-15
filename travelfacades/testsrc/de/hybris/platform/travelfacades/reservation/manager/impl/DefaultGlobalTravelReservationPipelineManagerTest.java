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

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.impl.GlobalTravelReservationBasicHandler;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.services.impl.DefaultBookingService;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGlobalTravelReservationPipelineManagerTest
{

	@InjectMocks
	private DefaultGlobalTravelReservationPipelineManager defaultGlobalTravelReservationPipelineManager;

	@Mock
	private AccommodationReservationPipelineManager accommodationReservationPipelineManager;

	private DefaultAccommodationReservationPipelineManager defaultAccommodationReservationPipelineManager;

	@Mock
	private ReservationPipelineManager reservationPipelineManager;

	private DefaultReservationPipelineManager defaultReservationPipelineManager;

	@Mock
	private AbstractOrderModel abstractOrderModel;

	@Mock
	private AbstractOrderEntryModel abstractOrderEntryModel;

	@Mock
	private AbstractOrderEntryModel transportAbstractOrderEntryModel;

	@Mock
	private AbstractOrderEntryModel accommodationAbstractOrderEntryModel;

	@Mock
	private DefaultBookingService bookingService;

	@Mock
	private GlobalTravelReservationBasicHandler globalTravelReservationBasicHandler;

	@Mock
	private Converter<UserModel, CustomerData> customerConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultReservationPipelineManager = new DefaultReservationPipelineManager();
		defaultReservationPipelineManager.setHandlers(Collections.EMPTY_LIST);
		defaultAccommodationReservationPipelineManager = new DefaultAccommodationReservationPipelineManager();
		defaultAccommodationReservationPipelineManager.setHandlers(Collections.EMPTY_LIST);
		defaultGlobalTravelReservationPipelineManager.setBookingService(bookingService);
		defaultGlobalTravelReservationPipelineManager.setHandlers(Collections.EMPTY_LIST);
		defaultGlobalTravelReservationPipelineManager.setReservationPipelineManager(defaultReservationPipelineManager);
		defaultGlobalTravelReservationPipelineManager
				.setAccommodationReservationPipelineManager(defaultAccommodationReservationPipelineManager);
	}

	@Test
	public void testExecutePipelineWithEmptyOrderEntries()
	{
		defaultGlobalTravelReservationPipelineManager.executePipeline(abstractOrderModel);
		given(abstractOrderModel.getEntries()).willReturn(Collections.emptyList());
		given(bookingService.checkIfAnyOrderEntryByType(Matchers.any(), Matchers.any())).willReturn(false);
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();
		Assert.assertNull(globalTravelReservationData.getReservationData());
		Assert.assertNull(globalTravelReservationData.getAccommodationReservationData());
	}

	@Test
	public void testExecutePipelineWithTransportOrderEntries()
	{
		given(abstractOrderModel.getEntries()).willReturn(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(true);
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.ACCOMMODATION)).willReturn(false);
		given(abstractOrderModel.getTransportationOrderStatus()).willReturn(OrderStatus.ACTIVE);
		given(abstractOrderEntryModel.getType()).willReturn(OrderEntryType.TRANSPORT);
		final GlobalTravelReservationData globalTravelReservationData = defaultGlobalTravelReservationPipelineManager
				.executePipeline(abstractOrderModel);
		Assert.assertNotNull(globalTravelReservationData.getReservationData());
		Assert.assertNull(globalTravelReservationData.getAccommodationReservationData());
	}

	@Test
	public void testExecutePipelineWithAccommodationOrderEntries()
	{
		given(abstractOrderModel.getEntries()).willReturn(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(false);
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.ACCOMMODATION)).willReturn(true);
		given(abstractOrderModel.getAccommodationOrderStatus()).willReturn(OrderStatus.ACTIVE);
		given(abstractOrderEntryModel.getType()).willReturn(OrderEntryType.ACCOMMODATION);
		final GlobalTravelReservationData globalTravelReservationData = defaultGlobalTravelReservationPipelineManager
				.executePipeline(abstractOrderModel);
		Assert.assertNull(globalTravelReservationData.getReservationData());
		Assert.assertNotNull(globalTravelReservationData.getAccommodationReservationData());
	}

	@Test
	public void testExecutePipelineWithBothEntries()
	{
		given(abstractOrderModel.getEntries())
				.willReturn(Arrays.asList(transportAbstractOrderEntryModel, accommodationAbstractOrderEntryModel));
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(true);
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.ACCOMMODATION)).willReturn(true);
		given(abstractOrderModel.getTransportationOrderStatus()).willReturn(OrderStatus.ACTIVE);
		given(abstractOrderModel.getAccommodationOrderStatus()).willReturn(OrderStatus.ACTIVE);
		given(transportAbstractOrderEntryModel.getType()).willReturn(OrderEntryType.TRANSPORT);
		given(accommodationAbstractOrderEntryModel.getType()).willReturn(OrderEntryType.ACCOMMODATION);
		final GlobalTravelReservationData globalTravelReservationData = defaultGlobalTravelReservationPipelineManager
				.executePipeline(abstractOrderModel);
		Assert.assertNotNull(globalTravelReservationData.getReservationData());
		Assert.assertNotNull(globalTravelReservationData.getAccommodationReservationData());
	}

	@Test
	public void testExecutePipelineWithCancelledOrder()
	{
		given(abstractOrderModel.getEntries())
				.willReturn(Arrays.asList(transportAbstractOrderEntryModel, accommodationAbstractOrderEntryModel));
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(true);
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.ACCOMMODATION)).willReturn(true);
		given(abstractOrderModel.getTransportationOrderStatus()).willReturn(OrderStatus.CANCELLED);
		given(abstractOrderModel.getAccommodationOrderStatus()).willReturn(OrderStatus.CANCELLED);
		given(transportAbstractOrderEntryModel.getType()).willReturn(OrderEntryType.TRANSPORT);
		given(accommodationAbstractOrderEntryModel.getType()).willReturn(OrderEntryType.ACCOMMODATION);

		given(bookingService.getLastActiveOrderForType(abstractOrderModel, OrderEntryType.TRANSPORT))
				.willReturn(abstractOrderModel);
		given(bookingService.getLastActiveOrderForType(abstractOrderModel, OrderEntryType.ACCOMMODATION))
				.willReturn(abstractOrderModel);

		final GlobalTravelReservationData globalTravelReservationData = defaultGlobalTravelReservationPipelineManager
				.executePipeline(abstractOrderModel);
		Assert.assertNotNull(globalTravelReservationData.getReservationData());
		Assert.assertNotNull(globalTravelReservationData.getAccommodationReservationData());
	}

	@Test
	public void testExecutePipelineWithHandlers()
	{
		given(abstractOrderModel.getEntries())
				.willReturn(Arrays.asList(transportAbstractOrderEntryModel, accommodationAbstractOrderEntryModel));
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(true);
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.ACCOMMODATION)).willReturn(true);
		given(abstractOrderModel.getTransportationOrderStatus()).willReturn(OrderStatus.ACTIVE);
		given(abstractOrderModel.getAccommodationOrderStatus()).willReturn(OrderStatus.ACTIVE);
		given(transportAbstractOrderEntryModel.getType()).willReturn(OrderEntryType.TRANSPORT);
		given(accommodationAbstractOrderEntryModel.getType()).willReturn(OrderEntryType.ACCOMMODATION);

		final GlobalTravelReservationBasicHandler globalTravelReservationBasicHandler = new GlobalTravelReservationBasicHandler()
		{
			@Override
			public void handle(final AbstractOrderModel abstractOrderModel,
					final GlobalTravelReservationData globalTravelReservationData)
			{
				return;
			}
		};

		defaultGlobalTravelReservationPipelineManager
				.setHandlers(Stream.of(globalTravelReservationBasicHandler).collect(Collectors.toList()));
		final GlobalTravelReservationData globalTravelReservationData = defaultGlobalTravelReservationPipelineManager
				.executePipeline(abstractOrderModel);

		Assert.assertNotNull(globalTravelReservationData.getReservationData());
		Assert.assertNotNull(globalTravelReservationData.getAccommodationReservationData());
	}

	@Test
	public void testExecutePipelineWithReservationData()
	{
		given(abstractOrderModel.getEntries())
				.willReturn(Arrays.asList(transportAbstractOrderEntryModel, accommodationAbstractOrderEntryModel));
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT)).willReturn(true);
		given(bookingService.checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.ACCOMMODATION)).willReturn(true);
		given(abstractOrderModel.getTransportationOrderStatus()).willReturn(OrderStatus.ACTIVE);
		given(abstractOrderModel.getAccommodationOrderStatus()).willReturn(OrderStatus.ACTIVE);
		given(transportAbstractOrderEntryModel.getType()).willReturn(OrderEntryType.TRANSPORT);
		given(accommodationAbstractOrderEntryModel.getType()).willReturn(OrderEntryType.ACCOMMODATION);

		final GlobalTravelReservationBasicHandler globalTravelReservationBasicHandler = new GlobalTravelReservationBasicHandler()
		{
			@Override
			public void handle(final AbstractOrderModel abstractOrderModel,
					final GlobalTravelReservationData globalTravelReservationData)
			{
				return;
			}
		};

		defaultGlobalTravelReservationPipelineManager
				.setHandlers(Stream.of(globalTravelReservationBasicHandler).collect(Collectors.toList()));
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();
		defaultGlobalTravelReservationPipelineManager.executePipeline(abstractOrderModel, globalTravelReservationData);

		Assert.assertNotNull(globalTravelReservationData.getReservationData());
		Assert.assertNotNull(globalTravelReservationData.getAccommodationReservationData());
	}

}
