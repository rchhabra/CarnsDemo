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
import de.hybris.platform.travelservices.enums.TripType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;


/**
 * Translates travel dates rule condition for CartRao
 */
public class RuleTravelDateCartConditionTranslator implements RuleConditionTranslator
{

	private static final String CART_RAO_TRIP_TYPE_ATTRIBUTE = "tripType";
	private static final String CART_RAO_LEG_INFOS = "legInfos";
	private static final String CART_RAO_LEG_INFOS_SIZE = "legInfos.size";
	private static final String CART_RAO_LEG_INFO_0_DEPARTURE_TIME_ATTRIBUTE = "legInfos[0].departureTime";
	private static final String CART_RAO_LEG_INFO_1_DEPARTURE_TIME_ATTRIBUTE = "legInfos[1].departureTime";

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
		final Instant instant = myUtilDate.toInstant();
		final ZoneId zoneId = ZoneId.of("Europe/Paris");
		final ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
		final LocalDate localDate = zdt.toLocalDate();
		final java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
		// -----------------------

		final RuleIrGroupCondition irCartCondition = new RuleIrGroupCondition();
		irCartCondition.setOperator(RuleIrGroupOperator.OR);
		irCartCondition.setChildren(new ArrayList<>());

		// RETURN TRIP GROUP CONDITION
		final RuleIrGroupCondition irReturnGroupCondition = new RuleIrGroupCondition();
		irReturnGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irReturnGroupCondition.setChildren(new ArrayList<>());

		final RuleIrAttributeCondition irTripTypeReturnCondition = new RuleIrAttributeCondition();
		irTripTypeReturnCondition.setVariable(cartRAOVariable);
		irTripTypeReturnCondition.setAttribute(CART_RAO_TRIP_TYPE_ATTRIBUTE);
		irTripTypeReturnCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irTripTypeReturnCondition.setValue(TripType.RETURN);

		final RuleIrAttributeCondition irLegInfoPresentCondition = new RuleIrAttributeCondition();
		irLegInfoPresentCondition.setVariable(cartRAOVariable);
		irLegInfoPresentCondition.setAttribute(CART_RAO_LEG_INFOS);
		irLegInfoPresentCondition.setOperator(RuleIrAttributeOperator.NOT_EQUAL);
		irLegInfoPresentCondition.setValue(null);

		final RuleIrAttributeCondition irLegInfoSizeReturnCondition = new RuleIrAttributeCondition();
		irLegInfoSizeReturnCondition.setVariable(cartRAOVariable);
		irLegInfoSizeReturnCondition.setAttribute(CART_RAO_LEG_INFOS_SIZE);
		irLegInfoSizeReturnCondition.setOperator(RuleIrAttributeOperator.GREATER_THAN);
		irLegInfoSizeReturnCondition.setValue(1);

		final RuleIrAttributeCondition irOutboundDateReturnCondition = new RuleIrAttributeCondition();
		irOutboundDateReturnCondition.setVariable(cartRAOVariable);
		irOutboundDateReturnCondition.setAttribute(CART_RAO_LEG_INFO_0_DEPARTURE_TIME_ATTRIBUTE);
		irOutboundDateReturnCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
		irOutboundDateReturnCondition.setValue(sqlDate);

		final RuleIrAttributeCondition irInboundDateReturnCondition = new RuleIrAttributeCondition();
		irInboundDateReturnCondition.setVariable(cartRAOVariable);
		irInboundDateReturnCondition.setAttribute(CART_RAO_LEG_INFO_1_DEPARTURE_TIME_ATTRIBUTE);
		irInboundDateReturnCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
		irInboundDateReturnCondition.setValue(sqlDate);

		irReturnGroupCondition.getChildren().add(irTripTypeReturnCondition);
		irReturnGroupCondition.getChildren().add(irLegInfoPresentCondition);
		irReturnGroupCondition.getChildren().add(irLegInfoSizeReturnCondition);
		irReturnGroupCondition.getChildren().add(irOutboundDateReturnCondition);
		irReturnGroupCondition.getChildren().add(irInboundDateReturnCondition);

		irCartCondition.getChildren().add(irReturnGroupCondition);

		// SINGLE TRIP GROUP CONDITION
		final RuleIrGroupCondition irSingleGroupCondition = new RuleIrGroupCondition();
		irSingleGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irSingleGroupCondition.setChildren(new ArrayList<>());

		final RuleIrAttributeCondition irTripTypeSingleCondition = new RuleIrAttributeCondition();
		irTripTypeSingleCondition.setVariable(cartRAOVariable);
		irTripTypeSingleCondition.setAttribute(CART_RAO_TRIP_TYPE_ATTRIBUTE);
		irTripTypeSingleCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irTripTypeSingleCondition.setValue(TripType.SINGLE);

		final RuleIrAttributeCondition irOutboundDateSingleCondition = new RuleIrAttributeCondition();
		irOutboundDateSingleCondition.setVariable(cartRAOVariable);
		irOutboundDateSingleCondition.setAttribute(CART_RAO_LEG_INFO_0_DEPARTURE_TIME_ATTRIBUTE);
		irOutboundDateSingleCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
		irOutboundDateSingleCondition.setValue(sqlDate);

		final RuleIrAttributeCondition irLegInfoSizeSIngleCondition = new RuleIrAttributeCondition();
		irLegInfoSizeSIngleCondition.setVariable(cartRAOVariable);
		irLegInfoSizeSIngleCondition.setAttribute(CART_RAO_LEG_INFOS_SIZE);
		irLegInfoSizeSIngleCondition.setOperator(RuleIrAttributeOperator.GREATER_THAN);
		irLegInfoSizeSIngleCondition.setValue(0);

		irSingleGroupCondition.getChildren().add(irTripTypeSingleCondition);
		irSingleGroupCondition.getChildren().add(irLegInfoPresentCondition);
		irSingleGroupCondition.getChildren().add(irLegInfoSizeSIngleCondition);
		irSingleGroupCondition.getChildren().add(irOutboundDateSingleCondition);

		irCartCondition.getChildren().add(irSingleGroupCondition);

		return irCartCondition;
	}

}
