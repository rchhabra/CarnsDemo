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

package de.hybris.platform.travelfacades.accommodation.search.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.travelfacades.accommodation.search.mock.request.MockAccommodationSearchRequestData;
import de.hybris.platform.travelfacades.accommodation.search.mock.response.MockAccommodationOfferingDayRateDataList;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.PropertyDataBasicHandler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PropertyDataBasicHandlerTest
{
	MockAccommodationSearchRequestData requestBuilder = new MockAccommodationSearchRequestData();
	MockAccommodationOfferingDayRateDataList dayRatesBuilder = new MockAccommodationOfferingDayRateDataList();

	PropertyDataBasicHandler handler = new PropertyDataBasicHandler();

	PropertyData propertyData = new PropertyData();

	@Test
	public void testHandler()
	{

		handler.handle(dayRatesBuilder.buildMapForSingleProperty(), requestBuilder.buildRequestData(), propertyData);
		Assert.assertNotNull(propertyData.getAccommodationOfferingName());
	}
}
