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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Resolves the Rate Plan Configs of MarketingRatePlanInfoModel to put names in solr document as a list of values (multi
 * value indexed property)
 */
public class RatePlanConfigsValueResolver extends AbstractValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;
	private static final String SEPARATOR = "|";

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel marketingRatePlanInfoModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		final List<String> ratePlanConfigChain = getRatePlanConfigChain(marketingRatePlanInfoModel);

		if (CollectionUtils.isNotEmpty(ratePlanConfigChain))
		{
			document.addField(indexedProperty, ratePlanConfigChain, resolverContext.getFieldQualifier());
			return;
		}

		final boolean isOptional = ValueProviderParameterUtils.getBoolean(indexedProperty, OPTIONAL_PARAM,
				OPTIONAL_PARAM_DEFAULT_VALUE);
		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}
	}

	protected List<String> getRatePlanConfigChain(final MarketingRatePlanInfoModel marketingRatePlanInfoModel)
	{
		if (CollectionUtils.isEmpty(marketingRatePlanInfoModel.getRatePlanConfig()))
		{
			return Collections.emptyList();
		}

		final List<String> ratePlanConfigChain = new ArrayList<>();

		marketingRatePlanInfoModel.getRatePlanConfig().forEach(ratePlanConfig -> {
			final String ratePlanCofigCode = ratePlanConfig.getRatePlan().getCode() + SEPARATOR
					+ ratePlanConfig.getAccommodation().getCode()
					+ SEPARATOR
					+ ratePlanConfig.getQuantity();
			ratePlanConfigChain.add(ratePlanCofigCode);
		});

		return ratePlanConfigChain;
	}
}
