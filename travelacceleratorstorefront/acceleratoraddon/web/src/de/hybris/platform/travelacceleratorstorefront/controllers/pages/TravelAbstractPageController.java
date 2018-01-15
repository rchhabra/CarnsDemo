
/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.travelacceleratorstorefront.controllers.pages;


import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.facades.customer.TravelCustomerFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Base controller for all travel page controllers. Provides common functionality for all page controllers.
 */
public class TravelAbstractPageController extends AbstractPageController
{
	private static final Logger LOG = Logger.getLogger(TravelAbstractPageController.class);

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "travelCustomerFacade")
	private TravelCustomerFacade travelCustomerFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@ModelAttribute("siteUid")
	public String getSiteUid()
	{
		final CMSSiteModel site = getCmsSiteService().getCurrentSite();
		return site != null ? site.getUid() : StringUtils.EMPTY;
	}

	@ModelAttribute("reservationCode")
	public String getReservationCode()
	{
		if (travelCartFacade.isAmendmentCart())
		{
			return travelCartFacade.getOriginalOrderCode();
		}
		return travelCartFacade.getCurrentCartCode();
	}

	@ModelAttribute("disableCurrencySelector")
	public Boolean getDisableCurrencySelector()
	{
		if (travelCartFacade.isAmendmentCart())
		{
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	protected String getJson(final Object objToBeConverted, final String objName)
	{
		if (Objects.isNull(objToBeConverted))
		{
			return null;
		}
		final ObjectMapper mapper = new ObjectMapper();
		try
		{
			return mapper.writeValueAsString(objToBeConverted);
		}
		catch (final IOException ex)
		{
			LOG.error("Error converting " + objName + " to Json, reason : " + ex.getMessage());
		}
		return null;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @return the travelCustomerFacade
	 */
	protected TravelCustomerFacade getTravelCustomerFacade()
	{
		return travelCustomerFacade;
	}

	/**
	 * @return the travelCustomerFacade
	 */
	protected TravelCartFacade getTravelCartFacade()
	{
		return travelCartFacade;
	}
}
