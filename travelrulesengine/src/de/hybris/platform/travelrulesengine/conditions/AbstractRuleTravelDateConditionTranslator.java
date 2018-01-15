package de.hybris.platform.travelrulesengine.conditions;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.ruledefinitions.AmountOperator;
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

public abstract class AbstractRuleTravelDateConditionTranslator implements RuleConditionTranslator {

	private static final String SEARCH_PARAM_RAO_TRIP_TYPE_ATTRIBUTE = "tripType";
	private static final String SEARCH_PARAM_RAO_LEG_INFO_0_DEPARTURE_TIME_ATTRIBUTE = "legInfos[0].departureTime";
	private static final String SEARCH_PARAM_RAO_LEG_INFO_1_DEPARTURE_TIME_ATTRIBUTE = "legInfos[1].departureTime";

	private static final String OPERATOR_PARAMETER = "operator";
	private static final String DATE_PARAMETER = "date";
	
	public RuleIrCondition translate(RuleCompilerContext paramRuleCompilerContext,
			RuleConditionData condition, RuleConditionDefinitionData paramRuleConditionDefinitionData, final String contextRAOVariable) 
	{
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

		final RuleIrGroupCondition irSearchParamsCondition = new RuleIrGroupCondition();
		irSearchParamsCondition.setOperator(RuleIrGroupOperator.OR);
		irSearchParamsCondition.setChildren(new ArrayList<>());

		// RETURN TRIP GROUP CONDITION
		final RuleIrGroupCondition irReturnGroupCondition = new RuleIrGroupCondition();
		irReturnGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irReturnGroupCondition.setChildren(new ArrayList<>());

		final RuleIrAttributeCondition irTripTypeReturnCondition = new RuleIrAttributeCondition();
		irTripTypeReturnCondition.setVariable(contextRAOVariable);
		irTripTypeReturnCondition.setAttribute(SEARCH_PARAM_RAO_TRIP_TYPE_ATTRIBUTE);
		irTripTypeReturnCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irTripTypeReturnCondition.setValue(TripType.RETURN);

		final RuleIrAttributeCondition irOutboundDateReturnCondition = new RuleIrAttributeCondition();
		irOutboundDateReturnCondition.setVariable(contextRAOVariable);
		irOutboundDateReturnCondition.setAttribute(SEARCH_PARAM_RAO_LEG_INFO_0_DEPARTURE_TIME_ATTRIBUTE);
		irOutboundDateReturnCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
		irOutboundDateReturnCondition.setValue(sqlDate);

		final RuleIrAttributeCondition irInboundDateReturnCondition = new RuleIrAttributeCondition();
		irInboundDateReturnCondition.setVariable(contextRAOVariable);
		irInboundDateReturnCondition.setAttribute(SEARCH_PARAM_RAO_LEG_INFO_1_DEPARTURE_TIME_ATTRIBUTE);
		irInboundDateReturnCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
		irInboundDateReturnCondition.setValue(sqlDate);

		irReturnGroupCondition.getChildren().add(irTripTypeReturnCondition);
		irReturnGroupCondition.getChildren().add(irOutboundDateReturnCondition);
		irReturnGroupCondition.getChildren().add(irInboundDateReturnCondition);

		irSearchParamsCondition.getChildren().add(irReturnGroupCondition);

		// SINGLE TRIP GROUP CONDITION
		final RuleIrGroupCondition irSingleGroupCondition = new RuleIrGroupCondition();
		irSingleGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irSingleGroupCondition.setChildren(new ArrayList<>());

		final RuleIrAttributeCondition irTripTypeSingleCondition = new RuleIrAttributeCondition();
		irTripTypeSingleCondition.setVariable(contextRAOVariable);
		irTripTypeSingleCondition.setAttribute(SEARCH_PARAM_RAO_TRIP_TYPE_ATTRIBUTE);
		irTripTypeSingleCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irTripTypeSingleCondition.setValue(TripType.SINGLE);

		final RuleIrAttributeCondition irOutboundDateSingleCondition = new RuleIrAttributeCondition();
		irOutboundDateSingleCondition.setVariable(contextRAOVariable);
		irOutboundDateSingleCondition.setAttribute(SEARCH_PARAM_RAO_LEG_INFO_0_DEPARTURE_TIME_ATTRIBUTE);
		irOutboundDateSingleCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
		irOutboundDateSingleCondition.setValue(sqlDate);

		irSingleGroupCondition.getChildren().add(irTripTypeSingleCondition);
		irSingleGroupCondition.getChildren().add(irOutboundDateSingleCondition);

		irSearchParamsCondition.getChildren().add(irSingleGroupCondition);

		return irSearchParamsCondition;
	}

}
