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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultEncodeSavedSearchStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEncodeSavedSearchStrategyTest
{
	@InjectMocks
	DefaultEncodeSavedSearchStrategy defaultEncodeSavedSearchStrategy;

	@Test
	public void testGetEncodedSearch()
	{
		final SavedSearchData savedSearchData = new SavedSearchData();
		savedSearchData.setDepartureLocationName("departureLocationName");
		savedSearchData.setArrivalLocation("arrivalLocation");
		savedSearchData.setArrivalLocationName("arrivalLocationName");
		savedSearchData.setCabinClass("cabinClass");
		savedSearchData.setDepartingDateTime("departingDateTime");
		savedSearchData.setDepartureLocation("departureLocation");
		savedSearchData.setPassengerTypeQuantities(new ArrayList<PassengerTypeQuantityData>());
		savedSearchData.setReturnDateTime("returnDateTime");
		savedSearchData.setTripType("tripType");
		final String expectedOutput = "departureLocation=departureLocation" + "|arrivalLocation=arrivalLocation"
				+ "|departureLocationName=departureLocationName" + "|arrivalLocationName=arrivalLocationName"
				+ "|departingDateTime=departingDateTime" + "|returnDateTime=returnDateTime" + "|cabinClass=cabinClass"
				+ "|tripType=tripType";
		Assert.assertEquals(expectedOutput, defaultEncodeSavedSearchStrategy.getEncodedSearch(savedSearchData));
	}
}
