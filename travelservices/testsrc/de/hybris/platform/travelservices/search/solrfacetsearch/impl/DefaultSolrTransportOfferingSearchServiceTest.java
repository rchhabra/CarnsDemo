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

package de.hybris.platform.travelservices.search.solrfacetsearch.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSolrTransportOfferingSearchServiceTest
{
	@InjectMocks
	DefaultSolrTransportOfferingSearchService solrTransportOfferingSearchService;

	@Mock
	private Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> searchQueryPageableConverter;

	@Mock
	private Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter;

	@Mock
	private Converter<SolrSearchResponse, TransportOfferingSearchPageData> searchResponseConverter;

	@Test
	public void testDoSearch()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final PageableData pageableData = new PageableData();
		final SolrSearchRequest solrSearchRequest = new SolrSearchRequest();
		final SolrSearchResponse solrSearchResponse = new SolrSearchResponse();
		final TransportOfferingSearchPageData transportOfferingSearchPageData = new TransportOfferingSearchPageData();

		BDDMockito.given(searchQueryPageableConverter.convert(Matchers.any(SearchQueryPageableData.class)))
				.willReturn(solrSearchRequest);
		BDDMockito.given(searchRequestConverter.convert(solrSearchRequest)).willReturn(solrSearchResponse);
		BDDMockito.given(searchResponseConverter.convert(solrSearchResponse)).willReturn(transportOfferingSearchPageData);

		final TransportOfferingSearchPageData transportOfferingSearchPageDataAct = solrTransportOfferingSearchService
				.doSearch(searchQueryData, pageableData);
		Assert.assertEquals(transportOfferingSearchPageData, transportOfferingSearchPageDataAct);

	}
}
