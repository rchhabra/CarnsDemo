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
import de.hybris.platform.ruledefinitions.conditions.builders.RuleIrAttributeConditionBuilder;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrVariable;
import de.hybris.platform.ruleengineservices.compiler.RuleIrVariablesContainer;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.travelrulesengine.rao.PassengerTypeQuantityRAO;

import java.util.ArrayList;


/**
 * The type Abstract rule passenger type quantity condition translator.
 */
public abstract class AbstractRulePassengerTypeQuantityConditionTranslator implements RuleConditionTranslator
{
	private static final String PASSENGER_TYPE_PARAMETER = "ptc";
	private static final String QUANTITY_PARAMETER = "quantity";
	private static final String OPERATOR_PARAMETER = "operator";

	private static final String QUANTITY_ATTRIBUTE = "quantity";
	private static final String PASSENGER_TYPE_ATTRIBUTE = "passengerType.code";

	/**
	 * Generic passenger type quantity condition translator rule ir condition.
	 *
	 * @param context
	 * 		the context
	 * @param condition
	 * 		the condition
	 * @param ruleConditionDefinitionData
	 * 		the rule condition definition data
	 * @param contextRAOVariable
	 * 		the context RAO Variable
	 * @return the rule ir condition
	 */
	protected RuleIrCondition translate(final RuleCompilerContext context,
			final RuleConditionData condition, final RuleConditionDefinitionData ruleConditionDefinitionData,
			final String contextRAOVariable)
	{
		final RuleParameterData passengerTypeParameter = condition.getParameters().get(PASSENGER_TYPE_PARAMETER);
		final RuleParameterData quantityParameter = condition.getParameters().get(QUANTITY_PARAMETER);
		final RuleParameterData operatorParameter = condition.getParameters().get(OPERATOR_PARAMETER);

		final String passengerType = passengerTypeParameter.getValue();
		final Integer quantity = quantityParameter.getValue();
		final AmountOperator operator = operatorParameter.getValue();

		final RuleIrGroupCondition irPassengerTypeQuantityGroupCondition = new RuleIrGroupCondition();
		irPassengerTypeQuantityGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irPassengerTypeQuantityGroupCondition.setChildren(new ArrayList<>());

		final RuleIrGroupCondition irPassengerTypeQuantityCondition = new RuleIrGroupCondition();
		irPassengerTypeQuantityCondition.setOperator(RuleIrGroupOperator.AND);
		irPassengerTypeQuantityCondition.setChildren(new ArrayList<>());

		final boolean isPresent = context.getVariablesGenerator().getCurrentContainer().getVariables()
				.entrySet().stream().anyMatch(entry -> PassengerTypeQuantityRAO.class.equals(entry.getValue().getType()));
		final String containsPassengerTypeRaoVariable;

		if (isPresent)
		{
			final RuleIrLocalVariablesContainer ruleIrLocalVariablesContainer = context.createLocalContainer();
			containsPassengerTypeRaoVariable = context
					.generateLocalVariable(ruleIrLocalVariablesContainer, PassengerTypeQuantityRAO.class);

			final RuleIrVariablesContainer container = context.getVariablesGenerator().getCurrentContainer();
			final RuleIrVariable variable = new RuleIrVariable();
			variable.setName(containsPassengerTypeRaoVariable);
			variable.setType(PassengerTypeQuantityRAO.class);
			variable.setPath(container.getPath());
			container.getVariables().put(containsPassengerTypeRaoVariable, variable);
		}
		else
		{
			containsPassengerTypeRaoVariable = context.generateVariable(PassengerTypeQuantityRAO.class);
		}

		final RuleIrAttributeCondition irContainsPassengerTypeCondition = RuleIrAttributeConditionBuilder
				.newAttributeConditionFor(containsPassengerTypeRaoVariable).withAttribute(PASSENGER_TYPE_ATTRIBUTE)
				.withOperator(RuleIrAttributeOperator.EQUAL).withValue(passengerType).build();

		final RuleIrAttributeCondition irContainsPassengerQuantityCondition = RuleIrAttributeConditionBuilder
				.newAttributeConditionFor(containsPassengerTypeRaoVariable).withAttribute(QUANTITY_ATTRIBUTE)
				.withOperator(RuleIrAttributeOperator.valueOf(operator.name())).withValue(quantity).build();

		irPassengerTypeQuantityCondition.getChildren().add(irContainsPassengerTypeCondition);
		irPassengerTypeQuantityCondition.getChildren().add(irContainsPassengerQuantityCondition);

		final RuleIrTypeCondition irSearchParamCondition = new RuleIrTypeCondition();
		irSearchParamCondition.setVariable(contextRAOVariable);

		irPassengerTypeQuantityGroupCondition.getChildren().add(irPassengerTypeQuantityCondition);
		irPassengerTypeQuantityGroupCondition.getChildren().add(irSearchParamCondition);

		return irPassengerTypeQuantityGroupCondition;
	}
}
