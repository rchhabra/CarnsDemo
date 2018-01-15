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

package de.hybris.platform.travelfacades.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingOption;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.facades.impl.DefaultTransportOfferingFacade;
import de.hybris.platform.travelfacades.fare.search.strategies.LocationCodesResolvingStrategy;
import de.hybris.platform.travelfacades.fare.search.strategies.impl.CityLocationCodesResolvingStrategy;
import de.hybris.platform.travelfacades.search.TransportOfferingSearchFacade;
import de.hybris.platform.travelfacades.strategies.SuggestionsDisplayStrategy;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link TransportOfferingFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTransportOfferingFacadeTest
{
	@Mock
	private TransportOfferingService transportOfferingService;
	@Mock
	private Converter<TravelRouteModel, TravelRouteData> travelRouteConverter;
	@Mock
	private Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter;
	@Mock
	private ConfigurablePopulator<TransportOfferingModel, TransportOfferingData, TransportOfferingOption> transportOfferingConfiguredPopulator;
	@Mock
	private TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade;
	@Mock
	private TravelRouteService travelRouteService;
	@Mock
	private SuggestionsDisplayStrategy suggestionsDisplayStrategy;
	@Mock
	private TimeService timeService;
	@Mock
	private ConfigurationService mockConfigurationService;

	@Mock
	private Configuration configuration;
	@Mock
	private TravelRulesService travelRulesService;

	private final Map<LocationType, LocationCodesResolvingStrategy> locationCodesResolvingStrategyMap = new HashMap<>();

	@Mock
	private CityLocationCodesResolvingStrategy cityLocationCodesResolvingStrategy;

	DefaultTransportOfferingFacade defaultTransportOfferingFacade;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultTransportOfferingFacade = new DefaultTransportOfferingFacade();
		defaultTransportOfferingFacade.setTransportOfferingService(transportOfferingService);
		defaultTransportOfferingFacade.setTravelRouteConverter(travelRouteConverter);
		defaultTransportOfferingFacade.setTransportOfferingConverter(transportOfferingConverter);
		defaultTransportOfferingFacade.setTransportOfferingConfiguredPopulator(transportOfferingConfiguredPopulator);
		defaultTransportOfferingFacade.setTransportOfferingSearchFacade(transportOfferingSearchFacade);
		defaultTransportOfferingFacade.setTravelRouteService(travelRouteService);
		defaultTransportOfferingFacade.setSuggestionsDisplayStrategy(suggestionsDisplayStrategy);
		defaultTransportOfferingFacade.setTimeService(timeService);
		defaultTransportOfferingFacade.setConfigurationService(mockConfigurationService);
		defaultTransportOfferingFacade.setTravelRulesService(travelRulesService);
		locationCodesResolvingStrategyMap.put(LocationType.CITY, cityLocationCodesResolvingStrategy);
		defaultTransportOfferingFacade.setLocationCodesResolvingStrategyMap(locationCodesResolvingStrategyMap);
	}

	@Test
	public void testGetTransportOfferings()
	{
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		given(transportOfferingService.getTransportOfferings(Matchers.anyString(), Matchers.any(Date.class))).willReturn(
				Arrays.asList(transportOfferingModel));

		final List<TransportOfferingData> results = defaultTransportOfferingFacade.getTransportOfferings(StringUtils.EMPTY,
				new Date(), Arrays.asList(TransportOfferingOption.TERMINAL));
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetScheduledRoutesWithDateValidationEnabledAndMultiSector()
	{
		final OriginDestinationInfoData originDestinationInfoData = new OriginDestinationInfoData();
		originDestinationInfoData.setDepartureTime(DateUtils.addHours(new Date(), 4));
		originDestinationInfoData.setDepartureLocationType(LocationType.CITY);
		originDestinationInfoData.setDepartureLocation("EDI");
		originDestinationInfoData.setArrivalLocationType(LocationType.CITY);
		originDestinationInfoData.setArrivalLocation("CDG");

		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setOriginDestinationInfo(Arrays.asList(originDestinationInfoData));

		final TravelRouteModel travelRouteModel = new TravelRouteModel();

		given(travelRouteService.getTravelRoutes(Matchers.anyString(), Matchers.anyString())).willReturn(
				Arrays.asList(travelRouteModel));

		TransportFacilityData originTransportFacility = new TransportFacilityData();
		originTransportFacility.setCode("EDI");
		TransportFacilityData destinationTransportFacility = new TransportFacilityData();
		destinationTransportFacility.setCode("LGW");
		final TravelSectorData travelSectorData1 = new TravelSectorData();
		travelSectorData1.setOrigin(originTransportFacility);
		travelSectorData1.setDestination(destinationTransportFacility);

		originTransportFacility = new TransportFacilityData();
		originTransportFacility.setCode("LGW");
		destinationTransportFacility = new TransportFacilityData();
		destinationTransportFacility.setCode("CDG");
		final TravelSectorData travelSectorData2 = new TravelSectorData();
		travelSectorData2.setOrigin(originTransportFacility);
		travelSectorData2.setDestination(destinationTransportFacility);

		final TravelRouteData travelRouteData = new TravelRouteData();
		travelRouteData.setSectors(Stream.of(travelSectorData1, travelSectorData2).collect(Collectors.toList()));

		given(travelRouteConverter.convert(travelRouteModel)).willReturn(travelRouteData);

		final TransportOfferingData transportOfferingData1 = new TransportOfferingData();
		transportOfferingData1.setSector(travelSectorData1);
		transportOfferingData1.setDepartureTime(DateUtils.addHours(new Date(), 4));
		transportOfferingData1.setDepartureTimeZoneId(ZoneId.systemDefault());
		transportOfferingData1.setArrivalTime(DateUtils.addHours(new Date(), 5));
		transportOfferingData1.setArrivalTimeZoneId(ZoneId.systemDefault());

		final TransportOfferingData transportOfferingData2 = new TransportOfferingData();
		transportOfferingData2.setSector(travelSectorData2);
		transportOfferingData2.setDepartureTime(DateUtils.addHours(new Date(), 7));
		transportOfferingData2.setDepartureTimeZoneId(ZoneId.systemDefault());
		transportOfferingData2.setArrivalTime(DateUtils.addHours(new Date(), 8));
		transportOfferingData2.setArrivalTimeZoneId(ZoneId.systemDefault());

		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = new TransportOfferingSearchPageData<SearchData, TransportOfferingData>();
		searchPageData.setResults(Stream.of(transportOfferingData1, transportOfferingData2).collect(Collectors.toList()));
		given(transportOfferingSearchFacade.transportOfferingSearch(Matchers.any(SearchData.class))).willReturn(searchPageData);

		given(timeService.getCurrentTime()).willReturn(new Date());
		given(mockConfigurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(true);
		given(cityLocationCodesResolvingStrategy.getLocationCodes("EDI"))
				.willReturn(Stream.of("EDI").collect(Collectors.toList()));
		given(cityLocationCodesResolvingStrategy.getLocationCodes("CDG")).willReturn(Stream.of("CDG").collect(Collectors.toList()));
		final List<ScheduledRouteData> scheduledRoutes = defaultTransportOfferingFacade.getScheduledRoutes(fareSearchRequestData);
		Assert.assertNotNull(scheduledRoutes);
		Assert.assertSame(scheduledRoutes.get(0).getTransportOfferings().get(0).getSector().getOrigin().getCode(), "EDI");
	}

	@Test
	public void testGetTransportOffering()
	{
		final String code = "EZY004";
		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode(code);

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();

		given(transportOfferingService.getTransportOffering(code)).willReturn(transportOfferingModel);
		given(transportOfferingConverter.convert(transportOfferingModel)).willReturn(transportOfferingData);

		final TransportOfferingData result = defaultTransportOfferingFacade.getTransportOffering(code);
		Assert.assertNotNull(result);
		Assert.assertSame(result.getCode(), code);
	}

	@Test
	public void testIsMultiSectorRouteForMultipleTransportOfferings()
	{
		final boolean result = defaultTransportOfferingFacade.isMultiSectorRoute(Stream.of("code1", "code2").collect(
				Collectors.toList()));
		Assert.assertTrue(result);
	}

	@Test
	public void testIsMultiSectorRouteForSingleTransportOffering()
	{
		final boolean result = defaultTransportOfferingFacade.isMultiSectorRoute(Stream.of("code1").collect(Collectors.toList()));
		Assert.assertFalse(result);
	}

	@Test
	public void testGetOriginSuggestions()
	{
		final TransportFacilityData originTransportFacility = new TransportFacilityData();
		originTransportFacility.setCode("EDI");
		final TransportFacilityData destinationTransportFacility = new TransportFacilityData();
		destinationTransportFacility.setCode("LGW");

		final TravelSectorData travelSectorData = new TravelSectorData();
		travelSectorData.setOrigin(originTransportFacility);
		travelSectorData.setDestination(destinationTransportFacility);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setSector(travelSectorData);
		transportOfferingData.setDepartureTime(DateUtils.addHours(new Date(), 4));
		transportOfferingData.setArrivalTime(DateUtils.addHours(new Date(), 5));

		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = new TransportOfferingSearchPageData<SearchData, TransportOfferingData>();
		searchPageData.setResults(Stream.of(transportOfferingData).collect(Collectors.toList()));
		given(transportOfferingSearchFacade.transportOfferingSearch(Matchers.any(SearchData.class))).willReturn(searchPageData);

		final Map<String, Map<String, String>> results = defaultTransportOfferingFacade.getOriginSuggestions("searchText");
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetDestinationSuggestions()
	{
		final TransportFacilityData originTransportFacility = new TransportFacilityData();
		originTransportFacility.setCode("EDI");
		final TransportFacilityData destinationTransportFacility = new TransportFacilityData();
		destinationTransportFacility.setCode("LGW");

		final TravelSectorData travelSectorData = new TravelSectorData();
		travelSectorData.setOrigin(originTransportFacility);
		travelSectorData.setDestination(destinationTransportFacility);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setSector(travelSectorData);
		transportOfferingData.setDepartureTime(DateUtils.addHours(new Date(), 4));
		transportOfferingData.setArrivalTime(DateUtils.addHours(new Date(), 5));

		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = new TransportOfferingSearchPageData<SearchData, TransportOfferingData>();
		searchPageData.setResults(Stream.of(transportOfferingData).collect(Collectors.toList()));
		given(transportOfferingSearchFacade.transportOfferingSearch(Matchers.any(SearchData.class))).willReturn(searchPageData);

		final Map<String, Map<String, String>> results = defaultTransportOfferingFacade.getDestinationSuggestions("EDI",
				"searchText");
		Assert.assertNotNull(results);
	}

}
