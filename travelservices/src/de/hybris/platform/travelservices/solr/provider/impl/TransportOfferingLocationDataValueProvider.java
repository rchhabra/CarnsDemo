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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Location information value provider for the transport offering origin/destination.
 */
public class TransportOfferingLocationDataValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{

	private static String LOCATION_ORIGIN = "ORIGIN";
	private static String LOCATION_DESTINATION = "DESTINATION";

	private FieldNameProvider fieldNameProvider;
	private String locationOption;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final Collection<FieldValue> fieldValues = new ArrayList<>();
		if (!(model instanceof TransportOfferingModel))
		{
			return fieldValues;
		}

		final TransportOfferingModel transportOffering = (TransportOfferingModel) model;

		final TransportFacilityModel transportFacilityModel = getTransportFacility(transportOffering);
		if (transportFacilityModel == null)
		{
			return fieldValues;
		}
		final List<String> locationInfoList = new ArrayList<String>();
		addTransportFacilityInformation(transportFacilityModel, locationInfoList);
		addCityInformation(transportFacilityModel, locationInfoList);
		addCountryInformation(transportFacilityModel, locationInfoList);

		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			for (final String location : locationInfoList)
			{
				fieldValues.add(new FieldValue(fieldName, location));
			}
		}

		return fieldValues;
	}

	/**
	 * Add transport facility information.
	 *
	 * @param transportFacility
	 * 		the transport facility
	 * @param locationInfoList
	 * 		the location info list
	 */
	protected void addTransportFacilityInformation(final TransportFacilityModel transportFacility,
			final List<String> locationInfoList)
	{
		locationInfoList.add(transportFacility.getCode());
		locationInfoList.add(transportFacility.getName());
	}

	/**
	 * Add city information.
	 *
	 * @param transportFacility
	 * 		the transport facility
	 * @param locationInfoList
	 * 		the location info list
	 */
	protected void addCityInformation(final TransportFacilityModel transportFacility, final List<String> locationInfoList)
	{
		LocationModel locationCity = transportFacility.getLocation();
		if (!LocationType.CITY.getCode().equalsIgnoreCase(locationCity.getLocationType().getCode()))
		{
			for (final LocationModel location : locationCity.getSuperlocations())
			{
				if (LocationType.CITY.getCode().equalsIgnoreCase(location.getLocationType().getCode()))
				{
					locationCity = location;
					break;
				}
			}
		}
		locationInfoList.add(locationCity.getCode());
		locationInfoList.add(locationCity.getName());
	}

	/**
	 * Add country information.
	 *
	 * @param transportFacility
	 * 		the transport facility
	 * @param locationInfoList
	 * 		the location info list
	 */
	protected void addCountryInformation(final TransportFacilityModel transportFacility, final List<String> locationInfoList)
	{
		LocationModel locationCountry = null;
		for (final LocationModel location : transportFacility.getLocation().getSuperlocations())
		{
			if (LocationType.COUNTRY.getCode().equalsIgnoreCase(location.getLocationType().getCode()))
			{
				locationCountry = location;
				break;
			}
		}
		if (locationCountry != null)
		{
			locationInfoList.add(locationCountry.getCode());
			locationInfoList.add(locationCountry.getName());
		}
	}

	/**
	 * Gets transport facility.
	 *
	 * @param transportOffering
	 * 		the transport offering
	 * @return the transport facility
	 */
	protected TransportFacilityModel getTransportFacility(final TransportOfferingModel transportOffering)
	{
		if (transportOffering.getTravelSector() == null)
		{
			return null;
		}

		TransportFacilityModel transportFacilityModel = null;
		if (LOCATION_ORIGIN.equalsIgnoreCase(getLocationOption()))
		{
			transportFacilityModel = transportOffering.getTravelSector().getOrigin();
		}
		else if (LOCATION_DESTINATION.equalsIgnoreCase(getLocationOption()))
		{
			transportFacilityModel = transportOffering.getTravelSector().getDestination();
		}
		return transportFacilityModel;
	}

	/**
	 * Gets field name provider.
	 *
	 * @return field name provider
	 */
	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	/**
	 * Sets field name provider.
	 *
	 * @param fieldNameProvider
	 * 		the field name provider
	 */
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	/**
	 * Gets location option.
	 *
	 * @return location option
	 */
	protected String getLocationOption()
	{
		return locationOption;
	}

	/**
	 * Sets location option.
	 *
	 * @param locationOption
	 * 		the location option
	 */
	public void setLocationOption(final String locationOption)
	{
		this.locationOption = locationOption;
	}

}
