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
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.travelfacades.process.email.context.AbstractBookingEmailContext;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


/**
 * The class is responsible for populating the Booking Amendment Email Context.
 */
public class BookingAmendmentEmailContext extends AbstractBookingEmailContext
{
	private AccommodationReservationData accommodationReservationData;
	private String reservationCode;
	private String totalToRefund;
	private String totalToPay;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		final OrderModel order = orderProcessModel.getOrder();
		final String currencyIsoCode = order.getCurrency().getIsocode();
		reservationCode = order.getCode();

		BigDecimal totalPaid = BigDecimal.ZERO;
		if (order.getOriginalOrder() != null)
		{
			final OrderModel previousOrderVersion = order.getOriginalOrder();
			reservationCode = previousOrderVersion.getCode();
			totalPaid = getBookingService().getOrderTotalPaidForOrderEntryType(previousOrderVersion, OrderEntryType.ACCOMMODATION);
		}

		final BigDecimal totalPrice = getBookingService().getOrderTotalPaidForOrderEntryType(order, OrderEntryType.ACCOMMODATION);

		final BigDecimal updatedTotalToPay = totalPrice.subtract(totalPaid);
		if (updatedTotalToPay.compareTo(BigDecimal.ZERO) >= 0)
		{
			totalToPay = getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, updatedTotalToPay, currencyIsoCode)
					.getFormattedValue();
		}
		else
		{
			totalToRefund = getTravelCommercePriceFacade()
					.createPriceData(PriceDataType.BUY, updatedTotalToPay.negate(), currencyIsoCode)
					.getFormattedValue();
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
	 * @return accommodationReservationData
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

	/**
	 * @return the totalToPay
	 */
	public String getTotalToPay()
	{
		return totalToPay;
	}

	/**
	 * @return the reservationCode
	 */
	public String getReservationCode()
	{
		return reservationCode;
	}

}
