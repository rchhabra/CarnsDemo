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

package de.hybris.platform.travelfacades.reservation.manager.impl;

import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.GlobalTravelReservationHandler;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete Pipeline Manager class that will return a {@link GlobalTravelReservationData} after executing a list of handlers
 * on the {@link AbstractOrderModel} given as input
 */
public class DefaultGlobalTravelReservationPipelineManager implements GlobalTravelReservationPipelineManager
{
	private ReservationPipelineManager reservationPipelineManager;

	private AccommodationReservationPipelineManager accommodationReservationPipelineManager;

	private List<GlobalTravelReservationHandler> handlers;

	private BookingService bookingService;


	@Override
	public GlobalTravelReservationData executePipeline(final AbstractOrderModel abstractOrderModel)
	{
		final GlobalTravelReservationData globalTravelReservationData = new GlobalTravelReservationData();
		populateReservation(abstractOrderModel, globalTravelReservationData);
		return globalTravelReservationData;
	}

	protected void populateReservation(final AbstractOrderModel abstractOrderModel,
			final GlobalTravelReservationData globalTravelReservationData)
	{
		if (getBookingService().checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.TRANSPORT))
		{
			AbstractOrderModel transportAbstractOrderModel = abstractOrderModel;
			if (OrderStatus.CANCELLED.equals(abstractOrderModel.getTransportationOrderStatus()))
			{
				transportAbstractOrderModel = getBookingService().getLastActiveOrderForType(abstractOrderModel,
						OrderEntryType.TRANSPORT);
			}
			globalTravelReservationData
					.setReservationData(getReservationPipelineManager().executePipeline(transportAbstractOrderModel));
		}
		if (Objects.nonNull(getAccommodationReservationPipelineManager())
				&& getBookingService().checkIfAnyOrderEntryByType(abstractOrderModel, OrderEntryType.ACCOMMODATION))
		{
			AbstractOrderModel accommodationAbstractOrderModel = abstractOrderModel;
			if (OrderStatus.CANCELLED.equals(abstractOrderModel.getAccommodationOrderStatus()))
			{
				accommodationAbstractOrderModel = getBookingService().getLastActiveOrderForType(abstractOrderModel,
						OrderEntryType.ACCOMMODATION);
			}
			globalTravelReservationData.setAccommodationReservationData(
					getAccommodationReservationPipelineManager().executePipeline(accommodationAbstractOrderModel));
		}

		for (final GlobalTravelReservationHandler handler : handlers)
		{
			handler.handle(abstractOrderModel, globalTravelReservationData);
		}
	}

	@Override
	public void executePipeline(final AbstractOrderModel abstractOrderModel,
			final GlobalTravelReservationData globalTravelReservationData)
	{
		populateReservation(abstractOrderModel, globalTravelReservationData);
	}

	/**
	 * @return the reservationPipelineManager
	 */
	protected ReservationPipelineManager getReservationPipelineManager()
	{
		return reservationPipelineManager;
	}

	/**
	 * @param reservationPipelineManager
	 *           the reservationPipelineManager to set
	 */
	@Required
	public void setReservationPipelineManager(final ReservationPipelineManager reservationPipelineManager)
	{
		this.reservationPipelineManager = reservationPipelineManager;
	}

	/**
	 * @return the accommodationReservationPipelineManager
	 */
	protected AccommodationReservationPipelineManager getAccommodationReservationPipelineManager()
	{
		return accommodationReservationPipelineManager;
	}

	/**
	 * @param accommodationReservationPipelineManager
	 *           the accommodationReservationPipelineManager to set
	 */
	@Required
	public void setAccommodationReservationPipelineManager(
			final AccommodationReservationPipelineManager accommodationReservationPipelineManager)
	{
		this.accommodationReservationPipelineManager = accommodationReservationPipelineManager;
	}

	/**
	 * @return the handlers
	 */
	protected List<GlobalTravelReservationHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * @param handlers
	 *           the handlers to set
	 */
	@Required
	public void setHandlers(final List<GlobalTravelReservationHandler> handlers)
	{
		this.handlers = handlers;
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}



}
