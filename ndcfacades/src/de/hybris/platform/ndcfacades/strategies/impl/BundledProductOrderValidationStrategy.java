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
import de.hybris.platform.ndcfacades.strategies.AmendOrderValidationStrategy;
import de.hybris.platform.travelservices.services.BookingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy that implements the {@link AmendOrderValidationStrategy}.
 * The strategy is used to validate the addToOrder of a product. The addToOrder is not valid if the product is included in
 * a bundle.
 */
public class BundledProductOrderValidationStrategy implements AmendOrderValidationStrategy
{
	private BookingService bookingService;

	@Override
	public boolean validateAmendOrder(final OrderModel order, final String productCode, final long qty, final String travellerCode,
			final List<String> transportOfferingCodes, final String travelRouteCode)
	{
		final List<String> travellerCodes = new ArrayList<>();
		if (StringUtils.isNotBlank(travellerCode))
		{
			travellerCodes.add(travellerCode);
		}

		return getBookingService().checkBundleToAmendProduct(order, productCode, qty,
				travelRouteCode, transportOfferingCodes, travellerCodes);
	}

	protected BookingService getBookingService()
	{
		return bookingService;
	}

	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}
}
