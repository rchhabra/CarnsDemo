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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


/**
 * The class is responsible for populating the Booking Confirmation Email Context.
 */
public class BookingNotificationEmailContext extends AbstractBookingEmailContext
{
	private AccommodationReservationData accommodationReservationData;
	private String totalPaid;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		final OrderModel order = orderProcessModel.getOrder();
		accommodationReservationData = getReservationFacade().getAccommodationReservationData(order);
		totalPaid = getTotalPaid(accommodationReservationData, order);
	}

	protected String getTotalPaid(final AccommodationReservationData accommodationReservationData, final OrderModel order)
	{
		final Double totalPaid = accommodationReservationData.getTotalRate().getActualRate().getValue().doubleValue()
				- accommodationReservationData.getTotalToPay().getValue().doubleValue();
		return getTravelCommercePriceFacade()
				.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(totalPaid), order.getCurrency().getIsocode())
				.getFormattedValue();
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
	 * @return the totalPaid
	 */
	public String getTotalPaid()
	{
		return totalPaid;
	}

	/**
	 * @param totalPaid
	 *           the totalPaid to set
	 */
	public void setTotalPaid(final String totalPaid)
	{
		this.totalPaid = totalPaid;
	}
}
