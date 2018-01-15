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
package de.hybris.platform.travelfacades.search;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;


/**
 * Transport offering search facade interface. Used to retrieve transport offering of type {@link TransportOfferingData}
 * (or subclasses of).
 *
 * @param <ITEM>
 * 		The type of the transport offering result items
 */
public interface TransportOfferingSearchFacade<ITEM extends TransportOfferingData>
{

	/**
	 * Initiate a new search using simple query with travel search data.
	 *
	 * @param searchData
	 * 		the travel search data
	 * @return the search results
	 */
	TransportOfferingSearchPageData<SearchData, ITEM> transportOfferingSearch(SearchData searchData);

	/**
	 * Refine an exiting search. The query object allows more complex queries using facet selection. The SearchStateData
	 * must have been obtained from the results of a call to {@link #transportOfferingSearch(SearchData)}.
	 *
	 * @param searchData
	 * 		the travel search data
	 * @param pageableData
	 * 		the page to return
	 * @return the search results
	 */
	TransportOfferingSearchPageData<SearchData, ITEM> transportOfferingSearch(SearchData searchData,
			final PageableData pageableData);

}
