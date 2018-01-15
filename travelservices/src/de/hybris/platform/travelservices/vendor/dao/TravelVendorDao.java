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

package de.hybris.platform.travelservices.vendor.dao;

import de.hybris.platform.ordersplitting.model.VendorModel;


/**
 * Travel specific Data Access Object oriented on {@link VendorModel}
 */
public interface TravelVendorDao
{

	/**
	 * Method returns {@link VendorModel} for given code.
	 * 
	 * @param code
	 */
	VendorModel getVendorByCode(String code);

}
