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
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for
 * populating the Fare Prices for scheduled route and populates the Passenger breakdown prices
 * on {@link FareSelectionData}
 */
public class DealFareInfoHandler extends AbstractFareInfoHandler
{
	private TransportOfferingFacade transportOfferingFacade;

	@Override
	protected void populateProductPricesAndSort(final PricedItineraryData pricedItinerary,
			final ItineraryPricingInfoData itineraryPricingInfoData)
	{
		if (Objects.isNull(pricedItinerary) || Objects.isNull(itineraryPricingInfoData)
				|| CollectionUtils.isEmpty(itineraryPricingInfoData.getBundleTemplates()))
		{
			return;
		}
		for (final TravelBundleTemplateData bundleTemplateData : itineraryPricingInfoData.getBundleTemplates())
		{
			final String routeCode = pricedItinerary.getItinerary().getRoute().getCode();

			final List<TransportOfferingData> transportOfferings = bundleTemplateData.getTransportOfferings();

			final TransportOfferingData transportOfferingData = transportOfferings.get(0);
			final String transportOfferingCode = transportOfferingData.getCode();
			final String sectorCode = transportOfferingData.getSector().getCode();
			final BundleTemplateModel bundleTemplateModel = getBundleTemplateService()
					.getBundleTemplateForCode(bundleTemplateData.getFareProductBundleTemplateId());

			populateProductPricesForOptions(bundleTemplateData.getFareProducts(), transportOfferingCode, sectorCode, routeCode,
					bundleTemplateModel);

			if (Objects.nonNull(bundleTemplateData.getNonFareProducts()))
			{
				bundleTemplateData.getNonFareProducts().forEach((childBundleId, childBundleProducts) -> {
					final BundleTemplateModel childBundleTemplate = getBundleTemplateService().getBundleTemplateForCode(childBundleId);
					populateProductPricesForOptions(childBundleProducts, transportOfferingCode, sectorCode, routeCode,
							childBundleTemplate);
				});
			}

			// populate products for includedAncillaries
			if (Objects.nonNull(bundleTemplateData.getIncludedAncillaries()))
			{
				bundleTemplateData.getIncludedAncillaries().forEach(includedAncillaries -> {
					final BundleTemplateModel childBundleTemplate = getBundleTemplateService()
							.getBundleTemplateForCode(bundleTemplateData.getId());
					populateProductPricesForOptions(includedAncillaries.getProducts(), null, null, routeCode, childBundleTemplate);
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
			if (bundleTemplate != null)
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

	@Override
	protected void populatePTCFareBreakDownData(final ItineraryPricingInfoData itineraryPricingInfoData,
			final FareSearchRequestData fareSearchRequestData)
	{
		if (Objects.isNull(itineraryPricingInfoData))
		{
			return;
		}
		super.populatePTCFareBreakDownData(itineraryPricingInfoData, fareSearchRequestData);

		for(final PTCFareBreakdownData ptcFareBreakdownData : itineraryPricingInfoData.getPtcFareBreakdownDatas())
		{
			double wasRate = 0d;
			for (final TravelBundleTemplateData bundleTemplateData : itineraryPricingInfoData.getBundleTemplates())
			{
				final FareProductData selectedFareProductData = getFareProductFromBundleTemplate(bundleTemplateData);
				final double totalBaseWasRate = getTotalWasRateFromFareAndAncillaryProducts(bundleTemplateData,
						selectedFareProductData);
				wasRate += totalBaseWasRate;
			}

			final int noOfTravellers = ptcFareBreakdownData.getPassengerTypeQuantity().getQuantity();
			final double totalWasFare = noOfTravellers * wasRate;
			ptcFareBreakdownData.getPassengerFare().setWasRate(createPriceData(totalWasFare));
		}
	}

	/**
	 * Returns the value of the wasRate calculated from Fare products and Ancillary products
	 *
	 * @param bundleTemplateData
	 * 		as the bundleTemplateData
	 * @param fareProductData
	 * 		as the selectedFareProductData
	 *
	 * @return the double value of the total wasRate
	 */
	protected double getTotalWasRateFromFareAndAncillaryProducts(final TravelBundleTemplateData bundleTemplateData,
			final FareProductData fareProductData)
	{
		BigDecimal ancillaryProductsTotal = BigDecimal.ZERO;

		for (final Map.Entry<String, List<ProductData>> nonFareProductEntry : bundleTemplateData.getNonFareProducts().entrySet())
		{
			for (final ProductData productData : nonFareProductEntry.getValue())
			{
				final PriceInformation priceInfo = getTravelCommercePriceFacade().getPriceInformation(productData.getCode());
				ancillaryProductsTotal = ancillaryProductsTotal.add(BigDecimal.valueOf(priceInfo.getPriceValue().getValue()));
			}
		}

		final PriceInformation fareProductPriceInfo = getTravelCommercePriceFacade().getPriceInformation(fareProductData.getCode());

		return fareProductPriceInfo.getPriceValue().getValue() + ancillaryProductsTotal.doubleValue();
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
	public void setTransportOfferingFacade(final TransportOfferingFacade transportOfferingFacade)
	{
		this.transportOfferingFacade = transportOfferingFacade;
	}

}
