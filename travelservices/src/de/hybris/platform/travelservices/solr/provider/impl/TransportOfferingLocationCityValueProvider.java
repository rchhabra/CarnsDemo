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

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportFacilityService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


/**
 * Location city value provider for origin/destination of the transport offering
 */
public class TransportOfferingLocationCityValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{
	private FieldNameProvider fieldNameProvider;
	private CommonI18NService commonI18NService;
	private String locationOption;

	private TransportFacilityService transportFacilityService;

	private static String LOCATION_ORIGIN = "ORIGIN";
	private static String LOCATION_DESTINATION = "DESTINATION";

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{

		final Collection<FieldValue> fieldValues = new ArrayList<>();
		if (!(model instanceof TransportOfferingModel))
		{
			return fieldValues;
		}

		final TransportFacilityModel transportFacilityModel = getTransportFacility((TransportOfferingModel) model);
		final LocationModel locationModel = getTransportFacilityService().getCity(transportFacilityModel);

		if (locationModel == null)
		{
			return fieldValues;
		}

		if (indexedProperty.isLocalized())
		{
			final Collection<LanguageModel> languages = indexConfig.getLanguages();
			for (final LanguageModel language : languages)
			{
				fieldValues.addAll(createFieldValue(locationModel, language, indexedProperty));
			}
		}
		return fieldValues;
	}

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
	 * The method gets List of field names and values for solr indexing of city field
	 *
	 * @param location
	 * @param indexedProperty
	 * @return List<FieldValue>
	 */
	protected List<FieldValue> createFieldValue(final LocationModel location, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{

		final List<FieldValue> fieldValues = new ArrayList<>();
		final Locale locale = i18nService.getCurrentLocale();
		try
		{
			i18nService.setCurrentLocale(getCommonI18NService().getLocaleForLanguage(language));

		}
		finally
		{
			i18nService.setCurrentLocale(locale);
		}

		final String value = location.getName(locale);

		// Find field names for the location city field
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, language.getIsocode());
		String fieldName;
		for (final Iterator<String> iterator = fieldNames.iterator(); iterator.hasNext(); fieldValues
				.add(new FieldValue(fieldName, value)))
		{
			fieldName = iterator.next();
		}
		return fieldValues;
	}


	/**
	 * @return the fieldNameProvider
	 */
	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	/**
	 * @param fieldNameProvider
	 *           the fieldNameProvider to set
	 */
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the location option
	 */
	protected String getLocationOption()
	{
		return locationOption;
	}

	/**
	 * @param locationOption
	 */
	public void setLocationOption(final String locationOption)
	{
		this.locationOption = locationOption;
	}

	protected TransportFacilityService getTransportFacilityService()
	{
		return transportFacilityService;
	}

	@Required
	public void setTransportFacilityService(final TransportFacilityService transportFacilityService)
	{
		this.transportFacilityService = transportFacilityService;
	}

}
