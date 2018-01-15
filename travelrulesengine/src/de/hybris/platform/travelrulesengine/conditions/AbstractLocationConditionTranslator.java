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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * The type Abstract location condition translator.
 */
public abstract class AbstractLocationConditionTranslator implements RuleConditionTranslator
{
	private static final String INCLUDED_ORIGIN_LOCATION_PARAMETER = "inclDepLocations";
	private static final String EXCLUDED_ORIGIN_LOCATION_PARAMETER = "excDepLocations";
	private static final String INCLUDED_DESTINATION_LOCATION_PARAMETER = "inclArrLocations";
	private static final String EXCLUDED_DESTINATION_LOCATION_PARAMETER = "excArrLocations";

	private static final String ORIGIN_LOCATIONS_ATTRIBUTE = "originLocations";
	private static final String DESTINATION_LOCATIONS_ATTRIBUTE = "destinationLocations";
	
	private static final String INCLUDED_MARKET_LOCATION_PARAMETER = "inclMarketLocations";
	private static final String EXCLUDED_MARKET_LOCATION_PARAMETER = "excMarketLocations";

	private static final String ORIGINATING_LOCATIONS_ATTRIBUTE = "originatingLocations";
	
	
	public RuleIrCondition translateMarketLocationCondition(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData ruleConditionDefinitionData, final String contextRaoVariable)
	{
		final RuleParameterData inclMarketLocationParameter = condition.getParameters().get(INCLUDED_MARKET_LOCATION_PARAMETER);
		final RuleParameterData excMarketLocationParameter = condition.getParameters().get(EXCLUDED_MARKET_LOCATION_PARAMETER);

		final List<String> inclMarketLocations = inclMarketLocationParameter.getValue();
		final List<String> excMarketLocations = excMarketLocationParameter.getValue();

		final RuleIrGroupCondition irLocationCondition = new RuleIrGroupCondition();
		irLocationCondition.setOperator(RuleIrGroupOperator.AND);
		irLocationCondition.setChildren(new ArrayList<>());

		// INCLUDED MARKET LOCATIONS
		createGroupLocationOrCondition(inclMarketLocations, ORIGINATING_LOCATIONS_ATTRIBUTE, contextRaoVariable, irLocationCondition,
				RuleIrAttributeOperator.CONTAINS);

		// EXCLUDED MARKET LOCATIONS
		createGroupLocationAndCondition(excMarketLocations, ORIGINATING_LOCATIONS_ATTRIBUTE, contextRaoVariable, irLocationCondition,
				RuleIrAttributeOperator.NOT_CONTAINS);

		return irLocationCondition;
	}
	
