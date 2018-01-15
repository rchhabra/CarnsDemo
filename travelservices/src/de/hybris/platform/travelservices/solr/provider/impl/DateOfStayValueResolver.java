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
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Date;


/**
 * The type Date of stay value resolver.
 */
public class DateOfStayValueResolver extends AbstractDateBasedValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel model,
			final ValueResolverContext<Object, Object> valueResolverContext, final Date documentDate)
			throws FieldValueProviderException
	{
		if (documentDate != null)
		{
			document.addField(indexedProperty,
					TravelDateUtils.convertDateToStringDate(documentDate, TravelservicesConstants.DATE_PATTERN),
					valueResolverContext.getFieldQualifier());
			return;
		}

		final boolean isOptional = ValueProviderParameterUtils
				.getBoolean(indexedProperty, OPTIONAL_PARAM, OPTIONAL_PARAM_DEFAULT_VALUE);

		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}
	}
}
