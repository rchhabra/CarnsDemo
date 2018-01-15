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
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of {@link AccommodationDetailsHandler} handling accommodation prices
 */
public class DealAccommodationPriceHandler extends AbstractAccommodationPriceHandler
{
	private BundleTemplateService bundleTemplateService;
	private PriceService priceService;
	private ProductService productService;

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final String currencyIso = getCurrentCurrency(availabilityRequestData);
		final BundleTemplateModel accommodationBundleTemplateModel = getBundleTemplateService()
				.getBundleTemplateForCode(availabilityRequestData.getCriterion().getBundleTemplateId());

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
					populatePricesForRoomRateAndBundleTemplate(roomRateData, accommodationBundleTemplateModel, currencyIso);
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

	/**
	 * Populates the roomRates prices based on the priceRow of the RoomRateProductModel and the
	 * ChangeProductPriceBundleRule for the given bundle. The actualRate is set as the price of the product included in
	 * the bundle; the wasRate is set as the price of the product if not included in the bundle; the discount is set as
	 * the difference between the wasRate and the actualRate.
	 *
	 * @param roomRate
	 *           as the roomRate
	 * @param bundleTemplate
	 *           as the bundleTemplate
	 * @param currencyIso
	 *           as the currencyIso
	 */
	protected void populatePricesForRoomRateAndBundleTemplate(final RoomRateData roomRate,
			final BundleTemplateModel bundleTemplate, final String currencyIso)
	{
		final ProductModel roomRateProduct = getProductService().getProductForCode(roomRate.getCode());

		// Price information for bundle price
		final PriceInformation priceBundleInformation = getTravelCommercePriceFacade()
				.getPriceInformationByProductPriceBundleRule(bundleTemplate, roomRateProduct.getCode());

		List<PriceInformation> priceInformations;
		if (Objects.nonNull(priceBundleInformation))
		{
			priceInformations = Collections.singletonList(priceBundleInformation);
		}
		else
		{
			priceInformations = getPriceService().getPriceInformationsForProduct(roomRateProduct);
		}
		final Double roomRateBundleBasePrice = getPriceValue(priceInformations);
		final List<TaxData> roomRateBundleTaxes = getTaxes(roomRateProduct.getEurope1Taxes(), currencyIso, roomRateBundleBasePrice);
		final Double roomRateBundleTaxesValue = getTaxesValue(roomRateBundleTaxes);
		final Double roomRateActualPrice = Double.sum(roomRateBundleBasePrice, roomRateBundleTaxesValue);

		// Price of product
		priceInformations = getPriceService().getPriceInformationsForProduct(roomRateProduct);
		final Double roomRateBasePrice = getPriceValue(priceInformations);
		final List<TaxData> roomRateTaxes = getTaxes(roomRateProduct.getEurope1Taxes(), currencyIso, roomRateBasePrice);
		final Double roomRateTaxesValue = getTaxesValue(roomRateTaxes);
		final Double roomRateWasPrice = roomRateBasePrice + roomRateTaxesValue;

		// Discount = wasRate - actualPrice
		final Double roomRateDiscounts = Double.sum(roomRateWasPrice, -roomRateActualPrice);

		final RateData rateData = new RateData();
		rateData.setBasePrice(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(roomRateBundleBasePrice),
						currencyIso));
		rateData
				.setActualRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
						BigDecimal.valueOf(roomRateActualPrice), currencyIso));
		rateData.setWasRate(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(roomRateWasPrice), currencyIso));
		rateData.setTotalDiscount(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(roomRateDiscounts),
						currencyIso));
		rateData.setTaxes(roomRateBundleTaxes);
		rateData.setTotalTax(createTaxData(
				roomRateBundleTaxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum(), currencyIso));
		roomRate.setRate(rateData);
	}

	/**
	 * @return the bundleTemplateService
	 */
	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	/**
	 * @param bundleTemplateService
	 *           the bundleTemplateService to set
	 */
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	/**
	 * @return the priceService
	 */
	protected PriceService getPriceService()
	{
		return priceService;
	}

	/**
	 * @param priceService
	 *           the priceService to set
	 */
	@Required
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}
}
