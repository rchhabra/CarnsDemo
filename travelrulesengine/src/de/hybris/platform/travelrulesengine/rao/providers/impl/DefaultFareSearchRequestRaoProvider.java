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

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.FareSearchRequestRAO;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * RAO Provider which creates FareSearchRequestRAO facts to be used in rules evaluation
 */
public class DefaultFareSearchRequestRaoProvider implements RAOProvider
{
	private Converter<FareSearchRequestData, FareSearchRequestRAO> fareSearchRequestRaoConverter;
	private Collection<String> defaultOptions;

	public DefaultFareSearchRequestRaoProvider()
	{

	}

	@Override
	public Set<Object> expandFactModel(final Object modelFact)
	{
		return expandFactModel(modelFact, getDefaultOptions());
	}

	protected Set<Object> expandFactModel(final Object modelFact, final Collection<String> options)
	{
		return modelFact instanceof FareSearchRequestData ?
				expandRAO(createRAO((FareSearchRequestData) modelFact), options) :
				Collections.emptySet();
	}

	/**
	 * Converts FareSearchRequestData to FareSearchRequestRAO
	 *
	 * @param source
	 * @return UserRAO
	 */
	protected FareSearchRequestRAO createRAO(final FareSearchRequestData source)
	{
		return getFareSearchRequestRaoConverter().convert(source);
	}

	/**
	 * Expands RAO to include both FareSearchRequestRAO and inclusive LegInfos and PassengerTypeQuantities in facts
	 *
	 * @param rao
	 * @param options
	 * @return set of facts
	 */
	protected Set<Object> expandRAO(final FareSearchRequestRAO rao, final Collection<String> options)
	{
		Set<Object> facts = new LinkedHashSet<>();

		options.forEach(option -> {
			if ("INCLUDE_FARE_SEARCH".equals(option))
			{
				facts.add(rao);
			}
			if ("EXPAND_LEG_INFOS".equals(option))
			{
				rao.getLegInfos().forEach(facts::add);
			}
			if ("EXPAND_PASSENGER_TYPE_QUANTITIES".equals(option))
			{
				rao.getPassengerTypeQuantities().forEach(facts::add);
			}
		});

		return facts;
	}

	/**
	 * Gets fare search request rao converter.
	 *
	 * @return the fare search request rao converter
	 */
	protected Converter<FareSearchRequestData, FareSearchRequestRAO> getFareSearchRequestRaoConverter()
	{
		return fareSearchRequestRaoConverter;
	}

	/**
	 * Sets fare search request rao converter.
	 *
	 * @param fareSearchRequestRaoConverter
	 * 		the fare search request rao converter
	 */
	@Required
	public void setFareSearchRequestRaoConverter(
			final Converter<FareSearchRequestData, FareSearchRequestRAO> fareSearchRequestRaoConverter)
	{
		this.fareSearchRequestRaoConverter = fareSearchRequestRaoConverter;
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
