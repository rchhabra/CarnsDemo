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

package de.hybris.platform.ndcfacades.baggagelist.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.baggagelist.BaggageListFacade;
import de.hybris.platform.ndcfacades.ndc.BaggageListRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageListRS;
import de.hybris.platform.ndcfacades.offers.impl.AbstractNDCOffersFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link BaggageListFacade}
 */
public class DefaultBaggageListFacade extends AbstractNDCOffersFacade implements BaggageListFacade
{
	private Converter<BaggageListRQ, OfferRequestData> baggageListRequestConverter;
	private Converter<OfferResponseData, BaggageListRS> baggageListResponseConverter;

	@Override
	public BaggageListRS getBaggageList(final BaggageListRQ baggageListRQ)
	{
		final BaggageListRS baggageListRS = new BaggageListRS();
		final OfferRequestData offerRequestData = getBaggageListRequestConverter().convert(baggageListRQ);
		final OfferResponseData offerResponseData = getOffers(offerRequestData);

		//filter offer response data.
		filterOfferResponseData(offerResponseData);

		getBaggageListResponseConverter().convert(offerResponseData, baggageListRS);
		return baggageListRS;
	}

	/**
	 * Gets baggage list request converter.
	 *
	 * @return the baggage list request converter
	 */
	protected Converter<BaggageListRQ, OfferRequestData> getBaggageListRequestConverter()
	{
		return baggageListRequestConverter;
	}

	/**
	 * Sets baggage list request converter.
	 *
	 * @param baggageListRequestConverter
	 * 		the baggage list request converter
	 */
	@Required
	public void setBaggageListRequestConverter(final Converter<BaggageListRQ, OfferRequestData> baggageListRequestConverter)
	{
		this.baggageListRequestConverter = baggageListRequestConverter;
	}

	/**
	 * Gets baggage list response converter.
	 *
	 * @return the baggage list response converter
	 */
	protected Converter<OfferResponseData, BaggageListRS> getBaggageListResponseConverter()
	{
		return baggageListResponseConverter;
	}

	/**
	 * Sets baggage list response converter.
	 *
	 * @param baggageListResponseConverter
	 * 		the baggage list response converter
	 */
	@Required
	public void setBaggageListResponseConverter(final Converter<OfferResponseData, BaggageListRS> baggageListResponseConverter)
	{
		this.baggageListResponseConverter = baggageListResponseConverter;
	}

}
