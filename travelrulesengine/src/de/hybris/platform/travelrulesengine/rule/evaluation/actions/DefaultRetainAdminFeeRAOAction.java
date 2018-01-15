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

import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelrulesengine.rao.BookingRAO;
import de.hybris.platform.travelrulesengine.rao.RefundActionRAO;

import java.util.Map;
import java.util.Objects;


/**
 * Implementation class of RAO action for retaining Fee
 */
public class DefaultRetainAdminFeeRAOAction extends AbstractTravelRuleRAOAction
{
	@Override
	public void performAction(final RuleActionContext ruleContext, final Map<String, Object> parameters)
	{
		validateRule(ruleContext);
		retainAdminFee(ruleContext);

		trackRuleGroupExecutions(ruleContext);
		trackRuleExecution(ruleContext);
	}

	/**
	 * Retain admin fee.
	 *
	 * @param ruleContext
	 *           the rule context
	 */
	protected void retainAdminFee(final RuleActionContext ruleContext)
	{
		final BookingRAO bookingRao = ruleContext.getValue(BookingRAO.class);
		ServicesUtil.validateParameterNotNull(bookingRao, "booking rao must not be null");
		final RuleEngineResultRAO ruleEngineResultRao = ruleContext.getRuleEngineResultRao();
		final RefundActionRAO refundActionRao = this.getTravelRuleEngineCalculationService().addRefundFeeAction(bookingRao);
		if (Objects.nonNull(refundActionRao))
		{
			setRAOMetaData(ruleContext, refundActionRao);
			ruleEngineResultRao.getActions().add(refundActionRao);
			ruleContext.insertFacts(ruleContext, refundActionRao);
		}
	}
}
