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

import de.hybris.platform.commercefacades.accommodation.RatePlanInclusionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.accommodation.RatePlanInclusionModel;


/**
 * This class will populate the {@link RatePlanInclusionData} from the {@link RatePlanInclusionModel}
 */
public class RatePlanInclusionPopulator<SOURCE extends RatePlanInclusionModel, TARGET extends RatePlanInclusionData>
		implements Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setShortDescription(source.getShortDescription());
		target.setLongDescription(source.getLongDescription());
	}

}
