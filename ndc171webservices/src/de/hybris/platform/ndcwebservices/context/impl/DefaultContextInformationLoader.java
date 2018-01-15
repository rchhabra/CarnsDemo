/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ndcwebservices.context.impl;

import de.hybris.platform.acceleratorcms.preview.strategies.PreviewContextInformationLoaderStrategy;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.ndcwebservices.context.ContextInformationLoader;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default context information loader
 */
public class DefaultContextInformationLoader implements ContextInformationLoader
{
	private static final Logger LOG = Logger.getLogger(ContextInformationLoader.class);

	private CMSSiteService cmsSiteService;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private I18NService i18NService;
	private ModelService modelService;
	private TimeService timeService;
	private List<PreviewContextInformationLoaderStrategy> strategies;
	private List<PreviewContextInformationLoaderStrategy> previewRequestStrategies;



	/**
	 * Gets current site.
	 *
	 * @return the current site
	 */
	public CMSSiteModel getCurrentSite()
	{
		return getCMSSiteService().getCurrentSite();
	}

	@Override
	public void setCatalogVersions()
	{
		try
		{
			final CMSSiteModel currentSiteModel = getCurrentSite();
			if (currentSiteModel != null)
			{
				getCMSSiteService().setCurrentSiteAndCatalogVersions(currentSiteModel, true);
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Catalog has no active catalog version!", e);
			}
		}
	}

	@Override
	public CMSSiteModel initializeSiteFromRequest(final String absoluteURL)
	{
		try
		{
			final URL currentURL = new URL(absoluteURL);
			final CMSSiteModel cmsSiteModel = getCMSSiteService().getSiteForURL(currentURL);
			if (cmsSiteModel != null)
			{
				getBaseSiteService().setCurrentBaseSite(cmsSiteModel, true);
				return cmsSiteModel;
			}
		}
		catch (final MalformedURLException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Cannot find CMSSite associated with current URL ( " + absoluteURL
						+ " - check whether this is correct URL) !");
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.warn("Cannot find CMSSite associated with current URL (" + absoluteURL + ")!");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
		}
		return null;
	}

	/**
	 * Sets base site service.
	 *
	 * @param baseSiteService
	 * 		the base site service
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * Sets cms site service.
	 *
	 * @param cmsSiteService
	 * 		the cms site service
	 */
	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	/**
	 * Sets i 18 n service.
	 *
	 * @param i18nService
	 * 		the 18 n service
	 */
	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the time service
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * Sets user service.
	 *
	 * @param userService
	 * 		the user service
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Sets preview request strategies.
	 *
	 * @param previewRequestStrategies
	 * 		the preview request strategies
	 */
	@Required
	public void setPreviewRequestStrategies(final List<PreviewContextInformationLoaderStrategy> previewRequestStrategies)
	{
		this.previewRequestStrategies = previewRequestStrategies;

	}

	/**
	 * Sets strategies.
	 *
	 * @param strategies
	 * 		the strategies
	 */
	@Required
	public void setStrategies(final List<PreviewContextInformationLoaderStrategy> strategies)
	{
		this.strategies = strategies;
	}

	/**
	 * Gets cms site service.
	 *
	 * @return the cms site service
	 */
	protected CMSSiteService getCMSSiteService()
	{
		return cmsSiteService;
	}

	/**
	 * Gets base site service.
	 *
	 * @return the base site service
	 */
	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}


	/**
	 * Gets user service.
	 *
	 * @return the user service
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * Gets i 18 n service.
	 *
	 * @return the i 18 n service
	 */
	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Gets preview request strategies.
	 *
	 * @return the preview request strategies
	 */
	protected List<PreviewContextInformationLoaderStrategy> getPreviewRequestStrategies()
	{
		return previewRequestStrategies;
	}

	/**
	 * Gets strategies.
	 *
	 * @return the strategies
	 */
	protected List<PreviewContextInformationLoaderStrategy> getStrategies()
	{
		return strategies;
	}
}
