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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.strategies.TransportSuggestionsDisplayStrategy.LegSuggestionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTransportSuggestionsDisplayStrategyTest
{
	@InjectMocks
	DefaultTransportSuggestionsDisplayStrategy defaultTransportSuggestionsDisplayStrategy;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	ConfigurationService configurationService;

	@Test
	public void testCreateSuggestionsMapForOrigin()
	{
		final String searchText = "Lon";
		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<>();
		final TransportOfferingData transportOfferingData1 = new TransportOfferingData();
		final TravelSectorData sector = new TravelSectorData();
		final TransportFacilityData origin = new TransportFacilityData();
		origin.setCode("LTN");
		final LocationData location = new LocationData();
		location.setCode("LON");
		origin.setLocation(location);
		sector.setOrigin(origin);
		transportOfferingData1.setSector(sector);

		transportOfferingData1.setOriginLocationCountry("United Kingdom");
		transportOfferingData1.setOriginLocationCity("London");
		transportOfferingDataList.add(transportOfferingData1);

		final LegSuggestionType type = LegSuggestionType.ORIGIN;

		Mockito.when(configurationService.getConfiguration().getString(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("{city} - {country}");

		final Map<GlobalSuggestionData, List<GlobalSuggestionData>> mapToReturn = defaultTransportSuggestionsDisplayStrategy
				.createSuggestionsMap(searchText, transportOfferingDataList, type);
		Assert.assertTrue(MapUtils.isNotEmpty(mapToReturn));
		Assert.assertEquals("London - United Kingdom", mapToReturn.values().stream().findFirst().get().get(0).getName());
	}

	@Test
	public void testCreateSuggestionsMapForDestination()
	{
		final String searchText = "Lon";
		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<>();
		final TransportOfferingData transportOfferingData1 = new TransportOfferingData();
		final TravelSectorData sector = new TravelSectorData();
		final TransportFacilityData destination = new TransportFacilityData();
		destination.setCode("LTN");
		final LocationData location = new LocationData();
		location.setCode("LON");
		destination.setLocation(location);
		sector.setDestination(destination);
		transportOfferingData1.setSector(sector);

		transportOfferingData1.setDestinationLocationCountry("United Kingdom");
		transportOfferingData1.setDestinationLocationCity("London");

		transportOfferingData1.setOriginLocationCountry("France");
		transportOfferingData1.setOriginLocationCity("Paris");

		transportOfferingDataList.add(transportOfferingData1);

		final LegSuggestionType type = LegSuggestionType.DESTINATION;

		Mockito.when(configurationService.getConfiguration().getString(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("{city} - {country}");

		final Map<GlobalSuggestionData, List<GlobalSuggestionData>> mapToReturn = defaultTransportSuggestionsDisplayStrategy
				.createSuggestionsMap(searchText, transportOfferingDataList, type);
		Assert.assertTrue(MapUtils.isNotEmpty(mapToReturn));
		Assert.assertEquals("London - United Kingdom", mapToReturn.values().stream().findFirst().get().get(0).getName());
	}
}
