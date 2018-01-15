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
import de.hybris.platform.travelservices.model.order.GuestCountModel;

/**
 * GuestCountDao interface which provides functionality to manage the GuestCountModel.
 */
public interface GuestCountDao extends Dao
{
	/**
	 * Returns a {@link GuestCountModel} based on the passenger type code and the quantity
	 *
	 * @param passengerTypeCode
	 * 		as the passenger type code
	 * @param quantity
	 * 		as the quantity
	 *
	 * @return the guest count model
	 */
	GuestCountModel findGuestCount(String passengerTypeCode, int quantity);
}
