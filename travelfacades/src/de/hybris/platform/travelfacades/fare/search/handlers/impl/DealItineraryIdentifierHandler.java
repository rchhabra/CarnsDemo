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
package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for
 * populating the Itinerary identifier {@link ItineraryPricingInfoData}
 */
public class DealItineraryIdentifierHandler extends ItineraryIdentifierHandler implements FareSearchHandler
{
	private TravelBundleTemplateFacade travelBundleTemplateFacade;

	/**
	 * This method populate the itinerary identifier for each {@link ItineraryPricingInfoData} provided
	 *
	 * @param itineraryPricingInfos the List of ItineraryPricingInfoData
	 */
	protected void populateItineraryPricingInfoIdentifier(final List<ItineraryPricingInfoData> itineraryPricingInfos)
	{
		for (final ItineraryPricingInfoData itineraryPricingInfoData : itineraryPricingInfos)
		{
			final String masterBundleTemplateId = getTravelBundleTemplateFacade().getMasterBundleTemplateId(
					itineraryPricingInfoData.getBundleTemplates().get(0).getFareProductBundleTemplateId());
			final String itineraryIdentifier = getFareSearchHashResolver().generateIdentifier(itineraryPricingInfoData, masterBundleTemplateId);
			itineraryPricingInfoData.setItineraryIdentifier(itineraryIdentifier);
		}
	}

	/**
	 * Gets travel bundle template facade.
	 *
	 * @return the travel bundle template facade
	 */
	protected TravelBundleTemplateFacade getTravelBundleTemplateFacade()
	{
		return travelBundleTemplateFacade;
	}

	/**
	 * Sets travel bundle template facade.
	 *
	 * @param travelBundleTemplateFacade
	 * 		the travel bundle template facade
	 */
	@Required
	public void setTravelBundleTemplateFacade(final TravelBundleTemplateFacade travelBundleTemplateFacade)
	{
		this.travelBundleTemplateFacade = travelBundleTemplateFacade;
	}
}
