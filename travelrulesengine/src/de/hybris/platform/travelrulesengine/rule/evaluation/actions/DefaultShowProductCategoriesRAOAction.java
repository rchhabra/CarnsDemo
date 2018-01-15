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
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;
import de.hybris.platform.travelrulesengine.rao.ShowProductCategoriesActionRAO;

import java.util.ArrayList;
import java.util.Map;


/**
 * Rule Action executed as part of show product category drools rule
 */
public class DefaultShowProductCategoriesRAOAction extends AbstractTravelRuleRAOAction {

	@Override
	public void performAction(final RuleActionContext ruleContext, final Map<String, Object> parameters)
	{
		validateRule(ruleContext);
		final ArrayList<String> categories = (ArrayList<String>) parameters.get("categories");
		showProductCategories(ruleContext, categories);

		trackRuleGroupExecutions(ruleContext);
		trackRuleExecution(ruleContext);
	}

	private void showProductCategories(final RuleActionContext ruleContext, final ArrayList<String> categories)
	{
		final OfferRequestRAO offerRequestRAO = ruleContext.getValue(OfferRequestRAO.class);
		ServicesUtil.validateParameterNotNull(offerRequestRAO, "Offer request rao must not be null");
		final RuleEngineResultRAO ruleEngineResultRao = ruleContext.getRuleEngineResultRao();

		final ShowProductCategoriesActionRAO showProductCategoriesActionRAO = new ShowProductCategoriesActionRAO();
		showProductCategoriesActionRAO.setCategories(categories);
		getRaoUtils().addAction(offerRequestRAO, showProductCategoriesActionRAO);
		ruleEngineResultRao.getActions().add(showProductCategoriesActionRAO);
		setRAOMetaData(ruleContext, showProductCategoriesActionRAO);
		ruleContext.insertFacts(ruleContext, showProductCategoriesActionRAO);
	}

}
