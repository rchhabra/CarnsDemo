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

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler class to populate the total price for each roomStay in {@link AccommodationAvailabilityResponseData}
 */
public class AccommodationTotalPriceHandler implements AccommodationDetailsHandler
{
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		accommodationAvailabilityResponseData.getRoomStays().forEach(roomStay -> {
			if (roomStay instanceof ReservedRoomStayData && roomStay.getFromPrice() != null)
			{
				populateTotalRoomStayPrice((ReservedRoomStayData) roomStay);
				populateBaseRoomStayPrice((ReservedRoomStayData) roomStay);
			}
		});
	}

	protected void populateTotalRoomStayPrice(final ReservedRoomStayData reservedRoomStayData)
	{
		final BigDecimal totalPriceForServices = getTotalPriceForExtraServices(reservedRoomStayData.getServices());
		final BigDecimal totalPriceForRatePlans = reservedRoomStayData.getFromPrice().getValue();
		final BigDecimal totalPriceForRoomStay = totalPriceForServices.add(totalPriceForRatePlans);

		reservedRoomStayData
				.setTotalRate(createRateData(totalPriceForRoomStay, reservedRoomStayData.getFromPrice().getCurrencyIso()));
	}

	protected void populateBaseRoomStayPrice(final ReservedRoomStayData reservedRoomStayData)
	{
		if (CollectionUtils.isEmpty(reservedRoomStayData.getRatePlans()))
		{
			return;
		}

		BigDecimal totalActualPrice = BigDecimal.valueOf(0);
		BigDecimal totalBasePrice = BigDecimal.valueOf(0);
		BigDecimal totalWasPrice = BigDecimal.valueOf(0);
		BigDecimal totalDisounts = BigDecimal.valueOf(0);
		final List<TaxData> totalTaxes = new ArrayList<>();
		for (final RatePlanData ratePlanData : reservedRoomStayData.getRatePlans())
		{
			for (final RoomRateData roomRateData : ratePlanData.getRoomRates())
			{
				final RateData roomRate = roomRateData.getRate();

				totalBasePrice = totalBasePrice.add(roomRate.getBasePrice().getValue());
				totalActualPrice = totalActualPrice.add(roomRate.getActualRate().getValue());
				totalWasPrice = totalWasPrice.add(roomRate.getWasRate().getValue());
				totalDisounts = totalDisounts.add(roomRate.getTotalDiscount().getValue());

				if (CollectionUtils.isNotEmpty(roomRate.getTaxes()))
				{
					totalTaxes.addAll(roomRate.getTaxes());
				}
			}
		}
		final String currencyIso = reservedRoomStayData.getFromPrice().getCurrencyIso();
		final RateData rateData = new RateData();
		rateData.setBasePrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalBasePrice, currencyIso));
		rateData.setActualRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalActualPrice, currencyIso));
		rateData.setWasRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalWasPrice, currencyIso));
		rateData.setTotalDiscount(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalDisounts, currencyIso));
		rateData.setTaxes(totalTaxes);
		rateData.setTotalTax(
				createTaxData(totalTaxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum(), currencyIso));
		reservedRoomStayData.setBaseRate(rateData);
	}


	protected BigDecimal getTotalPriceForExtraServices(final List<ServiceData> serviceDatas)
	{
		BigDecimal totalPriceData = BigDecimal.valueOf(0);
		if (CollectionUtils.isEmpty(serviceDatas))
		{
			return totalPriceData;
		}
		for (final ServiceData serviceData : serviceDatas)
		{
			totalPriceData = totalPriceData.add(serviceData.getPrice().getTotal().getValue());
		}
		return totalPriceData;
	}



	protected RateData createRateData(final BigDecimal priceValue, final String currencyIso)
	{
		final RateData rateData = new RateData();
		rateData.setActualRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, priceValue, currencyIso));
		return rateData;
	}

	private TaxData createTaxData(final Double value, final String currencyIso)
	{
		final TaxData taxData = new TaxData();
		taxData.setPrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(value), currencyIso));
		return taxData;
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
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
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
