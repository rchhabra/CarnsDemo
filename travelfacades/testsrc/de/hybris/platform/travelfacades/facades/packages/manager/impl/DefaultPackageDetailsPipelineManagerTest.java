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
import de.hybris.platform.commercefacades.packages.request.TransportPackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationDetailsPipelineManager;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageReservedRoomStaysPipelineManager;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;

import java.util.ArrayList;
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultPackageDetailsPipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPackageDetailsPipelineManagerTest
{
	@InjectMocks
	DefaultPackageDetailsPipelineManager defaultPackageDetailsPipelineManager;

	@Mock
	private AccommodationDetailsPipelineManager accommodationDetailsPipelineManager;

	@Mock
	private PackageReservedRoomStaysPipelineManager packageReservedRoomStaysPipelineManager;

	@Mock
	private FareSearchFacade fareSearchFacade;

	@Before
	public void setUp()
	{
		defaultPackageDetailsPipelineManager.setHandlers(Collections.emptyList());
	}

	@Test
	public void testExecutePipelineForEmpty()
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
		accommodationAvailabilityRequestData.setCriterion(criterianData);
		final AccommodationPackageRequestData accommodationPackageRequestData = new AccommodationPackageRequestData();
		accommodationPackageRequestData.setAccommodationAvailabilityRequest(accommodationAvailabilityRequestData);
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequestData);
		final PackageResponseData packageResponse = defaultPackageDetailsPipelineManager.executePipeline(packageRequestData);
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
		availabilityResponseData.setRoomStays(Arrays.asList(testDataSetup.createRoomStays(testDataSetup.createRoomTypes(), 1)));
		availabilityResponseData.setReservedRoomStays(
				Arrays.asList(testDataSetup.createReservedRoomStay(0, true), testDataSetup.createReservedRoomStay(0, true)));
		accommodationAvailabilityRequestData.setCriterion(criterianData);
		Mockito.when(accommodationDetailsPipelineManager.executePipeline(accommodationAvailabilityRequestData))
				.thenReturn(availabilityResponseData);
		final AccommodationPackageRequestData accommodationPackageRequestData = new AccommodationPackageRequestData();
		accommodationPackageRequestData.setAccommodationAvailabilityRequest(accommodationAvailabilityRequestData);
		accommodationPackageRequestData.setAccommodationSearchRequest(new AccommodationSearchRequestData());
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequestData);


		final FareSelectionData fareSelectionData = testDataSetup.createFareSelectionData();

		final TransportPackageRequestData transportPackageRequestData = new TransportPackageRequestData();
		transportPackageRequestData.setFareSearchRequest(new FareSearchRequestData());

		packageRequestData.setTransportPackageRequest(transportPackageRequestData);
		Mockito.when(fareSearchFacade.doSearch(Matchers.any(FareSearchRequestData.class))).thenReturn(fareSelectionData);

		Mockito.doNothing().when(packageReservedRoomStaysPipelineManager).executePipeline(packageRequestData,
				availabilityResponseData);
		final PackageResponseData packageResponse = defaultPackageDetailsPipelineManager.executePipeline(packageRequestData);

		Assert.assertNull(packageResponse.getTransportPackageResponse().getReservationData());
		Assert.assertEquals(packageResponse.getTransportPackageResponse().getFareSearchResponse(), fareSelectionData);
		Assert.assertTrue(CollectionUtils.isEmpty(
				packageResponse.getAccommodationPackageResponse().getAccommodationAvailabilityResponse().getReservedRoomStays()));
	}

	@Test
	public void testExecutePipelineForValidRoomStayCandidateData()
	{
		final TestDataSetUp testDataSetup = new TestDataSetUp();
		final PackageRequestData packageRequestData = new PackageRequestData();

		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new AccommodationAvailabilityRequestData();


		final PropertyData property = testDataSetup.createPropertData("TEST_ACCOMMODATION_OFFERING_CODE");
		final CriterionData criterianData = testDataSetup.createCriterionData(property, testDataSetup.createRoomStayCandidates(0));

		final AccommodationAvailabilityResponseData availabilityResponseData = new AccommodationAvailabilityResponseData();
		availabilityResponseData.setAccommodationReference(property);
		availabilityResponseData.setRoomStays(Arrays.asList(testDataSetup.createRoomStays(testDataSetup.createRoomTypes(), 0)));
		accommodationAvailabilityRequestData.setCriterion(criterianData);
		Mockito.when(accommodationDetailsPipelineManager.executePipeline(accommodationAvailabilityRequestData))
				.thenReturn(availabilityResponseData);
		final AccommodationPackageRequestData accommodationPackageRequestData = new AccommodationPackageRequestData();
		accommodationPackageRequestData.setAccommodationAvailabilityRequest(accommodationAvailabilityRequestData);
		accommodationPackageRequestData.setAccommodationSearchRequest(new AccommodationSearchRequestData());
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequestData);


		final FareSelectionData fareSelectionData = testDataSetup.createFareSelectionData();

		final TransportPackageRequestData transportPackageRequestData = new TransportPackageRequestData();
		transportPackageRequestData.setFareSearchRequest(new FareSearchRequestData());

		packageRequestData.setTransportPackageRequest(transportPackageRequestData);
		Mockito.when(fareSearchFacade.doSearch(Matchers.any(FareSearchRequestData.class))).thenReturn(fareSelectionData);

		Mockito.doNothing().when(packageReservedRoomStaysPipelineManager).executePipeline(packageRequestData,
				availabilityResponseData);
		final PackageResponseData packageResponse = defaultPackageDetailsPipelineManager.executePipeline(packageRequestData);

		Assert.assertNull(packageResponse.getTransportPackageResponse().getReservationData());
		Assert.assertEquals(packageResponse.getTransportPackageResponse().getFareSearchResponse(), fareSelectionData);
		Assert.assertTrue(CollectionUtils.isEmpty(
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

		public List<PricedItineraryData> createPricedItineraries()
		{
			final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
			pricedItineraries.add(createPricedItinerary(true, 0));
			pricedItineraries.add(createPricedItinerary(true, 1));
			return pricedItineraries;
		}

		public PricedItineraryData createPricedItinerary(final boolean isAvailable, final int id)
		{
			final PricedItineraryData pricedItinerary = new PricedItineraryData();
			pricedItinerary.setAvailable(isAvailable);
			pricedItinerary.setId(id);
			return pricedItinerary;
		}

		public FareSelectionData createFareSelectionData()
		{
			final FareSelectionData fareSelectionData = new FareSelectionData();
			fareSelectionData.setPricedItineraries(createPricedItineraries());
			return fareSelectionData;
		}

	}
}
