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
import de.hybris.platform.ruleengineservices.compiler.*;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.travelrulesengine.rao.FareProductRAO;
import de.hybris.platform.travelrulesengine.rao.FareSearchRequestRAO;

import java.util.ArrayList;


/**
 * Translator which handles FareTripTypeCondition definition
 */
public class RuleFareTripTypeConditionTranslator implements RuleConditionTranslator
{
	private static final String FARE_PRODUCT_RAO_CATEGORIES_ATTRIBUTE = "categories";
	private static final String CATEGORY_RAO_CODE_ATTRIBUTE = "code";
	private static final String FARE_SEARCH_RAO_TRIP_TYPE_ATTRIBUTE = "tripType";
	private static final String FARE_PRODUCT_CATEGORY_OW = "CATEGORY_OW";
	private static final String FARE_PRODUCT_CATEGORY_RT = "CATEGORY_RT";

	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData ruleConditionDefinitionData) throws RuleCompilerException
	{

		final String fareSearchRequestRaoVariable = context.generateVariable(FareSearchRequestRAO.class);
		final String fareProductRaoVariable = context.generateVariable(FareProductRAO.class);
		final String categoryRaoVariable = context.generateVariable(CategoryRAO.class);

		final RuleIrGroupCondition irFareSearchRequestCondition = new RuleIrGroupCondition();
		irFareSearchRequestCondition.setOperator(RuleIrGroupOperator.OR);
		irFareSearchRequestCondition.setChildren(new ArrayList<>());

		// RETURN TRIP GROUP CONDITION
		final RuleIrGroupCondition irReturnGroupCondition = new RuleIrGroupCondition();
		irReturnGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irReturnGroupCondition.setChildren(new ArrayList<>());

		final RuleIrAttributeCondition irTripTypeReturnCondition = new RuleIrAttributeCondition();
		irTripTypeReturnCondition.setVariable(fareSearchRequestRaoVariable);
		irTripTypeReturnCondition.setAttribute(FARE_SEARCH_RAO_TRIP_TYPE_ATTRIBUTE);
		irTripTypeReturnCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irTripTypeReturnCondition.setValue(TripType.RETURN);

		final RuleIrAttributeCondition irCategoryOWCondition = new RuleIrAttributeCondition();
		irCategoryOWCondition.setVariable(categoryRaoVariable);
		irCategoryOWCondition.setAttribute(CATEGORY_RAO_CODE_ATTRIBUTE);
		irCategoryOWCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irCategoryOWCondition.setValue(FARE_PRODUCT_CATEGORY_OW);

		final RuleIrAttributeRelCondition irProductCategoryOWRel = new RuleIrAttributeRelCondition();
		irProductCategoryOWRel.setVariable(fareProductRaoVariable);
		irProductCategoryOWRel.setAttribute(FARE_PRODUCT_RAO_CATEGORIES_ATTRIBUTE);
		irProductCategoryOWRel.setOperator(RuleIrAttributeOperator.CONTAINS);
		irProductCategoryOWRel.setTargetVariable(categoryRaoVariable);

		irReturnGroupCondition.getChildren().add(irTripTypeReturnCondition);
		irReturnGroupCondition.getChildren().add(irCategoryOWCondition);
		irReturnGroupCondition.getChildren().add(irProductCategoryOWRel);

		irFareSearchRequestCondition.getChildren().add(irReturnGroupCondition);

		// SINGLE TRIP GROUP CONDITION
		final RuleIrGroupCondition irSingleGroupCondition = new RuleIrGroupCondition();
		irSingleGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irSingleGroupCondition.setChildren(new ArrayList<>());

		final RuleIrAttributeCondition irTripTypeSingleCondition = new RuleIrAttributeCondition();
		irTripTypeSingleCondition.setVariable(fareSearchRequestRaoVariable);
		irTripTypeSingleCondition.setAttribute(FARE_SEARCH_RAO_TRIP_TYPE_ATTRIBUTE);
		irTripTypeSingleCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irTripTypeSingleCondition.setValue(TripType.SINGLE);

		final RuleIrAttributeCondition irCategoryRTCondition = new RuleIrAttributeCondition();
		irCategoryRTCondition.setVariable(categoryRaoVariable);
		irCategoryRTCondition.setAttribute(CATEGORY_RAO_CODE_ATTRIBUTE);
		irCategoryRTCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irCategoryRTCondition.setValue(FARE_PRODUCT_CATEGORY_RT);

		final RuleIrAttributeRelCondition irProductCategoryRTRel = new RuleIrAttributeRelCondition();
		irProductCategoryRTRel.setVariable(fareProductRaoVariable);
		irProductCategoryRTRel.setAttribute(FARE_PRODUCT_RAO_CATEGORIES_ATTRIBUTE);
		irProductCategoryRTRel.setOperator(RuleIrAttributeOperator.CONTAINS);
		irProductCategoryRTRel.setTargetVariable(categoryRaoVariable);

		irSingleGroupCondition.getChildren().add(irTripTypeSingleCondition);
		irSingleGroupCondition.getChildren().add(irCategoryRTCondition);
		irSingleGroupCondition.getChildren().add(irProductCategoryRTRel);

		irFareSearchRequestCondition.getChildren().add(irSingleGroupCondition);

		return irFareSearchRequestCondition;

	}
}
