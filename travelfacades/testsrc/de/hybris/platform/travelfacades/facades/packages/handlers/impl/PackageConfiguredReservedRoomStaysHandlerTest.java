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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.packages.request.AccommodationPackageRequestData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.AccommodationSearchFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageConfiguredReservedRoomStaysHandlerTest
{
	@InjectMocks
	PackageConfiguredReservedRoomStaysHandler packageConfiguredReservedRoomStaysHandler;

	@Mock
	AccommodationSearchFacade accommodationSearchFacade;

	@Mock
	private AccommodationOfferingFacade accommodationOfferingFacade;

	@Mock
	private BookingFacade bookingFacade;

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();

		final List<ReservedRoomStayData> reservedRoomStays = new ArrayList<>();
		final ReservedRoomStayData reservedRoomStayData = new ReservedRoomStayData();
		reservedRoomStayData.setNonModifiable(false);
		reservedRoomStays.add(reservedRoomStayData);
		accommodationAvailabilityResponseData.setReservedRoomStays(reservedRoomStays);

		packageConfiguredReservedRoomStaysHandler.handle(packageRequestData, accommodationAvailabilityResponseData);

		reservedRoomStayData.setNonModifiable(true);
		final AccommodationPackageRequestData accommodationPackageRequest = new AccommodationPackageRequestData();
		final AccommodationSearchRequestData accommodationSearchRequest = new AccommodationSearchRequestData();
		accommodationPackageRequest.setAccommodationSearchRequest(accommodationSearchRequest);
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequest);

		final AccommodationSearchResponseData accommodationSearchResponseData = new AccommodationSearchResponseData();
		Mockito.when(accommodationSearchFacade.doSearch(accommodationSearchRequest)).thenReturn(accommodationSearchResponseData);

		packageConfiguredReservedRoomStaysHandler.handle(packageRequestData, accommodationAvailabilityResponseData);

		final List<PropertyData> properties = new ArrayList<>();
		final PropertyData propertyData = new PropertyData();
		propertyData.setRatePlanConfigs(Collections.singletonList("ratePlan|Config|1"));
		properties.add(propertyData);
		accommodationSearchResponseData.setProperties(properties);

		final CriterionData criterion = new CriterionData();
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
		roomStayCandidates.add(roomStayCandidateData);
		criterion.setRoomStayCandidates(roomStayCandidates);
		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setRatePlanConfigs(Collections.singletonList("ratePlan|Config|1"));
		criterion.setAccommodationReference(accommodationReference);
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		criterion.setStayDateRange(stayDateRange);
		accommodationSearchRequest.setCriterion(criterion);
		accommodationSearchResponseData.setCriterion(criterion);

		final List<RatePlanData> ratePlans = new ArrayList<>();
		final RatePlanData ratePlanData = new RatePlanData();
		ratePlanData.setAvailableQuantity(2);
		ratePlans.add(ratePlanData);
		reservedRoomStayData.setRatePlans(ratePlans);

		final List<RoomStayData> roomStays = new ArrayList<>();
		roomStays.add(reservedRoomStayData);
		accommodationAvailabilityResponseData.setRoomStays(roomStays);

		Mockito.when(bookingFacade.getOldAccommodationOrderEntryGroupRefs()).thenReturn(Collections.singletonList(1));
		Mockito
				.when(accommodationOfferingFacade
						.getSelectedAccommodationOfferingDetails(Mockito.any(AccommodationAvailabilityRequestData.class)))
				.thenReturn(accommodationAvailabilityResponseData);

		packageConfiguredReservedRoomStaysHandler.handle(packageRequestData, accommodationAvailabilityResponseData);

		Assert.assertTrue(accommodationAvailabilityResponseData.getConfigRoomsUnavailable());
	}
}
