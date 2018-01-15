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

import de.hybris.platform.ndcfacades.ndc.AirShopReqAttributeQueryType.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcservices.constants.NdcservicesConstants;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;

import java.util.List;


/**
 * The NDC Origin Destination Validator
 * Check if the originDestination is under the maximum threshold
 * Check if the outbound is consistent in the departure and arrival airport with the inbound
 * Check if a flight has different departure and arrival locations
 */
public class NDCAirShoppingOriginDestinationValidator extends NDCAbstractOriginDestinationValidator<AirShoppingRQ>
		implements NDCRequestValidator<AirShoppingRQ>
{
	@Override
	public void validate(final AirShoppingRQ airShoppingRQ, final ErrorsType errorsType)
	{
		final List<OriginDestination> originDestinationList = airShoppingRQ
				.getCoreQuery().getOriginDestinations().getOriginDestination();

		if (!validateFlightNumber(originDestinationList, errorsType))
		{
			return;
		}

		if (!validateSameAirport(originDestinationList, errorsType))
		{
			return;
		}

		validateReturnFlight(originDestinationList, errorsType);
	}

	/**
	 * Validate flight number boolean.
	 *
	 * @param originDestinationList
	 * 		the origin destination list
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateFlightNumber(final List<OriginDestination> originDestinationList, final ErrorsType errorsType)
	{
		if (originDestinationList.size() < NdcwebservicesConstants.MIN_ORIGINIDESTINATION)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MIN_ORIGINDESTINATION_EXCEEDED));
			return false;
		}

		if (originDestinationList.size() > NdcwebservicesConstants.MAX_ORIGINIDESTINATION)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_ORIGINDESTINATION_EXCEEDED));
			return false;
		}

		return true;
	}

	/**
	 * Validate same airport boolean.
	 *
	 * @param originDestinationList
	 * 		the origin destination list
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateSameAirport(final List<OriginDestination> originDestinationList, final ErrorsType errorsType)
	{
		for (final OriginDestination originDestination : originDestinationList)
		{
			if (isSameCity(originDestination.getDeparture().getAirportCode().getValue(),
					originDestination.getArrival().getAirportCode().getValue()))
			{
				addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.SAME_AIRPORT));
				return false;
			}
		}
		return true;
	}

	/**
	 * Validate return flight boolean.
	 *
	 * @param originDestinationList
	 * 		the origin destination list
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateReturnFlight(final List<OriginDestination> originDestinationList, final ErrorsType errorsType)
	{
		if (originDestinationList.size() == NdcservicesConstants.RETURN_FLIGHT)
		{
			final String outboundDeparture = originDestinationList.get(NdcservicesConstants.OUTBOUND_FLIGHT_REF_NUMBER)
					.getDeparture()
					.getAirportCode().getValue();
			final String outboundArrival = originDestinationList.get(NdcservicesConstants.OUTBOUND_FLIGHT_REF_NUMBER).getArrival()
					.getAirportCode().getValue();

			final String inboundDeparture = originDestinationList.get(NdcservicesConstants.INBOUND_FLIGHT_REF_NUMBER).getDeparture()
					.getAirportCode().getValue();
			final String inboundArrival = originDestinationList.get(NdcservicesConstants.INBOUND_FLIGHT_REF_NUMBER).getArrival()
					.getAirportCode().getValue();

			if (!isSameCity(outboundDeparture, inboundArrival) || !isSameCity(outboundArrival, inboundDeparture))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_AIRPORTS_COMBINATION));
				return false;
			}
		}
		return true;
	}
}
