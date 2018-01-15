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
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddStandardBundleToCartStrategyTest
{
	@InjectMocks
	AddStandardBundleToCartStrategy addStandardBundleToCartStrategy;

	@Mock
	BundleCartFacade bundleCartFacade;

	@Mock
	private TravelCartFacade cartFacade;

	@Test
	public void testAddBundleToCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final AddDealToCartData addDealToCartData = new AddDealToCartData();

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode("adult");
		passengerTypeQuantityData.setPassengerType(passengerType);
		passengerTypeQuantityData.setQuantity(2);
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = Collections.singletonList(passengerTypeQuantityData);
		addDealToCartData.setPassengerTypes(passengerTypeQuantityList);

		Assert.assertTrue(
				CollectionUtils.isEmpty(addStandardBundleToCartStrategy.addBundleToCart(bundleTemplate, addDealToCartData)));

		bundleTemplate.setId("bundleTemplateId");
		final List<ProductModel> products = new ArrayList<>();
		final ProductModel product = new ProductModel();
		product.setCode("productCode");
		products.add(product);
		bundleTemplate.setProducts(products);

		final List<CartModificationData> cartModifications = new ArrayList<>();
		final CartModificationData cartModification = new CartModificationData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setEntryNumber(1);
		cartModification.setEntry(entry);
		cartModifications.add(cartModification);

		given(bundleCartFacade.startBundle(Matchers.anyString(), Matchers.anyString(), Matchers.anyLong()))
				.willReturn(cartModification);
		given(bundleCartFacade.addToCart(Matchers.anyString(), Matchers.anyLong(), Matchers.anyInt())).willReturn(cartModification);
		Assert.assertEquals(cartModifications, addStandardBundleToCartStrategy.addBundleToCart(bundleTemplate, addDealToCartData));
	}
}
