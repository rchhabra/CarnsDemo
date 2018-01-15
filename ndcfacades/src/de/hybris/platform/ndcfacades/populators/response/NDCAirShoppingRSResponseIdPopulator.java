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
package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseIDType;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseIDType.ResponseID;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Required;


/**
 * NDC AirShopping Response Id Populator for NDC {@link AirShoppingRS}
 */
public class NDCAirShoppingRSResponseIdPopulator implements Populator<FareSelectionData, AirShoppingRS>
{
	private ConfigurationService configurationService;

	@Override
	public void populate(final FareSelectionData fareSelectionData, final AirShoppingRS airShoppingRS) throws ConversionException
	{
		final ShoppingResponseIDType shoppingResponseId = new ShoppingResponseIDType();

		populateResponseID(shoppingResponseId);

		shoppingResponseId.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));
		airShoppingRS.setShoppingResponseID(shoppingResponseId);
	}

	/**
	 * Generates a random id for the {@link ResponseID}
	 *
	 * @param shoppingResponseId
	 * 		the shopping response id
	 */
	protected void populateResponseID(final ShoppingResponseIDType shoppingResponseId)
	{
		final ResponseID responseId = new ResponseID();

		responseId.setValue(new BigInteger(130, new SecureRandom()).toString(32));

		shoppingResponseId.setResponseID(responseId);
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
