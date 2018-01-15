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

package de.hybris.platform.travelrulesengine.rao.providers.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelrulesengine.rao.BookingRAO;
import de.hybris.platform.travelrulesengine.rao.CancelBookingRAO;
import de.hybris.platform.travelrulesengine.utils.TravelRuleUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation class for booking RAO provider {@link RAOProvider}
 */
public class DefaultBookingRAOProvider implements RAOProvider
{
	private static final String INCLUDE_CANCEL_BOOKING = "INCLUDE_CANCEL_BOOKING";
	private static final String EXPAND_BOOKING = "EXPAND_BOOKING";

	private Converter<OrderModel, BookingRAO> bookingRaoConverter;
	private Collection<String> defaultOptions;
	private TimeService timeService;

	@Override
	public Set<Object> expandFactModel(final Object modelFact)
	{
		return expandFactModel(modelFact, getDefaultOptions());
	}

	protected Set<Object> expandFactModel(final Object modelFact, final Collection<String> options)
	{
		return modelFact instanceof OrderModel ?
				expandRAO(createRAO((OrderModel) modelFact), options) :
				Collections.emptySet();
	}

	/**
	 * Converts OrderModel to BookingRAO
	 *
	 * @param source
	 * @return FareProductRAO
	 */
	protected BookingRAO createRAO(final OrderModel source)
	{
		return getBookingRaoConverter().convert(source);
	}

	/**
	 * Expands RAO to include both CancelBookingRAO and inclusive BookingRAO in facts
	 *
	 * @param bookingRao
	 * @param options
	 * @return set of facts
	 */
	protected Set<Object> expandRAO(final BookingRAO bookingRao, final Collection<String> options)
	{
		final Set<Object> facts = new LinkedHashSet<>();

		options.forEach(option -> {
			if (INCLUDE_CANCEL_BOOKING.equals(option))
			{
				final CancelBookingRAO cancelBookingRAO = new CancelBookingRAO();
				cancelBookingRAO.setBooking(bookingRao);
				final Date now = getTimeService().getCurrentTime();
				if (CollectionUtils.isNotEmpty(bookingRao.getReservationItems()))
				{
					cancelBookingRAO.setAdvanceCancellationDays((int) TravelRuleUtils.getDaysBetweenDates(now,
							bookingRao.getReservationItems().get(0).getItinerary().getTransportOfferings().get(0).getDepartureTime()));
					facts.add(cancelBookingRAO);
				}
			}
			if (EXPAND_BOOKING.equals(option))
			{
				facts.add(bookingRao);
			}
		});

		return facts;
	}

	/**
	 * @return the bookingRaoConverter
	 */
	protected Converter<OrderModel, BookingRAO> getBookingRaoConverter()
	{
		return bookingRaoConverter;
	}

	/**
	 * @param bookingRaoConverter the bookingRaoConverter to set
	 */
	public void setBookingRaoConverter(final Converter<OrderModel, BookingRAO> bookingRaoConverter)
	{
		this.bookingRaoConverter = bookingRaoConverter;
	}

	protected Collection<String> getDefaultOptions()
	{
		return defaultOptions;
	}

	@Required
	public void setDefaultOptions(final Collection<String> defaultOptions)
	{
		this.defaultOptions = defaultOptions;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
