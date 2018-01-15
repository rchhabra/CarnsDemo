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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelPreferencesData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.bundle.impl.DefaultTravelBundleTemplateService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.impl.DefaultCabinClassService;
import de.hybris.platform.travelservices.services.impl.DefaultTravelRouteService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for PricedItineraryBundleHandler
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PricedItineraryBundleHandlerTest
{
	@InjectMocks
	private final PricedItineraryBundleHandler pricedItineraryBundleHandler = new PricedItineraryBundleHandler()
	{
		@Override
		protected String getParameterValue(final String key)
		{
			if (StringUtils.equals(key, "fareselection.fareoptionlist"))
			{
				return "3";
			}
			if (StringUtils.equals(key, "fareselection.upgrade.allowed"))
			{
				return "true";
			}
			return null;
		}
	};

	@Mock
	private DefaultTravelRouteService travelRouteService;

	@Mock
	private DefaultCabinClassService cabinClassService;

	@Mock
	private DefaultTravelBundleTemplateService travelBundleTemplateService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private Converter<BundleTemplateModel, TravelBundleTemplateData> travelBundleTemplateConverter;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private TravelRulesService travelRulesService;

	@Mock
	private UserService userService;

	private FareSearchRequestData fareSearchRequestData;
	private CabinClassModel mockCabinClass_M;
	private CabinClassModel mockCabinClass_J;
	private BundleTemplateModel bundleTemplateModel_M;
	private BundleTemplateModel bundleTemplateModel_J;

	@Before
	public void prepare() throws ParseException
	{
		fareSearchRequestData = new FareSearchRequestData();
		fareSearchRequestData.setTravelPreferences(new TravelPreferencesData());
		fareSearchRequestData.getTravelPreferences().setCabinPreference("M");
		final PassengerTypeQuantityData pxData = new PassengerTypeQuantityData();
		pxData.setQuantity(1);
		final PassengerTypeData type = new PassengerTypeData();
		type.setName("Adult");
		pxData.setPassengerType(type);
		fareSearchRequestData.setPassengerTypes(Stream.of(pxData).collect(Collectors.toList()));
		final OriginDestinationInfoData originDestInfoData = new OriginDestinationInfoData();
		final SimpleDateFormat sdf = new SimpleDateFormat(TravelservicesConstants.DATE_TIME_PATTERN);
		originDestInfoData.setDepartureTime(sdf.parse("11/07/2016 07:35:00"));
		originDestInfoData.setArrivalTime(sdf.parse("11/07/2016 08:35:00"));
		originDestInfoData.setDepartureLocation("LHR");
		originDestInfoData.setArrivalLocation("DXB");
		fareSearchRequestData.setOriginDestinationInfo(Stream.of(originDestInfoData).collect(Collectors.toList()));
		fareSearchRequestData.setTripType(TripType.SINGLE);
		mockCabinClass_M = new CabinClassModel();
		mockCabinClass_M.setCabinClassIndex(1);
		given(cabinClassService.getCabinClass("M")).willReturn(mockCabinClass_M);

		mockCabinClass_J = new CabinClassModel();
		mockCabinClass_J.setCabinClassIndex(2);
		given(cabinClassService.getCabinClass("J")).willReturn(mockCabinClass_J);
		given(cabinClassService.getCabinClass(2)).willReturn(mockCabinClass_J);

		bundleTemplateModel_M = new BundleTemplateModel()
		{
			@Override
			public String getName()
			{
				return "BUNDLE_M";
			}
		};
		bundleTemplateModel_M.setType(BundleType.ECONOMY);
		bundleTemplateModel_M.setIgnoreRules(true);
		bundleTemplateModel_M.setId("EconomyBundle");
		final BundleTemplateModel fare_bundleTemplateModel_M = new BundleTemplateModel();
		fare_bundleTemplateModel_M.setType(BundleType.ECONOMY);

		final BundleTemplateModel ancillary_bundleTemplateModel_M = new BundleTemplateModel();

		bundleTemplateModel_M.setChildTemplates(Stream.of(fare_bundleTemplateModel_M, ancillary_bundleTemplateModel_M)
				.collect(Collectors.toList()));

		bundleTemplateModel_J = new BundleTemplateModel()
		{
			@Override
			public String getName()
			{
				return "BUNDLE_J";
			}
		};
		bundleTemplateModel_J.setType(BundleType.BUSINESS);
		bundleTemplateModel_J.setIgnoreRules(false);
		bundleTemplateModel_J.setId("BusinessBundle");
		final BundleTemplateModel fare_bundleTemplateModel_J = new BundleTemplateModel();
		fare_bundleTemplateModel_J.setType(BundleType.BUSINESS);
		bundleTemplateModel_J
				.setChildTemplates(Stream.of(fare_bundleTemplateModel_J).collect(Collectors.toList()));

	}

	/**
	 * Test method to populate BundleTemplates available at route level.
	 */
	@Test
	public void testPopulateForBundleTemplateAtRouteLevel()
	{

		final TestDataSetup dataSetup = new TestDataSetup();
		final TravelRouteData route = dataSetup.createRoute("LHR", "DXB", "lhrMUCdxb");
		final List<OriginDestinationOptionData> odList = dataSetup.createOriginDestinationOptionData(Arrays.asList(
				"EZY1234121220150635", "EZY5678121220150935"));
		final ItineraryData itineraryData = dataSetup.createItineraryData(TripType.SINGLE, route, odList);
		final FareSelectionData mockFareSelectionData = dataSetup.createFareSelectionData(itineraryData);

		final TravelRouteModel mockTravelRouteModel_lhrMUCdxb = mock(TravelRouteModel.class);

		given(travelRouteService.getTravelRoute("lhrMUCdxb")).willReturn(mockTravelRouteModel_lhrMUCdxb);
		given(travelBundleTemplateService.getBundleTemplates(mockTravelRouteModel_lhrMUCdxb, mockCabinClass_M))
				.willReturn(Collections.singletonList(bundleTemplateModel_M));

		given(travelBundleTemplateService.getBundleTemplates(mockTravelRouteModel_lhrMUCdxb, mockCabinClass_J))
				.willReturn(Collections.singletonList(bundleTemplateModel_J));

		final TravelBundleTemplateData travelBundleData_M = new TravelBundleTemplateData();
		travelBundleData_M.setBundleType(BundleType.ECONOMY.getCode());
		travelBundleData_M.setIgnoreRules(true);

		final TravelBundleTemplateData travelBundleData_J = new TravelBundleTemplateData();
		travelBundleData_J.setBundleType(BundleType.BUSINESS.getCode());
		travelBundleData_J.setIgnoreRules(true);

		given(travelBundleTemplateConverter.convert(bundleTemplateModel_M)).willReturn(travelBundleData_M);
		given(travelBundleTemplateConverter.convert(bundleTemplateModel_J)).willReturn(travelBundleData_J);

		given(enumerationService.getEnumerationName(bundleTemplateModel_M.getType())).willReturn(bundleTemplateModel_M.getName());
		given(enumerationService.getEnumerationName(bundleTemplateModel_J.getType())).willReturn(bundleTemplateModel_J.getName());

		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);

		given(userService.getCurrentUser()).willReturn(customerModel);
		given(travelRulesService.showBundleTemplates(Mockito.anyObject())).willReturn(Collections.emptyList());

		pricedItineraryBundleHandler.handle(null, fareSearchRequestData, mockFareSelectionData);

		assertEquals(2, mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().size());

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.ECONOMY.getCode())));

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.BUSINESS.getCode())));

	}

	/**
	 * Test method to populate BundleTemplates available at route level.
	 */
	@Test
	public void testPopulateForBundlTemplateAtRouteLevelWithNoBundles()
	{

		final TestDataSetup dataSetup = new TestDataSetup();
		final TravelRouteData route = dataSetup.createRoute("LHR", "DXB", "lhrMUCdxb");

		final List<OriginDestinationOptionData> odList = dataSetup.createOriginDestinationOptionData(Arrays.asList(
				"EZY1234121220150635", "EZY5678121220150935"));

		final ItineraryData itineraryData = dataSetup.createItineraryData(TripType.SINGLE, route, odList);
		final FareSelectionData mockFareSelectionData = dataSetup.createFareSelectionData(itineraryData);

		final TravelRouteModel mockTravelRouteModel_lhrMUCdxb = mock(TravelRouteModel.class);

		given(travelRouteService.getTravelRoute("lhrMUCdxb")).willReturn(mockTravelRouteModel_lhrMUCdxb);
		given(travelBundleTemplateService.getBundleTemplates(mockTravelRouteModel_lhrMUCdxb, mockCabinClass_M))
				.willReturn(new ArrayList<>());

		final TransportOfferingModel TO_EZY1234121220150635 = new TransportOfferingModel();
		final TransportOfferingModel TO_EZY5678121220150935 = new TransportOfferingModel();

		given(transportOfferingService.getTransportOffering("EZY1234121220150635")).willReturn(TO_EZY1234121220150635);
		given(transportOfferingService.getTransportOffering("EZY5678121220150935")).willReturn(TO_EZY5678121220150935);

		final TravelBundleTemplateData travelBundleData_M = new TravelBundleTemplateData();
		travelBundleData_M.setBundleType(BundleType.ECONOMY.getCode());
		travelBundleData_M.setIgnoreRules(true);

		final TravelBundleTemplateData travelBundleData_J = new TravelBundleTemplateData();
		travelBundleData_J.setBundleType(BundleType.BUSINESS.getCode());
		travelBundleData_J.setIgnoreRules(true);

		// bundle templates for transport offering mocks
		given(travelBundleTemplateService.getBundleTemplates(TO_EZY1234121220150635, mockCabinClass_M))
				.willReturn(Arrays.asList(bundleTemplateModel_M, bundleTemplateModel_M));

		given(travelBundleTemplateService.getBundleTemplates(TO_EZY5678121220150935, mockCabinClass_M))
				.willReturn(Collections.singletonList(bundleTemplateModel_M));

		// bundle templates for travel sector mocks
		given(travelBundleTemplateService.getBundleTemplates(TO_EZY1234121220150635.getTravelSector(), mockCabinClass_J))
				.willReturn(Collections.singletonList(bundleTemplateModel_J));

		given(travelBundleTemplateService.getBundleTemplates(TO_EZY5678121220150935.getTravelSector(), mockCabinClass_J))
				.willReturn(Collections.singletonList(bundleTemplateModel_J));

		// default bundle templates mocks
		given(travelBundleTemplateService.getDefaultBundleTemplates(mockCabinClass_M))
				.willReturn(Collections.singletonList(bundleTemplateModel_M));

		given(travelBundleTemplateConverter.convert(bundleTemplateModel_M)).willReturn(travelBundleData_M);
		given(travelBundleTemplateConverter.convert(bundleTemplateModel_J)).willReturn(travelBundleData_J);

		given(enumerationService.getEnumerationName(bundleTemplateModel_M.getType())).willReturn(bundleTemplateModel_M.getName());
		given(enumerationService.getEnumerationName(bundleTemplateModel_J.getType())).willReturn(bundleTemplateModel_J.getName());

		pricedItineraryBundleHandler.handle(null, fareSearchRequestData, mockFareSelectionData);

		assertEquals(2, mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().size());

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.ECONOMY.getCode())));

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.BUSINESS.getCode())));

	}

	/**
	 * Test method to populate BundleTemplates available at route level.
	 */
	@Test
	public void testPopulateForBundleTemplateAtTransportOfferingLevel()
	{
		final TestDataSetup dataSetup = new TestDataSetup();
		final TravelRouteData route = dataSetup.createRoute("LHR", "DXB", "LHRDXB");

		final TravelRouteModel mockTravelRouteModel_lhrdxb = mock(TravelRouteModel.class);
		given(travelRouteService.getTravelRoute("LHRDXB")).willReturn(mockTravelRouteModel_lhrdxb);

		final List<OriginDestinationOptionData> odList = dataSetup.createOriginDestinationOptionData(Collections.singletonList(
				"EZY1234121220150635"));
		final ItineraryData itineraryData = dataSetup.createItineraryData(TripType.SINGLE, route, odList);
		final FareSelectionData mockFareSelectionData = dataSetup.createFareSelectionData(itineraryData);

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();

		given(transportOfferingService.getTransportOffering("EZY1234121220150635")).willReturn(transportOfferingModel);
		given(travelBundleTemplateService.getBundleTemplates(transportOfferingModel, mockCabinClass_M))
				.willReturn(Collections.singletonList(bundleTemplateModel_M));
		given(travelBundleTemplateService.getBundleTemplates(transportOfferingModel, mockCabinClass_J))
				.willReturn(Collections.singletonList(bundleTemplateModel_J));

		final TravelBundleTemplateData travelBundleData_M = new TravelBundleTemplateData();
		travelBundleData_M.setBundleType(BundleType.ECONOMY.getCode());
		travelBundleData_M.setIgnoreRules(true);

		final TravelBundleTemplateData travelBundleData_J = new TravelBundleTemplateData();
		travelBundleData_J.setBundleType(BundleType.BUSINESS.getCode());
		travelBundleData_J.setIgnoreRules(true);

		given(travelBundleTemplateConverter.convert(bundleTemplateModel_M)).willReturn(travelBundleData_M);
		given(travelBundleTemplateConverter.convert(bundleTemplateModel_J)).willReturn(travelBundleData_J);

		given(enumerationService.getEnumerationName(bundleTemplateModel_M.getType())).willReturn(bundleTemplateModel_M.getName());
		given(enumerationService.getEnumerationName(bundleTemplateModel_J.getType())).willReturn(bundleTemplateModel_J.getName());

		pricedItineraryBundleHandler.handle(null, fareSearchRequestData, mockFareSelectionData);

		assertEquals(2, mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().size());

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.ECONOMY.getCode())));

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.BUSINESS.getCode())));

	}

	/**
	 * Test method to populate BundleTemplates available at route level.
	 */
	@Test
	public void testPopulateForBundleTemplateAtSectorLevel()
	{

		final TestDataSetup dataSetup = new TestDataSetup();
		final TravelRouteData route = dataSetup.createRoute("LHR", "DXB", "LHRDXB");

		final TravelRouteModel mockTravelRouteModel_lhrdxb = mock(TravelRouteModel.class);
		given(travelRouteService.getTravelRoute("LHRDXB")).willReturn(mockTravelRouteModel_lhrdxb);

		final List<OriginDestinationOptionData> odList = dataSetup.createOriginDestinationOptionData(Collections.singletonList(
				"EZY1234121220150635"));
		final ItineraryData itineraryData = dataSetup.createItineraryData(TripType.SINGLE, route, odList);
		final FareSelectionData mockFareSelectionData = dataSetup.createFareSelectionData(itineraryData);

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		transportOfferingModel.setTravelSector(travelSectorModel);

		given(transportOfferingService.getTransportOffering("EZY1234121220150635")).willReturn(transportOfferingModel);
		given(travelBundleTemplateService.getBundleTemplates(transportOfferingModel, mockCabinClass_M)).willReturn(null);
		given(travelBundleTemplateService.getBundleTemplates(travelSectorModel, mockCabinClass_M))
				.willReturn(Collections.singletonList(bundleTemplateModel_M));
		given(travelBundleTemplateService.getBundleTemplates(travelSectorModel, mockCabinClass_J))
				.willReturn(Collections.singletonList(bundleTemplateModel_J));

		final TravelBundleTemplateData travelBundleData_M = new TravelBundleTemplateData();
		travelBundleData_M.setBundleType(BundleType.ECONOMY.getCode());
		travelBundleData_M.setIgnoreRules(true);

		final TravelBundleTemplateData travelBundleData_J = new TravelBundleTemplateData();
		travelBundleData_J.setBundleType(BundleType.BUSINESS.getCode());
		travelBundleData_J.setIgnoreRules(true);
		given(travelBundleTemplateConverter.convert(bundleTemplateModel_M)).willReturn(travelBundleData_M);
		given(travelBundleTemplateConverter.convert(bundleTemplateModel_J)).willReturn(travelBundleData_J);

		given(enumerationService.getEnumerationName(bundleTemplateModel_M.getType())).willReturn(bundleTemplateModel_M.getName());
		given(enumerationService.getEnumerationName(bundleTemplateModel_J.getType())).willReturn(bundleTemplateModel_J.getName());

		pricedItineraryBundleHandler.handle(null, fareSearchRequestData, mockFareSelectionData);

		assertEquals(2, mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().size());

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.ECONOMY.getCode())));

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.BUSINESS.getCode())));

	}

	/**
	 * Test method to populate BundleTemplates available at route level.
	 */
	@Test
	public void testPopulateForBundleTemplateDefault()
	{

		final TestDataSetup dataSetup = new TestDataSetup();
		final TravelRouteData route = dataSetup.createRoute("LHR", "DXB", "LHRDXB");
		final List<OriginDestinationOptionData> odList = dataSetup.createOriginDestinationOptionData(Collections.singletonList(
				"EZY1234121220150635"));
		final ItineraryData itineraryData = dataSetup.createItineraryData(TripType.SINGLE, route, odList);
		final FareSelectionData mockFareSelectionData = dataSetup.createFareSelectionData(itineraryData);

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		transportOfferingModel.setTravelSector(travelSectorModel);

		given(transportOfferingService.getTransportOffering("EZY1234121220150635")).willReturn(transportOfferingModel);
		given(travelBundleTemplateService.getBundleTemplates(transportOfferingModel, mockCabinClass_M)).willReturn(null);
		given(travelBundleTemplateService.getBundleTemplates(travelSectorModel, mockCabinClass_M)).willReturn(null);

		final TravelRouteModel travelRouteModel_lhrdxb = new TravelRouteModel();
		given(travelRouteService.getTravelRoute("LHRDXB")).willReturn(travelRouteModel_lhrdxb);
		given(travelBundleTemplateService.getBundleTemplates(travelRouteModel_lhrdxb, mockCabinClass_M)).willReturn(null);
		given(travelBundleTemplateService.getDefaultBundleTemplates(mockCabinClass_M))
				.willReturn(Collections.singletonList(bundleTemplateModel_M));
		given(travelBundleTemplateService.getDefaultBundleTemplates(mockCabinClass_J))
				.willReturn(Collections.singletonList(bundleTemplateModel_J));

		final TravelBundleTemplateData travelBundleData_M = new TravelBundleTemplateData();
		travelBundleData_M.setBundleType(BundleType.ECONOMY.getCode());
		travelBundleData_M.setIgnoreRules(true);

		final TravelBundleTemplateData travelBundleData_J = new TravelBundleTemplateData();
		travelBundleData_J.setBundleType(BundleType.BUSINESS.getCode());
		travelBundleData_J.setIgnoreRules(true);

		given(travelBundleTemplateConverter.convert(bundleTemplateModel_M)).willReturn(travelBundleData_M);
		given(travelBundleTemplateConverter.convert(bundleTemplateModel_J)).willReturn(travelBundleData_J);
		given(enumerationService.getEnumerationName(bundleTemplateModel_M.getType())).willReturn(bundleTemplateModel_M.getName());
		given(enumerationService.getEnumerationName(bundleTemplateModel_J.getType())).willReturn(bundleTemplateModel_J.getName());

		pricedItineraryBundleHandler.handle(null, fareSearchRequestData, mockFareSelectionData);

		assertEquals(2, mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().size());

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.ECONOMY.getCode())));

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().stream()
				.anyMatch(itinData -> itinData.getBundleType().equals(BundleType.BUSINESS.getCode())));

	}


	/**
	 * Test method to test when no bundles are available.
	 */
	@Ignore
	@Test
	public void testNoBundlesAvailable()
	{

		final TestDataSetup dataSetup = new TestDataSetup();
		final TravelRouteData route = dataSetup.createRoute("LHR", "DXB", "LHRDXB");
		final List<OriginDestinationOptionData> odList = dataSetup.createOriginDestinationOptionData(Collections.singletonList(
				"EZY1234121220150635"));
		final ItineraryData itineraryData = dataSetup.createItineraryData(TripType.SINGLE, route, odList);
		final FareSelectionData mockFareSelectionData = dataSetup.createFareSelectionData(itineraryData);

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		transportOfferingModel.setTravelSector(travelSectorModel);

		given(transportOfferingService.getTransportOffering("EZY1234121220150635")).willReturn(transportOfferingModel);
		given(travelBundleTemplateService.getBundleTemplates(transportOfferingModel, mockCabinClass_M)).willReturn(null);
		given(travelBundleTemplateService.getBundleTemplates(travelSectorModel, mockCabinClass_M)).willReturn(null);

		final TravelRouteModel travelRouteModel_lhrdxb = new TravelRouteModel();

		given(travelRouteService.getTravelRoute("LHRDXB")).willReturn(travelRouteModel_lhrdxb);
		given(travelBundleTemplateService.getBundleTemplates(travelRouteModel_lhrdxb, mockCabinClass_M)).willReturn(null);
		given(travelBundleTemplateService.getDefaultBundleTemplates(mockCabinClass_M)).willReturn(null);

		pricedItineraryBundleHandler.handle(null, fareSearchRequestData, mockFareSelectionData);

		assertTrue(mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().isEmpty());

	}

	@Test
	public void testFilterBundle()
	{

		final TestDataSetup dataSetup = new TestDataSetup();
		final TravelRouteData route = dataSetup.createRoute("LHR", "DXB", "lhrMUCdxb");
		final List<OriginDestinationOptionData> odList = dataSetup.createOriginDestinationOptionData(Arrays.asList(
				"EZY1234121220150635", "EZY5678121220150935"));
		final ItineraryData itineraryData = dataSetup.createItineraryData(TripType.SINGLE, route, odList);
		final FareSelectionData mockFareSelectionData = dataSetup.createFareSelectionData(itineraryData);

		final TravelRouteModel mockTravelRouteModel_lhrMUCdxb = mock(TravelRouteModel.class);

		given(travelRouteService.getTravelRoute("lhrMUCdxb")).willReturn(mockTravelRouteModel_lhrMUCdxb);
		given(travelBundleTemplateService.getBundleTemplates(mockTravelRouteModel_lhrMUCdxb, mockCabinClass_M))
				.willReturn(Collections.singletonList(bundleTemplateModel_M));

		given(travelBundleTemplateService.getBundleTemplates(mockTravelRouteModel_lhrMUCdxb, mockCabinClass_J))
				.willReturn(Collections.singletonList(bundleTemplateModel_J));

		final TravelBundleTemplateData travelBundleData_M = new TravelBundleTemplateData();
		travelBundleData_M.setBundleType(BundleType.ECONOMY.getCode());
		travelBundleData_M.setId("EconomyBundle");
		travelBundleData_M.setIgnoreRules(true);

		final TravelBundleTemplateData travelBundleData_J = new TravelBundleTemplateData();
		travelBundleData_J.setBundleType(BundleType.BUSINESS.getCode());
		travelBundleData_J.setId("BusinessBundle");
		travelBundleData_M.setIgnoreRules(false);

		given(travelBundleTemplateConverter.convert(bundleTemplateModel_M)).willReturn(travelBundleData_M);
		given(travelBundleTemplateConverter.convert(bundleTemplateModel_J)).willReturn(travelBundleData_J);

		given(enumerationService.getEnumerationName(bundleTemplateModel_M.getType())).willReturn(bundleTemplateModel_M.getName());
		given(enumerationService.getEnumerationName(bundleTemplateModel_J.getType())).willReturn(bundleTemplateModel_J.getName());

		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);

		given(userService.getCurrentUser()).willReturn(customerModel);
		given(travelRulesService.showBundleTemplates(Mockito.anyObject())).willReturn(
				Collections.singletonList("BusinessBundle"));

		pricedItineraryBundleHandler.handle(null, fareSearchRequestData, mockFareSelectionData);

		assertEquals(1, mockFareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().size());
	}

	/**
	 * Inner class to set-up test data
	 */
	private class TestDataSetup
	{
		public FareSelectionData createFareSelectionData(final ItineraryData itineraryData)
		{

			final FareSelectionData fareSelectionData = new FareSelectionData();

			final List<PricedItineraryData> pricedItineraries = new ArrayList<>();

			final PricedItineraryData pricedItinerary1 = createPricedItinerary(itineraryData);

			pricedItineraries.add(pricedItinerary1);
			fareSelectionData.setPricedItineraries(pricedItineraries);
			return fareSelectionData;
		}

		private PricedItineraryData createPricedItinerary(final ItineraryData itineraryData1)
		{
			final PricedItineraryData pricedItinerary1 = new PricedItineraryData();
			pricedItinerary1.setId(1);
			pricedItinerary1.setOriginDestinationRefNumber(1);
			pricedItinerary1.setAvailable(Boolean.TRUE);

			pricedItinerary1.setItinerary(itineraryData1);
			return pricedItinerary1;
		}

		private ItineraryData createItineraryData(final TripType tripType, final TravelRouteData route,
				final List<OriginDestinationOptionData> originDestinationOptions)
		{
			final ItineraryData itineraryData1 = new ItineraryData();
			itineraryData1.setTripType(tripType);
			itineraryData1.setRoute(route);
			itineraryData1.setOriginDestinationOptions(originDestinationOptions);
			return itineraryData1;
		}

		private List<OriginDestinationOptionData> createOriginDestinationOptionData(final List<String> flightCodes)
		{
			final List<OriginDestinationOptionData> originDestinationOptions1 = new ArrayList<>();

			final OriginDestinationOptionData originDestinationOption1 = new OriginDestinationOptionData();
			originDestinationOption1.setTransportOfferings(flightCodes.stream().map(this::createFlight)
					.collect(Collectors.toList()));
			originDestinationOptions1.add(originDestinationOption1);

			return originDestinationOptions1;
		}

		private TransportOfferingData createFlight(final String flightCode)
		{
			final TransportOfferingData flight1 = new TransportOfferingData();
			flight1.setCode(flightCode);
			return flight1;
		}

		private TravelRouteData createRoute(final String origin, final String destination, final String code)
		{
			final LocationData london = createLocationData(origin);
			final LocationData dubai = createLocationData(destination);

			final TransportFacilityData originAirport = createTransportFacility(london);
			final TransportFacilityData destinationAirport = createTransportFacility(dubai);

			final TravelRouteData lhrDXBRoute = new TravelRouteData();
			lhrDXBRoute.setCode(code);
			lhrDXBRoute.setOrigin(originAirport);
			lhrDXBRoute.setDestination(destinationAirport);
			return lhrDXBRoute;
		}

		private TransportFacilityData createTransportFacility(final LocationData location)
		{
			/* TRANSPORT FACILITIES */
			final TransportFacilityData lhrAirport = new TransportFacilityData();
			lhrAirport.setLocation(location);
			return lhrAirport;
		}

		private LocationData createLocationData(final String code)
		{
			final LocationData london = new LocationData();
			london.setCode(code);
			return london;
		}
	}

}
