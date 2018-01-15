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
package de.hybris.platform.travelservices.services;


import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;

import java.util.List;


/**
 * Extension of CategoryService with methods specific to Travel
 */
public interface TravelCategoryService extends CategoryService
{

	/**
	 * Retrieves a list of ancillary categories for given transportOfferings
	 *
	 * @param transportOfferingCodes
	 *           list of transport Offering codes
	 * @return list of relevant ancillary categories
	 */
	List<CategoryModel> getAncillaryCategories(List<String> transportOfferingCodes);

	/**
	 * Retrieves a list of accommodation categories for given transportOfferings
	 *
	 * @param transportOfferingCodes
	 * @return list of relevant accommodation categories
	 */
	List<CategoryModel> getAvailableAccommodationCategories(List<String> transportOfferingCodes);
}
