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
package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.order.NDCProductFacade;
import de.hybris.platform.ndcfacades.strategies.AmendOrderValidationStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCProductFacade}
 */
public class DefaultNDCProductFacade implements NDCProductFacade
{
	private List<AmendOrderValidationStrategy> amendOrderValidationStrategyList;

	@Override
	public void checkIfProductCanBeAddedToTraveller(final OrderModel order, final TravellerModel traveller,
			final ProductModel product, final int quantity, final List<String> transportOfferings, final String routeCode)
			throws NDCOrderException
	{
		checkIfProductCanBeAddedToTraveller(order, traveller.getLabel(), product, quantity, transportOfferings, routeCode);
	}

	@Override
	public void checkIfProductCanBeAddedToTraveller(final OrderModel order, final String travellerCode,
			final ProductModel product, final int quantity, final List<String> transportOfferings, final String routeCode)
			throws NDCOrderException
	{
		for (final AmendOrderValidationStrategy strategy : getAmendOrderValidationStrategyList())
		{
			if (!strategy
					.validateAmendOrder(order, product.getCode(), quantity, travellerCode, transportOfferings, routeCode))
			{
				if (quantity > 0)
				{
					throw new NDCOrderException(product.getName() + " cannot be added to the order");
				}
				else
				{
					throw new NDCOrderException(product.getName() + " cannot be removed from the order");
				}
			}
		}
	}

	@Override
	public void checkIfValidProductForTravellers(final OrderModel order, final List<TravellerModel> travellers,
			final ProductModel product, final int quantity, final List<String> transportOfferings, final String routeCode)
			throws NDCOrderException
	{
		for (final TravellerModel traveller : travellers)
		{
			checkIfProductCanBeAddedToTraveller(order, traveller, product, quantity, transportOfferings, routeCode);
		}
	}

	/**
	 * Gets amend order validation strategy list.
	 *
	 * @return the amend order validation strategy list
	 */
	protected List<AmendOrderValidationStrategy> getAmendOrderValidationStrategyList()
	{
		return amendOrderValidationStrategyList;
	}

	/**
	 * Sets amend order validation strategy list.
	 *
	 * @param amendOrderValidationStrategyList
	 * 		the amend order validation strategy list
	 */
	@Required
	public void setAmendOrderValidationStrategyList(
			final List<AmendOrderValidationStrategy> amendOrderValidationStrategyList)
	{
		this.amendOrderValidationStrategyList = amendOrderValidationStrategyList;
	}
}
