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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.strategies.AbstractOfferResponseFilterStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Strategy that extends the {@link de.hybris.platform.travelfacades.strategies.AbstractOfferResponseFilterStrategy}.
 * The strategy is used to filter out the originDestinationOfferInfos if at least one of its transportOfferings is in
 * the past, that means that the departure date is before the current time.
 */
public class TransportOfferingPastDepartureDateStrategy extends AbstractOfferResponseFilterStrategy
{
	private TimeService timeService;

	@Override
	public void filterOfferResponseData(final OfferResponseData offerResponseData)
	{

		for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
		{
			if (CollectionUtils.isNotEmpty(offerGroupData.getOriginDestinationOfferInfos()))
			{
				offerGroupData.getOriginDestinationOfferInfos().removeIf(odOfferInfo -> {
					final boolean result = isAnyTransportOfferingInThePast(odOfferInfo, offerGroupData);
					setOriginDestinationStatus(odOfferInfo.getOriginDestinationRefNumber(), offerResponseData, !result);
					return result;
				});

			}
		}

	}

	/**
	 * Is any transport offering in the past boolean.
	 *
	 * @param originDestinationOfferInfoData
	 * 		the origin destination offer info data
	 * @param offerGroupData
	 * 		the offer group data
	 * @return the boolean
	 */
	protected boolean isAnyTransportOfferingInThePast(final OriginDestinationOfferInfoData originDestinationOfferInfoData,
			final OfferGroupData offerGroupData)
	{

		final int odNumber = originDestinationOfferInfoData.getOriginDestinationRefNumber();

		final List<OriginDestinationOfferInfoData> odOfferInfos = offerGroupData.getOriginDestinationOfferInfos().stream()
				.filter(odOfferInfo -> odOfferInfo.getOriginDestinationRefNumber() == odNumber).collect(Collectors.toList());

		return odOfferInfos.stream().flatMap(odOfferInfo -> odOfferInfo.getTransportOfferings().stream())
				.anyMatch(to -> isTransportOfferingInThePast(to));
	}

	@Override
	public void filterSeatMapData(final SeatMapResponseData seatMapResponseData)
	{
		seatMapResponseData.getSeatMap().removeIf(seatMap -> isTransportOfferingInThePast(seatMap.getTransportOffering()));

	}

	/**
	 * Is transport offering in the past boolean.
	 *
	 * @param to
	 * 		the to
	 * @return the boolean
	 */
	protected boolean isTransportOfferingInThePast(final TransportOfferingData to)
	{
		return TravelDateUtils.isBefore(to.getDepartureTime(), to.getDepartureTimeZoneId(), getTimeService().getCurrentTime(),
				ZoneId.systemDefault());
	}

	/**
	 * Gets time service.
	 *
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

}
