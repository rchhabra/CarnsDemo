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

package de.hybris.platform.travelfacades.travel.process.email.context;


import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.travelfacades.process.email.context.AbstractBookingEmailContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * The class is responsible for populating the Booking Confirmation Email Context.
 */
public class BookingNotificationEmailContext extends AbstractBookingEmailContext
{
	private GlobalTravelReservationData travelReservationData;
	private String totalPaid;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		final OrderModel order = orderProcessModel.getOrder();
		travelReservationData = getReservationFacade().getGlobalTravelReservationData(order);
		totalPaid = getTotalPaid(travelReservationData, order);
	}

	/**
	 * Gets total paid.
	 *
	 * @param travelReservationData
	 * 		the travel reservation data
	 * @param order
	 * 		the order
	 * @return the total paid
	 */
	protected String getTotalPaid(final GlobalTravelReservationData travelReservationData, final OrderModel order)
	{
		final ReservationData reservationData = travelReservationData.getReservationData();
		final AccommodationReservationData accommodationReservationData = travelReservationData.getAccommodationReservationData();
		BigDecimal totalPaid = BigDecimal.ZERO;
		if (Objects.nonNull(reservationData) && Objects.nonNull(accommodationReservationData))
		{
			totalPaid = reservationData.getTotalFare().getTotalPrice().getValue().add(accommodationReservationData.getTotalRate()
					.getActualRate().getValue().subtract(accommodationReservationData.getTotalToPay().getValue()));
		}
		else if (Objects.nonNull(reservationData) && Objects.isNull(accommodationReservationData))
		{
			totalPaid = reservationData.getTotalFare().getTotalPrice().getValue();
		}
		else if (Objects.nonNull(accommodationReservationData))
		{
			totalPaid = accommodationReservationData.getTotalRate().getActualRate().getValue()
					.subtract(accommodationReservationData.getTotalToPay().getValue());
		}

		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalPaid, order.getCurrency().getIsocode())
				.getFormattedValue();
	}

	@Override
	protected Set<String> getAdditionalEmails(final OrderProcessModel orderProcessModel)
	{
		final Set<String> toEmails = new HashSet<>();
		initTravellersEmails(orderProcessModel, toEmails);
		initGuestsEmails(orderProcessModel, toEmails);
		return toEmails;
	}

	/**
	 * Gets travel reservation data.
	 *
	 * @return accommodationReservationData travel reservation data
	 */
	public GlobalTravelReservationData getTravelReservationData()
	{
		return travelReservationData;
	}

	/**
	 * Gets total paid.
	 *
	 * @return the totalPaid
	 */
	public String getTotalPaid()
	{
		return totalPaid;
	}

	/**
	 * Sets total paid.
	 *
	 * @param totalPaid
	 * 		the totalPaid to set
	 */
	public void setTotalPaid(final String totalPaid)
	{
		this.totalPaid = totalPaid;
	}
}
