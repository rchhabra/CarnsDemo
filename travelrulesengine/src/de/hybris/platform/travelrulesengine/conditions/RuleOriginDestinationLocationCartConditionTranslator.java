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
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.ArrayList;
import java.util.List;


/**
 * This is a condition translator to translate Origin Destination location conditions for CartRao
 */
public class RuleOriginDestinationLocationCartConditionTranslator extends AbstractLocationConditionTranslator
{
	private static final String INCLUDED_ORIGIN_LOCATION_PARAMETER = "inclDepLocations";
	private static final String EXCLUDED_ORIGIN_LOCATION_PARAMETER = "excDepLocations";
	private static final String INCLUDED_DESTINATION_LOCATION_PARAMETER = "inclArrLocations";
	private static final String EXCLUDED_DESTINATION_LOCATION_PARAMETER = "excArrLocations";

	private static final String ORIGIN_LOCATIONS_ATTRIBUTE = "originLocations";
	private static final String DESTINATION_LOCATIONS_ATTRIBUTE = "destinationLocations";

	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData ruleConditionDefinitionData)
	{
		final String cartRAOVariable = context.generateVariable(CartRAO.class);

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
		createGroupLocationOrCondition(inclOriLocations, ORIGIN_LOCATIONS_ATTRIBUTE, cartRAOVariable, irLocationCondition,
				RuleIrAttributeOperator.CONTAINS);

		// EXCLUDED ORIGIN LOCATIONS
		createGroupLocationAndCondition(excOriLocations, ORIGIN_LOCATIONS_ATTRIBUTE, cartRAOVariable, irLocationCondition,
				RuleIrAttributeOperator.NOT_CONTAINS);

		// INCLUDED DESTINATION LOCATIONS
		createGroupLocationOrCondition(inclDesLocations, DESTINATION_LOCATIONS_ATTRIBUTE, cartRAOVariable,
				irLocationCondition, RuleIrAttributeOperator.CONTAINS);

		// EXCLUDED DESTINATION LOCATIONS
		createGroupLocationAndCondition(excDesLocations, DESTINATION_LOCATIONS_ATTRIBUTE, cartRAOVariable,
				irLocationCondition, RuleIrAttributeOperator.NOT_CONTAINS);

		return irLocationCondition;
	}
}
