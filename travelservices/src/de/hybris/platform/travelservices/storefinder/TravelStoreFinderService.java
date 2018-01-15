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

package de.hybris.platform.travelservices.storefinder;

import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Collection;
import java.util.List;


/**
 * The interface Travel store finder service.
 *
 * @param <ITEM>
 * 		the type parameter
 * @param <RESULT>
 * 		the type parameter
 */
public interface TravelStoreFinderService<ITEM extends PointOfServiceDistanceData, RESULT extends StoreFinderSearchPageData<ITEM>>
		extends StoreFinderService<ITEM, StoreFinderSearchPageData<ITEM>>
{
	/**
	 * Position search store finder search page data.
	 *
	 * @param baseStore
	 * 		the base store
	 * @param geoPoint
	 * 		the geo point
	 * @param pageableData
	 * 		the pageable data
	 * @param posResults
	 * 		the pos results
	 * @return the store finder search page data
	 */
	StoreFinderSearchPageData<ITEM> positionSearch(BaseStoreModel baseStore, GeoPoint geoPoint, PageableData pageableData,
			Collection<PointOfServiceModel> posResults);

	/**
	 * Gets point of service.
	 *
	 * @param baseStore
	 * 		the base store
	 * @param transportFacilityData
	 * 		the transport facility data
	 * @return the point of service
	 */
	Collection<PointOfServiceModel> getPointOfService(BaseStoreModel baseStore, List<TransportFacilityData> transportFacilityData);
}
