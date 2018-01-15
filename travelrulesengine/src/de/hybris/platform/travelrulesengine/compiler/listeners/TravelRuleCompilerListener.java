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

package de.hybris.platform.travelrulesengine.compiler.listeners;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerListener;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.travelrulesengine.model.BundleFilterSourceRuleModel;
import de.hybris.platform.travelrulesengine.model.CancellationSourceRuleModel;
import de.hybris.platform.travelrulesengine.model.FareFilterSourceRuleModel;
import de.hybris.platform.travelrulesengine.model.FeeSourceRuleModel;
import de.hybris.platform.travelrulesengine.rao.CancelBookingRAO;
import de.hybris.platform.travelrulesengine.rao.FareProductRAO;
import de.hybris.platform.travelrulesengine.rao.FareSearchRequestRAO;


/**
 * Generates relevant RAOs based on rule type
 */
public class TravelRuleCompilerListener implements RuleCompilerListener
{

	@Override
	public void beforeCompile(final RuleCompilerContext context) throws RuleCompilerException
	{
		if (!(context.getRule() instanceof SourceRuleModel))
		{
			return;
		}

		context.generateVariable(RuleEngineResultRAO.class);

		if (context.getRule() instanceof FeeSourceRuleModel)
		{
			context.generateVariable(CartRAO.class);
		}
		else if (context.getRule() instanceof CancellationSourceRuleModel)
		{
			context.generateVariable(CancelBookingRAO.class);
		}
		else if (context.getRule() instanceof FareFilterSourceRuleModel)
		{
			context.generateVariable(FareProductRAO.class);
		}
		else if (context.getRule() instanceof BundleFilterSourceRuleModel)
		{
			context.generateVariable(UserRAO.class);
			context.generateVariable(FareSearchRequestRAO.class);
		}

	}

	@Override
	public void afterCompile(final RuleCompilerContext context) throws RuleCompilerException
	{
		// DO NOTHING
	}

	@Override
	public void afterCompileError(final RuleCompilerContext context) throws RuleCompilerException
	{
		// DO NOTHING
	}

}
