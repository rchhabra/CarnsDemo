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

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.travelfacades.strategies.AbstractOfferResponseFilterStrategy;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Strategy that extends the {@link de.hybris.platform.travelfacades.strategies.AbstractOfferResponseFilterStrategy}.
 * The strategy is used to filter out the originDestinationOfferInfos if at least one of its transportOfferings has
 * status DEPARTED.
 */
public class TransportOfferingStatusStrategy extends AbstractOfferResponseFilterStrategy
{

	private List<TransportOfferingStatus> notAllowedStatuses;

	@Override
	public void filterOfferResponseData(final OfferResponseData offerResponseData)
	{

		for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
		{
			if (CollectionUtils.isNotEmpty(offerGroupData.getOriginDestinationOfferInfos()))
			{
				offerGroupData.getOriginDestinationOfferInfos().removeIf(odOfferInfo -> {
					final boolean result = isAnyTransportOfferingInNotAllowedStatus(odOfferInfo, offerGroupData);
					setOriginDestinationStatus(odOfferInfo.getOriginDestinationRefNumber(), offerResponseData, !result);
					return result;
				});
			}
		}

	}

	/**
	 * Is any transport offering in not allowed status boolean.
	 *
	 * @param originDestinationOfferInfoData
	 * 		the origin destination offer info data
	 * @param offerGroupData
	 * 		the offer group data
	 * @return the boolean
	 */
	protected boolean isAnyTransportOfferingInNotAllowedStatus(final OriginDestinationOfferInfoData originDestinationOfferInfoData,
			final OfferGroupData offerGroupData)
	{
		final int odNumber = originDestinationOfferInfoData.getOriginDestinationRefNumber();

		final List<OriginDestinationOfferInfoData> odOfferInfos = offerGroupData.getOriginDestinationOfferInfos().stream()
				.filter(odOfferInfo -> odOfferInfo.getOriginDestinationRefNumber() == odNumber).collect(Collectors.toList());

		return odOfferInfos.stream().flatMap(odOfferInfo -> odOfferInfo.getTransportOfferings().stream())
				.anyMatch(to -> notAllowedStatuses.stream().anyMatch(
						notAllowedStatus -> StringUtils.equalsIgnoreCase(notAllowedStatus.getCode(), to.getStatus())));
	}

	@Override
	public void filterSeatMapData(final SeatMapResponseData seatMapResponseData)
	{
		seatMapResponseData.getSeatMap().removeIf(seatMap -> getNotAllowedStatuses().stream()
				.anyMatch(status -> StringUtils.equalsIgnoreCase(status.getCode(), seatMap.getTransportOffering().getStatus())));
	}

	/**
	 * Gets not allowed statuses.
	 *
	 * @return the notAllowedStatuses
	 */
	protected List<TransportOfferingStatus> getNotAllowedStatuses()
	{
		return notAllowedStatuses;
	}

	/**
	 * Sets not allowed statuses.
	 *
	 * @param notAllowedStatuses
	 * 		the notAllowedStatuses to set
	 */
	public void setNotAllowedStatuses(final List<TransportOfferingStatus> notAllowedStatuses)
	{
		this.notAllowedStatuses = notAllowedStatuses;
	}

}
