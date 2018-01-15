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

import de.hybris.platform.ruledefinitions.AmountOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * The type Rule search date cart condition translator.
 */
public class RuleSearchDateCartConditionTranslator implements RuleConditionTranslator
{
	private static final String SEARCH_PARAM_RAO_SEARCH_DATE_ATTRIBUTE = "searchDate";

	private static final String OPERATOR_PARAMETER = "operator";
	private static final String DATE_PARAMETER = "date";

	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData ruleConditionDefinitionData)
	{
		final String cartRAOVariable = context.generateVariable(CartRAO.class);
		final RuleParameterData operatorParameter = condition.getParameters().get(OPERATOR_PARAMETER);
		final RuleParameterData operatorDate = condition.getParameters().get(DATE_PARAMETER);

		final AmountOperator operator = operatorParameter.getValue();
		final java.util.Date myUtilDate = operatorDate.getValue();

		// TODO: This part of the code needs to be removed consistently with a change in the DefaultDroolsRuleValueFormatter.class that is currently using java.sql.Date

		final Calendar cal = Calendar.getInstance();
		cal.setTime(myUtilDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		final java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());

		// DATE GROUP CONDITION
		final RuleIrGroupCondition irDateGroupCondition = new RuleIrGroupCondition();
		irDateGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irDateGroupCondition.setChildren(new ArrayList<>());

		final RuleIrAttributeCondition irDateCondition = new RuleIrAttributeCondition();
		irDateCondition.setVariable(cartRAOVariable);
		irDateCondition.setAttribute(SEARCH_PARAM_RAO_SEARCH_DATE_ATTRIBUTE);
		irDateCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
		irDateCondition.setValue(sqlDate);

		irDateGroupCondition.getChildren().add(irDateCondition);

		return irDateGroupCondition;
	}
}
