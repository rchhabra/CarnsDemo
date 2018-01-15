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

package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.ndcfacades.ndc.DataListType.OriginDestinationList;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.FlightInfoAssocType;
import de.hybris.platform.ndcfacades.ndc.FlightSegmentReference;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;



/**
 * The Concrete class that validates origin destination for {@link SeatAvailabilityRQ}
 */
public class NDCSeatAvailabilityOriginDestinationValidator extends NDCOriginDestinationValidator<SeatAvailabilityRQ>
{
	@Override
	public void validate(final SeatAvailabilityRQ seatAvailabilityRQ, final ErrorsType errorsType)
	{
		if (Objects.nonNull(seatAvailabilityRQ.getQuery()))
		{
			final List<FlightInfoAssocType> originDestinations = seatAvailabilityRQ.getQuery().getOriginDestination();
			if (Objects.isNull(seatAvailabilityRQ.getDataLists()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.DATALISTS_UNAVAILABLE));
				return;
			}
			if (Objects.nonNull(originDestinations))
			{
				if (!validateFlightNumber(originDestinations.size(), errorsType))
				{
					return;
				}

				if (!validateOriginDestinationRef(originDestinations, errorsType))
				{
					return;
				}

				validateOriginDestinationList(originDestinations, seatAvailabilityRQ.getDataLists().getOriginDestinationList(),
						errorsType);
			}
			else
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MIN_ORIGINDESTINATION_EXCEEDED));
			}
		}
		else
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.QUERY_UNAVAILABLE));
		}
	}

	/**
	 * This method returns true if the size of {@link FlightInfoAssocType}'s list is equal to size of
	 * {@link OriginDestination}'s list otherwise false;
	 *
	 * @param flightInfoAssocTypes
	 * 		the flight info assoc types
	 * @param originDestinations
	 * 		the origin destinations
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validateOriginDestinationList(final List<FlightInfoAssocType> flightInfoAssocTypes,
			final OriginDestinationList originDestinations, final ErrorsType errorsType)
	{
		if (Objects.nonNull(flightInfoAssocTypes) && Objects.nonNull(originDestinations)
				&& CollectionUtils.size(flightInfoAssocTypes) != CollectionUtils.size(originDestinations.getOriginDestination()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISMATCH_ORIGIN_DESTINATION));
			return false;
		}
		return true;
	}

	/**
	 * This method returns false in below following 3 conditions : 1. if any {@link FlightSegmentReference} is null in
	 * list of {@link FlightInfoAssocType} element. 2. if reference attribute is null of any
	 * {@link FlightSegmentReference} element. 3. if any OriginDestinationReferences contains more than 1 ref
	 *
	 * @param originDestinations
	 * 		the origin destinations
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validateOriginDestinationRef(final List<FlightInfoAssocType> originDestinations, final ErrorsType errorsType)
	{
		if (originDestinations.stream()
				.anyMatch(originDestination -> Objects.isNull(originDestination.getOriginDestinationReferences())
						|| CollectionUtils.isEmpty(originDestination.getOriginDestinationReferences())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.ORIGIN_DEST_REF_UNAVAILABLE));
			return false;
		}

		if (originDestinations.stream().anyMatch(originDestination -> originDestination.getOriginDestinationReferences().stream()
				.anyMatch(Objects::isNull)))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.ORIGIN_DEST_REF_ATTR_UNAVAILABLE));
			return false;
		}

		if (originDestinations.stream()
				.anyMatch(originDestination -> originDestination.getOriginDestinationReferences().size() > 1))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.ORIGIN_DEST_REF_ATTR_MAX_REF));
			return false;
		}

		return true;
	}
}
