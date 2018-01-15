/*
 *
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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.enums.PaymentType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Accommodation reservation basic handler.
 */
public class AccommodationReservationBasicHandler implements AccommodationReservationHandler
{
	private Map<String, PaymentType> paymentInfoToPaymentTypeMap;

	@Override
	public void handle(final AbstractOrderModel abstractOrder, final AccommodationReservationData accommodationReservationData)
	{
		accommodationReservationData.setCode(abstractOrder.getCode());
		if (Objects.nonNull(abstractOrder.getPaymentInfo()))
		{
			accommodationReservationData
					.setPaymentType(getPaymentInfoToPaymentTypeMap().get(abstractOrder.getPaymentInfo().getClass().getSimpleName()));
		}
		if (abstractOrder.getCurrency() != null)
		{
			accommodationReservationData.setCurrencyIso(abstractOrder.getCurrency().getIsocode());
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
