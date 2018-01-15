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

package de.hybris.platform.travelfacades.facades.accommodation.search.manager.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;
import de.hybris.platform.travelfacades.facades.accommodation.search.manager.AccommodationOfferingSearchPipelineManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AccommodationOfferingSearchPipelineManager}
 */
public class DefaultAccommodationOfferingSearchPipelineManager implements AccommodationOfferingSearchPipelineManager
{

	private List<AccommodationSearchHandler> handlers;

	@Override
	public AccommodationSearchResponseData executePipeline(
			final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final AccommodationSearchRequestData accommodationSearchRequest)
	{
		final AccommodationSearchResponseData accommodationSearchResponse = new AccommodationSearchResponseData();

		getHandlers().forEach(
				handler -> handler.handle(accommodationOfferingDayRates, accommodationSearchRequest, accommodationSearchResponse));

		return accommodationSearchResponse;
	}

	protected List<AccommodationSearchHandler> getHandlers()
	{
		return handlers;
	}

	@Required
	public void setHandlers(final List<AccommodationSearchHandler> handlers)
	{
		this.handlers = handlers;
	}


}
