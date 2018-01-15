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

package de.hybris.platform.travelfacades.fare.search.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.fare.search.manager.FareSearchPipelineManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUpgradeFareSearchFacadeTest
{
	@InjectMocks
	DefaultUpgradeFareSearchFacade defaultUpgradeFareSearchFacade;

	@Mock
	ReservationFacade reservationFacade;

	@Mock
	FareSearchPipelineManager upgradeBundleFareSearchPipelineManager;

	@Test
	public void testDoUpgradeSearch()
	{
		final ReservationData reservationdata = new ReservationData();

		final List<ReservationItemData> reservationItems = new ArrayList<>();
		final ReservationItemData reservationItemData = new ReservationItemData();
		reservationItems.add(reservationItemData);
		reservationdata.setReservationItems(reservationItems);

		final ReservationPricingInfoData reservationPricingInfo = new ReservationPricingInfoData();
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();

		final List<PTCFareBreakdownData> ptcFareBreakdownDatas = new ArrayList<>();
		final PTCFareBreakdownData pTCFareBreakdownData = new PTCFareBreakdownData();
		ptcFareBreakdownDatas.add(pTCFareBreakdownData);
		itineraryPricingInfo.setPtcFareBreakdownDatas(ptcFareBreakdownDatas);
		reservationPricingInfo.setItineraryPricingInfo(itineraryPricingInfo);
		reservationItemData.setReservationPricingInfo(reservationPricingInfo);

		final ItineraryData reservationItinerary = new ItineraryData();
		final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<>();
		final OriginDestinationOptionData originDestinationOptionData = new OriginDestinationOptionData();
		originDestinationOptions.add(originDestinationOptionData);
		final List<TransportOfferingData> transportOfferings = new ArrayList<>();
		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("testTransportOfferingCode");
		transportOfferingData.setDepartureTime(DateUtils.addHours(new Date(), 4));
		transportOfferingData.setArrivalTime(DateUtils.addHours(new Date(), 5));

		final TravelSectorData sector = new TravelSectorData();
		final TransportFacilityData origin = new TransportFacilityData();
		origin.setCode("LTN");
		sector.setOrigin(origin);
		final TransportFacilityData destination = new TransportFacilityData();
		destination.setCode("CDG");
		sector.setDestination(destination);
		transportOfferingData.setSector(sector);
		transportOfferings.add(transportOfferingData);
		originDestinationOptionData.setTransportOfferings(transportOfferings);
		reservationItinerary.setOriginDestinationOptions(originDestinationOptions);
		reservationItemData.setReservationItinerary(reservationItinerary);

		Mockito.when(reservationFacade.getCurrentReservationData()).thenReturn(reservationdata);
		final FareSelectionData fareSelectionDataExp = new FareSelectionData();
		Mockito.when(
				upgradeBundleFareSearchPipelineManager.executePipeline(Mockito.anyList(), Mockito.any(FareSearchRequestData.class)))
				.thenReturn(fareSelectionDataExp);

		Assert.assertEquals(fareSelectionDataExp, defaultUpgradeFareSearchFacade.doUpgradeSearch());
	}

}
