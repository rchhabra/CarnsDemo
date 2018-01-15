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
import de.hybris.platform.travelrulesengine.rao.FilterTransportOfferingRAO;
import de.hybris.platform.travelrulesengine.rao.TransportOfferingRAO;

import java.util.Map;


/**
 * Implementation class of RAO action for transport offering filtering
 */
public class DefaultTransportOfferingFilteringRAOAction extends AbstractTravelRuleRAOAction
{
	@Override
	public void performAction(final RuleActionContext ruleContext, final Map<String, Object> parameters)
	{
		validateRule(ruleContext);
		discardTransportOffering(ruleContext);

		trackRuleGroupExecutions(ruleContext);
		trackRuleExecution(ruleContext);
	}

	/**
	 * Discard transport offering.
	 *
	 * @param ruleContext
	 *           the rule context
	 */
	protected void discardTransportOffering(final RuleActionContext ruleContext)
	{
		final TransportOfferingRAO transportOfferingRAO = ruleContext.getValue(TransportOfferingRAO.class);
		ServicesUtil.validateParameterNotNull(transportOfferingRAO, "TransportOffering rao must not be null");
		final RuleEngineResultRAO ruleEngineResultRao = ruleContext.getRuleEngineResultRao();
		final FareSearchRequestRAO fareSearchRequestRAO = ruleContext.getValue(FareSearchRequestRAO.class);

		final FilterTransportOfferingRAO filterTransportOfferingRAO = new FilterTransportOfferingRAO();
		filterTransportOfferingRAO.setValid(Boolean.FALSE);
		filterTransportOfferingRAO.setTransportOfferingCode(transportOfferingRAO.getTransportOfferingCode());
		getRaoUtils().addAction(fareSearchRequestRAO, filterTransportOfferingRAO);
		ruleEngineResultRao.getActions().add(filterTransportOfferingRAO);
		this.setRAOMetaData(ruleContext, filterTransportOfferingRAO);
		ruleContext.insertFacts(ruleContext, filterTransportOfferingRAO);
	}
}
