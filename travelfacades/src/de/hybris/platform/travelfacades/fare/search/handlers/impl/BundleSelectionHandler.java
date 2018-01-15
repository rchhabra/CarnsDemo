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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelservices.bundle.TravelBundleTemplateService;
import de.hybris.platform.travelservices.order.TravelCartService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible to set selected
 * ItineraryInfoData for each Pricing Itinerary based on the cart and sort the itinerary Infos based sequence Number of
 * each bundler type {@link PricedItineraryData} on the {@link FareSelectionData}
 */
public class BundleSelectionHandler implements FareSearchHandler
{
	private TravelCartService travelCartService;
	private TravelBundleTemplateFacade travelBundleTemplateFacade;
	private TravelBundleTemplateService travelBundleTemplateService;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		final CartModel cart = getTravelCartService().getSessionCart();
		fareSelectionData.getPricedItineraries().forEach(pricedItinerary -> {
			final String bundleSelectedInCart = getTravelBundleTemplateService().getBundleTemplateIdFromOrder(cart,
					pricedItinerary.getOriginDestinationRefNumber());
			pricedItinerary.getItineraryPricingInfos().forEach(itineraryPricingInfoData -> itineraryPricingInfoData
					.setSelected(StringUtils.equals(itineraryPricingInfoData.getBundleType(), bundleSelectedInCart)));
			sortItineraryPricingInfos(pricedItinerary.getItineraryPricingInfos());
		});
	}

	protected void sortItineraryPricingInfos(final List<ItineraryPricingInfoData> itineraryPricingInfos)
	{
		final Comparator<ItineraryPricingInfoData> bundleSequenceComparator = (b1,
				b2) -> getTravelBundleTemplateFacade().getSequenceNumber(b1.getBundleType())
						- getTravelBundleTemplateFacade().getSequenceNumber(b2.getBundleType());
		Collections.sort(itineraryPricingInfos, bundleSequenceComparator);
	}

	/**
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * @param travelCartService
	 *           the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
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

	/**
	 * @return the travelBundleTemplateService
	 */
	protected TravelBundleTemplateService getTravelBundleTemplateService()
	{
		return travelBundleTemplateService;
	}

	/**
	 * @param travelBundleTemplateService
	 *           the travelBundleTemplateService to set
	 */
	@Required
	public void setTravelBundleTemplateService(final TravelBundleTemplateService travelBundleTemplateService)
	{
		this.travelBundleTemplateService = travelBundleTemplateService;
	}

}
