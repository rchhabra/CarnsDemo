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
 */
package de.hybris.platform.travelb2bfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelb2bfacades.data.B2BReservationData;
import de.hybris.platform.travelb2bfacades.reservation.handlers.B2BReservationHandler;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.math.BigDecimal;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Default B2B Reservation Basic Handler.
 */
public class DefaultB2BReservationBasicHandler implements B2BReservationHandler
{
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final B2BReservationData b2bReservationData)
	{
		b2bReservationData.setOwner(abstractOrderModel.getUser().getUid());
		b2bReservationData.setUnit(abstractOrderModel.getUnit().getName());
		b2bReservationData.setCreationTime(abstractOrderModel.getCreationtime());
		b2bReservationData.setCurrencyCode(abstractOrderModel.getCurrency().getIsocode());
		b2bReservationData.setTotal(getBookingTotal(abstractOrderModel));
		b2bReservationData.setCostCenter(abstractOrderModel.getEntries().get(0).getCostCenter().getName());
	}

	/**
	 * Calculates the total of the reservation in order to set it against the b2breservation
	 *
	 * @param abstractOrderModel
	 * 		the order
	 *
	 * @return priceData the total
	 */
	protected PriceData getBookingTotal(final AbstractOrderModel abstractOrderModel)
	{
		BigDecimal totalPrice = BigDecimal.valueOf(abstractOrderModel.getTotalPrice().doubleValue());
		if (BooleanUtils.isTrue(abstractOrderModel.getNet()))
		{
			totalPrice = totalPrice.add(BigDecimal.valueOf(abstractOrderModel.getTotalTax().doubleValue()));
		}
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalPrice,
				abstractOrderModel.getCurrency().getIsocode());
	}

	/**
	 * Gets travel commerce price facade.
	 *
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * Sets travel commerce price facade.
	 *
	 * @param travelCommercePriceFacade
	 * 		the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * Gets price data factory.
	 *
	 * @return the priceDataFactory
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @param priceDataFactory
	 * 		the priceDataFactory to set
	 *
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

}
