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

import de.hybris.platform.ndcfacades.ndc.BaggageListRQ;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.FlightType.Flight;
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to validate Segment key for {@link BaggageListRQ} and {@link ServiceListRQ}
 */
public abstract class NDCOffersSegmentKeyValidator<N> implements NDCRequestValidator<N>
{
	private static final String SPACE = " ";

	private ConfigurationService configurationService;
	private NDCTransportOfferingService ndcTransportOfferingService;

	/**
	 * Validate segment key boolean.
	 *
	 * @param segmentKey
	 * 		the segment key
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	public boolean validateSegmentKey(final String segmentKey, final ErrorsType errorsType)
	{
		if (StringUtils.isEmpty(segmentKey) || !StringUtils.isAlphanumeric(segmentKey))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.SEGMENT_KEY_UNAVAILABLE));
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Validate origin destination key.
	 *
	 * @param originDestinationKey
	 *           the origin destination key
	 * @param flights
	 *           the flights
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validateOriginDestinationKey(final String originDestinationKey, final List<Flight> flights,
			final ErrorsType errorsType)
	{
		for (final Flight flight : flights)
		{
			try
			{
				final TransportOfferingModel transportOffering = getNdcTransportOfferingService()
						.getTransportOffering(flight.getSegmentKey());
				final boolean validRoute = transportOffering.getTravelSector().getTravelRoute().stream()
						.anyMatch(route -> StringUtils.equals(originDestinationKey, route.getCode()));
				if (!validRoute)
				{
					final String errorMessage = new StringBuilder(flight.getSegmentKey()).append(SPACE)
							.append(getConfigurationService().getConfiguration()
									.getString(NdcwebservicesConstants.SEGMENT_NOT_VALID_FOR_ORIGIN_DESTINATION))
							.toString();
					addError(errorsType, errorMessage);
					return false;
				}
			}
			catch (final ModelNotFoundException e)
			{
				final String errorMessage = new StringBuilder(
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_SEGMENT_KEY))
								.append(SPACE).append(flight.getSegmentKey()).toString();
				addError(errorsType, errorMessage);
			}
		}
		return true;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the ndcTransportOfferingService
	 */
	protected NDCTransportOfferingService getNdcTransportOfferingService()
	{
		return ndcTransportOfferingService;
	}

	/**
	 * @param ndcTransportOfferingService
	 *           the ndcTransportOfferingService to set
	 */
	@Required
	public void setNdcTransportOfferingService(final NDCTransportOfferingService ndcTransportOfferingService)
	{
		this.ndcTransportOfferingService = ndcTransportOfferingService;
	}
}
