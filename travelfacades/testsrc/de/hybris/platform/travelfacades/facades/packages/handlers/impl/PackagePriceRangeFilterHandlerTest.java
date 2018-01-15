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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackagePriceRangeFilterHandlerTest
{
	@InjectMocks
	PackagePriceRangeFilterHandler packagePriceRangeFilterHandler;

	@Test
	public void testHandle()
	{
		final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates = new ArrayList<>();
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();
		final AccommodationSearchResponseData accommodationSearchResponse = new AccommodationSearchResponseData();

		packageSearchRequestData.setMinPrice(0l);
		packageSearchRequestData.setMaxPrice(200l);

		final List<PropertyData> properties = new ArrayList<>();
		final PackageData packageData = new PackageData();

		final PriceData totalPackagePrice = new PriceData();
		totalPackagePrice.setValue(BigDecimal.valueOf(110));
		packageData.setTotalPackagePrice(totalPackagePrice);

		properties.add(packageData);
		accommodationSearchResponse.setProperties(properties);

		packagePriceRangeFilterHandler.handle(accommodationOfferingDayRates, packageSearchRequestData, accommodationSearchResponse);

		Assert.assertTrue(packageData.getFiltered());
	}
}
