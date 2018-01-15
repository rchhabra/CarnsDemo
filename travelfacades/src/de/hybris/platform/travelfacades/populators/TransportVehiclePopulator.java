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

import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * The type Transport vehicle populator.
 */
public class TransportVehiclePopulator implements Populator<TransportVehicleModel, TransportVehicleData>
{

	private Converter<TransportVehicleInfoModel, TransportVehicleInfoData> transportVehicleInfoConverter;

	@Override
	public void populate(final TransportVehicleModel source, final TransportVehicleData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final TransportVehicleInfoModel transportVehicleInfo = source.getTransportVehicleInfo();
		if (transportVehicleInfo != null)
		{
			target.setVehicleInfo(getTransportVehicleInfoConverter().convert(transportVehicleInfo));
		}
	}

	/**
	 * Gets transport vehicle info converter.
	 *
	 * @return the transport vehicle info converter
	 */
	protected Converter<TransportVehicleInfoModel, TransportVehicleInfoData> getTransportVehicleInfoConverter()
	{
		return transportVehicleInfoConverter;
	}

	/**
	 * Sets transport vehicle info converter.
	 *
	 * @param transportVehicleInfoConverter
	 * 		the transport vehicle info converter
	 */
	@Required
	public void setTransportVehicleInfoConverter(
			final Converter<TransportVehicleInfoModel, TransportVehicleInfoData> transportVehicleInfoConverter)
	{
		this.transportVehicleInfoConverter = transportVehicleInfoConverter;
	}

}
