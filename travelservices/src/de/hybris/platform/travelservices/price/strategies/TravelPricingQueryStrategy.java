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
package de.hybris.platform.travelservices.price.strategies;

import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.jalo.TaxRow;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;

import java.util.Collection;


/**
 * Strategy to fetch the prices and taxes. This will be used to create and run a query against the price rows for a
 * specific functionality. The price factory will use this strategy to execute the query.
 */
public interface TravelPricingQueryStrategy
{

	/**
	 * Gets price rows.
	 *
	 * @param ctx
	 * 		the ctx
	 * @param product
	 * 		the product
	 * @param productGroup
	 * 		the product group
	 * @param user
	 * 		the user
	 * @param userGroup
	 * 		the user group
	 * @return price rows
	 */
	Collection<PriceRow> getPriceRows(final SessionContext ctx, final Product product, final EnumerationValue productGroup,
			final User user, final EnumerationValue userGroup);

	/**
	 * Gets tax rows.
	 *
	 * @param ctx
	 * 		the ctx
	 * @param product
	 * 		the product
	 * @param productGroup
	 * 		the product group
	 * @param user
	 * 		the user
	 * @param userGroup
	 * 		the user group
	 * @return tax rows
	 */
	Collection<TaxRow> getTaxRows(final SessionContext ctx, final Product product, final EnumerationValue productGroup,
			final User user, final EnumerationValue userGroup);
}
