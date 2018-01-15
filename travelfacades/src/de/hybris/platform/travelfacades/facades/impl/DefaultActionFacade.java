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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.commercefacades.travel.BookingActionData;
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
import de.hybris.platform.travelfacades.facades.ActionFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the {@link de.hybris.platform.travelfacades.facades.ActionFacade} interface.
 */
public class DefaultActionFacade implements ActionFacade
{

	private Map<ActionTypeOption, BookingActionStrategy> bookingActionStrategyMap;
	private Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> bookingActionEnabledCalculationStrategiesMap;
	private Map<ActionTypeOption, AccommodationBookingActionStrategy> accommodationBookingActionStrategyMap;
	private Map<ActionTypeOption, List<AccommodationBookingActionEnabledEvaluatorStrategy>> accommodationBookingActionEnabledCalculationStrategiesMap;
	private Map<ActionTypeOption, GlobalBookingActionStrategy> globalBookingActionStrategyMap;
	private Map<ActionTypeOption, List<GlobalBookingActionEnabledEvaluatorStrategy>> globalBookingActionEnabledCalculationStrategiesMap;

	@Override
	public BookingActionResponseData getBookingAction(final BookingActionRequestData bookingActionRequest,
			final ReservationData reservationData)
	{
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();

		bookingActionResponse.setBookingReference(bookingActionRequest.getBookingReference());
		bookingActionResponse.setUserId(bookingActionRequest.getUserId());

		final List<BookingActionData> bookingActions = getBookingActionList(bookingActionRequest, reservationData);

		bookingActionResponse.setBookingActions(bookingActions);
		return bookingActionResponse;
	}

	protected List<BookingActionData> getBookingActionList(final BookingActionRequestData bookingActionRequest,
			final ReservationData reservationData)
	{
		final List<BookingActionData> bookingActions = new ArrayList<>();
		for (final ActionTypeOption actionType : bookingActionRequest.getRequestActions())
		{
			final List<BookingActionData> bookingActionDataList = createBookingActionDataList(actionType, reservationData);

			// Strategies to evaluate the enabled value
			final List<BookingActionEnabledEvaluatorStrategy> strategies = getBookingActionEnabledCalculationStrategiesMap()
					.get(actionType);
			for (final BookingActionEnabledEvaluatorStrategy strategy : strategies)
			{
				strategy.applyStrategy(bookingActionDataList, reservationData);
			}

			bookingActions.addAll(bookingActionDataList);
		}
		return bookingActions;
	}

