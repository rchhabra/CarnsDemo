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
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.travelfacades.process.email.context.AbstractBookingEmailContext;

import java.util.HashSet;
import java.util.Set;

/**
 * The class is responsible for populating the Booking Confirmation Email Context.
 */
public class BookingNotificationEmailContext extends AbstractBookingEmailContext
{
	private ReservationData reservationData;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		reservationData = getReservationFacade().getReservationData(orderProcessModel.getOrder());
	}

	@Override
	protected Set<String> getAdditionalEmails(final OrderProcessModel orderProcessModel)
	{
		final Set<String> emails = new HashSet<>();
		initTravellersEmails(orderProcessModel, emails);
		return emails;
	}

	/**
	 * @return ReservationData
	 */
	public ReservationData getReservation()
	{
		return reservationData;
	}
}
