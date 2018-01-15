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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SelectedSeatData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.impl.AccommodationMapRequestHandler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationMapRequestHandlerTest
{

	@InjectMocks
	private final AccommodationMapRequestHandler accommodationMapRequestHandler = new AccommodationMapRequestHandler();

	@Test
	public void noAccommMapRequestDataIfNoReservationItemsTest()
	{
		final ReservationData reservationData = new ReservationData();
		final OfferRequestData offerRequestData = new OfferRequestData();
		accommodationMapRequestHandler.handle(reservationData, offerRequestData);
		Assert.assertNull(offerRequestData.getSeatMapRequest());
	}

	@Test
	public void accomMapRequestExistIfReservationItemsExistTest()
	{
		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItemData = new ReservationItemData();
		final List<ReservationItemData> reservationItems = new ArrayList<>();
		reservationItems.add(reservationItemData);
		reservationData.setReservationItems(reservationItems);
		final OfferRequestData offerRequestData = new OfferRequestData();
		accommodationMapRequestHandler.handle(reservationData, offerRequestData);
		Assert.assertNotNull(offerRequestData.getSeatMapRequest());
	}

	@Test
	public void accomMapRequestContainsSameNoOfSelectedSeatDataAsInReservationItemsTest()
	{
		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItemData = new ReservationItemData();
		final ReservationPricingInfoData reservationPricingInfo = new ReservationPricingInfoData();
		reservationItemData.setReservationPricingInfo(reservationPricingInfo);
		final SelectedSeatData selectedSeatData = new SelectedSeatData();
		final List<SelectedSeatData> selectedSeats = new ArrayList<>();
		selectedSeats.add(selectedSeatData);
		reservationPricingInfo.setSelectedSeats(selectedSeats);
		final List<ReservationItemData> reservationItems = new ArrayList<>();
		reservationItems.add(reservationItemData);
		reservationData.setReservationItems(reservationItems);
		final OfferRequestData offerRequestData = new OfferRequestData();
		accommodationMapRequestHandler.handle(reservationData, offerRequestData);
		Assert.assertEquals(selectedSeats.size(), offerRequestData.getSeatMapRequest().getSelectedSeats().size());
	}

}
