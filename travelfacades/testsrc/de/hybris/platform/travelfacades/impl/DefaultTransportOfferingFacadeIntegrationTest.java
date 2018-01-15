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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.fare.search.utils.MockSolrTransportOfferingUtils;
import de.hybris.platform.travelfacades.search.TransportOfferingSearchFacade;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.TransportFacilityType;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit Test for the implementation of {@link TransportOfferingFacade}.
 */

@IntegrationTest
public class DefaultTransportOfferingFacadeIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	@InjectMocks
	private TransportOfferingFacade transportOfferingFacade;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "travelDroolsService")
	TravelRulesService travelDroolsService;

	@Mock
	private TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade;;

	final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();

	private static final String NO_SECTOR_ROUTE = "TR_000";
	private static final String SINGLE_SECTOR_ROUTE = "TR_001";
	private static final String MULTI_SECTOR_ROUTE = "JFK_LGW";
	private static final String INVALID_THIRD_SECTOR = "TR_003";
	private static final String NO_SOLR_TRANSPORT_OFFERINGS = "TR_004";

	private static final String LONDON_HEATHROW = "LHR";
	private static final String LONDON_GATWICK = "LGW";
	private static final String JOHN_F_KENNEDY = "JFK";
	private static final String CHARLE_DE_GAULLE = "CDG";

	private static final String LONDON_HEATHROW_NAME = "London Heathrow Airport";
	private static final String JOHN_F_KENNEDY_NAME = "John F. Kennedy International Airport";
	private static final String CHARLE_DE_GAULLE_NAME = "Charles de Gaulle Airport";


	@Before
	public void setup() throws ImpExException
	{
		MockitoAnnotations.initMocks(this);
		fareSearchRequestData.setOriginDestinationInfo(new ArrayList<OriginDestinationInfoData>());
		importCsv("/travelservices/test/testRuleEngine.csv", "utf-8");
	}

	@Test
	public void originSuggestionsTest() throws ParseException
	{
		createSingleSectorRoute(SINGLE_SECTOR_ROUTE);

		final String travelDate = MockSolrTransportOfferingUtils.getFutureTravelDate(1);
		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfo.setReferenceNumber(1);
		originDestinationInfo
				.setDepartureTime(TravelDateUtils.convertStringDateToDate(travelDate, TravelservicesConstants.DATE_PATTERN));
		originDestinationInfo.setDepartureLocation(LONDON_HEATHROW);
		originDestinationInfo.setArrivalLocation(CHARLE_DE_GAULLE);

		fareSearchRequestData.getOriginDestinationInfo().add(originDestinationInfo);

		// Setup mock Solr responses based on Sector
		Mockito.when(transportOfferingSearchFacade.transportOfferingSearch(Mockito.any(SearchData.class)))
				.thenReturn(getSolrTransportOfferings(MockSolrTransportOfferingUtils.getFutureTravelDateForTime(1, 18, 0, 0),
						LONDON_HEATHROW, CHARLE_DE_GAULLE));

		final Map<String, Map<String, String>> results = transportOfferingFacade.getOriginSuggestions("LHR");

		for (final String result : results.keySet())
		{
			for (final String value : results.get(result).keySet())
			{
				Assert.assertEquals("LHR", results.get(result).get(value));
			}
		}
	}

	@Test
	public void singleSectorRouteTest() throws ParseException
	{
		createSingleSectorRoute(SINGLE_SECTOR_ROUTE);

		final String departureDate = MockSolrTransportOfferingUtils.getFutureTravelDate(1);
		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfo.setReferenceNumber(1);
		originDestinationInfo
				.setDepartureTime(TravelDateUtils.convertStringDateToDate(departureDate, TravelservicesConstants.DATE_PATTERN));
		originDestinationInfo.setDepartureLocation(LONDON_HEATHROW);
		originDestinationInfo.setArrivalLocation(CHARLE_DE_GAULLE);

		fareSearchRequestData.getOriginDestinationInfo().add(originDestinationInfo);

		fareSearchRequestData.setPassengerTypes(createPassengerTypeQuantityData());

		// Setup mock Solr responses based on Sector

		final String travelDate = MockSolrTransportOfferingUtils.getFutureTravelDateForTime(1, 18, 0, 0);
		Mockito.when(transportOfferingSearchFacade.transportOfferingSearch(Mockito.any(SearchData.class)))
				.thenReturn(getSolrTransportOfferings(travelDate, LONDON_HEATHROW, CHARLE_DE_GAULLE));

		final List<ScheduledRouteData> scheduledRoutes = transportOfferingFacade.getScheduledRoutes(fareSearchRequestData);

		Assert.assertTrue(!scheduledRoutes.isEmpty());
		Assert.assertEquals(2, scheduledRoutes.size());

		scheduledRoutes.forEach(sr -> {

			Assert.assertEquals(originDestinationInfo.getReferenceNumber(), sr.getReferenceNumber());
			Assert.assertEquals(SINGLE_SECTOR_ROUTE, sr.getRoute().getCode());

			sr.getTransportOfferings().forEach(to -> {

				final TransportOfferingData transportOfferingData = MockSolrTransportOfferingUtils
						.getMockSolrTransportOfferingCode(to.getCode());

				Assert.assertNotNull(transportOfferingData);
				Assert.assertEquals(transportOfferingData.getCode(), to.getCode());

				Assert.assertNotNull(to.getSector());
				Assert.assertNotNull(to.getSector().getOrigin());
				Assert.assertEquals(transportOfferingData.getSector().getOrigin().getCode(), to.getSector().getOrigin().getCode());

				Assert.assertNotNull(to.getSector());
				Assert.assertNotNull(to.getSector().getDestination());
				Assert.assertEquals(transportOfferingData.getSector().getDestination().getCode(),
						to.getSector().getDestination().getCode());
			});

		});
	}

	private List<PassengerTypeQuantityData> createPassengerTypeQuantityData()
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final List<PassengerTypeData> passengerTypes = passengerTypeFacade.getPassengerTypes();

		for (final PassengerTypeData passengerTypeData : passengerTypes)
		{
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(passengerTypeData);
			passengerTypeQuantityData.setQuantity(2);
			passengerTypeQuantityList.add(passengerTypeQuantityData);
		}

		return passengerTypeQuantityList;
	}

	@Test
	public void multiSectorRouteWithNextDayFlightTest() throws ParseException
	{
		final String departureDate = MockSolrTransportOfferingUtils.getFutureTravelDate(1);
		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfo.setReferenceNumber(2);
		originDestinationInfo
				.setDepartureTime(TravelDateUtils.convertStringDateToDate(departureDate, TravelservicesConstants.DATE_PATTERN));
		originDestinationInfo.setDepartureLocation(JOHN_F_KENNEDY);
		originDestinationInfo.setArrivalLocation(LONDON_GATWICK);

		fareSearchRequestData.getOriginDestinationInfo().add(originDestinationInfo);

		fareSearchRequestData.setPassengerTypes(createPassengerTypeQuantityData());

		final List<TransportOfferingData> transportOfferings = new ArrayList<TransportOfferingData>();
		transportOfferings.addAll(MockSolrTransportOfferingUtils.getMockSolrTransportOfferingByDepartureDate(
				MockSolrTransportOfferingUtils.getFutureTravelDateForTime(1, 1, 0, 0), JOHN_F_KENNEDY, CHARLE_DE_GAULLE));
		transportOfferings.addAll(MockSolrTransportOfferingUtils.getMockSolrTransportOfferingByDepartureDate(
				MockSolrTransportOfferingUtils.getFutureTravelDateForTime(2, 4, 0, 0), CHARLE_DE_GAULLE, LONDON_GATWICK));

		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = new TransportOfferingSearchPageData<>();
		searchPageData.setResults(transportOfferings);

		// Setup mock Solr responses based on Sector
		Mockito.when(transportOfferingSearchFacade.transportOfferingSearch(Mockito.any(SearchData.class)))
				.thenReturn(searchPageData);

		final List<ScheduledRouteData> scheduledRoutes = transportOfferingFacade.getScheduledRoutes(fareSearchRequestData);

		Assert.assertTrue(scheduledRoutes.isEmpty());
		Assert.assertEquals(0, scheduledRoutes.size());

		// check data
		scheduledRoutes.forEach(sr -> {

			Assert.assertEquals(originDestinationInfo.getReferenceNumber(), sr.getReferenceNumber());
			Assert.assertEquals(MULTI_SECTOR_ROUTE, sr.getRoute().getCode());

			final TransportOfferingData origin = sr.getTransportOfferings().get(0);
			final TransportOfferingData destination = sr.getTransportOfferings().get(sr.getTransportOfferings().size() - 1);

			// assert the following over night connection route exisits
			if (origin.getCode().equals("TO_006") && destination.getCode().equals("TO_007"))
			{
				Assert.assertTrue(TravelDateUtils.isSameDate(origin.getArrivalTime(), TravelDateUtils.convertStringDateToDate(
						MockSolrTransportOfferingUtils.getFutureTravelDate(1), TravelservicesConstants.DATE_PATTERN)));
				Assert.assertTrue(TravelDateUtils.isSameDate(destination.getDepartureTime(), TravelDateUtils.convertStringDateToDate(
						MockSolrTransportOfferingUtils.getFutureTravelDate(2), TravelservicesConstants.DATE_PATTERN)));
			}
		});
	}

	@Test
	public void invalidRouteTest() throws ParseException
	{
		createSingleSectorRoute(SINGLE_SECTOR_ROUTE);

		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfo.setReferenceNumber(3);
		originDestinationInfo.setArrivalLocation(LONDON_HEATHROW);
		originDestinationInfo.setDepartureLocation(CHARLE_DE_GAULLE);

		fareSearchRequestData.getOriginDestinationInfo().add(originDestinationInfo);

		final List<ScheduledRouteData> scheduledRoutes = transportOfferingFacade.getScheduledRoutes(fareSearchRequestData);

		Assert.assertTrue(scheduledRoutes.isEmpty());
	}


	@Test
	public void routesWithNoSectorsTest() throws ParseException
	{
		createNoSectorRoute(NO_SECTOR_ROUTE);

		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfo.setReferenceNumber(4);
		originDestinationInfo.setArrivalLocation(LONDON_HEATHROW);
		originDestinationInfo.setDepartureLocation(JOHN_F_KENNEDY);

		fareSearchRequestData.getOriginDestinationInfo().add(originDestinationInfo);

		final List<ScheduledRouteData> scheduledRoutes = transportOfferingFacade.getScheduledRoutes(fareSearchRequestData);

		Assert.assertTrue(scheduledRoutes.isEmpty());
	}

	@Test
	public void routeWithNoSolrTransportOfferingsTest() throws ParseException
	{
		createRouteSectorWithNoSolrTransportOfferingsForFirstSector(NO_SOLR_TRANSPORT_OFFERINGS);

		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfo.setReferenceNumber(5);
		originDestinationInfo.setArrivalLocation(JOHN_F_KENNEDY);
		originDestinationInfo.setDepartureLocation(LONDON_HEATHROW);

		fareSearchRequestData.getOriginDestinationInfo().add(originDestinationInfo);

		final List<ScheduledRouteData> scheduledRoutes = transportOfferingFacade.getScheduledRoutes(fareSearchRequestData);

		Assert.assertTrue(scheduledRoutes.isEmpty());
	}

	@Test
	public void routeWithAnInvalidThirdSectorTest() throws ParseException
	{
		createTripleSectorRouteWithInvalidSecondSector(INVALID_THIRD_SECTOR);

		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfo.setReferenceNumber(6);
		originDestinationInfo.setArrivalLocation(LONDON_HEATHROW);
		originDestinationInfo.setDepartureLocation(JOHN_F_KENNEDY);

		fareSearchRequestData.getOriginDestinationInfo().add(originDestinationInfo);

		final List<ScheduledRouteData> scheduledRoutes = transportOfferingFacade.getScheduledRoutes(fareSearchRequestData);

		Assert.assertTrue(scheduledRoutes.isEmpty());
	}

	/**
	 * Setup of single sector data
	 *
	 * @param travelRouteCode
	 */
	private void createSingleSectorRoute(final String travelRouteCode)
	{
		final TravelRouteModel travelRoute = new TravelRouteModel();

		travelRoute.setCode(travelRouteCode);
		travelRoute.setName("London to Paris - Direct", Locale.ENGLISH);

		travelRoute.setOrigin(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));
		travelRoute.setDestination(getTransportFacilityModel(CHARLE_DE_GAULLE, CHARLE_DE_GAULLE_NAME));

		// sectors

		final TravelSectorModel sector = new TravelSectorModel();
		sector.setCode("S001");
		sector.setName("London to Paris", Locale.ENGLISH);
		sector.setOrigin(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));
		sector.setDestination(getTransportFacilityModel(CHARLE_DE_GAULLE, CHARLE_DE_GAULLE_NAME));

		final List<TravelSectorModel> sectors = new ArrayList<>();
		sectors.add(sector);

		travelRoute.setTravelSector(sectors);

		modelService.save(sector);
		modelService.save(travelRoute);
	}

	/**
	 * Setup of multi sector data
	 *
	 * @param travelRouteCode
	 */
	private void createMultiSectorRoute(final String travelRouteCode)
	{
		final TravelRouteModel travelRoute = new TravelRouteModel();

		travelRoute.setCode(travelRouteCode);
		travelRoute.setName("New York to Paris via London", Locale.ENGLISH);

		travelRoute.setOrigin(getTransportFacilityModel(JOHN_F_KENNEDY, JOHN_F_KENNEDY_NAME));
		travelRoute.setDestination(getTransportFacilityModel(CHARLE_DE_GAULLE, CHARLE_DE_GAULLE_NAME));

		// sectors

		final TravelSectorModel sector1 = new TravelSectorModel();
		sector1.setCode("S002");
		sector1.setName("New York to London", Locale.ENGLISH);
		sector1.setOrigin(getTransportFacilityModel(JOHN_F_KENNEDY, JOHN_F_KENNEDY_NAME));
		sector1.setDestination(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));

		final TravelSectorModel sector2 = new TravelSectorModel();
		sector2.setCode("S003");
		sector2.setName("London to Paris", Locale.ENGLISH);
		sector2.setOrigin(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));
		sector2.setDestination(getTransportFacilityModel(CHARLE_DE_GAULLE, CHARLE_DE_GAULLE_NAME));

		final List<TravelSectorModel> sectors = new ArrayList<>();
		sectors.add(sector1);
		sectors.add(sector2);

		travelRoute.setTravelSector(sectors);

		modelService.save(sector1);
		modelService.save(sector2);
		modelService.save(travelRoute);
	}

	/**
	 * Setup of triple sector route where the second sector has no solr transport offerings
	 *
	 * @param travelRouteCode
	 */
	private void createTripleSectorRouteWithInvalidSecondSector(final String travelRouteCode)
	{
		final TravelRouteModel travelRoute = new TravelRouteModel();

		travelRoute.setCode(travelRouteCode);
		travelRoute.setName("London to New York", Locale.ENGLISH);

		travelRoute.setOrigin(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));
		travelRoute.setDestination(getTransportFacilityModel(JOHN_F_KENNEDY, JOHN_F_KENNEDY_NAME));

		// sectors

		final TravelSectorModel sector1 = new TravelSectorModel();
		sector1.setCode("S001");
		sector1.setName("London to Paris", Locale.ENGLISH);
		sector1.setOrigin(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));
		sector1.setDestination(getTransportFacilityModel(CHARLE_DE_GAULLE, CHARLE_DE_GAULLE_NAME));

		// this route will return no transport offerings from solr mock class
		final TravelSectorModel sector2 = new TravelSectorModel();
		sector2.setCode("S002");
		sector2.setName("Paris to Edinburgh", Locale.ENGLISH);
		sector2.setOrigin(getTransportFacilityModel(CHARLE_DE_GAULLE, CHARLE_DE_GAULLE_NAME));
		sector2.setDestination(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));

		final TravelSectorModel sector3 = new TravelSectorModel();
		sector3.setCode("S003");
		sector3.setName("Paris to New York", Locale.ENGLISH);
		sector3.setOrigin(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));
		sector3.setDestination(getTransportFacilityModel(JOHN_F_KENNEDY, JOHN_F_KENNEDY_NAME));

		final List<TravelSectorModel> sectors = new ArrayList<>();
		sectors.add(sector1);
		sectors.add(sector2);
		sectors.add(sector3);

		travelRoute.setTravelSector(sectors);

		modelService.save(sector1);
		modelService.save(sector2);
		modelService.save(sector3);
		modelService.save(travelRoute);
	}

	/**
	 * Setup of routes with sectors where the first sector has no transport offering in solr
	 *
	 * @param travelRouteCode
	 */
	private void createRouteSectorWithNoSolrTransportOfferingsForFirstSector(final String travelRouteCode)
	{
		final TravelRouteModel travelRoute = new TravelRouteModel();

		travelRoute.setCode(travelRouteCode);
		travelRoute.setName("London to Paris via Edinburgh", Locale.ENGLISH);

		travelRoute.setOrigin(getTransportFacilityModel(JOHN_F_KENNEDY, JOHN_F_KENNEDY_NAME));
		travelRoute.setDestination(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));

		// sectors
		// this route will return no transport offerings from solr mock class
		final TravelSectorModel sector1 = new TravelSectorModel();
		sector1.setCode("S001");
		sector1.setName("London to Paris", Locale.ENGLISH);
		sector1.setOrigin(getTransportFacilityModel(JOHN_F_KENNEDY, JOHN_F_KENNEDY_NAME));
		sector1.setDestination(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));

		final TravelSectorModel sector2 = new TravelSectorModel();
		sector2.setCode("S002");
		sector2.setName("Paris to Edinburgh", Locale.ENGLISH);
		sector2.setOrigin(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));
		sector2.setDestination(getTransportFacilityModel(CHARLE_DE_GAULLE, CHARLE_DE_GAULLE_NAME));

		final List<TravelSectorModel> sectors = new ArrayList<>();
		sectors.add(sector1);
		sectors.add(sector2);

		travelRoute.setTravelSector(sectors);

		modelService.save(sector1);
		modelService.save(sector2);
		modelService.save(travelRoute);
	}

	/**
	 * Setup of route without sectors
	 *
	 * @param travelRouteCode
	 */
	private void createNoSectorRoute(final String travelRouteCode)
	{
		final TravelRouteModel travelRoute = new TravelRouteModel();

		travelRoute.setCode(travelRouteCode);
		travelRoute.setName("London to New York - Direct", Locale.ENGLISH);

		travelRoute.setOrigin(getTransportFacilityModel(LONDON_HEATHROW, LONDON_HEATHROW_NAME));
		travelRoute.setDestination(getTransportFacilityModel(JOHN_F_KENNEDY, JOHN_F_KENNEDY_NAME));

		modelService.save(travelRoute);
	}

	/**
	 * Method to setup TransportFacilities. The method will first check to see if one already exists and if so returns it
	 * back otherwise it will create one.
	 *
	 * @param code
	 * @param name
	 * @return
	 */
	private TransportFacilityModel getTransportFacilityModel(final String code, final String name)
	{

		TransportFacilityModel transportFacility = getExisitingTransportFacility(code);

		if (transportFacility == null)
		{

			transportFacility = new TransportFacilityModel();

			transportFacility.setCode(code);
			transportFacility.setName(name, Locale.ENGLISH);
			transportFacility.setType(TransportFacilityType.AIRPORT);

			modelService.save(transportFacility);
		}

		return transportFacility;
	}

	/**
	 * The method query the database for a TransportFacility and returns null if one doesn't exist.
	 *
	 * @param code
	 * @return
	 */
	private TransportFacilityModel getExisitingTransportFacility(final String code)
	{
		final StringBuilder query = new StringBuilder();
		query.append("SELECT {").append(TransportFacilityModel.PK).append("}");
		query.append(" FROM {").append(TransportFacilityModel._TYPECODE + "}");
		query.append(" WHERE {").append(TransportFacilityModel.CODE).append("}=?").append(TransportFacilityModel.CODE);

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TransportFacilityModel.CODE, code);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString(), params);

		final SearchResult<TransportFacilityModel> results = flexibleSearchService.search(flexibleSearchQuery);

		return results.getResult().isEmpty() ? null : results.getResult().get(0);
	}

	private TransportOfferingSearchPageData<SearchData, TransportOfferingData> getSolrTransportOfferings(
			final String departureDateTime, final String originLocation, final String destinationLocation)
	{
		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = new TransportOfferingSearchPageData<>();
		searchPageData.setResults(MockSolrTransportOfferingUtils.getMockSolrTransportOfferingByDepartureDate(departureDateTime,
				originLocation, destinationLocation));

		return searchPageData;

	}

}
