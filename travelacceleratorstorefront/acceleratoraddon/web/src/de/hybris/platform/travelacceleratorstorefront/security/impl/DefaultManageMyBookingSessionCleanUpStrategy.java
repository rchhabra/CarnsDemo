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
 */

package de.hybris.platform.travelacceleratorstorefront.security.impl;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation class of ManageMyBookingSessionCleanUpStrategy.
 */
public class DefaultManageMyBookingSessionCleanUpStrategy
		implements de.hybris.platform.travelacceleratorstorefront.security.ManageMyBookingSessionCleanUpStrategy
{
	private SessionService sessionService;

	private Pattern manageMyBookingUrlPattern;

	private String excludedUrls;

	@Override
	public void manageMyBookingCleanUp(final HttpServletRequest request)
	{
		if (Boolean.TRUE
				.equals(getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.MANAGE_MY_BOOKING_AUTHENTICATION))
				&& isGetMethod(request) && !checkManageMyBookingURLPattern(request.getRequestURL().toString())
				&& !isExcludedUrlPattern(request.getRequestURL().toString()))
		{
			getSessionService().removeAttribute(TravelacceleratorstorefrontWebConstants.MANAGE_MY_BOOKING_AUTHENTICATION);
			getSessionService().removeAttribute(TravelacceleratorstorefrontWebConstants.MANAGE_MY_BOOKING_GUEST_UID);
			getSessionService().removeAttribute(TravelacceleratorstorefrontWebConstants.MANAGE_MY_BOOKING_BOOKING_REFERENCE);

		}

		if (isGetMethod(request) && !isExcludedUrlPattern(request.getRequestURL().toString()))
		{
			getSessionService().removeAttribute(WebConstants.ANONYMOUS_CHECKOUT);
		}
	}

	protected boolean isExcludedUrlPattern(final String urlPath)
	{
		boolean isUrlMatching = false;

		final String[] excludedUrl = getExcludedUrls().split(",");

		for (final String pattern : excludedUrl)
		{
			if (Pattern.compile(pattern).matcher(urlPath).matches())
			{
				isUrlMatching = true;
				break;
			}
		}

		return isUrlMatching;
	}

	protected boolean isGetMethod(final HttpServletRequest httpRequest)
	{
		return "GET".equalsIgnoreCase(httpRequest.getMethod());
	}

	protected boolean checkManageMyBookingURLPattern(final String urlPath)
	{
		return getManageMyBookingUrlPattern().matcher(urlPath).matches();
	}

	protected Pattern getManageMyBookingUrlPattern()
	{
		return manageMyBookingUrlPattern;
	}

	@Required
	public void setManageMyBookingUrlPattern(final Pattern manageMyBookingUrlPattern)
	{
		this.manageMyBookingUrlPattern = manageMyBookingUrlPattern;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected String getExcludedUrls()
	{
		return excludedUrls;
	}

	@Required
	public void setExcludedUrls(final String excludedUrls)
	{
		this.excludedUrls = excludedUrls;
	}

}
