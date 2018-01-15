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
*/
package de.hybris.platform.travelfacades.ancillary.search.manager.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchRequestHandler;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchRequestPipelineManager;

import java.util.List;
import java.util.Objects;


/**
 * Concrete Pipeline Manager class that will return a {@link OfferRequestData} after executing a list of handlers on the
 * {@link ReservationData} given as input
 */
public class DefaultAncillarySearchRequestPipelineManager implements AncillarySearchRequestPipelineManager
{

	private List<AncillarySearchRequestHandler> handlers;

	@Override
	public OfferRequestData executePipeline(final ReservationData reservationData)
	{
		if (Objects.isNull(reservationData))
		{
			return null;
		}

		final OfferRequestData offerRequestData = new OfferRequestData();
		for (final AncillarySearchRequestHandler handler : getHandlers())
		{
			handler.handle(reservationData, offerRequestData);
		}

		return offerRequestData;
	}

	protected List<AncillarySearchRequestHandler> getHandlers()
	{
		return handlers;
	}

	public void setHandlers(final List<AncillarySearchRequestHandler> handlers)
	{
		this.handlers = handlers;
	}
}
