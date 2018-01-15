/*
 *
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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.provider.Qualifier;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.utils.RatePlanUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Resolves the price value of the RoomProduct(s) plus the Taxes for given MarketingRatePlanInfo
 */
public class RoomRatePriceRangeValueResolver extends AbstractPriceValueResolver
{
	@Override
	protected Map<String, Double> loadQualifierData(final IndexerBatchContext batchContext,
			final Collection<IndexedProperty> indexedProperties, final MarketingRatePlanInfoModel marketingRatePlanInfo,
			final Qualifier qualifier, final Date documentDate)
	{
		final Collection<RatePlanConfigModel> ratePlanConfigs = marketingRatePlanInfo.getRatePlanConfig();
		final Map<String, Double> priceInformationMap = new HashMap<>(ratePlanConfigs.size());

		ratePlanConfigs.forEach(ratePlanConfigModel -> {
			final RoomRateProductModel roomRateProduct = RatePlanUtils.getRoomRateForRatePlan(ratePlanConfigModel.getRatePlan(),
					documentDate);
			if (roomRateProduct != null)
			{
				final List<PriceInformation> priceInformations = loadPriceInformations(indexedProperties, roomRateProduct);
				final Double roomRateBasePrice = getPriceValue(priceInformations);
				final Double roomRateTaxValue = getTaxes(roomRateProduct.getEurope1Taxes(), roomRateBasePrice);
				Double ratePlanConfigPrice = 0d;
				if (roomRateBasePrice != null && roomRateTaxValue != null)
				{
					ratePlanConfigPrice = (roomRateBasePrice + roomRateTaxValue) * ratePlanConfigModel.getQuantity();
				}
				priceInformationMap.put(ratePlanConfigModel.getCode(), ratePlanConfigPrice);
			}
		});

		return priceInformationMap;
	}

}
