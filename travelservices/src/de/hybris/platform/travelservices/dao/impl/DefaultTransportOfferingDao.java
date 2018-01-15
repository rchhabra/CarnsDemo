/*
 *
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
import de.hybris.platform.travelservices.dao.TransportOfferingDao;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation of the DAO on Transport Offering model objects. Default implementation of the
 * {@link de.hybris.platform.travelservices.dao.TransportOfferingDao} interface.
 */
public class DefaultTransportOfferingDao extends DefaultGenericDao<TransportOfferingModel> implements TransportOfferingDao
{
	private static final String FIND_BY_NUMBER_AND_DEPARTURE_DATE =
			"SELECT {to." + TransportOfferingModel.PK + "} FROM {" + TransportOfferingModel._TYPECODE + " AS to JOIN "
					+ TravelProviderModel._TYPECODE + " AS tp ON {to." + TransportOfferingModel.TRAVELPROVIDER + "}={tp."
					+ TravelProviderModel.PK + "}} WHERE CONCAT ( {tp." + TravelProviderModel.CODE + "}, {to."
					+ TransportOfferingModel.NUMBER + "}) = ?number AND {to." + TransportOfferingModel.DEPARTURETIME
					+ "} BETWEEN ?startDate AND ?endDate";

	private static final String FIND_BY_CODES =
			"SELECT {" + TransportOfferingModel.PK + "} FROM {" + TransportOfferingModel._TYPECODE + "} WHERE {"
					+ TransportOfferingModel.CODE + "} IN (?transportOfferingCodes)";

	/**
	 * @param typecode
	 */
	public DefaultTransportOfferingDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<TransportOfferingModel> findTransportOfferings(final String number, final Date departureDate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("number", number);

		final Calendar startDate = new GregorianCalendar();
		startDate.setTime(departureDate);
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		params.put("startDate", startDate.getTime());

		final Calendar endDate = new GregorianCalendar();
		endDate.setTime(departureDate);
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		params.put("endDate", endDate.getTime());

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BY_NUMBER_AND_DEPARTURE_DATE, params);
		final SearchResult<TransportOfferingModel> results = getFlexibleSearchService().search(flexibleSearchQuery);
		return results.getResult();
	}

	/**
	 * This method to retrieve all transportOfferings.
	 */
	@Override
	public List<TransportOfferingModel> findTransportOfferings()
	{
		return find();
	}

	/**
	 * This method to retrieve transportOffering for a given code.
	 */
	@Override
	public TransportOfferingModel findTransportOffering(final String code)
			throws AmbiguousIdentifierException, ModelNotFoundException
	{

		validateParameterNotNull(code, "Transport Offering Code must not be null!");

		final List<TransportOfferingModel> transportOfferings = find(
				Collections.singletonMap(TransportOfferingModel.CODE, (Object) code));

		if (CollectionUtils.isEmpty(transportOfferings))
		{
			throw new ModelNotFoundException("No result for the given query");
		}

		if (transportOfferings.size() > 1)
		{
			throw new AmbiguousIdentifierException("Found " + transportOfferings.size() + " results for the given query");
		}

		return transportOfferings.get(0);
	}

	@Override
	public List<TransportOfferingModel> getTransportOfferings(final Collection<String> codes)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(FIND_BY_CODES);
		fQuery.addQueryParameter("transportOfferingCodes", codes);
		final SearchResult<TransportOfferingModel> searchResult = getFlexibleSearchService().search(fQuery);
		return searchResult.getResult();
	}


}
