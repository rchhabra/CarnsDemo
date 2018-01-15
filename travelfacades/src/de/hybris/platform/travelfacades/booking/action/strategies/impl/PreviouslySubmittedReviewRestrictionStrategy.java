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
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.services.AccommodationOfferingCustomerReviewService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<AccommodationBookingActionData>. It will disabled the
 * accommodationBookingActionData if the review was already submitted.
 */
public class PreviouslySubmittedReviewRestrictionStrategy implements AccommodationBookingActionEnabledEvaluatorStrategy
{

	private AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService;

	private static final String ALTERNATIVE_MESSAGE = "booking.action.accommodation.review.already.submitted";
	private static final Logger LOG = Logger.getLogger(AccommodationOfferingCustomerReviewService.class);

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
		for (final ReservedRoomStayData roomStay : accommodationReservationData.getRoomStays())
		{
			try
			{
				final CustomerReviewModel review = getAccommodationOfferingCustomerReviewService()
						.retrieveCustomerReviewForRoomStay(accommodationReservationData.getCode(),
								accommodationReservationData.getAccommodationReference().getAccommodationOfferingCode(),
								roomStay.getRoomStayRefNumber());
				if (Objects.nonNull(review))
				{
					enabledBookingActions.stream()
							.filter(
									bookingActionData -> roomStay.getRoomStayRefNumber().equals(bookingActionData.getRoomStayRefNumber()))
							.forEach(bookingActionData -> {
								bookingActionData.setEnabled(Boolean.FALSE);
								bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
							});
				}
			}
			catch (final ModelNotFoundException e)
			{
				LOG.info(
						String.format("No review found for room stay %d belonging to booking no. %s. No restriction will be applied.",
								roomStay.getRoomStayRefNumber(), accommodationReservationData.getCode()));
				LOG.debug("Logging exception…", e);
				continue;
			}
		}


	}

	/**
	 *
	 * @return the accommodationOfferingCustomerReviewService
	 */
	protected AccommodationOfferingCustomerReviewService getAccommodationOfferingCustomerReviewService()
	{
		return accommodationOfferingCustomerReviewService;
	}

	/**
	 *
	 * @param accommodationOfferingCustomerReviewService
	 *           the accommodationOfferingCustomerReviewService to set
	 */
	@Required
	public void setAccommodationOfferingCustomerReviewService(
			final AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService)
	{
		this.accommodationOfferingCustomerReviewService = accommodationOfferingCustomerReviewService;
	}

}
