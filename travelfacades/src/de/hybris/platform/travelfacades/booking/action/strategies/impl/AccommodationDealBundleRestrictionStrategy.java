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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.travelfacades.booking.action.strategies.AccommodationBookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.services.DealBundleTemplateService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to evaluate the enabled property of the List<AccommodationBookingActionData> for deal bundles. If the order is a deal,
 * the enabled property of the {@link AccommodationBookingActionData} is set to false.
 */
public class AccommodationDealBundleRestrictionStrategy implements AccommodationBookingActionEnabledEvaluatorStrategy
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.accommodation.deal.bundle.alternative.message";

	private DealBundleTemplateService dealBundleTemplateService;

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

		if (getDealBundleTemplateService().isDealBundleOrder(accommodationReservationData.getCode()))
		{
			enabledBookingActions.forEach(bookingActionData ->
			{
				bookingActionData.setEnabled(Boolean.FALSE);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
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
