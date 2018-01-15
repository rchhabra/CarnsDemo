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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for TravelCustomerReviewPopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelCustomerReviewPopulatorTest
{
	@InjectMocks
	private TravelCustomerReviewPopulator travelCustomerReviewPopulator;

	@Mock
	private TimeService timeService;

	@Test
	public void populateTest()
	{
		final CustomerReviewModel source = Mockito.mock(CustomerReviewModel.class);
		final Date date = new Date();
		given(source.getCreationtime()).willReturn(date);
		given(timeService.getCurrentTime()).willReturn(date);

		final ReviewData target = new ReviewData();
		travelCustomerReviewPopulator.populate(source, target);

		Assert.assertEquals(0l, target.getNumOfDays().longValue());
	}
}
