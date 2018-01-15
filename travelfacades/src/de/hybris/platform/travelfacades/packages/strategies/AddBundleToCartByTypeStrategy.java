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

package de.hybris.platform.travelfacades.packages.strategies;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;

import java.util.List;


/**
 * Interface for strategies responsible for adding products belonging to a specific BundleTemplate subtype to the cart
 */
public interface AddBundleToCartByTypeStrategy
{
	/**
	 * Adds the products relative to the specified bundle to the cart. Each implementation covers a specific bundle type.
	 *
	 * @param bundleTemplate
	 * @param addDealToCartData
	 * @throws CommerceCartModificationException
	 */
	List<CartModificationData> addBundleToCart(BundleTemplateModel bundleTemplate, AddDealToCartData addDealToCartData)
			throws CommerceCartModificationException;
}
