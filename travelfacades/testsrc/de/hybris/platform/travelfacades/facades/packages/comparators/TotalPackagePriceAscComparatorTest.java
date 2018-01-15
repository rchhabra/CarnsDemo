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

package de.hybris.platform.travelfacades.facades.packages.comparators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TotalPackagePriceAscComparator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TotalPackagePriceAscComparatorTest
{
	@InjectMocks
	TotalPackagePriceAscComparator totalPackagePriceAscComparator;

	@Test
	public void testCompareForNullArguments()
	{
		Assert.assertEquals(totalPackagePriceAscComparator.compare(null, null), 0);
		Assert.assertEquals(totalPackagePriceAscComparator.compare(new PropertyData(), new PropertyData()), 0);
		final PackageData packageData = new PackageData();
		packageData.setTotalPackagePrice(createPriceData(100d));
		Assert.assertEquals(totalPackagePriceAscComparator.compare(null, packageData), -1);
		Assert.assertEquals(totalPackagePriceAscComparator.compare(new PackageData(), packageData), -1);
		Assert.assertEquals(totalPackagePriceAscComparator.compare(packageData, null), 1);
		Assert.assertEquals(totalPackagePriceAscComparator.compare(packageData, new PackageData()), 1);

	}

	@Test
	public void testCompare()
	{
		final PackageData packageData1 = new PackageData();
		packageData1.setTotalPackagePrice(createPriceData(100d));
		final PackageData packageData2 = new PackageData();
		packageData2.setTotalPackagePrice(createPriceData(200d));
		Assert.assertEquals(totalPackagePriceAscComparator.compare(packageData1, packageData2), -1);
	}

	private PriceData createPriceData(final double price)
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(price));
		priceData.setCurrencyIso("GBP");
		priceData.setFormattedValue("GBP " + price);
		return priceData;
	}

}
