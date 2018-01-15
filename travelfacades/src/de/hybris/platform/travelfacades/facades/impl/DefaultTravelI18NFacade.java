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
package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.i18n.impl.DefaultI18NFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelI18NFacade;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Class is responsible for providing concrete implementation of the TravelI18NFacade interface. The class uses the
 * travelI18NService class to do logic and then converts outcome to DTOs
 */
public class DefaultTravelI18NFacade extends DefaultI18NFacade implements TravelI18NFacade
{

	private Converter<LanguageModel, LanguageData> languageConverter;

	@Override
	public List<LanguageData> getAllLanguages()
	{
		final List<LanguageData> languages = Converters.convertAll(getCommonI18NService().getAllLanguages(),
				getLanguageConverter());

		if (languages != null && !languages.isEmpty())
		{
			Collections.sort(languages, (final LanguageData o1, final LanguageData o2) -> o1.getName().compareTo(o2.getName()));
			return languages;
		}
		return Collections.emptyList();
	}


	@Override
	public List<CountryData> getAllCountries()
	{
		final List<CountryData> countries = Converters.convertAll(getCommonI18NService().getAllCountries(), getCountryConverter());

		if (CollectionUtils.isNotEmpty(countries))
		{
			Collections.sort(countries, (final CountryData o1, final CountryData o2) -> o1.getName().compareTo(o2.getName()));
			return countries;
		}
		return Collections.emptyList();
	}

	/**
	 * @return the languageConverter
	 */
	protected Converter<LanguageModel, LanguageData> getLanguageConverter()
	{
		return languageConverter;
	}

	/**
	 * @param languageConverter
	 *           the languageConverter to set
	 */
	public void setLanguageConverter(final Converter<LanguageModel, LanguageData> languageConverter)
	{
		this.languageConverter = languageConverter;
	}
}
