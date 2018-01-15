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

package de.hybris.platform.travelservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCommerceUpdateCartEntryStrategyTest
{
	@InjectMocks
	private final DefaultTravelCommerceUpdateCartEntryStrategy travelCommerceUpdateCartEntryStrategy = new DefaultTravelCommerceUpdateCartEntryStrategy();

	@Mock
	private BookingService bookingService;

	@Mock
	private AbstractOrderEntryModel entryToUpdate;

	@Mock
	private CartModel cartModel;

	@Mock
	private ModelService modelService;

	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Mock
	private ProductModel productModel;

	@Mock
	private TravelRouteModel travelRoute;

	@Mock
	private TravelOrderEntryInfoModel travelOrderEntryInfoModel;

	final long actualAllowedQuantityChange = 0;
	long newQuantity = 0;
	final Integer maxOrderQuantity = 0;

	@Before
	public void setup()
	{
		BDDMockito.given(entryToUpdate.getProduct()).willReturn(productModel);
		BDDMockito.given(entryToUpdate.getTravelOrderEntryInfo()).willReturn(travelOrderEntryInfoModel);
		BDDMockito.given(travelOrderEntryInfoModel.getTravelRoute()).willReturn(travelRoute);
	}


	//entryNewQuantity==0
	//for AmendStatus.NEW && newQuantity = 0
	@Test
	public void testModifyEntryWithNoNewQty()
	{
		BDDMockito.given(entryToUpdate.getAmendStatus()).willReturn(AmendStatus.NEW);
		final CommerceCartModification commerceCartModification = travelCommerceUpdateCartEntryStrategy.modifyEntry(cartModel,
				entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertNotNull(commerceCartModification);
	}

	//entryNewQuantity==0
	//for AmendStatus.NEW && newQuantity != 0
	@Test
	public void testModifyEntryWithNewQty()
	{
		newQuantity = 1;
		BDDMockito.given(entryToUpdate.getAmendStatus()).willReturn(AmendStatus.NEW);
		final CommerceCartModification commerceCartModification4 = travelCommerceUpdateCartEntryStrategy.modifyEntry(cartModel,
				entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertNotNull(commerceCartModification4);
	}

	//for AmendStatus.SAME
	@Test
	public void testModifyEntryAmendStatusSame()
	{
		BDDMockito.given(entryToUpdate.getAmendStatus()).willReturn(AmendStatus.SAME);
		final CommerceCartModification commerceCartModification1 = travelCommerceUpdateCartEntryStrategy.modifyEntry(cartModel,
				entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertNotNull(commerceCartModification1);
	}

	//for AmendStatus.CHANGED
	@Test
	public void testModifyEntryAmendStatusChanged()
	{
		BDDMockito.given(entryToUpdate.getAmendStatus()).willReturn(AmendStatus.CHANGED);
		BDDMockito.given(productModel.getCode()).willReturn("123");
		BDDMockito.given(travelRoute.getCode()).willReturn("456");
		final OrderModel originalOrder = BDDMockito.mock(OrderModel.class);
		BDDMockito.given(cartModel.getOriginalOrder()).willReturn(originalOrder);

		//AbstractOrderEntryModel == null
		BDDMockito.given(bookingService.getOriginalOrderEntry(cartModel.getOriginalOrder().getCode(), productModel.getCode(),
				travelRoute.getCode(), new ArrayList<String>(), new ArrayList<String>(), Boolean.TRUE)).willReturn(null);

		final CommerceCartModification commerceCartModification2 = travelCommerceUpdateCartEntryStrategy.modifyEntry(cartModel,
				entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertNotNull(commerceCartModification2);
	}

	//AbstractOrderEntryModel != null && entryNewQuantity.equals(originalEntry.getQuantity())
	@Test
	public void testModifyEntryWithSameQty()
	{
		final OrderModel originalOrder = BDDMockito.mock(OrderModel.class);
		BDDMockito.given(cartModel.getOriginalOrder()).willReturn(originalOrder);
		BDDMockito
				.given(bookingService.getOriginalOrderEntry(cartModel.getOriginalOrder().getCode(), productModel.getCode(),
						travelRoute.getCode(), new ArrayList<String>(), new ArrayList<String>(), Boolean.TRUE))
				.willReturn(entryToUpdate);
		newQuantity = 5L;

		BDDMockito.given(entryToUpdate.getQuantity()).willReturn(5L);
		final CommerceCartModification commerceCartModification3 = travelCommerceUpdateCartEntryStrategy.modifyEntry(cartModel,
				entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertNotNull(commerceCartModification3);
	}


	//AbstractOrderEntryModel != null && entryNewQuantity.equals(originalEntry.getQuantity())
	@Test
	public void testModifyEntryWithDiffQty()
	{
		final OrderModel originalOrder = BDDMockito.mock(OrderModel.class);
		BDDMockito.given(cartModel.getOriginalOrder()).willReturn(originalOrder);
		BDDMockito
				.given(bookingService.getOriginalOrderEntry(cartModel.getOriginalOrder().getCode(), productModel.getCode(),
						travelRoute.getCode(), new ArrayList<String>(), new ArrayList<String>(), Boolean.TRUE))
				.willReturn(entryToUpdate);
		newQuantity = 10L;

		BDDMockito.given(entryToUpdate.getQuantity()).willReturn(5L);
		final CommerceCartModification commerceCartModification5 = travelCommerceUpdateCartEntryStrategy.modifyEntry(cartModel,
				entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertNotNull(commerceCartModification5);
	}
}
