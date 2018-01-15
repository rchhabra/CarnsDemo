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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.AddBundleToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OriginDestinationValidationStrategyTest
{
	@InjectMocks
	OriginDestinationValidationStrategy originDestinationValidationStrategy;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	CartService cartService;

	@Mock
	TravelRouteService travelRouteService;

	@Test
	public void testValidate()
	{
		final AddBundleToCartRequestData addBundleToCartRequestData = new AddBundleToCartRequestData();
		final AddBundleToCartData addBundleToCartData = new AddBundleToCartData();
		addBundleToCartData.setOriginDestinationRefNumber(1);
		addBundleToCartData.setTravelRouteCode("LTN_CDG");
		final List<AddBundleToCartData> addBundleToCartDatas = Collections.singletonList(addBundleToCartData);
		addBundleToCartRequestData.setAddBundleToCartData(addBundleToCartDatas);

		Mockito.when(cartService.hasSessionCart()).thenReturn(false);

		final AddToCartResponseData addToCartResponseData1 = originDestinationValidationStrategy
				.validate(addBundleToCartRequestData);

		Assert.assertTrue(addToCartResponseData1.getErrors().contains("add.bundle.to.cart.validation.error.no.session.cart"));

		Mockito.when(cartService.hasSessionCart()).thenReturn(true);
		final AbstractOrderEntryModel orderEntry = Mockito.mock(AbstractOrderEntryModel.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(orderEntry.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(orderEntry.getProduct().getProductType()).thenReturn(ProductType.FARE_PRODUCT);
		Mockito.when(orderEntry.getActive()).thenReturn(true);
		Mockito.when(orderEntry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()).thenReturn(0);
		Mockito.when(orderEntry.getTravelOrderEntryInfo().getTravelRoute().getDestination().getLocation().getCode())
				.thenReturn("CDG");

		final TravelRouteModel travelRoute = new TravelRouteModel();
		final TransportFacilityModel origin = new TransportFacilityModel();
		final LocationModel location = new LocationModel();
		location.setCode("LTN");
		origin.setLocation(location);
		travelRoute.setOrigin(origin);
		Mockito
				.when(travelRouteService
						.getTravelRoute(addBundleToCartRequestData.getAddBundleToCartData().get(0).getTravelRouteCode()))
				.thenReturn(travelRoute);

		final List<AbstractOrderEntryModel> orderEntries = Collections.singletonList(orderEntry);
		Mockito.when(cartService.getSessionCart().getEntries()).thenReturn(orderEntries);

		final AddToCartResponseData addToCartResponseData2 = originDestinationValidationStrategy
				.validate(addBundleToCartRequestData);

		Assert.assertFalse(addToCartResponseData2.isValid());

		location.setCode("CDG");
		Assert.assertTrue(originDestinationValidationStrategy.validate(addBundleToCartRequestData).isValid());
	}
}