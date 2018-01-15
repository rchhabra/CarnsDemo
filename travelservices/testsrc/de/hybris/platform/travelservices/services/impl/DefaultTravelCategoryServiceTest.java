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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.travelservices.dao.TravelCategoryDao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultTravelCategoryService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCategoryServiceTest
{
	@InjectMocks
	private DefaultTravelCategoryService travelCategoryService;
	@Mock
	private TravelCategoryDao travelCategoryDao;

	@Test
	public void getAncillaryCategoriesTest()
	{
		final CategoryModel ancillaryCategory = new CategoryModel();
		Mockito.when(travelCategoryDao.getAncillaryCategories(Matchers.anyList()))
				.thenReturn(Stream.of(ancillaryCategory).collect(Collectors.toList()));

		final List<CategoryModel> result = travelCategoryService.getAncillaryCategories(Arrays.asList("to1"));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}
}
