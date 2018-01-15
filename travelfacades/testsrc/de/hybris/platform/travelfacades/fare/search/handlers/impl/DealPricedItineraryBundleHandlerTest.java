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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.DealOriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.bundle.TravelBundleTemplateService;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.services.CabinClassService;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DealPricedItineraryBundleHandlerTest
{
	@InjectMocks
	DealPricedItineraryBundleHandler handler;

	@Mock
	private TravelBundleTemplateService travelBundleTemplateService;
	@Mock
	private Converter<BundleTemplateModel, TravelBundleTemplateData> travelBundleTemplateConverter;
	@Mock
	private CabinClassService cabinClassService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private BundleTemplateModel bundleTemplateModel;
	@Mock
	private BundleType bundleType;
	@Mock
	private TravelBundleTemplateData bundleTemplateData;

	@Test
	public void testHandle()
	{
		final TestDataSetup testData = new TestDataSetup();
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData
				.setOriginDestinationInfo(
						Stream.of(testData.createOriginDestinationInfo(0, "package1")).collect(Collectors.toList()));
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(Stream.of(testData.createPricedItinerary(0)).collect(Collectors.toList()));
		Mockito.when(travelBundleTemplateService.getBundleTemplateForCode("package1")).thenReturn(bundleTemplateModel);
		Mockito.when(bundleTemplateModel.getType()).thenReturn(bundleType);
		Mockito.when(bundleType.getCode()).thenReturn(BundleType.ECONOMY.getCode());
		Mockito.when(travelBundleTemplateConverter.convert(bundleTemplateModel)).thenReturn(bundleTemplateData);
		Mockito.when(enumerationService.getEnumerationName(bundleType)).thenReturn("economy");
		handler.handle(Collections.emptyList(), fareSearchRequestData, fareSelectionData);
		assertTrue(fareSelectionData.getPricedItineraries().get(0).isAvailable());
	}

	@Test
	public void testHandleWithoutBundleTemplate()
	{
		final TestDataSetup testData = new TestDataSetup();
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setOriginDestinationInfo(
				Stream.of(testData.createOriginDestinationInfo(0, "package1")).collect(Collectors.toList()));
		final FareSelectionData fareSelectionData = new FareSelectionData();
		fareSelectionData.setPricedItineraries(Stream.of(testData.createPricedItinerary(0)).collect(Collectors.toList()));
		Mockito.when(travelBundleTemplateService.getBundleTemplateForCode("package1")).thenReturn(null);
		Mockito.when(bundleTemplateModel.getType()).thenReturn(bundleType);
		Mockito.when(bundleType.getCode()).thenReturn(BundleType.ECONOMY.getCode());
		Mockito.when(enumerationService.getEnumerationName(bundleType)).thenReturn("economy");
		handler.handle(Collections.emptyList(), fareSearchRequestData, fareSelectionData);
		assertFalse(fareSelectionData.getPricedItineraries().get(0).isAvailable());
	}

	@Test
	public void testHandleWithNullParameters()
	{
		handler.handle(Collections.emptyList(), null, null);
	}

	class TestDataSetup
	{

		public OriginDestinationInfoData createOriginDestinationInfo(final int refNumber, final String packageId)
		{
			final DealOriginDestinationInfoData originDestinationInfo = new DealOriginDestinationInfoData();
			originDestinationInfo.setReferenceNumber(refNumber);
			originDestinationInfo.setPackageId(packageId);
			return originDestinationInfo;
		}

		public PricedItineraryData createPricedItinerary(final int refNumber)
		{
			final PricedItineraryData pricedItinerary = new PricedItineraryData();
			pricedItinerary.setOriginDestinationRefNumber(refNumber);
			pricedItinerary.setItinerary(createItineraryData());
			return pricedItinerary;
		}

		private ItineraryData createItineraryData()
		{
			final ItineraryData itinerary = new ItineraryData();
			itinerary.setOriginDestinationOptions(Stream.of(createOriginDestinationOptionData()).collect(Collectors.toList()));
			return itinerary;
		}

		private OriginDestinationOptionData createOriginDestinationOptionData()
		{
			final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
			originDestinationOption.setTransportOfferings(Stream.of(createTransportOffering()).collect(Collectors.toList()));
			return originDestinationOption;
		}

		private TransportOfferingData createTransportOffering()
		{
			final TransportOfferingData transportOffering = new TransportOfferingData();
			return transportOffering;
		}

	}

}
