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

import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.ruledefinitions.AmountOperator;
import de.hybris.platform.ruleengineservices.compiler.*;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.travelrulesengine.rao.FareProductRAO;
import de.hybris.platform.travelrulesengine.rao.FareSearchRequestRAO;

import java.util.ArrayList;

/**
 * Abstract Fare Category Condition for generic use of various FareCategory Translators
 */
public abstract class AbstractFareCategoryConditionTranslator implements RuleConditionTranslator
{
	private static final String OPERATOR_PARAMETER = "operator";
	private static final String CATEGORY_PARAMETER = "category";
	private static final String CATEGORY_RAO_CODE_ATTRIBUTE = "code";
	private static final String FARE_PRODUCT_RAO_CATEGORIES_ATTRIBUTE = "categories";
	private static final String FARE_SEARCH_RAO_TRIP_TYPE_ATTRIBUTE = "tripType";

	/**
	 * Makes translator more generic so it can be used in different types of categories
	 * @param context
	 * @param condition
	 * @param parameter
	 * @param forceReturn
	 * @return RuleIrCondition
	 * @throws RuleCompilerException
	 */
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition, final String parameter,
			final Boolean forceReturn) throws RuleCompilerException
	{
		final RuleParameterData operatorParameter = condition.getParameters().get(OPERATOR_PARAMETER);
		final RuleParameterData valueParameter = condition.getParameters().get(parameter);
		final RuleParameterData categoryParameter = condition.getParameters().get(CATEGORY_PARAMETER);

		if (operatorParameter != null && valueParameter != null && categoryParameter != null)
		{
			final AmountOperator operator = (AmountOperator) operatorParameter.getValue();
			final Integer value = (Integer) valueParameter.getValue();
			final String category = (String) categoryParameter.getValue();

			if (operator != null && value != null && category != null)
			{
				final RuleIrGroupCondition irGroupCondition = new RuleIrGroupCondition();
				irGroupCondition.setOperator(RuleIrGroupOperator.AND);
				irGroupCondition.setChildren(new ArrayList<>());

				final String fareProductRaoVariable = context.generateVariable(FareProductRAO.class);
				final String fareSearchRequestRaoVariable = context.generateVariable(FareSearchRequestRAO.class);
				final String categoryRaoVariable = context.generateVariable(CategoryRAO.class);

				final RuleIrAttributeCondition categoryCondition = new RuleIrAttributeCondition();
				categoryCondition.setVariable(categoryRaoVariable);
				categoryCondition.setAttribute(CATEGORY_RAO_CODE_ATTRIBUTE);
				categoryCondition.setOperator(RuleIrAttributeOperator.EQUAL);
				categoryCondition.setValue(category);

				final RuleIrAttributeRelCondition categoryFareProductRelCondition = new RuleIrAttributeRelCondition();
				categoryFareProductRelCondition.setVariable(fareProductRaoVariable);
				categoryFareProductRelCondition.setAttribute(FARE_PRODUCT_RAO_CATEGORIES_ATTRIBUTE);
				categoryFareProductRelCondition.setOperator(RuleIrAttributeOperator.CONTAINS);
				categoryFareProductRelCondition.setTargetVariable(categoryRaoVariable);

				if (forceReturn)
				{
					final RuleIrAttributeCondition tripTypeReturnCondition = new RuleIrAttributeCondition();
					tripTypeReturnCondition.setVariable(fareSearchRequestRaoVariable);
					tripTypeReturnCondition.setAttribute(FARE_SEARCH_RAO_TRIP_TYPE_ATTRIBUTE);
					tripTypeReturnCondition.setOperator(RuleIrAttributeOperator.EQUAL);
					tripTypeReturnCondition.setValue(TripType.RETURN);
					irGroupCondition.getChildren().add(tripTypeReturnCondition);
				}

				final RuleIrAttributeCondition valueCondition = new RuleIrAttributeCondition();
				valueCondition.setVariable(fareSearchRequestRaoVariable);
				valueCondition.setAttribute(parameter);
				valueCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
				valueCondition.setValue(value);

				irGroupCondition.getChildren().add(categoryCondition);
				irGroupCondition.getChildren().add(categoryFareProductRelCondition);

				irGroupCondition.getChildren().add(valueCondition);

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
