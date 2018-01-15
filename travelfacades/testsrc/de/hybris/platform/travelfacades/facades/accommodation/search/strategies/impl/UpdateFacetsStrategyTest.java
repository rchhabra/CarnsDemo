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
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link UpdateFacetsStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateFacetsStrategyTest
{
	@InjectMocks
	UpdateFacetsStrategy updateFacetsStrategy;

	private final String TEST_FACE_CODE_A = "TEST_FACE_CODE_A";
	private final String TEST_FACE_CODE_B = "TEST_FACE_CODE_B";
	private final String TEST_FACE_CODE_C = "TEST_FACE_CODE_C";
	private final String TEST_FACE_CODE_D = "TEST_FACE_CODE_D";
	private final String TEST_FACE_CODE_E = "TEST_FACE_CODE_E";

	private final String TEST_FACE_VALUE_CODE_A = "TEST_FACE_VALUE_CODE_A";
	private final String TEST_FACE_VALUE_CODE_B = "TEST_FACE_VALUE_CODE_B";

	@Test
	public void testApplyStrategyForEmptyFacets()
	{
		final CriterionData criteria = new CriterionData();
		updateFacetsStrategy.applyStrategy(criteria, new AccommodationOfferingSearchPageData());
		Assert.assertTrue(CollectionUtils.isEmpty(criteria.getFacets()));
	}

	@Test
	public void testApplyStrategyForEmptyFacetsForCriteria()
	{
		final CriterionData criteria = new CriterionData();
		final AccommodationOfferingSearchPageData accommodationOfferingSearchPageData = createAccommodationOfferingSearchPageData();

		updateFacetsStrategy.applyStrategy(criteria, createAccommodationOfferingSearchPageData());
		Assert.assertTrue(CollectionUtils.isNotEmpty(criteria.getFacets()));
	}

	@Test
	public void testApplyStrategy()
	{
		final CriterionData criterionData = createCriterionData();
		final AccommodationOfferingSearchPageData accommodationOfferingSearchPageData = createAccommodationOfferingSearchPageData();
		updateFacetsStrategy.applyStrategy(criterionData, accommodationOfferingSearchPageData);
		Assert.assertTrue(
				StringUtils.equals(criterionData.getFacets().get(3).getValues().get(1).getCode(), TEST_FACE_VALUE_CODE_A));
	}

	public CriterionData createCriterionData()
	{
		final CriterionData criterionData = new CriterionData();
		final List<FacetData<SearchStateData>> facets = new ArrayList<>();
		facets.add(createFacetData(TEST_FACE_CODE_A, false, StringUtils.EMPTY));
		facets.add(createFacetData(TEST_FACE_CODE_B, false, StringUtils.EMPTY));
		facets.add(createFacetData(TEST_FACE_CODE_C, true, TEST_FACE_VALUE_CODE_A));
		facets.add(createFacetData(TEST_FACE_CODE_D, true, TEST_FACE_VALUE_CODE_B));
		criterionData.setFacets(facets);

		return criterionData;
	}

	public AccommodationOfferingSearchPageData createAccommodationOfferingSearchPageData()
	{
		final AccommodationOfferingSearchPageData accommodationOfferingSearchPageData = new AccommodationOfferingSearchPageData();
		final List<FacetData<SearchStateData>> facets = new ArrayList<>();
		facets.add(createFacetData(TEST_FACE_CODE_A, false, StringUtils.EMPTY));
		facets.add(createFacetData(TEST_FACE_CODE_B, true, TEST_FACE_VALUE_CODE_A));
		facets.add(createFacetData(TEST_FACE_CODE_C, true, TEST_FACE_VALUE_CODE_A));
		facets.add(createFacetData(TEST_FACE_CODE_D, true, TEST_FACE_VALUE_CODE_A));
		facets.add(createFacetData(TEST_FACE_CODE_E, true, TEST_FACE_VALUE_CODE_A));

		accommodationOfferingSearchPageData.setFacets(facets);
		return accommodationOfferingSearchPageData;

	}


	public FacetData<SearchStateData> createFacetData(final String facetCode, final boolean hasValues, final String facetValueCode)
	{
		final FacetData<SearchStateData> facetData = new FacetData<>();
		facetData.setCode(facetCode);
		final List<FacetValueData<SearchStateData>> facetValues = new ArrayList<>(1);
		if (hasValues)
		{
			facetValues.add(createFacetValueData(facetValueCode));
		}
		facetData.setValues(facetValues);
		return facetData;
	}

	public FacetValueData<SearchStateData> createFacetValueData(final String facetValueCode)
	{
		final FacetValueData<SearchStateData> facetValueData = new FacetValueData<SearchStateData>();
		facetValueData.setCode(facetValueCode);

		return facetValueData;
	}
}
