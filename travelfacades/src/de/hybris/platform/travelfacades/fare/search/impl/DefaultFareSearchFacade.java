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
package de.hybris.platform.travelfacades.fare.search.impl;

import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;
import de.hybris.platform.travelfacades.fare.search.manager.FareSearchPipelineManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of FareSearchFacade
 */
public class DefaultFareSearchFacade implements FareSearchFacade
{

	private FareSearchPipelineManager fareSearchPipelineManager;
	private TransportOfferingFacade transportOfferingFacade;

	@Override
	public FareSelectionData doSearch(final FareSearchRequestData fareSearchRequestData)
	{
		final List<ScheduledRouteData> scheduledRoutes = getTransportOfferingFacade().getScheduledRoutes(fareSearchRequestData);

		return getFareSearchPipelineManager().executePipeline(scheduledRoutes, fareSearchRequestData);
	}

	@Override
	public Map<Integer, Map<String, Long>> getRemainingSeats(final FareSelectionData fareSelectionData)
	{
		final Map<Integer, Map<String, Long>> remainingSeatsMap = new HashMap<>();
		for (final PricedItineraryData pricedItineraryData : fareSelectionData.getPricedItineraries())
		{
			final Map<String, Long> bundleTypeStockMap = new HashMap<>();
			for (final ItineraryPricingInfoData itineraryPricingInfoData : pricedItineraryData.getItineraryPricingInfos())
			{
				if (!itineraryPricingInfoData.isAvailable())
				{
					continue;
				}
				final Long stock = getStockForFare(itineraryPricingInfoData);

				bundleTypeStockMap.put(itineraryPricingInfoData.getBundleType(), stock);
			}

			remainingSeatsMap.put(pricedItineraryData.getId(), bundleTypeStockMap);
		}

		return remainingSeatsMap;
	}

	protected Long getStockForFare(final ItineraryPricingInfoData itineraryPricingInfoData)
	{
		Long stock = Long.valueOf(-1);
		for (final PTCFareBreakdownData ptcFareBreakdownData : itineraryPricingInfoData.getPtcFareBreakdownDatas())
		{
			for (final FareInfoData fareInfoData : ptcFareBreakdownData.getFareInfos())
			{
				for (final FareDetailsData fareDetailsData : fareInfoData.getFareDetails())
				{
					stock = changeStockIfLower(stock, fareDetailsData);
				}
			}
		}
		return stock;
	}

	protected Long changeStockIfLower(final Long stock, final FareDetailsData fareDetailsData)
	{
		Long evaluatedStock = stock;
		final long currentStock = fareDetailsData.getFareProduct().getStock().getStockLevel();

		if (currentStock < stock || stock.longValue() == -1)
		{
			evaluatedStock = currentStock;
		}
		return evaluatedStock;
	}

	/**
	 * @return the fareSearchPipelineManager
	 */
	protected FareSearchPipelineManager getFareSearchPipelineManager()
	{
		return fareSearchPipelineManager;
	}

	/**
	 * @param fareSearchPipelineManager
	 *           the fareSearchPipelineManager to set
	 */
	@Required
	public void setFareSearchPipelineManager(final FareSearchPipelineManager fareSearchPipelineManager)
	{
		this.fareSearchPipelineManager = fareSearchPipelineManager;
	}

	/**
	 * @return the transportOfferingFacade
	 */
	protected TransportOfferingFacade getTransportOfferingFacade()
	{
		return transportOfferingFacade;
	}

	/**
	 * @param transportOfferingFacade
	 *           the transportOfferingFacade to set
	 */
	@Required
	public void setTransportOfferingFacade(final TransportOfferingFacade transportOfferingFacade)
	{
		this.transportOfferingFacade = transportOfferingFacade;
	}
}
