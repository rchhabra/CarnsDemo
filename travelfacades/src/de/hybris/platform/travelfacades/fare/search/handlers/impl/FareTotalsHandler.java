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

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.util.PricingUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for populating the totals
 * on {@link FareSelectionData}
 */
public class FareTotalsHandler implements FareSearchHandler
{
	private ConfigurationService configurationService;
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private CommonI18NService commonI18NService;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			if (!pricedItinerary.isAvailable())
			{
				continue;
			}
			for (final ItineraryPricingInfoData itineraryPricingInfoData : pricedItinerary.getItineraryPricingInfos())
			{
				if (itineraryPricingInfoData.isAvailable())
				{
					populateTotals(itineraryPricingInfoData);
					populatePerPax(itineraryPricingInfoData);
				}
			}
		}
	}


	/**
	 * Method takes the Itinerary Pricing Info and calculates and sets the BaseFare and TotalFare.
	 *
	 * @param itineraryPricingInfo
	 */
	protected void populateTotals(final ItineraryPricingInfoData itineraryPricingInfo)
	{

		BigDecimal basePrice = BigDecimal.valueOf(0);
		BigDecimal totalPrice = BigDecimal.valueOf(0);

		final TotalFareData totalFare = new TotalFareData();

		final List<TaxData> totalTaxes = new ArrayList<>();
		for (final PTCFareBreakdownData ptcBreakdown : itineraryPricingInfo.getPtcFareBreakdownDatas())
		{
			basePrice = basePrice.add(ptcBreakdown.getPassengerFare().getTotalFare().getValue());

			totalPrice = totalPrice.add(ptcBreakdown.getPassengerFare().getTotalFare().getValue());

			final List<TaxData> taxes = ptcBreakdown.getPassengerFare().getTaxes();

			if (CollectionUtils.isNotEmpty(taxes) && ptcBreakdown.getPassengerTypeQuantity().getQuantity() > 0)
			{
				totalPrice = totalPrice.add(PricingUtils.getTotalTaxValue(taxes));
				totalTaxes.addAll(taxes);
			}
		}

		final BigDecimal extrasPrice = getIncludedAncillariesTotalPrice(itineraryPricingInfo);
		totalPrice = totalPrice.add(extrasPrice);

		totalFare.setBasePrice(getTravelCommercePriceFacade().createPriceData(basePrice.doubleValue()));
		totalFare.setTaxes(totalTaxes);
		totalFare.setTotalPrice(getTravelCommercePriceFacade().createPriceData(totalPrice.doubleValue()));
		totalFare.setTotalBaseExtrasPrice(getTravelCommercePriceFacade().createPriceData(basePrice.add(extrasPrice).doubleValue()));
		itineraryPricingInfo.setTotalFare(totalFare);
	}

	protected BigDecimal getIncludedAncillariesTotalPrice(final ItineraryPricingInfoData itineraryPricingInfo)
	{
		BigDecimal ancillaryProductsTotal = BigDecimal.valueOf(0);
		for (final TravelBundleTemplateData bundleTemplate : itineraryPricingInfo.getBundleTemplates())
		{
			final List<ProductData> includedProducts = bundleTemplate.getIncludedAncillaries().stream()
					.filter(includedAncillary -> includedAncillary.getCriteria()
							.equalsIgnoreCase(TravelfacadesConstants.PER_LEG_ADD_TO_CART_CRITERIA))
					.flatMap(includedAncillary -> includedAncillary.getProducts().stream()).collect(Collectors.toList());

			for (final ProductData productData : includedProducts)
			{
				ancillaryProductsTotal = ancillaryProductsTotal.add(productData.getPrice().getValue());
			}
		}
		return ancillaryProductsTotal;
	}

	/**
	 * Method takes the Itinerary Pricing Info. Calculates and sets Per Passenger Fare Value including base fare,
	 * taxes/fees, discounts.
	 * <p>
	 * Price per passenger = Base price + Total Taxes + Total Fees - Discounts.
	 *
	 * @param itineraryPricingInfo
	 */
	protected void populatePerPax(final ItineraryPricingInfoData itineraryPricingInfo)
	{
		final String priceDisplayPassengerType = getConfigurationService().getConfiguration()
				.getString(TravelfacadesConstants.PRICE_DISPLAY_PASSENGER_TYPE);

		if (StringUtils.isEmpty(priceDisplayPassengerType))
		{
			return;
		}

		itineraryPricingInfo.getPtcFareBreakdownDatas().stream()
				.filter(ptcBreakdownData -> StringUtils.equalsIgnoreCase(
						ptcBreakdownData.getPassengerTypeQuantity().getPassengerType().getCode(), priceDisplayPassengerType))
				.forEach(ptcBreakdown -> {
					final PassengerFareData passengerFareData = ptcBreakdown.getPassengerFare();
					// passengerQtyDivisor will always have value >0 because perPax value is being calculated per Passenger.
					final BigDecimal passengerQtyDivisor = BigDecimal
							.valueOf(ptcBreakdown.getPassengerTypeQuantity().getQuantity() == 0 ? 1
									: ptcBreakdown.getPassengerTypeQuantity().getQuantity());
					final BigDecimal basePrice = passengerFareData.getBaseFare() != null ? passengerFareData.getBaseFare().getValue()
							: BigDecimal.valueOf(0);
					final BigDecimal totalTaxes = passengerFareData.getTaxes() != null
							? PricingUtils.getTotalTaxValue(passengerFareData.getTaxes()).divide(passengerQtyDivisor)
							: BigDecimal.valueOf(0);
					final BigDecimal totalFees = passengerFareData.getFees() != null
							? PricingUtils.getTotalFeesValue(passengerFareData.getFees()).divide(passengerQtyDivisor)
							: BigDecimal.valueOf(0);
					final BigDecimal discount = passengerFareData.getDiscounts() != null
							? PricingUtils.getTotalDiscountValue(passengerFareData.getDiscounts()) : BigDecimal.valueOf(0);
					final BigDecimal perPax = calculatePerPax(basePrice, totalTaxes, totalFees, discount);
					passengerFareData.setPerPax(getTravelCommercePriceFacade().createPriceData(perPax.doubleValue()));
				});
	}

	/**
	 * @param basePrice
	 *
	 * @param totalTaxes
	 *
	 * @param totalFees
	 *
	 * @param discount
	 *
	 * @return the priceDataFactory
	 */

	private BigDecimal calculatePerPax(final BigDecimal basePrice, final BigDecimal totalTaxes, final BigDecimal totalFees,
			final BigDecimal discount)
	{
		BigDecimal perPax = BigDecimal.valueOf(0);
		perPax = perPax.add(basePrice);
		perPax = perPax.add(totalTaxes);
		perPax = perPax.add(totalFees);
		perPax = perPax.subtract(discount);
		return perPax;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return the commonI18NService
	 */
	@Deprecated
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Deprecated
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}


	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

}
