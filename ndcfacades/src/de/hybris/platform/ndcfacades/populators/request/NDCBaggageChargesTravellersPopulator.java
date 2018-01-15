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

import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRQ;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;


/**
 * The NDC Travellers Populator, create a list of {@link TravellerData} based on the AnonymousTraveler information
 */
public class NDCBaggageChargesTravellersPopulator extends NDCRQTravellersPopulator
		implements Populator<BaggageChargesRQ, OfferRequestData>
{

	@Override
	public void populate(final BaggageChargesRQ source, final OfferRequestData target)
	{
		final List<TravellerData> travellers = getTravellersFromNDCRQ(source.getTravelers());
		if (CollectionUtils.isNotEmpty(target.getItineraries()))
		{
			target.getItineraries().forEach(itineraryData -> itineraryData.setTravellers(travellers));
		}
	}
}
