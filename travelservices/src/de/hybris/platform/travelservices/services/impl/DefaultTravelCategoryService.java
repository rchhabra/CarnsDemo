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

package de.hybris.platform.travelservices.services.impl;


import de.hybris.platform.category.impl.DefaultCategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelservices.dao.TravelCategoryDao;
import de.hybris.platform.travelservices.services.TravelCategoryService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of TravelCategoryService
 */
public class DefaultTravelCategoryService extends DefaultCategoryService implements TravelCategoryService
{

	private TravelCategoryDao travelCategoryDao;

	@Override
	public List<CategoryModel> getAncillaryCategories(final List<String> transportOfferingCodes)
	{
		ServicesUtil.validateParameterNotNull(transportOfferingCodes, "Transport Offering codes must not be null");

		return travelCategoryDao.getAncillaryCategories(transportOfferingCodes);
	}

	@Override
	public List<CategoryModel> getAvailableAccommodationCategories(final List<String> transportOfferingCodes)
	{
		ServicesUtil.validateParameterNotNull(transportOfferingCodes, "Transport Offering codes must not be null");
		return travelCategoryDao.getAccommodationCategories(transportOfferingCodes);
	}

	/**
	 * @return the travelCategoryDao
	 */
	protected TravelCategoryDao getTravelCategoryDao()
	{
		return travelCategoryDao;
	}

	/**
	 * @param travelCategoryDao
	 *           the travelCategoryDao to set
	 */
	@Required
	public void setTravelCategoryDao(final TravelCategoryDao travelCategoryDao)
	{
		this.travelCategoryDao = travelCategoryDao;
	}

}
