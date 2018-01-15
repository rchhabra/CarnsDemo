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
package de.hybris.platform.travelfacades.ancillary.search.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

/**
 * This is to populate the basic fields for an ancillary product
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public class AncillaryProductPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
		implements Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
		if (Objects.nonNull(source.getIgnoreRules()))
		{
			target.setIgnoreRules(source.getIgnoreRules());
		}
	}
}
