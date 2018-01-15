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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;
import de.hybris.platform.travelservices.enums.MealType;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * The type Board type field value resolver.
 */
public class BoardTypeFieldValueResolver extends AbstractValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	private EnumerationService enumerationService;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel marketingRatePlanInfoModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		final List<MealType> mealTypes = getAllMealTypes(marketingRatePlanInfoModel.getRatePlanConfig());

		if (CollectionUtils.isNotEmpty(mealTypes))
		{
			final List<String> mealTypeNames = mealTypes.stream()
					.map(mealType -> getEnumerationService().getEnumerationName(mealType)).collect(Collectors.toList());
			document.addField(indexedProperty, mealTypeNames, resolverContext.getFieldQualifier());
			return;
		}

		final boolean isOptional = ValueProviderParameterUtils.getBoolean(indexedProperty, OPTIONAL_PARAM,
				OPTIONAL_PARAM_DEFAULT_VALUE);
		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}
	}

	/**
	 * Gets all meal types.
	 *
	 * @param ratePlanConfigs
	 * 		the rate plan configs
	 *
	 * @return the all meal types
	 */
	protected List<MealType> getAllMealTypes(final Collection<RatePlanConfigModel> ratePlanConfigs)
	{
		if (CollectionUtils.isEmpty(ratePlanConfigs))
		{
			return Collections.emptyList();
		}

		final List<MealType> mealTypes = new ArrayList<>();
		final List<Collection<MealType>> mealTypeCollections = ratePlanConfigs.stream()
				.filter(ratePlanConfig -> (ratePlanConfig != null && ratePlanConfig.getRatePlan() != null))
				.map(ratePlanConfig -> ratePlanConfig.getRatePlan().getMealType()).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(mealTypeCollections))
		{
			mealTypeCollections.stream().filter(mealTypeCollection -> CollectionUtils.isNotEmpty(mealTypeCollection))
					.forEach(mealTypeCollection -> mealTypes.addAll(mealTypeCollection));
		}
		return mealTypes;
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 * 		the enumerationService to set
	 */
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
