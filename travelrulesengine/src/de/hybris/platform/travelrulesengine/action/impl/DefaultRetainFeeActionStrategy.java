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

package de.hybris.platform.travelrulesengine.action.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.travelrulesengine.enums.RefundActionType;
import de.hybris.platform.travelrulesengine.rao.RefundActionRAO;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Action strategy class for refunds
 */
public class DefaultRetainFeeActionStrategy extends AbstractTravelRuleActionStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultRetainFeeActionStrategy.class);

	@Override
	public List<? extends ItemModel> apply(final AbstractRuleActionRAO action)
	{
		if (!(action instanceof RefundActionRAO))
		{
			LOG.debug(String.format("cannot apply %s, action is not of type FeeRAO", this.getClass().getSimpleName()));
			return Collections.emptyList();
		}
		final RefundActionRAO refundActionRAO = (RefundActionRAO) action;
		refundActionRAO.setRefundAction(RefundActionType.RETAIN_ADMIN_FEE);
		return Collections.emptyList();
	}

	@Override
	public void undo(final ItemModel var1)
	{
		// DO NOTHING
	}

}
