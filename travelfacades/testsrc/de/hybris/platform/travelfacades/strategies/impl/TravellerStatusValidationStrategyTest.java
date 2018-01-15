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
import de.hybris.platform.travelfacades.facades.CheckInFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class TravellerStatusValidationStrategyTest
{
	private TravellerStatusValidationStrategy travellerStatusValidationStrategy;
	
	@Mock
	private TravelCartFacade cartFacade;
	@Mock
	private CheckInFacade checkInFacade;
	@Mock
	private List<String> transportOfferingCodes;

	
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		travellerStatusValidationStrategy = new TravellerStatusValidationStrategy();
		travellerStatusValidationStrategy.setCartFacade(cartFacade);
		travellerStatusValidationStrategy.setCheckInFacade(checkInFacade);

	}
	
	@Test
	public void testValidateAddToCartWhenAmendment()
	{
		when(travellerStatusValidationStrategy.getCartFacade().isAmendmentCart()).thenReturn(true);
		when(travellerStatusValidationStrategy.getCheckInFacade().checkTravellerEligibility(Matchers.anyString(),
				Matchers.anyListOf(String.class), Matchers.anyString())).thenReturn(true);

		final AddToCartResponseData addProductToCartResponseData = travellerStatusValidationStrategy
				.validateAddToCart("Test_Fare_Product", 1, "adult1", transportOfferingCodes, "LGW_CDG");

		Assert.assertNotNull(addProductToCartResponseData);
		Assert.assertEquals(addProductToCartResponseData.isValid(), true);
	}

	@Test
	public void testValidateAddToCartWhenNoAmendment()
	{
		when(travellerStatusValidationStrategy.getCartFacade().isAmendmentCart()).thenReturn(false);

		final AddToCartResponseData addProductToCartResponseData = travellerStatusValidationStrategy
				.validateAddToCart("Test_Fare_Product", 1, "adult1", transportOfferingCodes, "LGW_CDG");

		Assert.assertNotNull(addProductToCartResponseData);
		Assert.assertEquals(addProductToCartResponseData.isValid(), true);
	}


}
