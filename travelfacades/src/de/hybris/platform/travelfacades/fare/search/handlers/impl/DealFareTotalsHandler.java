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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;

import java.math.BigDecimal;


/**
 * Extension of the {@link FareTotalsHandler} class. This handler calls the super to populate the totals on
 * {@link FareSelectionData}, then it populates the wasRate of the totalFare.
 */
public class DealFareTotalsHandler extends FareTotalsHandler
{
	@Override
	protected void populateTotals(final ItineraryPricingInfoData itineraryPricingInfo)
	{
		super.populateTotals(itineraryPricingInfo);

		final BigDecimal totalWasRate = itineraryPricingInfo.getPtcFareBreakdownDatas().stream()
				.map(ptcBreakdown -> ptcBreakdown.getPassengerFare().getWasRate().getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		itineraryPricingInfo.getTotalFare().setWasRate(getTravelCommercePriceFacade().createPriceData(totalWasRate.doubleValue()));
	}

}
