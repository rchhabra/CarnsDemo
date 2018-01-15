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

package de.hybris.platform.ndcfacades.servicelist.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcfacades.ndc.ServiceListRS;
import de.hybris.platform.ndcfacades.offers.impl.AbstractNDCOffersFacade;
import de.hybris.platform.ndcfacades.servicelist.ServiceListFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ServiceListFacade}
 */
public class DefaultServiceListFacade extends AbstractNDCOffersFacade implements ServiceListFacade
{

	private Converter<ServiceListRQ, OfferRequestData> serviceListRequestConverter;
	private Converter<OfferResponseData, ServiceListRS> serviceListResponseConverter;

	@Override
	public ServiceListRS getServiceList(final ServiceListRQ serviceListRQ)
	{
		final ServiceListRS serviceListRS = new ServiceListRS();
		final OfferRequestData offerRequestData = getServiceListRequestConverter().convert(serviceListRQ);
		final OfferResponseData offerResponseData = getOffers(offerRequestData);

		//filter offer response data.
		filterOfferResponseData(offerResponseData);

		getServiceListResponseConverter().convert(offerResponseData, serviceListRS);
		return serviceListRS;
	}

	/**
	 * Gets service list request converter.
	 *
	 * @return the service list request converter
	 */
	protected Converter<ServiceListRQ, OfferRequestData> getServiceListRequestConverter()
	{
		return serviceListRequestConverter;
	}

	/**
	 * Sets service list request converter.
	 *
	 * @param serviceListRequestConverter
	 * 		the service list request converter
	 */
	@Required
	public void setServiceListRequestConverter(final Converter<ServiceListRQ, OfferRequestData> serviceListRequestConverter)
	{
		this.serviceListRequestConverter = serviceListRequestConverter;
	}

	/**
	 * Gets service list response converter.
	 *
	 * @return the service list response converter
	 */
	protected Converter<OfferResponseData, ServiceListRS> getServiceListResponseConverter()
	{
		return serviceListResponseConverter;
	}

	/**
	 * Sets service list response converter.
	 *
	 * @param serviceListResponseConverter
	 * 		the service list response converter
	 */
	@Required
	public void setServiceListResponseConverter(final Converter<OfferResponseData, ServiceListRS> serviceListResponseConverter)
	{
		this.serviceListResponseConverter = serviceListResponseConverter;
	}

}
