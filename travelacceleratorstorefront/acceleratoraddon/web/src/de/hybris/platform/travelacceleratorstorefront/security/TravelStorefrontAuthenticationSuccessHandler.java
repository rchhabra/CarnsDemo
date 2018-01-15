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

package de.hybris.platform.travelacceleratorstorefront.security;

import de.hybris.platform.acceleratorstorefrontcommons.security.StorefrontAuthenticationSuccessHandler;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Extends StorefrontAuthenticationSuccessHandler to redefine target urls for checkout journey
 */
public class TravelStorefrontAuthenticationSuccessHandler extends StorefrontAuthenticationSuccessHandler
{
	private static final String PAYMENT_FLOW = "sop";

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	private SessionService sessionService;

	private CMSSiteService cmsSiteService;


	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response)
	{
		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (StringUtils.isEmpty(sessionBookingJourney))
		{
			return "/";
		}
		if (StringUtils.equalsIgnoreCase(sessionBookingJourney, TravelfacadesConstants.BOOKING_TRANSPORT_ONLY))
		{
			if (getTravelCartFacade().isAmendmentCart())
			{
				final String paymentFlowProperty = configurationService.getConfiguration().getString("payment.flow");
				if (StringUtils.isNotBlank(paymentFlowProperty))
				{
					return TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH + paymentFlowProperty;
				}
				return TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH + PAYMENT_FLOW;
			}
			return TravelacceleratorstorefrontWebConstants.TRAVELLER_DETAILS_PATH;
		}
		else if (StringUtils.equalsIgnoreCase(sessionBookingJourney,
				TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY))
		{
			return TravelacceleratorstorefrontWebConstants.GUEST_DETAILS_PATH;
		}
		else if (StringUtils.equalsIgnoreCase(sessionBookingJourney, TravelfacadesConstants.BOOKING_PACKAGE))
		{
			return TravelacceleratorstorefrontWebConstants.PERSONAL_DETAILS_PATH;
		}
		else if (StringUtils.equalsIgnoreCase(sessionBookingJourney,
				TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION))
		{
			return TravelacceleratorstorefrontWebConstants.TRAVELLER_DETAILS_PATH;
		}
		return "/";
	}

	/**
	 * @return the travelCartFacade
	 */
	protected TravelCartFacade getTravelCartFacade()
	{
		return travelCartFacade;
	}

	/**
	 * @param travelCartFacade the travelCartFacade to set
	 */
	public void setTravelCartFacade(final TravelCartFacade travelCartFacade)
	{
		this.travelCartFacade = travelCartFacade;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
