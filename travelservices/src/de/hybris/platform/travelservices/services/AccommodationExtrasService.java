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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Service that exposes the methods related to the extra services of an accommodation and accommodationOffering
 */
public interface AccommodationExtrasService
{

	/**
	 * Returns the list of available extra services for a accommodationOffering given its code
	 *
	 * @param accommodationOfferingCode
	 * 		the code of the accommodationOffering to be use to retrieve the extra services
	 * @return a list of ProductModel representing the list of available extra services for the given accommodationOffering
	 */
	List<ProductModel> getExtrasForAccommodationOffering(String accommodationOfferingCode);

}
