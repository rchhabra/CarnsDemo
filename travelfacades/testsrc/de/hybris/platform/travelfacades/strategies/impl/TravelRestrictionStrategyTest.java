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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.junit.Assert;

@UnitTest
public class TravelRestrictionStrategyTest
{
	private TravelRestrictionStrategy travelRestrictionStrategy;
	
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		travelRestrictionStrategy = new TravelRestrictionStrategy();
	}
	@Test
	public void testCheckQuantityForTravelRestrictionWhenQuantityIsValid() {
		
		final TravelRestrictionModel travelRestrictionModel = new TravelRestrictionModel();
		travelRestrictionModel.setTravellerMinOfferQty(1);
		travelRestrictionModel.setTravellerMaxOfferQty(10);
		
		final boolean isvalid = travelRestrictionStrategy.checkQuantityForTravelRestriction(travelRestrictionModel, 2L);
		
		Assert.assertTrue(isvalid);
		
	}

	@Test
	public void testCheckQuantityForTravelRestrictionWhenQuantityIsZero()
	{

		final TravelRestrictionModel travelRestrictionModel = new TravelRestrictionModel();
		travelRestrictionModel.setTravellerMinOfferQty(1);
		travelRestrictionModel.setTravellerMaxOfferQty(10);

		final boolean isvalid = travelRestrictionStrategy.checkQuantityForTravelRestriction(travelRestrictionModel, 0L);

		Assert.assertTrue(isvalid);

	}

	@Test
	public void testCheckQuantityForTravelRestrictionWithDefaultValues()
	{

		final TravelRestrictionModel travelRestrictionModel = new TravelRestrictionModel();

		final boolean isvalid = travelRestrictionStrategy.checkQuantityForTravelRestriction(null, 1L);

		Assert.assertTrue(isvalid);

	}

	@Test
	public void testCheckQuantityForMandatoryTravelRestriction()
	{

		final TravelRestrictionModel travelRestrictionModel = new TravelRestrictionModel();
		travelRestrictionModel.setTravellerMinOfferQty(1);
		travelRestrictionModel.setTravellerMaxOfferQty(10);

		final boolean isvalid = travelRestrictionStrategy.checkQuantityForMandatoryTravelRestriction(travelRestrictionModel, 1L);

		Assert.assertTrue(isvalid);

	}


}
