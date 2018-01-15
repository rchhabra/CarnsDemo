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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;


/**
 * RatePlanDao interface which provides functionality to manage RatePlan.
 */
public interface RatePlanDao extends Dao
{
	/**
	 * Returns the RatePlanModel corresponding to the given code.
	 *
	 * @param ratePlanCode
	 * 		the ratePlanCode
	 *
	 * @return the RatePlanModel corresponding to the given code
	 */
	RatePlanModel findRatePlan(String ratePlanCode);
}
