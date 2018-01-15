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

package de.hybris.platform.ndcfacades.populators.request;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRQ;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;


/**
 * Concrete class to populate currency for {@link ServicePriceRQ}
 */
public class NDCServicePriceCurrencyPopulator extends NDCAbstractOffersCurrencyPopulator
		implements Populator<ServicePriceRQ, OfferRequestData>
{

	@Override
	public void populate(final ServicePriceRQ source, final OfferRequestData target) throws ConversionException
	{
		if (!Objects.isNull(source.getParameters()) && !Objects.isNull(source.getParameters().getCurrCodes()))
		{
			populate(source.getParameters().getCurrCodes());
		}
	}
}
