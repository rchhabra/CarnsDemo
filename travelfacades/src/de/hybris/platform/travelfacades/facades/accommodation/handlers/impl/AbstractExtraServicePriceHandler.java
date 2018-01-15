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

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.accommodation.ServiceRateData;
import de.hybris.platform.commercefacades.accommodation.TimeSpanData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractExtraServicePriceHandler
{
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	protected ServiceRateData getServiceRate(final ServiceData serviceData, final ReservedRoomStayData reservedRoomStayData)
	{
		final ServiceRateData serviceRateData = new ServiceRateData();

		final TimeSpanData timeSpan = new TimeSpanData();
		timeSpan.setStartDate(reservedRoomStayData.getCheckInDate());
		timeSpan.setEndDate(reservedRoomStayData.getCheckOutDate());
		serviceRateData.setTimeSpan(timeSpan);
		serviceRateData.setBasePrice(serviceData.getServiceDetails().getProduct().getPrice());
		serviceRateData.setTotal(getTotalPriceData(serviceData));

		return serviceRateData;
	}

	protected PriceData getTotalPriceData(final ServiceData serviceData)
	{
		final PriceData priceData = serviceData.getServiceDetails().getProduct().getPrice();
		final BigDecimal totalPriceValue = priceData.getValue().multiply(BigDecimal.valueOf(serviceData.getQuantity()));
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalPriceValue, priceData.getCurrencyIso());
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

}
