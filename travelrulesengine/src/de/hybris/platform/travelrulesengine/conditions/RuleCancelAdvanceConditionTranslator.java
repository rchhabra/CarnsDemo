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

import de.hybris.platform.ruledefinitions.AmountOperator;
import de.hybris.platform.ruleengineservices.compiler.*;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.travelrulesengine.rao.CancelBookingRAO;

import java.util.ArrayList;


/**
 * Translators which handles advance cancellation condition definition
 */
public class RuleCancelAdvanceConditionTranslator implements RuleConditionTranslator
{
	private static final String OPERATOR_PARAMETER = "operator";
	private static final String CANCELLATION_ADVANCE_PARAMETER = "cancellationAdvance";
	private static final String CANCEL_BOOKING_RAO_ADVANCE_ATTRIBUTE = "advanceCancellationDays";

	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData ruleConditionDefinitionData) throws RuleCompilerException
	{
		final RuleParameterData operatorParameter = condition.getParameters().get(OPERATOR_PARAMETER);
		final RuleParameterData cancellationAdvanceParameter = condition.getParameters().get(CANCELLATION_ADVANCE_PARAMETER);

		if (operatorParameter != null && cancellationAdvanceParameter != null)
		{
			final AmountOperator operator = (AmountOperator) operatorParameter.getValue();
			final Integer cancellationAdvance = (Integer) cancellationAdvanceParameter.getValue();

			if (operator != null && cancellationAdvance != null)
			{
				final RuleIrGroupCondition irGroupCondition = new RuleIrGroupCondition();
				irGroupCondition.setOperator(RuleIrGroupOperator.AND);
				irGroupCondition.setChildren(new ArrayList<>());

				final String cancelBookingRaoVariable = context.generateVariable(CancelBookingRAO.class);

				final RuleIrAttributeCondition advanceCondition = new RuleIrAttributeCondition();
				advanceCondition.setVariable(cancelBookingRaoVariable);
				advanceCondition.setAttribute(CANCEL_BOOKING_RAO_ADVANCE_ATTRIBUTE);
				advanceCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
				advanceCondition.setValue(cancellationAdvance);

				irGroupCondition.getChildren().add(advanceCondition);

				return irGroupCondition;
			}
			else
			{
				return new RuleIrFalseCondition();
			}
		}
		else
		{
			return new RuleIrFalseCondition();
		}
	}
}
