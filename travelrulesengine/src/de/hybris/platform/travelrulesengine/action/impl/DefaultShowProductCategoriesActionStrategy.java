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
import de.hybris.platform.travelrulesengine.model.ShowProductCategoryActionResultModel;
import de.hybris.platform.travelrulesengine.rao.ShowProductCategoriesActionRAO;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Populates the product categories that are to be displayed after rule conditions are met successfully to
 * ShowProductCategoryActionResultModel from ActionRao
 */
public class DefaultShowProductCategoriesActionStrategy extends AbstractTravelRuleActionStrategy {

	private static final Logger LOG = Logger.getLogger(DefaultShowProductCategoriesActionStrategy.class);

	@Override
	public List<ShowProductCategoryActionResultModel> apply(final AbstractRuleActionRAO actionRAO)
	{
		if (!(actionRAO instanceof ShowProductCategoriesActionRAO))
		{
			LOG.debug(
					String.format("cannot apply %s, action is not of type ShowProductCategoryActionRAO",
							this.getClass().getSimpleName()));
			return Collections.emptyList();
		}
		final ShowProductCategoriesActionRAO showProductCategoriesActionRAO = (ShowProductCategoriesActionRAO) actionRAO;
		final ShowProductCategoryActionResultModel showProductCategoryActionResult = new ShowProductCategoryActionResultModel();
		showProductCategoryActionResult.setCategories(showProductCategoriesActionRAO.getCategories());

		return Collections.singletonList(showProductCategoryActionResult);
	}

	@Override
	public void undo(final ItemModel arg0)
	{
		//do nothing
	}

}
