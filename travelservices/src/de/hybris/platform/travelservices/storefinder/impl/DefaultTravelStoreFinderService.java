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

package de.hybris.platform.travelservices.storefinder.impl;

import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commerceservices.storefinder.impl.DefaultStoreFinderService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.dao.TravelPointOfServiceDao;
import de.hybris.platform.travelservices.storefinder.TravelStoreFinderService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;


/**
 * Default implementation of {@link TravelStoreFinderService}
 * 
 * @param <ITEM>
 *           travel distance to point of service
 */
public class DefaultTravelStoreFinderService<ITEM extends PointOfServiceDistanceData> extends DefaultStoreFinderService<ITEM>
		implements TravelStoreFinderService<ITEM, StoreFinderSearchPageData<ITEM>>
{
	private TravelPointOfServiceDao travelPointOfServiceDao;

	@Override
	public StoreFinderSearchPageData<ITEM> positionSearch(final BaseStoreModel baseStore, final GeoPoint geoPoint,
			final PageableData pageableData, final Collection<PointOfServiceModel> posResults)
	{
		final int resultRangeStart = pageableData.getCurrentPage() * pageableData.getPageSize();
		final int resultRangeEnd = (pageableData.getCurrentPage() + 1) * pageableData.getPageSize();

		if (posResults != null)
		{
			// Sort all the POS
			final List<ITEM> orderedResults = calculateDistances(geoPoint, posResults);
			final PaginationData paginationData = createPagination(pageableData, posResults.size());
			// Slice the required range window out of the results
			final List<ITEM> orderedResultsWindow = orderedResults
					.subList(Math.min(orderedResults.size(), resultRangeStart), Math.min(orderedResults.size(), resultRangeEnd));

			return createSearchResult(null, geoPoint, orderedResultsWindow, paginationData);
		}

		// Return no results
		return createSearchResult(null, geoPoint, Collections.<ITEM>emptyList(), createPagination(pageableData, 0));
	}

	@Override
	public Collection<PointOfServiceModel> getPointOfService(final BaseStoreModel baseStore,
			final List<TransportFacilityData> transportFacilityData)
	{
		Assert.notNull(baseStore);
		Assert.notNull(transportFacilityData);

		final Map<String, Object> filterParams = new HashMap<String, Object>();
		filterParams.put("baseStore", baseStore);
		filterParams.put("transportFacility", transportFacilityData);
		filterParams.put("type", PointOfServiceTypeEnum.STORE);

		return getTravelPointOfServiceDao().getPointOfService(filterParams);
	}

	/**
	 * Gets travel point of service dao.
	 *
	 * @return the travel point of service dao
	 */
	protected TravelPointOfServiceDao getTravelPointOfServiceDao()
	{
		return travelPointOfServiceDao;
	}

	/**
	 * Sets travel point of service dao.
	 *
	 * @param travelPointOfServiceDao
	 * 		the travel point of service dao
	 */
	public void setTravelPointOfServiceDao(final TravelPointOfServiceDao travelPointOfServiceDao)
	{
		this.travelPointOfServiceDao = travelPointOfServiceDao;
	}

}
