package de.hybris.platform.travelfacades.search;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;


/**
 * Accommodation offering search facade interface. Used to retrieve accommodation offering rate data of type
 * {@link AccommodationOfferingDayRateData} (or subclasses of).
 *
 * @param <ITEM>
 * 		the type of the accommodation offering result items
 */
public interface AccommodationOfferingSearchFacade<ITEM extends AccommodationOfferingDayRateData>
{
	/**
	 * Initiate a new search using simple query with accommodation search data.
	 *
	 * @param searchData
	 * 		the search data
	 * @return search results
	 */
	AccommodationOfferingSearchPageData<SearchStateData, ITEM> accommodationOfferingSearch(SearchData searchData);

	/**
	 * Refine an exiting search. The query object allows more complex queries using facet selection. The SearchStateData
	 * must have been obtained from the results of a call to {@link #accommodationOfferingSearch(SearchData)}.
	 *
	 * @param searchData
	 * 		the search data
	 * @param pageableData
	 * 		the pageable data
	 * @return search results
	 */
	AccommodationOfferingSearchPageData<SearchStateData, ITEM> accommodationOfferingSearch(SearchData searchData,
			final PageableData pageableData);
}
