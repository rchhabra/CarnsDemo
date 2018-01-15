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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.RateRangeData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * unit test for {@link PricedPropertiesFirstSortStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PricedPropertiesFirstSortStrategyTest
{
	@InjectMocks
	PricedPropertiesFirstSortStrategy pricedPropertiesFirstSortStrategy;
	
	@Test
	public void testSortForNull()
	{
		pricedPropertiesFirstSortStrategy.sort(null);
	}

	@Test
	public void testSortForEmptyProperties()
	{
		pricedPropertiesFirstSortStrategy.sort(new AccommodationSearchResponseData());
	}

	@Test
	public void testSort()
	{
		final AccommodationSearchResponseData accommodationSearchResponseData = createAccommodationSearchResponseData();
		pricedPropertiesFirstSortStrategy.sort(accommodationSearchResponseData);

		Assert.assertNotNull(accommodationSearchResponseData.getProperties().get(0).getRateRange());
		Assert.assertNull(accommodationSearchResponseData.getProperties()
				.get(accommodationSearchResponseData.getProperties().size() - 1).getRateRange());
	}

	private AccommodationSearchResponseData createAccommodationSearchResponseData()
	{
		final AccommodationSearchResponseData accommodationSearchResponseData = new AccommodationSearchResponseData();
		accommodationSearchResponseData.setProperties(createProperties());
		return accommodationSearchResponseData;
	}

	private List<PropertyData> createProperties()
	{
		final List<PropertyData> properties = new ArrayList<>();
		properties.add(createPropertyData(0));
		properties.add(createPropertyData(200));
		properties.add(createPropertyData(400));
		return properties;
	}

	private PropertyData createPropertyData(final int rateValue)
	{
		final PropertyData property = new PropertyData();

		if (rateValue != 0)
		{
			final RateRangeData rateRange = new RateRangeData();
			final PriceData price = new PriceData();
			price.setValue(BigDecimal.valueOf(rateValue));
			rateRange.setActualRate(price);
			property.setRateRange(rateRange);
		}

		return property;
	}
}
