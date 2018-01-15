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

package de.hybris.platform.travelrulesengine.rule.evaluation;

import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.RAOAction;
import de.hybris.platform.travelrulesengine.rao.FeeRAO;


/**
 * Interface for Admin Fee RAO action method
 * 
 * @deprecated Deprecated since version 3.0. Use {@link RAOAction}.
 */
@Deprecated
public interface AdminFeeRAOAction
{
	/**
	 * Method to create admin Fee Rao object
	 *
	 * @param cartRao
	 * 		the cart rao
	 * @param ruleEngineResultRao
	 * 		the rule engine result rao
	 * @param ruleContext
	 * 		the rule context
	 * @return fee rao
	 */
	 FeeRAO addAdminFee(CartRAO cartRao, RuleEngineResultRAO ruleEngineResultRao, Object ruleContext);

}
