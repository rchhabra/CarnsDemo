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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Strategy that extends the
 * {@link de.hybris.platform.travelfacades.booking.action.strategies.impl.AbstractBookingActionStrategy}. The strategy
 * is used to create and populate the BookingActionDataList defined at a leg level.
 */
public class OriginDestinationRefLevelBookingActionStrategy extends AbstractBookingActionStrategy
{
	private Map<ActionTypeOption, List<String>> bookingActionTypeAltMessagesMap;

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ActionTypeOption actionType,
			final ReservationData reservationData)
	{
		for (final ReservationItemData reservationItemData : reservationData.getReservationItems())
		{
			final BookingActionData bookingActionData = new BookingActionData();
			bookingActionData.setActionType(actionType);
			bookingActionData.setOriginDestinationRefNumber(reservationItemData.getOriginDestinationRefNumber());
			bookingActionData.setAlternativeMessages(new ArrayList<>());
			bookingActionData.setEnabled(true);
			populateUrl(bookingActionData, reservationData);

			bookingActionDataList.add(bookingActionData);
		}
	}

	/**
	 * @return the bookingActionTypeAltMessagesMap
	 */
	protected Map<ActionTypeOption, List<String>> getBookingActionTypeAltMessagesMap()
	{
		return bookingActionTypeAltMessagesMap;
	}

	/**
	 * @param bookingActionTypeAltMessagesMap
	 *           the bookingActionTypeAltMessagesMap to set
	 */
	public void setBookingActionTypeAltMessagesMap(final Map<ActionTypeOption, List<String>> bookingActionTypeAltMessagesMap)
	{
		this.bookingActionTypeAltMessagesMap = bookingActionTypeAltMessagesMap;
	}

}
