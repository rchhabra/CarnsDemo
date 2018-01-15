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
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link UpdateQueryStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateQueryStrategyTest
{

	@InjectMocks
	UpdateQueryStrategy updateQueryStrategy;

	private final String TEST_QUERY_CODE = "TEST_QUERY_CODE";
	@Test
	public void testApplyStrategy()
	{
		final CriterionData criterionData = new CriterionData();
		final AccommodationOfferingSearchPageData accommodationOfferingSearchPageData = new AccommodationOfferingSearchPageData();
		final SearchStateData searchStateData = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(TEST_QUERY_CODE);
		searchStateData.setQuery(searchQueryData);
		accommodationOfferingSearchPageData.setCurrentQuery(searchStateData);
		updateQueryStrategy.applyStrategy(criterionData, accommodationOfferingSearchPageData);

		Assert.assertEquals(TEST_QUERY_CODE, criterionData.getQuery());

	}
}
