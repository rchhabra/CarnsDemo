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
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionEnabledEvaluatorStrategy;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<AccommodationBookingActionData>, based on the maximum number of request
 * allowed.
 */
public class MaxRequestNumberRestrictionStrategy implements AccommodationBookingActionEnabledEvaluatorStrategy
{

	private static final Integer DEFAULT_MAX_NUMBER = 5;
	private static final String MAX_REQUEST_NUMBER = "accommodation.max.request.number";
	private static final String ALTERNATIVE_MESSAGE = "booking.action.accommodation.max.request.number.reached";

	private ConfigurationService configurationService;

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

		accommodationReservationData.getRoomStays().forEach(roomStay -> {
			if (Objects.nonNull(roomStay.getSpecialRequestDetail())
					&& CollectionUtils.isNotEmpty(roomStay.getSpecialRequestDetail().getRemarks())
					&& CollectionUtils.size(roomStay.getSpecialRequestDetail().getRemarks()) >= getConfigurationService()
							.getConfiguration().getInt(MAX_REQUEST_NUMBER, DEFAULT_MAX_NUMBER))
			{
				enabledBookingActions.stream()
						.filter(bookingActionData -> roomStay.getRoomStayRefNumber().equals(bookingActionData.getRoomStayRefNumber()))
						.forEach(bookingActionData -> {
							bookingActionData.setEnabled(Boolean.FALSE);
							bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
						});
			}

		});
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
