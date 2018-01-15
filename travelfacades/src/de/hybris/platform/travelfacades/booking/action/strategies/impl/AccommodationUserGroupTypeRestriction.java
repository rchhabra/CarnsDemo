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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionEnabledEvaluatorStrategy;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

/**
 * Strategy to evaluate the enabled property of the List<AccommodationBookingActionData>, based on the gruups of the current
 * user type: the bookingAction is disabled if the user belong to at least one of the given Restricted User Groups Codes list.
 */
public class AccommodationUserGroupTypeRestriction implements AccommodationBookingActionEnabledEvaluatorStrategy
{
	private UserService userService;
	private List<String> restrictedUserGroupCodeList;

	private static final String ALTERNATIVE_MESSAGE = "booking.action.accommodation.user.group.alternative.message";

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

		boolean disabled = getUserService().getCurrentUser().getAllGroups().stream()
				.anyMatch(group -> restrictedUserGroupCodeList.contains(group.getUid()));

		if (disabled)
		{
			enabledBookingActions.forEach(bookingActionData -> {
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the restrictedUserGroupCodeList
	 */
	protected List<String> getRestrictedUserGroupCodeList()
	{
		return restrictedUserGroupCodeList;
	}

	/**
	 * @param restrictedUserGroupCodeList
	 *           the restrictedUserGroupCodeList to set
	 */
	@Required
	public void setRestrictedUserGroupCodeList(final List<String> restrictedUserGroupCodeList)
	{
		this.restrictedUserGroupCodeList = restrictedUserGroupCodeList;
	}
}
