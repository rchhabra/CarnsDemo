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
 *
 */

package de.hybris.platform.travelrulesengine.rule.evaluation.actions;

import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;
import de.hybris.platform.travelrulesengine.rao.ShowProductsActionRAO;

import java.util.ArrayList;
import java.util.Map;


/**
 * Action to be executed on 'show products based on conditions' rule evaluation
 *
 */
public class DefaultShowProductRAOAction extends AbstractTravelRuleRAOAction
{

	@Override
	public void performAction(final RuleActionContext ruleContext, final Map<String, Object> parameters)
	{
		validateRule(ruleContext);
		final ArrayList<String> products = (ArrayList<String>) parameters.get("products");
		showProducts(ruleContext, products);

		trackRuleGroupExecutions(ruleContext);
		trackRuleExecution(ruleContext);
	}

	protected void showProducts(final RuleActionContext ruleContext, final ArrayList<String> products)
	{
		final OfferRequestRAO offerRequestRAO = ruleContext.getValue(OfferRequestRAO.class);
		ServicesUtil.validateParameterNotNull(offerRequestRAO, "Offer request rao must not be null");
		final RuleEngineResultRAO ruleEngineResultRao = ruleContext.getRuleEngineResultRao();

		final ShowProductsActionRAO showProductsActionRAO = new ShowProductsActionRAO();
		showProductsActionRAO.setProducts(products);
		getRaoUtils().addAction(offerRequestRAO, showProductsActionRAO);
		ruleEngineResultRao.getActions().add(showProductsActionRAO);
		setRAOMetaData(ruleContext, showProductsActionRAO);
		ruleContext.insertFacts(ruleContext, showProductsActionRAO);
	}

}
