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

import de.hybris.platform.ndcfacades.ndc.Contacts;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.MsgPartiesType.Sender;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Required;


/**
 * NDC Travel Agency Sender Validator
 *
 * @deprecated since version 4.0, TravelAgencySenderType and email address are not mandatory field. The email is taken
 *             from the user authenticate with the token provided in the request
 */
@Deprecated
public class NDCTravelAgencySenderValidator implements NDCRequestValidator<OrderCreateRQ>
{
	private ConfigurationService configurationService;

	@Override
	public void validate(final OrderCreateRQ orderCreateRQ, final ErrorsType errorsType)
	{
		final Sender sender = orderCreateRQ.getParty().getSender();

		validateTravelAgencyAddress(sender, errorsType);
	}

	/**
	 * Validate travel agency address boolean.
	 *
	 * @param sender
	 * 		the sender
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateTravelAgencyAddress(final Sender sender, final ErrorsType errorsType)
	{
		if (Objects.isNull(sender.getTravelAgencySender()) ||
				Objects.isNull(sender.getTravelAgencySender().getContacts()) ||
				CollectionUtils.isEmpty(sender.getTravelAgencySender().getContacts().getContact()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration()
							.getString(NdcwebservicesConstants.MISSING_TRAVEL_AGENCY_INFORMATION));
			return false;
		}

		final Optional<Contacts.Contact> emailContact = sender.getTravelAgencySender().getContacts().getContact().stream()
				.filter(contact -> Objects.nonNull(contact.getEmailContact())).findFirst();

		if (!emailContact.isPresent() || Objects.isNull(emailContact.get().getEmailContact()) || Objects
				.isNull(emailContact.get().getEmailContact().getAddress()) || StringUtils
				.isEmpty(emailContact.get().getEmailContact().getAddress().getValue()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration()
							.getString(NdcwebservicesConstants.MISSING_TRAVEL_AGENCY_INFORMATION));
			return false;
		}

		if (!EmailValidator.getInstance().isValid(emailContact.get().getEmailContact().getAddress().getValue()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration()
							.getString(NdcwebservicesConstants.WRONG_TRAVEL_AGENCY_EMAIL_FORMAT));
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

}
