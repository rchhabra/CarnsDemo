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

package de.hybris.platform.travelrulesengine.compiler.processors;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrProcessor;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
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
 * Creates RuleIrTypeConditions to instantiate relevant RAOs based on rule type
 */
public class TravelRuleIrProcessor implements RuleIrProcessor
{

	@Override
	public void process(final RuleCompilerContext context, final RuleIr ruleIr) throws RuleCompilerException
	{
		final AbstractRuleModel sourceRule = context.getRule();
		if (sourceRule instanceof SourceRuleModel)
		{
			final String resultRaoVariable = context.generateVariable(RuleEngineResultRAO.class);
			final RuleIrTypeCondition irResultCondition = new RuleIrTypeCondition();
			irResultCondition.setVariable(resultRaoVariable);
			ruleIr.getConditions().add(irResultCondition);

			if (sourceRule instanceof FeeSourceRuleModel)
			{
				final String cartRaoVariable = context.generateVariable(CartRAO.class);
				final RuleIrTypeCondition irCartCondition = new RuleIrTypeCondition();
				irCartCondition.setVariable(cartRaoVariable);
				ruleIr.getConditions().add(irCartCondition);
			}
			else if (sourceRule instanceof CancellationSourceRuleModel)
			{
				final String cancelBookingRaoVariable = context.generateVariable(CancelBookingRAO.class);
				final RuleIrTypeCondition irCancelBookingCondition = new RuleIrTypeCondition();
				irCancelBookingCondition.setVariable(cancelBookingRaoVariable);
				ruleIr.getConditions().add(irCancelBookingCondition);
			}
			else if (sourceRule instanceof FareFilterSourceRuleModel)
			{
				final String fareProductRaoVariable = context.generateVariable(FareProductRAO.class);
				final RuleIrTypeCondition irFareProductCondition = new RuleIrTypeCondition();
				irFareProductCondition.setVariable(fareProductRaoVariable);
				ruleIr.getConditions().add(irFareProductCondition);
			}
			else if (sourceRule instanceof BundleFilterSourceRuleModel)
			{
				final String userRaoVariable = context.generateVariable(UserRAO.class);
				final RuleIrTypeCondition irUserCondition = new RuleIrTypeCondition();
				irUserCondition.setVariable(userRaoVariable);
				ruleIr.getConditions().add(irUserCondition);

				final String fareSearchRequestRaoVariable = context.generateVariable(FareSearchRequestRAO.class);
				final RuleIrTypeCondition irFareSearchRequestCondition = new RuleIrTypeCondition();
				irFareSearchRequestCondition.setVariable(fareSearchRequestRaoVariable);
				ruleIr.getConditions().add(irFareSearchRequestCondition);
			}

		}
	}
}
