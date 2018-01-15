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
import de.hybris.platform.travelrulesengine.rao.FareProductRAO;
import de.hybris.platform.travelrulesengine.rao.FilterFareRAO;

import java.util.Map;


/**
 * Implementation class of RAO action for fare filtering
 */
public class DefaultFareFilteringRAOAction extends AbstractTravelRuleRAOAction
{
	@Override
	public void performAction(final RuleActionContext ruleContext, final Map<String, Object> parameters)
	{
		validateRule(ruleContext);
		discardFare(ruleContext);

		trackRuleGroupExecutions(ruleContext);
		trackRuleExecution(ruleContext);
	}

	/**
	 * Discard fare.
	 *
	 * @param ruleContext
	 *           the rule context
	 */
	protected void discardFare(final RuleActionContext ruleContext)
	{
		final FareProductRAO fareProductRAO = ruleContext.getValue(FareProductRAO.class);
		ServicesUtil.validateParameterNotNull(fareProductRAO, "Fare product rao must not be null");
		final RuleEngineResultRAO ruleEngineResultRao = ruleContext.getRuleEngineResultRao();

		final FilterFareRAO filterFareRAO = new FilterFareRAO();
		filterFareRAO.setValid(Boolean.FALSE);
		filterFareRAO.setFareProductCode(fareProductRAO.getCode());
		getRaoUtils().addAction(fareProductRAO, filterFareRAO);
		ruleEngineResultRao.getActions().add(filterFareRAO);
		this.setRAOMetaData(ruleContext, filterFareRAO);
		ruleContext.insertFacts(ruleContext, filterFareRAO);
	}
}
