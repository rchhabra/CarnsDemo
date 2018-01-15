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
package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.travel.TravelProviderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;
import org.springframework.util.Assert;


/**
 * The type Travel Provider Populator
 */
public class TravelProviderPopulator implements Populator<TravelProviderModel, TravelProviderData>
{
	@Override
	public void populate(final TravelProviderModel source, final TravelProviderData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setCode(source.getCode());
	}
}
