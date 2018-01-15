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

import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;


/**
 * Populates the basic information of the packageResponseData.
 */
public class DealPackageResponseBasicHandler implements PackageResponseHandler
{
	@Override
	public void handle(final PackageRequestData packageRequestData, final PackageResponseData packageResponseData)
	{
		packageResponseData.setStartingDatePattern(packageRequestData.getStartingDatePattern());
	}
}
