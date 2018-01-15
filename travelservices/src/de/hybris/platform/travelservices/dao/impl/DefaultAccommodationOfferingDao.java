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
 *
 */

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.AccommodationOfferingDao;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation of the DAO on Accommodation Offering model objects. Default implementation of the
 * {@link de.hybris.platform.travelservices.dao.AccommodationOfferingDao} interface.
 */
public class DefaultAccommodationOfferingDao extends DefaultGenericDao<AccommodationOfferingModel>
		implements AccommodationOfferingDao
{
	private static final String FIND_ACCOMMODATION_OFFERINGS = "SELECT {ao." + AccommodationOfferingModel.PK + "} FROM {"
			+ AccommodationOfferingModel._TYPECODE + " AS ao}";

	/**
	 * @param typecode
	 */
	public DefaultAccommodationOfferingDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<AccommodationOfferingModel> findAccommodationOfferings()
	{
		return find();
	}

	@Override
	public SearchResult<AccommodationOfferingModel> findAccommodationOfferings(final int batchSize, final int offset)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_ACCOMMODATION_OFFERINGS);
		flexibleSearchQuery.setCount(batchSize);
		flexibleSearchQuery.setStart(offset);
		flexibleSearchQuery.setNeedTotal(true);

		final SearchResult<AccommodationOfferingModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);

		if (searchResult != null)
		{
			return searchResult;
		}

		return null;
	}

	@Override
	public AccommodationOfferingModel findAccommodationOffering(final String code)
	{
		validateParameterNotNull(code, "Accommodation Offering Code must not be null!");

		final List<AccommodationOfferingModel> accommodationOfferings = find(
				Collections.singletonMap(AccommodationOfferingModel.CODE, (Object) code));
		if (CollectionUtils.isEmpty(accommodationOfferings))
		{
			throw new ModelNotFoundException("No result for the given query");
		}
		else if (accommodationOfferings.size() > 1)
		{
			throw new AmbiguousIdentifierException("Found " + accommodationOfferings.size() + " results for the given query");
		}
		final Optional<AccommodationOfferingModel> accommodationOfferingModel = accommodationOfferings.stream().findFirst();
		return accommodationOfferingModel.orElse(null);
	}

}
