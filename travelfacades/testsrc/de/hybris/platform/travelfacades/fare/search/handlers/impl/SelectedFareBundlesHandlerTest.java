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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SelectedFareBundlesHandlerTest
{
	@InjectMocks
	SelectedFareBundlesHandler handler;

	@Mock
	private CartService cartService;
	@Mock
	private ReservationPipelineManager transportReservationSummaryPipelineManager;
	@Mock
	private CartModel cart;

	@Test
	public void testHandleWithNullReservationData()
	{
		Mockito.when(cartService.hasSessionCart()).thenReturn(true);
		Mockito.when(cartService.getSessionCart()).thenReturn(cart);
		Mockito.when(transportReservationSummaryPipelineManager.executePipeline(cart)).thenReturn(null);
		handler.handle(Collections.emptyList(), null, null);
	}

	@Test
	public void testHandleWithEmptyReservationItems()
	{
		Mockito.when(cartService.hasSessionCart()).thenReturn(true);
		Mockito.when(cartService.getSessionCart()).thenReturn(cart);
		final ReservationData reservationData = new ReservationData();
		reservationData.setReservationItems(Collections.emptyList());
		Mockito.when(transportReservationSummaryPipelineManager.executePipeline(cart)).thenReturn(reservationData);
		handler.handle(Collections.emptyList(), null, null);
	}

	@Test
	public void testHandleWithReservationItemsWithSameTransportOfferingCodes()
	{
		final TestDataSetup testData = new TestDataSetup();
		Mockito.when(cartService.hasSessionCart()).thenReturn(true);
		Mockito.when(cartService.getSessionCart()).thenReturn(cart);
		final ReservationData reservationData = testData.createReservationData(0, "HY201", "economy");
		Mockito.when(transportReservationSummaryPipelineManager.executePipeline(cart)).thenReturn(reservationData);
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData
				.setPricedItineraries(Stream.of(testData.createPricedItinerary(0, "HY201", "economy")).collect(Collectors.toList()));
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertTrue(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).isSelected());
	}

	@Test
	public void testHandleWithReservationItemsWithDifferrentTransportOfferingCodes()
	{
		final TestDataSetup testData = new TestDataSetup();
		Mockito.when(cartService.hasSessionCart()).thenReturn(true);
		Mockito.when(cartService.getSessionCart()).thenReturn(cart);
		final ReservationData reservationData = testData.createReservationData(0, "HY201", "economy");
		Mockito.when(transportReservationSummaryPipelineManager.executePipeline(cart)).thenReturn(reservationData);
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData
				.setPricedItineraries(Stream.of(testData.createPricedItinerary(0, "HY202", "economy")).collect(Collectors.toList()));
		handler.handle(Collections.emptyList(), null, fareSelectionData);
		assertFalse(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).isSelected());
	}

	class TestDataSetup
	{
		public ReservationData createReservationData(final int originDesRefNum, final String transportOfferingCode,
				final String bundleType)
		{
			final ReservationData reservationData = new ReservationData();
			reservationData
					.setReservationItems(
							Stream.of(createReservationItemData(originDesRefNum, transportOfferingCode, bundleType))
									.collect(Collectors.toList()));
			return reservationData;
		}

		private ReservationItemData createReservationItemData(final int originDesRefNum, final String transportOfferingCode,
				final String bundleType)
		{
			final ReservationItemData reservationItem = new ReservationItemData();
			reservationItem.setReservationItinerary(createItineraryData(transportOfferingCode));
			reservationItem.setOriginDestinationRefNumber(originDesRefNum);
			reservationItem.setReservationPricingInfo(createReservaitonPricingInfo(bundleType));
			return reservationItem;
		}

		private ReservationPricingInfoData createReservaitonPricingInfo(final String bundleType)
		{
			final ReservationPricingInfoData reservationPricinfInfo = new ReservationPricingInfoData();
			reservationPricinfInfo.setItineraryPricingInfo(createItineraryPricingInfoData(bundleType));
			return reservationPricinfInfo;
		}

		private ItineraryPricingInfoData createItineraryPricingInfoData(final String bundleType)
		{
			final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
			itineraryPricingInfo.setBundleType(bundleType);
			return itineraryPricingInfo;
		}

		private ItineraryData createItineraryData(final String transportOfferingCode)
		{
			final ItineraryData itineraryData = new ItineraryData();
			itineraryData.setOriginDestinationOptions(
					Stream.of(createOriginDestinationOption(transportOfferingCode)).collect(Collectors.toList()));
			return itineraryData;
		}

		private OriginDestinationOptionData createOriginDestinationOption(final String transportOfferingCode)
		{
			final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
			originDestinationOption
					.setTransportOfferings(Stream.of(createTransportOffering(transportOfferingCode)).collect(Collectors.toList()));
			return originDestinationOption;
		}

		private TransportOfferingData createTransportOffering(final String code)
		{
			final TransportOfferingData transportOffering = new TransportOfferingData();
			transportOffering.setCode(code);
			return transportOffering;
		}

		public PricedItineraryData createPricedItinerary(final int originDesRefNum, final String transportOfferingCode,
				final String bundleType)
		{
			final PricedItineraryData priceditinerary = new PricedItineraryData();
			priceditinerary.setOriginDestinationRefNumber(originDesRefNum);
			priceditinerary.setItinerary(createItineraryData(transportOfferingCode));
			priceditinerary
					.setItineraryPricingInfos(Stream.of(createItineraryPricingInfoData(bundleType)).collect(Collectors.toList()));
			return priceditinerary;
		}

	}


}
