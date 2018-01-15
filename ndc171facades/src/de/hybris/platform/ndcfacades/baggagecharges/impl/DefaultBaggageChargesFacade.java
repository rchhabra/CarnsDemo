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

package de.hybris.platform.ndcfacades.baggagecharges.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.baggagecharges.BaggageChargesFacade;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS;
import de.hybris.platform.ndcfacades.offers.impl.AbstractNDCOffersFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link BaggageChargesFacade}
 */
public class DefaultBaggageChargesFacade extends AbstractNDCOffersFacade implements BaggageChargesFacade
{
	private Converter<BaggageChargesRQ, OfferRequestData> baggageChargesRequestConverter;
	private Converter<OfferResponseData, BaggageChargesRS> baggageChargesResponseConverter;

	@Override
	public BaggageChargesRS getBaggageCharges(final BaggageChargesRQ baggageChargesRQ)
	{
		final BaggageChargesRS baggageChargesRS = new BaggageChargesRS();
		final OfferRequestData offerRequestData = getBaggageChargesRequestConverter().convert(baggageChargesRQ);
		final OfferResponseData offerResponseData = getOffers(offerRequestData);

		//filter offer response data.
		filterOfferResponseData(offerResponseData);

		getBaggageChargesResponseConverter().convert(offerResponseData, baggageChargesRS);
		return baggageChargesRS;
	}

	/**
	 * Gets baggage charges request converter.
	 *
	 * @return the baggage charges request converter
	 */
	protected Converter<BaggageChargesRQ, OfferRequestData> getBaggageChargesRequestConverter()
	{
		return baggageChargesRequestConverter;
	}

	/**
	 * Sets baggage charges request converter.
	 *
	 * @param baggageChargesRequestConverter
	 * 		the baggage charges request converter
	 */
	@Required
	public void setBaggageChargesRequestConverter(
			final Converter<BaggageChargesRQ, OfferRequestData> baggageChargesRequestConverter)
	{
		this.baggageChargesRequestConverter = baggageChargesRequestConverter;
	}

	/**
	 * Gets baggage charges response converter.
	 *
	 * @return the baggage charges response converter
	 */
	protected Converter<OfferResponseData, BaggageChargesRS> getBaggageChargesResponseConverter()
	{
		return baggageChargesResponseConverter;
	}

	/**
	 * Sets baggage charges response converter.
	 *
	 * @param baggageChargesResponseConverter
	 * 		the baggage charges response converter
	 */
	@Required
	public void setBaggageChargesResponseConverter(
			final Converter<OfferResponseData, BaggageChargesRS> baggageChargesResponseConverter)
	{
		this.baggageChargesResponseConverter = baggageChargesResponseConverter;
	}

}
