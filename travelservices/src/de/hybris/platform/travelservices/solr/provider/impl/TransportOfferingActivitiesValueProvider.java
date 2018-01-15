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
import de.hybris.platform.travelservices.model.travel.ActivityModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.cglib.core.CollectionUtils;


/**
 * The type Transport offering activities value provider.
 */
public class TransportOfferingActivitiesValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{

	private FieldNameProvider fieldNameProvider;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final Collection<FieldValue> fieldValues = new ArrayList<>();

		if (model instanceof TransportOfferingModel)
		{

			final TransportOfferingModel transportOffering = (TransportOfferingModel) model;

			final Collection<ActivityModel> activites = getActivites(transportOffering);

			if (!activites.isEmpty())
			{
				fieldValues.addAll(createFieldValue(activites, indexedProperty));
			}
		}

		return fieldValues;
	}

	/**
	 * Method responsible for getting a merged list of activities from TravelFacility and Location. The method also
	 * filters out any duplicated entries.
	 *
	 * @param transportOffering
	 * 		the transport offering
	 * @return the activites
	 */
	protected Collection<ActivityModel> getActivites(final TransportOfferingModel transportOffering)
	{
		final Collection<ActivityModel> mergedActivites = new ArrayList<>();
		mergedActivites.addAll(getTravelFacilityActivites(transportOffering));
		mergedActivites.addAll(getLocationActivites(transportOffering));

		return removeDuplicates(getTravelFacilityActivites(transportOffering), getLocationActivites(transportOffering));
	}

	/**
	 * Method responsible for returning a list of activities associated with TravelFacility
	 *
	 * @param transportOffering
	 * 		the transport offering
	 * @return the travel facility activites
	 */
	protected Collection<ActivityModel> getTravelFacilityActivites(final TransportOfferingModel transportOffering)
	{
		final Collection<ActivityModel> travelFacilityActivites = new ArrayList<>();

		if (!transportOffering.getTravelSector().getDestination().getActivity().isEmpty())
		{
			travelFacilityActivites.addAll(transportOffering.getTravelSector().getDestination().getActivity());
		}
		return travelFacilityActivites;
	}

	/**
	 * Method responsible for returning a list of activities associated with Location
	 *
	 * @param transportOffering
	 * 		the transport offering
	 * @return the location activites
	 */
	protected Collection<ActivityModel> getLocationActivites(final TransportOfferingModel transportOffering)
	{
		final Collection<ActivityModel> locationActivites = new ArrayList<>();

		final LocationModel destinationLocation = transportOffering.getTravelSector().getDestination().getLocation();
		if (destinationLocation != null && !destinationLocation.getActivity().isEmpty())
		{
			locationActivites.addAll(destinationLocation.getActivity());
		}
		return locationActivites;
	}

	/**
	 * Method responsible for removing any duplicate entries.
	 *
	 * @param transportFacilityActivites
	 * 		the transport facility activites
	 * @param locationActivites
	 * 		the location activites
	 * @return the collection
	 */
	protected Collection<ActivityModel> removeDuplicates(final Collection<ActivityModel> transportFacilityActivites,
			final Collection<ActivityModel> locationActivites)
	{
		final Collection<ActivityModel> uniqueActivities = new ArrayList<>();

		CollectionUtils.filter(transportFacilityActivites, obj -> {
			final ActivityModel transportFacilityActivity = (ActivityModel) obj;

			for (final ActivityModel locationActivity : locationActivites)
			{
				if (locationActivity.getCode().equalsIgnoreCase(transportFacilityActivity.getCode()))
				{
					return true;
				}
			}

			uniqueActivities.add(transportFacilityActivity);

			return false;
		});

		uniqueActivities.addAll(locationActivites);

		return uniqueActivities;
	}

	/**
	 * Method takes a list of Activities and returns a list of Field Values
	 *
	 * @param activities
	 * 		the activities
	 * @param indexedProperty
	 * 		the indexed property
	 * @return the list
	 */
	protected List<FieldValue> createFieldValue(final Collection<ActivityModel> activities, final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);

		for (final ActivityModel activity : activities)
		{
			addFieldValues(fieldValues, fieldNames, activity.getCode());
		}

		return fieldValues;
	}

	/**
	 * Method responsible for creating a Field Value based of the provided Activity
	 *
	 * @param fieldValues
	 * 		the field values
	 * @param fieldNames
	 * 		the field names
	 * @param value
	 * 		the value
	 */
	protected void addFieldValues(final List<FieldValue> fieldValues, final Collection<String> fieldNames, final Object value)
	{
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
	}

	/**
	 * Gets field name provider.
	 *
	 * @return the field name provider
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
	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

}
