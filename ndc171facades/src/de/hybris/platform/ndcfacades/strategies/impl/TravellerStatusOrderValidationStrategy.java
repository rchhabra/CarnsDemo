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
package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.order.NDCOrderFacade;
import de.hybris.platform.ndcfacades.strategies.AmendOrderValidationStrategy;
import de.hybris.platform.travelfacades.facades.CheckInFacade;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * Strategy that implements the {@link AmendOrderValidationStrategy}.
 * The strategy is used to validate the addToOrder of a product. The addToOrder is not valid if during an amendment, the
 * product to be changed is related to a traveller that has been already checked in.
 */
public class TravellerStatusOrderValidationStrategy implements AmendOrderValidationStrategy
{
	private CheckInFacade checkInFacade;
	private NDCOrderFacade ndcOrderFacade;

	@Override
	public boolean validateAmendOrder(final OrderModel order, final String productCode, final long qty, final String travellerCode,
			final List<String> transportOfferingCodes, final String travelRouteCode)
	{
		if (getNdcOrderFacade().isAmendmentOrder(order))
		{
			return getCheckInFacade().checkTravellerEligibility(travellerCode, transportOfferingCodes,
					getNdcOrderFacade().getOriginalOrderCode(order));
		}
		return true;
	}

	protected CheckInFacade getCheckInFacade()
	{
		return checkInFacade;
	}

	@Required
	public void setCheckInFacade(final CheckInFacade checkInFacade)
	{
		this.checkInFacade = checkInFacade;
	}

	protected NDCOrderFacade getNdcOrderFacade()
	{
		return ndcOrderFacade;
	}

	@Required
	public void setNdcOrderFacade(final NDCOrderFacade ndcOrderFacade)
	{
		this.ndcOrderFacade = ndcOrderFacade;
	}
}
