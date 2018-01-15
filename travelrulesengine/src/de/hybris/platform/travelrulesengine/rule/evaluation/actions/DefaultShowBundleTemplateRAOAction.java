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
import de.hybris.platform.travelrulesengine.rao.SearchParamsRAO;
import de.hybris.platform.travelrulesengine.rao.ShowBundleTemplatesRAO;

import java.util.ArrayList;
import java.util.Map;


/**
 * The type Default show bundle template rao action.
 */
public class DefaultShowBundleTemplateRAOAction extends AbstractTravelRuleRAOAction
{
	@Override
	public void performAction(final RuleActionContext ruleContext, final Map<String, Object> parameters)
	{
		validateRule(ruleContext);
		final ArrayList<String> bundleTemplates = (ArrayList<String>) parameters.get("bundle_templates");
		showBundle(ruleContext, bundleTemplates);

		trackRuleGroupExecutions(ruleContext);
		trackRuleExecution(ruleContext);

	}

	/**
	 * Discard bundle.
	 *
	 * @param ruleContext
	 * 		the rule context
	 * @param bundleTemplates
	 * 		the bundle Templates
	 */
	protected void showBundle(final RuleActionContext ruleContext, final ArrayList<String> bundleTemplates)
	{
		final SearchParamsRAO searchParamsRAO = ruleContext.getValue(SearchParamsRAO.class);
		ServicesUtil.validateParameterNotNull(searchParamsRAO, "Search param rao must not be null");
		final RuleEngineResultRAO ruleEngineResultRao = ruleContext.getRuleEngineResultRao();

		final ShowBundleTemplatesRAO showBundleTemplatesRAO = new ShowBundleTemplatesRAO();
		showBundleTemplatesRAO.setBundleTemplates(bundleTemplates);
		getRaoUtils().addAction(searchParamsRAO, showBundleTemplatesRAO);
		ruleEngineResultRao.getActions().add(showBundleTemplatesRAO);
		setRAOMetaData(ruleContext, showBundleTemplatesRAO);
		ruleContext.insertFacts(ruleContext, showBundleTemplatesRAO);
	}
}
