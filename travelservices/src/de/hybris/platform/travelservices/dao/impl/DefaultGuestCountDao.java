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

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.GuestCountDao;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link GuestCountDao}
 */
public class DefaultGuestCountDao extends DefaultGenericDao<GuestCountModel> implements GuestCountDao
{
	private static final String PASSENGER_TYPE_CODE = "passengerTypeCode";

	private static final String FIND_BY_PASSENGER_TYPE_CODE_AND_QUANTITY = "SELECT {gc." + GuestCountModel.PK + "} FROM {"
			+ GuestCountModel._TYPECODE + " AS gc JOIN " + PassengerTypeModel._TYPECODE + " AS pt ON {gc."
			+ GuestCountModel.PASSENGERTYPE + "} = {pt." + PassengerTypeModel.PK + "} } " + "WHERE {pt." + PassengerTypeModel.CODE
			+ "} = ?" + PASSENGER_TYPE_CODE + " AND {gc." + GuestCountModel.QUANTITY + "} = ?" + GuestCountModel.QUANTITY;

	/**
	 * @param typecode
	 */
	public DefaultGuestCountDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public GuestCountModel findGuestCount(final String passengerTypeCode, final int quantity)
	{
		validateParameterNotNull(passengerTypeCode, "PassengerTypeCode must not be null!");
		validateParameterNotNull(quantity, "Quantity must not be null!");

		final Map<String, Object> params = new HashMap<>();
		params.put(PASSENGER_TYPE_CODE, passengerTypeCode);
		params.put(GuestCountModel.QUANTITY, quantity);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BY_PASSENGER_TYPE_CODE_AND_QUANTITY, params);
		final SearchResult<GuestCountModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);

		final Optional<GuestCountModel> guestCountModel = CollectionUtils.isNotEmpty(searchResult.getResult())
				? searchResult.getResult().stream().findFirst() : null;

		return Objects.nonNull(guestCountModel) && guestCountModel.isPresent() ? guestCountModel.get() : null;
	}

}
