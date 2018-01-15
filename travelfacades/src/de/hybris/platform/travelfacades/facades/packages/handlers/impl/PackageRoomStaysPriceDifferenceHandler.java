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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates the priceDifference property of the {@link RatePlanData} for each {@link RoomStayData}, calculated as the
 * difference between the roomStayData ActualRate and the ActualRate of the ratePlan of the corresponding
 * {@link ReservedRoomStayData}.
 */
public class PackageRoomStaysPriceDifferenceHandler implements PackageResponseHandler
{
	private static final String PLUS_SIGN = "+";

	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Override
	public void handle(final PackageRequestData packageRequestData, final PackageResponseData packageResponseData)
	{
		if (!packageResponseData.isAvailable())
		{
			return;
		}

		List<ReservedRoomStayData> reservedRoomStays = packageResponseData.getAccommodationPackageResponse()
				.getAccommodationAvailabilityResponse().getReservedRoomStays();

		if (CollectionUtils.isEmpty(reservedRoomStays))
		{
			return;
		}
		reservedRoomStays = reservedRoomStays.stream().filter(reservedRoomStay -> !reservedRoomStay.getNonModifiable())
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(reservedRoomStays))
		{
			return;
		}

		final List<RoomStayData> roomStays = packageResponseData.getAccommodationPackageResponse()
				.getAccommodationAvailabilityResponse().getRoomStays();

		for (final ReservedRoomStayData reservedRoomStay : reservedRoomStays)
		{
			final BigDecimal reservedRoomStayActualRate;
			if (reservedRoomStay.getRatePlans().get(0).getActualRate() != null)
			{
				reservedRoomStayActualRate = reservedRoomStay.getRatePlans().get(0).getActualRate().getValue();
			}
			else
			{
				reservedRoomStayActualRate = reservedRoomStay.getTotalRate().getActualRate().getValue();
			}

			final List<RatePlanData> ratePlans = roomStays.stream()
					.filter(roomStay -> roomStay.getRoomStayRefNumber().equals(reservedRoomStay.getRoomStayRefNumber()))
					.flatMap(roomStay -> roomStay.getRatePlans().stream()).collect(Collectors.toList());

			ratePlans.forEach(ratePlan -> {
				if (ratePlan.getAvailableQuantity() > 0)
				{
					final BigDecimal priceDifference = ratePlan.getActualRate().getValue().subtract(reservedRoomStayActualRate);
					ratePlan.setPriceDifference(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, priceDifference,
							ratePlan.getActualRate().getCurrencyIso()));
					updateFormattedValue(ratePlan.getPriceDifference());
				}
			});
		}
	}

	/**
	 * Updates the formattedValue property of the given {@link PriceData} to display a PLUS SIGN for non negative values
	 *
	 * @param priceData
	 *           as the priceData to update
	 */
	protected void updateFormattedValue(final PriceData priceData)
	{
		if (priceData.getValue().compareTo(BigDecimal.ZERO) >= 0)
		{
			priceData.setFormattedValue(PLUS_SIGN + priceData.getFormattedValue());
		}
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
