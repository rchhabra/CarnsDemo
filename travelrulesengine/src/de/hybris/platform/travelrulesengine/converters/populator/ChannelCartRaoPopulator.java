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
package de.hybris.platform.travelrulesengine.converters.populator;


import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelrulesengine.constants.TravelrulesengineConstants;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;


/**
 * This class populates the sales application in CartRao fetched from session and during NDC request it will fetch the
 * same from an OrderModel
 */
public class ChannelCartRaoPopulator implements Populator<AbstractOrderModel, CartRAO>
{
	private SessionService sessionService;

	@Override
	public void populate(final AbstractOrderModel source, final CartRAO target)
			throws ConversionException
	{
		SalesApplication salesApp = (SalesApplication) getSessionService()
				.getAttribute(TravelrulesengineConstants.SESSION_SALES_APPLICATION);
		String salesApplication = Objects.isNull(salesApp) ? StringUtils.EMPTY : salesApp.getCode();

		/*
		 * Below block will execute during NDC requests
		 */
		if (StringUtils.isEmpty(salesApplication) && source instanceof OrderModel)
		{
			salesApp = ((OrderModel) source).getSalesApplication();
			salesApplication = salesApp.getCode();
		}
		target.setSalesApplication(salesApplication);
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
