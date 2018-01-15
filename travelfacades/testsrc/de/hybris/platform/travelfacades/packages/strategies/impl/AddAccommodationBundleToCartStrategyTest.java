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

package de.hybris.platform.travelfacades.packages.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddAccommodationBundleToCartStrategyTest
{
	@InjectMocks
	AddAccommodationBundleToCartStrategy addAccommodationBundleToCartStrategy;

	@Mock
	AccommodationCartFacade accommodationCartFacade;

	@Test
	public void testAddBundleToCart() throws CommerceCartModificationException
	{
		final AccommodationBundleTemplateModel bundleTemplate = new AccommodationBundleTemplateModel();
		final AddDealToCartData addDealToCartData = new AddDealToCartData();

		final List<CartModificationData> cartModifications = Collections.emptyList();
		Mockito.when(accommodationCartFacade.addAccommodationBundleToCart(bundleTemplate, addDealToCartData))
				.thenReturn(cartModifications);

		Assert.assertEquals(cartModifications,
				addAccommodationBundleToCartStrategy.addBundleToCart(bundleTemplate, addDealToCartData));
	}
}
