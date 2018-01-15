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
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;

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
public class OriginDestinationRefNumberValidationStrategyTest
{
	@InjectMocks
	OriginDestinationRefNumberValidationStrategy originDestinationRefNumberValidationStrategy;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	CartService cartService;

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

		Assert.assertFalse(originDestinationRefNumberValidationStrategy.validate(addBundleToCartRequestData).isValid());

		Mockito.when(cartService.hasSessionCart()).thenReturn(true);
		final AbstractOrderEntryModel orderEntry = Mockito.mock(AbstractOrderEntryModel.class, Mockito.RETURNS_DEEP_STUBS);

		final List<AbstractOrderEntryModel> orderEntries = Collections.singletonList(orderEntry);
		Mockito.when(cartService.getSessionCart().getEntries()).thenReturn(orderEntries);

		Mockito.when(orderEntry.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(orderEntry.getProduct().getProductType()).thenReturn(ProductType.FARE_PRODUCT);
		Mockito.when(orderEntry.getActive()).thenReturn(true);
		Mockito.when(orderEntry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()).thenReturn(1);
		Mockito.when(orderEntry.getTravelOrderEntryInfo().getTravelRoute().getDestination().getLocation().getCode())
				.thenReturn("CDG");

		Assert.assertFalse(originDestinationRefNumberValidationStrategy.validate(addBundleToCartRequestData).isValid());

		Mockito.when(orderEntry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()).thenReturn(0);
		Assert.assertTrue(originDestinationRefNumberValidationStrategy.validate(addBundleToCartRequestData).isValid());
	}

}