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
*/

package de.hybris.platform.travelrulesengine.commerce.impl;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelrulesengine.rao.BookingRAO;
import de.hybris.platform.travelrulesengine.rao.RefundActionRAO;
import de.hybris.platform.travelrulesengine.rule.evaluation.RetainFeeRAOAction;

import org.drools.core.spi.KnowledgeHelper;


/**
 * Implementation class for Fee RAO action {@link RetainFeeRAOAction}
 */
public class DefaultRetainFeeRAOAction extends AbstractTravelCommerceRAOAction implements RetainFeeRAOAction
{
	@Override
	public RefundActionRAO retainAdminFee(final BookingRAO bookingRao, final RuleEngineResultRAO ruleEngineResultRao,
			final Object ruleContext)
	{
		final KnowledgeHelper context = this.checkAndGetRuleContext(ruleContext);
		this.validateRule(context);
		ServicesUtil.validateParameterNotNull(bookingRao, "booking rao must not be null");

		final RefundActionRAO refundActionRao = this.getTravelRuleEngineCalculationService().addRefundFeeAction(bookingRao);
		if (refundActionRao == null)
		{
			return null;
		}
		this.setRAOMetaData(context, new AbstractRuleActionRAO[]
		{ refundActionRao });
		ruleEngineResultRao.getActions().add(refundActionRao);
		this.insertFacts(context, new Object[]
		{ refundActionRao });
		return refundActionRao;
	}

}
