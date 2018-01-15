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

package de.hybris.platform.travelfacades.search.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SearchResultTransportOfferingPopulatorTest
{

	@InjectMocks
	private final SearchResultTransportOfferingPopulator populator = new SearchResultTransportOfferingPopulator();

	private SearchResultValueData source;
	private TransportOfferingData target;

	@Before
	public void setup()
	{
		source = TestData.createSearcgResultValueData();
		target = new TransportOfferingData();
	}

	@Test
	public void populatorTargetTest()
	{
		populator.populate(source, target);

		// check results
		Assert.assertEquals("HY0802", target.getNumber());
		Assert.assertEquals(TravelDateUtils.convertStringDateToDate("01/01/2016", TravelservicesConstants.DATE_PATTERN),
				target.getDepartureTime());
		Assert.assertEquals(TravelDateUtils.convertStringDateToDate("14/01/2016", TravelservicesConstants.DATE_PATTERN),
				target.getArrivalTime());
		Assert.assertEquals("HY0802010420160620", target.getCode());
		Assert.assertEquals("5400000", target.getDurationValue().toString());
		Assert.assertEquals("Hybris Airbus A380", target.getTransportVehicle().getVehicleInfo().getName());

		// check origin data
		Assert.assertEquals("T1", target.getOriginTerminal().getCode());
		Assert.assertEquals("LTN", target.getSector().getOrigin().getCode());
		Assert.assertEquals("Luton Airport", target.getSector().getOrigin().getName());
		Assert.assertEquals("London", target.getSector().getOrigin().getLocation().getName());
		Assert.assertEquals("LON", target.getSector().getOrigin().getLocation().getCode());
		Assert.assertEquals("London", target.getOriginLocationCity());
		Assert.assertEquals("UK", target.getOriginLocationCountry());

		// check destination data
		Assert.assertEquals("T2", target.getDestinationTerminal().getCode());
		Assert.assertEquals("CDG", target.getSector().getDestination().getCode());
		Assert.assertEquals("Charles De Gaulle", target.getSector().getDestination().getName());
		Assert.assertEquals("Paris", target.getSector().getDestination().getLocation().getName());
		Assert.assertEquals("PAR", target.getSector().getDestination().getLocation().getCode());
		Assert.assertEquals("Paris", target.getDestinationLocationCity());
		Assert.assertEquals("France", target.getDestinationLocationCountry());
	}

	@Test
	public void populatorTargetTestForTimeZoneId()
	{
		final Map<String, Object> values = source.getValues();
		values.put(TravelfacadesConstants.SOLR_FIELD_DEPARTURE_TIME_ZONE_ID, "Europe/London");
		values.put(TravelfacadesConstants.SOLR_FIELD_ARRIVAL_TIME_ZONE_ID, "Europe/London");
		source.setValues(values);
		populator.populate(source, target);

		// check results
		Assert.assertEquals("HY0802", target.getNumber());
		Assert.assertEquals(TravelDateUtils.convertStringDateToDate("01/01/2016", TravelservicesConstants.DATE_PATTERN),
				target.getDepartureTime());
		Assert.assertEquals(TravelDateUtils.convertStringDateToDate("14/01/2016", TravelservicesConstants.DATE_PATTERN),
				target.getArrivalTime());
		Assert.assertEquals("HY0802010420160620", target.getCode());
		Assert.assertEquals("5400000", target.getDurationValue().toString());
		Assert.assertEquals("Hybris Airbus A380", target.getTransportVehicle().getVehicleInfo().getName());

		// check origin data
		Assert.assertEquals("T1", target.getOriginTerminal().getCode());
		Assert.assertEquals("LTN", target.getSector().getOrigin().getCode());
		Assert.assertEquals("Luton Airport", target.getSector().getOrigin().getName());
		Assert.assertEquals("London", target.getSector().getOrigin().getLocation().getName());
		Assert.assertEquals("LON", target.getSector().getOrigin().getLocation().getCode());
		Assert.assertEquals("London", target.getOriginLocationCity());
		Assert.assertEquals("UK", target.getOriginLocationCountry());

		// check destination data
		Assert.assertEquals("T2", target.getDestinationTerminal().getCode());
		Assert.assertEquals("CDG", target.getSector().getDestination().getCode());
		Assert.assertEquals("Charles De Gaulle", target.getSector().getDestination().getName());
		Assert.assertEquals("Paris", target.getSector().getDestination().getLocation().getName());
		Assert.assertEquals("PAR", target.getSector().getDestination().getLocation().getCode());
		Assert.assertEquals("Paris", target.getDestinationLocationCity());
		Assert.assertEquals("France", target.getDestinationLocationCountry());
	}

	@Test
	public void testForNullValue()
	{
		source.setValues(null);
		populator.populate(source, target);

		// check results
		Assert.assertNull(target.getNumber());
	}

	private static class TestData
	{

		public static SearchResultValueData createSearcgResultValueData()
		{

			final SearchResultValueData source = new SearchResultValueData();

			final Map<String, Object> values = new HashMap<>();
			values.put(TravelfacadesConstants.SOLR_FIELD_NUMBER, "HY0802");
			values.put(TravelfacadesConstants.SOLR_FIELD_DEPARTURE_TIME,
					TravelDateUtils.convertStringDateToDate("01/01/2016", TravelservicesConstants.DATE_PATTERN));
			values.put(TravelfacadesConstants.SOLR_FIELD_ARRIVAL_TIME,
					TravelDateUtils.convertStringDateToDate("14/01/2016", TravelservicesConstants.DATE_PATTERN));
			values.put(TravelfacadesConstants.SOLR_FIELD_CODE, "HY0802010420160620");
			values.put(TravelfacadesConstants.SOLR_FIELD_DURATION, "5400000");
			values.put(TravelfacadesConstants.SOLR_FIELD_VEHICLE_INFORMATION_NAME, "Hybris Airbus A380");

			values.put(TravelfacadesConstants.SOLR_FIELD_ORIGIN_TERMINAL_CODE, "T1");
			values.put(TravelfacadesConstants.SOLR_FIELD_ORIGIN_TRANSPORT_FACILITY, "LTN");
			values.put(TravelfacadesConstants.SOLR_FIELD_ORIGIN_TRANSPORT_FACILITY_NAME, "Luton Airport");
			values.put(TravelfacadesConstants.SOLR_FIELD_ORIGIN_LOCATION_NAME, "London");
			values.put(TravelfacadesConstants.SOLR_FIELD_ORIGIN_LOCATION_DATA, createLocations("LON"));
			values.put(TravelfacadesConstants.SOLR_FIELD_ORIGIN_LOCATION_CITY, "London");
			values.put(TravelfacadesConstants.SOLR_FIELD_ORIGIN_LOCATION_COUNTRY, "UK");

			values.put(TravelfacadesConstants.SOLR_FIELD_DESTINATION_TERMINAL_CODE, "T2");
			values.put(TravelfacadesConstants.SOLR_FIELD_DESTINATION_TRANSPORT_FACILITY, "CDG");
			values.put(TravelfacadesConstants.SOLR_FIELD_DESTINATION_TRANSPORT_FACILITY_NAME, "Charles De Gaulle");
			values.put(TravelfacadesConstants.SOLR_FIELD_DESTINATION_LOCATION_NAME, "Paris");
			values.put(TravelfacadesConstants.SOLR_FIELD_DESTINATION_LOCATION_DATA, createLocations("PAR"));
			values.put(TravelfacadesConstants.SOLR_FIELD_DESTINATION_LOCATION_CITY, "Paris");
			values.put(TravelfacadesConstants.SOLR_FIELD_DESTINATION_LOCATION_COUNTRY, "France");

			source.setValues(values);

			return source;
		}

		private static List<String> createLocations(final String location)
		{
			final List<String> originLocations = new ArrayList<>();
			originLocations.add("");
			originLocations.add("");
			originLocations.add(location);
			return originLocations;
		}
	}

}
