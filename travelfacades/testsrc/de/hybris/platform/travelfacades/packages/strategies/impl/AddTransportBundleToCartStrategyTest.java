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
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.model.deal.RouteBundleTemplateModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddTransportBundleToCartStrategyTest
{
	@InjectMocks
	AddTransportBundleToCartStrategy addTransportBundleToCartStrategy;

	@Mock
	TravelCartFacade cartFacade;

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

		final List<BundleTemplateModel> childTemplates = new ArrayList<>();
		final RouteBundleTemplateModel routeBundleTemplate = new RouteBundleTemplateModel();

		routeBundleTemplate.setOriginDestinationRefNumber(0);
		routeBundleTemplate.setChildTemplates(childTemplates);

		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		final List<TravelBundleTemplateData> bundleTemplates = new ArrayList<>();
		final TravelBundleTemplateData travelBundleTemplate = new TravelBundleTemplateData();
		final List<FareProductData> fareProducts = new ArrayList<>();
		final FareProductData fareProduct = new FareProductData();
		fareProduct.setCode("fareProduct");
		fareProducts.add(fareProduct);
		travelBundleTemplate.setFareProducts(fareProducts);
		bundleTemplates.add(travelBundleTemplate);
		itineraryPricingInfo.setBundleTemplates(bundleTemplates);
		itineraryPricingInfos.add(itineraryPricingInfo);
		addDealToCartData.setItineraryPricingInfos(itineraryPricingInfos);

		final TravelRouteModel travelRoute = new TravelRouteModel();
		travelRoute.setCode("LTN_CDG");
		routeBundleTemplate.setTravelRoute(travelRoute);

		childTemplates.add(routeBundleTemplate);
		bundleTemplate.setChildTemplates(childTemplates);

		final List<CartModificationData> cartModifications = Collections.emptyList();
		Mockito.when(cartFacade.addBundleToCart(Mockito.any(AddBundleToCartRequestData.class))).thenReturn(cartModifications);
		Assert.assertEquals(cartModifications, addTransportBundleToCartStrategy.addBundleToCart(bundleTemplate, addDealToCartData));

	}
}
