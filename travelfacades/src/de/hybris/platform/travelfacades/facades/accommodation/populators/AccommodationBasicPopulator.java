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

import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.product.AccommodationModel;


/**
 * Populator responsible to populate the basic information of the RoomTypeData
 *
 * @param <SOURCE>
 *           the AccommodationModel
 * @param <TARGET>
 *           the RoomTypeData
 */
public class AccommodationBasicPopulator<SOURCE extends AccommodationModel, TARGET extends RoomTypeData>
		implements Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setSizeMeasurement(source.getSize());
		target.setDescription(source.getDescription());
		target.setName(source.getName());

	}

}
