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
 */

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Resolves the location chain of AccommodationOffering to put names in solr document as a list of values
 * (multi value indexed property)
 */
public class LocationNamesValueResolver extends AbstractValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel marketingRatePlanInfoModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		final List<String> locationChain = getLocationChain(marketingRatePlanInfoModel.getAccommodationOffering());

		if (CollectionUtils.isNotEmpty(locationChain))
		{
			document.addField(indexedProperty, locationChain, resolverContext.getFieldQualifier());
			return;
		}

		final boolean isOptional = ValueProviderParameterUtils
				.getBoolean(indexedProperty, OPTIONAL_PARAM, OPTIONAL_PARAM_DEFAULT_VALUE);
		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}
	}

	protected List<String> getLocationChain(final AccommodationOfferingModel accommodationOffering)
	{
		final List<String> locationChain = new ArrayList<>();
		LocationModel location = accommodationOffering.getLocation();
		if (location != null)
		{
			do
			{
				if (!(LocationType.PROPERTY.equals(location.getLocationType()) || LocationType.REGION
						.equals(location.getLocationType())))
				{
					locationChain.add(location.getName());
				}
				location = CollectionUtils.isNotEmpty(location.getSuperlocations()) ? location.getSuperlocations().get(0) : null;
			}
			while (location != null);
		}
		return locationChain;
	}
}
