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
import de.hybris.platform.travelrulesengine.rao.FareSearchRequestRAO;
import de.hybris.platform.travelrulesengine.rao.FilterBundleRAO;


/**
 * Interface for Bundle Filtering RAO action
 * 
 * @deprecated Deprecated since version 3.0. Use {@link RAOAction}.
 */
@Deprecated
public interface DiscardBundleRAOAction
{
	/**
	 * Discards bundle based on result of rule evaluation
	 *
	 * @param fareSearchRequestRAO
	 * 		the fare search request rao
	 * @param ruleEngineResultRao
	 * 		the rule engine result rao
	 * @param ruleContext
	 * 		the rule context
	 * @param bundleType
	 * 		the bundle type
	 * @return FilterBundleRAO result object
	 */
	FilterBundleRAO discardBundle(FareSearchRequestRAO fareSearchRequestRAO, RuleEngineResultRAO ruleEngineResultRao,
			Object ruleContext, String bundleType);
}
