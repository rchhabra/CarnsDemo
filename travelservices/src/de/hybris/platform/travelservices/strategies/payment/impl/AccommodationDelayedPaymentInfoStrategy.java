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

package de.hybris.platform.travelservices.strategies.payment.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.GuaranteeType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.RatePlanService;
import de.hybris.platform.travelservices.strategies.payment.EntryTypePaymentInfoCreationStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class AccommodationDelayedPaymentInfoStrategy extends AbstractPaymentInfoStrategy
		implements EntryTypePaymentInfoCreationStrategy
{
	private BookingService bookingService;
	private RatePlanService ratePlanService;
	private TimeService timeService;

	@Override
	public List<EntryTypePaymentInfo> create(final AbstractOrderModel abstractOrder)
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(abstractOrder);
		if (CollectionUtils.isEmpty(entryGroups))
		{
			return Collections.emptyList();
		}
		final List<EntryTypePaymentInfo> paymentInfos = new ArrayList<>();
		entryGroups.forEach(group -> {
			final EntryTypePaymentInfo paymentInfo = new EntryTypePaymentInfo();
			paymentInfo.setEntryType(OrderEntryType.ACCOMMODATION);
			paymentInfo.setBookingTimeAmount(0.0d);
			paymentInfo.setPrePaymentRequested(0.0d);
			paymentInfo.setCheckInPayOff(0.0d);
			paymentInfo.setEntries(group.getEntries());
			final RatePlanModel ratePlan = group.getRatePlan();
			if (!CollectionUtils.isEmpty(ratePlan.getGuarantee()))
			{
				final List<GuaranteeModel> guarantees = new ArrayList<>(ratePlan.getGuarantee());
				setAmounts(guarantees, group, paymentInfo, abstractOrder);
			}
			paymentInfo.setCheckInPayOff(getEntriesAmount(group.getEntries()) - paymentInfo.getBookingTimeAmount()
					- getBookingService().getOrderTotalPaidByEntryGroup(abstractOrder, group).doubleValue()
					- paymentInfo.getPrePaymentRequested());
			paymentInfos.add(paymentInfo);
		});

		return paymentInfos;
	}


	/**
	 * Calculates amounts to be populated against the EntryTypePaymentInfo. Is the group is being amended, an amended
	 * total to pay is calculated then the booking amount is calculated as difference between this value and the total
	 * already paid. In the normal order journey calculating the guarantee amount is enough. After calculating the
	 * booking time amount, if the applied guarantee is not a prepayment, a search is performed along rate plan's
	 * guarantees and if a valid prepayment is found it's eventually used to populate the prepayment amount along with
	 * the relative deadline.
	 *
	 * @param guarantees
	 * @param group
	 * @param paymentInfo
	 * @param abstractOrder
	 */
	protected void setAmounts(final List<GuaranteeModel> guarantees, final AccommodationOrderEntryGroupModel group,
			final EntryTypePaymentInfo paymentInfo, final AbstractOrderModel abstractOrder)
	{
		final BigDecimal currentValue = BigDecimal.valueOf(paymentInfo.getBookingTimeAmount());
		BigDecimal appliedGuaranteeAmount;
		final GuaranteeModel guaranteeToApply = getRatePlanService().getGuaranteeToApply(group, getTimeService().getCurrentTime());
		if (isGroupBeingAmended(group, abstractOrder))
		{
			appliedGuaranteeAmount = BigDecimal.valueOf(getTotalToPay(guaranteeToApply, group, abstractOrder));
			final BigDecimal totalPaidForGroup = getBookingService().getOrderTotalPaidByEntryGroup(abstractOrder, group);
			paymentInfo.setBookingTimeAmount(appliedGuaranteeAmount.doubleValue() > totalPaidForGroup.doubleValue()
					? currentValue.add(appliedGuaranteeAmount.subtract(totalPaidForGroup)).doubleValue()
					: currentValue.subtract(totalPaidForGroup.subtract(appliedGuaranteeAmount)).doubleValue());
		}
		else
		{
			appliedGuaranteeAmount = BigDecimal.valueOf(getTotalToPay(guaranteeToApply, group, abstractOrder));
			paymentInfo.setBookingTimeAmount(
					isGroupWithEntriesOfSameAmendStatus(group) ? 0d : currentValue.add(appliedGuaranteeAmount).doubleValue());
		}
		if (!GuaranteeType.PREPAYMENT.equals(guaranteeToApply.getType()))
		{
			handlePrePayment(guarantees, paymentInfo, group, appliedGuaranteeAmount.doubleValue(), abstractOrder);
		}
	}

	/**
	 * Calculates total to pay at booking time, applying guarantee to the rate plan total.
	 *
	 * @param guaranteeToApply
	 * @param group
	 * @param abstractOrder
	 * @return
	 */
	protected Double getTotalToPay(final GuaranteeModel guaranteeToApply, final AccommodationOrderEntryGroupModel group,
			final AbstractOrderModel abstractOrder)
	{
		return Objects.nonNull(guaranteeToApply.getFixedAmount()) ? guaranteeToApply.getFixedAmount()
				: getGuaranteeAmount(guaranteeToApply.getPercentageAmount(), getRoomRatesTotalPrice(group, abstractOrder));
	}


	/**
	 * Calculates prepayment value, if needed
	 *
	 * @param guarantees
	 * @param paymentInfo
	 * @param group
	 * @param appliedGuaranteeAmount
	 * @param abstractOrder
	 */
	protected void handlePrePayment(final List<GuaranteeModel> guarantees, final EntryTypePaymentInfo paymentInfo,
			final AccommodationOrderEntryGroupModel group, final Double appliedGuaranteeAmount,
			final AbstractOrderModel abstractOrder)
	{
		final Optional<GuaranteeModel> optionalGuarantee = guarantees.stream()
				.filter(guarantee -> GuaranteeType.PREPAYMENT.equals(guarantee.getType())).findFirst();
		if (optionalGuarantee.isPresent())
		{
			final double totalPaidByGroup = getBookingService().getOrderTotalPaidByEntryGroup(abstractOrder, group).doubleValue();
			final double guaranteeAmountPaidInAdvance = totalPaidByGroup > appliedGuaranteeAmount
					? totalPaidByGroup - appliedGuaranteeAmount : 0d;
			paymentInfo.setPrePaymentRequested(Double.sum(paymentInfo.getPrePaymentRequested(),
					getRoomRatesTotalPrice(group, abstractOrder) - appliedGuaranteeAmount - guaranteeAmountPaidInAdvance));
			final Long prePaymentDeadline = paymentInfo.getPrePaymentDeadline();
			if (Objects.isNull(prePaymentDeadline) || prePaymentDeadline < optionalGuarantee.get().getRelativeDeadline())
			{
				paymentInfo.setPrePaymentDeadline(optionalGuarantee.get().getRelativeDeadline());
			}
		}

	}

	protected boolean isGroupWithEntriesOfSameAmendStatus(final AccommodationOrderEntryGroupModel group)
	{
		return group.getEntries().stream().anyMatch(entry -> entry.getProduct() instanceof RoomRateProductModel && entry.getActive()
				&& AmendStatus.SAME.equals(entry.getAmendStatus()));
	}

	protected Double getGuaranteeAmount(final Double amountPercent, final Double totalPrice)
	{
		return Objects.nonNull(amountPercent) ? totalPrice * amountPercent / 100 : 0.0d;
	}

	private Double getRoomRatesTotalPrice(final AccommodationOrderEntryGroupModel group, final AbstractOrderModel abstractOrder)
	{
		return getBookingTimeAmount(group.getEntries().stream().filter(entry -> entry.getProduct() instanceof RoomRateProductModel)
				.collect(Collectors.toList()), abstractOrder);
	}

	/**
	 * Determines if the group is being amended. A group in considered amended if an original order exists (that is the
	 * order is being amended), among all the groups associated with the original order there is a group sharing the same
	 * room stay reference number with the current group, and the current group has at least one entry in CHANGED or NEW
	 * status.
	 *
	 * @param group
	 * @param abstractOrder
	 * @return
	 */
	protected boolean isGroupBeingAmended(final AccommodationOrderEntryGroupModel group, final AbstractOrderModel abstractOrder)
	{
		final boolean isAmendedCart = Objects.nonNull(abstractOrder.getOriginalOrder());
		return isAmendedCart
				&& getBookingService().getAccommodationOrderEntryGroups(abstractOrder.getOriginalOrder()).stream()
						.map(AccommodationOrderEntryGroupModel::getRoomStayRefNumber).collect(Collectors.toList())
						.contains(group.getRoomStayRefNumber())
				&& group.getEntries().stream()
						.anyMatch(entry -> Arrays.asList(AmendStatus.CHANGED, AmendStatus.NEW).contains(entry.getAmendStatus()));
	}


	/**
	 *
	 *
	 *
	 * @return bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 *
	 * @param bookingService
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}


	/**
	 *
	 * @return ratePlanService
	 */
	protected RatePlanService getRatePlanService()
	{
		return ratePlanService;
	}


	/**
	 *
	 * @param ratePlanService
	 *           the ratePlanService to set
	 */
	@Required
	public void setRatePlanService(final RatePlanService ratePlanService)
	{
		this.ratePlanService = ratePlanService;
	}


	/**
	 *
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 *
	 * @param timeService
	 *           timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

}
