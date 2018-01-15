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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populates ignore rules property for rules evaluation
 */
public class IgnoreRulesPopulator implements Populator<CategoryModel, OfferGroupData>
{

	@Override
	public void populate(final CategoryModel source, final OfferGroupData target) throws ConversionException
	{
		target.setIgnoreRules(source.getIgnoreRules());
	}

}
