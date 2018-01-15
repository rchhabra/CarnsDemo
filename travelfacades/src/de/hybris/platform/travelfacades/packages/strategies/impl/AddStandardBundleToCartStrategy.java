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

package de.hybris.platform.travelfacades.packages.strategies.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.packages.strategies.AddBundleToCartByTypeStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Concrete implementation of {@link AddBundleToCartByTypeStrategy} allowing to add products belonging to a
 * StandardBundleTemplate to the cart
 */
public class AddStandardBundleToCartStrategy implements AddBundleToCartByTypeStrategy
{
	private TravelCartFacade cartFacade;
	private BundleCartFacade bundleCartFacade;

	@Override
	public List<CartModificationData> addBundleToCart(final BundleTemplateModel bundleTemplate,
			final AddDealToCartData addDealToCartData) throws CommerceCartModificationException
	{
		final List<CartModificationData> cartModificationDatas = new ArrayList<>();
		final long quantityToAdd = addDealToCartData.getPassengerTypes().stream().mapToLong(PassengerTypeQuantityData::getQuantity)
				.sum();
		final List<ProductModel> products = bundleTemplate.getProducts();
		if (CollectionUtils.isEmpty(products))
		{
			return cartModificationDatas;
		}
		for (final ProductModel product : products)
		{
			final CartModificationData cartModification = CollectionUtils.isEmpty(cartModificationDatas) ?
					getBundleCartFacade().startBundle(bundleTemplate.getId(), product.getCode(), quantityToAdd) :
					getBundleCartFacade().addToCart(product.getCode(), quantityToAdd,
							cartModificationDatas.stream().findAny().get().getEntry().getEntryGroupNumbers().stream().findFirst().get());
			cartModificationDatas.add(cartModification);
		}
		cartModificationDatas.stream().map(cartModification -> cartModification.getEntry().getEntryNumber())
				.collect(Collectors.toList()).forEach(number -> getCartFacade().setOrderEntryType(OrderEntryType.DEFAULT, number));

		return cartModificationDatas;
	}

	/**
	 * Gets cart facade.
	 *
	 * @return cartFacade cart facade
	 */
	protected TravelCartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * Sets cart facade.
	 *
	 * @param cartFacade
	 * 		the cart facade
	 */
	public void setCartFacade(final TravelCartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	/**
	 * Gets bundle cart facade.
	 *
	 * @return the bundle cart facade
	 */
	protected BundleCartFacade getBundleCartFacade()
	{
		return bundleCartFacade;
	}

	/**
	 * Sets bundle cart facade.
	 *
	 * @param bundleCartFacade
	 * 		the bundle cart facade
	 */
	@Required
	public void setBundleCartFacade(final BundleCartFacade bundleCartFacade)
	{
		this.bundleCartFacade = bundleCartFacade;
	}
}
