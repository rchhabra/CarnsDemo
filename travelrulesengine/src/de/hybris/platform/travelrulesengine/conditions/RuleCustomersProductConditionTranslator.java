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

import de.hybris.platform.ruledefinitions.CollectionOperator;
import de.hybris.platform.ruledefinitions.conditions.RuleTargetCustomersConditionTranslator;
import de.hybris.platform.ruledefinitions.conditions.builders.RuleIrGroupConditionBuilder;
import de.hybris.platform.ruledefinitions.conditions.builders.RuleIrNotConditionBuilder;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;


/**
 * This class translates customer and customer group condition for product search
 */
public class RuleCustomersProductConditionTranslator extends AbstractRuleCustomersConditionTranslator
{
	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData conditionDefinition)
	{
		return super.translate(context, condition, conditionDefinition);
	}

	@Override
	protected void addTargetCustomersConditions(final RuleCompilerContext context, final CollectionOperator customerGroupsOperator,
			final List<String> customerGroups, final List<String> customers, final RuleIrGroupCondition irTargetCustomersCondition)
	{
		final String offerRequestVariable = context.generateVariable(OfferRequestRAO.class);
		super.addTargetCustomersConditions(context, customerGroupsOperator, customerGroups, customers, irTargetCustomersCondition, offerRequestVariable);
	}
}
