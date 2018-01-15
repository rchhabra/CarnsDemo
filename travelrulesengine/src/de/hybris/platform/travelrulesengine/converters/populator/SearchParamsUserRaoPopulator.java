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

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelrulesengine.constants.TravelrulesengineConstants;
import de.hybris.platform.travelrulesengine.rao.SearchParamsRAO;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Search params user rao populator.
 */
public class SearchParamsUserRaoPopulator implements Populator<FareSearchRequestData, SearchParamsRAO>
{
	private Converter<UserModel, UserRAO> userConverter;
	private SessionService sessionService;

	@Override
	public void populate(final FareSearchRequestData fareSearchRequestData, final SearchParamsRAO searchParamsRAO)
			throws ConversionException
	{
		if(Objects.nonNull(getSessionService().getAttribute(TravelrulesengineConstants.USER)))
		{
			searchParamsRAO.setUser(getUserConverter().convert(getSessionService().getAttribute(TravelrulesengineConstants.USER)));
		}
	}

	/**
	 * Gets user converter.
	 *
	 * @return the user converter
	 */
	protected Converter<UserModel, UserRAO> getUserConverter()
	{
		return userConverter;
	}

	/**
	 * Sets user converter.
	 *
	 * @param userConverter
	 * 		the user converter
	 */
	@Required
	public void setUserConverter(final Converter<UserModel, UserRAO> userConverter)
	{
		this.userConverter = userConverter;
	}

	/**
	 * Gets session service.
	 *
	 * @return the session service
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Sets session service.
	 *
	 * @param sessionService
	 * 		the session service
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
