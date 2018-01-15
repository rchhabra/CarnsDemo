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

package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ruleengineservices.converters.populator.ProductRaoPopulator;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;


public class TravelProductRaoPopulator extends ProductRaoPopulator
{
	@Override
	public void populate(final ProductModel source, final ProductRAO target)
	{
		super.populate(source, target);
		if (source instanceof FareProductModel)
		{
			final FareProductModel fareProduct = (FareProductModel) source;
			target.setProductType(ProductType.FARE_PRODUCT);
		}
	}
}
