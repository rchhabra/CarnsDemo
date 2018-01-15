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
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.travelservices.model.travel.TravelRouteModel} as source and
 * {@link de.hybris.platform.commercefacades.travel.TravelRouteData} as target type.
 */
public class TravelRoutePopulator implements Populator<TravelRouteModel, TravelRouteData>
{

	private Converter<TravelSectorModel, TravelSectorData> travelSectorConverter;
	private Converter<TransportFacilityModel, TransportFacilityData> transportFacilityConverter;

	@Override
	public void populate(final TravelRouteModel source, final TravelRouteData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getOrigin() != null)
		{
			populateRouteInfo(source, target);
		}

	}

	/**
	 * Populate route info.
	 *
	 * @param source
	 * 		the source
	 * @param target
	 * 		the target
	 */
	protected void populateRouteInfo(final TravelRouteModel source, final TravelRouteData target)
	{

		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setOrigin(getTransportFacilityConverter().convert(source.getOrigin()));
		target.setDestination(getTransportFacilityConverter().convert(source.getDestination()));

		if (!source.getTravelSector().isEmpty())
		{
			target.setSectors(Converters.convertAll(source.getTravelSector(), getTravelSectorConverter()));
		}
	}

	/**
	 * Gets travel sector converter.
	 *
	 * @return the travel sector converter
	 */
	protected Converter<TravelSectorModel, TravelSectorData> getTravelSectorConverter()
	{
		return travelSectorConverter;
	}

	/**
	 * Sets travel sector converter.
	 *
	 * @param travelSectorConverter
	 * 		the travel sector converter
	 */
	@Required
	public void setTravelSectorConverter(final Converter<TravelSectorModel, TravelSectorData> travelSectorConverter)
	{
		this.travelSectorConverter = travelSectorConverter;
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
