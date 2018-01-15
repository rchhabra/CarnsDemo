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

import de.hybris.platform.ndcfacades.ndc.Contacts.Contact;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.Passengers;
import de.hybris.platform.ndcfacades.ndc.Passenger;
import de.hybris.platform.ndcfacades.ndc.TravelerGenderSimpleType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * NDC {@link OrderCreateRQ} {@link Passengers} Validator
 */
public class NDCOrderCreatePassengerValidator implements NDCRequestValidator<OrderCreateRQ>
{
	private static final Logger LOG = Logger.getLogger(NDCOrderCreatePassengerValidator.class);

	private ConfigurationService configurationService;
	private UserService userService;
	private List<TravelerGenderSimpleType> allowedGenders;

	@Override
	public void validate(final OrderCreateRQ orderCreateRQ, final ErrorsType errorsType)
	{
		if (!validatePassengerInformationQuantity(orderCreateRQ.getQuery().getPassengers(), errorsType))
		{
			return;
		}

		for (final Passenger passenger : orderCreateRQ.getQuery().getPassengers().getPassenger())
		{
			if (!validatePassengerName(passenger, errorsType))
			{
				return;
			}

			if (!validatePassengerInfo(passenger, errorsType))
			{
				return;
			}

			if (!validatePassengerQuantity(passenger, errorsType))
			{
				return;
			}

			if (!validateTitle(passenger, errorsType))
			{
				return;
			}

			if (!validateGender(passenger, errorsType))
			{
				return;
			}

			if (!validateEmailAddress(passenger, errorsType))
			{
				return;
			}
		}
	}

	/**
	 * Checks if the gender provided is valid and in the allowedGenders list
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 * @return boolean
	 */
	protected boolean validateGender(final Passenger passenger, final ErrorsType errorsType)
	{
		if (Objects.isNull(passenger.getGender()) || Objects.isNull(passenger.getGender().getValue())
				|| StringUtils.isEmpty(passenger.getGender().getValue().value()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PASSENGER_INFORMATION));
			return false;
		}

		if (allowedGenders.stream()
				.noneMatch(allowedGender -> allowedGender.value().equals(passenger.getGender().getValue().value())))
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.GENDER_NOT_ALLOWED));
			return false;
		}

		return true;

	}

	/**
	 * Validate the email address of the passenger
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 * @return boolean
	 */
	protected boolean validateEmailAddress(final Passenger passenger, final ErrorsType errorsType)
	{
		if (Objects.isNull(passenger.getContacts()))
		{
			return true;
		}

		for (final Contact contact : passenger.getContacts().getContact())
		{
			if (!Objects.isNull(contact.getEmailContact()) && !Objects.isNull(contact.getEmailContact().getAddress().getValue()))
			{
				if (!EmailValidator.getInstance().isValid(contact.getEmailContact().getAddress().getValue()))
				{
					addError(errorsType, getConfigurationService().getConfiguration()
							.getString(NdcwebservicesConstants.WRONG_PASSENGER_EMAIL_FORMAT));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the passenger title is valid
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 * @return boolean
	 */
	protected boolean validateTitle(final Passenger passenger, final ErrorsType errorsType)
	{
		try
		{
			getUserService().getTitleForCode(passenger.getName().getTitle().toLowerCase());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.debug(e);
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_TITLE));
			return false;
		}
		return true;
	}

	/**
	 * Checks if the total number of passengers in not exceeding the MAX_TRAVELERS quantity
	 *
	 * @param passengers
	 * 		the passengers
	 * @param errorsType
	 * 		the errors type
	 * @return boolean
	 */
	protected boolean validatePassengerInformationQuantity(final Passengers passengers, final ErrorsType errorsType)
	{
		final int maxGuestQuantity = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_TRANSPORT_GUEST_QUANTITY, NdcwebservicesConstants.PASSENGER_MAX_LIMIT_DEFAULT);
		if (passengers.getPassenger().size() > maxGuestQuantity)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_TRAVELERS_EXCEEDED));
			return false;
		}
		return true;
	}

	/**
	 * Checks if the passenger quantity is MAX_QTY_PER_PASSENGER_INFORMATION
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 * @return boolean
	 */
	protected boolean validatePassengerQuantity(final Passenger passenger, final ErrorsType errorsType)
	{
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
	 * Checks if the passenger name is present and valid
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 * @return boolean
	 */
	protected boolean validatePassengerName(final Passenger passenger, final ErrorsType errorsType)
	{
		if (Objects.isNull(passenger.getName()) || Objects.isNull(passenger.getName().getGiven())
				|| CollectionUtils.isEmpty(passenger.getName().getGiven())
				|| StringUtils.isEmpty(passenger.getName().getGiven().get(0).getValue())
				|| Objects.isNull(passenger.getName().getSurname())
				|| StringUtils.isEmpty(passenger.getName().getSurname().getValue()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PASSENGER_INFORMATION));
			return false;
		}
		return true;
	}

	/**
	 * Checks if the passenger information are present
	 *
	 * @param passenger
	 * 		the passenger
	 * @param errorsType
	 * 		the errors type
	 * @return boolean
	 */
	protected boolean validatePassengerInfo(final Passenger passenger, final ErrorsType errorsType)
	{
		if (Objects.isNull(passenger.getPTC()) || StringUtils.isEmpty(passenger.getPTC().getValue())
				|| Objects.isNull(passenger.getPTC().getQuantity()) || StringUtils.isEmpty(passenger.getName().getTitle()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PASSENGER_INFORMATION));
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
	 * Gets user service.
	 *
	 * @return the user service
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * Sets user service.
	 *
	 * @param userService
	 * 		the user service
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Gets allowed genders.
	 *
	 * @return the allowed genders
	 */
	protected List<TravelerGenderSimpleType> getAllowedGenders()
	{
		return allowedGenders;
	}

	/**
	 * Sets allowed genders.
	 *
	 * @param allowedGenders
	 * 		the allowed genders
	 */
	@Required
	public void setAllowedGenders(final List<TravelerGenderSimpleType> allowedGenders)
	{
		this.allowedGenders = allowedGenders;
	}
}
