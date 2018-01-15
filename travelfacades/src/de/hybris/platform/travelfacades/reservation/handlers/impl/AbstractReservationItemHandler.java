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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class for the Reservation Item Handler
 */
public class AbstractReservationItemHandler
{
	private Converter<TravelRouteModel, TravelRouteData> travelRouteConverter;
	private Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter;
	private TransportOfferingFacade transportOfferingFacade;


	/**
	 * Creates a list of reservation items - 1 for each leg of the journey
	 *
	 * @param fareProductEntries
	 *           the fare product entries
	 * @param reservationItems
	 *           the reservation items
	 */
	protected void createReservationItems(final List<AbstractOrderEntryModel> fareProductEntries,
			final List<ReservationItemData> reservationItems)
	{
		fareProductEntries.forEach(entry -> {
			if (CollectionUtils.isEmpty(reservationItems))
			{
				createNewReservationItem(reservationItems, entry);
			}
			else
			{
				if (!isReservationItemForLeg(reservationItems, entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()))
				{
					createNewReservationItem(reservationItems, entry);
				}
			}
		});
	}

	/**
	 * Checks whether the reservation item for given leg is already in the list
	 *
	 * @param reservationItems
	 *           the reservation items
	 * @param originDestinationRefNumber
	 *           the origin destination ref number
	 * @return boolean
	 */
	protected boolean isReservationItemForLeg(final List<ReservationItemData> reservationItems,
			final Integer originDestinationRefNumber)
	{
		for (final ReservationItemData reservationItem : reservationItems)
		{
			if (originDestinationRefNumber.equals(reservationItem.getOriginDestinationRefNumber()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a new reservation item for given leg
	 *
	 * @param reservationItems
	 *           the reservation items
	 * @param entry
	 *           the entry
	 */
	protected void createNewReservationItem(final List<ReservationItemData> reservationItems, final AbstractOrderEntryModel entry)
	{
		final ReservationItemData reservationItem = new ReservationItemData();
		reservationItem.setOriginDestinationRefNumber(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber());
		reservationItems.add(reservationItem);
	}

	/**
	 * Gets travel route converter.
	 *
	 * @return the travelRouteConverter
	 */
	protected Converter<TravelRouteModel, TravelRouteData> getTravelRouteConverter()
	{
		return travelRouteConverter;
	}

	/**
	 * Sets travel route converter.
	 *
	 * @param travelRouteConverter
	 *           the travelRouteConverter to set
	 */
	@Required
	public void setTravelRouteConverter(final Converter<TravelRouteModel, TravelRouteData> travelRouteConverter)
	{
		this.travelRouteConverter = travelRouteConverter;
	}

	/**
	 * Gets transport offering converter.
	 *
	 * @return the transportOfferingConverter
	 */
	protected Converter<TransportOfferingModel, TransportOfferingData> getTransportOfferingConverter()
	{
		return transportOfferingConverter;
	}

	/**
	 * Sets transport offering converter.
	 *
	 * @param transportOfferingConverter
	 *           the transportOfferingConverter to set
	 */
	@Required
	public void setTransportOfferingConverter(
			final Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter)
	{
		this.transportOfferingConverter = transportOfferingConverter;
	}

	/**
	 * Gets transport offering facade.
	 *
	 * @return the TransportOfferingFacade
	 */
	protected TransportOfferingFacade getTransportOfferingFacade()
	{
		return transportOfferingFacade;
	}

	/**
	 * Sets transport offering facade.
	 *
	 * @param transportOfferingFacade
	 *           the transportOfferingFacade to set
	 */
	@Required
	public void setTransportOfferingFacade(final TransportOfferingFacade transportOfferingFacade)
	{
		this.transportOfferingFacade = transportOfferingFacade;
	}

}
