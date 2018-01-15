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

package de.hybris.platform.travelservices.model.dynamic.attribute;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingCustomerReviewService;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AverageUserRatingAttributeHandlerTest
{
	@InjectMocks
	private AverageUserRatingAttributeHandler averageUserRatingAttributeHandler;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService;

	@Test(expected = IllegalArgumentException.class)
	public void testGetWithException()
	{
		final AccommodationOfferingModel accommodationOfferingModel = null;
		averageUserRatingAttributeHandler.get(accommodationOfferingModel);
	}

	@Test
	public void testGet()
	{
		final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
		accommodationOfferingModel.setCode("AccommodationOfferingCode");
		BDDMockito.given(accommodationOfferingCustomerReviewService
				.getReviewForAccommodationOffering(Matchers.anyString(), Matchers.any(PageableData.class))
				.getResults()).willReturn(Collections.EMPTY_LIST);

		final Double avgUserRating = averageUserRatingAttributeHandler.get(accommodationOfferingModel);
		Assert.assertNull(avgUserRating);

		final CustomerReviewModel customerReviewModel = new CustomerReviewModel();
		customerReviewModel.setRating(7d);
		final List<CustomerReviewModel> results = Collections.singletonList(customerReviewModel);
		BDDMockito.given(accommodationOfferingCustomerReviewService
				.getReviewForAccommodationOffering(Matchers.anyString(), Matchers.any(PageableData.class))
				.getResults()).willReturn(results);
		final double avgUserRating1 = averageUserRatingAttributeHandler.get(accommodationOfferingModel);
		Assert.assertEquals(7d, avgUserRating1, 0d);
	}

}
