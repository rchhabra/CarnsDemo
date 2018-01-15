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
import de.hybris.platform.travelrulesengine.model.BundleTemplateShowResultModel;
import de.hybris.platform.travelrulesengine.rao.ShowBundleTemplatesRAO;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Strategy responsible for showing bundle templates based on rules evaluation result
 */
public class DefaultShowBundleTemplateActionStrategy extends AbstractTravelRuleActionStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultDiscardBundleActionStrategy.class);

	@Override
	public List<BundleTemplateShowResultModel> apply(final AbstractRuleActionRAO action)
	{
		if (!(action instanceof ShowBundleTemplatesRAO))
		{
			LOG.debug(String.format("cannot apply %s, action is not of type ShowBundleTemplatesRAO", this.getClass().getSimpleName()));
			return Collections.emptyList();
		}
		final ShowBundleTemplatesRAO showBundleTemplatesRAO = (ShowBundleTemplatesRAO) action;
		final BundleTemplateShowResultModel bundleTemplateShowResult = new BundleTemplateShowResultModel();
		bundleTemplateShowResult.setBundleTemplates(showBundleTemplatesRAO.getBundleTemplates());

		return Collections.singletonList(bundleTemplateShowResult);
	}

	@Override
	public void undo(final ItemModel itemModel)
	{
		// DO NOTHING
	}
}
