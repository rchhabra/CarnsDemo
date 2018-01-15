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

package de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;
import de.hybris.platform.travelservices.search.facetdata.FilteredFacetSearchPageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link UpdateFilteredFacetsStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateFilteredFacetsStrategyTest
{
	@InjectMocks
	UpdateFilteredFacetsStrategy updateFilteredFacetsStrategy;

	private final String TEST_FACE_CODE_A = "TEST_FACE_CODE_A";

	@Test
	public void testApplyStrategyForEmptyFilteredFacetsOnCriterion()
	{

		final CriterionData criterionData = createCriterionData();
		final AccommodationOfferingSearchPageData accommodationOfferingSearchPageData = createAccommodationOfferingSearchPageData();
		updateFilteredFacetsStrategy.applyStrategy(criterionData, accommodationOfferingSearchPageData);
		Assert.assertTrue(StringUtils.equals(criterionData.getFilteredFacets().get(0).getCode(), TEST_FACE_CODE_A));

	}

	@Test
	public void testApplyStrategyForEmptyFilteredFacetsOnSearchPageData()
	{
		final CriterionData criterionData = createCriterionData();
		final AccommodationOfferingSearchPageData accommodationOfferingSearchPageData = createAccommodationOfferingSearchPageData();
		accommodationOfferingSearchPageData.setFilteredFacets(Collections.emptyList());
		updateFilteredFacetsStrategy.applyStrategy(criterionData, accommodationOfferingSearchPageData);
		Assert.assertTrue(CollectionUtils.isEmpty(criterionData.getFilteredFacets()));
	}

	@Test
	public void testApplyStrategyForNonEmptyFilteredFacetsOnCriterion()
	{
		final CriterionData criterionData = createCriterionData();
		final List<FilteredFacetSearchPageData<SearchStateData>> facets = new ArrayList<>();
		facets.add(createFacetData(TEST_FACE_CODE_A));
		criterionData.setFilteredFacets(facets);
		final AccommodationOfferingSearchPageData accommodationOfferingSearchPageData = createAccommodationOfferingSearchPageData();
		updateFilteredFacetsStrategy.applyStrategy(criterionData, accommodationOfferingSearchPageData);
		Assert.assertSame(facets, criterionData.getFilteredFacets());
	}

	public CriterionData createCriterionData()
	{
		final CriterionData criterionData = new CriterionData();
		final List<FacetData<SearchStateData>> facets = new ArrayList<>();
		criterionData.setFacets(facets);

		return criterionData;
	}

	public AccommodationOfferingSearchPageData createAccommodationOfferingSearchPageData()
	{
		final AccommodationOfferingSearchPageData accommodationOfferingSearchPageData = new AccommodationOfferingSearchPageData();
		final List<FilteredFacetSearchPageData<SearchStateData>> facets = new ArrayList<>();
		facets.add(createFacetData(TEST_FACE_CODE_A));

		accommodationOfferingSearchPageData.setFilteredFacets(facets);
		return accommodationOfferingSearchPageData;

	}


	public FilteredFacetSearchPageData<SearchStateData> createFacetData(final String facetCode)
	{
		final FilteredFacetSearchPageData<SearchStateData> facetData = new FilteredFacetSearchPageData<>();
		facetData.setCode(facetCode);
		return facetData;
	}
}
