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
import de.hybris.platform.commercefacades.travel.ancillary.accommodation.data.ConfiguredAccommodationData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Configured accommodation populator.
 */
public class ConfiguredAccommodationPopulator implements Populator<ConfiguredAccommodationModel, ConfiguredAccommodationData>
{

	private Converter<ProductModel, ProductData> productConverter;

	@Override
	public void populate(final ConfiguredAccommodationModel source, final ConfiguredAccommodationData target)
			throws ConversionException
	{
		target.setBookable(source.isBookable());
		final ProductModel productModel = source.getProduct();
		if (productModel != null)
		{
			target.setProduct(getProductConverter().convert(productModel));
		}
	}

	/**
	 * Gets product converter.
	 *
	 * @return the product converter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * Sets product converter.
	 *
	 * @param productConverter
	 * 		the product converter
	 */
	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

}
