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

package de.hybris.platform.travelfacades.airline.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.travelfacades.process.email.context.AbstractBookingEmailContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


/**
 * The class is responsible for populating the Booking Amendment Email Context.
 */
public class BookingAmendmentEmailContext extends AbstractBookingEmailContext
{
	private ReservationData reservationData;
	private String reservationCode;
	private String totalToRefund;
	private String totalToPay;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		reservationCode = orderProcessModel.getOrder().getCode();
		reservationData = getReservationFacade().getReservationData(orderProcessModel.getOrder());
		if (orderProcessModel.getOrder().getOriginalOrder() != null)
		{
			reservationCode = orderProcessModel.getOrder().getOriginalOrder().getCode();
		}
		if (null != reservationData.getTotalToPay() && reservationData.getTotalToPay().getValue().compareTo(BigDecimal.ZERO) > 0)
		{
			totalToPay = reservationData.getTotalToPay().getFormattedValue();
		}
		else if (null != reservationData.getTotalToPay()
				&& reservationData.getTotalToPay().getValue().compareTo(BigDecimal.ZERO) < 0)
		{
			final PriceData formattedPrice = getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
					reservationData.getTotalToPay().getValue().negate(), orderProcessModel.getOrder().getCurrency());
			totalToRefund = formattedPrice.getFormattedValue();
		}
	}

	@Override
	protected Set<String> getAdditionalEmails(final OrderProcessModel orderProcessModel)
	{
		final Set<String> emails = new HashSet<>();
		initTravellersEmails(orderProcessModel, emails);
		return emails;
	}

	@Override
	protected CustomerModel getCustomer(final OrderProcessModel orderProcessModel)
	{
		if(orderProcessModel.getOrder().getAdditionalSecurity())
		{
			return (CustomerModel) orderProcessModel.getOrder().getOriginalOrder().getUser();
		}
		else {
			return super.getCustomer(orderProcessModel);
		}
	}

	/**
	 * @return ReservationData
	 */
	public ReservationData getReservation()
	{
		return reservationData;
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
