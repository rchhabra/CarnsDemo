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

package de.hybris.platform.travelfacades.fare.search.utils;

import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingType;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;


/**
 * MOCK TRANSPORT OFFERING DATA OBJECT SETUP
 */
public class MockSolrTransportOfferingUtils
{
	private static final Logger LOG = Logger.getLogger(MockSolrTransportOfferingUtils.class);

	private static final String US = "US";
	private static final String UK = "UK";
	private static final String FRANCE = "France";
	private static final String NEW_YORK = "NewYork";
	private static final String LONDON = "London";
	private static final String PARIS = "Paris";
	private static final String JFK = "JFK";
	private static final String LHR = "LHR";
	private static final String CDG = "CDG";
	private static final String LGW = "LGW";
	private static final String LONDON_HEATHROW = "London Heathrow";
	private static final String LONDON_GATWICK = "London Gatwick";
	private static final String PARIS_AIRPORT = "Paris airport";
	private static final String JOHN_KENNEDY = "John Kennedy";

	private MockSolrTransportOfferingUtils()
	{
		//empty to avoid instantiating utils class
	}

	public static TransportOfferingData getMockSolrTransportOfferingCode(final String transportOfferingCode)
	{

		final List<TransportOfferingData> result = generate().stream().filter(to -> to.getCode().equals(transportOfferingCode))
				.collect(Collectors.toList());

		return result.isEmpty() ? null : result.get(0);

	}

	public static List<TransportOfferingData> getMockSolrTransportOfferingByOriginDestinationCode(final String originCode,
			final String destinationCode)
	{

		return generate().stream().filter(to -> to.getSector().getOrigin().getCode().equals(originCode)
				&& to.getSector().getDestination().getCode().equals(destinationCode)).collect(Collectors.toList());
	}

	public static List<TransportOfferingData> getMockSolrTransportOfferingByDepartureDate(final String departureDate,
			final String originLocation, final String destinationLocation)
	{
		return generate().stream()
				.filter(to -> TravelDateUtils.isSameDate(to.getDepartureTime(),
						TravelDateUtils.convertStringDateToDate(departureDate, TravelservicesConstants.DATE_TIME_PATTERN))
				&& to.getSector().getOrigin().getCode().equals(originLocation)
				&& to.getSector().getDestination().getCode().equals(destinationLocation)).collect(Collectors.toList());
	}

	public static List<TransportOfferingData> generate()
	{

		final List<TransportOfferingData> transportOfferingData = new ArrayList<TransportOfferingData>();

		transportOfferingData.add(createTransportOfferingData("TO_000", getFutureTravelDateForTime(1, 16, 0, 0),
				getFutureTravelDateForTime(1, 20, 0, 0), LHR, LONDON_HEATHROW, CDG, PARIS_AIRPORT, LONDON, UK, PARIS, FRANCE));
		transportOfferingData.add(createTransportOfferingData("TO_001", getFutureTravelDateForTime(1, 18, 0, 0),
				getFutureTravelDateForTime(1, 22, 0, 0), LHR, LONDON_HEATHROW, CDG, PARIS_AIRPORT, LONDON, UK, PARIS, FRANCE));
		transportOfferingData.add(createTransportOfferingData("TO_002", getFutureTravelDateForTime(1, 14, 0, 0),
				getFutureTravelDateForTime(1, 22, 0, 0), JFK, JOHN_KENNEDY, LHR, LONDON_HEATHROW, NEW_YORK, US, LONDON, UK));
		transportOfferingData.add(createTransportOfferingData("TO_003", getFutureTravelDateForTime(1, 9, 0, 0),
				getFutureTravelDateForTime(1, 14, 0, 0), JFK, JOHN_KENNEDY, LHR, LONDON_HEATHROW, NEW_YORK, US, LONDON, UK));

		// Transport Offering overnight connections

		transportOfferingData.add(createTransportOfferingData("TO_004", getFutureTravelDateForTime(2, 1, 0, 0),
				getFutureTravelDateForTime(2, 4, 0, 0), LHR, LONDON_HEATHROW, CDG, PARIS_AIRPORT, LONDON, UK, PARIS, FRANCE));
		transportOfferingData.add(createTransportOfferingData("TO_005", getFutureTravelDateForTime(2, 3, 0, 0),
				getFutureTravelDateForTime(2, 6, 0, 0), LHR, LONDON_HEATHROW, CDG, PARIS_AIRPORT, LONDON, UK, PARIS, FRANCE));


		transportOfferingData.add(createTransportOfferingData("TO_006", getFutureTravelDateForTime(1, 9, 0, 0),
				getFutureTravelDateForTime(1, 23, 0, 0), JFK, JOHN_KENNEDY, CDG, PARIS_AIRPORT, NEW_YORK, US, PARIS, FRANCE));
		transportOfferingData.add(createTransportOfferingData("TO_007", getFutureTravelDateForTime(2, 3, 0, 0),
				getFutureTravelDateForTime(2, 5, 0, 0), CDG, PARIS_AIRPORT, LGW, LONDON_GATWICK, PARIS, FRANCE, LONDON, UK));

		return transportOfferingData;

	}

