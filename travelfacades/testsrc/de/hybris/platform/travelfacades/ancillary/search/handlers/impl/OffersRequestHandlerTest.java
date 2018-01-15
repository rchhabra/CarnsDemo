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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class to test OffersRequestHandler.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OffersRequestHandlerTest
{
	@InjectMocks
	private OffersRequestHandler offersRequestHandler;

	@Mock
	private SessionService sessionService;

	/**
	 * given: ReservationData with multiple products of different categories selected (or offered as bundle)
	 *
	 * When: Ancillary request is made
	 *
	 * Then: OfferRequestData should have selectedOffers with OfferGroups one per each category of the product in
	 * Reservation data.
	 */
	@Test
	public void testPopulate()
	{
		final TestDataSetup testdataSetUp = new TestDataSetup();
		final OfferPricingInfoData offerPricingInfoBag20Kg = testdataSetUp.createOfferPricingInfoData("ExtrBag20Kg", "HOLDITEM");
		final OfferPricingInfoData offerPricingInfoBag30Kg = testdataSetUp.createOfferPricingInfoData("ExtrBag30Kg", "HOLDITEM");
		final OfferPricingInfoData offerPricingInfoStandMeal = testdataSetUp.createOfferPricingInfoData("StanddardMeal", "MEAL");
		final OfferPricingInfoData offerPricingInfoLoungeAccess1 = testdataSetUp.createOfferPricingInfoData("LAaccess1",
				"LOUNGEACCESS");
		final OfferPricingInfoData offerPricingInfoLoungeAccess2 = testdataSetUp.createOfferPricingInfoData("LAaccess2",
				"LOUNGEACCESS");
		final OfferPricingInfoData offerPricingInfoVegMeal = testdataSetUp.createOfferPricingInfoData("VegeterianMeal", "MEAL");
		final OfferPricingInfoData offerPricingInfoPCheckIn1 = testdataSetUp.createOfferPricingInfoData("PriorityCheckin1",
				"PRIORITYCHECKIN");
		final OfferPricingInfoData offerPricingInfoPCheckIn2 = testdataSetUp.createOfferPricingInfoData("PriorityCheckin2",
				"PRIORITYCHECKIN");

		final OriginDestinationOfferInfoData odOfferInfoData1 = testdataSetUp.createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoBag20Kg, offerPricingInfoBag30Kg, offerPricingInfoStandMeal, offerPricingInfoPCheckIn1)
						.collect(Collectors.<OfferPricingInfoData> toList()));

		final ReservationPricingInfoData resPricingInfoData1 = testdataSetUp.createReservationPricingInfoData(
				Stream.of(odOfferInfoData1).collect(Collectors.<OriginDestinationOfferInfoData> toList()));

		final OriginDestinationOfferInfoData odOfferInfoData2 = testdataSetUp.createOriginDestinationOfferInfoData(Stream
				.of(offerPricingInfoBag20Kg, offerPricingInfoBag30Kg, offerPricingInfoVegMeal, offerPricingInfoPCheckIn2,
						offerPricingInfoLoungeAccess1, offerPricingInfoLoungeAccess2)
				.collect(Collectors.<OfferPricingInfoData> toList()));

		final ReservationPricingInfoData resPricingInfoData2 = testdataSetUp.createReservationPricingInfoData(
				Stream.of(odOfferInfoData2).collect(Collectors.<OriginDestinationOfferInfoData> toList()));

		final ReservationItemData reservationitem1 = testdataSetUp.createReservationItemData(resPricingInfoData1);

		final ReservationItemData reservationitem2 = testdataSetUp.createReservationItemData(resPricingInfoData2);

		final ReservationData reservationData = testdataSetUp.createReservationData(
				Stream.of(reservationitem1, reservationitem2).collect(Collectors.<ReservationItemData> toList()));

		final PricedItineraryData outboundPricedItinerary = new PricedItineraryData();
		given(offersRequestHandler.getSessionService().getAttribute(Matchers.eq("outboundPricedItinerary")))
				.willReturn(outboundPricedItinerary);

		final PricedItineraryData inboundPricedItinerary = new PricedItineraryData();
		given(offersRequestHandler.getSessionService().getAttribute(Matchers.eq("inboundPricedItinerary")))
				.willReturn(inboundPricedItinerary);

		final OfferRequestData offerReqData = new OfferRequestData();

		offersRequestHandler.handle(reservationData, offerReqData);

		assertNotNull(offerReqData.getSelectedOffers());
		assertEquals(4, offerReqData.getSelectedOffers().getOfferGroups().size());

		assertTrue(offerReqData.getSelectedOffers().getOfferGroups().stream().allMatch(offerGroup -> {
			if (offerGroup.getCode().equals("MEAL") || offerGroup.getCode().equals("HOLDITEM")
					|| offerGroup.getCode().equals("LOUNGEACCESS") || offerGroup.getCode().equals("PRIORITYCHECKIN"))
			{
				return true;
			}
			return false;
		}));
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
				final List<OfferPricingInfoData> offerPricingInfos)
		{
			final OriginDestinationOfferInfoData odOfferInfoData = new OriginDestinationOfferInfoData();
			odOfferInfoData.setOfferPricingInfos(offerPricingInfos);
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
	}

}
