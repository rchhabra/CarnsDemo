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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete Implementation of the {@link AccommodationSearchHandler} class. Handler is responsible for setting the
 * PackageSearchResponseData from the session
 */
public class PackageSearchResponseSessionHandler implements AccommodationSearchHandler
{
	private static final String PACKAGE_SEARCH_RESPONSE = "packageSearchResponse";

	private SessionService sessionService;

	@Override
	public void handle(final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final AccommodationSearchRequestData accommodationSearchRequest,
			final AccommodationSearchResponseData accommodationSearchResponse)
	{
		final PackageSearchResponseData packageSearchResponseData = sessionService.getAttribute(PACKAGE_SEARCH_RESPONSE);
		if (Objects.nonNull(packageSearchResponseData))
		{
			accommodationSearchResponse.setCriterion(packageSearchResponseData.getCriterion());
			accommodationSearchResponse.setProperties(packageSearchResponseData.getProperties());
		}
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
