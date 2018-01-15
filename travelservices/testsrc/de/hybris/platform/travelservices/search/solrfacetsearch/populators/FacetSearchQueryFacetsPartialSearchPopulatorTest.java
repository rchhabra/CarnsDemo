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
package de.hybris.platform.travelservices.search.solrfacetsearch.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.WildcardType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.FacetSearchQueryOperatorTranslator;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.FreeTextWildcardQueryField;
import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.QueryOperator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FacetSearchQueryFacetsPartialSearchPopulatorTest
{
	@InjectMocks
	private final FacetSearchQueryFacetsPartialSearchPopulator_Mock populator = new FacetSearchQueryFacetsPartialSearchPopulator_Mock();

	@Mock
	private FieldNameTranslator fieldNameTranslator;

	@Mock
	private FacetSearchQueryOperatorTranslator facetSearchQueryOperatorTranslator;

	@Before
	public void setup()
	{
		given(fieldNameTranslator.translate(Mockito.any(SearchQuery.class), Mockito.anyString(),
				Mockito.any(FieldNameProvider.FieldType.class))).willReturn("name");
		given(facetSearchQueryOperatorTranslator.translate(Mockito.anyString(), Mockito.any(QueryOperator.class)))
				.willReturn("Luton", "Paris");
	}

	@Test
	public void postfixFreeTextWildcardQueryTest()
	{
		final SearchQuery searchQuery = TestData.createSearchQuery();
		searchQuery.addFreeTextWildcardQuery(TestData.createFreeTextWildcardQueryField("origin", WildcardType.POSTFIX));

		final String result = populator.convertQueryField(searchQuery, TestData.createQueryField("origin", "LTN"));

		Assert.assertNotNull(result);
		Assert.assertEquals("name:Luton*", result);
	}

	@Test
	public void prefixFreeTextWildcardQueryTest()
	{
		final SearchQuery searchQuery = TestData.createSearchQuery();
		searchQuery.addFreeTextWildcardQuery(TestData.createFreeTextWildcardQueryField("origin", WildcardType.PREFIX));

		final String result = populator.convertQueryField(searchQuery, TestData.createQueryField("origin", "LTN"));

		Assert.assertNotNull(result);
		Assert.assertEquals("name:*Luton", result);
	}

	@Test
	public void prefixAndPostfixFreeTextWildcardQueryTest()
	{
		final SearchQuery searchQuery = TestData.createSearchQuery();
		searchQuery.addFreeTextWildcardQuery(TestData.createFreeTextWildcardQueryField("origin", WildcardType.PREFIX_AND_POSTFIX));

		final String result = populator.convertQueryField(searchQuery, TestData.createQueryField("origin", "LTN"));

		Assert.assertNotNull(result);
		Assert.assertEquals("name:*Luton*", result);
	}

	@Test
	public void defaultFreeTextWildcardQueryTest()
	{
		final SearchQuery searchQuery = TestData.createSearchQuery();
		searchQuery.addFreeTextWildcardQuery(TestData.createFreeTextWildcardQueryField("origin", null));

		final String result = populator.convertQueryField(searchQuery, TestData.createQueryField("origin", "LTN"));

		Assert.assertNotNull(result);
		Assert.assertEquals("name:*Luton*", result);
	}

	@Test
	public void testWithSingleQueryField()
	{
		final SearchQuery searchQuery = TestData.createSearchQuery();
		final String result = populator.convertQueryField(searchQuery, TestData.createQueryField("origin", "LTN"));
		Assert.assertNotNull(result);
		Assert.assertEquals("name:Luton", result);
	}

	@Test
	public void testWithMultipleQueryField()
	{
		final SearchQuery searchQuery = TestData.createSearchQuery();
		final String result = populator.convertQueryField(searchQuery, TestData.createQueryField("origin", "LTN", "CDG"));
		Assert.assertNotNull(result);
		Assert.assertEquals("name:(Luton AND Paris)", result);
	}

	// mock class to test protected methods only
	private class FacetSearchQueryFacetsPartialSearchPopulator_Mock extends FacetSearchQueryFacetsPartialSearchPopulator
	{
	}

	// test data
	private static class TestData
	{
		public static SearchQuery createSearchQuery()
		{
			return new SearchQuery(new FacetSearchConfig(), new IndexedType());
		}

		public static FreeTextWildcardQueryField createFreeTextWildcardQueryField(final String field,
				final WildcardType wildcardType)
		{
			final FreeTextWildcardQueryField freeTextWildcardQueryField = new FreeTextWildcardQueryField(field);
			freeTextWildcardQueryField.setWildcardType(wildcardType);
			return freeTextWildcardQueryField;
		}

		public static QueryField createQueryField(final String field, final String... value)
		{
			final QueryField queryField = new QueryField(field, value);
			queryField.setQueryOperator(QueryOperator.CONTAINS);
			return queryField;
		}
	}
}
