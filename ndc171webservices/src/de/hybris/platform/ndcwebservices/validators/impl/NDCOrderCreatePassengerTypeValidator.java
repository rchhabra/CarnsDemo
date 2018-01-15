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

import de.hybris.platform.ndcfacades.ndc.ContactInformationType;
import de.hybris.platform.ndcfacades.ndc.EmailAddressType;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.GenderCodeContentType;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Concrete class to validate passenger data for {@link OrderCreateRQ}
 */
public class NDCOrderCreatePassengerTypeValidator extends NDCAbstractPassengerTypeValidator<OrderCreateRQ>
{
	private static final Logger LOG = Logger.getLogger(NDCOrderCreatePassengerTypeValidator.class);

	private UserService userService;
	private List<GenderCodeContentType> allowedGenders;

	@Override
	public void validate(final OrderCreateRQ orderCreateRQ, final ErrorsType errorsType)
	{
		final List<PassengerType> passengers = orderCreateRQ.getQuery().getDataLists().getPassengerList().getPassenger();

		super.validate(passengers, errorsType);

		if (!validatePassengerIndividual(passengers, errorsType))
		{
			return;
		}

		if (!validateEmailAddress(passengers, errorsType))
		{
			return;
		}
	}

	/**
	 * Validate the email address of the passenger.
	 *
	 * @param passengers
	 *           the passengers
	 * @param errorsType
	 *           the errors type
	 * @return boolean
	 */
	protected boolean validateEmailAddress(final List<PassengerType> passengers, final ErrorsType errorsType)
	{
		final List<Object> contactInformations = passengers.stream()
				.filter(passenger -> (Objects.nonNull(passenger.getContactInfoRef())
						&& passenger.getContactInfoRef() instanceof ContactInformationType))
				.map(PassengerType::getContactInfoRef).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(contactInformations))
		{
			return true;
		}

		for (final Object ContactInformation : contactInformations)
		{
			final ContactInformationType contactInformationType = (ContactInformationType) ContactInformation;
			for (final EmailAddressType emailAddress : contactInformationType.getEmailAddress())
			{
				if (!EmailValidator.getInstance().isValid(emailAddress.getEmailAddressValue()))
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
	 * Checks if the passenger name is present and valid
	 *
	 * @param passengers
	 *           the passenger
	 * @param errorsType
	 *           the errors type
	 *
	 * @return boolean
	 */
	protected boolean validatePassengerIndividual(final List<PassengerType> passengers, final ErrorsType errorsType)
	{
		for (final PassengerType passenger : passengers)
		{
			if (Objects.isNull(passenger.getIndividual()) || CollectionUtils.isEmpty(passenger.getIndividual().getGivenName())
					|| StringUtils.isEmpty(passenger.getIndividual().getGivenName().stream().findFirst().orElse(null))
					|| StringUtils.isEmpty(passenger.getIndividual().getSurname())
					|| StringUtils.isEmpty(passenger.getIndividual().getNameTitle())
					|| Objects.isNull(passenger.getIndividual().getGender())
					|| StringUtils.isEmpty(passenger.getIndividual().getGender().value()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PASSENGER_INFORMATION));
				return false;
			}
			try
			{
				getUserService().getTitleForCode(passenger.getIndividual().getNameTitle().toLowerCase());
			}
			catch (final UnknownIdentifierException e)
			{
				LOG.debug(e);
				addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_TITLE));
				return false;
			}

			if (allowedGenders.stream()
					.noneMatch(allowedGender -> allowedGender.value().equals(passenger.getIndividual().getGender().value())))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.GENDER_NOT_ALLOWED));
				return false;
			}
		}
		return true;
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
	 * @return the allowedGenders
	 */
	protected List<GenderCodeContentType> getAllowedGenders()
	{
		return allowedGenders;
	}

	/**
	 * @param allowedGenders
	 *           the allowedGenders to set
	 */
	@Required
	public void setAllowedGenders(final List<GenderCodeContentType> allowedGenders)
	{
		this.allowedGenders = allowedGenders;
	}
}
