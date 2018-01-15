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
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Resolves the value for maximum number of children (children + infants) for given MarketingRatePlanInfo.
 * If extraGuest field is not on the model, default value is set to 1000.
 */
public class MaxChildrenCountValueResolver extends AbstractValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final int DEFAULT_MAX_CHILDREN_COUNT = 1000;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel marketingRatePlanInfoModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		final List<GuestOccupancyModel> extraGuests = marketingRatePlanInfoModel.getExtraGuests();
		if (CollectionUtils.isNotEmpty(extraGuests))
		{
			int childrenCount = 0;
			for (final GuestOccupancyModel extraGuest : extraGuests)
			{
				if (StringUtils.equalsIgnoreCase(extraGuest.getPassengerType().getCode(), TravelservicesConstants.PASSENGER_TYPE_CODE_CHILD) || StringUtils
						.equalsIgnoreCase(extraGuest.getPassengerType().getCode(), TravelservicesConstants.PASSENGER_TYPE_CODE_INFANT))
				{
					childrenCount += extraGuest.getQuantityMax();
				}
			}
			document.addField(indexedProperty, childrenCount, resolverContext.getFieldQualifier());
		}
		else
		{
			document.addField(indexedProperty, DEFAULT_MAX_CHILDREN_COUNT, resolverContext.getFieldQualifier());
		}
	}
}
