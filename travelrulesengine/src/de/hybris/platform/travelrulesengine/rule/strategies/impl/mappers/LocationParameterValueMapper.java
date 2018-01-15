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
package de.hybris.platform.travelrulesengine.rule.strategies.impl.mappers;

import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapper;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelrulesengine.dao.RuleTravelLocationDao;
import de.hybris.platform.travelservices.model.travel.LocationModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Location parameter value mapper.
 */
public class LocationParameterValueMapper implements RuleParameterValueMapper<LocationModel>
{
	private RuleTravelLocationDao ruleTravelLocationDao;

	@Override
	public String toString(final LocationModel locationModel)
	{
		ServicesUtil.validateParameterNotNull(locationModel, "Object cannot be null");
		return locationModel.getCode();
	}

	@Override
	public LocationModel fromString(final String code)
	{
		return getRuleTravelLocationDao().findLocation(code);
	}

	/**
	 * Gets rule travel location dao.
	 *
	 * @return the rule travel location dao
	 */
	protected RuleTravelLocationDao getRuleTravelLocationDao()
	{
		return ruleTravelLocationDao;
	}

	/**
	 * Sets rule travel location dao.
	 *
	 * @param ruleTravelLocationDao
	 * 		the rule travel location dao
	 */
	@Required
	public void setRuleTravelLocationDao(final RuleTravelLocationDao ruleTravelLocationDao)
	{
		this.ruleTravelLocationDao = ruleTravelLocationDao;
	}
}
