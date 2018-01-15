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


/**
 * Location hierarchy value provider for the transport offering origin/destination.
 */
public class TransportOfferingLocationHierarchyValueProvider extends AbstractPropertyFieldValueProvider
		implements FieldValueProvider
{
	private static String DELIMITER = "|";
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

		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
		final String locationHierarchy = getLocationHierarchy(transportFacilityModel);
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, locationHierarchy));
		}

		return fieldValues;
	}

	/**
	 * Gets location hierarchy.
	 *
	 * @param transportFacilityModel
	 * 		the transport facility model
	 * @return the location hierarchy
	 */
	protected String getLocationHierarchy(final TransportFacilityModel transportFacilityModel)
	{
		final StringBuilder locationHierarchy = new StringBuilder(getCountryCode(transportFacilityModel));
		locationHierarchy.append(DELIMITER);
		locationHierarchy.append(getCityCode(transportFacilityModel));
		locationHierarchy.append(DELIMITER);
		locationHierarchy.append(transportFacilityModel.getCode());
		return locationHierarchy.toString();
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
	 * Gets city code.
	 *
	 * @param origin
	 * 		the origin
	 * @return the city code
	 */
	protected String getCityCode(final TransportFacilityModel origin)
	{
		String cityCode = "";
		final LocationModel locationModel = origin.getLocation();
		if (LocationType.CITY.getCode().equalsIgnoreCase(locationModel.getLocationType().getCode()))
		{
			cityCode = locationModel.getCode();
		}
		return cityCode;
	}

	/**
	 * Gets country code.
	 *
	 * @param origin
	 * 		the origin
	 * @return the country code
	 */
	protected String getCountryCode(final TransportFacilityModel origin)
	{
		final String countryCode = "";
		final LocationModel originLocationModel = origin.getLocation();
		for (final LocationModel location : originLocationModel.getSuperlocations())
		{
			if (LocationType.COUNTRY.getCode().equalsIgnoreCase(location.getLocationType().getCode()))
			{
				return location.getCode();
			}
		}
		return countryCode;
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
