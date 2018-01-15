/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.RateRangeData;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.AccommodationOfferingSearchResponseSortStrategy;



/**
 * The type Priced properties first sort strategy. This strategy will sort the list of {@link PropertyData} on the
 * {@link AccommodationSearchResponseData} to have all properties with {@link RateRangeData}, i.e. priced properties, first
 * followed by all properties without, i.e. not available properties.
 */
public class PricedPropertiesFirstSortStrategy implements AccommodationOfferingSearchResponseSortStrategy
{
	@Override
	public void sort(final AccommodationSearchResponseData response)
	{
		if (response == null || CollectionUtils.isEmpty(response.getProperties()))
		{
			return;
		}

		final List<PropertyData> sortedProperties = response.getProperties().stream().filter(propertyData
				-> propertyData.getRateRange() != null).collect(Collectors.toList());

		sortedProperties.addAll(response.getProperties().stream().filter(propertyData
				-> propertyData.getRateRange() == null).collect(Collectors.toList()));

		response.setProperties(sortedProperties);
	}
}
