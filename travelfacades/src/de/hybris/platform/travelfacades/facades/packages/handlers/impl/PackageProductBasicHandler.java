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

import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.travelfacades.facades.packages.handlers.StandardPackageProductHandler;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link StandardPackageProductHandler} interface. This handler is responsible to create one
 * {@link PackageProductData} for each productModel in the given bundleTemplate and to set the productData and the quantity on the
 * newly created PackageProductData.
 */
public class PackageProductBasicHandler implements StandardPackageProductHandler
{
	private BundleTemplateService bundleTemplateService;

	@Override
	public void handle(final BundleTemplateData bundleTemplate, final List<PackageProductData> packageProductDataList)
	{
		final int quantity = getTotalNumberOfTravellers(bundleTemplate.getId());

		List<PackageProductData> newPackageProducts = bundleTemplate.getProducts().stream().map(productData ->
		{
			PackageProductData packageProductData = new PackageProductData();
			packageProductData.setProduct(productData);
			packageProductData.setQuantity(quantity);
			return packageProductData;
		}).collect(Collectors.toList());

		packageProductDataList.addAll(newPackageProducts);
	}

	/**
	 * Returns the total number of travellers based on the {@link GuestCountModel} of the root template for the given bundle Id
	 *
	 * @param bundleTemplateId
	 * 		the bundleTemplate id
	 *
	 * @return the int representing the total number of travellers
	 */
	protected int getTotalNumberOfTravellers(final String bundleTemplateId)
	{
		BundleTemplateModel standardBundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);
		BundleTemplateModel rootBundleTemplate = getBundleTemplateService().getRootBundleTemplate(standardBundleTemplate);

		if (!(rootBundleTemplate instanceof DealBundleTemplateModel))
		{
			return 0;
		}
		DealBundleTemplateModel dealBundleTemplate = (DealBundleTemplateModel) rootBundleTemplate;

		return dealBundleTemplate.getGuestCounts().stream().mapToInt(GuestCountModel::getQuantity).sum();
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
	 * 		the bundleTemplateService to set
	 */
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
