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

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TravelRestrictionValidationStrategyTest
{
	private TravelRestrictionValidationStrategy travelRestrictionValidationStrategy;
	@Mock
	private TravelRestrictionFacade travelRestrictionFacade;
	@Mock
	private List<String> transportOfferingCodes;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		travelRestrictionValidationStrategy = new TravelRestrictionValidationStrategy();
		travelRestrictionValidationStrategy.setTravelRestrictionFacade(travelRestrictionFacade);
	}

	@Test
	public void testValidateAddToCart()
	{
		when(travelRestrictionValidationStrategy.getTravelRestrictionFacade().checkIfProductCanBeAdded(Matchers.anyString(),
				Matchers.anyLong(), Matchers.anyString(), Matchers.anyListOf(String.class), Matchers.anyString())).thenReturn(true);

		final AddToCartResponseData addProductToCartResponseData = travelRestrictionValidationStrategy
				.validateAddToCart("Test_Fare_Product", 1, "adult1", transportOfferingCodes, "LGW_CDG");

		Assert.assertNotNull(addProductToCartResponseData);
		Assert.assertEquals(addProductToCartResponseData.isValid(), true);
	}
}
