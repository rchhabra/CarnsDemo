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
 */

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler which filters fare products based on the outcome of drools rule evaluation
 */
public class FareRulesHandler implements FareSearchHandler
{
	private TravelRulesService travelRulesService;
	private ConfigurationService configurationService;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		final Boolean isDroolsFilteringEnabled = getConfigurationService().getConfiguration()
				.getBoolean(TravelservicesConstants.CONFIG_DROOLS_FAREFILTER_ENABLED);
		if (CollectionUtils.isEmpty(fareSelectionData.getPricedItineraries()) || BooleanUtils.isNotTrue(isDroolsFilteringEnabled))
		{
			return;
		}

		// Create a list of all fare products from all priced itineraries
		final List<FareProductData> fareProducts = collectFareProducts(fareSelectionData);

		if (CollectionUtils.isEmpty(fareProducts))
		{
			return;
		}

		// Do drools filtering
		final List<FareProductData> excludedFareProducts = getTravelRulesService()
				.filterFareProducts(fareProducts, fareSearchRequestData);

		// Loop through all priced itineraries and remove excluded fare products from bundles
		removeExcludedProductsFromBundles(fareSelectionData, excludedFareProducts);

		updateAvailability(fareSelectionData);
	}

	/**
	 * Updates availability of bundles, itinerary pricing infos and priced itineraries after rules evaluation activity
	 *
	 * @param fareSelectionData
	 * 		the fare selection data
	 */
	protected void updateAvailability(final FareSelectionData fareSelectionData)
	{
		fareSelectionData.getPricedItineraries().stream().filter(PricedItineraryData::isAvailable).forEach(pricedItinerary -> {
			pricedItinerary.getItineraryPricingInfos().stream().filter(ItineraryPricingInfoData::isAvailable)
					.forEach(itineraryPricingInfo -> {
						itineraryPricingInfo.getBundleTemplates().forEach(bundleTemplate -> bundleTemplate
								.setAvailable(CollectionUtils.isNotEmpty(bundleTemplate.getFareProducts())));
						itineraryPricingInfo.setAvailable(!itineraryPricingInfo.getBundleTemplates().stream()
								.filter(bundleTemplate -> !bundleTemplate.isAvailable()).findAny().isPresent());
					});
			pricedItinerary.setAvailable(
					pricedItinerary.getItineraryPricingInfos().stream().filter(ItineraryPricingInfoData::isAvailable).findAny()
							.isPresent());
		});
	}

	/**
	 * Removes excluded fare products from all bundles that contain them
	 *
	 * @param fareSelectionData
	 * 		the fare selection data
	 * @param excludedFareProducts
	 * 		the excluded fare products
	 */
	protected void removeExcludedProductsFromBundles(final FareSelectionData fareSelectionData,
			final List<FareProductData> excludedFareProducts)
	{
		excludedFareProducts.forEach(excludedFareProduct -> fareSelectionData.getPricedItineraries().forEach(pricedItinerary ->
				pricedItinerary.getItineraryPricingInfos()
						.forEach(itineraryPricingInfo -> itineraryPricingInfo.getBundleTemplates().forEach(bundleTemplate -> {
							for (FareProductData fareProduct : bundleTemplate.getFareProducts())
							{
								if (StringUtils.equalsIgnoreCase(fareProduct.getCode(), excludedFareProduct.getCode()))
								{
									bundleTemplate.getFareProducts().remove(fareProduct);
									break;
								}
							}
						}))));
	}

	/**
	 * Collects unique fare products from all priced itineraries
	 *
	 * @param fareSelectionData
	 * 		the fare selection data
	 * @return list
	 */
	protected List<FareProductData> collectFareProducts(final FareSelectionData fareSelectionData)
	{
		final List<FareProductData> fareProducts = new ArrayList<>();
		fareSelectionData.getPricedItineraries().stream().filter(PricedItineraryData::isAvailable)
				.forEach(pricedItinerary -> pricedItinerary.getItineraryPricingInfos().stream().filter(
						ItineraryPricingInfoData::isAvailable).forEach(
						itineraryPricingInfo -> itineraryPricingInfo.getBundleTemplates().stream()
								.filter(bundleTemplate -> CollectionUtils.isNotEmpty(bundleTemplate.getFareProducts()))
								.forEach(bundleTemplate -> bundleTemplate.getFareProducts().forEach(fareProduct -> {
									if (isFareProductUnique(fareProducts, fareProduct))
									{
										fareProducts.add(fareProduct);
									}
								}))));
		return fareProducts;
	}

	/**
	 * Verifies that there is no fare product with the same code already in the list
	 *
	 * @param fareProducts
	 * 		the fare products
	 * @param fareProduct
	 * 		the fare product
	 * @return boolean
	 */
	protected Boolean isFareProductUnique(final List<FareProductData> fareProducts, final FareProductData fareProduct)
	{
		for (FareProductData fp : fareProducts)
		{
			if (StringUtils.equalsIgnoreCase(fp.getCode(), fareProduct.getCode()))
			{
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Gets travel rules service.
	 *
	 * @return the travel rules service
	 */
	protected TravelRulesService getTravelRulesService()
	{
		return travelRulesService;
	}

	/**
	 * Sets travel rules service.
	 *
	 * @param travelRulesService
	 * 		the travel rules service
	 */
	@Required
	public void setTravelRulesService(final TravelRulesService travelRulesService)
	{
		this.travelRulesService = travelRulesService;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
