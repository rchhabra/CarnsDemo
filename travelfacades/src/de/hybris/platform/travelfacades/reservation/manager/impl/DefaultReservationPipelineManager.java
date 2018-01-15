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
package de.hybris.platform.travelfacades.reservation.manager.impl;

import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete Pipeline Manager class that will return a {@link ReservationData} after executing a list of handlers
 * on the {@link AbstractOrderModel} given as input
 */
public class DefaultReservationPipelineManager implements ReservationPipelineManager
{

	private List<ReservationHandler> handlers;

	@Override
	public ReservationData executePipeline(final AbstractOrderModel abstractOrderModel)
	{
		if (!isTransportationBooking(abstractOrderModel))
		{
			return null;
		}

		final ReservationData reservationData = new ReservationData();

		for (final ReservationHandler handler :  handlers)
		{
			handler.handle(abstractOrderModel, reservationData);
		}

		if (CollectionUtils.isNotEmpty(reservationData.getReservationItems()))
		{
			reservationData.getReservationItems().sort(Comparator.comparing(ReservationItemData::getOriginDestinationRefNumber));
		}

		return reservationData;
	}

	/**
	 * Is transportation booking boolean.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 *
	 * @return the boolean
	 */
	protected boolean isTransportationBooking(final AbstractOrderModel abstractOrderModel)
	{
		return abstractOrderModel != null && abstractOrderModel.getEntries().stream()
				.anyMatch(abstractOrderEntry -> OrderEntryType.TRANSPORT.equals(abstractOrderEntry.getType()));
	}

	/**
	 * Gets handlers.
	 *
	 * @return the handlers
	 */
	protected List<ReservationHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * Sets handlers.
	 *
	 * @param handlers
	 * 		the handlers
	 */
	@Required
	public void setHandlers(final List<ReservationHandler> handlers)
	{
		this.handlers = handlers;
	}
}
