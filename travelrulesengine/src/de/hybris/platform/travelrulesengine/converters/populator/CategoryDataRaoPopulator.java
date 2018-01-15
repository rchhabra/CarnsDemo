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

package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populates CategoryRAO attributes form CategoryData
 */
public class CategoryDataRaoPopulator implements Populator<CategoryData, CategoryRAO>
{
	@Override
	public void populate(final CategoryData source, final CategoryRAO target) throws ConversionException
	{
		target.setCode(source.getCode());
	}
}
