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

package de.hybris.platform.ndcfacades.populators.request;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapRequestData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SegmentInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList.Flight;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;


/**
 * NDC Query populator for {@link SeatAvailabilityRQ}
 */
public class NDCSeatAvailabilityQueryPopulator implements Populator<SeatAvailabilityRQ, OfferRequestData>
{

	@Override
	public void populate(final SeatAvailabilityRQ source, final OfferRequestData target) throws ConversionException
	{
		final SeatMapRequestData seatMapRequest = new SeatMapRequestData();
		final List<SegmentInfoData> segmentInfoDatas = new ArrayList<>();
		seatMapRequest.setSegmentInfoDatas(segmentInfoDatas);
		source.getQuery().getOriginDestination()
				.forEach(queryOriginDestination -> queryOriginDestination.getOriginDestinationReferences().forEach(originDestinationRef -> {
					final OriginDestination originDestination = (OriginDestination) originDestinationRef;
					final Flight flight = (Flight) originDestination.getFlightReferences().getValue().get(0);
					flight.getSegmentReferences().getValue().forEach(obj -> {
						if (obj instanceof ListOfFlightSegmentType)
						{
							final SegmentInfoData segmentInfoData = new SegmentInfoData();
							final ListOfFlightSegmentType flightSegmentType = (ListOfFlightSegmentType) obj;
							segmentInfoData.setTransportOfferingCode(flightSegmentType.getSegmentKey());
							segmentInfoData.setCabinClass(flightSegmentType.getClassOfService().getCode().getValue());
							segmentInfoDatas.add(segmentInfoData);
						}
					});
				}));
		target.setSeatMapRequest(seatMapRequest);
	}
}
