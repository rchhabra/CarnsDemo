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

import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.RAOAction;
import de.hybris.platform.travelrulesengine.rao.BookingRAO;
import de.hybris.platform.travelrulesengine.rao.RefundActionRAO;


/**
 * Interface for retain fee RAO action method
 * 
 * @deprecated Deprecated since version 3.0. Use {@link RAOAction}.
 */
@Deprecated
public interface RetainFeeRAOAction
{
	/**
	 * Method to validate refunds
	 *
	 * @param bookingRao
	 * 		the booking rao
	 * @param ruleEngineResultRao
	 * 		the rule engine result rao
	 * @param ruleContext
	 * 		the rule context
	 * @return refund action rao
	 */
	RefundActionRAO retainAdminFee(BookingRAO bookingRao, RuleEngineResultRAO ruleEngineResultRao, Object ruleContext);

}
