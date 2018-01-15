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

package de.hybris.platform.travelfacades.strategies;

import java.util.List;


/**
 * Strategy to order the products in a collection.
 *
 * @param <T>
 * 		the type parameter
 */
public interface ProductsSortStrategy<T>
{

	/**
	 * Method to apply the ordering of products in a collection.
	 *
	 * @param productDatas
	 * 		the product datas
	 */
	void applyStrategy(List<? extends T> productDatas);

}
