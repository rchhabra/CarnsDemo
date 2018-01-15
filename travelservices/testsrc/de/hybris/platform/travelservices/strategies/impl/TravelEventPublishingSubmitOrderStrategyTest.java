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
 */
package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TravelEventPublishingSubmitOrderStrategyTest
{
	@InjectMocks
	TravelEventPublishingSubmitOrderStrategy travelEventPublishingSubmitOrderStrategy = new TravelEventPublishingSubmitOrderStrategy();

	@Mock
	EventService eventService;
	@Mock
	private SessionService sessionService;;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for {@link TravelEventPublishingSubmitOrderStrategy#submitOrder(OrderModel)} .
	 */
	@Test
	public void testSubmitOrder()
	{
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER)).thenReturn(1);
		final List<String> travellers = Arrays.asList("adult1", "adult2");
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRAVELLERS_TO_CHECK_IN)).thenReturn(travellers);

		final OrderModel order = new OrderModel();
		travelEventPublishingSubmitOrderStrategy.submitOrder(order);


		final Matcher<SubmitOrderEvent> matcher = new BaseMatcher<SubmitOrderEvent>()
		{

			@Override
			public boolean matches(final Object compareTo)
			{
				if (compareTo instanceof SubmitOrderEvent)
				{
					final SubmitOrderEvent event = (SubmitOrderEvent) compareTo;

					return (event.getOrder() == order && event.getOriginDestinationRefNumber() == 1
							&& event.getTravellers().equals(travellers));
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendText("Argument should be an SubmitOrderEvent for order= " + order);

			}
		};

		Mockito.verify(eventService).publishEvent(Mockito.argThat(matcher));
	}

	@Test
	public void testNullSessionOriginDestinationRefNumber()
	{
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER)).thenReturn(null);
		final List<String> travellers = Arrays.asList("adult1", "adult2");
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRAVELLERS_TO_CHECK_IN)).thenReturn(travellers);

		final OrderModel order = new OrderModel();
		travelEventPublishingSubmitOrderStrategy.submitOrder(order);


		final Matcher<SubmitOrderEvent> matcher = new BaseMatcher<SubmitOrderEvent>()
		{

			@Override
			public boolean matches(final Object compareTo)
			{
				if (compareTo instanceof SubmitOrderEvent)
				{
					final SubmitOrderEvent event = (SubmitOrderEvent) compareTo;

					return (event.getOrder() == order && event.getOriginDestinationRefNumber() == 0 && event.getTravellers() == null);
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendText("Argument should be an SubmitOrderEvent for order= " + order);

			}
		};

		Mockito.verify(eventService).publishEvent(Mockito.argThat(matcher));
	}

	@Test
	public void testNullSessionTravellersToCheckIn()
	{
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER)).thenReturn(1);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRAVELLERS_TO_CHECK_IN)).thenReturn(null);

		final OrderModel order = new OrderModel();
		travelEventPublishingSubmitOrderStrategy.submitOrder(order);


		final Matcher<SubmitOrderEvent> matcher = new BaseMatcher<SubmitOrderEvent>()
		{

			@Override
			public boolean matches(final Object compareTo)
			{
				if (compareTo instanceof SubmitOrderEvent)
				{
					final SubmitOrderEvent event = (SubmitOrderEvent) compareTo;

					return (event.getOrder() == order && event.getOriginDestinationRefNumber() == 0 && event.getTravellers() == null);
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendText("Argument should be an SubmitOrderEvent for order= " + order);

			}
		};

		Mockito.verify(eventService).publishEvent(Mockito.argThat(matcher));
	}

	@Test
	public void testEmptyTravellers()
	{
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER)).thenReturn(1);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRAVELLERS_TO_CHECK_IN))
				.thenReturn(Collections.emptyList());

		final OrderModel order = new OrderModel();
		travelEventPublishingSubmitOrderStrategy.submitOrder(order);


		final Matcher<SubmitOrderEvent> matcher = new BaseMatcher<SubmitOrderEvent>()
		{

			@Override
			public boolean matches(final Object compareTo)
			{
				if (compareTo instanceof SubmitOrderEvent)
				{
					final SubmitOrderEvent event = (SubmitOrderEvent) compareTo;

					return (event.getOrder() == order && event.getOriginDestinationRefNumber() == 0 && event.getTravellers() == null);
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendText("Argument should be an SubmitOrderEvent for order= " + order);

			}
		};

		Mockito.verify(eventService).publishEvent(Mockito.argThat(matcher));
	}

}
