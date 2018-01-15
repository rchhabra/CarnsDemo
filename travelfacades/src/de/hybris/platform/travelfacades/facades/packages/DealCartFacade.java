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

package de.hybris.platform.travelfacades.facades.packages;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;

import java.util.List;


/**
 * Interface for class responsible for handling all the operations related to add a deal to the cart
 */
public interface DealCartFacade
{
	/**
	 * This method allows to add a deal to the cart
	 *
	 * @param addDealToCartData
	 * @return list
	 */
	List<CartModificationData> addDealToCart(AddDealToCartData addDealToCartData);

	/**
	 * Checks whether current cart contains a deal.
	 *
	 * @return boolean
	 */
	boolean isDealInCart();
}
