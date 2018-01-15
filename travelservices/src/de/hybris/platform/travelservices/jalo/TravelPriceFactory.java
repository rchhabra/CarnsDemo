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
package de.hybris.platform.travelservices.jalo;

import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;

import java.util.List;
import java.util.Map;


/**
 * 
 * Overrides the {@link PriceFactory} to retrieve the {@link PriceRow} for a given product and travel search criteria
 * 
 */
public interface TravelPriceFactory extends PriceFactory
{
	/**
	 * Implement this to show all prices available for the given product and travel search criteria.
	 * 
	 * @param product
	 *           the product
	 * @param searchCriteria
	 *           the map with travel specific search criteria
	 * 
	 * @return a list of {@link PriceInformation} objects
	 * 
	 * @throws JaloPriceFactoryException
	 *            if price calculation error occured
	 */
	List<PriceInformation> getProductPriceInformations(Product product, Map<String, String> searchCriteria)
			throws JaloPriceFactoryException;
}
