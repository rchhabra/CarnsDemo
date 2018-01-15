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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.travelfacades.strategies.ProductsSortStrategy;

import java.util.Collections;
import java.util.List;


/**
 * Strategy implementation to sort the products in ascending order.
 * This strategy sorts the collection of products using the price information. The products are sorted in ascending
 * order and the lowest priced product will appear at the top.
 */
public class LowestFareStrategy implements ProductsSortStrategy<ProductData>
{

	@Override
	public void applyStrategy(final List<? extends ProductData> productDatas)
	{
		if (productDatas == null)
		{
			return;
		}

		Collections.sort(productDatas, (product1, product2) -> {
			if (product1 == null && product2 == null)
			{
				return 0;
			}
			if (product1 == null || product1.getPrice() == null)
			{
				return 1;
			}
			if (product2 == null || product2.getPrice() == null)
			{
				return -1;
			}
			return Double.compare(product1.getPrice().getValue().doubleValue(), product2.getPrice().getValue().doubleValue());
		});

	}
}
