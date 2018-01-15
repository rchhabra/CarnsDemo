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
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageProductBasicHandlerTest
{
	@InjectMocks
	PackageProductBasicHandler packageProductBasicHandler;

	@Mock
	BundleTemplateService bundleTemplateService;

	@Test
	public void testHandle()
	{
		final BundleTemplateData bundleTemplate = new BundleTemplateData();
		final List<PackageProductData> packageProductDataList = new ArrayList<>();

		bundleTemplate.setId("templateId");
		final List<ProductData> products = new ArrayList<>();
		final ProductData productData = new ProductData();
		products.add(productData);
		bundleTemplate.setProducts(products);

		final BundleTemplateModel standardBundleTemplate = new BundleTemplateModel();
		final DealBundleTemplateModel rootBundleTemplate = new DealBundleTemplateModel();
		final List<GuestCountModel> guestCounts = new ArrayList<>();
		final GuestCountModel guestCountModel = new GuestCountModel();
		guestCountModel.setQuantity(2);
		guestCounts.add(guestCountModel);
		rootBundleTemplate.setGuestCounts(guestCounts);

		Mockito.when(bundleTemplateService.getBundleTemplateForCode(bundleTemplate.getId())).thenReturn(standardBundleTemplate);
		Mockito.when(bundleTemplateService.getRootBundleTemplate(standardBundleTemplate)).thenReturn(rootBundleTemplate);

		packageProductBasicHandler.handle(bundleTemplate, packageProductDataList);
		Assert.assertTrue(CollectionUtils.isNotEmpty(packageProductDataList));
	}
}
