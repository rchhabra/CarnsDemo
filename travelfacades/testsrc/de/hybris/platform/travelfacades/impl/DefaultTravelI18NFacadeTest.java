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

package de.hybris.platform.travelfacades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.impl.DefaultTravelI18NFacade;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultTravelI18NFacade implementation
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelI18NFacadeTest
{
	@InjectMocks
	DefaultTravelI18NFacade defaultTravelI18NFacade;

	@Mock
	private Converter<LanguageModel, LanguageData> languageConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private Converter<RegionModel, RegionData> regionConverter;
	@Mock
	private Converter<CountryModel, CountryData> countryConverter;

	@Test
	public void testGetAllLanguages()
	{
		final LanguageData lgData1 = new LanguageData();
		lgData1.setName("TEST_LANGUAGE_DATA_1");
		final LanguageData lgData2 = new LanguageData();
		lgData2.setName("TEST_LANGUAGE_DATA_2");
		final List<LanguageData> languages = new ArrayList<>();
		languages.add(lgData2);
		languages.add(lgData1);
		final LanguageModel languageModel1 = Mockito.mock(LanguageModel.class);
		final LanguageModel languageModel2 = Mockito.mock(LanguageModel.class);
		final List<LanguageModel> languageList = Arrays.asList(languageModel1, languageModel2);
		when(commonI18NService.getAllLanguages()).thenReturn(languageList);
		when(languageConverter.convert(languageModel1)).thenReturn(lgData1);
		when(languageConverter.convert(languageModel2)).thenReturn(lgData2);
		Assert.assertEquals("TEST_LANGUAGE_DATA_1", defaultTravelI18NFacade.getAllLanguages().get(0).getName());
	}

	@Test
	public void testGetAllLanguagesForEmptyLanguageList()
	{
		when(commonI18NService.getAllLanguages()).thenReturn(Collections.emptyList());
		Assert.assertTrue(CollectionUtils.isEmpty(defaultTravelI18NFacade.getAllLanguages()));
	}

	@Test
	public void testGetAllCountries()
	{
		final CountryData countryData1 = new CountryData();
		countryData1.setName("TEST_COUNTRY_DATA_1");
		final CountryData countryData2 = new CountryData();
		countryData2.setName("TEST_COUNTRY_DATA_2");
		final List<CountryData> languages = new ArrayList<>();
		languages.add(countryData2);
		languages.add(countryData1);
		final CountryModel countryModel1 = Mockito.mock(CountryModel.class);
		final CountryModel countryModel2 = Mockito.mock(CountryModel.class);
		final List<CountryModel> countryList = Arrays.asList(countryModel1, countryModel2);
		when(commonI18NService.getAllCountries()).thenReturn(countryList);
		when(countryConverter.convert(countryModel1)).thenReturn(countryData1);
		when(countryConverter.convert(countryModel2)).thenReturn(countryData2);
		Assert.assertEquals("TEST_COUNTRY_DATA_1", defaultTravelI18NFacade.getAllCountries().get(0).getName());
	}

	@Test
	public void testGetAllCountriesForEmptyCountryList()
	{
		when(commonI18NService.getAllCountries()).thenReturn(Collections.emptyList());
		Assert.assertTrue(CollectionUtils.isEmpty(defaultTravelI18NFacade.getAllCountries()));
	}
}
