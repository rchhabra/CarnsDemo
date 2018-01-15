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

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to validate currency for NDC requests
 */
public abstract class NDCOffersCurrencyValidator<N> implements NDCRequestValidator<N>
{

	private ConfigurationService configurationService;
	private CommerceCommonI18NService commerceCommonI18NService;
	private CommonI18NService commonI18NService;

	/**
	 * This method validates whether given ISO code is exist in system.
	 *
	 * @param currencyCode
	 * 		the currency code
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean isValidIsoCode(final String currencyCode, final ErrorsType errorsType)
	{
		if (StringUtils.isEmpty(currencyCode))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_OFFERS_CURRENCY));
			return Boolean.FALSE;
		}

		Collection<CurrencyModel> currencies = getCommerceCommonI18NService().getAllCurrencies();
		if (currencies.isEmpty())
		{
			currencies = getCommonI18NService().getAllCurrencies();
		}

		for (final CurrencyModel currency : currencies)
		{
			if (StringUtils.equals(currency.getIsocode(), currencyCode))
			{
				return Boolean.TRUE;
			}
		}
		addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_CURRENCY));
		return Boolean.FALSE;
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
	 * Gets commerce common i 18 n service.
	 *
	 * @return the commerce common i 18 n service
	 */
	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	/**
	 * Sets commerce common i 18 n service.
	 *
	 * @param commerceCommonI18NService
	 * 		the commerce common i 18 n service
	 */
	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
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
