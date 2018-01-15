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

package de.hybris.platform.travelfacades.facades.packages.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.facades.packages.DealBundleTemplateFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.packages.strategies.AddBundleToCartByTypeStrategy;
import de.hybris.platform.travelfacades.packages.strategies.impl.AddAccommodationBundleToCartStrategy;
import de.hybris.platform.travelfacades.packages.strategies.impl.AddStandardBundleToCartStrategy;
import de.hybris.platform.travelfacades.packages.strategies.impl.AddTransportBundleToCartStrategy;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultDealCartFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDealCartFacadeTest
{

	@InjectMocks
	DefaultDealCartFacade defaultDealCartFacade;

	@Mock
	private CommerceCartService commerceCartService;

	@Mock
	private CartService cartService;

	@Mock
	private DealBundleTemplateFacade dealBundleTemplateFacade;

	@Mock
	private TravelCartFacade cartFacade;

	@Mock
	AddAccommodationBundleToCartStrategy addAccommodationBundleToCartStrategy;

	@Mock
	AddStandardBundleToCartStrategy addStandardBundleToCartStrategy;

	@Mock
	AddTransportBundleToCartStrategy addTransportBundleToCartStrategy;

	@Before
	public void setUp() throws CommerceCartModificationException
	{
		Mockito.doNothing().when(cartFacade).deleteCurrentCart();
		Mockito.doNothing().when(commerceCartService).removeAllEntries(Matchers.any(CommerceCartParameter.class));
		Mockito.when(cartService.getSessionCart()).thenReturn(new CartModel());

		final Map<String, AddBundleToCartByTypeStrategy> addBundleByTypeStrategyMap = new HashMap<>();
		addBundleByTypeStrategyMap.put("TransportBundleTemplateModel", addTransportBundleToCartStrategy);
		addBundleByTypeStrategyMap.put("AccommodationBundleTemplateModel", addAccommodationBundleToCartStrategy);
		addBundleByTypeStrategyMap.put("StandardBundleTemplateModel", addStandardBundleToCartStrategy);
		addBundleByTypeStrategyMap.put("DEFAULT", addStandardBundleToCartStrategy);

		defaultDealCartFacade.setAddBundleByTypeStrategyMap(addBundleByTypeStrategyMap);
		Mockito.when(addStandardBundleToCartStrategy.addBundleToCart(Matchers.any(), Matchers.any()))
				.thenThrow(new CommerceCartModificationException("CommerceCartModificationException"));
		Mockito.when(addTransportBundleToCartStrategy.addBundleToCart(Matchers.any(), Matchers.any()))
				.thenReturn(Collections.singletonList(createCartModificationData(CommerceCartModificationStatus.SUCCESS, 1)));
		Mockito.when(addAccommodationBundleToCartStrategy.addBundleToCart(Matchers.any(), Matchers.any()))
				.thenReturn(Collections.singletonList(createCartModificationData(CommerceCartModificationStatus.SUCCESS, 1)));
		Mockito.doNothing().when(cartFacade).evaluateCart();

	}

	@Test
	public void testAddDealToCartForNullMasterBundleTemplate()
	{

		Mockito.when(dealBundleTemplateFacade.getDealBundleTemplateById(Matchers.anyString())).thenReturn(null);
		final AddDealToCartData addDealToCartData = new AddDealToCartData();
		addDealToCartData.setDealBundleId("TEST_DEAL_BUNDLE_ID");
		Assert.assertEquals(defaultDealCartFacade.addDealToCart(addDealToCartData).get(0).getStatusCode(),
				CommerceCartModificationStatus.UNAVAILABLE);
	}

	@Test
	public void testAddDealToCartForDealDateOutOfValidDealDates()
	{
		final DealBundleTemplateModel masterBundleTemplate = new DealBundleTemplateModel();
		masterBundleTemplate.setLength(5);
		masterBundleTemplate.setStartingDatePattern("0 0 0 ? * SAT");
		Mockito.when(dealBundleTemplateFacade.getDealBundleTemplateById(Matchers.anyString())).thenReturn(masterBundleTemplate);

		final Date startingDate = TravelDateUtils.getDate("02/05/2017", TravelservicesConstants.DATE_PATTERN);
		final AddDealToCartData addDealToCartData = new AddDealToCartData();
		addDealToCartData.setStartingDate(startingDate);
		addDealToCartData.setDealBundleId("TEST_DEAL_BUNDLE_ID");
		Assert.assertEquals(defaultDealCartFacade.addDealToCart(addDealToCartData).get(0).getStatusCode(),
				CommerceCartModificationStatus.UNAVAILABLE);
	}

	@Test
	public void testAddDealToCartForCommerceCartModificationException()
	{
		final DealBundleTemplateModel masterBundleTemplate = new DealBundleTemplateModel();
		masterBundleTemplate.setLength(5);
		masterBundleTemplate.setStartingDatePattern("0 0 0 ? * *");
		final List<BundleTemplateModel> childTemplates = new ArrayList();
		childTemplates.add(new BundleTemplateModel());
		childTemplates.add(new AccommodationBundleTemplateModel());
		masterBundleTemplate.setChildTemplates(childTemplates);
		Mockito.when(dealBundleTemplateFacade.getDealBundleTemplateById(Matchers.anyString())).thenReturn(masterBundleTemplate);

		final Date startingDate = TravelDateUtils.getDate("02/05/2017", TravelservicesConstants.DATE_PATTERN);
		final AddDealToCartData addDealToCartData = new AddDealToCartData();
		addDealToCartData.setStartingDate(startingDate);
		addDealToCartData.setDealBundleId("TEST_DEAL_BUNDLE_ID");
		Assert.assertEquals(defaultDealCartFacade.addDealToCart(addDealToCartData).get(0).getStatusCode(),
				CommerceCartModificationStatus.UNAVAILABLE);
	}

	@Test
	public void testAddDealToCartForDealDateInValidDealDates()
	{
		final DealBundleTemplateModel masterBundleTemplate = new DealBundleTemplateModel();
		masterBundleTemplate.setLength(5);
		masterBundleTemplate.setStartingDatePattern("0 0 0 ? * *");
		final List<BundleTemplateModel> childTemplates = new ArrayList();
		childTemplates.add(new AccommodationBundleTemplateModel());
		masterBundleTemplate.setChildTemplates(childTemplates);
		Mockito.when(dealBundleTemplateFacade.getDealBundleTemplateById("TEST_DEAL_BUNDLE_ID")).thenReturn(masterBundleTemplate);

		final Date startingDate = TravelDateUtils.getDate("02/05/2017", TravelservicesConstants.DATE_PATTERN);
		final AddDealToCartData addDealToCartData = new AddDealToCartData();
		addDealToCartData.setStartingDate(startingDate);
		addDealToCartData.setDealBundleId("TEST_DEAL_BUNDLE_ID");
		Assert.assertEquals(defaultDealCartFacade.addDealToCart(addDealToCartData).get(0).getStatusCode(),
				CommerceCartModificationStatus.SUCCESS);
	}

	@Test
	public void testIsDealInCart()
	{
		Mockito.when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		Mockito.when(dealBundleTemplateFacade.isDealAbstractOrder(Matchers.any(CartModel.class))).thenReturn(Boolean.TRUE);
		Assert.assertTrue(defaultDealCartFacade.isDealInCart());
	}

	@Test
	public void testIsDealInCartForEmptyCart()
	{
		Mockito.when(cartService.hasSessionCart()).thenReturn(Boolean.FALSE);
		Assert.assertFalse(defaultDealCartFacade.isDealInCart());
	}

	@Test
	public void testIsDealInCartForCartNotContaingDeal()
	{
		Mockito.when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		Mockito.when(dealBundleTemplateFacade.isDealAbstractOrder(Matchers.any(CartModel.class))).thenReturn(Boolean.FALSE);
		Assert.assertFalse(defaultDealCartFacade.isDealInCart());
	}

	protected CartModificationData createCartModificationData(final String status, final long quantity)
	{
		final CartModificationData cartModificationData = new CartModificationData();
		cartModificationData.setQuantityAdded(quantity);
		cartModificationData.setStatusCode(status);
		return cartModificationData;
	}

}
