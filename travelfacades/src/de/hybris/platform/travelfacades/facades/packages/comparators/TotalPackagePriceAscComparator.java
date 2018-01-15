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

package de.hybris.platform.travelfacades.facades.packages.comparators;

import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;


/**
 * Compares total package prices to sort packages in ascending order
 */
public class TotalPackagePriceAscComparator implements Comparator<PropertyData>
{

	@Override
	public int compare(final PropertyData property1, final PropertyData property2)
	{
		PackageData package1 = null;
		PackageData package2 = null;

		if (property1 instanceof PackageData)
		{
			package1 = (PackageData) property1;
		}

		if (property2 instanceof PackageData)
		{
			package2 = (PackageData) property2;
		}

		if ((Objects.isNull(package1) && Objects.isNull(package2))
				|| (Objects.nonNull(package1) && Objects.isNull(package1.getTotalPackagePrice()) && Objects.nonNull(package2)
						&& Objects.isNull(package2.getTotalPackagePrice())))
		{
			return 0;
		}

		if (Objects.isNull(package1) || Objects.isNull(package1.getTotalPackagePrice()))
		{
			return -1;
		}

		if (Objects.isNull(package2) || Objects.isNull(package2.getTotalPackagePrice()))
		{
			return 1;
		}

		final BigDecimal packagePrice1 = package1.getTotalPackagePrice().getValue();
		final BigDecimal packagePrice2 = package2.getTotalPackagePrice().getValue();
		return packagePrice1.compareTo(packagePrice2);
	}

}
