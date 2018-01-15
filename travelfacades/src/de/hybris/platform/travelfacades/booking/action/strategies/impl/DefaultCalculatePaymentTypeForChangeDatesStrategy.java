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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelfacades.booking.action.strategies.CalculatePaymentTypeForChangeDatesStrategy;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.CalculateTotalPriceForChangeDatesStrategy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation for {@link= CalculatePaymentTypeForChangeDatesStrategy}
 */
public class DefaultCalculatePaymentTypeForChangeDatesStrategy implements CalculatePaymentTypeForChangeDatesStrategy
{
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private BookingService bookingService;
	private Map<String, CalculateTotalPriceForChangeDatesStrategy> calculateTotalPriceForChangeDatesStrategyMap;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	@Override
	public Map<String, String> calculate(final AccommodationReservationData accommodationReservationData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{

		if (accommodationReservationData == null || accommodationAvailabilityResponse == null
				|| accommodationReservationData.getTotalToPay() == null)
		{
			return Collections.emptyMap();
		}

		final BigDecimal totalPayedPriceFromOrder = accommodationReservationData.getTotalRate().getActualRate().getValue()
				.subtract(accommodationReservationData.getTotalToPay().getValue());
		BigDecimal payableAmount = BigDecimal.valueOf(0);
		String paymentType;
		final BigDecimal totalChangeDatePriceWithoutGurantees = getCalculateTotalPriceForChangeDatesStrategyMap()
				.get("NO_GUARANTEES").calculate(accommodationAvailabilityResponse, StringUtils.EMPTY);
		if (BigDecimal.ZERO.compareTo(totalPayedPriceFromOrder)==0)
		{
			paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME;
		}
		else if (BigDecimal.ZERO.compareTo(accommodationReservationData.getTotalToPay().getValue())==0)
		{
			final BigDecimal diffAmount = totalChangeDatePriceWithoutGurantees.subtract(totalPayedPriceFromOrder);
			if (BigDecimal.ZERO.compareTo(diffAmount)==0)
			{
				paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME;
			}
			else if (diffAmount.compareTo(BigDecimal.ZERO) > 0)
			{
				payableAmount = diffAmount;
				paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE;
			}
			else
			{
				payableAmount = totalPayedPriceFromOrder.subtract(totalChangeDatePriceWithoutGurantees);
				paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND;
			}
		}
		else if (totalPayedPriceFromOrder.compareTo(totalChangeDatePriceWithoutGurantees) == 0)
		{
			final boolean isPartialPayment = totalPayedPriceFromOrder.compareTo(BigDecimal.ZERO) >0;
			if (isPartialPayment)
			{
				final BigDecimal refundAmount = getTotalToRefundForChangeDates(accommodationReservationData.getCode(),
						accommodationAvailabilityResponse);
				if (BigDecimal.ZERO.compareTo(refundAmount)==0)
				{
					paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME;
				}
				else
				{
					payableAmount = refundAmount;
					paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND;
				}
			}
			else
			{
				paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME;
			}
		}
		else if (totalPayedPriceFromOrder.compareTo(totalChangeDatePriceWithoutGurantees) < 0)
		{
			final boolean isPartialPayment = totalPayedPriceFromOrder.compareTo(BigDecimal.ZERO) > 0;
			if (isPartialPayment)
			{
				final BigDecimal totalChangeDatePriceWithGurantees = getCalculateTotalPriceForChangeDatesStrategyMap()
						.get("GUARANTEES").calculate(accommodationAvailabilityResponse, accommodationReservationData.getCode());
				if (totalChangeDatePriceWithGurantees.compareTo(totalPayedPriceFromOrder) > 0)
				{
					payableAmount = totalChangeDatePriceWithGurantees.subtract(totalPayedPriceFromOrder);
					paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE;
				}
				else
				{
					final BigDecimal refundAmount = getTotalToRefundForChangeDates(accommodationReservationData.getCode(),
							accommodationAvailabilityResponse);
					if (BigDecimal.ZERO.compareTo(refundAmount)==0)
					{
						paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME;
					}
					else
					{
						payableAmount = refundAmount;
						paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND;
					}
				}
			}
			else
			{
				payableAmount = totalChangeDatePriceWithoutGurantees.subtract(totalPayedPriceFromOrder);
				paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE;
			}
		}
		else
		{
			payableAmount = getTotalToRefundForChangeDates(accommodationReservationData.getCode(),
					accommodationAvailabilityResponse);
			paymentType = TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND;
		}

		if (!StringUtils.equals(TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND, paymentType))
		{
			final BigDecimal previousCartTotal = accommodationReservationData.getTotalRate().getActualRate().getValue();
			if (totalChangeDatePriceWithoutGurantees.compareTo(previousCartTotal) > 0
					|| totalChangeDatePriceWithoutGurantees.compareTo(totalPayedPriceFromOrder) > 0)
			{
				payableAmount = totalChangeDatePriceWithoutGurantees.subtract(totalPayedPriceFromOrder);
			}
			else
			{
				payableAmount = BigDecimal.ZERO;
			}
		}

		final String currencyIso = accommodationReservationData.getTotalRate().getActualRate().getCurrencyIso();
		final Map<String, String> changeDatePaymentResults = new HashMap<>(3);
		changeDatePaymentResults.put(TravelservicesConstants.BOOKING_AMOUNT_PAID, getTravelCommercePriceFacade()
				.createPriceData(PriceDataType.BUY, totalPayedPriceFromOrder, currencyIso).getFormattedValue());
		changeDatePaymentResults.put(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE,
				getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, payableAmount, currencyIso).getFormattedValue());
		changeDatePaymentResults.put(TravelservicesConstants.BOOKING_PAYABLE_STATUS, paymentType);
		return changeDatePaymentResults;

	}

