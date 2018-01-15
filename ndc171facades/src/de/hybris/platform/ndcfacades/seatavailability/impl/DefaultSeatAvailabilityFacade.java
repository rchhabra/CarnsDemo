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

package de.hybris.platform.ndcfacades.seatavailability.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS;
import de.hybris.platform.ndcfacades.seatavailability.SeatAvailabilityFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchPipelineManager;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SeatAvailabilityFacade}
 */
public class DefaultSeatAvailabilityFacade implements SeatAvailabilityFacade
{
	private Converter<SeatAvailabilityRQ, OfferRequestData> seatAvailabilityRQConverter;
	private Converter<OfferResponseData, SeatAvailabilityRS> seatAvailabilityRSConverter;
	private AncillarySearchPipelineManager ndcAccommodationSearchPipelineManager;

	@Override
	public SeatAvailabilityRS getSeatMap(final SeatAvailabilityRQ seatAvailabilityRQ)
	{
		final SeatAvailabilityRS seatAvailabilityRS = new SeatAvailabilityRS();
		final OfferRequestData offerRequestData = getSeatAvailabilityRQConverter().convert(seatAvailabilityRQ);
		final OfferResponseData offerResponseData = getNdcAccommodationSearchPipelineManager().executePipeline(offerRequestData);
		getSeatAvailabilityRSConverter().convert(offerResponseData, seatAvailabilityRS);
		return seatAvailabilityRS;
	}

	/**
	 * Gets seat availability rq converter.
	 *
	 * @return the seat availability rq converter
	 */
	protected Converter<SeatAvailabilityRQ, OfferRequestData> getSeatAvailabilityRQConverter()
	{
		return seatAvailabilityRQConverter;
	}

	/**
	 * Sets seat availability rq converter.
	 *
	 * @param seatAvailabilityRQConverter
	 * 		the seat availability rq converter
	 */
	@Required
	public void setSeatAvailabilityRQConverter(final Converter<SeatAvailabilityRQ, OfferRequestData> seatAvailabilityRQConverter)
	{
		this.seatAvailabilityRQConverter = seatAvailabilityRQConverter;
	}

	/**
	 * Gets seat availability rs converter.
	 *
	 * @return the seat availability rs converter
	 */
	protected Converter<OfferResponseData, SeatAvailabilityRS> getSeatAvailabilityRSConverter()
	{
		return seatAvailabilityRSConverter;
	}

	/**
	 * Sets seat availability rs converter.
	 *
	 * @param seatAvailabilityRSConverter
	 * 		the seat availability rs converter
	 */
	@Required
	public void setSeatAvailabilityRSConverter(final Converter<OfferResponseData, SeatAvailabilityRS> seatAvailabilityRSConverter)
	{
		this.seatAvailabilityRSConverter = seatAvailabilityRSConverter;
	}

	/**
	 * Gets ndc accommodation search pipeline manager.
	 *
	 * @return the ndc accommodation search pipeline manager
	 */
	protected AncillarySearchPipelineManager getNdcAccommodationSearchPipelineManager()
	{
		return ndcAccommodationSearchPipelineManager;
	}

	/**
	 * Sets ndc accommodation search pipeline manager.
	 *
	 * @param ndcAccommodationSearchPipelineManager
	 * 		the ndc accommodation search pipeline manager
	 */
	@Required
	public void setNdcAccommodationSearchPipelineManager(
			final AncillarySearchPipelineManager ndcAccommodationSearchPipelineManager)
	{
		this.ndcAccommodationSearchPipelineManager = ndcAccommodationSearchPipelineManager;
	}

}
