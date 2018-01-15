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

package de.hybris.platform.travelfacades.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.BookingActionRequestData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.GlobalBookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.GlobalBookingActionStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.impl.AccommodationBookingLevelBookingActionStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.impl.AccommodationBookingStatusRestrictionStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.impl.BookingStatusRestrictionStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.impl.CheckInWindowRestrictionStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.impl.TravellerCheckInRestrictionStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.impl.TravellerLevelBookingActionStrategy;
import de.hybris.platform.travelfacades.facades.impl.DefaultActionFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultActionFacadeTest
{
	private DefaultActionFacade actionFacade;
	@Mock
	private BookingActionRequestData bookingActionRequest;
	@Mock
	private ReservationData reservationData;
	@Mock
	private AccommodationReservationData accommodationReservationData;
	@Mock
	GlobalTravelReservationData globalReservationData;

	@Mock
	private TravellerLevelBookingActionStrategy travellerLevelBookingActionStrategy;
	@Mock
	private BookingStatusRestrictionStrategy bookingStatusRestrictionStrategy;
	@Mock
	private TravellerCheckInRestrictionStrategy travellerCheckInRestrictionStrategy;
	@Mock
	private CheckInWindowRestrictionStrategy checkInWindowRestrictionStrategy;
	@Mock
	private Map<ActionTypeOption, AccommodationBookingActionStrategy> accommodationBookingActionStrategyMap;
	@Mock
	private Map<ActionTypeOption, List<AccommodationBookingActionEnabledEvaluatorStrategy>> accommodationBookingActionEnabledCalculationStrategiesMap;
	@Mock
	private Map<ActionTypeOption, GlobalBookingActionStrategy> globalBookingActionStrategyMap;
	@Mock
	private Map<ActionTypeOption, List<GlobalBookingActionEnabledEvaluatorStrategy>> globalBookingActionEnabledCalculationStrategiesMap;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		actionFacade = new DefaultActionFacade();

		final Map<ActionTypeOption, BookingActionStrategy> bookingActionStrategyMap = new HashMap<>();
		bookingActionStrategyMap.put(ActionTypeOption.CHECK_IN, travellerLevelBookingActionStrategy);

		actionFacade.setBookingActionStrategyMap(bookingActionStrategyMap);

		final Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> bookingActionEnabledCalculationStrategiesMap = new HashMap<>();

		final List<BookingActionEnabledEvaluatorStrategy> bookingActionEnabledEvaluatorStrategyList = new ArrayList<>();
		bookingActionEnabledEvaluatorStrategyList.add(bookingStatusRestrictionStrategy);
		bookingActionEnabledEvaluatorStrategyList.add(travellerCheckInRestrictionStrategy);
		bookingActionEnabledEvaluatorStrategyList.add(checkInWindowRestrictionStrategy);

		bookingActionEnabledCalculationStrategiesMap.put(ActionTypeOption.CHECK_IN, bookingActionEnabledEvaluatorStrategyList);

		actionFacade.setBookingActionEnabledCalculationStrategiesMap(bookingActionEnabledCalculationStrategiesMap);
		actionFacade.setAccommodationBookingActionStrategyMap(accommodationBookingActionStrategyMap);
		actionFacade.setAccommodationBookingActionEnabledCalculationStrategiesMap(
				accommodationBookingActionEnabledCalculationStrategiesMap);
		actionFacade.setGlobalBookingActionStrategyMap(globalBookingActionStrategyMap);
		actionFacade.setGlobalBookingActionEnabledCalculationStrategiesMap(globalBookingActionEnabledCalculationStrategiesMap);

	}

	@Test
	public void testGetBookingActionForCheckIn()
	{
		when(bookingActionRequest.getBookingReference()).thenReturn("test_reference");
		when(bookingActionRequest.getUserId()).thenReturn("test_userid");

		final List<ActionTypeOption> requestActions = new ArrayList<>();
		requestActions.add(ActionTypeOption.CHECK_IN);

		when(bookingActionRequest.getRequestActions()).thenReturn(requestActions);

		final BookingActionResponseData reposnseData = actionFacade.getBookingAction(bookingActionRequest, reservationData);
		Assert.assertNotNull(reposnseData);

	}

	@Test
	public void testGetAccommodationBookingAction()
	{
		final List<ActionTypeOption> requestActions = new ArrayList<>();
		requestActions.add(ActionTypeOption.CHECK_IN);

		final AccommodationBookingActionStrategy accommodationBookingLevelBookingActionStrategy = new AccommodationBookingLevelBookingActionStrategy();
		final AccommodationBookingActionStrategy accommodationBookingLevelBookingActionStrategySpy = Mockito
				.spy(accommodationBookingLevelBookingActionStrategy);
		final List<AccommodationBookingActionEnabledEvaluatorStrategy> strategies = new ArrayList<>();
		final AccommodationBookingStatusRestrictionStrategy accommodationBookingStatusRestrictionStrategy=new AccommodationBookingStatusRestrictionStrategy();
		strategies.add(accommodationBookingStatusRestrictionStrategy);

		when(bookingActionRequest.getRequestActions()).thenReturn(requestActions);
		when(accommodationBookingActionStrategyMap.get(ActionTypeOption.CHECK_IN))
				.thenReturn(accommodationBookingLevelBookingActionStrategySpy);
		when(accommodationBookingActionEnabledCalculationStrategiesMap.get(ActionTypeOption.CHECK_IN)).thenReturn(strategies);
		Mockito.doNothing().when(accommodationBookingLevelBookingActionStrategySpy).applyStrategy(Matchers.anyObject(),
				Matchers.anyObject(), Matchers.anyObject());

		final BookingActionResponseData reposnseData = actionFacade.getAccommodationBookingAction(bookingActionRequest,
				accommodationReservationData);
		Assert.assertNotNull(reposnseData);
	}

	@Test
	public void testGetTravelBookingAction()
	{
		when(globalReservationData.getReservationData()).thenReturn(reservationData);
		when(globalReservationData.getAccommodationReservationData()).thenReturn(accommodationReservationData);

		when(bookingActionRequest.getBookingReference()).thenReturn("test_reference");
		when(bookingActionRequest.getUserId()).thenReturn("test_userid");
		final List<ActionTypeOption> requestActions = new ArrayList<>();
		requestActions.add(ActionTypeOption.CHECK_IN);
		when(bookingActionRequest.getRequestActions()).thenReturn(requestActions);

		final AccommodationBookingActionStrategy accommodationBookingLevelBookingActionStrategy = new AccommodationBookingLevelBookingActionStrategy();
		final AccommodationBookingActionStrategy accommodationBookingLevelBookingActionStrategySpy = Mockito
				.spy(accommodationBookingLevelBookingActionStrategy);
		final List<AccommodationBookingActionEnabledEvaluatorStrategy> strategies = new ArrayList<>();
		final AccommodationBookingStatusRestrictionStrategy accommodationBookingStatusRestrictionStrategy = new AccommodationBookingStatusRestrictionStrategy();
		strategies.add(accommodationBookingStatusRestrictionStrategy);

		when(bookingActionRequest.getRequestActions()).thenReturn(requestActions);
		when(accommodationBookingActionStrategyMap.get(ActionTypeOption.CHECK_IN))
				.thenReturn(accommodationBookingLevelBookingActionStrategySpy);
		when(accommodationBookingActionEnabledCalculationStrategiesMap.get(ActionTypeOption.CHECK_IN)).thenReturn(strategies);
		Mockito.doNothing().when(accommodationBookingLevelBookingActionStrategySpy).applyStrategy(Matchers.anyObject(),
				Matchers.anyObject(), Matchers.anyObject());

		final GlobalBookingActionStrategy globalBookingActionStrategy = Mockito.mock(GlobalBookingActionStrategy.class);
		when(globalBookingActionStrategyMap.get(ActionTypeOption.CHECK_IN)).thenReturn(globalBookingActionStrategy);
		Mockito.doNothing().when(globalBookingActionStrategy).applyStrategy(Matchers.anyObject(), Matchers.anyObject(),
				Matchers.anyObject());
		final List<GlobalBookingActionEnabledEvaluatorStrategy> globalBookingActionEnabledEvaluatorStrategies = new ArrayList<>();
		final GlobalBookingActionEnabledEvaluatorStrategy globalBookingActionEnabledEvaluatorStrategy = Mockito
				.mock(GlobalBookingActionEnabledEvaluatorStrategy.class);
		globalBookingActionEnabledEvaluatorStrategies.add(globalBookingActionEnabledEvaluatorStrategy);
		when(globalBookingActionEnabledCalculationStrategiesMap.get(ActionTypeOption.CHECK_IN))
				.thenReturn(globalBookingActionEnabledEvaluatorStrategies);

		final BookingActionResponseData reposnseData = actionFacade.getTravelBookingAction(bookingActionRequest,
				bookingActionRequest, bookingActionRequest,
				globalReservationData);
		Assert.assertNotNull(reposnseData);
	}

}