	protected BigDecimal getTotalToRefundForChangeDates(final String orderCode,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{
		final OrderModel orderModel = getBookingService().getOrder(orderCode);
		if (orderModel == null)
		{
			return null;
		}

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels = getBookingService()
				.getAccommodationOrderEntryGroups(orderModel);
		BigDecimal refundAmount = BigDecimal.valueOf(0);
		for (final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel : accommodationOrderEntryGroupModels)
		{
			final Optional<RoomStayData> optionalRoomStayData = accommodationAvailabilityResponse.getRoomStays().stream()
					.filter(roomStay -> Objects
							.equals(roomStay.getRoomStayRefNumber(), accommodationOrderEntryGroupModel.getRoomStayRefNumber()))
					.findFirst();
			if (!optionalRoomStayData.isPresent() || !(optionalRoomStayData.get() instanceof ReservedRoomStayData))
			{
				return null;
			}
			final RoomStayData roomStayData = optionalRoomStayData.get();
			final BigDecimal pricePaidForGroup = getBookingService().getOrderTotalPaidByEntryGroup(orderModel,
					accommodationOrderEntryGroupModel);
			final ReservedRoomStayData reservedRoomStay = (ReservedRoomStayData) roomStayData;
			final String paymentType = reservedRoomStay.getTotalRate().getActualRate().getValue().compareTo(pricePaidForGroup) > 0
					? TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_SAME
					: TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND;
			final BigDecimal amountDiff = pricePaidForGroup.subtract(reservedRoomStay.getTotalRate().getActualRate().getValue());
			if (StringUtils.equals(paymentType, TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND))
			{
				refundAmount = refundAmount.add(amountDiff);
			}
		}

		return refundAmount;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
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
	 * @return the calculateTotalPriceForChangeDatesStrategyMap
	 */
	protected Map<String, CalculateTotalPriceForChangeDatesStrategy> getCalculateTotalPriceForChangeDatesStrategyMap()
	{
		return calculateTotalPriceForChangeDatesStrategyMap;
	}

	/**
	 * @param calculateTotalPriceForChangeDatesStrategyMap
	 *           the calculateTotalPriceForChangeDatesStrategyMap to set
	 */
	@Required
	public void setCalculateTotalPriceForChangeDatesStrategyMap(
			final Map<String, CalculateTotalPriceForChangeDatesStrategy> calculateTotalPriceForChangeDatesStrategyMap)
	{
		this.calculateTotalPriceForChangeDatesStrategyMap = calculateTotalPriceForChangeDatesStrategyMap;
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
