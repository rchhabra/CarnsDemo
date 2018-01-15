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

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.AccommodationCustomerReviewDao;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


public class DefaultAccommodationCustomerReviewDao extends DefaultGenericDao<CustomerReviewModel>
		implements AccommodationCustomerReviewDao
{

	private static final String FIND_CUSTOMER_REVIEW_QUERY = "SELECT {cr." + CustomerReviewModel.PK + "} from" + " {"
			+ CustomerReviewModel._TYPECODE + " AS cr JOIN " + AccommodationOfferingModel._TYPECODE + " AS ao ON {ao:"
			+ AccommodationOfferingModel.PK + "}={cr:" + CustomerReviewModel.ACCOMMODATIONOFFERING + "} JOIN " + UserModel._TYPECODE
			+ " as u  ON {cr:" + CustomerReviewModel.USER + "}={u:" + UserModel.PK
			+ "}}  WHERE {" + "ao:" + AccommodationOfferingModel.CODE + "} = ?accommodationOfferingCode" + " AND {cr:"
			+ CustomerReviewModel.BOOKINGREFERENCE + "} = ?bookingReference" + " AND {cr:" + CustomerReviewModel.USER + "}=?user";

	/**
	 * @param typecode
	 */
	public DefaultAccommodationCustomerReviewDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<CustomerReviewModel> getCustomerReviewsForBooking(final String bookingReference, final UserModel user,
			final String accommodationOfferingCode) throws ModelNotFoundException
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("bookingReference", bookingReference);
		params.put("user", user);
		params.put("accommodationOfferingCode", accommodationOfferingCode);
		final String query = FIND_CUSTOMER_REVIEW_QUERY;
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query, params);
		final SearchResult<CustomerReviewModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
		final List<CustomerReviewModel> foundReviews = searchResult.getResult();
		if (CollectionUtils.isEmpty(foundReviews))
		{
			throw new ModelNotFoundException("No result for the given query");
		}

		return foundReviews;
	}

}
