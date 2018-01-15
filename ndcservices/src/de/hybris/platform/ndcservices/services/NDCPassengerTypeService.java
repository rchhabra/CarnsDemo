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
package de.hybris.platform.ndcservices.services;

import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

/**
 * Interface that exposes PassengerTypeModel through his ndcCode
 */
public interface NDCPassengerTypeService
{
	/**
	 * Find a PassengerTypeModel through his ndcCode
	 *
	 * @param ndcCode
	 * 	of the passengerType
	 * @return
	 * 	the passengerType with that ndcCode
	 */
	PassengerTypeModel getPassengerType(String ndcCode);
}
