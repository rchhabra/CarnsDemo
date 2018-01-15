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

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import org.springframework.beans.factory.annotation.Required;

/**
 * Concrete implementation of {@link AccommodationDetailsHandler} handling accommodation basic details
 */
public class AccommodationDetailsBasicHandler implements AccommodationDetailsHandler
{
	private AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> accommodationOfferingConverter;
	private AccommodationOfferingService accommodationOfferingService;

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final String accommodationOfferingCode = availabilityRequestData.getCriterion().getAccommodationReference()
				.getAccommodationOfferingCode();
		final AccommodationOfferingModel accommodationOfferingModel = getAccommodationOfferingService()
				.getAccommodationOffering(accommodationOfferingCode);
		final PropertyData property = getAccommodationOfferingConverter().convert(accommodationOfferingModel);
		accommodationAvailabilityResponseData.setAccommodationReference(property);
		final StayDateRangeData stayDateRange = availabilityRequestData.getCriterion().getStayDateRange();
		accommodationAvailabilityResponseData
				.setLengthOfStay((int) TravelDateUtils.getDaysBetweenDates(stayDateRange.getStartTime(), stayDateRange.getEndTime()));
	}

	protected AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> getAccommodationOfferingConverter()
	{
		return accommodationOfferingConverter;
	}

	@Required
	public void setAccommodationOfferingConverter(
			final AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> accommodationOfferingConverter)
	{
		this.accommodationOfferingConverter = accommodationOfferingConverter;
	}

	/**
	 * @return the accommodationOfferingService
	 */
	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	/**
	 * @param accommodationOfferingService
	 *           the accommodationOfferingService to set
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}

}
