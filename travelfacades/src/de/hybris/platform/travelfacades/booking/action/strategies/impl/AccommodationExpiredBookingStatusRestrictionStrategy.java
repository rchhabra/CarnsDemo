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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionEnabledEvaluatorStrategy;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<AccommodationBookingActionData>, setting it to false if the
 * bookingStatusCode of the accommodationReservationData is included in the allowedStatuses list.
 */
public class AccommodationExpiredBookingStatusRestrictionStrategy implements AccommodationBookingActionEnabledEvaluatorStrategy
{

	private List<OrderStatus> allowedStatuses;

	private static final String ALTERNATIVE_MESSAGE = "booking.expired.action.status.alternative.message";

	@Override
	public void applyStrategy(final List<AccommodationBookingActionData> bookingActionDataList,
			final AccommodationReservationData accommodationReservationData)
	{
		final List<AccommodationBookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(AccommodationBookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		final boolean enabled = getAllowedStatuses().stream()
				.filter(status -> StringUtils.equalsIgnoreCase(status.getCode(), accommodationReservationData.getBookingStatusCode()))
				.findFirst().isPresent();

		if (!enabled)
		{
			enabledBookingActions.forEach(bookingActionData -> {
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}

	}

	/**
	 *
	 * @return the allowedStatuses
	 */
	protected List<OrderStatus> getAllowedStatuses()
	{
		return allowedStatuses;
	}

	/**
	 *
	 * @param allowedStatuses
	 *           the allowedStatuses to set
	 */
	@Required
	public void setAllowedStatuses(final List<OrderStatus> allowedStatuses)
	{
		this.allowedStatuses = allowedStatuses;
	}


}
