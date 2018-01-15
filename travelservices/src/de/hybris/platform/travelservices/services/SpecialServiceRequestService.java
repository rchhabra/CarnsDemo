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

import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;


/**
 * Interface that exposes Special Service Request specific services
 */
public interface SpecialServiceRequestService
{
	/**
	 * Method returns the SpecialServiceRequestModel for the given code
	 *
	 * @param code
	 * 		the code
	 * @return special service request
	 */
	SpecialServiceRequestModel getSpecialServiceRequest(String code);
}
