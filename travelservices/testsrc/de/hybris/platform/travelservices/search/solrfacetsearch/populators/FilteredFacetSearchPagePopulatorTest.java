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

package de.hybris.platform.travelservices.search.solrfacetsearch.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.search.facetdata.FilteredFacetSearchPageData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FilteredFacetSearchPagePopulatorTest
{

	@InjectMocks
	FilteredFacetSearchPagePopulator filteredFacetSearchPagePopulator;

	@Mock
	private Converter searchQueryConverter;

	@Mock
	private Converter facetValueConverter;

	@Test
	public void testForEmptyValues()
	{
		final FilteredFacetSearchPageData source = new FilteredFacetSearchPageData();
		final FilteredFacetSearchPageData target = new FilteredFacetSearchPageData();
		source.setCode("TEST_CODE");
		source.setClearFacetQuery(new Object());
		Mockito.when(searchQueryConverter.convert(Matchers.any())).thenReturn(null);
		filteredFacetSearchPagePopulator.setSearchQueryConverter(searchQueryConverter);
		filteredFacetSearchPagePopulator.populate(source, target);
		Assert.assertEquals("TEST_CODE", target.getCode());

	}

	@Test
	public void test()
	{
		final FilteredFacetSearchPageData source = new FilteredFacetSearchPageData();
		final FilteredFacetSearchPageData target = new FilteredFacetSearchPageData();
		source.setCode("TEST_CODE");
		source.setClearFacetQuery(new Object());
		final List<FacetValueData> values = new ArrayList<>();
		values.add(new FacetValueData<>());
		source.setValues(values);

		Mockito.when(searchQueryConverter.convert(Matchers.any())).thenReturn(null);
		Mockito.when(facetValueConverter.convert(Matchers.any())).thenReturn(new FacetValueData<>());
		filteredFacetSearchPagePopulator.setSearchQueryConverter(searchQueryConverter);
		filteredFacetSearchPagePopulator.setFacetValueConverter(facetValueConverter);
		filteredFacetSearchPagePopulator.populate(source, target);

		Assert.assertEquals("TEST_CODE", target.getCode());

	}
}
