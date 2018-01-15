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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates the total prices for the packageResponseData based on the prices of the TransportPackageResponse,
 * AccommodationPackageResponse and StandardPackageResponses.
 */
public class DealPackageResponsePriceHandler implements PackageResponseHandler
{
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Override
	public void handle(final PackageRequestData packageRequestData, final PackageResponseData packageResponseData)
	{
		if (!packageResponseData.isAvailable())
		{
			return;
		}
		calculateTotalPrice(packageResponseData);
	}

	protected void calculateTotalPrice(final PackageResponseData packageResponseData)
	{
		Double basePrice = 0d;
		Double totalPrice = 0d;
		Double wasRate = 0d;
		final List<TaxData> taxes = new ArrayList<>();
		String currencyIso = null;

		// TransportPackageResponse
		final List<ItineraryPricingInfoData> itineraryPricingInfos = packageResponseData.getTransportPackageResponse()
				.getFareSearchResponse().getPricedItineraries().stream()
				.flatMap(pricedItinerary -> pricedItinerary.getItineraryPricingInfos().stream()).collect(Collectors.toList());

		for (final ItineraryPricingInfoData itineraryPricingInfo : itineraryPricingInfos)
		{
			final TotalFareData totalFare = itineraryPricingInfo.getTotalFare();
			basePrice = Double.sum(basePrice, totalFare.getBasePrice().getValue().doubleValue());
			totalPrice = Double.sum(totalPrice, totalFare.getTotalPrice().getValue().doubleValue());
			wasRate = Double.sum(wasRate, totalFare.getWasRate().getValue().doubleValue());
			taxes.addAll(totalFare.getTaxes());

			if (StringUtils.isBlank(currencyIso))
			{
				currencyIso = totalFare.getBasePrice().getCurrencyIso();
			}
		}

		// AccommodationPackageResponse
		final List<RateData> accommodationRateDataList = packageResponseData.getAccommodationPackageResponse()
				.getAccommodationAvailabilityResponse().getRoomStays().stream().map(ReservedRoomStayData.class::cast)
				.map(ReservedRoomStayData::getBaseRate).collect(Collectors.toList());

		for (final RateData accommodationRateData : accommodationRateDataList)
		{
			basePrice = Double.sum(basePrice, accommodationRateData.getBasePrice().getValue().doubleValue());
			totalPrice = Double.sum(totalPrice, accommodationRateData.getActualRate().getValue().doubleValue());
			wasRate = Double.sum(wasRate, accommodationRateData.getWasRate().getValue().doubleValue());
			taxes.addAll(accommodationRateData.getTaxes());

			if (StringUtils.isBlank(currencyIso))
			{
				currencyIso = accommodationRateData.getBasePrice().getCurrencyIso();
			}
		}

		// StandardPackageResponses
		final List<PackageProductData> packageProductDatas = packageResponseData.getStandardPackageResponses().stream()
				.flatMap(response -> response.getPackageProducts().stream()).collect(Collectors.toList());

		for (final PackageProductData packageProductData : packageProductDatas)
		{
			final RateData rateData = packageProductData.getPrice();
			final Integer quantity = packageProductData.getQuantity();
			basePrice = Double.sum(basePrice, rateData.getBasePrice().getValue().doubleValue() * quantity);
			totalPrice = Double.sum(totalPrice, rateData.getActualRate().getValue().doubleValue() * quantity);
			wasRate = Double.sum(wasRate, rateData.getWasRate().getValue().doubleValue() * quantity);
			taxes.addAll(rateData.getTaxes());
		}

		final BigDecimal bookingFees = getTravelCommercePriceFacade().getBookingFeesAndTaxes();
		totalPrice = Double.sum(totalPrice, bookingFees.doubleValue());
		wasRate = Double.sum(wasRate, bookingFees.doubleValue());

		final RateData rateData = new RateData();
		rateData.setBasePrice(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(basePrice), currencyIso));
		rateData.setActualRate(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(totalPrice), currencyIso));
		rateData.setWasRate(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(wasRate), currencyIso));
		rateData.setTotalDiscount(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(wasRate).subtract(BigDecimal.valueOf(totalPrice)), currencyIso));

		rateData.setTaxes(taxes);
		final TaxData totalTaxData = new TaxData();
		final Double totalTaxValue = taxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
		totalTaxData.setPrice(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(totalTaxValue), currencyIso));

		rateData.setTotalTax(totalTaxData);

		packageResponseData.setPrice(rateData);
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
