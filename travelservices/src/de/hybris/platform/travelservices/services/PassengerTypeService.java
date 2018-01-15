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

import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.List;


/**
 * Interface that exposes Passenger Type specific services
 */
public interface PassengerTypeService
{

	/**
	 * Service which returns a list of PassengerTypeModel types
	 *
	 * @return List<PassengerTypeModel> passenger types
	 */
	List<PassengerTypeModel> getPassengerTypes();

	/**
	 * Service which returns a PassengerTypeModel by Passenger Type code
	 *
	 * @param passengerTypeCode
	 * 		the passenger type code
	 * @return PassengerTypeModel passenger type
	 */
	PassengerTypeModel getPassengerType(String passengerTypeCode);

}
