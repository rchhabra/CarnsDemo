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

package de.hybris.platform.ndcfacades.baggageallowance.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.baggageallowance.BaggageAllowanceFacade;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS;
import de.hybris.platform.ndcfacades.offers.impl.AbstractNDCOffersFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link BaggageAllowanceFacade}
 */
public class DefaultBaggageAllowanceFacade extends AbstractNDCOffersFacade implements BaggageAllowanceFacade
{
	private Converter<BaggageAllowanceRQ, OfferRequestData> baggageAllowanceRequestConverter;
	private Converter<OfferResponseData, BaggageAllowanceRS> baggageAllowanceResponseConverter;

	@Override
	public BaggageAllowanceRS getBaggageAllowance(final BaggageAllowanceRQ BaggageAllowanceRQ)
	{
		final BaggageAllowanceRS baggageAllowanceRS = new BaggageAllowanceRS();
		final OfferRequestData offerRequestData = getBaggageAllowanceRequestConverter().convert(BaggageAllowanceRQ);
		final OfferResponseData offerResponseData = getOffers(offerRequestData);

		//filter offer response data.
		filterOfferResponseData(offerResponseData);

		getBaggageAllowanceResponseConverter().convert(offerResponseData, baggageAllowanceRS);
		return baggageAllowanceRS;
	}

	/**
	 * Gets baggage allowance request converter.
	 *
	 * @return the baggage allowance request converter
	 */
	protected Converter<BaggageAllowanceRQ, OfferRequestData> getBaggageAllowanceRequestConverter()
	{
		return baggageAllowanceRequestConverter;
	}

	/**
	 * Sets baggage allowance request converter.
	 *
	 * @param baggageAllowanceRequestConverter
	 * 		the baggage allowance request converter
	 */
	@Required
	public void setBaggageAllowanceRequestConverter(
			final Converter<BaggageAllowanceRQ, OfferRequestData> baggageAllowanceRequestConverter)
	{
		this.baggageAllowanceRequestConverter = baggageAllowanceRequestConverter;
	}

	/**
	 * Gets baggage allowance response converter.
	 *
	 * @return the baggage allowance response converter
	 */
	protected Converter<OfferResponseData, BaggageAllowanceRS> getBaggageAllowanceResponseConverter()
	{
		return baggageAllowanceResponseConverter;
	}

	/**
	 * Sets baggage allowance response converter.
	 *
	 * @param baggageAllowanceResponseConverter
	 * 		the baggage allowance response converter
	 */
	@Required
	public void setBaggageAllowanceResponseConverter(
			final Converter<OfferResponseData, BaggageAllowanceRS> baggageAllowanceResponseConverter)
	{
		this.baggageAllowanceResponseConverter = baggageAllowanceResponseConverter;
	}

}
