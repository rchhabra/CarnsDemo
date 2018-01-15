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

package de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;
import de.hybris.platform.travelfacades.facades.accommodation.search.manager.PropertyPipelineManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link AccommodationSearchHandler} interface. Handler is responsible to set the list
 * of {@link PropertyData} on the {@link AccommodationSearchResponseData}
 */
public class AccommodationPropertyResponseHandler implements AccommodationSearchHandler
{
	private PropertyPipelineManager propertyPipelineManager;

	@Override
	public void handle(final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates,
			final AccommodationSearchRequestData accommodationSearchRequest,
			final AccommodationSearchResponseData accommodationSearchResponse)
	{
		final List<PropertyData> properties = new ArrayList<>();

		final Map<String, List<AccommodationOfferingDayRateData>> dayRatesForAccommodatioOfferingMap =
				groupByAccommodationOfferingCode(
				accommodationOfferingDayRates);

		dayRatesForAccommodatioOfferingMap.entrySet()
				.forEach(entry -> properties.add(propertyPipelineManager.executePipeline(entry, accommodationSearchRequest)));

		accommodationSearchResponse.setProperties(properties);
	}

	/**
	 * Group by accommodation offering code map.
	 *
	 * @param accommodationOfferingDayRates
	 * 		the accommodation offering day rates
	 *
	 * @return the map
	 */
	protected Map<String, List<AccommodationOfferingDayRateData>> groupByAccommodationOfferingCode(
			final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates)
	{
		final Map<String, List<AccommodationOfferingDayRateData>> groupedResult = new LinkedHashMap<>();

		accommodationOfferingDayRates.forEach(dayRate ->
		{
			final String accommodationOfferingCode = dayRate.getAccommodationOfferingCode();
			if (groupedResult.containsKey(accommodationOfferingCode))
			{
				groupedResult.get(accommodationOfferingCode).add(dayRate);
			}
			else
			{
				final List<AccommodationOfferingDayRateData> dayRates = new ArrayList<>();
				dayRates.add(dayRate);
				groupedResult.put(accommodationOfferingCode, dayRates);
			}
		});

		return groupedResult;
	}

	/**
	 * Gets property pipeline manager.
	 *
	 * @return the property pipeline manager
	 */
	protected PropertyPipelineManager getPropertyPipelineManager()
	{
		return propertyPipelineManager;
	}

	/**
	 * Sets property pipeline manager.
	 *
	 * @param propertyPipelineManager
	 * 		the property pipeline manager
	 */
	@Required
	public void setPropertyPipelineManager(final PropertyPipelineManager propertyPipelineManager)
	{
		this.propertyPipelineManager = propertyPipelineManager;
	}



}
