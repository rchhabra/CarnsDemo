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
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.services.DealBundleTemplateService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Strategy to evaluate the enabled property of the List<BookingActionData> for deal bundles. If the order is a deal, the enabled
 * property of the {@link BookingActionData} is set to false.
 */
public class TransportDealBundleRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.transport.deal.bundle.alternative.message";

	private DealBundleTemplateService dealBundleTemplateService;

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		if (CollectionUtils.isEmpty(bookingActionDataList) || Objects.isNull(reservationData))
		{
			return;
		}

		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(BookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		if (getDealBundleTemplateService().isDealBundleOrder(reservationData.getCode()))
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
