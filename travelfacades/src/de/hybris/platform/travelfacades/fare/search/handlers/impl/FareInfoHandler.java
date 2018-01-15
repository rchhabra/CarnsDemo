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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelservices.enums.ProductType;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for
 * populating the Fare Prices for scheduled route and populates the Passenger breakdown prices
 * on {@link FareSelectionData}
 */
public class FareInfoHandler extends AbstractFareInfoHandler
{

	private TransportOfferingFacade transportOfferingFacade;


	@Override
	protected void populateProductPricesAndSort(final PricedItineraryData pricedItinerary,
			final ItineraryPricingInfoData itineraryPricingInfoData)
	{
		for (final TravelBundleTemplateData bundleTemplateData : itineraryPricingInfoData.getBundleTemplates())
		{
			final String routeCode = pricedItinerary.getItinerary().getRoute().getCode();

			final List<TransportOfferingData> transportOfferings = bundleTemplateData.getTransportOfferings();

			if (getTransportOfferingFacade()
					.isMultiSectorRoute(transportOfferings.stream().map(TransportOfferingData::getCode).collect(Collectors.toList())))
			{
				// Populates prices for products for a multi sector trip.
				populateProductPrices(bundleTemplateData.getFareProducts(), PriceRowModel.TRAVELROUTECODE, routeCode, null);

				bundleTemplateData.getNonFareProducts().forEach((childBundleId, childBundleProducts) ->
				{
					final BundleTemplateModel childBundleTemplate = getBundleTemplateService().getBundleTemplateForCode
							(childBundleId);
					populateProductPrices(childBundleProducts, PriceRowModel.TRAVELROUTECODE, routeCode, childBundleTemplate);
				});

				// populate products for includedAncillaries
				bundleTemplateData.getIncludedAncillaries().forEach(includedAncillaries ->
				{
					final BundleTemplateModel childBundleTemplate = getBundleTemplateService()
							.getBundleTemplateForCode(bundleTemplateData.getId());
					populateProductPrices(includedAncillaries.getProducts(), PriceRowModel.TRAVELROUTECODE, routeCode,
							childBundleTemplate);
				});
			}
			else
			{
				// Populates prices for products for a point to point trip.
				final TransportOfferingData transportOfferingData = transportOfferings.get(0);
				final String transportOfferingCode = transportOfferingData.getCode();
				final String sectorCode = transportOfferingData.getSector().getCode();

				populateProductPricesForOptions(bundleTemplateData.getFareProducts(), transportOfferingCode, sectorCode, routeCode,
						null);

				bundleTemplateData.getNonFareProducts().forEach((childBundleId, childBundleProducts) ->
				{
					final BundleTemplateModel childBundleTemplate = getBundleTemplateService().getBundleTemplateForCode
							(childBundleId);

					populateProductPricesForOptions(childBundleProducts, transportOfferingCode, sectorCode, routeCode,
							childBundleTemplate);
				});

				// populate products for includedAncillaries
				bundleTemplateData.getIncludedAncillaries().forEach(includedAncillaries ->
				{
					final BundleTemplateModel childBundleTemplate = getBundleTemplateService()
							.getBundleTemplateForCode(bundleTemplateData.getId());
					populateProductPricesForOptions(includedAncillaries.getProducts(), null, null, routeCode,
							childBundleTemplate);
				});

			}
			getProductsSortStrategy().applyStrategy(bundleTemplateData.getFareProducts());
		}
	}

	@Override
	protected void populateProductPricesForOptions(final List<? extends ProductData> productList,
			final String transportOfferingCode, final String sectorCode, final String routeCode,
			final BundleTemplateModel bundleTemplate)
	{
		if (productList == null)
		{
			return;
		}

		for (final ProductData productData : productList)
		{
			PriceInformation priceInfo = null;
			if (bundleTemplate != null
					&& !StringUtils.equalsIgnoreCase(ProductType.FARE_PRODUCT.getCode(), productData.getProductType()))
			{
				priceInfo = getTravelCommercePriceFacade().getPriceInformationByProductPriceBundleRule(bundleTemplate,
						productData.getCode());
			}
			if (priceInfo == null)
			{
				priceInfo = getTravelCommercePriceFacade().getPriceInformationByHierarchy(productData.getCode(),
						transportOfferingCode, sectorCode, routeCode);
				if (priceInfo == null)
				{
					logNoPriceForTransportOffering(transportOfferingCode, productData);
					continue;
				}
			}
			productData.setPrice(createPriceData(priceInfo));
		}
	}

	/**
	 * Gets transport offering facade.
	 *
	 * @return the transportOfferingFacade
	 */
	protected TransportOfferingFacade getTransportOfferingFacade()
	{
		return transportOfferingFacade;
	}

	/**
	 * Sets transport offering facade.
	 *
	 * @param transportOfferingFacade
	 * 		the transportOfferingFacade to set
	 */
	@Required
	public void setTransportOfferingFacade(final TransportOfferingFacade transportOfferingFacade)
	{
		this.transportOfferingFacade = transportOfferingFacade;
	}

}
