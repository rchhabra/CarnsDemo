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

package de.hybris.platform.travelrulesengine.commerce.impl;

import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelrulesengine.rao.FareSearchRequestRAO;
import de.hybris.platform.travelrulesengine.rao.FilterBundleRAO;
import de.hybris.platform.travelrulesengine.rule.evaluation.DiscardBundleRAOAction;

import org.drools.core.spi.KnowledgeHelper;


/**
 * Implementation of bundle filtering action {@link DiscardBundleRAOAction}
 */
public class DefaultDiscardBundleRAOAction extends AbstractTravelCommerceRAOAction implements DiscardBundleRAOAction
{
	@Override
	public FilterBundleRAO discardBundle(final FareSearchRequestRAO fareSearchRequestRAO,
			final RuleEngineResultRAO ruleEngineResultRao, final Object ruleContext,
			final String bundleType)
	{
		final KnowledgeHelper context = this.checkAndGetRuleContext(ruleContext);
		this.validateRule(context);
		ServicesUtil.validateParameterNotNull(fareSearchRequestRAO, "Fare Search Request rao must not be null");

		final FilterBundleRAO filterBundleRAO = new FilterBundleRAO();
		filterBundleRAO.setValid(Boolean.FALSE);
		filterBundleRAO.setBundleType(bundleType);
		getRaoUtils().addAction(fareSearchRequestRAO, filterBundleRAO);
		ruleEngineResultRao.getActions().add(filterBundleRAO);
		this.setRAOMetaData(context, filterBundleRAO);
		this.insertFacts(context, filterBundleRAO);
		return filterBundleRAO;
	}
}
