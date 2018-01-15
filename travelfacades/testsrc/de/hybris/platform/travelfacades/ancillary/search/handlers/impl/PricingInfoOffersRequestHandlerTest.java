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
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for PricingInfoOffersRequestHandler
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PricingInfoOffersRequestHandlerTest
{

	PricingInfoOffersRequestHandler pricingInfoOffersRequestHandler = new PricingInfoOffersRequestHandler();
	@Mock
	private TravelRestrictionFacade travelRestrictionFacade;

	/**
	 * given: ReservationData with ItitneraryData and pricingInfoData
	 *
	 * when: Itinerary is Direct route i.e Single Sector.
	 *
	 * then: Chosen products and their pricingInfo are populated as selectedOffers in OfferRequestData.
	 */
	@Test
	public void testPopulateDirectRoute()
	{
		final TestDataSetup testDataSetUp = new TestDataSetup();
		final OfferPricingInfoData offerPricingInfoBag20Kg = testDataSetUp.createOfferPricingInfoData("ExtrBag20Kg", "HOLDITEM");
		final OfferPricingInfoData offerPricingInfoBag30Kg = testDataSetUp.createOfferPricingInfoData("ExtrBag30Kg", "HOLDITEM");
		final OfferPricingInfoData offerPricingInfoStandMeal = testDataSetUp.createOfferPricingInfoData("StanddardMeal", "MEAL");
		final OriginDestinationOfferInfoData odOfferInfoData1 = testDataSetUp.createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoBag20Kg, offerPricingInfoBag30Kg).collect(Collectors.<OfferPricingInfoData> toList()),
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));
		final OriginDestinationOfferInfoData odOfferInfoData2 = testDataSetUp.createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoStandMeal).collect(Collectors.<OfferPricingInfoData> toList()),
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));
		final ReservationPricingInfoData resPricingInfoData1 = testDataSetUp.createReservationPricingInfoData(
				Stream.of(odOfferInfoData1, odOfferInfoData2).collect(Collectors.<OriginDestinationOfferInfoData> toList()));
		final ReservationItemData reservationitem1 = testDataSetUp.createReservationItemData(resPricingInfoData1);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationitem1).collect(Collectors.<ReservationItemData> toList()));
		reservationData.setOfferPricingInfos(
				Stream.of(testDataSetUp.createOfferpricingInfoDataForReservationData("ExtrBag20Kg")).collect(Collectors.toList()));

		final OriginDestinationOptionData odOptionLGWCDGRes1 = testDataSetUp
				.createOrignDestinationOptionData(Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()), "LGW-CDG");

		final ItineraryData itineraryData = testDataSetUp.createItineraryData("LGW-CDG",
				Stream.of(odOptionLGWCDGRes1).collect(Collectors.<OriginDestinationOptionData> toList()));

		final OriginDestinationOfferInfoData selectedOdOfferInfoData1 = testDataSetUp.createOriginDestinationOfferInfoData(null,
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));

		final OriginDestinationOfferInfoData selectedOdOfferInfoData2 = testDataSetUp.createOriginDestinationOfferInfoData(null,
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));

		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(Stream
				.of(testDataSetUp.createOfferGroupData("MEAL",
						Stream.of(selectedOdOfferInfoData1).collect(Collectors.<OriginDestinationOfferInfoData> toList())),
				testDataSetUp.createOfferGroupData("HOLDITEM",
						Stream.of(selectedOdOfferInfoData2).collect(Collectors.<OriginDestinationOfferInfoData> toList())))
				.collect(Collectors.<OfferGroupData> toList()));

		final OfferRequestData offerRequestData = testDataSetUp.createOfferRequestData(selectedOffers,
				Stream.of(itineraryData).collect(Collectors.<ItineraryData> toList()));
		pricingInfoOffersRequestHandler.setTravelRestrictionFacade(travelRestrictionFacade);
		given(travelRestrictionFacade.getAddToCartCriteria(Matchers.anyString()))
				.willReturn(AddToCartCriteriaType.PER_LEG.getCode());
		pricingInfoOffersRequestHandler.handle(reservationData, offerRequestData);

		offerRequestData.getSelectedOffers().getOfferGroups().forEach(offerGroup -> {

			assertFalse(offerGroup.getOriginDestinationOfferInfos().stream()
					.anyMatch(originDestInfo -> originDestInfo.getOfferPricingInfos().isEmpty()));
		});

		final List<OfferGroupData> holdItemOfferGroupsHoldItem = offerRequestData.getSelectedOffers().getOfferGroups().stream()
				.filter(offerGroup -> "HOLDITEM".equals(offerGroup.getCode())).collect(Collectors.<OfferGroupData> toList());

		final List<OfferGroupData> mealOfferGroupsHoldItem = offerRequestData.getSelectedOffers().getOfferGroups().stream()
				.filter(offerGroup -> "MEAL".equals(offerGroup.getCode())).collect(Collectors.<OfferGroupData> toList());

		assertEquals(1, holdItemOfferGroupsHoldItem.size());
		assertEquals(1, mealOfferGroupsHoldItem.size());

		holdItemOfferGroupsHoldItem.forEach(offerGroup -> {
			offerGroup.getOriginDestinationOfferInfos()
					.forEach(odOfferInfo -> assertEquals(2, odOfferInfo.getOfferPricingInfos().size()));
		});

		mealOfferGroupsHoldItem.forEach(offerGroup -> {
			offerGroup.getOriginDestinationOfferInfos()
					.forEach(odOfferInfo -> assertEquals(1, odOfferInfo.getOfferPricingInfos().size()));
		});

	}

	/**
	 * given: ReservationData with ItitneraryData and pricingInfoData
	 *
	 * when: Itinerary is MultiSector.
	 *
	 * then: Chosen products and their pricingInfo are populated as selectedOffers in OfferRequestData.
	 */
	@Test
	public void testPopulateMultiSector()
	{
		final TestDataSetup testDataSetUp = new TestDataSetup();
		final OfferPricingInfoData offerPricingInfoBag20Kg = testDataSetUp.createOfferPricingInfoData("ExtrBag20Kg", "HOLDITEM");
		final OfferPricingInfoData offerPricingInfoBag30Kg = testDataSetUp.createOfferPricingInfoData("ExtrBag30Kg", "HOLDITEM");
		final OfferPricingInfoData offerPricingInfoStandMeal1 = testDataSetUp.createOfferPricingInfoData("StanddardMeal", "MEAL");
		final OfferPricingInfoData offerPricingInfoStandMeal2 = testDataSetUp.createOfferPricingInfoData("StanddardMeal", "MEAL");
		final OriginDestinationOfferInfoData odOfferInfoData1 = testDataSetUp.createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoBag20Kg, offerPricingInfoBag30Kg).collect(Collectors.<OfferPricingInfoData> toList()),
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"),
						testDataSetUp.createTransportOfferingData("EZY5678010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));
		final OriginDestinationOfferInfoData odOfferInfoData2 = testDataSetUp.createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoStandMeal1).collect(Collectors.<OfferPricingInfoData> toList()),
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));
		final OriginDestinationOfferInfoData odOfferInfoData3 = testDataSetUp.createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoStandMeal2).collect(Collectors.<OfferPricingInfoData> toList()),
				Stream.of(testDataSetUp.createTransportOfferingData("EZY5678010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));
		final ReservationPricingInfoData resPricingInfoData1 = testDataSetUp
				.createReservationPricingInfoData(Stream.of(odOfferInfoData1, odOfferInfoData2, odOfferInfoData3)
						.collect(Collectors.<OriginDestinationOfferInfoData> toList()));
		final ReservationItemData reservationitem1 = testDataSetUp.createReservationItemData(resPricingInfoData1);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationitem1).collect(Collectors.<ReservationItemData> toList()));

		final OriginDestinationOptionData odOptionEDILGWCDGRes1 = testDataSetUp.createOrignDestinationOptionData(Stream
				.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"),
						testDataSetUp.createTransportOfferingData("EZY5678010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()), "EDI-LGW-CDG");

		final ItineraryData itineraryData = testDataSetUp.createItineraryData("EDI-LGW-CDG",
				Stream.of(odOptionEDILGWCDGRes1).collect(Collectors.<OriginDestinationOptionData> toList()));

		final OriginDestinationOfferInfoData selectedOdOfferInfoData1 = testDataSetUp.createOriginDestinationOfferInfoData(null,
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"),
						testDataSetUp.createTransportOfferingData("EZY5678010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));

		final OriginDestinationOfferInfoData selectedOdOfferInfoData2 = testDataSetUp.createOriginDestinationOfferInfoData(null,
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));
		final OriginDestinationOfferInfoData selectedOdOfferInfoData3 = testDataSetUp.createOriginDestinationOfferInfoData(null,
				Stream.of(testDataSetUp.createTransportOfferingData("EZY5678010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));

		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(Stream
				.of(testDataSetUp.createOfferGroupData("MEAL",
						Stream.of(selectedOdOfferInfoData2, selectedOdOfferInfoData3)
								.collect(Collectors.<OriginDestinationOfferInfoData> toList())),
				testDataSetUp.createOfferGroupData("HOLDITEM",
						Stream.of(selectedOdOfferInfoData1).collect(Collectors.<OriginDestinationOfferInfoData> toList())))
				.collect(Collectors.<OfferGroupData> toList()));

		final OfferRequestData offerRequestData = testDataSetUp.createOfferRequestData(selectedOffers,
				Stream.of(itineraryData).collect(Collectors.<ItineraryData> toList()));

		pricingInfoOffersRequestHandler.handle(reservationData, offerRequestData);

		offerRequestData.getSelectedOffers().getOfferGroups().forEach(offerGroup -> {

			assertFalse(offerGroup.getOriginDestinationOfferInfos().stream()
					.anyMatch(originDestInfo -> originDestInfo.getOfferPricingInfos().isEmpty()));
		});

		final List<OfferGroupData> holdItemOfferGroupsHoldItem = offerRequestData.getSelectedOffers().getOfferGroups().stream()
				.filter(offerGroup -> "HOLDITEM".equals(offerGroup.getCode())).collect(Collectors.<OfferGroupData> toList());

		final List<OfferGroupData> mealOfferGroupsHoldItem = offerRequestData.getSelectedOffers().getOfferGroups().stream()
				.filter(offerGroup -> "MEAL".equals(offerGroup.getCode())).collect(Collectors.<OfferGroupData> toList());

		assertEquals(1, holdItemOfferGroupsHoldItem.size());
		assertEquals(1, mealOfferGroupsHoldItem.size());

		holdItemOfferGroupsHoldItem.forEach(offerGroup -> {
			offerGroup.getOriginDestinationOfferInfos()
					.forEach(odOfferInfo -> assertEquals(2, odOfferInfo.getOfferPricingInfos().size()));
		});

		mealOfferGroupsHoldItem.forEach(offerGroup -> {
			offerGroup.getOriginDestinationOfferInfos()
					.forEach(odOfferInfo -> assertEquals(1, odOfferInfo.getOfferPricingInfos().size()));
		});

	}

	/**
	 * given: ReservationData with ItitneraryData and pricingInfoData
	 *
	 * when: TransportOfferings in ReservationData misMatch with TransportOfferings in OfferRequestData
	 *
	 * then: No selected offers are populated in offerRequest data.
	 */
	@Test
	public void testPopulateTransportOfferingMismatch()
	{
		final TestDataSetup testDataSetUp = new TestDataSetup();
		final OfferPricingInfoData offerPricingInfoBag20Kg = testDataSetUp.createOfferPricingInfoData("ExtrBag20Kg", "HOLDITEM");
		final OriginDestinationOfferInfoData odOfferInfoData1 = testDataSetUp.createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoBag20Kg).collect(Collectors.<OfferPricingInfoData> toList()),
				Stream.of(testDataSetUp.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));
		final ReservationPricingInfoData resPricingInfoData1 = testDataSetUp.createReservationPricingInfoData(
				Stream.of(odOfferInfoData1).collect(Collectors.<OriginDestinationOfferInfoData> toList()));
		final ReservationItemData reservationitem1 = testDataSetUp.createReservationItemData(resPricingInfoData1);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationitem1).collect(Collectors.<ReservationItemData> toList()));

		final OriginDestinationOptionData odOptionLGWCDGRes1 = testDataSetUp
				.createOrignDestinationOptionData(Stream.of(testDataSetUp.createTransportOfferingData("EZY4567010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()), "LGW-CDG");

		final ItineraryData itineraryData = testDataSetUp.createItineraryData("LGW-CDG",
				Stream.of(odOptionLGWCDGRes1).collect(Collectors.<OriginDestinationOptionData> toList()));

		final OriginDestinationOfferInfoData selectedOdOfferInfoData1 = testDataSetUp.createOriginDestinationOfferInfoData(null,
				Stream.of(testDataSetUp.createTransportOfferingData("EZY4567010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()));

		final SelectedOffersData selectedOffers = testDataSetUp.createSelectedOffersData(Stream
				.of(testDataSetUp.createOfferGroupData("HOLDITEM",
						Stream.of(selectedOdOfferInfoData1).collect(Collectors.<OriginDestinationOfferInfoData> toList())))
				.collect(Collectors.<OfferGroupData> toList()));

		final OfferRequestData offerRequestData = testDataSetUp.createOfferRequestData(selectedOffers,
				Stream.of(itineraryData).collect(Collectors.<ItineraryData> toList()));

		pricingInfoOffersRequestHandler.handle(reservationData, offerRequestData);

		offerRequestData.getSelectedOffers().getOfferGroups().forEach(offerGroup -> {

			assertTrue(offerGroup.getOriginDestinationOfferInfos().stream()
					.anyMatch(originDestInfo -> originDestInfo.getOfferPricingInfos().isEmpty()));
		});
	}

	/**
	 * Test data setup
	 */
	private class TestDataSetup
	{
		private ReservationData createReservationData(final List<ReservationItemData> reservationItems)
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setReservationItems(reservationItems);
			return reservationData;
		}

		private ReservationItemData createReservationItemData(final ReservationPricingInfoData reservationPricingInfo)
		{
			final ReservationItemData reservationItemData = new ReservationItemData();
			reservationItemData.setReservationPricingInfo(reservationPricingInfo);
			return reservationItemData;
		}

		private ReservationPricingInfoData createReservationPricingInfoData(
				final List<OriginDestinationOfferInfoData> originDestinationOfferInfos)
		{
			final ReservationPricingInfoData resPricingInfoData = new ReservationPricingInfoData();
			resPricingInfoData.setOriginDestinationOfferInfos(originDestinationOfferInfos);
			return resPricingInfoData;
		}

		private OriginDestinationOfferInfoData createOriginDestinationOfferInfoData(
				final List<OfferPricingInfoData> offerPricingInfos, final List<TransportOfferingData> transportOfferings)
		{
			final OriginDestinationOfferInfoData odOfferInfoData = new OriginDestinationOfferInfoData();
			odOfferInfoData.setOfferPricingInfos(offerPricingInfos);
			odOfferInfoData.setTransportOfferings(transportOfferings);
			return odOfferInfoData;
		}

		private OfferPricingInfoData createOfferPricingInfoData(final String productCode, final String categoryCode)
		{
			final OfferPricingInfoData offerPriceInfo = new OfferPricingInfoData();
			final ProductData productData = new ProductData();
			productData.setCode(productCode);
			final CategoryData catData = new CategoryData();
			catData.setCode(categoryCode);
			productData.setCategories(Stream.of(catData).collect(Collectors.<CategoryData> toList()));
			offerPriceInfo.setProduct(productData);
			return offerPriceInfo;
		}

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

		private OfferGroupData createOfferGroupData(final String categoryCode,
				final List<OriginDestinationOfferInfoData> OriginDestinationOfferInfos)
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
			offerGroupData.setCode(categoryCode);
			offerGroupData.setOriginDestinationOfferInfos(OriginDestinationOfferInfos);
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

		public OfferPricingInfoData createOfferpricingInfoDataForReservationData(final String productCode)
		{
			final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
			final ProductData product = new ProductData();
			product.setCode(productCode);
			final CategoryData category = new CategoryData();
			category.setCode("HOLDITEM");
			product.setCategories(Stream.of(category).collect(Collectors.toList()));
			offerPricingInfo.setProduct(product);
			return offerPricingInfo;
		}
	}

}
