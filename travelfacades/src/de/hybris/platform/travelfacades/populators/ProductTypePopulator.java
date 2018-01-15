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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.enums.ProductType;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Product type populator.
 */
public class ProductTypePopulator implements Populator<ProductModel, ProductData>
{
	private Map<String, ProductType> productTypeInstanceMap;

	@Override
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		final String className = source.getClass().getSimpleName();
		ProductType productType = getProductTypeInstanceMap().get(className);
		if (Objects.isNull(productType))
		{
			productType = source.getProductType();
		}
		target.setProductType(productType.getCode());
	}

	/**
	 * Gets the product type instance map.
	 *
	 * @return the productTypeInstanceMap
	 */
	protected Map<String, ProductType> getProductTypeInstanceMap()
	{
		return productTypeInstanceMap;
	}

	/**
	 * Sets the product type instance map.
	 *
	 * @param productTypeInstanceMap
	 *           the productTypeInstanceMap to set
	 */
	@Required
	public void setProductTypeInstanceMap(final Map<String, ProductType> productTypeInstanceMap)
	{
		this.productTypeInstanceMap = productTypeInstanceMap;
	}

}
