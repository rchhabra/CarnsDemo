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

package de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.AccommodationOfferingSearchResponseSortStrategy;

import java.math.BigDecimal;
import java.util.Collections;


/**
 * Implementation of {@link AccommodationOfferingSearchResponseSortStrategy} to sort the list of {@link PropertyData} in the
 * {@link AccommodationSearchResponseData} based on the price in an ascending order
 */
public class PriceAscendingSortStrategy implements AccommodationOfferingSearchResponseSortStrategy
{

	@Override
	public void sort(final AccommodationSearchResponseData accommodationSearchResponseData)
	{
		Collections.sort(accommodationSearchResponseData.getProperties(), (property1, property2) -> {
			if (property1.getRateRange() == null && property2.getRateRange() == null)
			{
				return 0;
			}

			if (property1.getRateRange() == null)
			{
				return -1;
			}

			if (property2.getRateRange() == null)
			{
				return 1;
			}
			final BigDecimal propertyRate1 = property1.getRateRange().getActualRate().getValue();
			final BigDecimal propertyRate2 = property2.getRateRange().getActualRate().getValue();
			return propertyRate1.compareTo(propertyRate2);
		});
	}

}
