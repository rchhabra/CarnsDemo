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

package de.hybris.platform.travelrulesengine.action.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.travelrulesengine.model.FareFilterResultModel;
import de.hybris.platform.travelrulesengine.rao.FilterFareRAO;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Strategy responsible for filtering out the fare product based on rules evaluation result
 */
public class DefaultDiscardFareActionStrategy extends AbstractTravelRuleActionStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultDiscardFareActionStrategy.class);

	@Override
	public List<FareFilterResultModel> apply(final AbstractRuleActionRAO action)
	{
		if (!(action instanceof FilterFareRAO))
		{
			LOG.debug(String.format("cannot apply %s, action is not of type FilterFareRAO", this.getClass().getSimpleName()));
			return Collections.emptyList();
		}
		final FilterFareRAO filterFareAction = (FilterFareRAO) action;
		final FareFilterResultModel fareFilterResultModel = new FareFilterResultModel();
		fareFilterResultModel.setFareProductCode(filterFareAction.getFareProductCode());
		fareFilterResultModel.setValid(filterFareAction.isValid());

		return Collections.singletonList(fareFilterResultModel);
	}

	@Override
	public void undo(ItemModel itemModel)
	{
		// DO NOTHING
	}
}
