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

import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator responsible to populate the list of Occupancies of the RoomTypeData
 *
 * @param <SOURCE>
 *           the AccommodationModel
 * @param <TARGET>
 *           the RoomTypeData
 */
public class AccommodationOccupanciesPopulator<SOURCE extends AccommodationModel, TARGET extends RoomTypeData>
		implements Populator<SOURCE, TARGET>
{
	private Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setOccupancies(getGuestOccupancyConverter().convertAll(source.getGuestOccupancies()));
	}

	/**
	 * @return the guestOccupancyConverter
	 */
	protected Converter<GuestOccupancyModel, GuestOccupancyData> getGuestOccupancyConverter()
	{
		return guestOccupancyConverter;
	}

	/**
	 * @param guestOccupancyConverter
	 *           the guestOccupancyConverter to set
	 */
	@Required
	public void setGuestOccupancyConverter(final Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter)
	{
		this.guestOccupancyConverter = guestOccupancyConverter;
	}

}
