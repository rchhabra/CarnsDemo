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

package de.hybris.platform.travelservices.strategies.stock;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;


/**
 * Strategy to resolve stock level value according to product type
 */
public interface StockResolvingStrategyByProductType
{
	/**
	 * Return stock level based on specific product type
	 *
	 * @param entry
	 * 		the entry
	 * @return stock
	 */
	Long getStock(AbstractOrderEntryModel entry);
}
