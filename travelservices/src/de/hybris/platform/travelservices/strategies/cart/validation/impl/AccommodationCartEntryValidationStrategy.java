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
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.order.AccommodationCommerceCartService;
import de.hybris.platform.travelservices.strategies.cart.validation.AbstractCartEntryValidationStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Accommodation cart entry validation strategy.
 */
public class AccommodationCartEntryValidationStrategy extends AbstractCartEntryValidationStrategy
{
	private AccommodationCommerceCartService accommodationCommerceCartService;

	@Override
	protected long getCartLevel(final CartEntryModel cartEntryModel, final CartModel cartModel)
	{
		Long cartLevel = 0L;
		if (AmendStatus.SAME.equals(cartEntryModel.getAmendStatus()))
		{
			return cartLevel;
		}
		final ProductModel product = cartEntryModel.getProduct();
		final Map<AmendStatus, List<CartEntryModel>> matchingEntries = getAccommodationCommerceCartService()
				.getEntriesForProductAndAccommodation(cartModel, product, cartEntryModel).stream()
				.collect(Collectors.groupingBy(CartEntryModel::getAmendStatus));
		final List<CartEntryModel> newEntries = matchingEntries.get(AmendStatus.NEW);
		if (CollectionUtils.isNotEmpty(newEntries))
		{
			cartLevel = Long.sum(cartLevel,
					product instanceof RoomRateProductModel ? CollectionUtils.size(newEntries)
							: newEntries.stream().mapToLong(CartEntryModel::getQuantity).sum());
		}
		final List<CartEntryModel> changedEntries = matchingEntries.get(AmendStatus.CHANGED);
		if (CollectionUtils.isNotEmpty(changedEntries))
		{
			cartLevel = cartLevel - (product instanceof RoomRateProductModel
					? cartModel.getOriginalOrder().getEntries().stream().filter(entry -> entry.getProduct().equals(product)).count()
					: cartModel.getOriginalOrder().getEntries().stream().filter(entry -> entry.getProduct().equals(product))
					.mapToLong(AbstractOrderEntryModel::getQuantity).sum());
		}
		return cartLevel;
	}



	/**
	 * Gets accommodation commerce cart service.
	 *
	 * @return the accommodation commerce cart service
	 */
	protected AccommodationCommerceCartService getAccommodationCommerceCartService()
	{
		return accommodationCommerceCartService;
	}

	/**
	 * Sets accommodation commerce cart service.
	 *
	 * @param accommodationCommerceCartService
	 * 		the accommodation commerce cart service
	 */
	@Required
	public void setAccommodationCommerceCartService(final AccommodationCommerceCartService accommodationCommerceCartService)
	{
		this.accommodationCommerceCartService = accommodationCommerceCartService;
	}
}
