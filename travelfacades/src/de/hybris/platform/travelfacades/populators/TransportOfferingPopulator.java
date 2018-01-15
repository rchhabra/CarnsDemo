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

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TravelProviderData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.model.travel.TransportVehicleModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel} as
 * source and {@link de.hybris.platform.commercefacades.travel.TransportOfferingData} as target type.
 */
public class TransportOfferingPopulator implements Populator<TransportOfferingModel, TransportOfferingData>
{

	private Converter<TravelSectorModel, TravelSectorData> travelSectorConverter;
	private Converter<TransportVehicleModel, TransportVehicleData> transportVehicleConverter;
	private Converter<TravelProviderModel, TravelProviderData> travelProviderConverter;

	@Override
	public void populate(final TransportOfferingModel source, final TransportOfferingData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setNumber(source.getNumber());
		target.setDepartureTime(source.getDepartureTime());

		final List<PointOfServiceModel> originPointOfServices = source.getTravelSector().getOrigin().getPointOfService();
		if (CollectionUtils.isNotEmpty(originPointOfServices) && originPointOfServices.get(0).getTimeZoneId() != null)
		{
			target.setDepartureTimeZoneId(ZoneId.of(originPointOfServices.get(0).getTimeZoneId()));
		}
		else
		{
			target.setDepartureTimeZoneId(ZoneId.from(ZoneOffset.UTC));
		}

		target.setArrivalTime(source.getArrivalTime());

		final List<PointOfServiceModel> destinationPointOfServices = source.getTravelSector().getDestination().getPointOfService();
		if (CollectionUtils.isNotEmpty(destinationPointOfServices) && destinationPointOfServices.get(0).getTimeZoneId() != null)
		{
			target.setArrivalTimeZoneId(ZoneId.of(destinationPointOfServices.get(0).getTimeZoneId()));
		}
		else
		{
			target.setArrivalTimeZoneId(ZoneId.from(ZoneOffset.UTC));
		}

		target.setType(source.getType().getCode());
		target.setDurationValue(source.getDuration());
		target.setDuration(source.getDuration() != null ? TransportOfferingUtils.getDurationMap(source.getDuration()) : null);

		if (source.getTravelSector() != null)
		{
			target.setSector(getTravelSectorConverter().convert(source.getTravelSector()));
		}

		if (source.getTransportVehicle() != null)
		{
			target.setTransportVehicle(getTransportVehicleConverter().convert(source.getTransportVehicle()));
		}

		if (source.getTravelProvider() != null)
		{
			target.setTravelProvider(getTravelProviderConverter().convert(source.getTravelProvider()));
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
	 * Gets transport vehicle converter.
	 *
	 * @return the transport vehicle converter
	 */
	protected Converter<TransportVehicleModel, TransportVehicleData> getTransportVehicleConverter()
	{
		return transportVehicleConverter;
	}

	/**
	 * Sets transport vehicle converter.
	 *
	 * @param transportVehicleConverter
	 * 		the transport vehicle converter
	 */
	@Required
	public void setTransportVehicleConverter(
			final Converter<TransportVehicleModel, TransportVehicleData> transportVehicleConverter)
	{
		this.transportVehicleConverter = transportVehicleConverter;
	}

	/**
	 * Gets travel provide converter.
	 *
	 * @return the travel provide converter
	 */
	protected Converter<TravelProviderModel, TravelProviderData> getTravelProviderConverter()
	{
		return travelProviderConverter;
	}
	/**
	 * Sets travel provide converter.
	 *
	 * @param travelProviderConverter
	 * 		the travel provide converter
	 */
	@Required
	public void setTravelProviderConverter(
			final Converter<TravelProviderModel, TravelProviderData> travelProviderConverter)
	{
		this.travelProviderConverter = travelProviderConverter;
	}
}
