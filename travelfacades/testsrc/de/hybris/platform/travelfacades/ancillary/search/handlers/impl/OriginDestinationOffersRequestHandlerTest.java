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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;


/**
 * Unit test class for OriginDestinationOffersRequestHandler.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OriginDestinationOffersRequestHandlerTest
{
	@Mock
	private TravelRestrictionFacade travelRestrictionFacade;

	private final Map<String, String> offerGroupToOriginDestinationMapping = ImmutableMap.of("PRIORITYCHECKIN", "TravelRoute",
			"HOLDITEM", "TravelRoute", "PRIORITYBOARDING", "TransportOffering", "LOUNGEACCESS", "TransportOffering", "MEAL",
			"TransportOffering");

	private final OriginDestinationOffersRequestHandler originDestinationOffersRequestHandler = new OriginDestinationOffersRequestHandler();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		originDestinationOffersRequestHandler.setTravelRestrictionFacade(travelRestrictionFacade);
	}

	/**
	 * given: OfferRequestData for a multiSector has offerGroups of different types
	 *
	 * When: An ancillary offer Request is made
	 *
	 * Then: OrginDestination options are creates as, if the offerGroup code is configured at Route level, then one
	 * OrginDestination Option covering all sectors, if the offerGroup code is configured at TransportOffering level,
	 * then one OrginDestination Option for each transport offering.
	 *
	 */
	@Test
	public void testPopulate()
	{
		originDestinationOffersRequestHandler.setOfferGroupToOriginDestinationMapping(offerGroupToOriginDestinationMapping);

		//OfferRequest creation
		final TestDataSetup testDataSetup = new TestDataSetup();

		final OriginDestinationOptionData odOptionEDILGWCDGRes1 = testDataSetup.createOrignDestinationOptionData(Stream
				.of(testDataSetup.createTransportOfferingData("EZY1234010120160730"),
						testDataSetup.createTransportOfferingData("EZY5678010120160930"))
				.collect(Collectors.<TransportOfferingData> toList()), "EDI-LGW-CDG");

		final ItineraryData itineraryData = testDataSetup.createItineraryData("EDI-LGW-CDG",
				Stream.of(odOptionEDILGWCDGRes1).collect(Collectors.<OriginDestinationOptionData> toList()));

		final SelectedOffersData selectedOffers = testDataSetup.createSelectedOffersData(
				Stream.of(testDataSetup.createOfferGroupData("MEAL"), testDataSetup.createOfferGroupData("PRIORITYCHECKIN"),
						testDataSetup.createOfferGroupData("HOLDITEM"), testDataSetup.createOfferGroupData("PRIORITYBOARDING"),
						testDataSetup.createOfferGroupData("LOUNGEACCESS")).collect(Collectors.<OfferGroupData> toList()));

		final OfferRequestData offerRequestData = testDataSetup.createOfferRequestData(selectedOffers,
				Stream.of(itineraryData).collect(Collectors.<ItineraryData> toList()));

		originDestinationOffersRequestHandler.handle(null, offerRequestData);

		offerRequestData.getSelectedOffers().getOfferGroups().forEach(offerGroup -> {
			assertNotNull(offerGroup.getOriginDestinationOfferInfos());
			assertFalse(offerGroup.getOriginDestinationOfferInfos().isEmpty());
			switch (offerGroup.getCode())
			{
				case "PRIORITYCHECKIN":
					assertEquals(1, offerGroup.getOriginDestinationOfferInfos().size());
					break;
				case "HOLDITEM":
					assertEquals(1, offerGroup.getOriginDestinationOfferInfos().size());
					break;
				case "PRIORITYBOARDING":
					assertEquals(2, offerGroup.getOriginDestinationOfferInfos().size());
					break;
				case "LOUNGEACCESS":
					assertEquals(2, offerGroup.getOriginDestinationOfferInfos().size());
					break;
				case "MEAL":
					assertEquals(2, offerGroup.getOriginDestinationOfferInfos().size());
					break;
			}
		});

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

		private SelectedOffersData createSelectedOffersData(final List<OfferGroupData> offerGroups)
		{
			final SelectedOffersData selectedOffersData = new SelectedOffersData();
			selectedOffersData.setOfferGroups(offerGroups);
			return selectedOffersData;
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
