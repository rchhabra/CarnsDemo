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

import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.travelservices.dao.PagedAccommodationOfferingCustomerReviewDao;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultPagedAccommodationOfferingCustomerReviewDao extends DefaultPagedGenericDao<WorkflowActionModel>
		implements PagedAccommodationOfferingCustomerReviewDao
{
	private static final String SORT_BY_DATE =
			" ORDER BY {" + CustomerReviewModel.CREATIONTIME + "} DESC, {" + WorkflowActionModel.PK + "}";

	private static final String SORT_CODE = "byDate";
	private static final String ACCOMMODATION_OFFERING_CODE = "accommodationOfferingCode";

	private static final String FIND_CUSTOMER_REVIEW_BY_ACCOMMODATIONOFFERING =
			"SELECT {cr." + CustomerReviewModel.PK + "} from" + " {" + CustomerReviewModel._TYPECODE + " AS cr JOIN "
					+ AccommodationOfferingModel._TYPECODE + " AS ao ON {ao:" + AccommodationOfferingModel.PK + "}={cr:"
					+ CustomerReviewModel.ACCOMMODATIONOFFERING + " }}  WHERE {" + "ao:" + AccommodationOfferingModel.CODE
					+ "} = ?accommodationOfferingCode";

	public DefaultPagedAccommodationOfferingCustomerReviewDao(final String typeCode)
	{
		super(typeCode);
	}

	@Override
	public SearchPageData<CustomerReviewModel> findPagedCustomerReview(final String accommodationOfferingCode,
			final PageableData pageableData)
	{
		validateParameterNotNull(accommodationOfferingCode, "Accommodation offering code must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put(ACCOMMODATION_OFFERING_CODE, accommodationOfferingCode);
		final List<SortQueryData> sortQueries = Arrays
				.asList(createSortQueryData(SORT_CODE, FIND_CUSTOMER_REVIEW_BY_ACCOMMODATIONOFFERING + SORT_BY_DATE));

		return getPagedFlexibleSearchService().search(sortQueries, SORT_CODE, queryParams, pageableData);
	}

}
