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

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.AccommodationDiscountEvaluationStrategy;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of {@link AccommodationDetailsHandler} handling accommodation prices
 */
public class AccommodationPriceHandler extends AbstractAccommodationPriceHandler
{
	private PriceService priceService;
	private ProductService productService;
	private Map<String, AccommodationDiscountEvaluationStrategy> accommodationDiscountEvaluationStrategyMap;

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final String currencyIso = getCurrentCurrency(availabilityRequestData);

		for (final RoomStayData roomStayData : accommodationAvailabilityResponseData.getRoomStays())
		{
			BigDecimal fromPrice = null;
			for (final RatePlanData ratePlan : roomStayData.getRatePlans().stream()
					.filter(ratePlanData -> ratePlanData.getAvailableQuantity() != null && ratePlanData.getAvailableQuantity() > 0)
					.collect(Collectors.toList()))
			{
				BigDecimal wasRate = BigDecimal.ZERO;
				BigDecimal actualRate = BigDecimal.ZERO;
				BigDecimal discounts = BigDecimal.ZERO;
				for (final RoomRateData roomRateData : ratePlan.getRoomRates())
				{
					populatePricesForRoomRate(roomRateData, currencyIso);
					wasRate = wasRate.add(roomRateData.getRate().getWasRate().getValue());
					actualRate = actualRate.add(roomRateData.getRate().getActualRate().getValue());
					discounts = discounts.add(roomRateData.getRate().getTotalDiscount().getValue());
				}
				ratePlan.setActualRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, actualRate, currencyIso));
				ratePlan.setWasRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, wasRate, currencyIso));
				ratePlan.setTotalDiscount(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, discounts, currencyIso));
				if (fromPrice == null || fromPrice.compareTo(actualRate) > 0)
				{
					fromPrice = actualRate;
				}
			}
			if (fromPrice != null)
			{
				roomStayData.setFromPrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, fromPrice, currencyIso));
			}
		}
	}

	protected void populatePricesForRoomRate(final RoomRateData roomRate, final String currencyIso)
	{
		final ProductModel roomRateProduct = getProductService().getProductForCode(roomRate.getCode());
		final List<PriceInformation> priceInformations = getPriceService().getPriceInformationsForProduct(roomRateProduct);
		final Collection<ProductPromotionModel> promotions = roomRateProduct.getPromotions();

		final Double roomRateBasePrice = getPriceValue(priceInformations);
		final List<TaxData> roomRateTaxes = getTaxes(roomRateProduct.getEurope1Taxes(), currencyIso, roomRateBasePrice);
		final Double roomRateTaxesValue = getTaxesValue(roomRateTaxes);
		final Double roomRateWasPrice = roomRateBasePrice + roomRateTaxesValue;
		final Double roomRateDiscounts = calculateRoomRateDiscounts(roomRateBasePrice, promotions, currencyIso);
		final Double roomRateActualPrice = roomRateWasPrice - roomRateDiscounts;

		final RateData rateData = new RateData();
		rateData.setBasePrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(roomRateBasePrice), currencyIso));
		rateData.setActualRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(roomRateActualPrice), currencyIso));
		rateData.setWasRate(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(roomRateWasPrice), currencyIso));
		rateData.setTotalDiscount(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(roomRateDiscounts), currencyIso));
		rateData.setTaxes(roomRateTaxes);
		rateData.setTotalTax(
				createTaxData(roomRateTaxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum(), currencyIso));
		roomRate.setRate(rateData);

	}

	protected Double calculateRoomRateDiscounts(final Double basePrice, final Collection<ProductPromotionModel> promotions,
			final String currencyIso)
	{
		Double totalDiscounts = 0d;
		for (final ProductPromotionModel promotion : promotions.stream().filter(ProductPromotionModel::getEnabled)
				.collect(Collectors.toList()))
		{
			final AccommodationDiscountEvaluationStrategy strategy = getAccommodationDiscountEvaluationStrategyMap()
					.get(promotion.getClass().getSimpleName());
			if (strategy != null)
			{
				totalDiscounts += strategy.evaluateDiscount(basePrice, promotion, currencyIso);
			}
		}

		return totalDiscounts;
	}

	protected PriceService getPriceService()
	{
		return priceService;
	}

	@Required
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected Map<String, AccommodationDiscountEvaluationStrategy> getAccommodationDiscountEvaluationStrategyMap()
	{
		return accommodationDiscountEvaluationStrategyMap;
	}

	@Required
	public void setAccommodationDiscountEvaluationStrategyMap(
			final Map<String, AccommodationDiscountEvaluationStrategy> accommodationDiscountEvaluationStrategyMap)
	{
		this.accommodationDiscountEvaluationStrategyMap = accommodationDiscountEvaluationStrategyMap;
	}
}
