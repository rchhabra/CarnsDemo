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
package de.hybris.platform.travelrulesengine.rao.providers.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.SearchParamsRAO;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * RAO Provider which creates SearchParameterRao facts to be used in rules evaluation
 */
public class DefaultSearchParameterRaoProvider implements RAOProvider
{
	private Converter<FareSearchRequestData, SearchParamsRAO> searchParamsRaoConverter;
	private Collection<String> defaultOptions;

	/**
	 * Instantiates a new Default search parameter rao provider.
	 */
	public DefaultSearchParameterRaoProvider()
	{

	}

	@Override
	public Set<Object> expandFactModel(final Object modelFact)
	{
		return expandFactModel(modelFact, getDefaultOptions());
	}

	/**
	 * Expand fact model set.
	 *
	 * @param modelFact
	 * 		the model fact
	 * @param options
	 * 		the options
	 * @return the set
	 */
	protected Set<Object> expandFactModel(final Object modelFact, final Collection<String> options)
	{
		return modelFact instanceof FareSearchRequestData ?
				expandRAO(createRAO((FareSearchRequestData) modelFact), options) :
				Collections.emptySet();
	}

	/**
	 * Converts FareSearchRequestData to SearchParamsRAO
	 *
	 * @param source
	 * 		the source
	 * @return UserRAO search params rao
	 */
	protected SearchParamsRAO createRAO(final FareSearchRequestData source)
	{
		return getSearchParamsRaoConverter().convert(source);
	}

	/**
	 * Expands RAO to include both SearchParamsRAO and inclusive LegInfos, PassengerTypeQuantities, User and UserGroups in facts
	 *
	 * @param rao
	 * 		the rao
	 * @param options
	 * 		the options
	 * @return set of facts
	 */
	protected Set<Object> expandRAO(final SearchParamsRAO rao, final Collection<String> options)
	{
		final Set<Object> facts = new LinkedHashSet<>();

		options.forEach(option -> {
			if ("INCLUDE_SEARCH_PARAMS".equals(option))
			{
				facts.add(rao);
			}
			if ("EXPAND_LEG_INFOS".equals(option))
			{
				facts.addAll(rao.getLegInfos());
			}
			if ("EXPAND_PASSENGER_TYPE_QUANTITIES".equals(option))
			{
				facts.addAll(rao.getPassengerTypeQuantities());
			}
			if ("EXPAND_USER".equals(option))
			{
				facts.add(rao.getUser());
			}
			if ("EXPAND_USER_GROUP".equals(option))
			{
				facts.addAll(rao.getUser().getGroups());
			}
		});

		return facts;
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

	/**
	 * Gets search params rao converter.
	 *
	 * @return the search params rao converter
	 */
	protected Converter<FareSearchRequestData, SearchParamsRAO> getSearchParamsRaoConverter()
	{
		return searchParamsRaoConverter;
	}

	/**
	 * Sets search params rao converter.
	 *
	 * @param searchParamsRaoConverter
	 * 		the search params rao converter
	 */
	@Required
	public void setSearchParamsRaoConverter(
			final Converter<FareSearchRequestData, SearchParamsRAO> searchParamsRaoConverter)
	{
		this.searchParamsRaoConverter = searchParamsRaoConverter;
	}
}
