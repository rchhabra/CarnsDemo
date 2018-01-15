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

package de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.DayRateData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.RateRangeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.PropertyHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link PropertyHandler} interface. Handler is responsible to set price attributes on
 * the {@link PropertyData}
 */
public class PropertyDataPriceHandler extends AbstractDefaultPropertyHandler implements PropertyHandler
{
	private CommonI18NService commonI18NService;
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	@Override
	public void handle(final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final AccommodationSearchRequestData accommodationSearchRequest, final PropertyData propertyData)
	{
		if (BooleanUtils.isTrue(validateDayRatesAgainstRequest(dayRatesForRoomStayCandidate, accommodationSearchRequest)
				&& CollectionUtils.isNotEmpty(propertyData.getRatePlanConfigs())))
		{
			handlingAttributes(dayRatesForRoomStayCandidate, propertyData);
		}
	}

	/**
	 * Populates the prices and discounts on {@link RateRangeData} object belonging to a property
	 *
	 * @param dayRatesForRoomStayCandidate
	 *           map of documents per room stay candidates
	 * @param propertyData
	 *           the dto to be populated
	 *
	 */
	@Override
	protected void handlingAttributes(final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final PropertyData propertyData)
	{
		BigDecimal wasRate = BigDecimal.valueOf(0);
		BigDecimal totalDiscount = BigDecimal.valueOf(0);
		final RateRangeData rateRange = new RateRangeData();
		final List<DayRateData> dayRates = new ArrayList<>();

		for (final Map.Entry<Integer, List<AccommodationOfferingDayRateData>> entry : dayRatesForRoomStayCandidate.entrySet())
		{
			for (final AccommodationOfferingDayRateData rate : entry.getValue())
			{
				final DayRateData dayRateData = new DayRateData();
				dayRateData.setDateOfStay(rate.getDateOfStay());

				final BigDecimal dayPrice = rate.getPrice().getValue();

				final BigDecimal taxesPrice = Objects.isNull(rate.getTotalTaxes()) ? BigDecimal.ZERO
						: rate.getTotalTaxes().getValue();
				dayRateData.setDailyWasRate(createPriceData(dayPrice.add(taxesPrice)));

				BigDecimal dayDiscount = BigDecimal.valueOf(0);
				if (Objects.nonNull(rate.getPromotionalDiscount()))
				{
					dayDiscount = rate.getPromotionalDiscount().getPrice().getValue();
					dayRateData.setDailyDiscount(createPriceData(dayDiscount));
				}

				dayRateData.setDailyActualRate(createPriceData(dayPrice.subtract(dayDiscount)));
				dayRates.add(dayRateData);

				wasRate = wasRate.add(dayPrice).add(taxesPrice);
				totalDiscount = totalDiscount.add(dayDiscount);
			}
		}

		rateRange.setDayRates(dayRates);
		rateRange.setCurrencyCode(getCommonI18NService().getCurrentCurrency().getIsocode());
		rateRange.setWasRate(createPriceData(wasRate));
		rateRange.setTotalDiscount(createPriceData(totalDiscount));
		rateRange.setActualRate(createPriceData(wasRate.subtract(totalDiscount)));
		propertyData.setRateRange(rateRange);
	}

	protected PriceData createPriceData(final BigDecimal dayPrice)
	{
		return getTravelCommercePriceFacade().createPriceData(dayPrice.doubleValue());
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
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
