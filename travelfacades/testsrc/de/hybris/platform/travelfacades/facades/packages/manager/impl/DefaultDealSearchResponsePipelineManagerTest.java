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
import de.hybris.platform.commercefacades.packages.request.AccommodationPackageRequestData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.request.TransportPackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationDetailsPipelineManager;
import de.hybris.platform.travelfacades.facades.packages.handlers.impl.DealPackageResponsePriceHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.StandardPackagePipelineManager;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 * unit test for {@link DefaultDealSearchResponsePipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDealSearchResponsePipelineManagerTest
{
	@InjectMocks
	DefaultDealSearchResponsePipelineManager defaultDealSearchResponsePipelineManager;
	@Mock
	private FareSearchFacade dealFareSearchFacade;
	@Mock
	private AccommodationDetailsPipelineManager dealAccommodationDetailsPipelineManager;
	@Mock
	private StandardPackagePipelineManager standardPackagePipelineManager;

	@Mock
	DealPackageResponsePriceHandler dealPackageResponsePriceHandler;

	@Before
	public void setUp()
	{
		defaultDealSearchResponsePipelineManager.setHandlers(Collections.singletonList(dealPackageResponsePriceHandler));
		Mockito.doNothing().when(dealPackageResponsePriceHandler).handle(Matchers.any(PackageRequestData.class),
				Matchers.any(PackageResponseData.class));
	}

	@Test
	public void testExecutePipelineForValidRoomStayCandidateData()
	{
		final TestDataSetUp testDataSetup = new TestDataSetUp();
		final PackageRequestData packageRequestData = new PackageRequestData();

		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new AccommodationAvailabilityRequestData();

		final PropertyData property = testDataSetup.createPropertData("TEST_ACCOMMODATION_OFFERING_CODE");

		final AccommodationAvailabilityResponseData availabilityResponseData = new AccommodationAvailabilityResponseData();
		availabilityResponseData.setAccommodationReference(property);
		Mockito.when(dealAccommodationDetailsPipelineManager.executePipeline(accommodationAvailabilityRequestData))
				.thenReturn(availabilityResponseData);
		final AccommodationPackageRequestData accommodationPackageRequestData = new AccommodationPackageRequestData();
		accommodationPackageRequestData.setAccommodationAvailabilityRequest(accommodationAvailabilityRequestData);
		accommodationPackageRequestData.setAccommodationSearchRequest(new AccommodationSearchRequestData());
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequestData);
		packageRequestData.setBundleTemplates(Arrays.asList(new BundleTemplateData()));

		Mockito.when(standardPackagePipelineManager.executePipeline(Matchers.any(BundleTemplateData.class)))
				.thenReturn(Collections.singletonList(new PackageProductData()));
		final FareSelectionData fareSelectionData = testDataSetup.createFareSelectionData();

		final TransportPackageRequestData transportPackageRequestData = new TransportPackageRequestData();
		transportPackageRequestData.setFareSearchRequest(new FareSearchRequestData());

		packageRequestData.setTransportPackageRequest(transportPackageRequestData);
		Mockito.when(dealFareSearchFacade.doSearch(Matchers.any(FareSearchRequestData.class))).thenReturn(fareSelectionData);

		final PackageResponseData packageResponse = defaultDealSearchResponsePipelineManager.executePipeline(packageRequestData);

		Assert.assertNull(packageResponse.getTransportPackageResponse().getReservationData());
		Assert.assertEquals(packageResponse.getTransportPackageResponse().getFareSearchResponse(), fareSelectionData);
		Assert.assertEquals(packageResponse.getAccommodationPackageResponse().getAccommodationAvailabilityResponse(),
				availabilityResponseData);
		Assert.assertTrue(CollectionUtils.isNotEmpty(packageResponse.getStandardPackageResponses()));
	}

	private class TestDataSetUp
	{

		public PropertyData createPropertData(final String accommodationOfferingCode)
		{
			final PropertyData property = new PropertyData();
			property.setAccommodationOfferingCode(accommodationOfferingCode);
			return property;
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
