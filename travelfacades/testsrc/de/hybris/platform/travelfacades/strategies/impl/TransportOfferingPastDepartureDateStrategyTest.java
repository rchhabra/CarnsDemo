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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportOfferingPastDepartureDateStrategyTest
{

	@InjectMocks
	private final TransportOfferingPastDepartureDateStrategy strategy = new TransportOfferingPastDepartureDateStrategy();

	@Mock
	private TimeService timeService;

	private OfferResponseData offerResponseData;

	@Before
	public void setup()
	{
		offerResponseData = TestData.createOfferResponseData();
		given(timeService.getCurrentTime()).willReturn(Calendar.getInstance().getTime());
	}

	@Test
	public void departureTimeIsBeforeCurrentTimeFilterTest()
	{
		strategy.filterOfferResponseData(offerResponseData);
		Assert.assertTrue(offerResponseData.getOfferGroups().stream().findFirst().get().getOriginDestinationOfferInfos().isEmpty());
	}

	@Test
	public void departureTimeIsAfterCurrentTimeFilterTest()
	{
		offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getTransportOfferings().get(0)
				.setDepartureTime(TravelDateUtils.addDays(Calendar.getInstance().getTime(), 1));
		offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().get(0).getTransportOfferings().get(0)
				.setDepartureTimeZoneId(ZoneId.systemDefault());

		strategy.filterOfferResponseData(offerResponseData);

		Assert.assertFalse(offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().isEmpty());
		Assert.assertEquals(1, offerResponseData.getOfferGroups().get(0).getOriginDestinationOfferInfos().size());
	}

	@Test
	public void testfilterSeatMapData()
	{
		final SeatMapResponseData testResult=TestData.creataSeatMapResponseData();
		strategy.filterSeatMapData(testResult);
		Assert.assertEquals(1, CollectionUtils.size(testResult.getSeatMap()));
	}

	private static class TestData
	{
		public static OfferGroupData creataOfferGroup()
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
			offerGroupData.setOriginDestinationOfferInfos(creataOriginDestinationOfferInfos());
			return offerGroupData;
		}

		public static OfferResponseData createOfferResponseData()
		{
			final OfferResponseData offerResponseData = new OfferResponseData();
			offerResponseData.setOfferGroups(Stream.of(creataOfferGroup()).collect(Collectors.toList()));
			offerResponseData.setItineraries(Stream.of(createItineraryData()).collect(Collectors.toList()));
			return offerResponseData;
		}

		public static ItineraryData createItineraryData()
		{
			final ItineraryData itinData = new ItineraryData();
			itinData.setOriginDestinationOptions(Stream.of(createOriginDestinationOptionData(0)).collect(Collectors.toList()));
			return itinData;
		}

		public static OriginDestinationOptionData createOriginDestinationOptionData(final int originDestinationRefNumber)
		{
			final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
			originDestinationOption.setOriginDestinationRefNumber(originDestinationRefNumber);
			return originDestinationOption;
		}

		public static List<OriginDestinationOfferInfoData> creataOriginDestinationOfferInfos()
		{
			final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<>();
			originDestinationOfferInfos.add(creataOriginDestinationOfferInfo(0));
			return originDestinationOfferInfos;
		}

		public static OriginDestinationOfferInfoData creataOriginDestinationOfferInfo(final int originDestinationRefNumber)
		{
			final OriginDestinationOfferInfoData originDestinationOfferInfo = new OriginDestinationOfferInfoData();
			originDestinationOfferInfo.setOriginDestinationRefNumber(originDestinationRefNumber);
			originDestinationOfferInfo.setTransportOfferings(creataTransportOfferings());
			return originDestinationOfferInfo;
		}

		public static List<TransportOfferingData> creataTransportOfferings()
		{
			final List<TransportOfferingData> transportOfferings = new ArrayList<>();
			transportOfferings.add(creataTransportOffering(LocalDateTime.now().minusDays(1)));
			transportOfferings.add(creataTransportOffering(LocalDateTime.now().plusDays(1)));
			return transportOfferings;
		}

		public static TransportOfferingData creataTransportOffering(final LocalDateTime date)
		{
			final TransportOfferingData transportOffering = new TransportOfferingData();
			transportOffering.setDepartureTime(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
			transportOffering.setDepartureTimeZoneId(ZoneId.systemDefault());
			return transportOffering;
		}

		public static SeatMapResponseData creataSeatMapResponseData()
		{
			final SeatMapData smd1 = new SeatMapData();
			smd1.setTransportOffering(creataTransportOffering(LocalDateTime.now().plusDays(1)));

			final SeatMapData smd2 = new SeatMapData();
			smd2.setTransportOffering(creataTransportOffering(LocalDateTime.now().minusDays(3)));

			final List<SeatMapData> seatMaps = new ArrayList<>();
			seatMaps.add(smd1);
			seatMaps.add(smd2);
			final SeatMapResponseData seatMapResponseData = new SeatMapResponseData();
			seatMapResponseData.setSeatMap(seatMaps);
			return seatMapResponseData;
		}
	}
}
