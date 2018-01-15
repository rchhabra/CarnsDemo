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

package de.hybris.platform.travelfacades.accommodation.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.travelfacades.process.email.context.AbstractBookingEmailContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/**
 * The class is responsible for populating the Booking Cancellation Email Context.
 */
public class BookingCancelledEmailContext extends AbstractBookingEmailContext
{
	private AccommodationReservationData accommodationReservationData;
	private String totalToRefund;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		OrderModel order = orderProcessModel.getOrder();

		final BigDecimal refundAmount = getBookingService().getTotalToRefund(order);
		final PriceData formattedPrice = getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, refundAmount,
				order.getCurrency());
		if (null != refundAmount && refundAmount.compareTo(BigDecimal.ZERO) > 0)
		{
			totalToRefund = formattedPrice.getFormattedValue();
		}

		final Optional<OrderHistoryEntryModel> optionalOrderHistoryEntry = order.getHistoryEntries().stream()
				.filter(orderHistoryEntry -> orderHistoryEntry.getPreviousOrderVersion() != null).reduce((first, second) -> second);
		if (optionalOrderHistoryEntry.isPresent())
		{
			order = optionalOrderHistoryEntry.get().getPreviousOrderVersion();
		}
		accommodationReservationData = getReservationFacade().getAccommodationReservationData(order);
	}

	@Override
	protected Set<String> getAdditionalEmails(final OrderProcessModel orderProcessModel)
	{
		final Set<String> emails = new HashSet<>();
		initGuestsEmails(orderProcessModel, emails);
		return emails;
	}

	/**
	 * @return the accommodationReservationData
	 */
	public AccommodationReservationData getAccommodationReservationData()
	{
		return accommodationReservationData;
	}


	/**
	 * @return the totalToRefund
	 */
	public String getTotalToRefund()
	{
		return totalToRefund;
	}

}
