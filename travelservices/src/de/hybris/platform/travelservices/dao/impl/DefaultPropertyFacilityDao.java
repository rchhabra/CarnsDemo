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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.PropertyFacilityDao;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation of the DAO on Property Facility model objects. Default implementation of the
 * {@link de.hybris.platform.travelservices.dao.PropertyFacilityDao} interface.
 */
public class DefaultPropertyFacilityDao extends DefaultGenericDao<PropertyFacilityModel> implements PropertyFacilityDao
{
	private static final String FIND_PROPERTY_FALICILITIES = "SELECT {pf." + PropertyFacilityModel.PK + "} FROM {"
			+ PropertyFacilityModel._TYPECODE + " AS pf}";

	/**
	 * @param typecode
	 */
	public DefaultPropertyFacilityDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<PropertyFacilityModel> findPropertyFacilities()
	{
		return find();
	}

	@Override
	public SearchResult<PropertyFacilityModel> findPropertyFacilities(final int batchSize, final int offset)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_PROPERTY_FALICILITIES);
		flexibleSearchQuery.setCount(batchSize);
		flexibleSearchQuery.setStart(offset);
		flexibleSearchQuery.setNeedTotal(true);

		final SearchResult<PropertyFacilityModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);

		if (searchResult != null)
		{
			return searchResult;
		}

		return null;
	}

	@Override
	public PropertyFacilityModel findPropertyFacility(final String code)
	{
		validateParameterNotNull(code, "Property Facility Code must not be null!");

		final List<PropertyFacilityModel> propertyFacilities = find(
				Collections.singletonMap(PropertyFacilityModel.CODE, (Object) code));
		if (CollectionUtils.isEmpty(propertyFacilities))
		{
			throw new ModelNotFoundException("No result for the given query");
		}
		else if (propertyFacilities.size() > 1)
		{
			throw new AmbiguousIdentifierException("Found " + propertyFacilities.size() + " results for the given query");
		}

		final Optional<PropertyFacilityModel> propertyFacilityModel = propertyFacilities.stream().findFirst();
		return propertyFacilityModel.orElse(null);
	}

}
