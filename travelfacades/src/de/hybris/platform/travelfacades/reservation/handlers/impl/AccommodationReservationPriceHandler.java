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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForOrderEntryTypeCalculationStrategy;

import java.math.BigDecimal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler responsible for populating Price(TotalRate and TotalToPay) in AccommodationReservationData
 */
public class AccommodationReservationPriceHandler implements AccommodationReservationHandler
{
	private OrderTotalPaidForOrderEntryTypeCalculationStrategy orderTotalPaidForOrderEntryTypeCalculationStrategy;
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	@Override
	public void handle(final AbstractOrderModel abstractOrder, final AccommodationReservationData accommodationReservationData)
	{
		populateTotalRate(abstractOrder, accommodationReservationData);
		populateTotalToPay(abstractOrder, accommodationReservationData);
	}

	/**
	 * Gets total rate.
	 *
	 * @param abstractOrder
	 *           the abstract order
	 * @return the total rate
	 */
	protected void populateTotalRate(final AbstractOrderModel abstractOrder,
			final AccommodationReservationData accommodationReservationData)
	{
		Double totalRate = 0d;
		for (final AbstractOrderEntryModel entry : abstractOrder.getEntries())
		{
			if (BooleanUtils.isTrue(entry.getActive()) && OrderEntryType.ACCOMMODATION.equals(entry.getType()))
			{
				totalRate = Double.sum(totalRate, entry.getTotalPrice());
				if (CollectionUtils.isNotEmpty(entry.getTaxValues()))
				{
					totalRate = Double.sum(totalRate,
							entry.getTaxValues().stream().mapToDouble(taxValue -> taxValue.getAppliedValue()).sum());
				}
			}
		}

		final PriceData actualPrice = createPriceData(BigDecimal.valueOf(totalRate), abstractOrder.getCurrency().getIsocode());
		final RateData rate = new RateData();
		rate.setActualRate(actualPrice);
		accommodationReservationData.setTotalRate(rate);
	}

	protected void populateTotalToPay(final AbstractOrderModel abstractOrder,
			final AccommodationReservationData accommodationReservationData)
	{
		final Double totalPaid = getOrderTotalPaidForOrderEntryTypeCalculationStrategy()
				.calculate(abstractOrder, OrderEntryType.ACCOMMODATION).doubleValue();
		final Double totalRate = accommodationReservationData.getTotalRate().getActualRate().getValue().doubleValue();
		final PriceData totalToPay = createPriceData(BigDecimal.valueOf(totalRate - totalPaid),
				abstractOrder.getCurrency().getIsocode());
		accommodationReservationData.setTotalToPay(totalToPay);
	}

	protected PriceData createPriceData(final BigDecimal value, final String isoCode)
	{
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, value, isoCode);
	}

	/**
	 * Gets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the price data factory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the price data factory
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * @return the orderTotalPaidForOrderEntryTypeCalculationStrategy
	 */
	protected OrderTotalPaidForOrderEntryTypeCalculationStrategy getOrderTotalPaidForOrderEntryTypeCalculationStrategy()
	{
		return orderTotalPaidForOrderEntryTypeCalculationStrategy;
	}

	/**
	 * @param orderTotalPaidForOrderEntryTypeCalculationStrategy
	 *           the orderTotalPaidForOrderEntryTypeCalculationStrategy to set
	 */
	@Required
	public void setOrderTotalPaidForOrderEntryTypeCalculationStrategy(
			final OrderTotalPaidForOrderEntryTypeCalculationStrategy orderTotalPaidForOrderEntryTypeCalculationStrategy)
	{
		this.orderTotalPaidForOrderEntryTypeCalculationStrategy = orderTotalPaidForOrderEntryTypeCalculationStrategy;
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
