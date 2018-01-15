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

import de.hybris.platform.commercefacades.accommodation.AccommodationFacilityData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.facility.AccommodationFacilityModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator responsible to populate the list of Facilities of the RoomTypeData
 *
 * @param <SOURCE>
 *           the AccommodationModel
 * @param <TARGET>
 *           the RoomTypeData
 */
public class AccommodationFacilitiesPopulator<SOURCE extends AccommodationModel, TARGET extends RoomTypeData>
		implements Populator<SOURCE, TARGET>
{
	private Converter<AccommodationFacilityModel, AccommodationFacilityData> accommodationFacilityConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setFacilities(getAccommodationFacilityConverter().convertAll(source.getAccommodationFacility()));
	}

	/**
	 * @return the accommodationFacilityConverter
	 */
	protected Converter<AccommodationFacilityModel, AccommodationFacilityData> getAccommodationFacilityConverter()
	{
		return accommodationFacilityConverter;
	}

	/**
	 * @param accommodationFacilityConverter
	 *           the accommodationFacilityConverter to set
	 */
	@Required
	public void setAccommodationFacilityConverter(
			final Converter<AccommodationFacilityModel, AccommodationFacilityData> accommodationFacilityConverter)
	{
		this.accommodationFacilityConverter = accommodationFacilityConverter;
	}

}
