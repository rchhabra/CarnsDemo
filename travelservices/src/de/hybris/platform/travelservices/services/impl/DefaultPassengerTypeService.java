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

import de.hybris.platform.travelservices.dao.PassengerTypeDao;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Class is responsible for providing concrete implementation of the PassengerTypeService interface. The class uses the
 * passengerTypeDao class to query the database and return as list of List<PassengerTypeModel> types.
 *
 */

public class DefaultPassengerTypeService implements PassengerTypeService
{
	private PassengerTypeDao passengerTypeDao;

	@Override
	public List<PassengerTypeModel> getPassengerTypes()
	{
		return passengerTypeDao.findPassengerTypes();
	}

	@Override
	public PassengerTypeModel getPassengerType(final String passengerTypeCode)
	{
		return passengerTypeDao.findPassengerType(passengerTypeCode);
	}

	/**
	 * Gets passenger type dao.
	 *
	 * @return the passenger type dao
	 */
	protected PassengerTypeDao getPassengerTypeDao()
	{
		return passengerTypeDao;
	}

	/**
	 * Sets passenger type dao.
	 *
	 * @param passengerTypeDao
	 * 		the passenger type dao
	 */
	@Required
	public void setPassengerTypeDao(final PassengerTypeDao passengerTypeDao)
	{
		this.passengerTypeDao = passengerTypeDao;
	}
}
