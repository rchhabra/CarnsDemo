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

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.RatePlanService;
import de.hybris.platform.travelservices.strategies.CalculateTotalPriceForChangeDatesStrategy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy class to calculate the total amount to be paid for AccommodationAvailabilityResponseData.
 *
 */
public class DefaultCalculateTotalPriceWithGuaranteeForChangeDatesStrategy implements CalculateTotalPriceForChangeDatesStrategy
{

	private BookingService bookingService;

	private RatePlanService ratePlanService;

	private TimeService timeService;


	@Override
	public BigDecimal calculate(final AccommodationAvailabilityResponseData accommodationAvailabilityResponse,
			final String orderCode)
	{

		if (accommodationAvailabilityResponse == null || StringUtils.isEmpty(orderCode)
				|| CollectionUtils.isEmpty(accommodationAvailabilityResponse.getRoomStays()))
		{
			return null;
		}

		final OrderModel orderModel = getBookingService().getOrder(orderCode);

		if (orderModel == null)
		{
			return null;
		}

		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(orderModel);

		if (CollectionUtils.isEmpty(entryGroups))
		{
			return null;
		}

		BigDecimal totalPayablePrice = BigDecimal.valueOf(0);
		final Date startingDate = accommodationAvailabilityResponse.getRoomStays().get(0).getCheckInDate();

		for (final AccommodationOrderEntryGroupModel group : entryGroups)
		{
			final GuaranteeModel guaranteeToApply = getRatePlanService().getGuaranteeToApply(group, startingDate,
					getTimeService().getCurrentTime());

			if (guaranteeToApply == null)
			{
				continue;
			}

			final Optional<RoomStayData> roomStayData = accommodationAvailabilityResponse.getRoomStays().stream()
					.filter(roomStay -> Objects.equals(roomStay.getRoomStayRefNumber(), group.getRoomStayRefNumber())).findFirst();

			if (!roomStayData.isPresent() || !(roomStayData.get() instanceof ReservedRoomStayData))
			{
				return null;
			}
			final ReservedRoomStayData reservedRoomStay = (ReservedRoomStayData) roomStayData.get();

			final BigDecimal totalGroupPrice = reservedRoomStay.getTotalRate().getActualRate().getValue()
					.subtract(getNonRoomRateModelPrice(group));

			final BigDecimal guaranteeAmount = BigDecimal
					.valueOf(getRatePlanService().getAppliedGuaranteeAmount(guaranteeToApply, totalGroupPrice));

			totalPayablePrice = totalPayablePrice.add(guaranteeAmount);
		}

		return totalPayablePrice;
	}

	protected BigDecimal getNonRoomRateModelPrice(final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel)
	{

		final List<AbstractOrderEntryModel> nonRoomRateEntries = accommodationOrderEntryGroupModel.getEntries().stream()
				.filter(entry -> !(entry.getProduct() instanceof RoomRateProductModel)).collect(Collectors.toList());

		Double totalPrice = 0d;
		if (CollectionUtils.isNotEmpty(nonRoomRateEntries))
		{
			for (final AbstractOrderEntryModel nonRoomrateEntry : nonRoomRateEntries)
			{
				totalPrice = Double.sum(totalPrice, nonRoomrateEntry.getTotalPrice());
			}
		}

		return BigDecimal.valueOf(totalPrice);
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the ratePlanService
	 */
	protected RatePlanService getRatePlanService()
	{
		return ratePlanService;
	}

	/**
	 * @param ratePlanService
	 *           the ratePlanService to set
	 */
	@Required
	public void setRatePlanService(final RatePlanService ratePlanService)
	{
		this.ratePlanService = ratePlanService;
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
