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

import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.commercefacades.travel.TravelProviderData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;



/**
 * The class is responsible for populating TransportOfferingData DTO object from the solr search result data.
 */
public class SearchResultTransportOfferingPopulator implements Populator<SearchResultValueData, TransportOfferingData>
{
	private static final Logger LOG = Logger.getLogger(SearchResultTransportOfferingPopulator.class);

	/**
	 * The method populates TransportOfferingData.
	 *
	 * @param source
	 * @param target
	 */
	@Override
	public void populate(final SearchResultValueData source, final TransportOfferingData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setNumber(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_NUMBER));

		final TravelProviderData travelProvider = new TravelProviderData();
		travelProvider.setCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_TRAVEL_PROVIDER));
		target.setTravelProvider(travelProvider);

		target.setDepartureTime(this.<Date> getValue(source, TravelfacadesConstants.SOLR_FIELD_DEPARTURE_TIME));
		final String departureTimeZoneId = this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DEPARTURE_TIME_ZONE_ID);
		if (StringUtils.isNotBlank(departureTimeZoneId))
		{
			target.setDepartureTimeZoneId(ZoneId.of(departureTimeZoneId));
		}

		target.setArrivalTime(this.<Date> getValue(source, TravelfacadesConstants.SOLR_FIELD_ARRIVAL_TIME));
		final String arrivalTimeZoneId = this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_ARRIVAL_TIME_ZONE_ID);
		if (StringUtils.isNotBlank(arrivalTimeZoneId))
		{
			target.setArrivalTimeZoneId(ZoneId.of(arrivalTimeZoneId));
		}

		target.setCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_CODE));

		final TerminalData originTerminalData = new TerminalData();
		originTerminalData.setCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_ORIGIN_TERMINAL_CODE));
		target.setOriginTerminal(originTerminalData);

		final TerminalData destinationTerminalData = new TerminalData();
		destinationTerminalData
				.setCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DESTINATION_TERMINAL_CODE));
		target.setDestinationTerminal(destinationTerminalData);

		final TransportFacilityData originFacilityData = new TransportFacilityData();
		originFacilityData.setCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_ORIGIN_TRANSPORT_FACILITY));
		originFacilityData
				.setName(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_ORIGIN_TRANSPORT_FACILITY_NAME));
		final LocationData originLocationData = new LocationData();
		originLocationData.setName(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_ORIGIN_LOCATION_NAME));

		final List<String> originLocationCodes = getValue(source, TravelfacadesConstants.SOLR_FIELD_ORIGIN_LOCATION_DATA);

		if (CollectionUtils.isNotEmpty(originLocationCodes))
		{
			originLocationData.setCode(originLocationCodes.get(2));
		}

		originFacilityData.setLocation(originLocationData);

		final TransportFacilityData destinationFacilityData = new TransportFacilityData();
		destinationFacilityData
				.setCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DESTINATION_TRANSPORT_FACILITY));
		destinationFacilityData
				.setName(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DESTINATION_TRANSPORT_FACILITY_NAME));
		final LocationData destinationLocationData = new LocationData();
		destinationLocationData
				.setName(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DESTINATION_LOCATION_NAME));

		final List<String> destinationLocationCodes = getValue(source, TravelfacadesConstants.SOLR_FIELD_DESTINATION_LOCATION_DATA);

		if (CollectionUtils.isNotEmpty(destinationLocationCodes))
		{
			destinationLocationData.setCode(destinationLocationCodes.get(2));
		}

		destinationFacilityData.setLocation(destinationLocationData);

		final TravelSectorData travelSectorData = new TravelSectorData();
		travelSectorData.setDestination(destinationFacilityData);
		travelSectorData.setOrigin(originFacilityData);
		travelSectorData.setCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_TRAVEL_SECTOR_CODE));

		target.setSector(travelSectorData);

		target.setOriginLocationCity(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_ORIGIN_LOCATION_CITY));
		target.setDestinationLocationCity(
				this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DESTINATION_LOCATION_CITY));

		target.setOriginLocationCountry(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_ORIGIN_LOCATION_COUNTRY));
		target.setDestinationLocationCountry(
				this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DESTINATION_LOCATION_COUNTRY));

		final TransportVehicleData vehicle = new TransportVehicleData();
		final TransportVehicleInfoData vehicleInfo = new TransportVehicleInfoData();
		vehicleInfo.setName(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_VEHICLE_INFORMATION_NAME));
		vehicleInfo.setCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_VEHICLE_INFORMATION_CODE));
		vehicle.setVehicleInfo(vehicleInfo);
		target.setTransportVehicle(vehicle);

		target.setStatus(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_STATUS));

		final String duration = this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DURATION);
		try
		{
			final Long durationLong = Long.parseLong(duration);
			target.setDurationValue(durationLong);
			target.setDuration(TransportOfferingUtils.getDurationMap(durationLong));
		}
		catch (final NumberFormatException ex)
		{
			LOG.error("Error when parsing duration of transport offering: " + target.getCode());
		}
	}

	/**
	 * Gets value.
	 *
	 * @param <T>
	 * 		the type parameter
	 * @param source
	 * 		the source
	 * @param propertyName
	 * 		the property name
	 * @return the value
	 */
	protected <T> T getValue(final SearchResultValueData source, final String propertyName)
	{
		if (source.getValues() == null)
		{
			return null;
		}
		return (T) source.getValues().get(propertyName);
	}

}
