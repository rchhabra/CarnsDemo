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
package de.hybris.platform.travelservices.model.dynamic.attribute;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DurationAttributeHandlerTest
{
	private final DurationAttributeHandler handler = new DurationAttributeHandler();

	@Test
	public void testForNoOffset()
	{
		final TransportOfferingModel transportOffering = TestData.createTransportOffering();
		transportOffering.setTravelSector(TestData.createTravelSector(0L, 0L));

		final Long offset = handler.get(transportOffering);
		Assert.assertEquals(new Long(0), offset);
	}

	@Test
	public void testForOriginTimeOffsetOnly()
	{
		final TransportOfferingModel transportOffering = TestData.createTransportOffering();
		transportOffering.setTravelSector(TestData.createTravelSector(1L, 0L));

		final Long offset = handler.get(transportOffering);
		Assert.assertEquals(new Long(3600000), offset);
	}

	@Test
	public void testForDestinationTimeOffsetOnly()
	{
		final TransportOfferingModel transportOffering = TestData.createTransportOffering();
		transportOffering.setTravelSector(TestData.createTravelSector(0L, 1L));

		final Long offset = handler.get(transportOffering);
		Assert.assertEquals(new Long(-3600000), offset);
	}

	@Test
	public void testForOriginDefaultFallbackDuration()
	{
		final TransportOfferingModel transportOffering = TestData.createTransportOffering();
		transportOffering.setTravelSector(TestData.createTravelSector(null, 0L));

		final Long offset = handler.get(transportOffering);
		Assert.assertEquals(new Long(7200000), offset);
	}

	@Test
	public void testForDestinationDefaultFallbackDuration()
	{
		final TransportOfferingModel transportOffering = TestData.createTransportOffering();
		transportOffering.setTravelSector(TestData.createTravelSector(0L, null));

		final Long offset = handler.get(transportOffering);
		Assert.assertEquals(new Long(7200000), offset);
	}

	@Test
	public void testForNullOriginInTravelSector()
	{
		final TravelSectorModel travelSector = TestData.createTravelSector(0L, 0L);
		travelSector.setOrigin(null);

		final TransportOfferingModel transportOffering = TestData.createTransportOffering();
		transportOffering.setTravelSector(travelSector);

		final Long offset = handler.get(transportOffering);
		Assert.assertEquals(new Long(7200000), offset);
	}

	@Test
	public void testForNullDestinationInTravelSector()
	{
		final TravelSectorModel travelSector = TestData.createTravelSector(0L, 0L);
		travelSector.setDestination(null);

		final TransportOfferingModel transportOffering = TestData.createTransportOffering();
		transportOffering.setTravelSector(travelSector);

		final Long offset = handler.get(transportOffering);
		Assert.assertEquals(new Long(7200000), offset);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testForNullTransportOffering()
	{
		handler.get(null);
	}

	private static class TestData
	{
		public static TransportOfferingModel createTransportOffering()
		{
			final TransportOfferingModel transportOffering = new TransportOfferingModel();
			transportOffering.setDepartureTime(new Date());
			transportOffering.setArrivalTime(new Date());
			return transportOffering;
		}

		public static TravelSectorModel createTravelSector(final Long originTimezoneOffset, final Long destinationTimezoneOffset)
		{
			final TravelSectorModel travelSector = new TravelSectorModel();
			travelSector.setOrigin(createTransportFacility("LTN", originTimezoneOffset));
			travelSector.setDestination(createTransportFacility("CDG", destinationTimezoneOffset));
			return travelSector;
		}

		private static TransportFacilityModel createTransportFacility(final String code, final Long timezoneOffset)
		{
			final List<PointOfServiceModel> pos = new ArrayList<>();
			pos.add(createPointOfService(timezoneOffset));

			final TransportFacilityModel transportFacility = new TransportFacilityModel();
			transportFacility.setCode(code);
			transportFacility.setPointOfService(pos);

			return transportFacility;
		}

		private static PointOfServiceModel createPointOfService(final Long timezoneOffset)
		{
			final PointOfServiceModel pos = new PointOfServiceModel();
			if (timezoneOffset != null)
			{
				final int offset = (int) (timezoneOffset + 0L);
				final ZoneId zoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(offset));
				pos.setTimeZoneId(zoneId.toString());
			}

			return pos;
		}
	}

}
