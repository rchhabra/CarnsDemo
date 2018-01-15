package de.hybris.platform.travelfacades.search.solrfacetsearch.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.search.AccommodationOfferingSearchFacade;
import de.hybris.platform.travelservices.search.AccommodationOfferingSearchService;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of AccommodationOfferingSearchFacade
 *
 * @param <ITEM>
 * 		AccommodationOfferingDayRateData
 */
public class DefaultSolrAccommodationOfferingSearchFacade<ITEM extends AccommodationOfferingDayRateData>
		implements AccommodationOfferingSearchFacade<ITEM>
{

	private AccommodationOfferingSearchService<SolrSearchQueryData, SearchResultValueData, AccommodationOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>> accommodationOfferingSearchService;
	private Converter<AccommodationOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>,
			AccommodationOfferingSearchPageData<SearchStateData, ITEM>> accommodationOfferingSearchPageConverter;
	private Converter<SearchData, SolrSearchQueryData> solrTravelSearchQueryDecoder;
	private ThreadContextService threadContextService;

	@Override
	public AccommodationOfferingSearchPageData<SearchStateData, ITEM> accommodationOfferingSearch(final SearchData searchData)
	{
		return getThreadContextService().executeInContext(
				new ThreadContextService.Executor<AccommodationOfferingSearchPageData<SearchStateData, ITEM>, ThreadContextService.Nothing>()
				{

					@Override
					public AccommodationOfferingSearchPageData<SearchStateData, ITEM> execute()
					{
						return getAccommodationOfferingSearchPageConverter()
								.convert(getAccommodationOfferingSearchService().doSearch(decodeSearchData(searchData), null));
					}
				});
	}

	@Override
	public AccommodationOfferingSearchPageData<SearchStateData, ITEM> accommodationOfferingSearch(final SearchData searchData,
			final PageableData pageableData)
	{
		return getThreadContextService().executeInContext(
				new ThreadContextService.Executor<AccommodationOfferingSearchPageData<SearchStateData, ITEM>, ThreadContextService.Nothing>()
				{

					@Override
					public AccommodationOfferingSearchPageData<SearchStateData, ITEM> execute()
					{
						return getAccommodationOfferingSearchPageConverter()
								.convert(getAccommodationOfferingSearchService().doSearch(decodeSearchData(searchData), pageableData));
					}
				});
	}

	/**
	 * Decode search data solr search query data.
	 *
	 * @param searchData
	 * 		the search data
	 * @return the solr search query data
	 */
	protected SolrSearchQueryData decodeSearchData(final SearchData searchData)
	{
		return getSolrTravelSearchQueryDecoder().convert(searchData);
	}

	/**
	 * Gets accommodation offering search service.
	 *
	 * @return the accommodationOfferingSearchService
	 */
	protected AccommodationOfferingSearchService<SolrSearchQueryData, SearchResultValueData, AccommodationOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>> getAccommodationOfferingSearchService()
	{
		return accommodationOfferingSearchService;
	}

	/**
	 * Sets accommodation offering search service.
	 *
	 * @param accommodationOfferingSearchService
	 * 		the accommodation offering search service
	 */
	@Required
	public void setAccommodationOfferingSearchService(
			final AccommodationOfferingSearchService<SolrSearchQueryData, SearchResultValueData, AccommodationOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>> accommodationOfferingSearchService)
	{
		this.accommodationOfferingSearchService = accommodationOfferingSearchService;
	}

	/**
	 * Gets accommodation offering search page converter.
	 *
	 * @return the accommodationOfferingSearchPageConverter
	 */
	protected Converter<AccommodationOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>,
			AccommodationOfferingSearchPageData<SearchStateData, ITEM>> getAccommodationOfferingSearchPageConverter()
	{
		return accommodationOfferingSearchPageConverter;
	}

	/**
	 * Sets accommodation offering search page converter.
	 *
	 * @param accommodationOfferingSearchPageConverter
	 * 		the accommodation offering search page converter
	 */
	public void setAccommodationOfferingSearchPageConverter(
			final Converter<AccommodationOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>,
					AccommodationOfferingSearchPageData<SearchStateData, ITEM>> accommodationOfferingSearchPageConverter)
	{
		this.accommodationOfferingSearchPageConverter = accommodationOfferingSearchPageConverter;
	}

	/**
	 * Gets solr travel search query decoder.
	 *
	 * @return the solrTravelSearchQueryDecoder
	 */
	protected Converter<SearchData, SolrSearchQueryData> getSolrTravelSearchQueryDecoder()
	{
		return solrTravelSearchQueryDecoder;
	}

	/**
	 * Sets solr travel search query decoder.
	 *
	 * @param solrTravelSearchQueryDecoder
	 * 		the solr travel search query decoder
	 */
	@Required
	public void setSolrTravelSearchQueryDecoder(final Converter<SearchData, SolrSearchQueryData> solrTravelSearchQueryDecoder)
	{
		this.solrTravelSearchQueryDecoder = solrTravelSearchQueryDecoder;
	}

	/**
	 * Gets thread context service.
	 *
	 * @return the threadContextService
	 */
	protected ThreadContextService getThreadContextService()
	{
		return threadContextService;
	}

	/**
	 * Sets thread context service.
	 *
	 * @param threadContextService
	 * 		the thread context service
	 */
	@Required
	public void setThreadContextService(final ThreadContextService threadContextService)
	{
		this.threadContextService = threadContextService;
	}

}
