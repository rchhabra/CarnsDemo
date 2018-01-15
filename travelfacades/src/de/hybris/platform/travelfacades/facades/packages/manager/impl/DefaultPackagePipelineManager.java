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
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.PropertyHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.PackagePipelineManager;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PackagePipelineManager}
 */
public class DefaultPackagePipelineManager implements PackagePipelineManager
{
	private List<PropertyHandler> handlers;

	@Override
	public PackageData executePipeline(final Entry<String, List<AccommodationOfferingDayRateData>> packageEntry,
			final AccommodationSearchRequestData accommodationSearchRequest)
	{
		final PackageData packageData = new PackageData();
		final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate = groupByRefNumber(
				packageEntry.getValue());
		getHandlers().forEach(handler -> handler.handle(dayRatesForRoomStayCandidate, accommodationSearchRequest, packageData));
		return packageData;
	}

	/**
	 * Group by ref number map.
	 *
	 * @param dayRatesForAccommodationOffering
	 * 		the day rates for accommodation offering
	 *
	 * @return the map
	 */
	protected Map<Integer, List<AccommodationOfferingDayRateData>> groupByRefNumber(
			final List<AccommodationOfferingDayRateData> dayRatesForAccommodationOffering)
	{
		return dayRatesForAccommodationOffering.stream()
				.collect(Collectors.groupingBy(AccommodationOfferingDayRateData::getRoomStayCandidateRefNumber));
	}

	/**
	 * Gets handlers.
	 *
	 * @return the handlers
	 */
	protected List<PropertyHandler> getHandlers()
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
	public void setHandlers(final List<PropertyHandler> handlers)
	{
		this.handlers = handlers;
	}


}
