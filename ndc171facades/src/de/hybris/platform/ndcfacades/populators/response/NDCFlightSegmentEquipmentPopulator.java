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
package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.AircraftCode;
import de.hybris.platform.ndcfacades.ndc.AircraftSummaryType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcservices.services.NDCTransportVehicleInfoService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * NDC Flight Segment Equipment Populator populate aircraftSummary based on the transportvehicleinfo for
 * {@link ListOfFlightSegmentType}
 */
public class NDCFlightSegmentEquipmentPopulator implements Populator<TransportOfferingData, ListOfFlightSegmentType>
{

	private NDCTransportVehicleInfoService ndcTransportVehicleInfoService;

	@Override
	public void populate(final TransportOfferingData transportOfferingData, final ListOfFlightSegmentType listOfFlightSegmentType)
			throws ConversionException
	{
		if (Objects.nonNull(transportOfferingData.getTransportVehicle()))
		{
			final AircraftSummaryType aircraftSummary = new AircraftSummaryType();
			final AircraftCode aircraftCode = new AircraftCode();
			aircraftSummary.setName(transportOfferingData.getTransportVehicle().getVehicleInfo().getName());
			aircraftCode.setValue(getNdcTransportVehicleInfoService()
					.getTransportVehicle(transportOfferingData.getTransportVehicle().getVehicleInfo().getCode()).getNdcCode());

			aircraftSummary.setAircraftCode(aircraftCode);
			listOfFlightSegmentType.setEquipment(aircraftSummary);
		}
	}

	/**
	 * Gets ndc transport vehicle info service.
	 *
	 * @return the ndc transport vehicle info service
	 */
	protected NDCTransportVehicleInfoService getNdcTransportVehicleInfoService()
	{
		return ndcTransportVehicleInfoService;
	}

	/**
	 * Sets ndc transport vehicle info service.
	 *
	 * @param ndcTransportVehicleInfoService
	 * 		the ndc transport vehicle info service
	 */
	@Required
	public void setNdcTransportVehicleInfoService(final NDCTransportVehicleInfoService ndcTransportVehicleInfoService)
	{
		this.ndcTransportVehicleInfoService = ndcTransportVehicleInfoService;
	}
}
