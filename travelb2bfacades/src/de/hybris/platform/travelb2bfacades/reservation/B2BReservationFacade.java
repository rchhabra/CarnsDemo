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
 */
package de.hybris.platform.travelb2bfacades.reservation;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.travelb2bfacades.data.B2BReservationData;

import java.util.Date;
import java.util.List;


/**
 * Interface for B2B Reservation Facade.
 */
public interface B2BReservationFacade
{
	/**
	 * Retrieves a certain number of reservation based on the parameter passed in the method. b2bUnitCode is         mandatory,
	 * everything else can be passed as null
	 *
	 * @param searchState
	 * 		the search state
	 * @param pageableData
	 * 		the pageableData
	 * @param unitCode
	 * 		the b2b unit code
	 * @param email
	 * 		the email
	 * @param fromDate
	 * 		the from date
	 * @param toDate
	 * 		the to date
	 * @param costCenterUid
	 * 		the cost center
	 * @param currency
	 * 		the currency
	 *
	 * @return SearchPageData the search results
	 * @deprecated Deprecated since version 3.0. Please use
	 * {@link #findReservations(SearchStateData, PageableData, List, String, Date, Date, String, String)}
	 */
	@Deprecated
	SearchPageData<B2BReservationData> findReservations(SearchStateData searchState, PageableData pageableData, String unitCode,
			String email, Date fromDate, Date toDate, String costCenterUid, String currency);


	/**
	 * Retrieves a certain number of reservation based on the parameter passed in the method. b2bUnitCode is         mandatory,
	 * everything else can be passed as null
	 *
	 * @param searchState
	 * 		the search state
	 * @param pageableData
	 * 		the pageableData
	 * @param unitCodes
	 * 		the b2b unit code
	 * @param email
	 * 		the email
	 * @param fromDate
	 * 		the from date
	 * @param toDate
	 * 		the to date
	 * @param costCenterUid
	 * 		the cost center
	 * @param currency
	 * 		the currency
	 *
	 * @return SearchPageData the search results
	 */
	SearchPageData<B2BReservationData> findReservations(SearchStateData searchState, PageableData pageableData,
			List<String> unitCodes,
			String email, Date fromDate, Date toDate, String costCenterUid, String currency);

	/**
	 * Check if fromDate is before toDate.
	 *
	 * @param fromDate
	 * 		the date to compare
	 * @param toDate
	 * 		the date to be compared with
	 *
	 * @return true if fromDate is before toDate, false otherwise.
	 */
	boolean checkDatesInterval(final Date fromDate, final Date toDate);

	/**
	 * retrieves the default unit for the current user
	 *
	 * @return the default unit
	 */
	String getDefaultUnit();

	/**
	 * Validate unit boolean.
	 *
	 * @param unitCode
	 * 		the unit code validates a certain unit against the current user to make sure he can access the
	 * 		related informations
	 *
	 * @return the boolean
	 */
	boolean validateUnit(final String unitCode);

	/**
	 * Returns the total price for all the bookings matching the search parameters. unitId is mandatory,         everything else
	 * can be passed as null
	 *
	 * @param unitCode
	 * 		the b2b unit code
	 * @param email
	 * 		the email
	 * @param fromDate
	 * 		the from date
	 * @param toDate
	 * 		the to date
	 * @param costCenter
	 * 		the cost center
	 * @param currencyIso
	 * 		the currency iso code
	 *
	 * @return SearchPageData the search results
	 * @deprecated Deprecated since version 3.0. Please use {@link #findTotal(List, String, Date, Date, String, String)}
	 */
	@Deprecated
	PriceData findTotal(String unitCode, String email, Date fromDate, Date toDate, String costCenter, String currencyIso);

	/**
	 * Returns the total price for all the bookings matching the search parameters. unitId is mandatory,         everything else
	 * can be passed as null
	 *
	 * @param unitCodes
	 * 		the b2b unit code
	 * @param email
	 * 		the email
	 * @param fromDate
	 * 		the from date
	 * @param toDate
	 * 		the to date
	 * @param costCenter
	 * 		the cost center
	 * @param currencyIso
	 * 		the currency iso code
	 *
	 * @return SearchPageData the search results
	 */
	PriceData findTotal(List<String> unitCodes, String email, Date fromDate, Date toDate, String costCenter, String currencyIso);
}
