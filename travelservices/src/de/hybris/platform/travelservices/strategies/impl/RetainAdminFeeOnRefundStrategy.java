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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.RefundActionStrategy;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation for {@link RefundActionStrategy}. This strategy retains the admin fee from the total refund amount.
 */
public class RetainAdminFeeOnRefundStrategy implements RefundActionStrategy
{
	private BookingService bookingService;

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param order
	 *           the order
	 * @return
	 */
	@Override
	@Deprecated
	public double applyStrategy(final OrderModel order)
	{
		return applyStrategy(order, OrderEntryType.TRANSPORT);
	}

	@Override
	public double applyStrategy(final OrderModel order, final OrderEntryType orderEntryType)
	{
		Double totalRefund = getBookingService().getOrderTotalPriceByType(order, orderEntryType);
		final Optional<AbstractOrderEntryModel> adminFeeOrderEntry = order.getEntries().stream()
				.filter(
						orderEntry -> Objects.equals(orderEntry.getProduct().getProductType(), ProductType.FEE)
								|| orderEntry.getProduct() instanceof FeeProductModel)
				.filter(orderEntry -> orderEntry.getProduct().getCode().equals(TravelservicesConstants.ADMIN_FEE_PRODUCT_CODE))
				.findFirst();
		if (adminFeeOrderEntry.isPresent())
		{
			totalRefund = totalRefund - adminFeeOrderEntry.get().getBasePrice();
		}
		return totalRefund;
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

}
