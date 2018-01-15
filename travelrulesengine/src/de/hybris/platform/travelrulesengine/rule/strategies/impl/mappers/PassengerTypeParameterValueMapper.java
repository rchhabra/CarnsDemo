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
import de.hybris.platform.travelrulesengine.dao.RulePassengerTypeDao;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Passenger type parameter value mapper.
 */
public class PassengerTypeParameterValueMapper implements RuleParameterValueMapper<PassengerTypeModel>
{
	private RulePassengerTypeDao rulePassengerTypeDao;

	@Override
	public String toString(final PassengerTypeModel passengerTypeModel)
	{
		return passengerTypeModel.getCode();
	}

	@Override
	public PassengerTypeModel fromString(final String code)
	{
		return getRulePassengerTypeDao().findPassengerTypeByCode(code);
	}

	/**
	 * Gets rule passenger type dao.
	 *
	 * @return the rule passenger type dao
	 */
	protected RulePassengerTypeDao getRulePassengerTypeDao()
	{
		return rulePassengerTypeDao;
	}

	/**
	 * Sets rule passenger type dao.
	 *
	 * @param rulePassengerTypeDao
	 * 		the rule passenger type dao
	 */
	@Required
	public void setRulePassengerTypeDao(final RulePassengerTypeDao rulePassengerTypeDao)
	{
		this.rulePassengerTypeDao = rulePassengerTypeDao;
	}
}
