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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator to populate the products of a BundleTemplateData from the products of a BundleTemplateData
 */
public class BundleTemplateProductPopulator implements Populator<BundleTemplateModel, BundleTemplateData>
{
	private Converter<ProductModel, ProductData> productConverter;

	@Override
	public void populate(final BundleTemplateModel source, final BundleTemplateData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setProducts(source.getProducts().stream().map(productModel -> getProductConverter().convert(productModel))
				.collect(Collectors.toList()));
	}

	/**
	 * @return the productConverter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * @param productConverter
	 * 		the productConverter to set
	 */
	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}
}
