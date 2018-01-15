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

package de.hybris.platform.travelfacades.ancillary.search.accommodation.comparator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.ancillary.search.accommodation.comparator.ConfiguredAccomNumberComparator;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfiguredAccomNumberComparatorTest
{

	@InjectMocks
	private final ConfiguredAccomNumberComparator configuredAccomNumberComparator = new ConfiguredAccomNumberComparator();

	@Test
	public void accommodationsSortedInAscendingOrderByNumberTest()
	{
		final ConfiguredAccommodationModel configuredAccom1 = new ConfiguredAccommodationModel();
		final ConfiguredAccommodationModel configuredAccom2 = new ConfiguredAccommodationModel();
		final ConfiguredAccommodationModel configuredAccom3 = new ConfiguredAccommodationModel();
		final ConfiguredAccommodationModel configuredAccom4 = new ConfiguredAccommodationModel();
		final ConfiguredAccommodationModel configuredAccom5 = new ConfiguredAccommodationModel();
		configuredAccom1.setNumber(4);
		configuredAccom2.setNumber(5);
		configuredAccom3.setNumber(1);
		configuredAccom4.setNumber(2);
		configuredAccom5.setNumber(4);

		final List<ConfiguredAccommodationModel> configuredAccommodations = new ArrayList<>();
		configuredAccommodations.add(configuredAccom1);
		configuredAccommodations.add(configuredAccom2);
		configuredAccommodations.add(configuredAccom3);
		configuredAccommodations.add(configuredAccom4);
		configuredAccommodations.add(configuredAccom5);

		//before sorting
		Assert.assertEquals(4, configuredAccommodations.get(0).getNumber().intValue());

		Collections.sort(configuredAccommodations, configuredAccomNumberComparator);

		//before sorting
		Assert.assertEquals(1, configuredAccommodations.get(0).getNumber().intValue());

	}

}
