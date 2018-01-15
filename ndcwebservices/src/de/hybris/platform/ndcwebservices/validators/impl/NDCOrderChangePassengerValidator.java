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

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Passengers;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Passengers.Passenger;
import de.hybris.platform.ndcfacades.ndc.TravelerSummaryType.ProfileID;
import de.hybris.platform.ndcservices.enums.NDCActionType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigInteger;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * NDC {@link OrderChangeRQ} {@link Passenger} Validator
 */
public class NDCOrderChangePassengerValidator implements NDCRequestValidator<OrderChangeRQ>
{
	private ConfigurationService configurationService;
	private EnumerationService enumerationService;

	@Override
	public void validate(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		if (!validatePassengerInformationQuantity(orderChangeRQ.getQuery().getPassengers(),
				orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase(), errorsType))
		{
			return;
		}

		for (final Passenger passenger : orderChangeRQ.getQuery().getPassengers().getPassenger())
		{
			if (!validatePassengerQuantity(passenger, errorsType))
			{
				return;
			}

			if (!validatePassengerSurname(passenger, errorsType))
			{
				return;
			}

			if (!validateProfileID(passenger, errorsType))
			{
				return;
			}
		}
	}

	/**
	 * Checks if the {@link ProfileID} in the {@link Passenger} is present
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateProfileID(final Passenger passenger, final ErrorsType errorsType)
	{
		if (Objects.isNull(passenger.getProfileID()) || StringUtils.isEmpty(passenger.getProfileID().getValue()))
		{
			addError(errorsType, getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.MISSING_ORDER_CHANGE_PASSENGER_INFORMATION));
			return false;
		}
		return true;
	}

	/**
	 * Checks if the {@link Passenger} surname is present and valid
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validatePassengerSurname(final Passenger passenger, final ErrorsType errorsType)
	{
		if (Objects.isNull(passenger.getName()) || Objects.isNull(passenger.getName().getSurname()) || StringUtils
				.isEmpty(passenger.getName().getSurname().getValue()))
		{
			addError(errorsType, getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.MISSING_ORDER_CHANGE_PASSENGER_INFORMATION));
			return false;
		}
		return true;
	}

	/**
	 * Checks if the total number of {@link Passengers} in not exceeding the MAX_REMOVE_PASSENGER_QUANTITY quantity
	 *
	 * @param passengers
	 * 		the passengers
	 * @param action
	 * 		the action
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validatePassengerInformationQuantity(final Passengers passengers, final String action,
			final ErrorsType errorsType)
	{
		final NDCActionType enumerationValue = getEnumerationService().getEnumerationValue(NDCActionType.class, action);

		if (Objects.nonNull(enumerationValue) && NDCActionType.REMOVE_PASSENGER.equals(enumerationValue)
				&& passengers.getPassenger().size() > NdcwebservicesConstants.MAX_REMOVE_PASSENGER_QUANTITY)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_TRAVELERS_EXCEEDED));
			return false;
		}
		return true;
	}

	/**
	 * Checks if the {@link Passenger} quantity is MAX_QTY_PER_PASSENGER_INFORMATION
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validatePassengerQuantity(final Passenger passenger, final ErrorsType errorsType)
	{
		if (Objects.isNull(passenger.getPTC()) || StringUtils.isEmpty(passenger.getPTC().getValue())
				|| Objects.isNull(passenger.getPTC().getQuantity()))
		{
			addError(errorsType, getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.MISSING_ORDER_CHANGE_PASSENGER_INFORMATION));
			return false;
		}

		if (passenger.getPTC().getQuantity()
				.compareTo(BigInteger.valueOf(NdcwebservicesConstants.MAX_QTY_PER_PASSENGER_INFORMATION)) != 0)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_QTY_PER_PASSENGER_EXCEEDED));
			return false;
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
	 * Gets enumeration service.
	 *
	 * @return the enumeration service
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 * 		the enumeration service
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
