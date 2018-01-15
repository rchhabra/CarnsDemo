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

import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;


/**
 * Populator to populate the CabinClassData from the CabinClassModel
 */
public class CabinClassPopulator implements Populator<CabinClassModel, CabinClassData>
{

	@Override
	public void populate(final CabinClassModel source, final CabinClassData target) throws ConversionException
	{

		if (source != null)
		{
			target.setCode(source.getCode());
			target.setName(source.getName());
			target.setIndex(source.getCabinClassIndex());
		}
	}

}
