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
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Resolves the values of AccommodationInfos for given MarketingRatePlanInfo (multi-value property)
 */
public class AccommodationInfosValueResolver extends AbstractDateBasedValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel marketingRatePlanInfo,
			final ValueResolverContext<Object, Object> resolverContext, final Date documentDate) throws FieldValueProviderException
	{
		final List<AccommodationModel> accommodations = getAccommodations(marketingRatePlanInfo.getRatePlanConfig());

		if (CollectionUtils.isNotEmpty(accommodations))
		{
			final List<String> accommodationNames = accommodations.stream().map(AccommodationModel::getName)
					.collect(Collectors.toList());

			document.addField(indexedProperty, accommodationNames, resolverContext.getFieldQualifier());
			return;
		}

		final boolean isOptional = ValueProviderParameterUtils
				.getBoolean(indexedProperty, OPTIONAL_PARAM, OPTIONAL_PARAM_DEFAULT_VALUE);
		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}
	}

	protected List<AccommodationModel> getAccommodations(final Collection<RatePlanConfigModel> ratePlanConfigs)
	{
		final List<AccommodationModel> accommodationModels = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(ratePlanConfigs))
		{
			ratePlanConfigs.forEach(ratePlanConfigModel ->
			{
				for (int i = 0; i < ratePlanConfigModel.getQuantity(); i++)
				{
					accommodationModels.add(ratePlanConfigModel.getAccommodation());
				}
			});
		}
		return accommodationModels;
	}
}
