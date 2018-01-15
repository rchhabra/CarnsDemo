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

package de.hybris.platform.travelservices.cronjob;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.cronjob.MarketingRatePlanInfoCronjobModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.services.MarketingRatePlanInfoService;
import de.hybris.platform.travelservices.services.RatePlanConfigService;
import de.hybris.platform.travelservices.services.impl.DefaultAccommodationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for MarketingRatePlanInfoJob.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MarketingRatePlanInfoJobTest
{
	@InjectMocks
	private final MarketingRatePlanInfoJob marketingRatePlanInfoJob = new MarketingRatePlanInfoJob();
	@Mock
	private AccommodationOfferingService accommodationOfferingService;
	@Mock
	private DefaultAccommodationService defaultAccommodationService;
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private RatePlanConfigService ratePlanConfigService;
	@Mock
	private MarketingRatePlanInfoService marketingRatePlanInfoService;
	@Mock
	private MarketingRatePlanInfoCronjobModel marketingRatePlanInfoCronjobModel;
	@Mock
	private AccommodationModel accommodation;
	@Mock
	private GuestOccupancyModel guestOccupancy;
	@Mock
	private PassengerTypeModel passengerType;
	@Mock
	private MarketingRatePlanInfoModel marketingRatePlanInfoModel;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private RatePlanModel ratePlan;
	@Mock
	private RatePlanConfigModel ratePlanConfig;
	@Mock
	private AccommodationOfferingModel accommodationOffering;

	@Before
	public void prepare()
	{

	}

	private AccommodationOfferingModel prepareAccommodationOfferings()
	{
		final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
		accommodationOfferingModel.setCode("acco1");
		return accommodationOfferingModel;
	}

	@Test
	public void testPerform()
	{
		Mockito.when(marketingRatePlanInfoCronjobModel.getBatchSize()).thenReturn(1);
		Mockito.when(marketingRatePlanInfoCronjobModel.getAccommodationOfferings())
				.thenReturn(Stream.of(prepareAccommodationOfferings()).collect(Collectors.toList()));
		Mockito.when(marketingRatePlanInfoCronjobModel.getRatePlanCode()).thenReturn("ratePlan");
		Mockito.when(defaultAccommodationService.getAccommodationForAccommodationOffering("acco1"))
				.thenReturn(Stream.of(accommodation).collect(Collectors.toList()));
		Mockito.when(accommodation.getGuestOccupancies()).thenReturn(Stream.of(guestOccupancy).collect(Collectors.toList()));
		Mockito.when(guestOccupancy.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		Mockito.when(guestOccupancy.getCode()).thenReturn("2A");
		Mockito.when(marketingRatePlanInfoService.getMarketingRatePlanInfoForCode(Matchers.anyString()))
				.thenReturn(marketingRatePlanInfoModel);
		Mockito.when(guestOccupancy.getQuantityMax()).thenReturn(2);
		Mockito.when(catalogVersionService.getCatalogVersion(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(catalogVersion);
		Mockito.when(accommodation.getRatePlan()).thenReturn(Stream.of(ratePlan).collect(Collectors.toList()));
		Mockito.when(ratePlan.getCode()).thenReturn("ratePlan");
		Mockito.when(ratePlanConfigService.getRatePlanConfigForCode(Matchers.anyString())).thenReturn(ratePlanConfig);
		Mockito.doNothing().when(modelService).saveAll(Matchers.anyCollection());
		marketingRatePlanInfoJob.perform(marketingRatePlanInfoCronjobModel);
	}

	@Test
	public void testPerformWithEmptyTOs()
	{
		Mockito.when(marketingRatePlanInfoCronjobModel.getBatchSize()).thenReturn(1);
		Mockito.when(marketingRatePlanInfoCronjobModel.getAccommodationOfferings())
				.thenReturn(Collections.emptyList());
		final SearchResult<AccommodationOfferingModel> searchResults = new SearchResultImpl<AccommodationOfferingModel>(
				Stream.of(accommodationOffering).collect(Collectors.toList()), 0, 0, 0);

		Mockito.when(accommodationOfferingService.getAccommodationOfferings(Matchers.anyInt(), Matchers.anyInt()))
				.thenReturn(searchResults);
		Mockito.when(marketingRatePlanInfoCronjobModel.getRatePlanCode()).thenReturn("ratePlan");
		Mockito.when(defaultAccommodationService.getAccommodationForAccommodationOffering("acco1"))
				.thenReturn(Stream.of(accommodation).collect(Collectors.toList()));
		Mockito.when(accommodation.getGuestOccupancies()).thenReturn(Stream.of(guestOccupancy).collect(Collectors.toList()));
		Mockito.when(guestOccupancy.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		Mockito.when(guestOccupancy.getCode()).thenReturn("2A");
		Mockito.when(marketingRatePlanInfoService.getMarketingRatePlanInfoForCode(Matchers.anyString()))
				.thenReturn(marketingRatePlanInfoModel);
		Mockito.when(guestOccupancy.getQuantityMax()).thenReturn(2);
		Mockito.when(catalogVersionService.getCatalogVersion(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(catalogVersion);
		Mockito.when(accommodation.getRatePlan()).thenReturn(Stream.of(ratePlan).collect(Collectors.toList()));
		Mockito.when(ratePlan.getCode()).thenReturn("ratePlan");
		Mockito.when(ratePlanConfigService.getRatePlanConfigForCode(Matchers.anyString())).thenReturn(ratePlanConfig);
		Mockito.doNothing().when(modelService).saveAll(Matchers.anyCollection());
		marketingRatePlanInfoJob.perform(marketingRatePlanInfoCronjobModel);
	}

	@Test
	public void testPerformWithNoMarketingRatePlanInfo()
	{
		Mockito.when(marketingRatePlanInfoCronjobModel.getBatchSize()).thenReturn(1);
		Mockito.when(marketingRatePlanInfoCronjobModel.getAccommodationOfferings())
				.thenReturn(Stream.of(prepareAccommodationOfferings()).collect(Collectors.toList()));
		Mockito.when(marketingRatePlanInfoCronjobModel.getRatePlanCode()).thenReturn("ratePlan");
		Mockito.when(defaultAccommodationService.getAccommodationForAccommodationOffering("acco1"))
				.thenReturn(Stream.of(accommodation).collect(Collectors.toList()));
		Mockito.when(accommodation.getGuestOccupancies()).thenReturn(Stream.of(guestOccupancy).collect(Collectors.toList()));
		Mockito.when(guestOccupancy.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		Mockito.when(guestOccupancy.getCode()).thenReturn("2A");
		Mockito.when(marketingRatePlanInfoService.getMarketingRatePlanInfoForCode(Matchers.anyString()))
				.thenThrow(new NoSuchElementException("No marketingRatePlanInfo found for given code"));
		Mockito.when(modelService.create(MarketingRatePlanInfoModel.class)).thenReturn(marketingRatePlanInfoModel);
		Mockito.when(guestOccupancy.getQuantityMax()).thenReturn(2);
		Mockito.when(catalogVersionService.getCatalogVersion(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(catalogVersion);
		Mockito.when(accommodation.getRatePlan()).thenReturn(Stream.of(ratePlan).collect(Collectors.toList()));
		Mockito.when(ratePlan.getCode()).thenReturn("ratePlan");
		Mockito.when(ratePlanConfigService.getRatePlanConfigForCode(Matchers.anyString())).thenReturn(ratePlanConfig);
		Mockito.doNothing().when(modelService).saveAll(Matchers.anyCollection());
		marketingRatePlanInfoJob.perform(marketingRatePlanInfoCronjobModel);
	}

	@Test
	public void testPerformWithExtraGuests()
	{
		Mockito.when(marketingRatePlanInfoCronjobModel.getBatchSize()).thenReturn(1);
		Mockito.when(marketingRatePlanInfoCronjobModel.getAccommodationOfferings())
				.thenReturn(Stream.of(prepareAccommodationOfferings()).collect(Collectors.toList()));
		Mockito.when(marketingRatePlanInfoCronjobModel.getRatePlanCode()).thenReturn("ratePlan");
		Mockito.when(defaultAccommodationService.getAccommodationForAccommodationOffering("acco1"))
				.thenReturn(Stream.of(accommodation).collect(Collectors.toList()));
		final GuestOccupancyModel extraGuest = new GuestOccupancyModel();
		final PassengerTypeModel extraPassengerType = new PassengerTypeModel();
		extraPassengerType.setCode("child");
		extraGuest.setPassengerType(extraPassengerType);
		Mockito.when(accommodation.getGuestOccupancies()).thenReturn(Arrays.asList(guestOccupancy, extraGuest));
		Mockito.when(guestOccupancy.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		Mockito.when(guestOccupancy.getCode()).thenReturn("2A");
		Mockito.when(marketingRatePlanInfoService.getMarketingRatePlanInfoForCode(Matchers.anyString()))
				.thenReturn(marketingRatePlanInfoModel);
		Mockito.when(guestOccupancy.getQuantityMax()).thenReturn(2);
		Mockito.when(catalogVersionService.getCatalogVersion(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(catalogVersion);
		Mockito.when(accommodation.getRatePlan()).thenReturn(Stream.of(ratePlan).collect(Collectors.toList()));
		Mockito.when(ratePlan.getCode()).thenReturn("ratePlan");
		Mockito.when(ratePlanConfigService.getRatePlanConfigForCode(Matchers.anyString())).thenReturn(ratePlanConfig);
		Mockito.doNothing().when(modelService).saveAll(Matchers.anyCollection());
		marketingRatePlanInfoJob.perform(marketingRatePlanInfoCronjobModel);
	}

	@Test
	public void testPerformWithRatePlanConfig()
	{
		Mockito.when(marketingRatePlanInfoCronjobModel.getBatchSize()).thenReturn(1);
		Mockito.when(marketingRatePlanInfoCronjobModel.getAccommodationOfferings())
				.thenReturn(Stream.of(prepareAccommodationOfferings()).collect(Collectors.toList()));
		Mockito.when(marketingRatePlanInfoCronjobModel.getRatePlanCode()).thenReturn("ratePlan");
		Mockito.when(defaultAccommodationService.getAccommodationForAccommodationOffering("acco1"))
				.thenReturn(Stream.of(accommodation).collect(Collectors.toList()));
		Mockito.when(accommodation.getGuestOccupancies()).thenReturn(Stream.of(guestOccupancy).collect(Collectors.toList()));
		Mockito.when(guestOccupancy.getPassengerType()).thenReturn(passengerType);
		Mockito.when(passengerType.getCode()).thenReturn("adult");
		Mockito.when(accommodationOffering.getCode()).thenReturn("acco1");
		Mockito.when(guestOccupancy.getCode()).thenReturn("2A");
		Mockito.when(marketingRatePlanInfoService.getMarketingRatePlanInfoForCode(Matchers.anyString()))
				.thenReturn(marketingRatePlanInfoModel);
		Mockito.when(guestOccupancy.getQuantityMax()).thenReturn(2);
		Mockito.when(catalogVersionService.getCatalogVersion(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(catalogVersion);
		Mockito.when(accommodation.getRatePlan()).thenReturn(Stream.of(ratePlan).collect(Collectors.toList()));
		Mockito.when(ratePlan.getCode()).thenReturn("ratePlan");

		Mockito.when(ratePlanConfigService.getRatePlanConfigForCode(Matchers.anyString()))
				.thenThrow(new NoSuchElementException("No ratePlanConfig found for given code"));
		Mockito.when(modelService.create(RatePlanConfigModel.class)).thenReturn(ratePlanConfig);

		Mockito.doNothing().when(modelService).saveAll(Matchers.anyCollection());
		marketingRatePlanInfoJob.perform(marketingRatePlanInfoCronjobModel);
	}

}
