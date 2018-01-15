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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Accommodation Extras Dao interface which provides functionalities for the extra services products of the
 * accommodationOfferings.
 */
public interface AccommodationExtrasDao extends Dao
{

	/**
	 * Returns all the extra services products related to the given accommodationOffering
	 *
	 * @param accommodationOfferingCode
	 * 		the code of the accommodationOffering to be used to return the list of extra services
	 * @return the list of products representing all the available extra services for the given accommodationOffering
	 */
	List<ProductModel> findExtras(String accommodationOfferingCode);

}
