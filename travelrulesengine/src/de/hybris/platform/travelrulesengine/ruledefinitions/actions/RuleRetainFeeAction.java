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

package de.hybris.platform.travelrulesengine.ruledefinitions.actions;

import de.hybris.platform.ruledefinitions.actions.DefaultRuleExecutableAction;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleEvaluationException;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleExecutableAction;
import de.hybris.platform.travelrulesengine.rao.CancelBookingRAO;
import de.hybris.platform.travelrulesengine.rule.evaluation.RetainFeeRAOAction;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Action responsible for linking it's definition with RetainFeeRAOAction
 *
 * @deprecated Deprecated since version 3.0. Use {@link DefaultRuleExecutableAction}.
 */
@Deprecated
public class RuleRetainFeeAction implements RuleExecutableAction
{
	private RetainFeeRAOAction retainFeeRAOAction;

	@Override
	public void executeAction(final RuleActionContext context, final Map<String, Object> map)
			throws RuleEvaluationException
	{
		final RuleEngineResultRAO result = context.getValue(RuleEngineResultRAO.class);
		final CancelBookingRAO cancelBookingRAO = context.getValue(CancelBookingRAO.class);
		getRetainFeeRAOAction().retainAdminFee(cancelBookingRAO.getBooking(), result, context.getDelegate());
	}

	public RetainFeeRAOAction getRetainFeeRAOAction()
	{
		return retainFeeRAOAction;
	}

	@Required
	public void setRetainFeeRAOAction(final RetainFeeRAOAction retainFeeRAOAction)
	{
		this.retainFeeRAOAction = retainFeeRAOAction;
	}
}
