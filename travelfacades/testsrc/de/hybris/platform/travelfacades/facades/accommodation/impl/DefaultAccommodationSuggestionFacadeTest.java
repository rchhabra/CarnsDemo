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

package de.hybris.platform.travelfacades.facades.accommodation.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.accommodation.strategies.AccommodationSuggestionsDisplayStrategy;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.search.AccommodationOfferingSearchFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultAccommodationSuggestionFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationSuggestionFacadeTest
{
	@InjectMocks
	DefaultAccommodationSuggestionFacade defaultAccommodationSuggestionFacade;
	@Mock
	private AccommodationOfferingSearchFacade<AccommodationOfferingDayRateData> accommodationOfferingSearchFacade;
	@Mock
	private AccommodationSuggestionsDisplayStrategy accommodationSuggestionsDisplayStrategy;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;

	@Test
	public void testGetLocationSuggestions()
	{

		final GlobalSuggestionData globalSuggestionData = new GlobalSuggestionData();
		final List<GlobalSuggestionData> locationSuggestions = new ArrayList<GlobalSuggestionData>();
		locationSuggestions.add(globalSuggestionData);
		final AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData>
		accommodationOfferingSearchPageData=new AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData>();

		given(configurationService.getConfiguration().getInt(TravelfacadesConstants.ACCOMMODATION_SUGGESTIONS_PAGE_SIZE))
				.willReturn(0);
		given(configurationService.getConfiguration().getInt(TravelfacadesConstants.ACCOMMODATION_SUGGESTIONS_MAX_LOCATION_SIZE))
				.willReturn(10);
		given(accommodationOfferingSearchFacade.accommodationOfferingSearch(Matchers.any(SearchData.class),
				Matchers.any(PageableData.class))).willReturn(accommodationOfferingSearchPageData);
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationSuggestionFacade.getLocationSuggestions("Lon")));

		given(configurationService.getConfiguration().getInt(TravelfacadesConstants.ACCOMMODATION_SUGGESTIONS_PAGE_SIZE))
				.willReturn(5);
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationSuggestionFacade.getLocationSuggestions("Lon")));

		final AccommodationOfferingDayRateData accommodationOfferingDayRateData = new AccommodationOfferingDayRateData();
		accommodationOfferingSearchPageData.setResults(Collections.singletonList(accommodationOfferingDayRateData));

		given(accommodationSuggestionsDisplayStrategy.createGlobalSuggestionData("Lon",
				accommodationOfferingSearchPageData.getResults(),
				TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_LOCATION)).willReturn(locationSuggestions);
		given(accommodationSuggestionsDisplayStrategy.createGlobalSuggestionData("Lon",
				accommodationOfferingSearchPageData.getResults(),
				TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_PROPERTY)).willReturn(locationSuggestions);
		Assert.assertTrue(CollectionUtils.isNotEmpty(defaultAccommodationSuggestionFacade.getLocationSuggestions("Lon")));

	}

}
