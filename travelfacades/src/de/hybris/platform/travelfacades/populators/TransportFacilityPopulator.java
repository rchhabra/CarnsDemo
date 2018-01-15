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

import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.travelservices.model.travel.TransportFacilityModel} as source
 * and {@link de.hybris.platform.commercefacades.travel.TransportFacilityData} as target type.
 */
public class TransportFacilityPopulator implements Populator<TransportFacilityModel, TransportFacilityData>
{
	private Converter<LocationModel, LocationData> locationConverter;

	@Override
	public void populate(final TransportFacilityModel source, final TransportFacilityData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setLocation(getLocationConverter().convert(source.getLocation()));
	}

	/**
	 * @return locationConverter
	 */
	protected Converter<LocationModel, LocationData> getLocationConverter()
	{
		return locationConverter;
	}

	/**
	 * @param locationConverter
	 * 		the locationConverter to set
	 */
	@Required
	public void setLocationConverter(final Converter<LocationModel, LocationData> locationConverter)
	{
		this.locationConverter = locationConverter;
	}
}
