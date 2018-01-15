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
import de.hybris.platform.commercefacades.travel.RemarkData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;


/**
 * Strategy that extends the
 * {@link de.hybris.platform.travelfacades.booking.action.strategies.impl.AbstractAccommodationBookingActionStrategy}.
 * The strategy is used to create and populate the BookingActionDataList defined at an accommodation request level.
 */
public class AccommodationRequestLevelBookingActionStrategy extends AbstractAccommodationBookingActionStrategy
{
	private static final String REQUEST_CODE_PLACEHOLDER = "\\{requestCode}";
	private static final String ROOMSTAY_NUMBER_PLACEHOLDER = "\\{roomStayRefNumber}";

	@Override
	public void applyStrategy(final List<AccommodationBookingActionData> bookingActionDataList, final ActionTypeOption actionType,
			final AccommodationReservationData accommodationReservationData)
	{
		accommodationReservationData.getRoomStays().forEach(roomStay -> {
			final SpecialRequestDetailData specialRequest = roomStay.getSpecialRequestDetail();
			if (Objects.nonNull(specialRequest) && !CollectionUtils.isEmpty(specialRequest.getRemarks()))
			{
				specialRequest.getRemarks().forEach(remarkData -> {
					final AccommodationBookingActionData bookingActionData = new AccommodationBookingActionData();
					bookingActionData.setActionType(actionType);
					bookingActionData.setAlternativeMessages(new ArrayList<>());
					bookingActionData.setEnabled(true);
					bookingActionData.setRequestCode(remarkData.getCode());
					bookingActionData.setRoomStayRefNumber(roomStay.getRoomStayRefNumber());
					populateUrl(bookingActionData, accommodationReservationData);
					replaceRequestPlaceholders(bookingActionData, remarkData, roomStay.getRoomStayRefNumber());

					bookingActionDataList.add(bookingActionData);
				});
			}
		});
	}

	protected void replaceRequestPlaceholders(final AccommodationBookingActionData bookingActionData, final RemarkData remarkData,
			final Integer roomStayRefNumber)
	{
		String url = bookingActionData.getActionUrl();
		url = url.replaceAll(REQUEST_CODE_PLACEHOLDER, remarkData.getCode());
		url = url.replaceAll(ROOMSTAY_NUMBER_PLACEHOLDER, roomStayRefNumber.toString());
		bookingActionData.setActionUrl(url);
	}

}
