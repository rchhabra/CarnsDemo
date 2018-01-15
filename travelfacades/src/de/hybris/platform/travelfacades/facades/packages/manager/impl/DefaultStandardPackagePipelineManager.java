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

package de.hybris.platform.travelfacades.facades.packages.manager.impl;

import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.travelfacades.facades.packages.handlers.StandardPackageProductHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.StandardPackagePipelineManager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation class for the {@link StandardPackagePipelineManager}. This pipeline manager will instantiate a new list of
 * {@link PackageProductData} and will call a list of handlers to populate the package product data with different information.
 */
public class DefaultStandardPackagePipelineManager implements StandardPackagePipelineManager
{
	private List<StandardPackageProductHandler> handlers;

	@Override
	public List<PackageProductData> executePipeline(final BundleTemplateData bundleTemplate)
	{
		List<PackageProductData> packageProductDataList = new ArrayList<>();

		for (final StandardPackageProductHandler handler : getHandlers())
		{
			handler.handle(bundleTemplate, packageProductDataList);
		}
		return packageProductDataList;
	}

	/**
	 * @return the handlers
	 */
	protected List<StandardPackageProductHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * @param handlers
	 * 		the handlers to set
	 */
	@Required
	public void setHandlers(final List<StandardPackageProductHandler> handlers)
	{
		this.handlers = handlers;
	}

}
