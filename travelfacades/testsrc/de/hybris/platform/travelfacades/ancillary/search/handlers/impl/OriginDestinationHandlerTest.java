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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.impl.OriginDestinationHandler;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test class for OriginDestinationHandler.
 */
@UnitTest
public class OriginDestinationHandlerTest
{
	private Map<String, String> offerGroupToOriginDestinationMapping;

	private OriginDestinationHandler originDestinationHandler;

	/**
	 * Initializing test data
	 */
	@Before
	public void setUp()
	{
		offerGroupToOriginDestinationMapping = new HashMap<>();
		offerGroupToOriginDestinationMapping.put("HOLDITEM", TravelfacadesConstants.TRAVEL_ROUTE);
		offerGroupToOriginDestinationMapping.put("MEAL", TravelfacadesConstants.TRANSPORT_OFFERING);

		originDestinationHandler = new OriginDestinationHandler();
		originDestinationHandler.setOfferGroupToOriginDestinationMapping(offerGroupToOriginDestinationMapping);
	}

	/**
	 * given: TransportOfferings and OfferGroups.
	 * 
	 * when: Journey type is a singleSector
	 * 
	 * Then: Creates One OriginDestinationOption for all the transportOfferings if the OfferGroup type is configured at
	 * route level. Creates OriginDestinationOption per TransportOffering, if the OfferGroup type is configured at
	 * transport offering level
	 */
	@Test
	public void testPopulateSingleSector()
	{
		final TestDataSetup testDataSetUp = new TestDataSetup();
		final OriginDestinationOptionData odOptionLGWCDGRes1 = testDataSetUp
				.createOrignDestinationOptionData(Stream.of(testDataSetUp.createTransportOfferingData("EZY4567010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()), "LGW-CDG");
		final ItineraryData itineraryData = testDataSetUp.createItineraryData("LGW-CDG",
				Stream.of(odOptionLGWCDGRes1).collect(Collectors.<OriginDestinationOptionData> toList()));
		final OfferRequestData offerRequestData = testDataSetUp.createOfferRequestData(null,
				Stream.of(itineraryData).collect(Collectors.<ItineraryData> toList()));

		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(
				Stream.of(testDataSetUp.createOfferGroupData("HOLDITEM"), testDataSetUp.createOfferGroupData("MEAL"),
						testDataSetUp.createOfferGroupData("PRIORITY_BOARDING")).collect(Collectors.<OfferGroupData> toList()));

		originDestinationHandler.handle(offerRequestData, offerResponseData);

		final List<OfferGroupData> holdItemsGroup = offerResponseData.getOfferGroups().stream()
				.filter(group -> "HOLDITEM".equals(group.getCode())).collect(Collectors.toList());
		final List<OfferGroupData> mealGroup = offerResponseData.getOfferGroups().stream()
				.filter(group -> "MEAL".equals(group.getCode())).collect(Collectors.toList());

		assertEquals(1, holdItemsGroup.stream().findFirst().get().getOriginDestinationOfferInfos().size());
		assertEquals(1, mealGroup.stream().findFirst().get().getOriginDestinationOfferInfos().size());
	}

	/**
	 * given: TransportOfferings and OfferGroups.
	 * 
	 * when: Journey type is a multiSector
	 * 
	 * Then: Creates One OriginDestinationOption for all the transportOfferings if the OfferGroup type is configured at
	 * route level. Creates OriginDestinationOption per TransportOffering, if the OfferGroup type is configured at
	 * transport offering level
	 */
	@Test
	public void testPopulateMultiSector()
	{
		final TestDataSetup testDataSetUp = new TestDataSetup();
		final OriginDestinationOptionData odOptionEDILGWCDGRes1 = testDataSetUp.createOrignDestinationOptionData(Stream
				.of(testDataSetUp.createTransportOfferingData("EZY4567010120160730"),
						testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
				.collect(Collectors.<TransportOfferingData> toList()), "EDI-LGW-CDG");
		final ItineraryData itineraryData = testDataSetUp.createItineraryData("EDI-LGW-CDG",
				Stream.of(odOptionEDILGWCDGRes1).collect(Collectors.<OriginDestinationOptionData> toList()));
		final OfferRequestData offerRequestData = testDataSetUp.createOfferRequestData(null,
				Stream.of(itineraryData).collect(Collectors.<ItineraryData> toList()));

		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(
				Stream.of(testDataSetUp.createOfferGroupData("HOLDITEM"), testDataSetUp.createOfferGroupData("MEAL"),
						testDataSetUp.createOfferGroupData("PRIORITY_BOARDING")).collect(Collectors.<OfferGroupData> toList()));

		originDestinationHandler.handle(offerRequestData, offerResponseData);

		final List<OfferGroupData> holdItemsGroup = offerResponseData.getOfferGroups().stream()
				.filter(group -> "HOLDITEM".equals(group.getCode())).collect(Collectors.toList());
		final List<OfferGroupData> mealGroup = offerResponseData.getOfferGroups().stream()
				.filter(group -> "MEAL".equals(group.getCode())).collect(Collectors.toList());

		assertEquals(1, holdItemsGroup.stream().findFirst().get().getOriginDestinationOfferInfos().size());
		assertEquals(2, mealGroup.stream().findFirst().get().getOriginDestinationOfferInfos().size());
	}

	@Test
	public void testPopulateReturnSector()
	{
		final TestDataSetup testDataSetUp = new TestDataSetup();
		final OriginDestinationOptionData odOptionLGWCDGRes1 = testDataSetUp
				.createOrignDestinationOptionData(Stream.of(testDataSetUp.createTransportOfferingData("EZY4567010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()), "LGW-CDG");
		final ItineraryData itineraryData = testDataSetUp.createItineraryData("LGW-CDG",
				Stream.of(odOptionLGWCDGRes1).collect(Collectors.<OriginDestinationOptionData> toList()));

		final OriginDestinationOptionData odOptionCDGLGWRes2 = testDataSetUp
				.createOrignDestinationOptionData(Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()), "CDG-LGW");
		final ItineraryData itineraryData2 = testDataSetUp.createItineraryData("CDG-LGW",
				Stream.of(odOptionCDGLGWRes2).collect(Collectors.<OriginDestinationOptionData> toList()));
		final OfferRequestData offerRequestData = testDataSetUp.createOfferRequestData(null,
				Stream.of(itineraryData, itineraryData2).collect(Collectors.<ItineraryData> toList()));

		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(
				Stream.of(testDataSetUp.createOfferGroupData("HOLDITEM"), testDataSetUp.createOfferGroupData("MEAL"),
						testDataSetUp.createOfferGroupData("PRIORITY_BOARDING")).collect(Collectors.<OfferGroupData> toList()));

		originDestinationHandler.handle(offerRequestData, offerResponseData);

		final List<OfferGroupData> holdItemsGroup = offerResponseData.getOfferGroups().stream()
				.filter(group -> "HOLDITEM".equals(group.getCode())).collect(Collectors.toList());
		final List<OfferGroupData> mealGroup = offerResponseData.getOfferGroups().stream()
				.filter(group -> "MEAL".equals(group.getCode())).collect(Collectors.toList());

		assertEquals(2, holdItemsGroup.stream().findFirst().get().getOriginDestinationOfferInfos().size());
		assertEquals(2, mealGroup.stream().findFirst().get().getOriginDestinationOfferInfos().size());
	}

	/**
	 * Test data setup
	 */
	private class TestDataSetup
	{

		private OfferRequestData createOfferRequestData(final SelectedOffersData selectedOffers,
				final List<ItineraryData> itineraries)
		{
			final OfferRequestData offerReqData = new OfferRequestData();
			offerReqData.setSelectedOffers(selectedOffers);
			offerReqData.setItineraries(itineraries);
			return offerReqData;

		}

		private OfferResponseData createOfferResponseData(final List<OfferGroupData> offerGroups)
		{
			final OfferResponseData offerResData = new OfferResponseData();
			offerResData.setOfferGroups(offerGroups);
			return offerResData;

		}

		private OfferGroupData createOfferGroupData(final String categoryCode)
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
			offerGroupData.setCode(categoryCode);
			return offerGroupData;
		}

		private TransportOfferingData createTransportOfferingData(final String code)
		{
			final TransportOfferingData toData = new TransportOfferingData();
			toData.setCode(code);
			return toData;
		}

		private ItineraryData createItineraryData(final String routeCode, final List<OriginDestinationOptionData> odOptions)
		{
			final ItineraryData itineraryData = new ItineraryData();
			final TravelRouteData travelRouteData = new TravelRouteData();
			travelRouteData.setCode(routeCode);
			itineraryData.setRoute(travelRouteData);
			itineraryData.setOriginDestinationOptions(odOptions);
			return itineraryData;
		}

		private OriginDestinationOptionData createOrignDestinationOptionData(
				final List<TransportOfferingData> transportOfferingData, final String routeCode)
		{
			final OriginDestinationOptionData odData = new OriginDestinationOptionData();
			odData.setTransportOfferings(transportOfferingData);
			odData.setTravelRouteCode(routeCode);
			return odData;
		}

	}

}
