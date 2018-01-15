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

package de.hybris.platform.travelfacades.facades.accommodation.strategies;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract Strategy implementing {@link AccommodationRestrictionExtrasStrategy}
 */
public abstract class AbstractRestrictionExtrasStrategy implements AccommodationRestrictionExtrasStrategy
{

	private AccommodationOfferingService accommodationOfferingService;
	private TravelCommerceStockService commerceStockService;

	/**
	 * Returns the available quantity of the product, considering both the quantity in the reservationData and the
	 * stockLevel.
	 *
	 * @param productModel
	 * 		the product model
	 * @param reservedRoomStayData
	 * 		the reserved room stay data
	 * @param accommodationReservationData
	 * 		the accommodation reservation data
	 * @return the available quantity of the product
	 */
	protected int getServiceAvailableQuantity(final ProductModel productModel, final ReservedRoomStayData reservedRoomStayData,
			final AccommodationReservationData accommodationReservationData)
	{
		final AccommodationOfferingModel accommodationOfferingModel = getAccommodationOfferingService()
				.getAccommodationOffering(accommodationReservationData.getAccommodationReference().getAccommodationOfferingCode());

		Date date = reservedRoomStayData.getCheckInDate();

		final int reservedQuantity = getQuantityForReservedService(productModel.getCode(), reservedRoomStayData,
				accommodationReservationData);

		final List<Integer> availableQuantities = new ArrayList<>();

		while (!TravelDateUtils.isSameDate(date, reservedRoomStayData.getCheckOutDate()))
		{
			final Integer stockLevel = getCommerceStockService().getStockForDate(productModel, date,
					Arrays.asList(accommodationOfferingModel));
			availableQuantities.add(Integer.sum(stockLevel.intValue(), reservedQuantity));

			date = TravelDateUtils.addDays(date, 1);
		}
		return availableQuantities.stream().mapToInt(Integer::intValue).min().getAsInt();
	}


	/**
	 * Get the quantity of the given productCode in the reservationData
	 *
	 * @param productCode
	 * 		the product code
	 * @param reservedRoomStayData
	 * 		the reserved room stay data
	 * @param accommodationReservationData
	 * 		the accommodation reservation data
	 * @return the quantity of the given productCode in the reservationData
	 */
	protected int getQuantityForReservedService(final String productCode, final ReservedRoomStayData reservedRoomStayData,
			final AccommodationReservationData accommodationReservationData)
	{
		final Optional<ReservedRoomStayData> optional = accommodationReservationData.getRoomStays().stream()
				.filter(roomStay -> roomStay.getRoomStayRefNumber().equals(reservedRoomStayData.getRoomStayRefNumber())).findFirst();
		int quantity = 0;

		if (optional.isPresent())
		{
			quantity = optional.get().getServices().stream().filter(service -> StringUtils.equals(service.getCode(), productCode))
					.mapToInt(service -> service.getQuantity()).sum();
		}

		return quantity;
	}

	/**
	 * Gets accommodation offering service.
	 *
	 * @return the accommodationOfferingService
	 */
	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	/**
	 * Sets accommodation offering service.
	 *
	 * @param accommodationOfferingService
	 * 		the accommodationOfferingService to set
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}


	/**
	 * Gets commerce stock service.
	 *
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * Sets commerce stock service.
	 *
	 * @param commerceStockService
	 * 		the commerceStockService to set
	 */
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

}
