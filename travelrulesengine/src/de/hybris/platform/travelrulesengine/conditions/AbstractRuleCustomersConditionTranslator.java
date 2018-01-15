package de.hybris.platform.travelrulesengine.conditions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import de.hybris.platform.ruledefinitions.CollectionOperator;
import de.hybris.platform.ruledefinitions.conditions.RuleTargetCustomersConditionTranslator;
import de.hybris.platform.ruledefinitions.conditions.builders.RuleIrGroupConditionBuilder;
import de.hybris.platform.ruledefinitions.conditions.builders.RuleIrNotConditionBuilder;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.rao.UserRAO;

public abstract class AbstractRuleCustomersConditionTranslator extends RuleTargetCustomersConditionTranslator 
{
	protected void addTargetCustomersConditions(final RuleCompilerContext context, final CollectionOperator customerGroupsOperator,
			final List<String> customerGroups, final List<String> customers, final RuleIrGroupCondition irTargetCustomersCondition, String contextRaoVariable)
	{
		final String userRaoVariable = context.generateVariable(UserRAO.class);
		final List<RuleIrCondition> irConditions = Lists.newArrayList();
		final RuleIrTypeCondition irUserCondition = new RuleIrTypeCondition();
		irUserCondition.setVariable(userRaoVariable);
		final RuleIrTypeCondition irOfferRequestCondition = new RuleIrTypeCondition();
		irOfferRequestCondition.setVariable(contextRaoVariable);
		irConditions.add(irUserCondition);
		irConditions.add(irOfferRequestCondition);
		final RuleIrGroupCondition irCustomerGroupsCondition = this.getCustomerGroupConditions(context, customerGroupsOperator,
				customerGroups);
		final RuleIrAttributeCondition irCustomersCondition = this.getCustomerConditions(context, customers);
		if (this.verifyAllPresent(irCustomerGroupsCondition, irCustomersCondition))
		{
			final RuleIrGroupCondition groupCondition = RuleIrGroupConditionBuilder.newGroupConditionOf(RuleIrGroupOperator.OR)
					.build();
			groupCondition.setChildren(Arrays.asList(irCustomerGroupsCondition, irCustomersCondition));
			irConditions.add(groupCondition);
		}
		else if (Objects.nonNull(irCustomerGroupsCondition))
		{
			irConditions.add(irCustomerGroupsCondition);
		}
		else if (Objects.nonNull(irCustomersCondition))
		{
			irConditions.add(irCustomersCondition);
		}

		if (CollectionOperator.NOT_CONTAINS.equals(customerGroupsOperator))
		{
			irTargetCustomersCondition.getChildren()
					.add(RuleIrNotConditionBuilder.newNotCondition().withChildren(irConditions).build());
		}
		else
		{
			irTargetCustomersCondition.getChildren().addAll(irConditions);
		}
	}
}
