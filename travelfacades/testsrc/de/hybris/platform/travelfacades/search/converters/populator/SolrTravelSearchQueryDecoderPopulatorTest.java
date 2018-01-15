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

package de.hybris.platform.travelfacades.search.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SolrTravelSearchQueryDecoderPopulatorTest
{
	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Test
	public void testPopulateWithNonEmptyQuery()
	{
		final SearchData searchData = new SearchData();
		final Map<String, String> filterTerms = new HashMap<>();
		filterTerms.put("orgin", "LGW");
		filterTerms.put("destination", "CDG");
		filterTerms.put("departureTime", "06/07/2016 07:35");
		filterTerms.put("cabin", "M");
		searchData.setFilterTerms(filterTerms);
		searchData.setSearchType("full");

		final String query = "locationCodes:LGW CDG:dateOfStay:06/07/2016:numberOfAdults:2";
		searchData.setQuery(query);

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		final SolrTravelSearchQueryDecoderPopulator solrTravelSearchQueryDecoderPopulator = new SolrTravelSearchQueryDecoderPopulator();
		solrTravelSearchQueryDecoderPopulator.setConfigurationService(configurationService);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getString(Matchers.anyString())).willReturn("locationCodes,numberOfAdults,dateOfStay,propertyCode");
		solrTravelSearchQueryDecoderPopulator.populate(searchData, solrSearchQueryData);
		assertEquals("full", solrSearchQueryData.getSearchType());
	}

	@Test
	public void testPopulateWithEmptyQuery()
	{
		final SearchData searchData = new SearchData();
		final Map<String, String> filterTerms = new HashMap<>();
		filterTerms.put("orgin", "LGW");
		filterTerms.put("destination", "CDG");
		filterTerms.put("departureTime", "06/07/2016 07:35");
		filterTerms.put("cabin", "M");
		searchData.setFilterTerms(filterTerms);
		searchData.setSearchType("full");

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		final SolrTravelSearchQueryDecoderPopulator solrTravelSearchQueryDecoderPopulator = new SolrTravelSearchQueryDecoderPopulator();
		solrTravelSearchQueryDecoderPopulator.setConfigurationService(configurationService);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getString(Matchers.anyString())).willReturn("locationCodes,numberOfAdults,dateOfStay,propertyCode");
		solrTravelSearchQueryDecoderPopulator.populate(searchData, solrSearchQueryData);
		assertEquals("full", solrSearchQueryData.getSearchType());
	}

	@Test
	public void testPopulateWithRequiedFacetsNotContainingAllTheTerms()
	{
		final SearchData searchData = new SearchData();
		final Map<String, String> filterTerms = new HashMap<>();
		filterTerms.put("orgin", "LGW");
		filterTerms.put("destination", "CDG");
		filterTerms.put("departureTime", "06/07/2016 07:35");
		filterTerms.put("cabin", "M");
		searchData.setFilterTerms(filterTerms);
		searchData.setSearchType("full");

		final String query = "locationCodes:LGW CDG:dateOfStay:06/07/2016:numberOfAdults:2:cabin:M";
		searchData.setQuery(query);

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		final SolrTravelSearchQueryDecoderPopulator solrTravelSearchQueryDecoderPopulator = new SolrTravelSearchQueryDecoderPopulator();
		solrTravelSearchQueryDecoderPopulator.setConfigurationService(configurationService);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getString(Matchers.anyString())).willReturn("locationCodes,numberOfAdults,dateOfStay,propertyCode");
		solrTravelSearchQueryDecoderPopulator.populate(searchData, solrSearchQueryData);
		assertEquals("full", solrSearchQueryData.getSearchType());
	}

}
