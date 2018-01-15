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
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Value provider for the transport offering destination to populate all origins from routes.
 */
public class TransportOfferingAllOriginsToDestinationValueProvider extends AbstractPropertyFieldValueProvider
		implements FieldValueProvider
{

	private FieldNameProvider fieldNameProvider;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final Collection<FieldValue> fieldValues = new ArrayList<>();
		if (!(model instanceof TransportOfferingModel))
		{
			return fieldValues;
		}

		final List<String> allOriginsList = new ArrayList<String>();
		final TransportOfferingModel transportOffering = (TransportOfferingModel) model;

		addAllOriginsFromRoutes(transportOffering.getTravelSector(), allOriginsList);

		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			for (final String location : allOriginsList)
			{
				fieldValues.add(new FieldValue(fieldName, location));
			}
		}

		return fieldValues;
	}

	/**
	 * Add all origins from routes.
	 *
	 * @param travelSector
	 * 		the travel sector
	 * @param allOriginsToDestinationList
	 * 		the all origins to destination list
	 */
	protected void addAllOriginsFromRoutes(final TravelSectorModel travelSector, final List<String> allOriginsToDestinationList)
	{
		final String sectorDestinationCode = travelSector.getDestination().getCode();
		for (final TravelRouteModel travelRoute : travelSector.getTravelRoute())
		{
			if (sectorDestinationCode.equalsIgnoreCase(travelRoute.getDestination().getCode()))
			{
				allOriginsToDestinationList.add(travelRoute.getOrigin().getCode());
				allOriginsToDestinationList.add(travelRoute.getOrigin().getLocation().getCode());
			}
		}
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

}
