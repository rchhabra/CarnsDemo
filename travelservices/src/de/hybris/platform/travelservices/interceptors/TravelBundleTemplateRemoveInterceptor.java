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

package de.hybris.platform.travelservices.interceptors;

import de.hybris.platform.configurablebundleservices.interceptor.impl.BundleTemplateRemoveInterceptor;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateStatusModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;


/**
 * Interceptor called on BundleTemplate removal performing travel specific logic
 */
public class TravelBundleTemplateRemoveInterceptor extends BundleTemplateRemoveInterceptor
{
	/**
	 * Avoid deleting {@link BundleTemplateStatusModel} on removing a BundleTemplateModel
	 * @param ctx
	 * @param bundleTemplate
	 */
	@Override
	protected void removeStatus(final InterceptorContext ctx, final BundleTemplateModel bundleTemplate)
	{
		// do nothing
	}
}
