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
package de.hybris.platform.travelservices.price;

import de.hybris.platform.europe1.jalo.PDTRowsQueryBuilder;

import java.util.List;
import java.util.Map;


/**
 * Extends the {@link PDTRowsQueryBuilder} to add methods specific to travel.
 */
public interface TravelPDTRowsQueryBuilder extends PDTRowsQueryBuilder
{
	/**
	 * The method builds the query for querying price rows. The travel specific search criteria is added to the query
	 * along with the required parameters.
	 *
	 * @param searchParams
	 * 		the search params
	 * @return the queryWithParams object
	 */
	QueryWithParams buildPriceQueryAndParams(Map<String, String> searchParams);

	/**
	 * The method builds the query for querying price rows. The travel specific search criteria is added to the query
	 * along with the required parameters.
	 *
	 * @param searchParams
	 * 		the search params
	 * @return the queryWithParams object
	 */
	QueryWithParams buildTaxQueryAndParams(Map<String, List<String>> searchParams);
}
