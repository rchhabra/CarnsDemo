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
import de.hybris.platform.ndcfacades.ndc.Travelers.Traveler;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to validate NDC Passenger Type
 */
public abstract class NDCAbstractPassengerTypeValidator<N> implements NDCRequestValidator<N>
{
	private ConfigurationService configurationService;

	protected void validate(final List<Traveler> travelers, final ErrorsType errorsType)
	{
		if (!validatePassengerQuantity(travelers, errorsType))
		{
			return;
		}

		if (!validateMinPassengers(travelers, errorsType))
		{
			return;
		}

		if (!validateMaxPassengers(travelers, errorsType))
		{
			return;
		}

		if (!validateRequiredAdult(travelers, errorsType))
		{
			return;
		}

		validatePTCValues(travelers, errorsType);
	}

	/**
	 * Checks if the provided PTC values are valid
	 *
	 * @param travelers
	 * @param errorsType
	 * @return
	 */
	protected boolean validatePTCValues(final List<Traveler> travelers, final ErrorsType errorsType)
	{
		final List<String> allowedPTCArray = Arrays
				.asList(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ALLOWED_PTC_VALUES).split(","));

		for (final Traveler traveler : travelers)
		{
			if (CollectionUtils.isEmpty(traveler.getAnonymousTraveler()) || Objects
					.isNull(traveler.getAnonymousTraveler().get(0).getPTC()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_TRAVELER_INFORMATION));
				return false;
			}

			if (!allowedPTCArray.contains(traveler.getAnonymousTraveler().get(0).getPTC().getValue()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_PASSENGER_TYPE));
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the number of passenger is positive
	 *
	 * @param travelers
	 * @param errorsType
	 * @return
	 */
	protected boolean validateMinPassengers(final List<Traveler> travelers, final ErrorsType errorsType)
	{
		final Optional<Traveler> optTraveler = travelers.stream()
				.filter(traveler -> traveler.getAnonymousTraveler().stream()
						.anyMatch(anonymousTraveler -> anonymousTraveler.getPTC().getQuantity().compareTo(BigInteger.ZERO) < 0))
				.findAny();

		if (optTraveler.isPresent())
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.NEGATIVE_PASSENGER_QUANTITY));
			return false;
		}

		return true;
	}

	/**
	 * Check if the total number of passenger do not exceed the limit
	 *
	 * @param travelers
	 * @param errorsType
	 * @return
	 */
	protected boolean validateMaxPassengers(final List<Traveler> travelers, final ErrorsType errorsType)
	{
		final int maxGuestQuantity = configurationService.getConfiguration()
				.getInt(TravelfacadesConstants.MAX_TRANSPORT_GUEST_QUANTITY, NdcwebservicesConstants.PASSENGER_MAX_LIMIT_DEFAULT);
		final Optional<Traveler> optTraveler = travelers.stream()
				.filter(traveler -> traveler.getAnonymousTraveler().stream().anyMatch(anonymousTraveler -> anonymousTraveler.getPTC()
						.getQuantity().compareTo(BigInteger.valueOf(maxGuestQuantity)) > 0))
				.findAny();

		if (optTraveler.isPresent())
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_TRAVELERS_EXCEEDED));
			return false;
		}

		if (travelers.stream()
				.mapToInt(traveler -> traveler.getAnonymousTraveler().stream()
						.mapToInt(anonymousTraveler -> anonymousTraveler.getPTC().getQuantity().intValue()).sum())
				.sum() > maxGuestQuantity)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_TRAVELERS_EXCEEDED));
			return false;
		}
		return true;
	}

	/**
	 * Checks if the PTC quantity is specified
	 *
	 * @param travelers
	 * @param errorsType
	 * @return
	 */
	protected boolean validatePassengerQuantity(final List<Traveler> travelers, final ErrorsType errorsType)
	{
		if (travelers.stream().anyMatch(traveler -> traveler.getAnonymousTraveler().stream()
				.anyMatch(anonymousTraveler -> Objects.isNull(anonymousTraveler.getPTC()))))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PTC_ELEMENT));
			return false;
		}
		if (travelers.stream().anyMatch(traveler -> traveler.getAnonymousTraveler().stream()
				.anyMatch(anonymousTraveler -> Objects.isNull(anonymousTraveler.getPTC().getQuantity()))))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.QUANTITY_NOT_SPECIFIED));
			return false;
		}
		return true;
	}

	/**
	 * Checks if there is at least one Adult per search query
	 *
	 * @param travelers
	 * @param errorsType
	 * @return
	 */
	protected boolean validateRequiredAdult(final List<Traveler> travelers, final ErrorsType errorsType)
	{
		final Optional<Traveler> optTraveler = travelers.stream()
				.filter(traveler -> traveler.getAnonymousTraveler().stream()
						.anyMatch(anonymousTraveler -> anonymousTraveler.getPTC().getValue().equals(NdcwebservicesConstants.ADULT)
								&& anonymousTraveler.getPTC().getQuantity().intValue() > 0))
				.findAny();

		if (!optTraveler.isPresent())
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
