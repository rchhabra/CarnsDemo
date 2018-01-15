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
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.payment.EntryTypePaymentInfoCreationStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Create PaymentInfo for Accommodation entries in a pay in advance scenario
 */
public class AccommodationPayInAdvancePaymentInfoStrategy extends AbstractPaymentInfoStrategy
		implements EntryTypePaymentInfoCreationStrategy
{
	private BookingService bookingService;

	@Override
	public List<EntryTypePaymentInfo> create(final AbstractOrderModel abstractOrder)
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(abstractOrder);
		if (CollectionUtils.isEmpty(entryGroups)
				|| abstractOrder.getEntries().stream().filter(entry -> OrderEntryType.ACCOMMODATION.equals(entry.getType()))
						.allMatch(entry -> AmendStatus.SAME.equals(entry.getAmendStatus())))
		{
			return Collections.emptyList();
		}
		final boolean isAmendedCart = Objects.nonNull(abstractOrder.getOriginalOrder());
		final List<EntryTypePaymentInfo> paymentInfos = new ArrayList<>();
		entryGroups.forEach(group -> {
			final EntryTypePaymentInfo paymentInfo = new EntryTypePaymentInfo();
			paymentInfo.setEntryType(OrderEntryType.ACCOMMODATION);
			paymentInfo.setBookingTimeAmount(isAmendedCart ? getAmendedTotalToPay(abstractOrder, group)
					: getBookingTimeAmount(group.getEntries(), abstractOrder));
			paymentInfo.setPrePaymentRequested(0.0d);
			paymentInfo.setCheckInPayOff(0.0d);
			paymentInfo.setEntries(group.getEntries());
			paymentInfos.add(paymentInfo);
		});

		return paymentInfos;
	}

	protected Double getAmendedTotalToPay(final AbstractOrderModel abstractOrder, final AbstractOrderEntryGroupModel entryGroup)
	{
		final BigDecimal totalToPay = BigDecimal.valueOf(getEntriesAmount(entryGroup.getEntries()));
		return totalToPay.subtract(getBookingService().getOrderTotalPaidByEntryGroup(abstractOrder.getOriginalOrder(), entryGroup))
				.doubleValue();
	}

	/**
	 *
	 * @return the bookingService
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
}