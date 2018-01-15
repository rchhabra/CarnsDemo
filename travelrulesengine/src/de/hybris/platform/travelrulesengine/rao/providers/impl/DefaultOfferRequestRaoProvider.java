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

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class adds OfferRequestRao to the rule fact
 *
 */
public class DefaultOfferRequestRaoProvider implements RAOProvider
{
	private Converter<OfferRequestData, OfferRequestRAO> offerRequestRaoConverter;
	private Collection<String> defaultOptions;

	@Override
	public Set<Object> expandFactModel(final Object modelFact)
	{
		return expandFactModel(modelFact, getDefaultOptions());
	}

	protected Set<Object> expandFactModel(final Object modelFact, final Collection<String> options)
	{
		return modelFact instanceof OfferRequestData ? expandRAO(createRAO((OfferRequestData) modelFact), options)
				: Collections.emptySet();
	}

	protected OfferRequestRAO createRAO(final OfferRequestData source)
	{
		return getOfferRequestRaoConverter().convert(source);
	}

	protected Set<Object> expandRAO(final OfferRequestRAO rao, final Collection<String> options)
	{
		final Set<Object> facts = new LinkedHashSet<>();

		options.forEach(option -> {
			if ("INCLUDE_OFFER_REQUEST".equals(option))
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
	 * @return the defaultOptions
	 */
	protected Collection<String> getDefaultOptions()
	{
		return defaultOptions;
	}

	/**
	 * @param defaultOptions
	 *           the defaultOptions to set
	 */
	public void setDefaultOptions(final Collection<String> defaultOptions)
	{
		this.defaultOptions = defaultOptions;
	}

	/**
	 * @return the offerRequestRaoConverter
	 */
	protected Converter<OfferRequestData, OfferRequestRAO> getOfferRequestRaoConverter()
	{
		return offerRequestRaoConverter;
	}

	/**
	 * @param offerRequestRaoConverter
	 *           the offerRequestRaoConverter to set
	 */
	public void setOfferRequestRaoConverter(final Converter<OfferRequestData, OfferRequestRAO> offerRequestRaoConverter)
	{
		this.offerRequestRaoConverter = offerRequestRaoConverter;
	}

}
