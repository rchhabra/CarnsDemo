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
import de.hybris.platform.travelrulesengine.model.BundleFilterResultModel;
import de.hybris.platform.travelrulesengine.rao.FilterBundleRAO;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Strategy responsible for filtering out the bundles based on rules evaluation result
 */
public class DefaultDiscardBundleActionStrategy extends AbstractTravelRuleActionStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultDiscardBundleActionStrategy.class);

	@Override
	public List<BundleFilterResultModel> apply(final AbstractRuleActionRAO action)
	{
		if (!(action instanceof FilterBundleRAO))
		{
			LOG.debug(String.format("cannot apply %s, action is not of type FilterBundleRAO", this.getClass().getSimpleName()));
			return Collections.emptyList();
		}
		final FilterBundleRAO filterBundleAction = (FilterBundleRAO) action;
		final BundleFilterResultModel bundleFilterResultModel = new BundleFilterResultModel();
		bundleFilterResultModel.setBundleType(filterBundleAction.getBundleType());
		bundleFilterResultModel.setValid(filterBundleAction.isValid());

		return Collections.singletonList(bundleFilterResultModel);
	}

	@Override
	public void undo(final ItemModel itemModel)
	{
		// DO NOTHING
	}
}
