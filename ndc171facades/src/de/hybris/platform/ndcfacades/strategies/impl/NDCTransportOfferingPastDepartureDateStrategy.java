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
package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.strategies.AmendOrderOfferFilterStrategy;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy that extends the {@link AmendOrderOfferFilterStrategy}.
 * The strategy is used to filter out the OrderItem if at least one of its transportOfferings is in
 * the past, that means that the departure date is before the current time.
 * If there are no transport offering related to the Order entry, true is returned.
 */
public class NDCTransportOfferingPastDepartureDateStrategy implements AmendOrderOfferFilterStrategy
{
	private TimeService timeService;
	private NDCTransportOfferingService ndcTransportOfferingService;

	@Override
	public boolean filterOffer(final OrderModel orderModel, final List<TransportOfferingModel> transportOfferings,
			final List<String> travellerUIDList)
	{
		return transportOfferings.stream().noneMatch(this::isTransportOfferingInThePast);
	}

	/**
	 * Return true if the {@link TransportOfferingModel} provided is in the past
	 *
	 * @param transportOffering the TransportOfferingModel
	 * @return the boolean
	 */
	protected boolean isTransportOfferingInThePast(final TransportOfferingModel transportOffering)
	{
		return TravelDateUtils.isBefore(transportOffering.getDepartureTime(),
				getNdcTransportOfferingService().getDepartureZonedDateTimeFromTransportOffering(transportOffering).getZone(),
				getTimeService().getCurrentTime(), ZoneId.systemDefault());
	}

	public TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected NDCTransportOfferingService getNdcTransportOfferingService()
	{
		return ndcTransportOfferingService;
	}

	@Required
	public void setNdcTransportOfferingService(final NDCTransportOfferingService ndcTransportOfferingService)
	{
		this.ndcTransportOfferingService = ndcTransportOfferingService;
	}
}
