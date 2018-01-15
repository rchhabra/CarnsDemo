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
package de.hybris.platform.travelfacades.ancillary.search.manager.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchPipelineManager;

import java.util.List;
import java.util.Objects;


/**
 * Concrete Pipeline Manager class that will return a {@link OfferResponseData} after executing a list of handlers on
 * the {@link OfferRequestData} given as input
 */
public class DefaultAncillarySearchPipelineManager implements AncillarySearchPipelineManager
{

	private List<AncillarySearchHandler> handlers;

	@Override
	public OfferResponseData executePipeline(final OfferRequestData offerRequestData)
	{
		if (Objects.isNull(offerRequestData))
		{
			return null;
		}

		final OfferResponseData offerResponseData = new OfferResponseData();

		for (final AncillarySearchHandler handler : getHandlers())
		{
			handler.handle(offerRequestData, offerResponseData);
		}

		return offerResponseData;
	}

	protected List<AncillarySearchHandler> getHandlers()
	{
		return handlers;
	}

	public void setHandlers(final List<AncillarySearchHandler> handlers)
	{
		this.handlers = handlers;
	}
}
