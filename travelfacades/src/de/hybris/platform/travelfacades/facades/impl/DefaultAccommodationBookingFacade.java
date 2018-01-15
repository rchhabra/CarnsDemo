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
package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.travelfacades.facades.AccommodationBookingFacade;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of the {@link AccommodationBookingFacade}
 */
public class DefaultAccommodationBookingFacade implements AccommodationBookingFacade
{

	@Override
	public String getBookerEmailIDFromAccommodationReservationData(final GlobalTravelReservationData globalReservationData,
			final String lastName)
	{
		final List<GuestData> guestDataList = globalReservationData.getAccommodationReservationData().getRoomStays().stream()
				.flatMap(roomStay -> roomStay.getReservedGuests().stream()).collect(Collectors.toList());

		for (final GuestData guestData : guestDataList)
		{
			if (checkGuest(lastName, guestData))
			{
				return getCustomerUid(globalReservationData.getCustomerData());
			}
		}
		return null;
	}

	/**
	 * Check guest boolean.
	 *
	 * @param lastName
	 * 		the last name
	 * @param guestData
	 * 		the guest data
	 * @return the boolean
	 */
	protected boolean checkGuest(final String lastName, final GuestData guestData)
	{
		return StringUtils.containsIgnoreCase(guestData.getProfile().getLastName(), lastName);
	}

	/**
	 * Gets customer uid.
	 *
	 * @param customerData
	 * 		the customer data
	 * @return the customer uid
	 */
	protected String getCustomerUid(final CustomerData customerData)
	{
		final String uid = customerData.getUid();
		if (CustomerType.GUEST.equals(customerData.getType()))
		{
			return StringUtils.substringAfter(uid, "|");
		}
		return uid;
	}
}
