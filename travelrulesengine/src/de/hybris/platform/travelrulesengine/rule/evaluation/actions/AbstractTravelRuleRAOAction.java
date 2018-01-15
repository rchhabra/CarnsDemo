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
 */

package de.hybris.platform.travelrulesengine.rule.evaluation.actions;

import de.hybris.platform.ruleengineservices.rule.evaluation.actions.AbstractRuleExecutableSupport;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.RAOAction;
import de.hybris.platform.travelrulesengine.calculation.TravelRuleEngineCalculationService;


/**
 * Abstract Drools RAO action class to support travel specific calculation service
 */
public abstract class AbstractTravelRuleRAOAction extends AbstractRuleExecutableSupport implements RAOAction
{
	private TravelRuleEngineCalculationService travelRuleEngineCalculationService;

	/**
	 * @return the travelRuleEngineCalculationService
	 */
	public TravelRuleEngineCalculationService getTravelRuleEngineCalculationService()
	{
		return travelRuleEngineCalculationService;
	}

	/**
	 * @param travelRuleEngineCalculationService
	 *           the travelRuleEngineCalculationService to set
	 */
	public void setTravelRuleEngineCalculationService(final TravelRuleEngineCalculationService travelRuleEngineCalculationService)
	{
		this.travelRuleEngineCalculationService = travelRuleEngineCalculationService;
	}
}
