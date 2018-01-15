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

import de.hybris.platform.travelservices.dao.CabinClassDao;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.services.CabinClassService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Class is responsible for providing concrete implementation of the CabinClassService interface. The class uses the
 * CabinClassDao class to query the database and return as list of List<CabinClassModel> types.
 *
 */

public class DefaultCabinClassService implements CabinClassService
{

	private CabinClassDao cabinClassDao;

	@Override
	public List<CabinClassModel> getCabinClasses()
	{
		return cabinClassDao.findCabinClasses();
	}

	@Override
	public CabinClassModel getCabinClass(final String cabinCode)
	{
		return cabinClassDao.findCabinClass(cabinCode);
	}

	@Override
	public CabinClassModel getCabinClass(final Integer cabinClassIndex)
	{
		return cabinClassDao.findCabinClass(cabinClassIndex);
	}

	@Override
	public CabinClassModel findCabinClassFromBundleTemplate(String bundleTemplate)
	{
		return getCabinClassDao().findCabinClassFromBundleTemplate(bundleTemplate);
	}

	/**
	 * Gets cabin class dao.
	 *
	 * @return the cabin class dao
	 */
	protected CabinClassDao getCabinClassDao()
	{
		return cabinClassDao;
	}

	/**
	 * Sets cabin class dao.
	 *
	 * @param cabinClassDao
	 * 		the cabin class dao
	 */
	@Required
	public void setCabinClassDao(final CabinClassDao cabinClassDao)
	{
		this.cabinClassDao = cabinClassDao;
	}

}
