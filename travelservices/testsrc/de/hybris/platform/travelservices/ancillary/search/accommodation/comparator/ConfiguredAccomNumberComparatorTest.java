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

package de.hybris.platform.travelservices.ancillary.search.accommodation.comparator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link ConfiguredAccomNumberComparator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfiguredAccomNumberComparatorTest
{
	@InjectMocks
	ConfiguredAccomNumberComparator configuredAccomNumberComparator;

	@Test
	public void testCompare()
	{
		final ConfiguredAccommodationModel rowCol1 = new ConfiguredAccommodationModel();
		rowCol1.setNumber(10);
		final ConfiguredAccommodationModel rowCol2 = new ConfiguredAccommodationModel();
		rowCol2.setNumber(20);
		Assert.assertEquals(-1, configuredAccomNumberComparator.compare(rowCol1, rowCol2));

		rowCol1.setNumber(20);
		rowCol2.setNumber(10);
		Assert.assertEquals(1, configuredAccomNumberComparator.compare(rowCol1, rowCol2));

		rowCol1.setNumber(10);
		rowCol2.setNumber(10);
		Assert.assertEquals(0, configuredAccomNumberComparator.compare(rowCol1, rowCol2));
	}
}
