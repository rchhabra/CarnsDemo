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

package de.hybris.platform.travelservices.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;


/**
 * Abstract class that exposes the common methods for the Booking Amendment Email Context.
 */
public abstract class AbstractTravelBookingEmailContext extends AbstractEmailContext<OrderProcessModel>
{
	private Boolean additionalSecurity;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		final OrderModel order = orderProcessModel.getOrder();
		additionalSecurity = order.getAdditionalSecurity();
		put(TravelservicesConstants.FILTER_TRAVELLERS_BY_RECIPIENT, Boolean.FALSE);
	}

	/**
	 * Gets additional security.
	 *
	 * @return the additional security
	 */
	public Boolean getAdditionalSecurity()
	{
		return additionalSecurity;
	}

	/**
	 * Sets additional security.
	 *
	 * @param additionalSecurity
	 * 		the additional security
	 */
	public void setAdditionalSecurity(final Boolean additionalSecurity)
	{
		this.additionalSecurity = additionalSecurity;
	}
}
