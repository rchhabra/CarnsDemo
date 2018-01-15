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

package de.hybris.platform.travelfacades.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.strategies.SuggestionsDisplayStrategy.SuggestionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultSuggestionsDisplayStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSuggestionsDisplayStrategyTest
{
	private static String FORMAT_CITY = "{city}";
	private static String FORMAT_COUNTRY = "{country}";
	private static String FORMAT_CODE = "{code}";
	private static String FORMAT_NAME = "{name}";

	private static String DEFAULT_HEADER_FORMAT = FORMAT_CITY + " - " + FORMAT_COUNTRY;
	private static String DEFAULT_OPTION_FORMAT = FORMAT_CITY + " - " + FORMAT_NAME + "(" + FORMAT_CODE + ")";

	private static String PROPERTY_FORMAT_HEADER = "suggestions.display.format.header";
	private static String PROPERTY_FORMAT_OPTION = "suggestions.display.format.option";

	@InjectMocks
	DefaultSuggestionsDisplayStrategy defaultSuggestionsDisplayStrategy;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@Before
	public void setUp() throws Exception
	{
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configurationService.getConfiguration().getString(PROPERTY_FORMAT_HEADER, DEFAULT_HEADER_FORMAT))
				.willReturn(DEFAULT_HEADER_FORMAT);
		given(configurationService.getConfiguration().getString(PROPERTY_FORMAT_OPTION, DEFAULT_OPTION_FORMAT))
				.willReturn(DEFAULT_OPTION_FORMAT);
	}

	@Test
	public void testSuggestionsDisplayForEmptyTOList()
	{
		final Map<String, Map<String, String>> results = defaultSuggestionsDisplayStrategy
				.createStructuredView(SuggestionType.ORIGIN, "Lon", Collections.emptyList());
		Assert.assertTrue(MapUtils.isEmpty(results));
	}

	@Test
	public void testSuggestionsDisplayForOriginWithNullSector()
	{
		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setOriginLocationCountry("UK");
		transportOfferingData.setOriginLocationCity("London");
		transportOfferingData.setSector(new TravelSectorData());

		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<TransportOfferingData>();
		transportOfferingDataList.add(transportOfferingData);

		final Map<String, Map<String, String>> results = defaultSuggestionsDisplayStrategy
				.createStructuredView(SuggestionType.ORIGIN, "Lon", transportOfferingDataList);
		Assert.assertTrue(MapUtils.isEmpty(results));
	}

	@Test
	public void testSuggestionsDisplayForOrigin()
	{
		final TransportFacilityData originTransportFacilityData = new TransportFacilityData();
		originTransportFacilityData.setCode("LTN");
		originTransportFacilityData.setName("Luton Airport");

		final TravelSectorData travelSectorData = new TravelSectorData();
		travelSectorData.setOrigin(originTransportFacilityData);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setOriginLocationCountry("UK");
		transportOfferingData.setOriginLocationCity("London");
		transportOfferingData.setSector(travelSectorData);

		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<TransportOfferingData>();
		transportOfferingDataList.add(transportOfferingData);
		transportOfferingDataList.add(transportOfferingData);

		final Map<String, Map<String, String>> results = defaultSuggestionsDisplayStrategy
				.createStructuredView(SuggestionType.ORIGIN, "Lon", transportOfferingDataList);
		Assert.assertNotNull(results);
		Assert.assertTrue(results.containsKey("London - UK"));
	}

	@Test
	public void testSuggestionsDisplayForDestination()
	{
		final TransportFacilityData destinationTransportFacilityData = new TransportFacilityData();
		destinationTransportFacilityData.setCode("LTN");
		destinationTransportFacilityData.setName("Luton Airport");

		final TravelSectorData travelSectorData = new TravelSectorData();
		travelSectorData.setDestination(destinationTransportFacilityData);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setDestinationLocationCountry("UK");
		transportOfferingData.setDestinationLocationCity("London");
		transportOfferingData.setSector(travelSectorData);

		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<TransportOfferingData>();
		transportOfferingDataList.add(transportOfferingData);

		final Map<String, Map<String, String>> results = defaultSuggestionsDisplayStrategy
				.createStructuredView(SuggestionType.DESTINATION, "Lon", transportOfferingDataList);
		Assert.assertNotNull(results);
		Assert.assertTrue(results.containsKey("London - UK"));
	}
}
