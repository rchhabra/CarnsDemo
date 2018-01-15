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

package de.hybris.platform.travelacceleratorstorefront.filters;

import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelacceleratorstorefront.security.cookie.EnhancedCookieGenerator;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;


public class SessionTimeoutCookieFilter extends OncePerRequestFilter
{
	private TimeService timeService;

	protected EnhancedCookieGenerator enhancedCookieGenerator;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		if (request.isRequestedSessionIdValid())
		{
			final Long cookieExpirationTime = getTimeService().getCurrentTime().getTime()
					+ getEnhancedCookieGenerator().getCookieMaxAge() * 1000;
			getEnhancedCookieGenerator().addCookie(response, URLEncoder.encode(cookieExpirationTime.toString(), "UTF-8"));
		}
		filterChain.doFilter(request, response);
	}

	public TimeService getTimeService()
	{
		return timeService;
	}

	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	public EnhancedCookieGenerator getEnhancedCookieGenerator()
	{
		return enhancedCookieGenerator;
	}

	public void setEnhancedCookieGenerator(final EnhancedCookieGenerator enhancedCookieGenerator)
	{
		this.enhancedCookieGenerator = enhancedCookieGenerator;
	}

}
