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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;


/**
 * PagedAccommodationOfferingCustomerReviewDao interface which provides functionality to fetch pageable CustomerReview.
 */
public interface PagedAccommodationOfferingCustomerReviewDao
{

	/**
	 * Returns the SearchPageData corresponding to the given AccommodaionOffering's code.
	 *
	 * @param accommodationOfferingCode
	 * 		the accommodation offering code
	 * @param pageableData
	 * 		the pageable data
	 * @return the SearchPageData having list of CustomerReviewModel
	 */
	SearchPageData<CustomerReviewModel> findPagedCustomerReview(String accommodationOfferingCode,
			PageableData pageableData);
}
