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

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;
import de.hybris.platform.travelservices.services.PropertyFacilityService;


public class AmenityFacetValueDisplayNameProvider implements FacetValueDisplayNameProvider
{

	private PropertyFacilityService propertyFacilityService;

	@Override
	public String getDisplayName(final SearchQuery query, final IndexedProperty indexedProperty, final String amenityCode)
	{
		final PropertyFacilityModel pfModel = getPropertyFacilityService().getPropertyFacility(amenityCode);
		if (pfModel != null)
		{
			return pfModel.getShortDescription();
		}
		return amenityCode;
	}

	/**
	 * @return the propertyFacilityService
	 */
	protected PropertyFacilityService getPropertyFacilityService()
	{
		return propertyFacilityService;
	}

	/**
	 * @param propertyFacilityService
	 *           the propertyFacilityService to set
	 */
	public void setPropertyFacilityService(final PropertyFacilityService propertyFacilityService)
	{
		this.propertyFacilityService = propertyFacilityService;
	}

}
