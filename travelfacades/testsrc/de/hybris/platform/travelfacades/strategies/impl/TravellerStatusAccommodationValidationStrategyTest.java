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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.travelfacades.facades.CheckInFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TravellerStatusAccommodationValidationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravellerStatusAccommodationValidationStrategyTest
{
	@InjectMocks
	TravellerStatusAccommodationValidationStrategy travellerStatusAccommodationValidationStrategy;

	@Mock
	private TravelCartFacade cartFacade;

	@Mock
	private CheckInFacade checkInFacade;

	private final String TEST_TRANSPORT_OFFERING_CODES_FOR_AMEND = "TEST_TRANSPORT_OFFERING_CODES_FOR_AMEND";
	private final String TEST_TRANSPORT_OFFERING_CODES_FOR_NO_AMEND = "TEST_TRANSPORT_OFFERING_CODES_FOR_NO_AMEND";
	private final String TEST_ORIGINAL_ORDER_CODE = "TEST_ORIGINAL_ORDER_CODE";

	@Before
	public void setUp()
	{
		given(cartFacade.isAmendmentCart()).willReturn(Boolean.TRUE);
		given(cartFacade.getOriginalOrderCode()).willReturn(TEST_ORIGINAL_ORDER_CODE);
		given(checkInFacade.checkTravellerEligibility(StringUtils.EMPTY, Arrays.asList(TEST_TRANSPORT_OFFERING_CODES_FOR_AMEND),
				TEST_ORIGINAL_ORDER_CODE)).willReturn(Boolean.TRUE);
	}

	@Test
	public void testValidateSelectedAccommodationForValidAmendmentCart()
	{
		final AddToCartResponseData actualResult = travellerStatusAccommodationValidationStrategy.validateSelectedAccommodation(
				StringUtils.EMPTY, StringUtils.EMPTY, TEST_TRANSPORT_OFFERING_CODES_FOR_AMEND, StringUtils.EMPTY, StringUtils.EMPTY,
				StringUtils.EMPTY);
		Assert.assertTrue(actualResult.isValid());
		Assert.assertNull(actualResult.getErrors());
	}

	@Test
	public void testValidateSelectedAccommodationForInValidAmendmentCart()
	{
		final AddToCartResponseData actualResult = travellerStatusAccommodationValidationStrategy.validateSelectedAccommodation(
				StringUtils.EMPTY, StringUtils.EMPTY, TEST_TRANSPORT_OFFERING_CODES_FOR_NO_AMEND, StringUtils.EMPTY,
				StringUtils.EMPTY, StringUtils.EMPTY);
		Assert.assertFalse(actualResult.isValid());
		Assert.assertTrue(actualResult.getErrors().contains("add.accommodation.to.cart.validation.error.traveller.status"));
	}

	@Test
	public void testValidateSelectedAccommodationForNoAmendmentCart()
	{
		given(cartFacade.isAmendmentCart()).willReturn(Boolean.FALSE);
		final AddToCartResponseData actualResult = travellerStatusAccommodationValidationStrategy.validateSelectedAccommodation(
				StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
		Assert.assertTrue(actualResult.isValid());
		Assert.assertNull(actualResult.getErrors());
	}
}
