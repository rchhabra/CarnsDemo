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

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelrulesengine.constants.TravelrulesengineConstants;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class populates sales application value in Offer Request Rao
 */
public class OfferRequestRaoChannelPopulator implements Populator<OfferRequestData, OfferRequestRAO>
{

	private SessionService sessionService;

	@Override
	public void populate(final OfferRequestData source, final OfferRequestRAO target) throws ConversionException
	{
		final SalesApplication salesApp = (SalesApplication) getSessionService().getAttribute(TravelrulesengineConstants.SESSION_SALES_APPLICATION);
		final String salesApplication = Objects.isNull(salesApp) ? StringUtils.EMPTY : salesApp.getCode();

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
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
