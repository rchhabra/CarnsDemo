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

package de.hybris.platform.travelfacades.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;


@UnitTest
public class TransportOfferingUtilsTest
{
	@Test
	public void testGetDurationMapMinutes()
	{
		final Map<String, Integer> durationMap = TransportOfferingUtils.getDurationMap(300000l);
		final String key = "transport.offering.status.result.minutes";
		assertEquals(key, durationMap.entrySet().iterator().next().getKey());
		assertEquals(5, durationMap.entrySet().iterator().next().getValue().intValue());
	}

	@Test
	public void testGetDurationMapHours()
	{
		final Map<String, Integer> durationMap = TransportOfferingUtils.getDurationMap(7200000l);
		final String key = "transport.offering.status.result.hours";
		assertEquals(key, durationMap.entrySet().iterator().next().getKey());
		assertEquals(2, durationMap.entrySet().iterator().next().getValue().intValue());
	}

	@Test
	public void testGetDurationMapDays()
	{
		final Map<String, Integer> durationMap = TransportOfferingUtils.getDurationMap(259200000l);
		final String key = "transport.offering.status.result.days";
		assertEquals(key, durationMap.entrySet().iterator().next().getKey());
		assertEquals(3, durationMap.entrySet().iterator().next().getValue().intValue());
	}

	@Test
	public void testCalulateJourneyDurationSingleSector() throws ParseException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final Map<String, Integer> durationMap = new HashMap<>();
		durationMap.put("transport.offering.status.result.minutes", 45);
		final TransportOfferingData toData = testDataSetUp.createTransportOfferingData(durationMap, 2700000l, "12/03/2016 07:35:00",
				"12/03/2016 08:20:00");
		final Map<String, Integer> durationResult = TransportOfferingUtils.calculateJourneyDuration(Stream.of(toData).collect(
				Collectors.toList()));
		final String key = "transport.offering.status.result.minutes";
		assertEquals(key, durationResult.entrySet().iterator().next().getKey());
		assertEquals(45, durationResult.entrySet().iterator().next().getValue().intValue());
	}

	@Test
	public void testCalulateJourneyDurationEmptyTransportOfferings() throws ParseException
	{
		final Map<String, Integer> durationResult = TransportOfferingUtils.calculateJourneyDuration(Collections.EMPTY_LIST);
		assertNull(durationResult);
	}

	@Test
	public void testCalulateJourneyDurationMultiSector() throws ParseException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final Map<String, Integer> durationMap = new HashMap<>();
		durationMap.put("transport.offering.status.result.minutes", 45);
		final TransportOfferingData toData = testDataSetUp.createTransportOfferingData(durationMap, 2700000l, "12/03/2016 07:35:00",
				"12/03/2016 08:20:00");
		final Map<String, Integer> durationMap1 = new HashMap<>();
		durationMap1.put("transport.offering.status.result.minutes", 50);
		final TransportOfferingData toData1 = testDataSetUp.createTransportOfferingData(durationMap1, 3000000l,
				"12/03/2016 09:30:00",
				"12/03/2016 10:20:00");
		final Map<String, Integer> durationResult = TransportOfferingUtils.calculateJourneyDuration(Stream.of(toData, toData1)
				.collect(Collectors.toList()));
		assertEquals(2, durationResult.get("transport.offering.status.result.hours").intValue());
		assertEquals(45, durationResult.get("transport.offering.status.result.minutes").intValue());
	}

	@Test
	public void testGetDurationMinutes()
	{
		final Map<String, Integer> durationMap1 = new HashMap<>();
		durationMap1.put("transport.offering.status.result.minutes", 50);
		durationMap1.put("transport.offering.status.result.hours", 2);
		durationMap1.put("transport.offering.status.result.days", 1);
		final long result = TransportOfferingUtils.getDuration(durationMap1);
		assertEquals(96600000l, result);
	}

	@Test
	public void testGetDurationEmptyMap()
	{
		final Map<String, Integer> durationMap1 = new HashMap<>();
		final long result = TransportOfferingUtils.getDuration(durationMap1);
		assertEquals(0l, result);
	}

	@Test
	public void testCompareTransportOfferingsInEqualSizes()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TransportOfferingData toData = testDataSetUp.createTransportOfferingData("EZY1234050620160735");
		final TransportOfferingData toData1 = testDataSetUp.createTransportOfferingData("EZY1111050620160735");
		final TransportOfferingData toData2 = testDataSetUp.createTransportOfferingData("EZY2222050620160735");
		assertFalse(TransportOfferingUtils.compareTransportOfferings(Stream.of(toData).collect(Collectors.toList()),
				Stream.of(toData1, toData2).collect(Collectors.toList())));
	}

	@Test
	public void testCompareTransportOfferingsInEqualCodes()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TransportOfferingData toData1 = testDataSetUp.createTransportOfferingData("EZY1234050620160735");
		final TransportOfferingData toData2 = testDataSetUp.createTransportOfferingData("EZY1111050620160735");
		final TransportOfferingData toData3 = testDataSetUp.createTransportOfferingData("EZY1111050620160735");
		final TransportOfferingData toData4 = testDataSetUp.createTransportOfferingData("EZY2222050620160735");
		assertFalse(TransportOfferingUtils.compareTransportOfferings(Stream.of(toData1, toData2).collect(Collectors.toList()),
				Stream.of(toData3, toData4).collect(Collectors.toList())));
	}

	@Test
	public void testCompareTransportOfferingsEqualCodes()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TransportOfferingData toData1 = testDataSetUp.createTransportOfferingData("EZY1234050620160735");
		final TransportOfferingData toData2 = testDataSetUp.createTransportOfferingData("EZY1111050620160735");
		final TransportOfferingData toData3 = testDataSetUp.createTransportOfferingData("EZY1111050620160735");
		final TransportOfferingData toData4 = testDataSetUp.createTransportOfferingData("EZY1234050620160735");
		assertTrue(TransportOfferingUtils.compareTransportOfferings(Stream.of(toData1, toData2).collect(Collectors.toList()),
				Stream.of(toData3, toData4).collect(Collectors.toList())));
	}

	private class TestDataSetUp
	{
		private TransportOfferingData createTransportOfferingData(final Map<String, Integer> durationMap, final Long duration,
				final String depTime, final String arrTime) throws ParseException
		{
			final SimpleDateFormat dateFormatter = new SimpleDateFormat(TravelservicesConstants.DATE_TIME_PATTERN);
			final TransportOfferingData toData = new TransportOfferingData();
			toData.setDuration(durationMap);
			toData.setDurationValue(duration);
			toData.setDepartureTime(dateFormatter.parse(depTime));
			toData.setDepartureTimeZoneId(ZoneId.systemDefault());
			toData.setArrivalTime(dateFormatter.parse(arrTime));
			toData.setArrivalTimeZoneId(ZoneId.systemDefault());
			return toData;
		}

		private TransportOfferingData createTransportOfferingData(final String code)
		{
			final TransportOfferingData toCode = new TransportOfferingData();
			toCode.setCode(code);
			return toCode;
		}
	}

}
