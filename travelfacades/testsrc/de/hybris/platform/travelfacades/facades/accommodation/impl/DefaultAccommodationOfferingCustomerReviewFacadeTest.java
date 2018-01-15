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

package de.hybris.platform.travelfacades.facades.accommodation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.services.AccommodationOfferingCustomerReviewService;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultAccommodationOfferingCustomerReviewFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationOfferingCustomerReviewFacadeTest
{
	@InjectMocks
	DefaultAccommodationOfferingCustomerReviewFacade defaultAccommodationOfferingCustomerReviewFacade;
	@Mock
	private AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService;
	@Mock
	private Converter<CustomerReviewModel, ReviewData> accommodationOfferingCustomerReviewConverter;
	@Mock
	private Converter<ReviewData, CustomerReviewModel> accommodationCustomerReviewReverseConverter;

	@Test
	public void testGetAccommodationOfferingCustomerReviewDetails()
	{
		final PageableData pageableData = new PageableData();
		final SearchPageData<CustomerReviewModel> source=new SearchPageData();


		given(accommodationOfferingCustomerReviewService.getReviewForAccommodationOffering("AccommodationOfferingCode",
				pageableData)).willReturn(source);

		Assert.assertNotNull(defaultAccommodationOfferingCustomerReviewFacade
				.getAccommodationOfferingCustomerReviewDetails("AccommodationOfferingCode", pageableData));
	}

	@Test
	public void testRetrieveCustomerReviewByBooking()
	{
		final CustomerReviewModel customerReviewModel = new CustomerReviewModel();
		final List<CustomerReviewModel> reviewList = Collections.singletonList(customerReviewModel);
		final ReviewData reviewData = new ReviewData();
		final List<ReviewData> reviews = Collections.singletonList(reviewData);


		given(accommodationOfferingCustomerReviewService.retrieveCustomerReviewsForBooking("bookingReference",
				"accommodationOfferingCode")).willReturn(reviewList);
		given(accommodationOfferingCustomerReviewConverter.convertAll(reviewList)).willReturn(reviews);

		Assert.assertNotNull(defaultAccommodationOfferingCustomerReviewFacade.retrieveCustomerReviewByBooking("bookingReference",
				"accommodationOfferingCode"));

		given(accommodationOfferingCustomerReviewService.retrieveCustomerReviewsForBooking("bookingReference",
				"accommodationOfferingCode")).willThrow(ModelNotFoundException.class);

		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationOfferingCustomerReviewFacade
				.retrieveCustomerReviewByBooking("bookingReference", "accommodationOfferingCode")));
	}

	@Test
	public void testRetrieveCustomerReviewByRefNumber()
	{
		final CustomerReviewModel review = new CustomerReviewModel();
		final ReviewData reviewData = new ReviewData();

		given(accommodationOfferingCustomerReviewService.retrieveCustomerReviewForRoomStay("bookingReference",
				"accommodationOfferingCode", 0)).willReturn(review);
		given(accommodationOfferingCustomerReviewConverter.convert(review)).willReturn(reviewData);

		Assert.assertEquals(reviewData, defaultAccommodationOfferingCustomerReviewFacade
				.retrieveCustomerReviewByRefNumber("bookingReference",
				0, "accommodationOfferingCode"));

		given(accommodationOfferingCustomerReviewService.retrieveCustomerReviewForRoomStay("bookingReference",
				"accommodationOfferingCode", 0)).willThrow(ModelNotFoundException.class);

		Assert.assertNull(defaultAccommodationOfferingCustomerReviewFacade.retrieveCustomerReviewByRefNumber("bookingReference", 0,
				"accommodationOfferingCode"));
	}

	@Test
	public void testPostReview()
	{
		final CustomerReviewModel review = new CustomerReviewModel();
		final ReviewData reviewData = new ReviewData();

		given(accommodationCustomerReviewReverseConverter.convert(reviewData)).willReturn(review);
		BDDMockito.willDoNothing().given(accommodationOfferingCustomerReviewService).saveReview(review);

		Assert.assertTrue(defaultAccommodationOfferingCustomerReviewFacade.postReview(reviewData));

		given(accommodationCustomerReviewReverseConverter.convert(reviewData)).willThrow(ModelNotFoundException.class);

		Assert.assertFalse(defaultAccommodationOfferingCustomerReviewFacade.postReview(reviewData));

	}

}
