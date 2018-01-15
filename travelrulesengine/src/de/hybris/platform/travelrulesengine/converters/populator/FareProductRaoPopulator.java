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

package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.FareProductRAO;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates FareProductRAO attributes from FareProductData. Uses categoryDataRaoConverter to populate categories attribute
 */
public class FareProductRaoPopulator implements Populator<FareProductData, FareProductRAO>
{
	private Converter<CategoryData, CategoryRAO> categoryDataRaoConverter;

	@Override
	public void populate(final FareProductData source, final FareProductRAO target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setCategories(Converters.convertAll(source.getCategories(), getCategoryDataRaoConverter()).stream().collect(
				Collectors.toSet()));
		target.setValid(Boolean.TRUE);
	}

	/**
	 * Gets category data rao converter.
	 *
	 * @return the category data rao converter
	 */
	protected Converter<CategoryData, CategoryRAO> getCategoryDataRaoConverter()
	{
		return categoryDataRaoConverter;
	}

	/**
	 * Sets category data rao converter.
	 *
	 * @param categoryDataRaoConverter
	 * 		the category data rao converter
	 */
	@Required
	public void setCategoryDataRaoConverter(
			final Converter<CategoryData, CategoryRAO> categoryDataRaoConverter)
	{
		this.categoryDataRaoConverter = categoryDataRaoConverter;
	}
}
