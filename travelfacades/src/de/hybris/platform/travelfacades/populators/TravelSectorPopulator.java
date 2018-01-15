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

import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.travelservices.model.travel.TravelSectorModel} as source and
 * {@link de.hybris.platform.commercefacades.travel.TravelSectorData} as target type.
 */
public class TravelSectorPopulator implements Populator<TravelSectorModel, TravelSectorData>
{

	private Converter<TransportFacilityModel, TransportFacilityData> transportFacilityConverter;

	@Override
	public void populate(final TravelSectorModel source, final TravelSectorData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());

		if (source.getOrigin() != null)
		{
			target.setOrigin(new TransportFacilityData());
			getTransportFacilityConverter().convert(source.getOrigin(), target.getOrigin());
		}

		if (source.getDestination() != null)
		{
			target.setDestination(getTransportFacilityConverter().convert(source.getDestination()));
		}

	}

	/**
	 * Gets transport facility converter.
	 *
	 * @return the transport facility converter
	 */
	protected Converter<TransportFacilityModel, TransportFacilityData> getTransportFacilityConverter()
	{
		return transportFacilityConverter;
	}

	/**
	 * Sets transport facility converter.
	 *
	 * @param transportFacilityConverter
	 * 		the transport facility converter
	 */
	@Required
	public void setTransportFacilityConverter(
			final Converter<TransportFacilityModel, TransportFacilityData> transportFacilityConverter)
	{
		this.transportFacilityConverter = transportFacilityConverter;
	}


}