	public static String getFutureTravelDate(final int days)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
		final Date date = DateUtils.addDays(new Date(), days);
		return formatter.format(date);
	}

	public static String getFutureTravelDateForTime(final int days, final int hour, final int min, final int sec)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat(TravelservicesConstants.DATE_TIME_PATTERN);
		Date date = DateUtils.addDays(new Date(), days);
		date = DateUtils.setHours(date, hour);
		date = DateUtils.setMinutes(date, min);
		date = DateUtils.setSeconds(date, sec);
		return formatter.format(date);
	}

	public static TransportOfferingData createTransportOfferingData(final String code, final String departureDateTime,
			final String arrivalDateTime, final String origin, final String originName, final String destination,
			final String destinationName, final String originCity, final String originCountry, final String destinationCity,
			final String destinationCountry)
	{

		final Date departure = TravelDateUtils.convertStringDateToDate(departureDateTime, TravelservicesConstants.DATE_TIME_PATTERN);
		final Date arrival = TravelDateUtils.convertStringDateToDate(arrivalDateTime, TravelservicesConstants.DATE_TIME_PATTERN);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();

		transportOfferingData.setCode(code);
		transportOfferingData.setType(TransportOfferingType.DIRECT.name());
		transportOfferingData.setArrivalTime(arrival);
		transportOfferingData.setOriginTerminal(getTerminal("T5", "Terminal 5"));
		transportOfferingData.setDepartureTime(departure);
		transportOfferingData.setDestinationTerminal(getTerminal("T1", "Terminal 1"));
		transportOfferingData.setStatus(TransportOfferingStatus.SCHEDULED.name());
		transportOfferingData.setSector(createTravelSectorData(origin, originName, destination, destinationName));
		transportOfferingData.setOriginLocationCity(originCity);
		transportOfferingData.setOriginLocationCountry(originCountry);
		transportOfferingData.setDestinationLocationCity(destinationCity);
		transportOfferingData.setDestinationLocationCountry(destinationCountry);
		transportOfferingData.setDepartureTimeZoneId(getZoneIdForTransportFacility(origin));
		transportOfferingData.setArrivalTimeZoneId(getZoneIdForTransportFacility(destination));

		return transportOfferingData;

	}

	protected static ZoneId getZoneIdForTransportFacility(final String transportFacility)
	{
		ZoneId zoneId = null;
		if (transportFacility.equals("LHR"))
		{
			zoneId = ZoneId.of("Europe/London");
		}
		if (transportFacility.equals("CDG"))
		{
			zoneId = ZoneId.of("Europe/Paris");
		}
		if (transportFacility.equals("JFK"))
		{
			zoneId = ZoneId.of("America/New_York");
		}
		return zoneId;
	}

	protected static TravelSectorData createTravelSectorData(final String origin, final String originName,
			final String destination, final String destinationName)
	{

		final TravelSectorData travelSectorData = new TravelSectorData();

		travelSectorData.setOrigin(createTransportFacilityData(origin, originName));
		travelSectorData.setDestination(createTransportFacilityData(destination, destinationName));

		return travelSectorData;
	}

	protected static TransportFacilityData createTransportFacilityData(final String code, final String name)
	{

		final TransportFacilityData transportFacility = new TransportFacilityData();
		transportFacility.setCode(code);
		transportFacility.setName(name);

		return transportFacility;

	}

	protected static TerminalData getTerminal(final String code, final String name)
	{

		final TerminalData terminal = new TerminalData();
		terminal.setCode(code);
		terminal.setName(name);

		return terminal;
	}

}
