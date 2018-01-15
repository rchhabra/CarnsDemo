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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RetainAdminFeeOnRefundStrategyTest
{
	@InjectMocks
	RetainAdminFeeOnRefundStrategy retainAdminFeeOnRefundStrategy;

	@Mock
	private BookingService bookingService;

	@Test
	public void testApplyStrategy()
	{
		final OrderModel order = new OrderModel();

		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final ProductModel product1 = new ProductModel();
		product1.setProductType(ProductType.FEE);
		product1.setCode(TravelservicesConstants.ADMIN_FEE_PRODUCT_CODE);
		entry1.setProduct(product1);
		entry1.setBasePrice(10d);

		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		final ProductModel product2 = new ProductModel();
		product2.setProductType(ProductType.FEE);
		product2.setCode("TEST_PRODCUT_CODE");
		entry2.setProduct(product2);

		final AbstractOrderEntryModel entry3 = new AbstractOrderEntryModel();
		final ProductModel product3 = new ProductModel();
		product3.setProductType(ProductType.ACCOMMODATION);
		product3.setCode(TravelservicesConstants.ADMIN_FEE_PRODUCT_CODE);
		entry3.setProduct(product3);

		order.setEntries(Stream.of(entry3, entry2, entry1).collect(Collectors.toList()));
		Mockito.when(bookingService.getOrderTotalPriceByType(order, OrderEntryType.TRANSPORT)).thenReturn(100d);
		Assert.assertEquals(90d, retainAdminFeeOnRefundStrategy.applyStrategy(order, OrderEntryType.TRANSPORT), 0.001);
	}
}