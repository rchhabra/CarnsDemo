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
package de.hybris.platform.travelservices.dao;


import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.model.CategoryModel;

import java.util.List;


/**
 * The interface Travel category dao.
 */
public interface TravelCategoryDao extends CategoryDao
{

	/**
	 * Gets ancillary categories.
	 *
	 * @param transportOfferingsCodes
	 *           the transport offerings codes
	 * @return the ancillary categories
	 */
	List<CategoryModel> getAncillaryCategories(List<String> transportOfferingsCodes);

	/**
	 *
	 * @param transportOfferingCodes
	 * @return
	 */
	List<CategoryModel> getAccommodationCategories(List<String> transportOfferingCodes);
}
