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

package de.hybris.platform.travelfacades.search.converters.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link SearchResultAccommodationOfferingPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SearchResultAccommodationOfferingPopulatorTest
{
	@InjectMocks
	SearchResultAccommodationOfferingPopulator searchResultAccommodationOfferingPopulator;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	private SearchResultValueData source;
	private AccommodationOfferingDayRateData target;

	final static String TEST_ACCOMMODATOIN_OFFERING_CODE = "TEST_ACCOMMODATOIN_OFFERING_CODE";
	final String TEST_PRICE_VALUE = "TEST_PRICE_VALUE";

	@Before
	public void setup()
	{
		source = TestData.createSearcgResultValueData();
		target = new AccommodationOfferingDayRateData();
	}

	@Test
	public void testPopulate()
	{
		final Map<String, Object> values = source.getValues();
		values.put(TravelfacadesConstants.SOLR_FIELD_PRICE_VALUE, new Double(200d));
		source.setValues(values);
		final PriceData priceData = new PriceData();
		priceData.setFormattedValue(TEST_PRICE_VALUE);
		given(travelCommercePriceFacade.createPriceData(Matchers.anyDouble())).willReturn(priceData);
		searchResultAccommodationOfferingPopulator.populate(source, target);
		Assert.assertSame(priceData, target.getPrice());
	}

	@Test
	public void testPopulateForNullPriceValue()
	{
		searchResultAccommodationOfferingPopulator.populate(source, target);
		Assert.assertEquals(TEST_ACCOMMODATOIN_OFFERING_CODE, target.getAccommodationOfferingCode());
	}

	private static class TestData
	{

		public static SearchResultValueData createSearcgResultValueData()
		{
			final SearchResultValueData source = new SearchResultValueData();
			final Map<String, Object> values = new HashMap<>();
			values.put(TravelfacadesConstants.SOLR_FIELD_PROPERTY_CODE, TEST_ACCOMMODATOIN_OFFERING_CODE);
			values.put(TravelfacadesConstants.SOLR_FIELD_DATE_OF_STAY, "01/01/2016");

			source.setValues(values);
			return source;
		}
	}
}
