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

package de.hybris.platform.travelfacades.ancillary.search.accommodation.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapRequestData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SelectedSeatData;
import de.hybris.platform.travelfacades.ancillary.search.accommodation.handlers.SelectedAccommodationHandler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SelectedAccommodationHandlerTest
{

	@InjectMocks
	private final SelectedAccommodationHandler selectedAccommodationHandler = new SelectedAccommodationHandler();

	@Test
	public void noSeatMapInOfferResponseIfNoSelectedSeatMapInOfferRequestTest()
	{
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferResponseData offerResponseData = new OfferResponseData();
		selectedAccommodationHandler.handle(offerRequestData, offerResponseData);
		Assert.assertNull(offerResponseData.getSeatMap());
	}

	@Test
	public void noSelectedSeatMapInOfferResponseIfNoSeatMapInOfferResponseTest()
	{
		final OfferRequestData offerRequestData = new OfferRequestData();
		final SeatMapRequestData seatMapRequest = new SeatMapRequestData();
		offerRequestData.setSeatMapRequest(seatMapRequest);
		final List<SelectedSeatData> selectedSeats = new ArrayList<>();
		final SelectedSeatData selectedSeatData = new SelectedSeatData();
		selectedSeats.add(selectedSeatData);
		seatMapRequest.setSelectedSeats(selectedSeats);
		final OfferResponseData offerResponseData = new OfferResponseData();
		selectedAccommodationHandler.handle(offerRequestData, offerResponseData);
		Assert.assertNull(offerResponseData.getSeatMap());
	}

	@Test
	public void offerResponseSeatMapDataContainsSelectedSeatsIfTransportOfferingMatchTest()
	{
		final OfferRequestData offerRequestData = new OfferRequestData();
		final SeatMapRequestData seatMapRequest = new SeatMapRequestData();
		offerRequestData.setSeatMapRequest(seatMapRequest);
		final List<SelectedSeatData> selectedSeats = new ArrayList<>();
		final SelectedSeatData selectedSeatData = new SelectedSeatData();
		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setCode("testCode");
		selectedSeatData.setTransportOffering(transportOffering);
		selectedSeats.add(selectedSeatData);
		seatMapRequest.setSelectedSeats(selectedSeats);

		final OfferResponseData offerResponseData = new OfferResponseData();
		final SeatMapResponseData seatMapResponseData = new SeatMapResponseData();
		offerResponseData.setSeatMap(seatMapResponseData);
		final List<SeatMapData> seatMap = new ArrayList<>();
		seatMapResponseData.setSeatMap(seatMap);
		final SeatMapData seatMapData = new SeatMapData();
		seatMap.add(seatMapData);
		final TransportOfferingData transportOffering1 = new TransportOfferingData();
		transportOffering1.setCode("testCode");
		seatMapData.setTransportOffering(transportOffering1);
		selectedAccommodationHandler.handle(offerRequestData, offerResponseData);
		Assert.assertNotNull(seatMapData.getSelectedSeats());
		Assert.assertEquals(1, seatMapData.getSelectedSeats().size());
	}

}
