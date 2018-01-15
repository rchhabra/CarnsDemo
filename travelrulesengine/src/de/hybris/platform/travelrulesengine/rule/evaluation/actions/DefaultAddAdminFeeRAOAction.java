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

import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelrulesengine.rao.FeeRAO;

import java.util.Map;
import java.util.Objects;


/**
 * Implementation class of RAO action for adding admin fee
 */
public class DefaultAddAdminFeeRAOAction extends AbstractTravelRuleRAOAction
{
	@Override
	public void performAction(final RuleActionContext ruleContext, final Map<String, Object> parameters)
	{
		validateRule(ruleContext);
		addAdminFee(ruleContext);

		trackRuleGroupExecutions(ruleContext);
		trackRuleExecution(ruleContext);
	}

	/**
	 * Adds the admin fee.
	 *
	 * @param ruleContext
	 *           the rule context
	 */
	protected void addAdminFee(final RuleActionContext ruleContext)
	{
		final CartRAO cartRao = ruleContext.getCartRao();
		final RuleEngineResultRAO ruleEngineResultRao = ruleContext.getRuleEngineResultRao();

		ServicesUtil.validateParameterNotNull(cartRao, "cart rao must not be null");

		final FeeRAO feeRao = this.getTravelRuleEngineCalculationService().addFee(cartRao);
		if (Objects.nonNull(feeRao))
		{
			setRAOMetaData(ruleContext, feeRao);
			ruleEngineResultRao.getActions().add(feeRao);
			ruleContext.insertFacts(ruleContext, feeRao);
		}

	}
}
