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

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible to filter the
 * ItineraryPricingInfoData whose sequence number based on bundle type is less that sequence number of selected
 * ItineraryPricingInfoData the list of {@link PricedItineraryData} on the {@link FareSelectionData}
 */
public class FilterUpgradeBundleHandler implements FareSearchHandler
{
	private TravelBundleTemplateFacade travelBundleTemplateFacade;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		fareSelectionData.getPricedItineraries().forEach(pricedItinerary -> {
			final ItineraryPricingInfoData selectedItineraryPricingInfoData = getTravelBundleTemplateFacade()
					.getSelectedItineraryPricingInfoData(pricedItinerary);

			final int selectedBundleSequenceNum = getTravelBundleTemplateFacade()
					.getSequenceNumber(selectedItineraryPricingInfoData.getBundleType());
			pricedItinerary.getItineraryPricingInfos()
					.removeIf(itineraryPricingInfo -> !selectedItineraryPricingInfoData.isAvailable()
							|| !itineraryPricingInfo.isAvailable() || getTravelBundleTemplateFacade()
							.getSequenceNumber(itineraryPricingInfo.getBundleType()) < selectedBundleSequenceNum);
		});
	}

	/**
	 * @return the travelBundleTemplateFacade
	 */
	protected TravelBundleTemplateFacade getTravelBundleTemplateFacade()
	{
		return travelBundleTemplateFacade;
	}

	/**
	 * @param travelBundleTemplateFacade
	 *           the travelBundleTemplateFacade to set
	 */
	@Required
	public void setTravelBundleTemplateFacade(final TravelBundleTemplateFacade travelBundleTemplateFacade)
	{
		this.travelBundleTemplateFacade = travelBundleTemplateFacade;
	}

}
