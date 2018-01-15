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

package de.hybris.platform.travelrulesengine.rao.providers.impl;

import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.FareProductRAO;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * RAO Provider which creates FareProductRAO facts to be used in rules evaluation
 */
public class DefaultFareProductRAOProvider implements RAOProvider
{
	private static final String INCLUDE_PRODUCT = "INCLUDE_PRODUCT";
	private static final String EXPAND_CATEGORIES = "EXPAND_CATEGORIES";
	
	private Converter<FareProductData, FareProductRAO> fareProductRaoConverter;
	private Collection<String> defaultOptions;

	@Override
	public Set<Object> expandFactModel(final Object modelFact)
	{
		return expandFactModel(modelFact, getDefaultOptions());
	}

	protected Set<Object> expandFactModel(final Object modelFact, Collection<String> options)
	{
		return modelFact instanceof FareProductData ?
				expandRAO(createRAO((FareProductData) modelFact), options) :
				Collections.emptySet();
	}

	/**
	 * Converts FareProductData to FareProductRAO
	 *
	 * @param source
	 * @return FareProductRAO
	 */
	protected FareProductRAO createRAO(final FareProductData source)
	{
		return getFareProductRaoConverter().convert(source);
	}

	/**
	 * Expands RAO to include both FareProductRAO and inclusive CategoryRAOs in facts
	 *
	 * @param rao
	 * @param options
	 * @return set of facts
	 */
	protected Set<Object> expandRAO(final ProductRAO rao, final Collection<String> options)
	{
		final Set<Object> facts = new LinkedHashSet<Object>();

		options.forEach(option -> {
			if (INCLUDE_PRODUCT.equals(option))
			{
				facts.add(rao);
			}
			if (EXPAND_CATEGORIES.equals(option))
			{
				rao.getCategories().forEach(facts::add);
			}
		});

		return facts;
	}

	/**
	 * Gets fare product rao converter.
	 *
	 * @return the fare product rao converter
	 */
	protected Converter<FareProductData, FareProductRAO> getFareProductRaoConverter()
	{
		return fareProductRaoConverter;
	}

	/**
	 * Sets fare product rao converter.
	 *
	 * @param fareProductRaoConverter
	 * 		the fare product rao converter
	 */
	@Required
	public void setFareProductRaoConverter(
			final Converter<FareProductData, FareProductRAO> fareProductRaoConverter)
	{
		this.fareProductRaoConverter = fareProductRaoConverter;
	}

	/**
	 * Gets default options.
	 *
	 * @return the default options
	 */
	protected Collection<String> getDefaultOptions()
	{
		return defaultOptions;
	}

	/**
	 * Sets default options.
	 *
	 * @param defaultOptions
	 * 		the default options
	 */
	@Required
	public void setDefaultOptions(final Collection<String> defaultOptions)
	{
		this.defaultOptions = defaultOptions;
	}
}
