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

package de.hybris.platform.travelrulesengine.action.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.travelrulesengine.model.ShowProductActionResultModel;
import de.hybris.platform.travelrulesengine.rao.ShowProductsActionRAO;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class is executed as part of rule action and sets action parameters to the action rao
 *
 */
public class DefaultShowProductsActionStrategy extends AbstractTravelRuleActionStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultShowProductsActionStrategy.class);

	@Override
	public List<ShowProductActionResultModel> apply(final AbstractRuleActionRAO actionRAO)
	{
		if (!(actionRAO instanceof ShowProductsActionRAO))
		{
			LOG.debug(
					String.format("cannot apply %s, action is not of type ShowProductActionRAO", this.getClass().getSimpleName()));
			return Collections.emptyList();
		}
		final ShowProductsActionRAO showProductActionRAO = (ShowProductsActionRAO) actionRAO;
		final ShowProductActionResultModel showProductActionResult = new ShowProductActionResultModel();
		showProductActionResult.setProducts(showProductActionRAO.getProducts());

		return Collections.singletonList(showProductActionResult);
	}

	@Override
	public void undo(final ItemModel paramItemModel)
	{
		// do nothing
	}

}
