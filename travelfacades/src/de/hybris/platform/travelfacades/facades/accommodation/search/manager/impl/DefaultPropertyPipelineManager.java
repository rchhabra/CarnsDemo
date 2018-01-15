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
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.PropertyHandler;
import de.hybris.platform.travelfacades.facades.accommodation.search.manager.PropertyPipelineManager;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PropertyPipelineManager}
 */
public class DefaultPropertyPipelineManager implements PropertyPipelineManager
{

	private List<PropertyHandler> handlers;

	@Override
	public PropertyData executePipeline(final Entry<String, List<AccommodationOfferingDayRateData>> propertyEntry,
			final AccommodationSearchRequestData accommodationSearchRequest)
	{
		final PropertyData propertyData = new PropertyData();
		final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate = groupByRefNumber(
				propertyEntry.getValue());
		getHandlers().forEach(handler -> handler.handle(dayRatesForRoomStayCandidate, accommodationSearchRequest, propertyData));
		return propertyData;
	}

	protected Map<Integer, List<AccommodationOfferingDayRateData>> groupByRefNumber(
			final List<AccommodationOfferingDayRateData> dayRatesForAccommodationOffering)
	{
		return dayRatesForAccommodationOffering.stream()
				.collect(Collectors.groupingBy(AccommodationOfferingDayRateData::getRoomStayCandidateRefNumber));
	}

	protected List<PropertyHandler> getHandlers()
	{
		return handlers;
	}

	@Required
	public void setHandlers(final List<PropertyHandler> handlers)
	{
		this.handlers = handlers;
	}


}
