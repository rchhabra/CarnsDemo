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

package de.hybris.platform.travelrulesengine.conditions;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExistsCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.rao.UserGroupRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.travelrulesengine.rao.PassengerTypeQuantityRAO;

import java.util.ArrayList;


/**
 * Translator which handles FareSearchCondition definition
 */
public class RuleSearchParamsTranslator implements RuleConditionTranslator
{
	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData ruleConditionDefinitionData) throws RuleCompilerException
	{
		final RuleParameterData userGroupParameter = condition.getParameters().get("usergroup");
		final RuleParameterData adultQuantityParameter = condition.getParameters().get("adultQuantity");
		final RuleParameterData nonAdultQuantityParameter = condition.getParameters().get("nonadultQuantity");

		if (userGroupParameter != null && adultQuantityParameter != null && nonAdultQuantityParameter != null)
		{
			final String userGroup = (String) userGroupParameter.getValue();
			final int adultQuantity = adultQuantityParameter.getValue() != null ? (int) adultQuantityParameter.getValue() : 0;
			final int nonAdultQuantity =
					nonAdultQuantityParameter.getValue() != null ? (int) nonAdultQuantityParameter.getValue() : (-1);

			if (userGroup == null)
			{
				return new RuleIrFalseCondition();
			}
			final String userRaoVariable = context.generateVariable(UserRAO.class);
			final String ptqRaoVariable = context.generateVariable(PassengerTypeQuantityRAO.class);

			final RuleIrGroupCondition irGroupCondition = new RuleIrGroupCondition();
			irGroupCondition.setOperator(RuleIrGroupOperator.OR);
			irGroupCondition.setChildren(new ArrayList<>());


			// not user group condition
			final RuleIrNotCondition irNotUserGroupCondition = new RuleIrNotCondition();
			irNotUserGroupCondition.setChildren(new ArrayList<>());

			RuleIrLocalVariablesContainer irUserGroupVarContainer = context.createLocalContainer();
			String userGroupRaoVariable = context.generateLocalVariable(irUserGroupVarContainer, UserGroupRAO.class);
			irNotUserGroupCondition.setVariablesContainer(irUserGroupVarContainer);

			final RuleIrAttributeCondition userGroupCondition = new RuleIrAttributeCondition();
			userGroupCondition.setVariable(userGroupRaoVariable);
			userGroupCondition.setAttribute("id");
			userGroupCondition.setOperator(RuleIrAttributeOperator.EQUAL);
			userGroupCondition.setValue(userGroup);

			final RuleIrAttributeRelCondition userUserGroupRelation = new RuleIrAttributeRelCondition();
			userUserGroupRelation.setVariable(userRaoVariable);
			userUserGroupRelation.setAttribute("groups");
			userUserGroupRelation.setOperator(RuleIrAttributeOperator.CONTAINS);
			userUserGroupRelation.setTargetVariable(userGroupRaoVariable);

			irNotUserGroupCondition.getChildren().add(userGroupCondition);
			irNotUserGroupCondition.getChildren().add(userUserGroupRelation);

			irGroupCondition.getChildren().add(irNotUserGroupCondition);

			//number adults != adultQuantity

			final RuleIrGroupCondition irAdultNumberContainer = new RuleIrGroupCondition();
			irAdultNumberContainer.setOperator(RuleIrGroupOperator.AND);
			irAdultNumberContainer.setChildren(new ArrayList<>());

			createPassengerQuantityCondition(adultQuantity, ptqRaoVariable, irAdultNumberContainer, "adult",
					RuleIrAttributeOperator.NOT_EQUAL);

			irGroupCondition.getChildren().add(irAdultNumberContainer);

			// number of non adults != nonAdultQuantity
			if (nonAdultQuantity >= 0)
			{
				final RuleIrGroupCondition irNonAdultContainer = new RuleIrGroupCondition();
				irNonAdultContainer.setOperator(RuleIrGroupOperator.OR);
				irNonAdultContainer.setChildren(new ArrayList<>());

				createOverAmountCondition(context, nonAdultQuantity, irNonAdultContainer, "child");
				createOverAmountCondition(context, nonAdultQuantity, irNonAdultContainer, "infant");
				if (nonAdultQuantity > 0)
				{
					createNonAdultCasesCondition(context, nonAdultQuantity, irNonAdultContainer);
				}

				irGroupCondition.getChildren().add(irNonAdultContainer);
			}

			return irGroupCondition;
		}

		return new RuleIrFalseCondition();
	}

	private void createOverAmountCondition(final RuleCompilerContext context, final int nonAdultQuantity,
			final RuleIrGroupCondition irNonAdultContainer, final String child)
	{
		final RuleIrExistsCondition irOverChildrenCaseCondition = new RuleIrExistsCondition();
		irOverChildrenCaseCondition.setChildren(new ArrayList<>());

		final RuleIrLocalVariablesContainer irOverChildrenVariableContainer = context.createLocalContainer();
		final String childPTQRaoVariable = context
				.generateLocalVariable(irOverChildrenVariableContainer, PassengerTypeQuantityRAO.class);

		irOverChildrenCaseCondition.setVariablesContainer(irOverChildrenVariableContainer);

		createPassengerQuantityCondition(nonAdultQuantity, childPTQRaoVariable, irOverChildrenCaseCondition, child,
				RuleIrAttributeOperator.GREATER_THAN);

		irNonAdultContainer.getChildren().add(irOverChildrenCaseCondition);
	}

	private void createNonAdultCasesCondition(final RuleCompilerContext context, final int nonAdultQuantity,
			final RuleIrGroupCondition irNonAdultContainer)
	{
		for (int i = 0; i <= nonAdultQuantity; i++)
		{
			final RuleIrExistsCondition irNonAdultCaseCondition = new RuleIrExistsCondition();
			irNonAdultCaseCondition.setChildren(new ArrayList<>());

			final RuleIrLocalVariablesContainer irNonAdultVariableContainer = context.createLocalContainer();
			final String childPTQRaoVariable = context
					.generateLocalVariable(irNonAdultVariableContainer, PassengerTypeQuantityRAO.class);
			final String infantPTQRaoVariable = context
					.generateLocalVariable(irNonAdultVariableContainer, PassengerTypeQuantityRAO.class);
			irNonAdultCaseCondition.setVariablesContainer(irNonAdultVariableContainer);

			createPassengerQuantityCondition(i, childPTQRaoVariable, irNonAdultCaseCondition, "child",
					RuleIrAttributeOperator.EQUAL);
			createPassengerQuantityCondition(nonAdultQuantity - i, infantPTQRaoVariable, irNonAdultCaseCondition, "infant",
					RuleIrAttributeOperator.NOT_EQUAL);

			irNonAdultContainer.getChildren().add(irNonAdultCaseCondition);

		}
	}

	private void createPassengerQuantityCondition(final int quantity, final String ptqRaoVariable, final RuleIrCondition
			container,
			final String passengerType, final RuleIrAttributeOperator operator)
	{
		final RuleIrAttributeCondition passengerTypeCondition = new RuleIrAttributeCondition();
		passengerTypeCondition.setVariable(ptqRaoVariable);
		passengerTypeCondition.setAttribute("passengerType.code");
		passengerTypeCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		passengerTypeCondition.setValue(passengerType);

		final RuleIrAttributeCondition passengerQuantityCondition = new RuleIrAttributeCondition();
		passengerQuantityCondition.setVariable(ptqRaoVariable);
		passengerQuantityCondition.setAttribute("quantity");
		passengerQuantityCondition.setOperator(operator);
		passengerQuantityCondition.setValue(quantity);

		if (container instanceof RuleIrGroupCondition)
		{
			((RuleIrGroupCondition) container).getChildren().add(passengerTypeCondition);
			((RuleIrGroupCondition) container).getChildren().add(passengerQuantityCondition);
		}
		else if (container instanceof RuleIrExistsCondition)
		{
			((RuleIrExistsCondition) container).getChildren().add(passengerTypeCondition);
			((RuleIrExistsCondition) container).getChildren().add(passengerQuantityCondition);
		}
	}
}
