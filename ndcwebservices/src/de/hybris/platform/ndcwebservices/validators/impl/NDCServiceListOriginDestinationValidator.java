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

import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.FlightType.Flight;
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ.Query.OriginDestination;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The Concrete class that validates origin destination for {@link ServiceListRQ}
 */
public class NDCServiceListOriginDestinationValidator extends NDCOriginDestinationValidator<ServiceListRQ>
{
	private NDCTransportOfferingService ndcTransportOfferingService;

	@Override
	public void validate(final ServiceListRQ serviceListRQ, final ErrorsType errorsType)
	{
		final List<OriginDestination> originDestinations = serviceListRQ.getQuery().getOriginDestination();
		if (CollectionUtils.isNotEmpty(originDestinations))
		{
			if (!validateFlightNumber(originDestinations.size(), errorsType))
			{
				return;
			}
			validateODKey(originDestinations, errorsType);
		}
	}

	/**
	 * This method validates OriginDestinationKey for every {@link OriginDestination}, add error if it is not present.
	 *
	 * @param originDestinations
	 * 		the origin destinations
	 * @param errorsType
	 * 		the errors type
	 * @return the boolean
	 */
	protected boolean validateODKey(final List<OriginDestination> originDestinations, final ErrorsType errorsType)
	{
		if (originDestinations.stream().anyMatch(originDestination -> Objects.isNull(originDestination.getOriginDestinationKey())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_ORIGIN_DESTINATION_KEY));
			return false;
		}
		for (final OriginDestination originDestination : originDestinations)
		{
			final String originDestinationKey = originDestination.getOriginDestinationKey();
			for (final Flight flight : originDestination.getFlight())
			{
				if(Objects.isNull(flight.getSegmentKey()))
				{
					addError(errorsType, getConfigurationService().getConfiguration()
							.getString(NdcwebservicesConstants.MISSING_SEGMENT_KEY));
					return false;
				}
				try
				{
					final TransportOfferingModel transportOffering = getNdcTransportOfferingService()
							.getTransportOffering(flight.getSegmentKey());
					final boolean validRoute = transportOffering.getTravelSector().getTravelRoute().stream()
							.anyMatch(route -> StringUtils.equals(originDestinationKey, route.getCode()));
					if (!validRoute)
					{
						addError(errorsType, getConfigurationService().getConfiguration()
								.getString(NdcwebservicesConstants.SEGMENT_NOT_VALID_FOR_ORIGIN_DESTINATION));
						return false;
					}
				}
				catch (final ModelNotFoundException e)
				{
					addError(errorsType, getConfigurationService().getConfiguration()
							.getString(NdcwebservicesConstants.INVALID_SEGMENT_KEY));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets ndc transport offering service.
	 *
	 * @return the ndcTransportOfferingService
	 */
	protected NDCTransportOfferingService getNdcTransportOfferingService()
	{
		return ndcTransportOfferingService;
	}

	/**
	 * Sets ndc transport offering service.
	 *
	 * @param ndcTransportOfferingService
	 * 		the ndcTransportOfferingService to set
	 */
	@Required
	public void setNdcTransportOfferingService(final NDCTransportOfferingService ndcTransportOfferingService)
	{
		this.ndcTransportOfferingService = ndcTransportOfferingService;
	}
}
