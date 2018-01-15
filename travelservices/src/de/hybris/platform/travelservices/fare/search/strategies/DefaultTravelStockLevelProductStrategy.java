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
package de.hybris.platform.travelservices.fare.search.strategies;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.jalo.StockLevel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.stock.strategy.impl.DefaultStockLevelProductStrategy;

import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * This implementation extends the {@link DefaultStockLevelProductStrategy} to use the the configured
 * productsQualifierMap instead of a single configured 'qualifier' for code generation.
 */
public class DefaultTravelStockLevelProductStrategy extends DefaultStockLevelProductStrategy
{

	private Map<String, String> productsQualifierMap;

	/**
	 * Returns a product related ID for {@link StockLevel} based on the key-value entries of the productsQualifierMap.
	 *
	 * @param model
	 *           the product we need a StockLelevel qualifier for.
	 * @return String the qualifier of the assigned product based on the corresponding entry of the productsQualifierMap.
	 */
	@Override
	public String convert(final ProductModel model)
	{
		validateParameterNotNull(model, "Parameter 'model' was null.");

		if (model.getProductType() == null)
		{
			return super.convert(model);
		}

		final String className = model.getClass().getSimpleName();
		String attributeCode = getProductsQualifierMap().get(className);
		if (StringUtils.isEmpty(attributeCode))
		{
			attributeCode = getProductsQualifierMap().get(model.getProductType().getCode());
		}

		if (StringUtils.isEmpty(attributeCode))
		{
			return super.convert(model);
		}

		final ItemModelContextImpl internalContext = (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
		final Object value = internalContext.getAttributeProvider().getAttribute(attributeCode);
		return value == null ? null : value.toString();
	}

	/**
	 * Getter for the productQualifierMap property
	 *
	 * @return productsQualifierMap as the map with the qualifier name for different type of product
	 */
	protected Map<String, String> getProductsQualifierMap()
	{
		return productsQualifierMap;
	}

	/**
	 * Setter for the productQualifierMap property
	 *
	 * @param productsQualifierMap
	 *           as the map with the qualifier name for different type of product
	 */
	public void setProductsQualifierMap(final Map<String, String> productsQualifierMap)
	{
		this.productsQualifierMap = productsQualifierMap;
	}

}