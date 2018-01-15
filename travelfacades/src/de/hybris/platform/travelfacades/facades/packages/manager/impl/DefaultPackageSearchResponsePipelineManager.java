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

package de.hybris.platform.travelfacades.facades.packages.manager.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageSearchResponsePipelineManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default Implementation class for the {@link PackageSearchResponsePipelineManager}
 */
public class DefaultPackageSearchResponsePipelineManager implements PackageSearchResponsePipelineManager
{
	private List<AccommodationSearchHandler> handlers;

	@Override
	public PackageSearchResponseData executePipeline(final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final PackageSearchRequestData packageSearchRequestData)
	{
		final PackageSearchResponseData packageSearchResponseData = new PackageSearchResponseData();

		getHandlers().forEach(
				handler -> handler.handle(accommodationOfferingDayRates, packageSearchRequestData, packageSearchResponseData));

		return packageSearchResponseData;
	}

	/**
	 * @return the handlers
	 */
	protected List<AccommodationSearchHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * Sets the handlers.
	 *
	 * @param handlers
	 *           the handlers to set
	 */
	@Required
	public void setHandlers(final List<AccommodationSearchHandler> handlers)
	{
		this.handlers = handlers;
	}

}
