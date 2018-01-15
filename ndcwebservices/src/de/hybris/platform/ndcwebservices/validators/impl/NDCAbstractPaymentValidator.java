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
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.ndcfacades.ndc.PaymentCardType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.time.LocalDate;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to validate the {@link OrderPaymentFormType}
 */
public abstract class NDCAbstractPaymentValidator<N> implements NDCRequestValidator<N>
{
	private static final Logger LOG = Logger.getLogger(NDCAbstractPaymentValidator.class);

	private ConfigurationService configurationService;
	private CommonI18NService commonI18NService;

	/**
	 * Validate.
	 *
	 * @param orderPaymentFormType
	 * 		the order payment form type
	 * @param errorsType
	 * 		the errors type
	 */
	public void validate(final OrderPaymentFormType orderPaymentFormType, final ErrorsType errorsType)
	{
		if (!validateCardPayment(orderPaymentFormType, errorsType))
		{
			return;
		}

		if (!validatePaymentRequiredFields(orderPaymentFormType, errorsType))
		{
			return;
		}

		if (!validateCardValidity(orderPaymentFormType, errorsType))
		{
			return;
		}

		validateCountryCode(orderPaymentFormType, errorsType);
	}


	/**
	 * Validates the country code provided in the {@link OrderPaymentFormType}
	 *
	 * @param orderPaymentFormType
	 * 		the order payment form type
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateCountryCode(final OrderPaymentFormType orderPaymentFormType, final ErrorsType errorsType)
	{
		for (final Contact contact : orderPaymentFormType.getPayer().getContacts().getContact())
		{
			if (Objects.nonNull(contact.getAddressContact()) && (Objects
					.nonNull(contact.getAddressContact().getCountryCode().getValue())))
			{
				try
				{
					getCommonI18NService().getCountry(contact.getAddressContact().getCountryCode().getValue());
				}
				catch (final UnknownIdentifierException e)
				{
					addError(errorsType,
							getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_COUNTRY_CODE));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the {@link OrderPaymentFormType} is present
	 *
	 * @param orderPaymentFormType
	 * 		the order payment form type
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateCardPayment(final OrderPaymentFormType orderPaymentFormType, final ErrorsType errorsType)
	{
		if (Objects.isNull(orderPaymentFormType))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PAYMENT_INFORMATION));
			return false;
		}
		return true;
	}

	/**
	 * Validates the expiration date of the provided {@link PaymentCardType} in the {@link OrderPaymentFormType}
	 *
	 * @param orderPaymentFormType
	 * 		the order payment form type
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateCardValidity(final OrderPaymentFormType orderPaymentFormType, final ErrorsType errorsType)
	{
		final PaymentCardType paymentCard = orderPaymentFormType.getMethod().getPaymentCard();

		final String month = paymentCard.getEffectiveExpireDate().getExpiration().substring(0, 2);
		final String year = "20" + paymentCard.getEffectiveExpireDate().getExpiration().substring(2, 4);

		final LocalDate expirationDate = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), 1);

		if (expirationDate.compareTo(LocalDate.now()) < 0)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.CREDIT_CARD_EXPIRED));
			return false;
		}
		return true;
	}

	/**
	 * Validates the required field in the {@link OrderPaymentFormType}
	 *
	 * @param orderPaymentFormType
	 * 		the order payment form type
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validatePaymentRequiredFields(final OrderPaymentFormType orderPaymentFormType, final ErrorsType errorsType)
	{
		final PaymentCardType paymentCard = orderPaymentFormType.getMethod().getPaymentCard();

		if (Objects.isNull(paymentCard))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PAYMENT_INFORMATION));
			return false;
		}

		if (Objects.isNull(paymentCard.getCardType()) || Objects.isNull(paymentCard.getCardNumber())
				|| Objects.isNull(paymentCard.getSeriesCode()) || Objects.isNull(paymentCard.getEffectiveExpireDate()) || Objects
				.isNull(paymentCard.getEffectiveExpireDate().getExpiration()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PAYMENT_INFORMATION));
			return false;
		}

		if(StringUtils.length(paymentCard.getCardNumber().getValue()) < NdcwebservicesConstants.MIN_CREDIT_CARD_LENGTH)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_CARD_NUMBER_PROVIDED));
			return false;
		}

		if(Objects.isNull(orderPaymentFormType.getPayer()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PAYMENT_INFORMATION));
			return false;
		}

		final OrderPaymentFormType.Payer payer = orderPaymentFormType.getPayer();

		if (Objects.isNull(payer.getName()) || Objects.isNull(payer.getName().getGiven()) || CollectionUtils
				.isEmpty(payer.getName().getGiven()) || StringUtils.isEmpty(payer.getName().getGiven().get(0).getValue()) || Objects
				.isNull(payer.getName().getSurname()) || StringUtils.isEmpty(payer.getName().getSurname().getValue()) ||
				Objects.isNull(payer.getContacts()) || CollectionUtils.isEmpty(payer.getContacts().getContact()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PAYMENT_INFORMATION));
			return false;
		}

		for (final Contact contact : payer.getContacts().getContact())
		{
			if (Objects.isNull(contact.getAddressContact()))
			{
				continue;
			}

			if (CollectionUtils.isEmpty(contact.getAddressContact().getStreet())
					|| StringUtils.isEmpty(contact.getAddressContact().getStreet().get(0)) || Objects
					.isNull(contact.getAddressContact().getCityName()) || StringUtils
					.isEmpty(contact.getAddressContact().getCityName()) || Objects
					.isNull(contact.getAddressContact().getPostalCode()) || StringUtils
					.isEmpty(contact.getAddressContact().getPostalCode()) || Objects
					.isNull(contact.getAddressContact().getCountryCode()) || Objects
					.isNull(contact.getAddressContact().getCountryCode().getValue()) || StringUtils
					.isEmpty(contact.getAddressContact().getCountryCode().getValue()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PAYMENT_INFORMATION));
				return false;
			}
			else
			{
				return true;
			}
		}

		addError(errorsType,
				getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PAYMENT_INFORMATION));
		return false;
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
	 * Gets common i 18 n service.
	 *
	 * @return the common i 18 n service
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 * 		the common i 18 n service
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
