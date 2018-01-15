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

package de.hybris.platform.travelfacades.facades.packages.manager.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.packages.request.AccommodationPackageRequestData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationDetailsPipelineManager;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageReservedRoomStaysPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.order.TravelCartService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultAmendPackageDetailsPipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAmendPackageDetailsPipelineManagerTest
{

	@InjectMocks
	DefaultAmendPackageDetailsPipelineManager defaultAmendPackageDetailsPipelineManager;

	@Mock
	private TravelCartService travelCartService;

	@Mock
	private AccommodationDetailsPipelineManager accommodationDetailsPipelineManager;

	@Mock
	private PackageReservedRoomStaysPipelineManager packageReservedRoomStaysPipelineManager;

	@Mock
	private ReservationPipelineManager packageTransportReservationSummaryPipelineManager;

	private List<PackageResponseHandler> handlers;

	@Before
	public void setUp()
	{
		defaultAmendPackageDetailsPipelineManager.setHandlers(Collections.emptyList());
	}

	@Test
	public void testExecutePipelineForEmptySessionCart()
	{
		final TestDataSetUp testDataSetup = new TestDataSetUp();
		final PackageRequestData packageRequestData = new PackageRequestData();

		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new AccommodationAvailabilityRequestData();


		final PropertyData property = testDataSetup.createPropertData("TEST_ACCOMMODATION_OFFERING_CODE");
		final CriterionData criterianData = testDataSetup.createCriterionData(property, testDataSetup.createRoomStayCandidates(0));

		final AccommodationAvailabilityResponseData availabilityResponseData = new AccommodationAvailabilityResponseData();
		availabilityResponseData.setAccommodationReference(property);
		availabilityResponseData.setRoomStays(Arrays.asList(testDataSetup.createRoomStays(testDataSetup.createRoomTypes(), 1)));

		Mockito.when(accommodationDetailsPipelineManager.executePipeline(accommodationAvailabilityRequestData))
				.thenReturn(availabilityResponseData);
		Mockito.when(travelCartService.hasSessionCart()).thenReturn(Boolean.FALSE);
		accommodationAvailabilityRequestData.setCriterion(criterianData);
		final AccommodationPackageRequestData accommodationPackageRequestData = new AccommodationPackageRequestData();
		accommodationPackageRequestData.setAccommodationAvailabilityRequest(accommodationAvailabilityRequestData);
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequestData);
		final PackageResponseData packageResponse = defaultAmendPackageDetailsPipelineManager.executePipeline(packageRequestData);
		Assert.assertNull(packageResponse.getTransportPackageResponse());
		Assert.assertTrue(CollectionUtils.isEmpty(
				packageResponse.getAccommodationPackageResponse().getAccommodationAvailabilityResponse().getReservedRoomStays()));
	}

	@Test
	public void testExecutePipelineForInvalidRoomStayCandidateData()
	{
		final TestDataSetUp testDataSetup = new TestDataSetUp();
		final PackageRequestData packageRequestData = new PackageRequestData();

		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new AccommodationAvailabilityRequestData();
		final PropertyData property = testDataSetup.createPropertData("TEST_ACCOMMODATION_OFFERING_CODE");
		final CriterionData criterianData = testDataSetup.createCriterionData(property, testDataSetup.createRoomStayCandidates(0));

		final AccommodationAvailabilityResponseData availabilityResponseData = new AccommodationAvailabilityResponseData();
		availabilityResponseData.setAccommodationReference(property);
		availabilityResponseData.setRoomStays(Arrays.asList(testDataSetup.createRoomStays(testDataSetup.createRoomTypes(), 0)));
		availabilityResponseData.setReservedRoomStays(
				Arrays.asList(testDataSetup.createReservedRoomStay(0, true), testDataSetup.createReservedRoomStay(1, true)));

		Mockito.when(accommodationDetailsPipelineManager.executePipeline(accommodationAvailabilityRequestData))
				.thenReturn(availabilityResponseData);
		Mockito.when(travelCartService.hasSessionCart()).thenReturn(Boolean.TRUE);

		final CartModel cartModel = new CartModel();
		Mockito.when(travelCartService.getSessionCart()).thenReturn(cartModel);
		final ReservationData reservationData = new ReservationData();
		Mockito.when(packageTransportReservationSummaryPipelineManager.executePipeline(cartModel)).thenReturn(reservationData);
		accommodationAvailabilityRequestData.setCriterion(criterianData);
		final AccommodationPackageRequestData accommodationPackageRequestData = new AccommodationPackageRequestData();
		accommodationPackageRequestData.setAccommodationAvailabilityRequest(accommodationAvailabilityRequestData);
		accommodationPackageRequestData.setAccommodationSearchRequest(new AccommodationSearchRequestData());
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequestData);
		Mockito.doNothing().when(packageReservedRoomStaysPipelineManager).executePipeline(packageRequestData,
				availabilityResponseData);
		final PackageResponseData packageResponse = defaultAmendPackageDetailsPipelineManager.executePipeline(packageRequestData);
		Assert.assertEquals(packageResponse.getTransportPackageResponse().getReservationData(), reservationData);
		Assert.assertTrue(CollectionUtils.isNotEmpty(
				packageResponse.getAccommodationPackageResponse().getAccommodationAvailabilityResponse().getReservedRoomStays()));
	}


	private class TestDataSetUp
	{

		public RoomTypeData createRoomTypes()
		{
			final RoomTypeData roomtypeData = new RoomTypeData();
			roomtypeData.setBedType("Single Bed");
			return roomtypeData;
		}

		public RoomStayData createRoomStays(final RoomTypeData roomType, final int roomStayRefNumber)
		{
			final RoomStayData roomStay = new RoomStayData();
			roomStay.setRoomStayRefNumber(roomStayRefNumber);
			return roomStay;
		}

		public ReservedRoomStayData createReservedRoomStay(final int roomStayRefNumber, final boolean nonModifiable)
		{
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			roomStay.setRoomStayRefNumber(roomStayRefNumber);
			roomStay.setNonModifiable(nonModifiable);
			return roomStay;
		}

		public RoomStayCandidateData createRoomStayCandidates(final int roomStayCandidateRefNumber)
		{
			final RoomStayCandidateData roomStayCandidate = new RoomStayCandidateData();
			roomStayCandidate.setRoomStayCandidateRefNumber(roomStayCandidateRefNumber);
			roomStayCandidate.setRatePlanCode("ratePlan" + roomStayCandidateRefNumber);
			return roomStayCandidate;
		}

		public PropertyData createPropertData(final String accommodationOfferingCode)
		{
			final PropertyData property = new PropertyData();
			property.setAccommodationOfferingCode(accommodationOfferingCode);
			return property;
		}

		public CriterionData createCriterionData(final PropertyData accommodationReference,
				final RoomStayCandidateData roomStayCandidateData)
		{
			final CriterionData criterion = new CriterionData();
			criterion.setAccommodationReference(accommodationReference);
			criterion.setRoomStayCandidates(Stream.of(roomStayCandidateData).collect(Collectors.toList()));
			return criterion;
		}
	}
}