	protected List<BookingActionData> createBookingActionDataList(final ActionTypeOption actionType,
			final ReservationData reservationData)
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionStrategy strategy = getBookingActionStrategyMap().get(actionType);
		strategy.applyStrategy(bookingActionDataList, actionType, reservationData);
		return bookingActionDataList;
	}

	@Override
	public BookingActionResponseData getAccommodationBookingAction(final BookingActionRequestData bookingActionRequest,
			final AccommodationReservationData reservationData)
	{
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();

		bookingActionResponse.setBookingReference(bookingActionRequest.getBookingReference());
		bookingActionResponse.setUserId(bookingActionRequest.getUserId());

		final List<AccommodationBookingActionData> bookingActions = getAccommodationBookingActionList(bookingActionRequest,
				reservationData);
		bookingActionResponse.setAccommodationBookingActions(bookingActions);

		return bookingActionResponse;
	}

	protected List<AccommodationBookingActionData> getAccommodationBookingActionList(
			final BookingActionRequestData bookingActionRequest, final AccommodationReservationData accommodationReservationData)
	{
		final List<AccommodationBookingActionData> bookingActions = new ArrayList<>();
		for (final ActionTypeOption actionType : bookingActionRequest.getRequestActions())
		{
			final List<AccommodationBookingActionData> bookingActionDataList = createAccommodationBookingActionDataList(actionType,
					accommodationReservationData);

			// Strategies to evaluate the enabled value
			final List<AccommodationBookingActionEnabledEvaluatorStrategy> strategies = getAccommodationBookingActionEnabledCalculationStrategiesMap()
					.get(actionType);
			for (final AccommodationBookingActionEnabledEvaluatorStrategy strategy : strategies)
			{
				strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
			}

			bookingActions.addAll(bookingActionDataList);
		}
		return bookingActions;
	}

	protected List<AccommodationBookingActionData> createAccommodationBookingActionDataList(final ActionTypeOption actionType,
			final AccommodationReservationData reservationData)
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionStrategy strategy = getAccommodationBookingActionStrategyMap().get(actionType);
		strategy.applyStrategy(bookingActionDataList, actionType, reservationData);
		return bookingActionDataList;
	}

	@Override
	public BookingActionResponseData getTravelBookingAction(final BookingActionRequestData transportBookingActionRequest,
			final BookingActionRequestData accommodationBookingActionRequest,
			final BookingActionRequestData globalBookingActionRequest, final GlobalTravelReservationData globalReservationData)
	{
		final BookingActionResponseData bookingActionResponse = new BookingActionResponseData();

		bookingActionResponse.setBookingReference(transportBookingActionRequest.getBookingReference());
		bookingActionResponse.setUserId(transportBookingActionRequest.getUserId());

		if (globalReservationData.getReservationData() != null)
		{
			final List<BookingActionData> bookingActions = getBookingActionList(transportBookingActionRequest,
					globalReservationData.getReservationData());
			bookingActionResponse.setBookingActions(bookingActions);
		}

		if (globalReservationData.getAccommodationReservationData() != null)
		{
			final List<AccommodationBookingActionData> accommodationBookingActions = getAccommodationBookingActionList(
					accommodationBookingActionRequest, globalReservationData.getAccommodationReservationData());
			bookingActionResponse.setAccommodationBookingActions(accommodationBookingActions);
		}

		final List<BookingActionData> globalBookingActions = getGlobalActionList(globalBookingActionRequest, globalReservationData,
				bookingActionResponse);
		bookingActionResponse.setGlobalBookingActions(globalBookingActions);

		return bookingActionResponse;
	}

	protected List<BookingActionData> getGlobalActionList(final BookingActionRequestData globalBookingActionRequest,
			final GlobalTravelReservationData globalReservationData, final BookingActionResponseData bookingActionResponse)
	{
		final List<BookingActionData> bookingActions = new ArrayList<>();
		for (final ActionTypeOption actionType : globalBookingActionRequest.getRequestActions())
		{
			final List<BookingActionData> bookingActionDataList = createGlobalBookingActionDataList(actionType,
					globalReservationData);

			// Strategies to evaluate the enabled value
			final List<GlobalBookingActionEnabledEvaluatorStrategy> strategies = getGlobalBookingActionEnabledCalculationStrategiesMap()
					.get(actionType);
			for (final GlobalBookingActionEnabledEvaluatorStrategy strategy : strategies)
			{
				strategy.applyStrategy(bookingActionDataList, globalReservationData, bookingActionResponse);
			}

			bookingActions.addAll(bookingActionDataList);
		}
		return bookingActions;
	}

	protected List<BookingActionData> createGlobalBookingActionDataList(final ActionTypeOption actionType,
			final GlobalTravelReservationData globalReservationData)
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final GlobalBookingActionStrategy strategy = getGlobalBookingActionStrategyMap().get(actionType);
		strategy.applyStrategy(bookingActionDataList, actionType, globalReservationData);
		return bookingActionDataList;
	}

	/**
	 * @return the bookingActionStrategyMap
	 */
	protected Map<ActionTypeOption, BookingActionStrategy> getBookingActionStrategyMap()
	{
		return bookingActionStrategyMap;
	}

	/**
	 * @param bookingActionStrategyMap
	 *           the bookingActionStrategyMap to set
	 */
	public void setBookingActionStrategyMap(final Map<ActionTypeOption, BookingActionStrategy> bookingActionStrategyMap)
	{
		this.bookingActionStrategyMap = bookingActionStrategyMap;
	}

	/**
	 * @return the bookingActionEnabledCalculationStrategiesMap
	 */
	protected Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> getBookingActionEnabledCalculationStrategiesMap()
	{
		return bookingActionEnabledCalculationStrategiesMap;
	}

	/**
	 * @param bookingActionEnabledCalculationStrategiesMap
	 *           the bookingActionEnabledCalculationStrategiesMap to set
	 */
	public void setBookingActionEnabledCalculationStrategiesMap(
			final Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> bookingActionEnabledCalculationStrategiesMap)
	{
		this.bookingActionEnabledCalculationStrategiesMap = bookingActionEnabledCalculationStrategiesMap;
	}

	/**
	 *
	 * @return the accommodationBookingActionStrategyMap
	 */
	protected Map<ActionTypeOption, AccommodationBookingActionStrategy> getAccommodationBookingActionStrategyMap()
	{
		return accommodationBookingActionStrategyMap;
	}

	/**
	 *
	 * @param accommodationBookingActionStrategyMap
	 *           the accommodationBookingActionStrategyMap to set
	 */
	public void setAccommodationBookingActionStrategyMap(
			final Map<ActionTypeOption, AccommodationBookingActionStrategy> accommodationBookingActionStrategyMap)
	{
		this.accommodationBookingActionStrategyMap = accommodationBookingActionStrategyMap;
	}

	/**
	 *
	 * @return the accommodationBookingActionEnabledCalculationStrategiesMap
	 */
	protected Map<ActionTypeOption, List<AccommodationBookingActionEnabledEvaluatorStrategy>>
	getAccommodationBookingActionEnabledCalculationStrategiesMap()
	{
		return accommodationBookingActionEnabledCalculationStrategiesMap;
	}

	/**
	 *
	 * @param accommodationBookingActionEnabledCalculationStrategiesMap
	 *           the accommodationBookingActionEnabledCalculationStrategiesMap to set
	 */
	public void setAccommodationBookingActionEnabledCalculationStrategiesMap(
			final Map<ActionTypeOption, List<AccommodationBookingActionEnabledEvaluatorStrategy>> accommodationBookingActionEnabledCalculationStrategiesMap)
	{
		this.accommodationBookingActionEnabledCalculationStrategiesMap = accommodationBookingActionEnabledCalculationStrategiesMap;
	}

	/**
	 *
	 * @return the globalBookingActionStrategyMap
	 */
	protected Map<ActionTypeOption, GlobalBookingActionStrategy> getGlobalBookingActionStrategyMap()
	{
		return globalBookingActionStrategyMap;
	}

	/**
	 *
	 * @param globalBookingActionStrategyMap
	 *           the globalBookingActionStrategyMap to set
	 */
	public void setGlobalBookingActionStrategyMap(
			final Map<ActionTypeOption, GlobalBookingActionStrategy> globalBookingActionStrategyMap)
	{
		this.globalBookingActionStrategyMap = globalBookingActionStrategyMap;
	}

	/**
	 *
	 * @return the globalBookingActionEnabledCalculationStrategiesMap
	 */
	protected Map<ActionTypeOption, List<GlobalBookingActionEnabledEvaluatorStrategy>>
	getGlobalBookingActionEnabledCalculationStrategiesMap()
	{
		return globalBookingActionEnabledCalculationStrategiesMap;
	}

	/**
	 *
	 * @param globalBookingActionEnabledCalculationStrategiesMap
	 *           the globalBookingActionEnabledCalculationStrategiesMap to set
	 */
	public void setGlobalBookingActionEnabledCalculationStrategiesMap(
			final Map<ActionTypeOption, List<GlobalBookingActionEnabledEvaluatorStrategy>> globalBookingActionEnabledCalculationStrategiesMap)
	{
		this.globalBookingActionEnabledCalculationStrategiesMap = globalBookingActionEnabledCalculationStrategiesMap;
	}

}
