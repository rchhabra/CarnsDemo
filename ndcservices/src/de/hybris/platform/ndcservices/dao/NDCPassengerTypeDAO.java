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
package de.hybris.platform.ndcservices.dao;

import de.hybris.platform.travelservices.model.user.PassengerTypeModel;


/**
 * Interface for dao handling NDC Passenger Type
 */
public interface NDCPassengerTypeDAO
{
	/**
	 * Retrieves passengerTypeModel based on the ndcCode provided
	 *
	 * @param ndcCode that identifies the PassengerTypeModel
	 * @return PassengerTypeModel with the specified ndcCode
	 */
	PassengerTypeModel getPassengerType(String ndcCode);
}
