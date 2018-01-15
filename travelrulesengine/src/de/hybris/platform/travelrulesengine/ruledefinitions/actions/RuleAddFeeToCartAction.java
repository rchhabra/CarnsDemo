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
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleEvaluationException;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleExecutableAction;
import de.hybris.platform.travelrulesengine.rule.evaluation.AdminFeeRAOAction;

import java.util.Map;


/**
 * @deprecated Deprecated since version 3.0. Use {@link DefaultRuleExecutableAction}.
 */
@Deprecated
public class RuleAddFeeToCartAction implements RuleExecutableAction
{

	private AdminFeeRAOAction adminFeeRAOAction;

	public RuleAddFeeToCartAction()
	{
	}

	public void executeAction(final RuleActionContext context, final Map<String, Object> parameters)
			throws RuleEvaluationException
	{
		final RuleEngineResultRAO result = context.getValue(RuleEngineResultRAO.class);
		final CartRAO cart = context.getValue(CartRAO.class);
		getAdminFeeRAOAction().addAdminFee(cart, result, context.getDelegate());
	}

	/**
	 * @return the adminFeeRAOAction
	 */
	public AdminFeeRAOAction getAdminFeeRAOAction()
	{
		return adminFeeRAOAction;
	}

	/**
	 * @param adminFeeRAOAction
	 *           the adminFeeRAOAction to set
	 */
	public void setAdminFeeRAOAction(final AdminFeeRAOAction adminFeeRAOAction)
	{
		this.adminFeeRAOAction = adminFeeRAOAction;
	}


}
