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

package de.hybris.platform.travelservices.model.dynamic.attribute;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

public class DurationAttributeHandler extends AbstractDynamicAttributeHandler<Long, TransportOfferingModel>
{
	private static final Logger LOG = Logger.getLogger(DurationAttributeHandler.class);

	//to handle system failure
	private static final Long DEFAULT_FALLBACK_DURATION = 7200000L;

	@Override
	public Long get(final TransportOfferingModel transportOfferingModel)
	{
		if (transportOfferingModel == null)
		{
			throw new IllegalArgumentException("Transport Offering model is required");
		}

		final TravelSectorModel travelSector = transportOfferingModel.getTravelSector();
		if (travelSector == null)
		{
			return DEFAULT_FALLBACK_DURATION;
		}

		final TransportFacilityModel origin = travelSector.getOrigin();
		if (origin == null || CollectionUtils.isEmpty(origin.getPointOfService()))
		{
			LOG.error("Duration for transport offering : " + transportOfferingModel.getCode() + " cannot be calculated as sector : "
					+ travelSector.getCode() + "does not contain origin details");
			return DEFAULT_FALLBACK_DURATION;
		}

		final TransportFacilityModel destination = travelSector.getDestination();
		if (destination == null || CollectionUtils.isEmpty(destination.getPointOfService()))
		{
			LOG.error("Duration for transport offering : " + transportOfferingModel.getCode() + " cannot be calculated as sector : "
					+ travelSector.getCode() + "does not contain destination details");
			return DEFAULT_FALLBACK_DURATION;
		}

		if (origin.getPointOfService().get(0).getTimeZoneId() == null
				|| destination.getPointOfService().get(0).getTimeZoneId() == null)
		{
			return DEFAULT_FALLBACK_DURATION;
		}
		final ZoneId originZoneId = ZoneId.of(origin.getPointOfService().get(0).getTimeZoneId());
		final ZoneId destinationZoneId = ZoneId.of(destination.getPointOfService().get(0).getTimeZoneId());

		final ZonedDateTime originUtcDateTime = TravelDateUtils.getUtcZonedDateTime(transportOfferingModel.getDepartureTime(), originZoneId);
		final ZonedDateTime destinationUtcDateTime = TravelDateUtils.getUtcZonedDateTime(transportOfferingModel.getArrivalTime(), destinationZoneId);

		final Duration duration = Duration.between(originUtcDateTime, destinationUtcDateTime);

		return duration.toMillis();
	}

}
