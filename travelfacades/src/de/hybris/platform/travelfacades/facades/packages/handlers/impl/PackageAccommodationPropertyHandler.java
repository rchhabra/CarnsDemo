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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.AccommodationPropertyResponseHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.PackagePipelineManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * extension of the {@link AccommodationPropertyResponseHandler} class. Handler is responsible to set the list of
 * {@link PackageData} on the {@link AccommodationSearchResponseData}
 */
public class PackageAccommodationPropertyHandler extends AccommodationPropertyResponseHandler
{
	private PackagePipelineManager packagePipelineManager;

	@Override
	public void handle(final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final AccommodationSearchRequestData accommodationSearchRequest,
			final AccommodationSearchResponseData accommodationSearchResponse)
	{
		final List<PackageData> packages = new ArrayList<>();

		final Map<String, List<AccommodationOfferingDayRateData>> dayRatesForAccommodatioOfferingMap =
				groupByAccommodationOfferingCode(
				accommodationOfferingDayRates);

		for (final Map.Entry<String, List<AccommodationOfferingDayRateData>> entry : dayRatesForAccommodatioOfferingMap.entrySet())
		{
			packages.add(packagePipelineManager.executePipeline(entry, accommodationSearchRequest));
		}
		final List<PropertyData> properties = new ArrayList<>(packages);
		accommodationSearchResponse.setProperties(properties);
	}

	/**
	 * @return the packagePipelineManager
	 */
	protected PackagePipelineManager getPackagePipelineManager()
	{
		return packagePipelineManager;
	}

	/**
	 * @param packagePipelineManager
	 *           the packagePipelineManager to set
	 */
	@Required
	public void setPackagePipelineManager(final PackagePipelineManager packagePipelineManager)
	{
		this.packagePipelineManager = packagePipelineManager;
	}
}
