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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.TaxRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.packages.handlers.StandardPackageProductHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link StandardPackageProductHandler} interface. This handler is responsible to
 * populate the price for each PackageProductData, if the product is available. The wasRate will be the standard price
 * of the product, the actualRate will be the price determined by the changeProductPriceBundleRule if it exist,
 * otherwise will be the same as the wasRate.
 */
public class PackageProductPriceHandler implements StandardPackageProductHandler
{
	private BundleTemplateService bundleTemplateService;
	private ProductService productService;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private CommercePriceService commercePriceService;
	private CommonI18NService commonI18NService;

	@Override
	public void handle(final BundleTemplateData bundleTemplate, final List<PackageProductData> packageProductDataList)
	{
		final BundleTemplateModel bundleTemplateModel = getBundleTemplateService().getBundleTemplateForCode(bundleTemplate.getId());

		packageProductDataList.stream().filter(packageProduct -> (packageProduct.getProduct().getStock() != null
				&& packageProduct.getProduct().getStock().getStockLevel() > 0)).forEach(packageProductData -> {
					final ProductModel productModel = getProductService().getProductForCode(packageProductData.getProduct().getCode());
					packageProductData.setPrice(getPriceForProductAndBundle(productModel, bundleTemplateModel));
				});
	}

	/**
	 * Returns the RateData properly populated given the productModel and the bundleTemplate. The wasRate is the standard
	 * price of the product taken from the priceRow for the current Currency. The actualRate is the price determined by
	 * the changeProductPriceBundleRule if it exist, otherwise will be the same as the wasRate.
	 *
	 * @param product
	 *           as the productModel
	 * @param bundleTemplate
	 *           as the bundleTemplateModel
	 *
	 * @return the RateDate populated based on the given parameters.
	 */
	protected RateData getPriceForProductAndBundle(final ProductModel product, final BundleTemplateModel bundleTemplate)
	{
		final String currencyIso = getCommonI18NService().getCurrentCurrency().getIsocode();

		// Price of product
		final PriceInformation priceInformation = getCommercePriceService().getFromPriceForProduct(product);
		final Double productBasePrice = priceInformation.getPriceValue().getValue();
		final List<TaxData> productTaxes = getTaxes(product.getEurope1Taxes(), currencyIso, productBasePrice);
		final Double productTaxesValue = productTaxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
		final Double productWasPrice = Double.sum(productBasePrice, productTaxesValue);

		// Price information for bundle price
		final PriceInformation priceBundleInformation = getTravelCommercePriceFacade()
				.getPriceInformationByProductPriceBundleRule(bundleTemplate, product.getCode());
		final Double productBundleBasePrice;
		if (priceBundleInformation != null)
		{
			productBundleBasePrice = priceBundleInformation.getPriceValue().getValue();
		}
		else
		{
			productBundleBasePrice = productBasePrice;
		}
		final List<TaxData> productBundleTaxes = getTaxes(product.getEurope1Taxes(), currencyIso, productBundleBasePrice);
		final Double productBundleTaxesValue = productBundleTaxes.stream()
				.mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
		final Double productActualPrice = Double.sum(productBundleBasePrice, productBundleTaxesValue);

		final RateData rateData = new RateData();

		rateData.setBasePrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(productBundleBasePrice), currencyIso));
		rateData.setActualRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(productActualPrice), currencyIso));
		rateData.setWasRate(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(productWasPrice), currencyIso));
		rateData.setTotalDiscount(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(Double.sum(productWasPrice, -productActualPrice)), currencyIso));
		rateData.setTaxes(productBundleTaxes);

		final TaxData totalTaxData = new TaxData();
		final double totalTax = productBundleTaxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
		totalTaxData.setPrice(
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(totalTax), currencyIso));
		rateData.setTotalTax(totalTaxData);

		return rateData;
	}

	/**
	 * Returns the list of TaxData.
	 *
	 * @param taxes
	 *           as the taxes
	 * @param currencyIso
	 *           as the currencyIso
	 * @param productBasePrice
	 *           as the productBasePrice
	 *
	 * @return the List of TaxData
	 */
	protected List<TaxData> getTaxes(final Collection<TaxRowModel> taxes, final String currencyIso, final Double productBasePrice)
	{
		final List<TaxData> result = new ArrayList<>();

		final List<TaxRowModel> taxRowModels = taxes.stream().filter(
				tax -> Objects.isNull(tax.getCurrency()) || StringUtils.equalsIgnoreCase(tax.getCurrency().getIsocode(), currencyIso))
				.collect(Collectors.toList());

		result.addAll(taxRowModels.stream().map(tax -> {
			final TaxData taxData = new TaxData();
			final Double taxValue = Objects.isNull(tax.getCurrency()) ? productBasePrice / 100 * tax.getValue() : tax.getValue();
			taxData.setPrice(
					getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(taxValue), currencyIso));
			return taxData;
		}).collect(Collectors.toList()));

		return result;
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

	/**
	 * @return the commercePriceService
	 */
	protected CommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	/**
	 * @param commercePriceService
	 *           the commercePriceService to set
	 */
	@Required
	public void setCommercePriceService(final CommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
