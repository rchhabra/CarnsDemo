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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.enums.PaymentType;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * This handler is responsible for providing basic values of Reservation instance like code
 */
public class ReservationBasicHandler implements ReservationHandler
{

	private Map<String, PaymentType> paymentInfoToPaymentTypeMap;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		reservationData.setCode(abstractOrderModel.getCode());
		if (Objects.nonNull(abstractOrderModel.getPaymentInfo()))
		{
			reservationData.setPaymentType(
					getPaymentInfoToPaymentTypeMap().get(abstractOrderModel.getPaymentInfo().getClass().getSimpleName()));
		}
	}

	/**
	 *
	 * @return paymentInfoToPaymentTypeMap
	 */
	protected Map<String, PaymentType> getPaymentInfoToPaymentTypeMap()
	{
		return paymentInfoToPaymentTypeMap;
	}

	/**
	 *
	 * @param paymentInfoToPaymentTypeMap
	 *           the paymentInfoToPaymentTypeMap
	 */
	@Required
	public void setPaymentInfoToPaymentTypeMap(final Map<String, PaymentType> paymentInfoToPaymentTypeMap)
	{
		this.paymentInfoToPaymentTypeMap = paymentInfoToPaymentTypeMap;
	}


}
