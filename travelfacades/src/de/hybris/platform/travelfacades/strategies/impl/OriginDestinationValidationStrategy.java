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
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy that extends the {@link AbstractAddBundleToCartValidationStrategy}.
 * The strategy is used to validate the addBundleToCart, that will be valid only if the origin location corresponding to the
 * {@link de.hybris.platform.travelservices.model.travel.TravelRouteModel} specified in the {@link AddBundleToCartRequestData} is
 * equals to the destination location of the entry with the previous originDestinationRefNumber.
 */
public class OriginDestinationValidationStrategy extends AbstractAddBundleToCartValidationStrategy
{
	public static final String ADD_BUNDLE_TO_CART_VALIDATION_ERROR_ORIGIN_DESTINATION = "add.bundle.to.cart.validation.error"
			+ ".origin.destination";

	private CartService cartService;
	private TravelRouteService travelRouteService;

	@Override
	public AddToCartResponseData validate(final AddBundleToCartRequestData addBundleToCartRequestData)
	{
		final Optional<Integer> odRefNumber = addBundleToCartRequestData.getAddBundleToCartData().stream()
				.map(AddBundleToCartData::getOriginDestinationRefNumber).distinct().findFirst();

		if (odRefNumber.isPresent() && !odRefNumber.get().equals(TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER))
		{
			if (!getCartService().hasSessionCart())
			{
				return createAddToCartResponse(false, ADD_BUNDLE_TO_CART_VALIDATION_ERROR_NO_SESSION_CART, null);
			}

			final Integer previousOdRefNumber = Integer.sum(odRefNumber.get(), -1);
			final Optional<AbstractOrderEntryModel> previousEntry = getCartService().getSessionCart().getEntries().stream().filter(
					entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
							&& (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
									|| entry.getProduct() instanceof FareProductModel)
							&& entry.getActive()
							&& previousOdRefNumber
							.equals(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber())).findAny();

			if (previousEntry.isPresent())
			{
				final String destinationLocation = previousEntry.get().getTravelOrderEntryInfo().getTravelRoute().getDestination()
						.getLocation().getCode();

				final String originLocation = getTravelRouteService()
						.getTravelRoute(addBundleToCartRequestData.getAddBundleToCartData().get(0).getTravelRouteCode()).getOrigin()
						.getLocation().getCode();

				if (!destinationLocation.equals(originLocation))
				{
					return createAddToCartResponse(false, ADD_BUNDLE_TO_CART_VALIDATION_ERROR_ORIGIN_DESTINATION, null);
				}

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

	/**
	 * @return the travelRouteService
	 */
	protected TravelRouteService getTravelRouteService()
	{
		return travelRouteService;
	}

	/**
	 * @param travelRouteService
	 * 		the travelRouteService to set
	 */
	@Required
	public void setTravelRouteService(final TravelRouteService travelRouteService)
	{
		this.travelRouteService = travelRouteService;
	}
}
