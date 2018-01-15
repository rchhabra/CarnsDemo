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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.daos.impl.DefaultProductDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.AccommodationExtrasDao;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default implementation of the {@link AccommodationExtrasDao} interface.
 */
public class DefaultAccommodationExtrasDao extends DefaultProductDao implements AccommodationExtrasDao
{
	private static final String ACCOMMODATION_OFFERING_CODE = "accommodationOfferingCode";

	private static final String FIND_BY_ACCOMMODATION_OFFERING_CODE = "SELECT DISTINCT {p." + ProductModel.PK + "} FROM {"
			+ ProductModel._TYPECODE + " AS p JOIN " + StockLevelModel._TYPECODE + " AS sl ON {sl."
			+ StockLevelModel.PRODUCTCODE
			+ "} = {p." + ProductModel.CODE + "} JOIN " + AccommodationOfferingModel._TYPECODE + " AS ao ON {sl."
			+ StockLevelModel.WAREHOUSE + "} = {ao." + AccommodationOfferingModel.PK + "} JOIN " + TypeModel._TYPECODE
			+ " AS t ON {p." + ProductModel.ITEMTYPE + "} = {t." + TypeModel.PK + "} } WHERE {t." + TypeModel.CODE + "} != '"
			+ AccommodationModel._TYPECODE + "' AND {t." + TypeModel.CODE + "} != '" + RoomRateProductModel._TYPECODE + "' AND {ao."
			+ AccommodationOfferingModel.CODE + "} = ?accommodationOfferingCode";

	/**
	 * @param typecode
	 */
	public DefaultAccommodationExtrasDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<ProductModel> findExtras(final String accommodationOfferingCode)
	{
		validateParameterNotNull(accommodationOfferingCode, "AccommodationOfferingCode must not be null!");

		final Map<String, Object> params = new HashMap<>();
		params.put(ACCOMMODATION_OFFERING_CODE, accommodationOfferingCode);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BY_ACCOMMODATION_OFFERING_CODE, params);
		final SearchResult<ProductModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);

		if (searchResult != null)
		{
			return searchResult.getResult();
		}

		return Collections.emptyList();
	}

}