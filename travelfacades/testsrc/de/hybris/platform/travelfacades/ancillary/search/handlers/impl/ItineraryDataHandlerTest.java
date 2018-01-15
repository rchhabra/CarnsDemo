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

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.impl.ItineraryDataHandler;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;


/**
 * Unit test class for ItineraryDataHandler.
 */
@UnitTest
public class ItineraryDataHandlerTest
{
	ItineraryDataHandler handler = new ItineraryDataHandler();

	/**
	 * UnitTest case on setting ItineraryData to OfferRequestData
	 */
	@Test
	public void testPopulate()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferRequestData offerRequestData = testDataSetUp.createOfferRequestData();
		handler.handle(testDataSetUp.createReservationData(), offerRequestData);
		assertNotNull(offerRequestData.getItineraries());
	}

	private class TestDataSetUp
	{
		private ReservationData createReservationData()
		{
			final ReservationData reservationData = new ReservationData();
			reservationData
					.setReservationItems(Stream.of(createReservationItem(), createReservationItem()).collect(Collectors.toList()));
			return reservationData;
		}

		private ReservationItemData createReservationItem()
		{
			final ReservationItemData reservationItemData = new ReservationItemData();
			reservationItemData.setReservationItinerary(new ItineraryData());
			return reservationItemData;
		}

		private OfferRequestData createOfferRequestData()
		{
			return new OfferRequestData();
		}
	}

}
