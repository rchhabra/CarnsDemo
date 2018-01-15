/*
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.AddBundleToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.strategies.AbstractAddBundleToCartValidationStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy that extends the {@link AbstractAddBundleToCartValidationStrategy}. The strategy is used to validate the
 * addBundleToCart, that will be valid only if the departure time of the first transport offerings specified in the
 * {@link AddBundleToCartRequestData} is after the arrival time of the last transport offering of the previous leg of
 * the journey and its arrival time is after the departure time of the first transport offering of the next leg of the journey.
 */
public class TransportOfferingsTimesValidationStrategy extends AbstractAddBundleToCartValidationStrategy
{
	public static final String ADD_BUNDLE_TO_CART_VALIDATION_ERROR_DEPARTURE_ARRIVAL = "add.bundle.to.cart.validation.error"
			+ ".departure.arrival";
	public static final String ADD_BUNDLE_TO_CART_VALIDATION_ERROR_NO_ODREFNUMBER = "add.bundle.to.cart.validation.error.no"
			+ ".odrefnumber";

	private CartService cartService;
	private TransportOfferingService transportOfferingService;

	@Override
	public AddToCartResponseData validate(final AddBundleToCartRequestData addBundleToCartRequestData)
	{
		final Optional<Integer> optOdRefNumber = addBundleToCartRequestData.getAddBundleToCartData().stream()
				.map(AddBundleToCartData::getOriginDestinationRefNumber).distinct().findFirst();

		if (!getCartService().hasSessionCart())
		{
			return createAddToCartResponse(false, ADD_BUNDLE_TO_CART_VALIDATION_ERROR_NO_SESSION_CART, null);
		}

		if (optOdRefNumber.isPresent())
		{
			final List<AbstractOrderEntryModel> transportOrderEntries = getCartService().getSessionCart().getEntries().stream()
					.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
							&& (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
									|| entry.getProduct() instanceof FareProductModel)
							&& entry.getActive())
					.collect(Collectors.toList());

			final TransportOfferingModel currentTransportOffering = addBundleToCartRequestData.getAddBundleToCartData()
					.get(0).getTransportOfferings().stream()
					.map(toCode -> getTransportOfferingService().getTransportOffering(toCode))
					.sorted(Comparator.comparing(this::getUTCDepartureTime)).findFirst().get();

			final Integer odRefNumber = optOdRefNumber.get();
			if (odRefNumber > 0)
			{
				final Integer previousOdRefNumber = Integer.sum(odRefNumber, -1);
				final Optional<AbstractOrderEntryModel> previousEntry = transportOrderEntries.stream()
						.filter(entry -> previousOdRefNumber.equals(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()))
						.findAny();
				if (previousEntry.isPresent())
				{
					final List<TransportOfferingModel> transportOfferings = new ArrayList<>(
							previousEntry.get().getTravelOrderEntryInfo().getTransportOfferings());
					final Optional<TransportOfferingModel> previousTransportOffering = transportOfferings.stream()
							.sorted((to1, to2) -> getUTCDepartureTime(to2).compareTo(getUTCDepartureTime(to1))).findFirst();

					if (previousTransportOffering.isPresent() && previousTransportOffering.get().getArrivalTime()
							.after(currentTransportOffering.getDepartureTime()))
					{
						return createAddToCartResponse(false, ADD_BUNDLE_TO_CART_VALIDATION_ERROR_DEPARTURE_ARRIVAL, null);
					}
				}
			}

			final Integer nextOdRefNumber = Integer.sum(odRefNumber, 1);
			final Optional<AbstractOrderEntryModel> nextEntry = transportOrderEntries.stream()
					.filter(entry -> nextOdRefNumber.equals(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()))
					.findAny();
			if (nextEntry.isPresent())
			{
				final List<TransportOfferingModel> transportOfferings = new ArrayList<>(
						nextEntry.get().getTravelOrderEntryInfo().getTransportOfferings());
				final Optional<TransportOfferingModel> nextTransportOffering = transportOfferings.stream()
						.sorted(Comparator.comparing(this::getUTCDepartureTime)).findFirst();

				if (nextTransportOffering.isPresent() && nextTransportOffering.get().getDepartureTime()
						.before(currentTransportOffering.getArrivalTime()))
				{
					return createAddToCartResponse(false, ADD_BUNDLE_TO_CART_VALIDATION_ERROR_DEPARTURE_ARRIVAL, null);
				}
			}
			return createAddToCartResponse(true, null, null);
		}
		else
		{
			return createAddToCartResponse(false, ADD_BUNDLE_TO_CART_VALIDATION_ERROR_NO_ODREFNUMBER, null);
		}
	}

	/**
	 * Return the UTC departure time for the given transportOffering
	 *
	 * @param transportOfferingModel
	 * 		as the tranportOffering
	 *
	 * @return the zoned date time
	 */
	protected ZonedDateTime getUTCDepartureTime(final TransportOfferingModel transportOfferingModel)
	{
		final String zoneId = transportOfferingModel.getTravelSector().getOrigin().getPointOfService().get(0).getTimeZoneId();
		return TravelDateUtils.getUtcZonedDateTime(transportOfferingModel.getDepartureTime(), ZoneId.of(zoneId));
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
	 * @return the transportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * @param transportOfferingService
	 * 		the transportOfferingService to set
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}
}
