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

package de.hybris.platform.travelacceleratorstorefront.interceptors.aftercontroller;

import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Below Interceptor will assign the current currency to the previous currency session attribute to avoid clearing of
 * facets on listing page through facets. If don't do this the system would clear the facets on facet search as previous
 * currency will always be different than the current currency due to the code executed in
 * CurrencyUpdateBeforeControllerHandler before currency change event
 */
public class AfterListingPageControllerInterceptor extends HandlerInterceptorAdapter
{

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception
	{
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PREVIOUS_CURRENCY,
				storeSessionFacade.getCurrentCurrency().getIsocode());
	}
}
