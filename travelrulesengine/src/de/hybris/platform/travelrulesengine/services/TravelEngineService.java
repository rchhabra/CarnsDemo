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

package de.hybris.platform.travelrulesengine.services;

import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengineservices.enums.FactContextType;

import java.util.List;


/**
 * Interface for drools engine services
 */
public interface TravelEngineService
{
	/**
	 * Method to evaluate the objects with applicable rules
	 *
	 * @param factObjects
	 * 		the fact objects
	 * @param contextType
	 * 		the context type
	 * @return rule evaluation result
	 */
	RuleEvaluationResult evaluate(List<Object> factObjects, FactContextType contextType);
}
