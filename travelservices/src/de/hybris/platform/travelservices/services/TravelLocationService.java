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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.travelservices.model.travel.LocationModel;


/**
 * Interface which provides functionality related to {@link LocationModel}
 */
public interface TravelLocationService
{
	/**
	 * Gets the location for given code
	 *
	 * @param code
	 * 		code
	 * @return location model
	 */
	LocationModel getLocation(final String code);
}
