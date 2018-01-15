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

import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.List;


/**
 * Interface that exposes Passenger Type specific DAO services
 */
public interface PassengerTypeDao extends Dao
{

	/**
	 * DAO service which returns a list of PassengerTypeModel types
	 *
	 * @return List<PassengerTypeModel> list
	 */
	List<PassengerTypeModel> findPassengerTypes();

	/**
	 * DAO service which returns a PassengerTypeModel type based on the code
	 *
	 * @param passengerTypeCode
	 * 		the passenger type code
	 * @return PassengerTypeModel passenger type model
	 */
	PassengerTypeModel findPassengerType(String passengerTypeCode);

}
