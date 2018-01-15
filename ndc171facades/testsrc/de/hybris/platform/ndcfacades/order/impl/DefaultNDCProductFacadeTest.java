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

package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.strategies.AmendOrderValidationStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNDCProductFacadeTest
{
	@InjectMocks
	DefaultNDCProductFacade defaultNDCProductFacade = new DefaultNDCProductFacade();


	final OrderModel order = new OrderModel();
	final TravellerModel traveller = new TravellerModel();
	final List<TravellerModel> travellers = Collections.singletonList(traveller);
	ProductModel product;
	final List<String> transportOfferings = Collections.singletonList("8823");
	final String routeCode = "LTN_CDG";

	@Before
	public void setup()
	{
		product = Mockito.mock(ProductModel.class);
		Mockito.when(product.getName()).thenReturn("productName");

		final AmendOrderValidationStrategy amendOrderValidationStrategy = Mockito.mock(AmendOrderValidationStrategy.class);
		final List<AmendOrderValidationStrategy> amendOrderValidationStrategyList = Collections
				.singletonList(amendOrderValidationStrategy);
		defaultNDCProductFacade.setAmendOrderValidationStrategyList(amendOrderValidationStrategyList);
		traveller.setLabel("Label");
	}

	@Test(expected = NDCOrderException.class)
	public void testCheckIfValidProductForTravellersWithAddException() throws NDCOrderException
	{
		final int quantity = 1;

		Mockito
				.when(defaultNDCProductFacade.getAmendOrderValidationStrategyList().stream().findFirst().get()
						.validateAmendOrder(order, product.getCode(), quantity, traveller.getLabel(), transportOfferings, routeCode))
				.thenReturn(false);

		defaultNDCProductFacade.checkIfValidProductForTravellers(order, travellers, product, quantity, transportOfferings,
				routeCode);
	}

	@Test(expected = NDCOrderException.class)
	public void testCheckIfValidProductForTravellersWithRemoveException() throws NDCOrderException
	{
		final int quantity = 0;

		Mockito.when(defaultNDCProductFacade.getAmendOrderValidationStrategyList().stream().findFirst().get()
				.validateAmendOrder(order, product.getCode(), quantity, traveller.getLabel(), transportOfferings, routeCode))
				.thenReturn(false);

		defaultNDCProductFacade.checkIfValidProductForTravellers(order, travellers, product, quantity, transportOfferings,
				routeCode);
	}

	@Test
	public void testCheckIfValidProductForTravellers() throws NDCOrderException
	{
		final int quantity = 1;

		Mockito
				.when(defaultNDCProductFacade.getAmendOrderValidationStrategyList().stream().findFirst().get()
						.validateAmendOrder(order, product.getCode(), quantity, traveller.getLabel(), transportOfferings, routeCode))
				.thenReturn(true);

		defaultNDCProductFacade.checkIfValidProductForTravellers(order, travellers, product, quantity, transportOfferings,
				routeCode);

		Mockito.verify(defaultNDCProductFacade.getAmendOrderValidationStrategyList().stream().findFirst().get(), Mockito.times(1))
				.validateAmendOrder(order, product.getCode(), quantity, traveller.getLabel(), transportOfferings, routeCode);
	}

}
