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
package de.hybris.platform.travelfacades.facades.accommodation.manager.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationDetailsPipelineManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation class for the {@link AccommodationDetailsPipelineManager}. This pipeline manager will instantiate a
 * new {@link PropertyData} and will call a list of handlers to populate the property data with different information.
 */
public class DefaultAccommodationDetailsPipelineManager implements AccommodationDetailsPipelineManager
{
	private List<AccommodationDetailsHandler> handlers;

	@Override
	public AccommodationAvailabilityResponseData executePipeline(
			final AccommodationAvailabilityRequestData availabilityRequestData)
	{

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();

		for (final AccommodationDetailsHandler handler : getHandlers())
		{
			handler.handle(availabilityRequestData, accommodationAvailabilityResponseData);
		}

		return accommodationAvailabilityResponseData;
	}

	/**
	 * Gets handlers.
	 *
	 * @return the handlers
	 */
	protected List<AccommodationDetailsHandler> getHandlers()
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
	public void setHandlers(final List<AccommodationDetailsHandler> handlers)
	{
		this.handlers = handlers;
	}
}
