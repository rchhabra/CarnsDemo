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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.accommodation.search.RadiusData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.accommodation.user.data.SearchAddressData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationDetailsPipelineManager;
import de.hybris.platform.travelfacades.search.AccommodationOfferingSearchFacade;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultAccommodationOfferingFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationOfferingFacadeTest
{
	@InjectMocks
	DefaultAccommodationOfferingFacade defaultAccommodationOfferingFacade;
	@Mock
	private AccommodationOfferingSearchFacade<AccommodationOfferingDayRateData> accommodationOfferingSearchFacade;
	@Mock
	private AccommodationOfferingService accommodationOfferingService;
	@Mock
	private AccommodationDetailsPipelineManager accommodationDetailsPipelineManager;
	@Mock
	private AccommodationDetailsPipelineManager selectedAccommodationDetailsPipelineManager;
	@Mock
	private AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> accommodationOfferingConverter;

	@Test
	public void testSearchAccommodationOfferingDayRates()
	{

		final AccommodationSearchRequestData accommodationRequestData = new AccommodationSearchRequestData();
		final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
		final CriterionData criterionData = new CriterionData();
		criterionData.setPropertyFilterText("PropertyFilterText");
		accommodationRequestData.setCriterion(criterionData);
		final StayDateRangeData stayDateRangeData = new StayDateRangeData();
		final SearchAddressData address = new SearchAddressData();
		address.setLine1("Line 1");
		stayDateRangeData.setStartTime(new Date(2016, 11, 20));
		stayDateRangeData.setEndTime(new Date(2016, 11, 22));
		criterionData.setStayDateRange(stayDateRangeData);
		criterionData.setAddress(address);
		final PassengerTypeQuantityData passengerTypeQuantityData=new PassengerTypeQuantityData();
		final PassengerTypeData passengerTypeData=new PassengerTypeData();
		passengerTypeData.setCode(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT);
		passengerTypeQuantityData.setPassengerType(passengerTypeData);
		final java.util.List<PassengerTypeQuantityData> passengerTypeQuantityList = Collections
				.singletonList(passengerTypeQuantityData);
		roomStayCandidateData.setPassengerTypeQuantityList(passengerTypeQuantityList);
		assertNull(defaultAccommodationOfferingFacade.searchAccommodationOfferingDayRates(accommodationRequestData,
				roomStayCandidateData));

		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setAccommodationOfferingCode("AccommodationOfferingCode");
		criterionData.setAccommodationReference(accommodationReference);
		assertNull(defaultAccommodationOfferingFacade.searchAccommodationOfferingDayRates(accommodationRequestData,
				roomStayCandidateData));

		final PositionData position = new PositionData();
		final RadiusData radius = new RadiusData();
		radius.setValue(10.0);
		criterionData.setPosition(position);
		criterionData.setRadius(radius);
		assertNull(defaultAccommodationOfferingFacade.searchAccommodationOfferingDayRates(accommodationRequestData,
				roomStayCandidateData));

		criterionData.setSort("sort");
		assertNull(defaultAccommodationOfferingFacade.searchAccommodationOfferingDayRates(accommodationRequestData,
				roomStayCandidateData));
	}

	@Test
	public void testGetPropertyData()
	{
		final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
		given(accommodationOfferingService.getAccommodationOffering("accommodationOfferingCode"))
				.willReturn(accommodationOfferingModel);
		final PropertyData propertyData=new PropertyData();
		given(accommodationOfferingConverter.convert(accommodationOfferingModel)).willReturn(propertyData);
		Assert.assertEquals(propertyData,
				defaultAccommodationOfferingFacade.getPropertyData("accommodationOfferingCode"));

	}

	@Test
	public void testCheckAvailability()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = new AccommodationAvailabilityResponseData();
		Assert.assertFalse(defaultAccommodationOfferingFacade.checkAvailability(accommodationAvailabilityResponse));


		final RoomStayData roomStayData = new RoomStayData();
		final RatePlanData ratePlan = new RatePlanData();
		roomStayData.setRatePlans(Collections.singletonList(ratePlan));
		accommodationAvailabilityResponse.setRoomStays(Collections.singletonList(roomStayData));
		Assert.assertFalse(defaultAccommodationOfferingFacade.checkAvailability(accommodationAvailabilityResponse));

		ratePlan.setAvailableQuantity(1);
		Assert.assertTrue(defaultAccommodationOfferingFacade.checkAvailability(accommodationAvailabilityResponse));
	}

	@Test
	public void testIsAccommodationAvailableForQuickSelection()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = new AccommodationAvailabilityResponseData();
		Assert.assertFalse(
				defaultAccommodationOfferingFacade.isAccommodationAvailableForQuickSelection(accommodationAvailabilityResponse));


		final RoomStayData roomStayData = new RoomStayData();
		accommodationAvailabilityResponse.setRoomStays(Collections.singletonList(roomStayData));
		final RatePlanData ratePlan = new RatePlanData();
		roomStayData.setRatePlans(Collections.singletonList(ratePlan));
		Assert.assertFalse(
				defaultAccommodationOfferingFacade.isAccommodationAvailableForQuickSelection(accommodationAvailabilityResponse));

		ratePlan.setAvailableQuantity(1);
		Assert.assertTrue(
				defaultAccommodationOfferingFacade.isAccommodationAvailableForQuickSelection(accommodationAvailabilityResponse));
	}

	@Test
	public void testGetAccommodationOfferingDetails()
	{
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		given(accommodationDetailsPipelineManager.executePipeline(availabilityRequestData))
				.willReturn(accommodationAvailabilityResponseData);

		assertEquals(accommodationAvailabilityResponseData,
				defaultAccommodationOfferingFacade.getAccommodationOfferingDetails(availabilityRequestData));
	}

	@Test
	public void testGetSelectedAccommodationOfferingDetails()
	{
		final AccommodationAvailabilityRequestData availabilityRequestData = new AccommodationAvailabilityRequestData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		given(selectedAccommodationDetailsPipelineManager.executePipeline(availabilityRequestData))
				.willReturn(accommodationAvailabilityResponseData);

		assertEquals(accommodationAvailabilityResponseData,
				defaultAccommodationOfferingFacade.getSelectedAccommodationOfferingDetails(availabilityRequestData));
	}

}
