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

package de.hybris.platform.travelrulesengine.rule.evaluation;

import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.RAOAction;
import de.hybris.platform.travelrulesengine.rao.FareProductRAO;
import de.hybris.platform.travelrulesengine.rao.FilterFareRAO;


/**
 * Interface for Fare Filtering RAO action
 * 
 * @deprecated Deprecated since version 3.0. Use {@link RAOAction}.
 */
@Deprecated
public interface DiscardFareRAOAction
{
	/**
	 * Discards fare based on result of rule evaluation
	 *
	 * @param fareProductRAO
	 * 		the fare product rao
	 * @param ruleEngineResultRao
	 * 		the rule engine result rao
	 * @param ruleContext
	 * 		the rule context
	 * @return FilterFareRAO result object
	 */
	FilterFareRAO discardFare(FareProductRAO fareProductRAO, RuleEngineResultRAO ruleEngineResultRao, Object ruleContext);
}
