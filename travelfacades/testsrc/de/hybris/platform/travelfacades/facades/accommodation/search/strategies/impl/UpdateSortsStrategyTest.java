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
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * unit test for {@link UpdateSortsStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateSortsStrategyTest
{
	@InjectMocks
	UpdateSortsStrategy updateSortsStrategy;

	private final String TEST_SORT_DATA_CODE = "TEST_SORT_DATA_CODE";

	@Test
	public void applyStrategyForEmptySorts()
	{
		final SortData sortData = new SortData();
		sortData.setCode(TEST_SORT_DATA_CODE);
		final List<SortData> sorts = Arrays.asList(sortData);

		final AccommodationOfferingSearchPageData searchPageData = new AccommodationOfferingSearchPageData();
		searchPageData.setSorts(sorts);

		final CriterionData criterion = new CriterionData();
		updateSortsStrategy.applyStrategy(criterion, searchPageData);
		Assert.assertEquals(TEST_SORT_DATA_CODE, criterion.getSorts().get(0).getCode());
	}

	@Test
	public void applyStrategy()
	{
		final SortData sortData = new SortData();
		sortData.setCode(TEST_SORT_DATA_CODE);
		final List<SortData> sorts = Arrays.asList(sortData);

		final AccommodationOfferingSearchPageData searchPageData = new AccommodationOfferingSearchPageData();
		final CriterionData criterion = new CriterionData();
		criterion.setSorts(sorts);
		updateSortsStrategy.applyStrategy(criterion, searchPageData);

		Assert.assertSame(sorts, criterion.getSorts());

	}
}
