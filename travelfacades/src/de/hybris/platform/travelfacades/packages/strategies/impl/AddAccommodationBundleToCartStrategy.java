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
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.packages.strategies.AddBundleToCartByTypeStrategy;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of {@link AddBundleToCartByTypeStrategy} allowing to add products belonging to an
 * AccommodationBundleTemplate to the cart
 */
public class AddAccommodationBundleToCartStrategy implements AddBundleToCartByTypeStrategy
{
	private AccommodationCartFacade accommodationCartFacade;

	@Override
	public List<CartModificationData> addBundleToCart(final BundleTemplateModel bundleTemplate,
			final AddDealToCartData addDealToCartData) throws CommerceCartModificationException
	{
		final AccommodationBundleTemplateModel accommodationBundleTemplate = (AccommodationBundleTemplateModel) bundleTemplate;

		return getAccommodationCartFacade().addAccommodationBundleToCart(accommodationBundleTemplate, addDealToCartData);
	}

	/**
	 *
	 * @return the accommodationCartFacade
	 */
	protected AccommodationCartFacade getAccommodationCartFacade()
	{
		return accommodationCartFacade;
	}

	/**
	 *
	 * @param accommodationCartFacade
	 *           the accommodationCartFacade to set
	 */
	@Required
	public void setAccommodationCartFacade(final AccommodationCartFacade accommodationCartFacade)
	{
		this.accommodationCartFacade = accommodationCartFacade;
	}
}
