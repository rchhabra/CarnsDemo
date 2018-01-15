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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.travel.AccommodationBookingActionData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.services.AccommodationOfferingCustomerReviewService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link PreviouslySubmittedReviewRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PreviouslySubmittedReviewRestrictionStrategyTest
{
	@InjectMocks
	PreviouslySubmittedReviewRestrictionStrategy strategy;
	@Mock
	private AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService;

	@Mock
	private CustomerReviewModel review;


	@Test
	public void testBookingAction()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		actionData.setRoomStayRefNumber(0);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setCode("acc1");
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(0);
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setAccommodationOfferingCode("ao1");
		accommodationReservationData.setAccommodationReference(accommodationReference);
		given(accommodationOfferingCustomerReviewService.retrieveCustomerReviewForRoomStay(Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).willReturn(review);
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithEmptyActions()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(false);
		actionData.setRoomStayRefNumber(0);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionThatThrowException()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		actionData.setRoomStayRefNumber(0);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setCode("acc1");
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(0);
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setAccommodationOfferingCode("ao1");
		accommodationReservationData.setAccommodationReference(accommodationReference);
		given(accommodationOfferingCustomerReviewService.retrieveCustomerReviewForRoomStay(Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).willThrow(new ModelNotFoundException("Model Not Found"));
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
	}


	@Test
	public void testBookingActionWithEmptyReview()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		actionData.setRoomStayRefNumber(0);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setCode("acc1");
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(0);
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setAccommodationOfferingCode("ao1");
		accommodationReservationData.setAccommodationReference(accommodationReference);
		given(accommodationOfferingCustomerReviewService.retrieveCustomerReviewForRoomStay(Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).willReturn(null);
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}

	@Test
	public void testBookingActionWithUnEqualRoomStayRefNumbers()
	{
		final List<AccommodationBookingActionData> bookingActionDataList = new ArrayList<>();
		final AccommodationBookingActionData actionData = new AccommodationBookingActionData();
		actionData.setAlternativeMessages(new ArrayList<>());
		actionData.setEnabled(true);
		actionData.setRoomStayRefNumber(1);
		bookingActionDataList.add(actionData);

		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationData.setCode("acc1");
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(0);
		accommodationReservationData.setRoomStays(Stream.of(roomStay).collect(Collectors.toList()));
		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setAccommodationOfferingCode("ao1");
		accommodationReservationData.setAccommodationReference(accommodationReference);
		given(accommodationOfferingCustomerReviewService.retrieveCustomerReviewForRoomStay(Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).willReturn(review);
		strategy.applyStrategy(bookingActionDataList, accommodationReservationData);
		Assert.assertTrue(bookingActionDataList.get(0).isEnabled());
	}


}
