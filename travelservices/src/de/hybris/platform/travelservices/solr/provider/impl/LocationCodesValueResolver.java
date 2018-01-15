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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Resolves the location chain of AccommodationOffering to put codes in in the solr document in format:
 * Loc1|Loc2|Loc3...
 */
public class LocationCodesValueResolver extends AbstractValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel marketingRatePlanInfoModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		final String locationCodeChain = getLocationCodeChain(marketingRatePlanInfoModel.getAccommodationOffering());

		if (StringUtils.isNotEmpty(locationCodeChain))
		{
			document.addField(indexedProperty, locationCodeChain, resolverContext.getFieldQualifier());
			return;
		}

		final boolean isOptional = ValueProviderParameterUtils.getBoolean(indexedProperty, OPTIONAL_PARAM,
				OPTIONAL_PARAM_DEFAULT_VALUE);
		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}
	}

	protected String getLocationCodeChain(final AccommodationOfferingModel accommodationOffering)
	{
		final StringBuilder locationCodesChain = new StringBuilder();
		LocationModel location = accommodationOffering.getLocation();
		if (location != null)
		{
			int index = 0;

			do
			{
				if (!(LocationType.PROPERTY.equals(location.getLocationType())
						|| LocationType.REGION.equals(location.getLocationType())))
				{
					locationCodesChain.append(index == 0 ? location.getCode() : "|" + location.getCode());
					index++;
				}
				location = CollectionUtils.isNotEmpty(location.getSuperlocations()) ? location.getSuperlocations().get(0) : null;
			}
			while (location != null);
		}
		return locationCodesChain.toString();
	}
}
