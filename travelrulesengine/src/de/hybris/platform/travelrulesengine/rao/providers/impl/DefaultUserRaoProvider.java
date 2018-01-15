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

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * RAO Provider which creates UserRAO facts to be used in rules evaluation
 */
public class DefaultUserRaoProvider implements RAOProvider
{
	private Converter<UserModel, UserRAO> userRaoConverter;
	private Collection<String> defaultOptions;

	@Override
	public Set<Object> expandFactModel(final Object modelFact)
	{
		return expandFactModel(modelFact, getDefaultOptions());
	}

	protected Set<Object> expandFactModel(final Object modelFact, final Collection<String> options)
	{
		return modelFact instanceof UserModel ?
				expandRAO(createRAO((UserModel) modelFact), options) :
				Collections.emptySet();
	}

	/**
	 * Converts UserModel to UserRAO
	 *
	 * @param source
	 * @return UserRAO
	 */
	protected UserRAO createRAO(final UserModel source)
	{
		return getUserRaoConverter().convert(source);
	}

	/**
	 * Expands RAO to include both UserRAO and inclusive UserGroupRAOs in facts
	 *
	 * @param rao
	 * @param options
	 * @return set of facts
	 */
	protected Set<Object> expandRAO(final UserRAO rao, final Collection<String> options)
	{
		final Set<Object> facts = new LinkedHashSet<>();

		options.forEach(option -> {
			if ("INCLUDE_USER".equals(option))
			{
				facts.add(rao);
			}
			if ("EXPAND_GROUPS".equals(option))
			{
				rao.getGroups().forEach(facts::add);
			}
		});

		return facts;
	}

	/**
	 * Gets user rao converter.
	 *
	 * @return the user rao converter
	 */
	protected Converter<UserModel, UserRAO> getUserRaoConverter()
	{
		return userRaoConverter;
	}

	/**
	 * Sets user rao converter.
	 *
	 * @param userRaoConverter
	 * 		the user rao converter
	 */
	@Required
	public void setUserRaoConverter(final Converter<UserModel, UserRAO> userRaoConverter)
	{
		this.userRaoConverter = userRaoConverter;
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
