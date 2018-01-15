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

package de.hybris.platform.travelfacades.tripfinder.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.fare.search.strategies.LocationCodesResolvingStrategy;
import de.hybris.platform.travelfacades.search.TransportOfferingSearchFacade;
import de.hybris.platform.travelfacades.strategies.SuggestionsDisplayStrategy;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Junit Test Suite {link DefaultTripFinderFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTripFinderFacadeTest
{
	@InjectMocks
	private DefaultTripFinderFacade tripFinderFacade;

	@Mock
	private TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade;
	@Mock
	private SuggestionsDisplayStrategy suggestionsDisplayStrategy;
	@Mock
	private TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData;
	@Mock
	private EnumerationService enumerationService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;
	@Mock
	private Map<LocationType, LocationCodesResolvingStrategy> locationCodesResolvingStrategyMap;

	/**
	 * The method is used to test get destination location based on activity
	 */
	@Test
	public void testGetDestinationLocationByActivity()
	{
		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<TransportOfferingData>();
		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setCode("LGW_CDG");
		transportOffering.setDestinationLocationCity("PARIS");
		transportOfferingDataList.add(transportOffering);

		given(transportOfferingSearchFacade.transportOfferingSearch(Matchers.any(SearchData.class))).willReturn(searchPageData);

		given(searchPageData.getResults()).willReturn(transportOfferingDataList);

		final List<TransportOfferingData> locationList = tripFinderFacade.getDestinationLocations("britishMuseum", "LGW");
		Assert.assertEquals(1, locationList.size());
		Assert.assertEquals("PARIS", locationList.get(0).getDestinationLocationCity());
	}

	@Test
	public void testGetOriginLocationSuggestion()
	{
		final Map<String, String> optionData = new HashMap<String, String>();
		optionData.put("London - Luton Airport(LTN)", "LTN");

		final Map<String, Map<String, String>> structuredResult = new HashMap<String, Map<String, String>>();
		structuredResult.put("London - UK", optionData);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<TransportOfferingData>();
		transportOfferingDataList.add(transportOfferingData);

		given(suggestionsDisplayStrategy.createStructuredView(Matchers.any(), Matchers.anyString(),
				Matchers.anyListOf(TransportOfferingData.class))).willReturn(structuredResult);

		final Map.Entry<String, Map<String, String>> result = tripFinderFacade.getOriginLocationSuggestion("LTN",
				transportOfferingDataList);

		Assert.assertNotNull(result);
		Assert.assertEquals(result.getKey(), "London - UK");
	}

	@Test
	public void testGetDestinationLocationSuggestion()
	{
		final Map<String, String> optionData = new HashMap<String, String>();
		optionData.put("London - Luton Airport(LTN)", "LTN");

		final Map<String, Map<String, String>> structuredResult = new HashMap<String, Map<String, String>>();
		structuredResult.put("London - UK", optionData);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<TransportOfferingData>();
		transportOfferingDataList.add(transportOfferingData);

		given(suggestionsDisplayStrategy.createStructuredView(Matchers.any(), Matchers.anyString(),
				Matchers.anyListOf(TransportOfferingData.class))).willReturn(structuredResult);

		final Map<String, Map<String, String>> result = tripFinderFacade.getDestinationLocationsSuggestion("LTN",
				transportOfferingDataList);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.containsKey("London - UK"));
	}

	@Test
	public void testGetDestinationTransportOfferings()
	{
		final String locationTypeStr="CITY";
		final LocationType locationType=LocationType.CITY;
		Mockito.when(enumerationService.getEnumerationValue(LocationType.class, locationTypeStr)).thenReturn(locationType);
		final LocationCodesResolvingStrategy locationCodesResolvingStrategy = Mockito.mock(LocationCodesResolvingStrategy.class);
		Mockito.when(locationCodesResolvingStrategyMap.get(locationType)).thenReturn(locationCodesResolvingStrategy);

		final String locationCode="London";
		final List<String> transportOfferingCodes = Collections.singletonList("8323");
		Mockito.when(locationCodesResolvingStrategy.getLocationCodes(locationCode)).thenReturn(transportOfferingCodes);

		given(transportOfferingSearchFacade.transportOfferingSearch(Matchers.any(SearchData.class))).willReturn(searchPageData);
		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<TransportOfferingData>();
		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setCode("LGW_CDG");
		transportOffering.setDestinationLocationCity("PARIS");
		transportOfferingDataList.add(transportOffering);
		given(searchPageData.getResults()).willReturn(transportOfferingDataList);

		Assert.assertTrue(CollectionUtils
				.isNotEmpty(tripFinderFacade.getDestinationTransportOfferings(locationCode, locationTypeStr, "Beach")));

		Mockito.when(locationCodesResolvingStrategy.getLocationCodes(locationCode)).thenReturn(Collections.emptyList());
		Assert.assertTrue(
				CollectionUtils.isEmpty(tripFinderFacade.getDestinationTransportOfferings(locationCode, locationTypeStr, "Beach")));

		Mockito.when(locationCodesResolvingStrategyMap.get(locationType)).thenReturn(null);
		Mockito.when(configurationService.getConfiguration().getString(TravelfacadesConstants.DEFAULT_LOCATION_TYPE))
				.thenReturn("COUNTRY");
		Mockito.when(enumerationService.getEnumerationValue(LocationType.class, "COUNTRY")).thenReturn(LocationType.COUNTRY);
		Mockito.when(locationCodesResolvingStrategyMap.get(LocationType.COUNTRY)).thenReturn(locationCodesResolvingStrategy);
		Assert.assertTrue(
				CollectionUtils.isEmpty(tripFinderFacade.getDestinationTransportOfferings(locationCode, locationTypeStr, "Beach")));
	}
}
