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

import de.hybris.platform.commercefacades.travel.AddBundleToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.strategies.AbstractAddBundleToCartValidationStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy that extends the {@link AbstractAddBundleToCartValidationStrategy}.
 * The strategy is used to validate the addBundleToCart, that is possible only if in the cart there is at least one entry for each
 * originDestinationRefNumber less than the one specified in the {@link AddBundleToCartRequestData}.
 */
public class OriginDestinationRefNumberValidationStrategy extends AbstractAddBundleToCartValidationStrategy
{
	public static final String ADD_BUNDLE_TO_CART_VALIDATION_ERROR_NO_PREVIOUS_ENTRIES = "add.bundle.to.cart.validation.error.no"
			+ ".previous.entries";

	private CartService cartService;

	@Override
	public AddToCartResponseData validate(final AddBundleToCartRequestData addBundleToCartRequestData)
	{
		final Optional<Integer> odRefNumber = addBundleToCartRequestData.getAddBundleToCartData().stream()
				.map(AddBundleToCartData::getOriginDestinationRefNumber).distinct().findFirst();

		if (odRefNumber.isPresent() && !odRefNumber.get().equals(TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER))
		{
			if (!getCartService().hasSessionCart())
			{
				return createAddToCartResponse(false, ADD_BUNDLE_TO_CART_VALIDATION_ERROR_NO_PREVIOUS_ENTRIES, null);
			}
			final List<AbstractOrderEntryModel> orderEntryList = getCartService().getSessionCart().getEntries().stream().filter(
					entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
							&& (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
									|| entry.getProduct() instanceof FareProductModel)
							&& entry.getActive())
					.collect(Collectors.toList());

			final boolean allPreviousEntriesExist = IntStream.range(0, odRefNumber.get()).allMatch(i -> orderEntryList.stream()
					.anyMatch(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == i));
			if (!allPreviousEntriesExist)
			{
				return createAddToCartResponse(false, ADD_BUNDLE_TO_CART_VALIDATION_ERROR_NO_PREVIOUS_ENTRIES, null);
			}
		}
		return createAddToCartResponse(true, null, null);
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 * 		the cartService to set
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}
