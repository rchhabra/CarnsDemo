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

package de.hybris.platform.travelacceleratorstorefront.interceptors.beforecontroller;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;

import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * This interceptor is created only to remove payment transactions from the session as we are using them to display the
 * partial amount in reservation totals and reservation overlay totals components. Since reservation totals and
 * reservation overlay totals components are mapped to almost every page so it wouldn't be appropriate to display the
 * partial payment on pages where it doesn't make any sense
 *
 */
public class RemoveTransactionsBeforeControllerInterceptor extends HandlerInterceptorAdapter
{

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception
	{
		if (Objects.nonNull(sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS)))
		{
			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS);
		}
		return true;
	}
}
