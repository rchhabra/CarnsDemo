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

package de.hybris.platform.travelservices.strategies.cart.validation.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.strategies.cart.validation.AbstractCartEntryValidationStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Strategy to validate order entries of default OrderEntryType in a cart before order is placed.
 */
public class DefaultCartEntryValidationStrategy extends AbstractCartEntryValidationStrategy
{

	@Override
	protected long getCartLevel(final CartEntryModel cartEntryModel, final CartModel cartModel)
	{
		Long cartLevel = 0L;
		if (AmendStatus.SAME.equals(cartEntryModel.getAmendStatus()))
		{
			return cartLevel;
		}
		final ProductModel product = cartEntryModel.getProduct();
		final Map<AmendStatus, List<CartEntryModel>> matchingEntries = getCartService().getEntriesForProduct(cartModel, product)
				.stream().collect(Collectors.groupingBy(CartEntryModel::getAmendStatus));
		final List<CartEntryModel> newEntries = matchingEntries.get(AmendStatus.NEW);
		if (CollectionUtils.isNotEmpty(newEntries))
		{
			cartLevel = Long.sum(cartLevel, newEntries.stream().mapToLong(CartEntryModel::getQuantity).sum());
		}
		final List<CartEntryModel> changedEntries = matchingEntries.get(AmendStatus.CHANGED);
		if (CollectionUtils.isNotEmpty(changedEntries))
		{
			cartLevel = cartLevel - cartModel.getOriginalOrder().getEntries().stream()
					.filter(entry -> entry.getProduct().equals(product)).mapToLong(AbstractOrderEntryModel::getQuantity).sum();
		}
		return cartLevel;
	}



}
