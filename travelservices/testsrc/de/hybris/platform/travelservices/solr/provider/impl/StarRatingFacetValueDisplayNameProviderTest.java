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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link StarRatingFacetValueDisplayNameProvider}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StarRatingFacetValueDisplayNameProviderTest
{
	@InjectMocks
	StarRatingFacetValueDisplayNameProvider starRatingFacetValueDisplayNameProvider;

	@Test
	public void testGetDisplayName()
	{
		Assert.assertEquals("accommodation.offering.5.star",
				starRatingFacetValueDisplayNameProvider.getDisplayName(null, null, "5"));
	}

	@Test
	public void testGetDisplayNameForEmptyFacetValue()
	{
		Assert.assertEquals("accommodation.offering.0.star",
				starRatingFacetValueDisplayNameProvider.getDisplayName(null, null, null));
	}

	@Test
	public void testGetDisplayNameForFacetValueGreaterThan5()
	{
		Assert.assertEquals("accommodation.offering.5+.star",
				starRatingFacetValueDisplayNameProvider.getDisplayName(null, null, "6"));
	}

}
