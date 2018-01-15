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

package de.hybris.platform.travelacceleratorstorefront.filters;

import de.hybris.platform.travelacceleratorstorefront.security.ManageMyBookingSessionCleanUpStrategy;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * This Filter cleans up ManageMyBookingSession Attribute, if the url of the page being accessed is not in
 * ManageMyBooking flow.
 */
public class ManageMyBookingFilter extends OncePerRequestFilter
{
	private ManageMyBookingSessionCleanUpStrategy mmbSessionCleanUpStrategy;

	/**
	 * Checks the URL pattern of the page url, and if its not of ManageMyBooking pattern, removes the manageMyBooking
	 * session attributes.
	 */
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		mmbSessionCleanUpStrategy.manageMyBookingCleanUp(request);
		filterChain.doFilter(request, response);

	}

	protected ManageMyBookingSessionCleanUpStrategy getMmbSessionCleanUpStrategy()
	{
		return mmbSessionCleanUpStrategy;
	}

	@Required
	public void setMmbSessionCleanUpStrategy(final ManageMyBookingSessionCleanUpStrategy mmbSessionCleanUpStrategy)
	{
		this.mmbSessionCleanUpStrategy = mmbSessionCleanUpStrategy;
	}

}
