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
import de.hybris.platform.travelrulesengine.rao.FareSearchRequestRAO;
import de.hybris.platform.travelrulesengine.rao.FilterBundleRAO;

import java.util.Map;



/**
 * The type Default bundle filtering rao action.
 * @deprecated since version 4.0
 */
@Deprecated
public class DefaultBundleFilteringRAOAction extends AbstractTravelRuleRAOAction
{
	@Override
	public void performAction(final RuleActionContext ruleContext, final Map<String, Object> parameters)
	{
		validateRule(ruleContext);
		final String bundleType = (String) parameters.get("bundle_type");
		discardBundle(ruleContext, bundleType);

		trackRuleGroupExecutions(ruleContext);
		trackRuleExecution(ruleContext);

	}

	/**
	 * Discard bundle.
	 *
	 * @param ruleContext
	 * 		the rule context
	 * @param bundleType
	 * 		the bundle type
	 */
	protected void discardBundle(final RuleActionContext ruleContext, final String bundleType)
	{
		final FareSearchRequestRAO fareSearchRequestRAO = ruleContext.getValue(FareSearchRequestRAO.class);
		ServicesUtil.validateParameterNotNull(fareSearchRequestRAO, "Fare Search Request rao must not be null");
		final RuleEngineResultRAO ruleEngineResultRao = ruleContext.getRuleEngineResultRao();

		final FilterBundleRAO filterBundleRAO = new FilterBundleRAO();
		filterBundleRAO.setValid(Boolean.FALSE);
		filterBundleRAO.setBundleType(bundleType);
		getRaoUtils().addAction(fareSearchRequestRAO, filterBundleRAO);
		ruleEngineResultRao.getActions().add(filterBundleRAO);
		setRAOMetaData(ruleContext, filterBundleRAO);
		ruleContext.insertFacts(ruleContext, filterBundleRAO);
	}
}
