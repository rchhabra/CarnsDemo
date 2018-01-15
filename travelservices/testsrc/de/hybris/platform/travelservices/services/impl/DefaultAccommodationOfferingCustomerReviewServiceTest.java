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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelservices.dao.AccommodationCustomerReviewDao;
import de.hybris.platform.travelservices.dao.PagedAccommodationOfferingCustomerReviewDao;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultAccommodationOfferingCustomerReviewService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationOfferingCustomerReviewServiceTest
{
	@InjectMocks
	private DefaultAccommodationOfferingCustomerReviewService customerReviewService;
	@Mock
	private PagedAccommodationOfferingCustomerReviewDao pagedAccommodationOfferingCustomerReviewDao;
	@Mock
	private AccommodationCustomerReviewDao accommodationCustomerReviewDao;
	@Mock
	private UserService userService;
	@Mock
	private ModelService modelService;
	@Mock
	private CustomerReviewModel customerReviewModel;
	@Mock
	private CustomerModel customer;

	@Test
	public void testGetReviewForAccommodationOffering()
	{
		Mockito.when(pagedAccommodationOfferingCustomerReviewDao.findPagedCustomerReview(Matchers.any(), Matchers.any()))
				.thenReturn(Mockito.mock(SearchPageData.class));
		Assert.assertNotNull(
				customerReviewService.getReviewForAccommodationOffering(Mockito.anyString(), Mockito.any(PageableData.class)));
	}

	@Test
	public void testRetrieveCustomerReviewsForBooking()
	{
		Mockito
				.when(accommodationCustomerReviewDao.getCustomerReviewsForBooking(Matchers.anyString(),
						Matchers.any(CustomerModel.class), Matchers.anyString()))
				.thenReturn(Stream.of(customerReviewModel).collect(Collectors.toList()));

		Assert.assertNotNull(customerReviewService.retrieveCustomerReviewsForBooking("0001", "acco1"));
	}

	@Test
	public void testRetrieveCustomerReviewsForRoomStay()
	{
		final CustomerReviewModel customerReviewModel = new CustomerReviewModel();
		customerReviewModel.setRoomStayRefNumber(0);
		Mockito
				.when(accommodationCustomerReviewDao.getCustomerReviewsForBooking(Matchers.anyString(),
						Matchers.any(CustomerModel.class), Matchers.anyString()))
				.thenReturn(Stream.of(customerReviewModel).collect(Collectors.toList()));

		Assert.assertNotNull(customerReviewService.retrieveCustomerReviewForRoomStay("0001", "acco1", 0));
	}

	@Test(expected = ModelNotFoundException.class)
	public void testRetrieveCustomerReviewsForRoomStayWithUnEqualRoomStayRefNumber()
	{
		final CustomerReviewModel customerReviewModel = new CustomerReviewModel();
		customerReviewModel.setRoomStayRefNumber(1);
		Mockito
				.when(accommodationCustomerReviewDao.getCustomerReviewsForBooking(Matchers.anyString(),
						Matchers.any(CustomerModel.class), Matchers.anyString()))
				.thenReturn(Stream.of(customerReviewModel).collect(Collectors.toList()));

		customerReviewService.retrieveCustomerReviewForRoomStay("0001", "acco1", 0);
	}

	@Test
	public void testSaveReview()
	{
		Mockito.doNothing().when(modelService).save(customerReviewModel);

		customerReviewService.saveReview(customerReviewModel);
	}
}
