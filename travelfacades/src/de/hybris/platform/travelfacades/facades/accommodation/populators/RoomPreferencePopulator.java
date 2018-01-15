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

import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;

import org.springframework.util.Assert;


/**
 * Populator to populate RoomPreferenceModel to RoomPreferenceData
 */
public class RoomPreferencePopulator<SOURCE extends RoomPreferenceModel, TARGET extends RoomPreferenceData>
		implements Populator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setCode(source.getPk().toString());
		target.setValue(source.getValue());
		target.setRoomPreferenceType(source.getPreferenceType().getCode());
	}
}
