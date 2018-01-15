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
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;


/**
 * Value provider for Guest Rating.
 */
public class GuestRatingValueResolver extends AbstractValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	@Override
	protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext, IndexedProperty indexedProperty,
			MarketingRatePlanInfoModel model, ValueResolverContext<Object, Object> resolverContext)
			throws FieldValueProviderException
	{
		if (model != null && model.getAccommodationOffering() != null)
		{
			final Double value = model.getAccommodationOffering().getAverageUserRating() == null ? 0d
					: model.getAccommodationOffering().getAverageUserRating();
			document.addField(indexedProperty, value);
		}
	}
}