	public RuleIrCondition translate(RuleCompilerContext paramRuleCompilerContext,
			RuleConditionData condition, RuleConditionDefinitionData paramRuleConditionDefinitionData, final String contextRAOVariable) 
	{
		final RuleParameterData inclOriLocationParameter = condition.getParameters().get(INCLUDED_ORIGIN_LOCATION_PARAMETER);
		final RuleParameterData excOriLocationParameter = condition.getParameters().get(EXCLUDED_ORIGIN_LOCATION_PARAMETER);
		final RuleParameterData inclDesLocationParameter = condition.getParameters().get(INCLUDED_DESTINATION_LOCATION_PARAMETER);
		final RuleParameterData excDesLocationParameter = condition.getParameters().get(EXCLUDED_DESTINATION_LOCATION_PARAMETER);

		final List<String> inclOriLocations = inclOriLocationParameter.getValue();
		final List<String> excOriLocations = excOriLocationParameter.getValue();
		final List<String> inclDesLocations = inclDesLocationParameter.getValue();
		final List<String> excDesLocations = excDesLocationParameter.getValue();

		final RuleIrGroupCondition irLocationCondition = new RuleIrGroupCondition();
		irLocationCondition.setOperator(RuleIrGroupOperator.AND);
		irLocationCondition.setChildren(new ArrayList<>());

		// INCLUDED ORIGIN LOCATIONS
		createGroupLocationOrCondition(inclOriLocations, ORIGIN_LOCATIONS_ATTRIBUTE, contextRAOVariable, irLocationCondition,
				RuleIrAttributeOperator.CONTAINS);

		// EXCLUDED ORIGIN LOCATIONS
		createGroupLocationAndCondition(excOriLocations, ORIGIN_LOCATIONS_ATTRIBUTE, contextRAOVariable, irLocationCondition,
				RuleIrAttributeOperator.NOT_CONTAINS);

		// INCLUDED DESTINATION LOCATIONS
		createGroupLocationOrCondition(inclDesLocations, DESTINATION_LOCATIONS_ATTRIBUTE, contextRAOVariable,
				irLocationCondition, RuleIrAttributeOperator.CONTAINS);

		// EXCLUDED DESTINATION LOCATIONS
		createGroupLocationAndCondition(excDesLocations, DESTINATION_LOCATIONS_ATTRIBUTE, contextRAOVariable,
				irLocationCondition, RuleIrAttributeOperator.NOT_CONTAINS);

		return irLocationCondition;
	}
	/**
	 * Create group location or condition.
	 *
	 * @param locations
	 * 		the locations
	 * @param attribute
	 * 		the attribute
	 * @param raoVariable
	 * 		the rao variable
	 * @param irLocationCondition
	 * 		the ir location condition
	 * @param attributeOperator
	 * 		the attribute operator
	 */
	protected void createGroupLocationOrCondition(final List<String> locations, final String attribute, final String raoVariable,
			final RuleIrGroupCondition irLocationCondition, final RuleIrAttributeOperator attributeOperator)
	{
		if (Objects.nonNull(locations))
		{
			final RuleIrGroupCondition irIncludeLocationWrapperCondition = new RuleIrGroupCondition();
			irIncludeLocationWrapperCondition.setOperator(RuleIrGroupOperator.OR);
			irIncludeLocationWrapperCondition.setChildren(new ArrayList<>());

			for (final String location : locations)
			{
				final RuleIrGroupCondition irIncludeLocationCondition = new RuleIrGroupCondition();
				irIncludeLocationCondition.setOperator(RuleIrGroupOperator.AND);
				irIncludeLocationCondition.setChildren(new ArrayList<>());

				final RuleIrAttributeCondition ruleIrAttributeCondition = new RuleIrAttributeCondition();
				ruleIrAttributeCondition.setVariable(raoVariable);
				ruleIrAttributeCondition.setAttribute(attribute);
				ruleIrAttributeCondition.setOperator(attributeOperator);
				ruleIrAttributeCondition.setValue(location);

				irIncludeLocationCondition.getChildren().add(ruleIrAttributeCondition);
				irIncludeLocationWrapperCondition.getChildren().add(irIncludeLocationCondition);
			}
			irLocationCondition.getChildren().add(irIncludeLocationWrapperCondition);
		}
	}

	/**
	 * Create group location and condition.
	 *
	 * @param locations
	 * 		the locations
	 * @param attribute
	 * 		the attribute
	 * @param raoVariable
	 * 		the rao variable
	 * @param irLocationCondition
	 * 		the ir location condition
	 * @param attributeOperator
	 * 		the attribute operator
	 */
	protected void createGroupLocationAndCondition(final List<String> locations, final String attribute, final String raoVariable,
			final RuleIrGroupCondition irLocationCondition, final RuleIrAttributeOperator attributeOperator)
	{
		if (Objects.nonNull(locations))
		{
			final RuleIrGroupCondition irExclDestinationLocationCondition = new RuleIrGroupCondition();
			irExclDestinationLocationCondition.setOperator(RuleIrGroupOperator.AND);
			irExclDestinationLocationCondition.setChildren(new ArrayList<>());

			for (final String location : locations)
			{
				final RuleIrAttributeCondition ruleIrAttributeCondition = new RuleIrAttributeCondition();
				ruleIrAttributeCondition.setVariable(raoVariable);
				ruleIrAttributeCondition.setAttribute(attribute);
				ruleIrAttributeCondition.setOperator(attributeOperator);
				ruleIrAttributeCondition.setValue(location);

				irExclDestinationLocationCondition.getChildren().add(ruleIrAttributeCondition);
			}
			irLocationCondition.getChildren().add(irExclDestinationLocationCondition);
		}
	}
}
