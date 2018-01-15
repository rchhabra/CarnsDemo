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

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.AccommodationDao;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link AccommodationDao}
 */
public class DefaultAccommodationDao extends DefaultGenericDao<AccommodationModel> implements AccommodationDao
{

	private static final String ACCOMMODATION_OFFERING_CODE = "accommodationOfferingCode";

	private static final String FIND_BY_ACCOMMODATION_OFFERING_CODE = "SELECT DISTINCT {a." + AccommodationModel.PK + "} FROM {"
			+ AccommodationModel._TYPECODE + " AS a JOIN " + StockLevelModel._TYPECODE + " AS sl ON {sl."
			+ StockLevelModel.PRODUCTCODE + "} = {a." + AccommodationModel.CODE + "} JOIN " + AccommodationOfferingModel._TYPECODE
			+ " AS ao ON {sl." + StockLevelModel.WAREHOUSE + "} = {ao." + AccommodationOfferingModel.PK + "} } WHERE {ao."
			+ AccommodationOfferingModel.CODE + "} = ?accommodationOfferingCode";

	private static final String FIND_BY_ACCOMMODATION_OFFERING_CODE_AND_ACCOMMODATION_CODE = "SELECT DISTINCT {a."
			+ AccommodationModel.PK + "} FROM {" + AccommodationModel._TYPECODE + " AS a JOIN " + StockLevelModel._TYPECODE
			+ " AS sl ON {sl." + StockLevelModel.PRODUCTCODE + "} = {a." + AccommodationModel.CODE + "} JOIN "
			+ AccommodationOfferingModel._TYPECODE + " AS ao ON {sl." + StockLevelModel.WAREHOUSE + "} = {ao."
			+ AccommodationOfferingModel.PK + "} } WHERE {ao." + AccommodationOfferingModel.CODE
			+ "}  = ?" + ACCOMMODATION_OFFERING_CODE + " and {a." + AccommodationModel.CODE + "} = ?" + AccommodationModel.CODE;

	/**
	 * @param typecode
	 */
	public DefaultAccommodationDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<AccommodationModel> findAccommodationForAccommodationOffering(final String accommodationOfferingCode)
	{

		validateParameterNotNull(accommodationOfferingCode, "AccommodationOfferingCode must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(ACCOMMODATION_OFFERING_CODE, accommodationOfferingCode);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BY_ACCOMMODATION_OFFERING_CODE, params);
		final SearchResult<AccommodationModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);

		if (searchResult != null)
		{
			return searchResult.getResult();
		}

		return Collections.emptyList();
	}

	@Override
	public AccommodationModel findAccommodationForAccommodationOffering(final String accommodationOfferingCode,
			final String accommodationCode)
	{
		validateParameterNotNull(accommodationOfferingCode, "AccommodationOfferingCode must not be null!");
		validateParameterNotNull(accommodationCode, "AccommodationCodes must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(ACCOMMODATION_OFFERING_CODE, accommodationOfferingCode);
		params.put(AccommodationModel.CODE, accommodationCode);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				FIND_BY_ACCOMMODATION_OFFERING_CODE_AND_ACCOMMODATION_CODE, params);

		return getFlexibleSearchService().searchUnique(flexibleSearchQuery);
	}

}
