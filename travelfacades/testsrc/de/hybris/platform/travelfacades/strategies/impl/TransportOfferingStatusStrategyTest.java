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
package de.hybris.platform.travelfacades.strategies.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;




/**
 * Test for the Strategy {@link TransportOfferingStatusStrategy}
 */
@UnitTest
public class TransportOfferingStatusStrategyTest
{

	private TransportOfferingStatusStrategy transportOfferingStatusStrategy;
	private final List<TransportOfferingStatus> notAllowedStatuses = Arrays.asList(TransportOfferingStatus.BOARDED,
			TransportOfferingStatus.CANCELLED, TransportOfferingStatus.DEPARTED);

	@Before
	public void setUp()
	{
		transportOfferingStatusStrategy = new TransportOfferingStatusStrategy();
		transportOfferingStatusStrategy.setNotAllowedStatuses(notAllowedStatuses);
	}

	@Test
	public void testTransportOfferingWithAllowedStatus()
	{
		final OfferGroupData offerGroupData = new OfferGroupData();

		final OriginDestinationOfferInfoData odOfferInfoData = new OriginDestinationOfferInfoData();
		odOfferInfoData.setOriginDestinationRefNumber(0);

		final List<OriginDestinationOfferInfoData> odOfferInfoDataList = new ArrayList<>();
		odOfferInfoDataList.add(odOfferInfoData);

		final TransportOfferingData to1 = new TransportOfferingData();
		to1.setStatus(TransportOfferingStatus.SCHEDULED.getCode());
		final TransportOfferingData to2 = new TransportOfferingData();
		to2.setStatus(TransportOfferingStatus.SCHEDULED.getCode());

		final List<TransportOfferingData> transportOfferingDatas = new ArrayList<>();
		transportOfferingDatas.add(to1);
		transportOfferingDatas.add(to2);

		odOfferInfoData.setTransportOfferings(transportOfferingDatas);

		offerGroupData.setOriginDestinationOfferInfos(odOfferInfoDataList);

		final OfferResponseData offerResponseData = new OfferResponseData();

		offerResponseData.setOfferGroups(Stream.of(offerGroupData).collect(Collectors.toList()));

		final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
		odOptionData.setOriginDestinationRefNumber(0);
		final ItineraryData itinData = new ItineraryData();
		itinData.setOriginDestinationOptions(Stream.of(odOptionData).collect(Collectors.toList()));
		offerResponseData.setItineraries(Stream.of(itinData).collect(Collectors.toList()));

		transportOfferingStatusStrategy.filterOfferResponseData(offerResponseData);

		Assert.assertNotNull(offerGroupData.getOriginDestinationOfferInfos());
		Assert.assertTrue(CollectionUtils.isNotEmpty(offerGroupData.getOriginDestinationOfferInfos()));
		assertTrue("Expected size was 1, found: " + offerGroupData.getOriginDestinationOfferInfos().size(),
				offerGroupData.getOriginDestinationOfferInfos().size() == 1);
	}

	@Test
	public void testTransportOfferingWithNotAllowedStatus()
	{
		final OfferGroupData offerGroupData = new OfferGroupData();

		final OriginDestinationOfferInfoData odOfferInfoData = new OriginDestinationOfferInfoData();
		odOfferInfoData.setOriginDestinationRefNumber(0);

		final List<OriginDestinationOfferInfoData> odOfferInfoDataList = new ArrayList<>();
		odOfferInfoDataList.add(odOfferInfoData);

		final TransportOfferingData to1 = new TransportOfferingData();
		to1.setStatus(TransportOfferingStatus.SCHEDULED.getCode());
		final TransportOfferingData to2 = new TransportOfferingData();
		to2.setStatus(TransportOfferingStatus.DEPARTED.getCode());

		final List<TransportOfferingData> transportOfferingDatas = new ArrayList<>();
		transportOfferingDatas.add(to1);
		transportOfferingDatas.add(to2);

		odOfferInfoData.setTransportOfferings(transportOfferingDatas);

		offerGroupData.setOriginDestinationOfferInfos(odOfferInfoDataList);

		final OfferResponseData offerResponseData = new OfferResponseData();

		offerResponseData.setOfferGroups(Stream.of(offerGroupData).collect(Collectors.toList()));

		final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
		odOptionData.setOriginDestinationRefNumber(0);
		final ItineraryData itinData = new ItineraryData();
		itinData.setOriginDestinationOptions(Stream.of(odOptionData).collect(Collectors.toList()));
		offerResponseData.setItineraries(Stream.of(itinData).collect(Collectors.toList()));

		transportOfferingStatusStrategy.filterOfferResponseData(offerResponseData);

		Assert.assertNotNull(offerGroupData.getOriginDestinationOfferInfos());
		assertTrue("Expected size was 0, found: " + offerGroupData.getOriginDestinationOfferInfos().size(),
				offerGroupData.getOriginDestinationOfferInfos().size() == 0);
	}

	@Test
	public void testFilterSeatMapData()
	{
		final SeatMapResponseData testSeatMapResponseData = createSeatMapResponseData();
		transportOfferingStatusStrategy.filterSeatMapData(testSeatMapResponseData);
		Assert.assertFalse(testSeatMapResponseData.getSeatMap().stream()
				.filter(seatMap -> notAllowedStatuses.stream()
						.anyMatch(status -> StringUtils.equalsIgnoreCase(status.getCode(), seatMap.getTransportOffering().getStatus())))
				.findAny().isPresent());
	}

	private SeatMapResponseData createSeatMapResponseData()
	{
		final TransportOfferingData to1 = new TransportOfferingData();
		to1.setStatus(TransportOfferingStatus.SCHEDULED.getCode());
		final TransportOfferingData to2 = new TransportOfferingData();
		to2.setStatus(TransportOfferingStatus.CANCELLED.getCode());

		final List<TransportOfferingData> transportOfferingDatas = new ArrayList<>();
		transportOfferingDatas.add(to1);
		transportOfferingDatas.add(to2);

		final SeatMapResponseData seatMapResponseData = new SeatMapResponseData();

		final List<SeatMapData> seatMaps = new ArrayList<>();
		seatMaps.add(createSeatMapData(to1));
		seatMaps.add(createSeatMapData(to2));

		seatMapResponseData.setSeatMap(seatMaps);
		return seatMapResponseData;
	}

	private SeatMapData createSeatMapData(final TransportOfferingData transportOffering)
	{
		final SeatMapData seatMap = new SeatMapData();
		seatMap.setTransportOffering(transportOffering);
		return seatMap;
	}

}
