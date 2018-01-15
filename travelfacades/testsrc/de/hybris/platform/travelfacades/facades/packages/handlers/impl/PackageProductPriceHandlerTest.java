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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.TaxRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageProductPriceHandlerTest
{
	@InjectMocks
	PackageProductPriceHandler packageProductPriceHandler;

	@Mock
	BundleTemplateService bundleTemplateService;

	@Mock
	ProductService productService;

	@Mock
	CommercePriceService commercePriceService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	CommonI18NService commonI18NService;

	@Test
	public void testHandle()
	{
		final BundleTemplateData bundleTemplate = new BundleTemplateData();
		final List<PackageProductData> packageProductDataList = new ArrayList<>();

		bundleTemplate.setId("templateId");
		final PackageProductData packageProductData = new PackageProductData();
		final ProductData product = new ProductData();
		final StockData stock = new StockData();
		stock.setStockLevel(2l);
		product.setStock(stock);
		product.setCode("ProductCode");
		packageProductData.setProduct(product);
		packageProductDataList.add(packageProductData);

		final BundleTemplateModel standardBundleTemplate = new BundleTemplateModel();
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		final TaxRowModel taxrow = new TaxRowModel();
		taxrow.setCurrency(currencyModel);
		taxrow.setValue(10.0);
		final Collection<TaxRowModel> taxes = Collections.singletonList(taxrow);
		final PriceValue priceValue = new PriceValue("GBP", 100, false);
		final PriceInformation priceInformation = new PriceInformation(priceValue);
		final PriceData price = new PriceData();
		price.setValue(BigDecimal.valueOf(10));

		Mockito.when(bundleTemplateService.getBundleTemplateForCode(bundleTemplate.getId())).thenReturn(standardBundleTemplate);
		Mockito.when(productService.getProductForCode(product.getCode())).thenReturn(productModel);
		Mockito.when(productModel.getEurope1Taxes()).thenReturn(taxes);
		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
		Mockito.when(commercePriceService.getFromPriceForProduct(productModel)).thenReturn(priceInformation);
		Mockito.when(travelCommercePriceFacade.createPriceData(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class),
				Mockito.anyString()))
				.thenReturn(price);

		packageProductPriceHandler.handle(bundleTemplate, packageProductDataList);

		Assert.assertTrue(Objects.nonNull(packageProductData.getPrice()));
	}
}
