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
 */

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.travel.LocationModel;


/**
 * Interface which provides the functionality of fetching {@link LocationModel} information from database
 */
public interface TravelLocationDao extends Dao
{
	/**
	 * Finds location for given code.
	 *
	 * @param code
	 * 		code
	 * @return location model
	 */
	LocationModel findLocation(final String code);
}
