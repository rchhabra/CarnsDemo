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
package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;

import java.util.Objects;


/**
 * This handler is responsible for populating the Sales application in the {@link ReservationData}
 */
public class ReservationSalesApplicationHandler implements ReservationHandler
{
	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		if(abstractOrderModel instanceof OrderModel && Objects.nonNull(((OrderModel) abstractOrderModel).getSalesApplication()))
		{
			reservationData.setSalesApplication(((OrderModel) abstractOrderModel).getSalesApplication().getCode());
		}
	}
}
