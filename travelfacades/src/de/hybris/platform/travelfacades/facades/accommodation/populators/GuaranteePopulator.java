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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import de.hybris.platform.commercefacades.accommodation.GuaranteeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;


/**
 * This class will populate the {@link GuaranteeData} from the {@link GuaranteeModel}
 */
public class GuaranteePopulator<SOURCE extends GuaranteeModel, TARGET extends GuaranteeData> implements Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setAbsoluteDeadline(source.getAbsoluteDeadline());
		target.setCode(source.getCode());
		target.setFixedAmount(source.getFixedAmount());
		target.setLongDescription(source.getLongDescription());
		target.setPercentageAmount(source.getPercentageAmount());
		target.setRelativeDeadline(source.getRelativeDeadline());
		target.setShortDescription(source.getShortDescription());
		target.setType(source.getType());
	}

}
