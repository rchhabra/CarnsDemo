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

import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * An abstract class to validate NDC Passenger Type
 */
public abstract class NDCAbstractPassengerTypeValidator<N> implements NDCRequestValidator<N>
{
	private ConfigurationService configurationService;

	/**
	 * Validate.
	 *
	 * @param passengers
	 *           the passengers
	 * @param errorsType
	 *           the errors type
	 */
	protected void validate(final List<PassengerType> passengers, final ErrorsType errorsType)
	{
		if (!validatePTCValues(passengers, errorsType))
		{
			return;
		}

		if (!validateMaxPassengers(passengers, errorsType))
		{
			return;
		}

		if (!validateRequiredAdult(passengers, errorsType))
		{
			return;
		}
	}

	/**
	 * Checks if the provided PTC values are valid.
	 *
	 * @param passengers
	 *           the passengers
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validatePTCValues(final List<PassengerType> passengers, final ErrorsType errorsType)
	{
		final List<String> allowedPTCArray = Arrays
				.asList(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ALLOWED_PTC_VALUES).split(","));

		if (passengers.stream().anyMatch(passenger -> StringUtils.isEmpty(passenger.getPTC())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PTC_ELEMENT));
			return false;
		}

		if (passengers.stream().anyMatch(passenger -> !allowedPTCArray.contains(passenger.getPTC())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_PASSENGER_TYPE));
			return false;
		}

		return true;
	}

	/**
	 * Check if the total number of passenger do not exceed the limit.
	 *
	 * @param passengers
	 *           the passengers
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validateMaxPassengers(final List<PassengerType> passengers, final ErrorsType errorsType)
	{
		final int maxGuestQuantity = configurationService.getConfiguration().getInt(TravelfacadesConstants.MAX_TRANSPORT_GUEST_QUANTITY,
				NdcwebservicesConstants.PASSENGER_MAX_LIMIT_DEFAULT);

		if (CollectionUtils.size(passengers) > maxGuestQuantity)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_TRAVELERS_EXCEEDED));
			return false;
		}
		return true;
	}

	/**
	 * Checks if there is at least one Adult per search query.
	 *
	 * @param passengers
	 *           the passengers
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validateRequiredAdult(final List<PassengerType> passengers, final ErrorsType errorsType)
	{
		final boolean adultPassengerPresent = passengers.stream()
				.anyMatch(passenger -> passenger.getPTC().equals(NdcwebservicesConstants.ADULT));

		if (!adultPassengerPresent)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_TRAVELLERS));
			return false;
		}
		return true;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
