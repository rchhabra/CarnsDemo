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
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrdCreateMetadataType;
import de.hybris.platform.ndcfacades.ndc.OrdCreateMetadataType.Other.OtherMetadata;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Collection;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The NDC Metadata Currency Validation
 * Check if a currency metadata key is specified
 */
public class NDCMetadataCurrencyValidator implements NDCRequestValidator<OrderCreateRQ>
{
	private ConfigurationService configurationService;
	private CommerceCommonI18NService commerceCommonI18NService;
	private CommonI18NService commonI18NService;

	@Override
	public void validate(final OrderCreateRQ orderCreateRQ, final ErrorsType errorsType)
	{
		validateCurrency(orderCreateRQ.getQuery().getMetadata(), errorsType);
	}

	/**
	 * Validate currency boolean.
	 *
	 * @param metadata
	 * 		the metadata
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateCurrency(final OrdCreateMetadataType metadata, final ErrorsType errorsType)
	{
		if (Objects.nonNull(metadata) && Objects.nonNull(metadata.getOther()) && Objects
				.nonNull(metadata.getOther().getOtherMetadata()) && CollectionUtils.isNotEmpty(metadata.getOther().getOtherMetadata()))
		{
			for (final OtherMetadata otherMetadata : metadata.getOther().getOtherMetadata())
			{
				if (Objects.nonNull(otherMetadata.getCurrencyMetadatas()) && CollectionUtils.isNotEmpty(otherMetadata.getCurrencyMetadatas()
						.getCurrencyMetadata()))
				{
					return isValidIsoCode(otherMetadata.getCurrencyMetadatas().getCurrencyMetadata().get(0).getMetadataKey(),
							errorsType);
				}
			}
		}

		addError(errorsType,
				getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_METADATA_CURRENCY));
		return false;
	}

	/**
	 * Is valid iso code boolean.
	 *
	 * @param metadataKey
	 * 		the metadata key
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean isValidIsoCode(final String metadataKey, final ErrorsType errorsType)
	{
		if (StringUtils.isEmpty(metadataKey))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_METADATA_CURRENCY));
			return false;
		}

		Collection<CurrencyModel> currencies = getCommerceCommonI18NService().getAllCurrencies();
		if (currencies.isEmpty())
		{
			currencies = getCommonI18NService().getAllCurrencies();
		}

		for (final CurrencyModel currency : currencies)
		{
			if (StringUtils.equals(currency.getIsocode(), metadataKey))
			{
				return true;
			}
		}
		addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_CURRENCY));
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
