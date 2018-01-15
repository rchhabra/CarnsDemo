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

package de.hybris.platform.travelrulesengine.conditions;

import java.util.ArrayList;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;


/**
 * Abstract class for creating a channel based group condition
 */
public abstract class AbstractSalesApplicationConditionTranslator implements RuleConditionTranslator
{
	private static final String SEARCH_PARAM_SALES_APPLICATION_ATTRIBUTE = "salesApplication";
	private static final String CHANNEL_PARAMETER = "channel";
	
	protected RuleIrCondition translate(final RuleCompilerContext context,
			final RuleConditionData condition, final RuleConditionDefinitionData ruleConditionDefinitionData,
			final String contextRAOVariable)
	{
		final RuleParameterData salesApplicationParameter = condition.getParameters().get(CHANNEL_PARAMETER);
		final String salesApplicationCode = salesApplicationParameter.getValue();

		final RuleIrGroupCondition irGroupCondition = new RuleIrGroupCondition();
		irGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irGroupCondition.setChildren(new ArrayList<>());

		createGroupChannelAndCondition(salesApplicationCode, SEARCH_PARAM_SALES_APPLICATION_ATTRIBUTE, contextRAOVariable,
				irGroupCondition, RuleIrAttributeOperator.EQUAL);
		return irGroupCondition;
	}
	
	protected void createGroupChannelAndCondition(final String salesApp, final String attribute, final String raoVariable,
			final RuleIrGroupCondition irGroupCondition, final RuleIrAttributeOperator attributeOperator)
	{
		// SALES APPLICATION CONDITION
		final RuleIrAttributeCondition irSalesApplicationCondition = new RuleIrAttributeCondition();
		irSalesApplicationCondition.setVariable(raoVariable);
		irSalesApplicationCondition.setAttribute(attribute);
		irSalesApplicationCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irSalesApplicationCondition.setValue(salesApp);

		irGroupCondition.getChildren().add(irSalesApplicationCondition);
	}
}
