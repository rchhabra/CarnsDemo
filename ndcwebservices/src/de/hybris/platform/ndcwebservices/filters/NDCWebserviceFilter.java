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
package de.hybris.platform.ndcwebservices.filters;

import de.hybris.platform.acceleratorcms.context.ContextInformationLoader;
import de.hybris.platform.acceleratorservices.site.strategies.SiteChannelValidationStrategy;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.jalo.c2l.LocalizableItem;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static de.hybris.platform.cms2.misc.CMSFilter.CLEAR_CMSSITE_PARAM;


public class NDCWebserviceFilter extends OncePerRequestFilter
{

	private static final Logger LOG = Logger.getLogger(NDCWebserviceFilter.class);

	protected static final int MISSING_CMS_SITE_ERROR_STATUS = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	protected static final String MISSING_CMS_SITE_ERROR_MESSAGE = "Cannot find CMSSite associated with current URL";
	protected static final String INCORRECT_CMS_SITE_CHANNEL_ERROR_MESSAGE = "Matched CMSSite for current URL has unsupported channel";

	private CMSSiteService cmsSiteService;
	private ContextInformationLoader contextInformationLoader;
	private SessionService sessionService;
	private SiteChannelValidationStrategy siteChannelValidationStrategy;

	@Override
	protected void doFilterInternal(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
			final FilterChain filterChain) throws ServletException, IOException
	{

		if(processNormalRequest(httpRequest, httpResponse))
		{
			filterChain.doFilter(httpRequest, httpResponse);
		}

	}

	protected boolean processNormalRequest(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse)
			throws IOException
	{
		final String queryString = httpRequest.getQueryString();
		final String currentRequestURL = httpRequest.getRequestURL().toString();

		//set current site
		CMSSiteModel cmsSiteModel = getCurrentCmsSite();
		if (cmsSiteModel == null || StringUtils.contains(queryString, CLEAR_CMSSITE_PARAM))
		{
			final String absoluteURL = StringUtils.removeEnd(currentRequestURL, "/")
					+ (StringUtils.isBlank(queryString) ? "" : "?" + queryString);

			cmsSiteModel = getContextInformationLoader().initializeSiteFromRequest(absoluteURL);
		}

		if (cmsSiteModel == null)
		{
			// Failed to lookup CMS site
			httpResponse.sendError(MISSING_CMS_SITE_ERROR_STATUS, MISSING_CMS_SITE_ERROR_MESSAGE);
			return false;
		}
		else if (!getSiteChannelValidationStrategy().validateSiteChannel(cmsSiteModel.getChannel())) // Restrict to configured channel
		{
			// CMS site that we looked up was for an unsupported channel
			httpResponse.sendError(MISSING_CMS_SITE_ERROR_STATUS, INCORRECT_CMS_SITE_CHANNEL_ERROR_MESSAGE);
			return false;
		}

		if (!isActiveSite(cmsSiteModel))
		{
			throw new IllegalStateException("Site is not active. Active flag behaviour must be implement for this project.");
		}

		getContextInformationLoader().setCatalogVersions();
		//set fall back language enabled
		setFallbackLanguage(httpRequest, Boolean.TRUE);

		return true;
	}

	protected CMSSiteModel getCurrentCmsSite()
	{
		try
		{
			return getCmsSiteService().getCurrentSite();
		}
		catch (final JaloObjectNoLongerValidException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
			return null;
		}
	}

	protected void setFallbackLanguage(final HttpServletRequest httpRequest, final Boolean enabled)
	{
		if (getSessionService() != null)
		{
			getSessionService().setAttribute(LocalizableItem.LANGUAGE_FALLBACK_ENABLED, enabled);
			getSessionService().setAttribute(AbstractItemModel.LANGUAGE_FALLBACK_ENABLED_SERVICE_LAYER, enabled);
		}
	}

	protected boolean isActiveSite(final CMSSiteModel site)
	{
		return site.getActive() != null && site.getActive().booleanValue();
	}

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	public void setCmsSiteService(CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	public ContextInformationLoader getContextInformationLoader()
	{
		return contextInformationLoader;
	}

	public void setContextInformationLoader(ContextInformationLoader contextInformationLoader)
	{
		this.contextInformationLoader = contextInformationLoader;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public SiteChannelValidationStrategy getSiteChannelValidationStrategy()
	{
		return siteChannelValidationStrategy;
	}

	public void setSiteChannelValidationStrategy(
			SiteChannelValidationStrategy siteChannelValidationStrategy)
	{
		this.siteChannelValidationStrategy = siteChannelValidationStrategy;
	}
}
