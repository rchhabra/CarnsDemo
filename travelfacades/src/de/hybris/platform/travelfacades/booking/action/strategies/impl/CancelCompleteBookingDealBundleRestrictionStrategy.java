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

import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.travelfacades.booking.action.strategies.GlobalBookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.services.DealBundleTemplateService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData> for deal bundles. If the order is a deal, the cancel
 * booking will be enabled only if it is possible to cancel both transport and accommodation booking, while the corresponding
 * bookingActionData will be disabled.
 */
public class CancelCompleteBookingDealBundleRestrictionStrategy implements GlobalBookingActionEnabledEvaluatorStrategy
{

	private static final String ALTERNATIVE_MESSAGE = "booking.action.cancel.complete.booking.deal.bundle.alternative.message";

	private DealBundleTemplateService dealBundleTemplateService;

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList,
			final GlobalTravelReservationData globalReservationData,
			final BookingActionResponseData bookingActionResponse)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream().filter(BookingActionData::isEnabled)
				.collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		if (getDealBundleTemplateService().isDealBundleOrder(globalReservationData.getCode()))
		{
			final Optional<BookingActionData> cancelBookingOptional = CollectionUtils
					.emptyIfNull(bookingActionResponse.getBookingActions()).stream()
					.filter(bookingAction -> ActionTypeOption.CANCEL_TRANSPORT_BOOKING.equals(bookingAction.getActionType()))
					.findFirst();

			final Optional<AccommodationBookingActionData> cancelAccommodationBookingOptional = CollectionUtils
					.emptyIfNull(bookingActionResponse.getAccommodationBookingActions()).stream()
					.filter(bookingAction -> ActionTypeOption.CANCEL_ACCOMMODATION_BOOKING.equals(bookingAction.getActionType()))
					.findFirst();

			final boolean enabled = cancelBookingOptional.isPresent() && cancelAccommodationBookingOptional.isPresent() &&
					cancelBookingOptional.get().isEnabled() && cancelAccommodationBookingOptional.get().isEnabled();

			if (!enabled)
			{
				enabledBookingActions.forEach(bookingActionData ->
				{
					bookingActionData.setEnabled(Boolean.FALSE);
					bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
				});
			}

			cancelBookingOptional.ifPresent(bookingAction -> bookingAction.setEnabled(Boolean.FALSE));
			cancelAccommodationBookingOptional.ifPresent(bookingAction -> bookingAction.setEnabled(Boolean.FALSE));
		}
	}

	/**
	 * @return the dealBundleTemplateService
	 */
	protected DealBundleTemplateService getDealBundleTemplateService()
	{
		return dealBundleTemplateService;
	}

	/**
	 * @param dealBundleTemplateService
	 * 		the dealBundleTemplateService to set
	 */
	@Required
	public void setDealBundleTemplateService(final DealBundleTemplateService dealBundleTemplateService)
	{
		this.dealBundleTemplateService = dealBundleTemplateService;
	}
}
