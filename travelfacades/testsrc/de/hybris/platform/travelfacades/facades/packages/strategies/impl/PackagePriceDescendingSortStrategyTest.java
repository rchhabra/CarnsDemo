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

package de.hybris.platform.travelfacades.facades.packages.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackagePriceDescendingSortStrategyTest
{
	@InjectMocks
	PackagePriceDescendingSortStrategy packagePriceDescendingSortStrategy;

	@Test
	public void testSort()
	{
		final AccommodationSearchResponseData accommodationSearchResponseData = new AccommodationSearchResponseData();
		final List<PropertyData> properties = new ArrayList<>();
		accommodationSearchResponseData.setProperties(properties);

		final PackageData package1 = new PackageData();
		final PackageData package2 = new PackageData();
		properties.add(package1);
		properties.add(package2);

		packagePriceDescendingSortStrategy.sort(accommodationSearchResponseData);

		final PriceData totalPackagePrice = new PriceData();
		totalPackagePrice.setValue(BigDecimal.valueOf(100));


		package2.setTotalPackagePrice(totalPackagePrice);
		packagePriceDescendingSortStrategy.sort(accommodationSearchResponseData);

		package1.setTotalPackagePrice(totalPackagePrice);
		packagePriceDescendingSortStrategy.sort(accommodationSearchResponseData);
	}
}
