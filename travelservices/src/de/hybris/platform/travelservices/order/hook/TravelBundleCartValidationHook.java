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
package de.hybris.platform.travelservices.order.hook;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.hooks.BundleCartValidationHook;

import java.util.List;


/**
 * The type Travel bundle cart validation hook. This class overrides the OOTB BundleCartValidationHook to prevent errors due to
 * erroneous group number. Our validation based on selection criteria is performed in {@link
 * TravelBundleSelectionCriteriaAddToCartMethodHook}
 */
public class TravelBundleCartValidationHook extends BundleCartValidationHook
{
	@Override
	public void afterValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)
	{
		// nop
	}
}
