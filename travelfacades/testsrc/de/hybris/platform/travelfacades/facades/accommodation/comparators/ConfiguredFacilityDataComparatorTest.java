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

package de.hybris.platform.travelfacades.facades.accommodation.comparators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.property.FacilityData;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfiguredFacilityDataComparatorTest
{
	@InjectMocks
	private ConfiguredFacilityDataComparator comparator;

	@Test
	public void testCompare()
	{
		final FacilityData fd1 = new FacilityData();
		fd1.setFacilityType("wifi");
		final FacilityData fd2 = new FacilityData();
		fd2.setFacilityType("gym");
		Assert.assertTrue(comparator.compare(fd1, fd2) > 0);
	}
}
