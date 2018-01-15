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
package de.hybris.platform.travelfacades.search.solrfacetsearch.impl;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.search.TransportOfferingSearchFacade;
import de.hybris.platform.travelservices.search.TransportOfferingSearchService;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of TransportOfferingSearchFacade
 *
 * @param <ITEM>
 * 		the type parameter
 */
public class DefaultSolrTransportOfferingSearchFacade<ITEM extends TransportOfferingData>
		implements TransportOfferingSearchFacade<ITEM>
{

	private TransportOfferingSearchService<SolrSearchQueryData, SearchResultValueData, TransportOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>> transportOfferingSearchService;
	private Converter<TransportOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>, TransportOfferingSearchPageData<SearchData, ITEM>> transportOfferingSearchPageConverter;
	private Converter<SearchData, SolrSearchQueryData> solrTravelSearchQueryDecoder;
	private ThreadContextService threadContextService;

	@Override
	public TransportOfferingSearchPageData<SearchData, ITEM> transportOfferingSearch(final SearchData searchData)
	{
		return getThreadContextService().executeInContext(
				new ThreadContextService.Executor<TransportOfferingSearchPageData<SearchData, ITEM>, ThreadContextService.Nothing>()
				{

					@Override
					public TransportOfferingSearchPageData<SearchData, ITEM> execute()
					{
						return getTransportOfferingSearchPageConverter()
								.convert(getTransportOfferingSearchService().doSearch(decodeSearchData(searchData), null));
					}
				});
	}

	@Override
	public TransportOfferingSearchPageData<SearchData, ITEM> transportOfferingSearch(final SearchData searchData,
			final PageableData pageableData)
	{
		return getThreadContextService().executeInContext(
				new ThreadContextService.Executor<TransportOfferingSearchPageData<SearchData, ITEM>, ThreadContextService.Nothing>()
				{

					@Override
					public TransportOfferingSearchPageData<SearchData, ITEM> execute()
					{
						return getTransportOfferingSearchPageConverter()
								.convert(getTransportOfferingSearchService().doSearch(decodeSearchData(searchData), pageableData));
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
	 * Gets transport offering search service.
	 *
	 * @return the transport offering search service
	 */
	protected TransportOfferingSearchService<SolrSearchQueryData, SearchResultValueData, TransportOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>> getTransportOfferingSearchService()
	{
		return transportOfferingSearchService;
	}

	/**
	 * Sets transport offering search service.
	 *
	 * @param transportOfferingSearchService
	 * 		the transport offering search service
	 */
	@Required
	public void setTransportOfferingSearchService(
			final TransportOfferingSearchService<SolrSearchQueryData, SearchResultValueData, TransportOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>> transportOfferingSearchService)
	{
		this.transportOfferingSearchService = transportOfferingSearchService;
	}

	/**
	 * Gets transport offering search page converter.
	 *
	 * @return the transport offering search page converter
	 */
	protected Converter<TransportOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>, TransportOfferingSearchPageData<SearchData, ITEM>> getTransportOfferingSearchPageConverter()
	{
		return transportOfferingSearchPageConverter;
	}

	/**
	 * Sets transport offering search page converter.
	 *
	 * @param transportOfferingSearchPageConverter
	 * 		the transport offering search page converter
	 */
	@Required
	public void setTransportOfferingSearchPageConverter(
			final Converter<TransportOfferingSearchPageData<SolrSearchQueryData, SearchResultValueData>, TransportOfferingSearchPageData<SearchData, ITEM>> transportOfferingSearchPageConverter)
	{
		this.transportOfferingSearchPageConverter = transportOfferingSearchPageConverter;
	}

	/**
	 * Gets thread context service.
	 *
	 * @return the thread context service
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

	/**
	 * Gets solr travel search query decoder.
	 *
	 * @return Converter<SearchData SolrSearchQueryData>
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
	public void setSolrTravelSearchQueryDecoder(final Converter<SearchData, SolrSearchQueryData> solrTravelSearchQueryDecoder)
	{
		this.solrTravelSearchQueryDecoder = solrTravelSearchQueryDecoder;
	}

}
