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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.services.BookingService;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultReservationFacadeTest
{
	private DefaultReservationFacade reservationFacade;
	@Mock
	private ReservationPipelineManager reservationPipelineManager;
	@Mock
	private ReservationPipelineManager reservationSummaryPipelineManager;
	@Mock
	private AccommodationReservationPipelineManager fullAccommodationReservationPipelineManager;
	@Mock
	private GlobalTravelReservationPipelineManager globalTravelReservationPipelineManager;

	@Mock
	private CartService cartService;

	@Mock
	private AbstractOrderModel abstractOrderModel;

	@Mock
	private CartModel cartModel;

	@Mock
	private BookingService bookingService;

	@Mock
	private ReservationPipelineManager transportReservationSummaryPipelineManager;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		reservationFacade = new DefaultReservationFacade();
		reservationFacade.setCartService(cartService);
		reservationFacade.setBookingService(bookingService);
		reservationFacade.setReservationPipelineManager(reservationPipelineManager);
		reservationFacade.setReservationSummaryPipelineManager(reservationSummaryPipelineManager);
		reservationFacade.setFullAccommodationReservationPipelineManager(fullAccommodationReservationPipelineManager);
		reservationFacade.setGlobalTravelReservationPipelineManager(globalTravelReservationPipelineManager);
		reservationFacade.setTransportReservationSummaryPipelineManager(transportReservationSummaryPipelineManager);
	}

	@Test
	public void testGetReservationData()
	{
		reservationFacade.getReservationData(abstractOrderModel);
		verify(reservationFacade.getReservationPipelineManager(), times(1)).executePipeline(abstractOrderModel);
	}

	@Test
	public void testGetReservationDataWhenOrderIsNull()
	{
		final ReservationData reservationData = reservationFacade.getReservationData(null);
		Assert.assertNull(reservationData);
	}

	@Test
	public void testGetBookingJourneyType()
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setBookingJourneyType(BookingJourneyType.BOOKING_PACKAGE);
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(orderModel);
		final String bookingJourneyType = reservationFacade.getBookingJourneyType("0001");
		Assert.assertEquals(bookingJourneyType, BookingJourneyType.BOOKING_PACKAGE.getCode());
	}

	@Test
	public void testRetrieveGlobalReservationDataForInvalidBookingReference()
	{
		when(bookingService.getOrderModelFromStore("0001")).thenReturn(null);
		final GlobalTravelReservationData globalTravelReservationData = reservationFacade.retrieveGlobalReservationData("0001");
		assertNull(globalTravelReservationData);
	}

	@Test
	public void testGetCurrentReservationData()
	{
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		reservationFacade.getCurrentReservationData();
		verify(reservationFacade.getReservationPipelineManager(), times(1)).executePipeline(cartModel);
	}

	@Test
	public void testGetCurrentReservationSummary()
	{
		when(cartService.getSessionCart()).thenReturn(null);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		final ReservationData reservationData = reservationFacade.getCurrentReservationSummary();
		Assert.assertNull(reservationData);

		final ReservationData reservationDataExp = new ReservationData();
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(transportReservationSummaryPipelineManager.executePipeline(cartModel)).thenReturn(reservationDataExp);
		final ReservationData reservationDataAct = reservationFacade.getCurrentReservationSummary();
		Assert.assertEquals(reservationDataExp, reservationDataAct);
	}

	@Test
	public void testGetAccommodationReservationData()
	{
		final AccommodationReservationData accommodationReservationData = reservationFacade.getAccommodationReservationData(null);
		Assert.assertNull(accommodationReservationData);

		final AccommodationReservationData accommodationReservationDataExp = new AccommodationReservationData();
		when(fullAccommodationReservationPipelineManager.executePipeline(cartModel)).thenReturn(accommodationReservationDataExp);
		final AccommodationReservationData accommodationReservationDataAct = reservationFacade
				.getAccommodationReservationData(cartModel);
		Assert.assertEquals(accommodationReservationDataExp, accommodationReservationDataAct);
	}

	@Test
	public void testGetCurrentAccommodationReservation()
	{
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		final AccommodationReservationData accommodationReservationDataExp = new AccommodationReservationData();
		when(fullAccommodationReservationPipelineManager.executePipeline(cartModel)).thenReturn(accommodationReservationDataExp);
		final AccommodationReservationData accommodationReservationDataAct = reservationFacade.getCurrentAccommodationReservation();
		Assert.assertEquals(accommodationReservationDataExp, accommodationReservationDataAct);
	}

	@Test
	public void testGetGlobalTravelReservationData()
	{
		final GlobalTravelReservationData globalTravelReservationData = reservationFacade.getGlobalTravelReservationData(null);
		Assert.assertNull(globalTravelReservationData);

		final GlobalTravelReservationData globalTravelReservationDataExp = new GlobalTravelReservationData();
		when(globalTravelReservationPipelineManager.executePipeline(cartModel)).thenReturn(globalTravelReservationDataExp);
		final GlobalTravelReservationData globalTravelReservationDataAct = reservationFacade
				.getGlobalTravelReservationData(cartModel);
		Assert.assertEquals(globalTravelReservationDataExp, globalTravelReservationDataAct);

	}

}
