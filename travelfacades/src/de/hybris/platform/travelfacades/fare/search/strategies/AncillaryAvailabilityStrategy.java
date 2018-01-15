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

package de.hybris.platform.travelfacades.fare.search.strategies;

import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.List;


/**
 * Strategy to check if a bundleTemplate is available based on the availability of the includedAncillaries
 */
public interface AncillaryAvailabilityStrategy
{

	/**
	 * Method to check if a bundleTemplate is available based on the availability of the includedAncillaries.
	 *
	 * @param productDataList
	 * 		the product data list
	 * @param passengerNumber
	 * 		the passenger number
	 * @return true if the bundleTemplate is available, false otherwise
	 */
	boolean checkIncludedAncillariesAvailability(List<ProductData> productDataList, int passengerNumber);

}
