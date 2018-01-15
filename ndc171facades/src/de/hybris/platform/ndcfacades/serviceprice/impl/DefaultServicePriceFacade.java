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

package de.hybris.platform.ndcfacades.serviceprice.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRQ;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS;
import de.hybris.platform.ndcfacades.offers.impl.AbstractNDCOffersFacade;
import de.hybris.platform.ndcfacades.serviceprice.ServicePriceFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ServicePriceFacade}
 */
public class DefaultServicePriceFacade extends AbstractNDCOffersFacade implements ServicePriceFacade
{

	private Converter<ServicePriceRQ, OfferRequestData> servicePriceRequestConverter;
	private Converter<OfferResponseData, ServicePriceRS> servicePriceResponseConverter;

	@Override
	public ServicePriceRS getServicePrice(final ServicePriceRQ servicePriceRQ)
	{
		final ServicePriceRS ServicePriceRS = new ServicePriceRS();
		final OfferRequestData offerRequestData = getServicePriceRequestConverter().convert(servicePriceRQ);
		final OfferResponseData offerResponseData = getOffers(offerRequestData);

		//filter offer response data.
		filterOfferResponseData(offerResponseData);

		getServicePriceResponseConverter().convert(offerResponseData, ServicePriceRS);
		return ServicePriceRS;
	}

	/**
	 * Gets service price request converter.
	 *
	 * @return the service price request converter
	 */
	protected Converter<ServicePriceRQ, OfferRequestData> getServicePriceRequestConverter()
	{
		return servicePriceRequestConverter;
	}

	/**
	 * Sets service price request converter.
	 *
	 * @param servicePriceRequestConverter
	 * 		the service price request converter
	 */
	@Required
	public void setServicePriceRequestConverter(final Converter<ServicePriceRQ, OfferRequestData> servicePriceRequestConverter)
	{
		this.servicePriceRequestConverter = servicePriceRequestConverter;
	}

	/**
	 * Gets service price response converter.
	 *
	 * @return the service price response converter
	 */
	protected Converter<OfferResponseData, ServicePriceRS> getServicePriceResponseConverter()
	{
		return servicePriceResponseConverter;
	}

	/**
	 * Sets service price response converter.
	 *
	 * @param servicePriceResponseConverter
	 * 		the service price response converter
	 */
	@Required
	public void setServicePriceResponseConverter(final Converter<OfferResponseData, ServicePriceRS> servicePriceResponseConverter)
	{
		this.servicePriceResponseConverter = servicePriceResponseConverter;
	}

}
