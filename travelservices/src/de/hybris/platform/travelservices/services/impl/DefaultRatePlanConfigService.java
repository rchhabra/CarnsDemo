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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.travelservices.dao.RatePlanConfigDao;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;
import de.hybris.platform.travelservices.services.RatePlanConfigService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link RatePlanConfigService}
 */
public class DefaultRatePlanConfigService implements RatePlanConfigService
{
	private RatePlanConfigDao ratePlanConfigDao;

	@Override
	public RatePlanConfigModel getRatePlanConfigForCode(final String ratePlanConfigCode)
	{
		return getRatePlanConfigDao().findRatePlanConfig(ratePlanConfigCode);
	}

	/**
	 * @return the ratePlanConfigDao
	 */
	protected RatePlanConfigDao getRatePlanConfigDao()
	{
		return ratePlanConfigDao;
	}

	/**
	 * @param ratePlanConfigDao
	 *           the ratePlanConfigDao to set
	 */
	@Required
	public void setRatePlanConfigDao(final RatePlanConfigDao ratePlanConfigDao)
	{
		this.ratePlanConfigDao = ratePlanConfigDao;
	}

}
