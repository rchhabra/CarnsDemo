/*
 *
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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Accommodation reservation property handler.
 */
public class AccommodationReservationPropertyHandler implements AccommodationReservationHandler
{
	private BookingService bookingService;

	private AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> accommodationOfferingConverter;

	@Override
	public void handle(final AbstractOrderModel abstractOrder, final AccommodationReservationData accommodationReservationData)
			throws AccommodationPipelineException
	{
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(abstractOrder);
		if (CollectionUtils.isEmpty(accommodationOrderEntryGroups))
		{
			throw new AccommodationPipelineException("No abstract order entry group in cart!");
		}
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = accommodationOrderEntryGroups.get(0);
		final AccommodationOfferingModel accommodationOffering = accommodationOrderEntryGroup.getAccommodationOffering();
		accommodationReservationData.setAccommodationReference(getAccommodationOfferingConverter().convert(accommodationOffering));
	}

	/**
	 * Gets accommodation offering converter.
	 *
	 * @return the accommodation offering converter
	 */
	protected AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> getAccommodationOfferingConverter()
	{
		return accommodationOfferingConverter;
	}

	/**
	 * Sets accommodation offering converter.
	 *
	 * @param accommodationOfferingConverter
	 * 		the accommodation offering converter
	 */
	@Required
	public void setAccommodationOfferingConverter(
			final AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> accommodationOfferingConverter)
	{
		this.accommodationOfferingConverter = accommodationOfferingConverter;
	}

	/**
	 * Gets booking service.
	 *
	 * @return the booking service
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * Sets booking service.
	 *
	 * @param bookingService
	 * 		the booking service
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}
}
