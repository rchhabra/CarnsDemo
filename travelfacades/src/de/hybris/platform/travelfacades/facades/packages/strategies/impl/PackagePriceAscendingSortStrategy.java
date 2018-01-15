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

package de.hybris.platform.travelfacades.facades.packages.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.AccommodationOfferingSearchResponseSortStrategy;

import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of {@link AccommodationOfferingSearchResponseSortStrategy} to sort the list of {@link PackageData} in
 * the {@link AccommodationSearchResponseData} based on the price in an ascending order
 */
public class PackagePriceAscendingSortStrategy implements AccommodationOfferingSearchResponseSortStrategy
{
	private Comparator<PropertyData> totalPackagePriceAscComparator;

	@Override
	public void sort(final AccommodationSearchResponseData accommodationSearchResponseData)
	{
		Collections.sort(accommodationSearchResponseData.getProperties(), totalPackagePriceAscComparator);
	}

	/**
	 * @return the totalPackagePriceAscComparator
	 */
	protected Comparator<PropertyData> getTotalPackagePriceAscComparator()
	{
		return totalPackagePriceAscComparator;
	}

	/**
	 * @param totalPackagePriceAscComparator
	 *           the totalPackagePriceAscComparator to set
	 */
	@Required
	public void setTotalPackagePriceAscComparator(final Comparator<PropertyData> totalPackagePriceAscComparator)
	{
		this.totalPackagePriceAscComparator = totalPackagePriceAscComparator;
	}

}
