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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link BundleSeatsAvailabilityHandler}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BundleSeatsAvailabilityHandlerTest
{
	@InjectMocks
	BundleSeatsAvailabilityHandler bundleSeatsAvailabilityHandler;

	@Mock
	private AccommodationMapService accommodationMapService;
	@Mock
	private TransportOfferingService transportOfferingService;
	@Mock
	private Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator;
	@Mock
	private ProductReferenceService productReferenceService;
	@Mock
	private ProductService productService;
	@Mock
	private TravelCommerceStockService commerceStockService;

	@Mock
	TransportOfferingModel transportOffering;
	@Mock
	AccommodationMapModel accommodationMapModel;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatMapA;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatMapB;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatMapC;

	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelCabinsC1;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelCabinsC2;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelCabinsC3;


	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelRowsC3_1;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelRowsC3_2;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelRowsC3_3;

	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelColumnC3_3_1;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelColumnC3_3_2;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelColumnC3_3_3;

	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatC3_3_3_1;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatC3_3_3_2;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatC3_3_3_3;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatC3_3_3_4;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatC3_3_3_5;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatC3_3_3_6;
	@Mock
	ConfiguredAccommodationModel configuredAccommodationModelSeatC3_3_3_7;

	@Mock
	SelectedAccommodationModel selectedAccommodationModel;

	@Mock
	ProductModel productModel1;
	@Mock
	ProductModel productModel2;
	@Mock
	ProductModel productModel3;
	@Mock
	ProductModel productModel4;

	@Mock
	ProductReferenceModel productReferenceModel;

	@Mock
	ProductModel fareProduct;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	private final String TEST_TRANSPORT_OFFERING_CODE_A = "TEST_TRANSPORT_OFFERING_CODE_A";
	private final String TEST_TRANSPORT_VEHICLE_INFO_CODE = "TEST_TRANSPORT_VEHICLE_INFO_CODE";
	private final String TEST_TRAVEL_SECTOR_CODE = "TEST_TRAVEL_SECTOR_CODE";
	private final String TEST_IDENTIFIER_CODE_A = "TEST_IDENTIFIER_CODE_A";
	private final String TEST_IDENTIFIER_CODE_B = "TEST_IDENTIFIER_CODE_B";
	private final String TEST_FARE_PRODUCT_CODE = "TEST_FARE_PRODUCT_CODE";
	private boolean isForAccommodationMapDataSetUpExceptionException;
	private boolean isForAccommodationMapDataSetUpExceptionExceptionFromVehicle;
	private List<AccommodationStatus> selectedAccomStatuses;
	private List<OrderStatus> cancelledOrderStatuses;

	@Before
	public void setUp()
	{
		given(transportOfferingService.getTransportOffering(TEST_TRANSPORT_OFFERING_CODE_A)).willReturn(transportOffering);
		selectedAccomStatuses = new ArrayList<>();
		selectedAccomStatuses.add(AccommodationStatus.OCCUPIED);
		selectedAccomStatuses.add(AccommodationStatus.UNAVAILABLE);

		cancelledOrderStatuses = new ArrayList<>();
		cancelledOrderStatuses.add(OrderStatus.CANCELLED);
		cancelledOrderStatuses.add(OrderStatus.CANCELLING);
	}

	@Test
	public void test()
	{
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(Boolean.TRUE);
		isForAccommodationMapDataSetUpExceptionException = false;
		given(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).willReturn(accommodationMapModel);
		createFareSelectionData();
		given(accommodationMapService.getAccommodationMapConfiguration(accommodationMapModel)).willReturn(Arrays.asList(
				configuredAccommodationModelSeatMapA, configuredAccommodationModelSeatMapB, configuredAccommodationModelSeatMapC));
		given(configuredAccommodationModelSeatMapA.getType()).willReturn(ConfiguredAccommodationType.BERTH);
		given(configuredAccommodationModelSeatMapB.getType()).willReturn(ConfiguredAccommodationType.DECK);
		given(configuredAccommodationModelSeatMapB.getConfiguredAccommodation()).willReturn(Collections.emptyList());
		given(configuredAccommodationModelSeatMapC.getType()).willReturn(ConfiguredAccommodationType.DECK);
		given(configuredAccommodationModelSeatMapC.getConfiguredAccommodation()).willReturn(Arrays.asList(
				configuredAccommodationModelCabinsC1, configuredAccommodationModelCabinsC2, configuredAccommodationModelCabinsC3));

		given(configuredAccommodationModelCabinsC1.getType()).willReturn(ConfiguredAccommodationType.BERTH);
		given(configuredAccommodationModelCabinsC2.getType()).willReturn(ConfiguredAccommodationType.CABIN);
		given(configuredAccommodationModelCabinsC3.getType()).willReturn(ConfiguredAccommodationType.CABIN);

		given(configuredAccommodationModelCabinsC2.getConfiguredAccommodation()).willReturn(Collections.emptyList());
		given(configuredAccommodationModelCabinsC3.getConfiguredAccommodation()).willReturn(Arrays.asList(
				configuredAccommodationModelRowsC3_1, configuredAccommodationModelRowsC3_2, configuredAccommodationModelRowsC3_3));


		given(configuredAccommodationModelRowsC3_1.getType()).willReturn(ConfiguredAccommodationType.CABIN);
		given(configuredAccommodationModelRowsC3_2.getType()).willReturn(ConfiguredAccommodationType.ROW);
		given(configuredAccommodationModelRowsC3_3.getType()).willReturn(ConfiguredAccommodationType.ROW);
		given(configuredAccommodationModelRowsC3_2.getConfiguredAccommodation()).willReturn(Collections.emptyList());
		given(configuredAccommodationModelRowsC3_3.getConfiguredAccommodation())
				.willReturn(Arrays.asList(configuredAccommodationModelColumnC3_3_1, configuredAccommodationModelColumnC3_3_2,
						configuredAccommodationModelColumnC3_3_3));

		given(configuredAccommodationModelColumnC3_3_1.getType()).willReturn(ConfiguredAccommodationType.CABIN);
		given(configuredAccommodationModelColumnC3_3_2.getType()).willReturn(ConfiguredAccommodationType.COLUMN);
		given(configuredAccommodationModelColumnC3_3_3.getType()).willReturn(ConfiguredAccommodationType.COLUMN);

		given(configuredAccommodationModelColumnC3_3_2.getConfiguredAccommodation()).willReturn(Collections.emptyList());
		given(configuredAccommodationModelColumnC3_3_3.getConfiguredAccommodation())
				.willReturn(Arrays.asList(configuredAccommodationModelSeatC3_3_3_1, configuredAccommodationModelSeatC3_3_3_2,
						configuredAccommodationModelSeatC3_3_3_3, configuredAccommodationModelSeatC3_3_3_4,
						configuredAccommodationModelSeatC3_3_3_5, configuredAccommodationModelSeatC3_3_3_6,
						configuredAccommodationModelSeatC3_3_3_7));

		given(configuredAccommodationModelSeatC3_3_3_1.getType()).willReturn(ConfiguredAccommodationType.CABIN);
		given(configuredAccommodationModelSeatC3_3_3_2.getType()).willReturn(ConfiguredAccommodationType.SEAT);
		given(configuredAccommodationModelSeatC3_3_3_3.getType()).willReturn(ConfiguredAccommodationType.SEAT);
		given(configuredAccommodationModelSeatC3_3_3_4.getType()).willReturn(ConfiguredAccommodationType.SEAT);
		given(configuredAccommodationModelSeatC3_3_3_5.getType()).willReturn(ConfiguredAccommodationType.SEAT);
		given(configuredAccommodationModelSeatC3_3_3_6.getType()).willReturn(ConfiguredAccommodationType.SEAT);
		given(configuredAccommodationModelSeatC3_3_3_7.getType()).willReturn(ConfiguredAccommodationType.SEAT);


		given(configuredAccommodationModelSeatC3_3_3_2.getProduct()).willReturn(productModel1);
		given(configuredAccommodationModelSeatC3_3_3_3.getProduct()).willReturn(null);
		given(configuredAccommodationModelSeatC3_3_3_4.getProduct()).willReturn(null);
		given(configuredAccommodationModelSeatC3_3_3_5.getProduct()).willReturn(productModel2);
		given(configuredAccommodationModelSeatC3_3_3_6.getProduct()).willReturn(productModel3);
		given(configuredAccommodationModelSeatC3_3_3_7.getProduct()).willReturn(productModel4);


		given(configuredAccommodationModelSeatC3_3_3_3.getIdentifier()).willReturn(TEST_IDENTIFIER_CODE_A);
		given(configuredAccommodationModelSeatC3_3_3_4.getIdentifier()).willReturn(TEST_IDENTIFIER_CODE_B);

		given(selectedAccommodationModel.getConfiguredAccommodation()).willReturn(configuredAccommodationModelSeatC3_3_3_3);
		given(accommodationMapService.getSelectedAccommodations(Matchers.any(TransportOfferingModel.class), Matchers.any(),
				Matchers.any())).willReturn(Arrays.asList(selectedAccommodationModel));

		given(commerceStockService.getStockLevel(Matchers.any(), Matchers.any())).willReturn(new Long(2));

		given(productService.getProductForCode(Matchers.anyString())).willReturn(fareProduct);
		given(productReferenceService.getProductReferencesForSourceAndTarget(fareProduct, productModel1, true))
				.willReturn(Collections.emptyList());
		given(productReferenceService.getProductReferencesForSourceAndTarget(fareProduct, productModel2, true))
				.willReturn(Arrays.asList(productReferenceModel));
		given(productReferenceService.getProductReferencesForSourceAndTarget(fareProduct, productModel3, true))
				.willReturn(Arrays.asList(productReferenceModel));
		given(configuredAccomNumberComparator.compare(Matchers.any(ConfiguredAccommodationModel.class),
				Matchers.any(ConfiguredAccommodationModel.class))).willReturn(0);

		final FareSelectionData fareSelectionData = createFareSelectionData();
		final FareSearchRequestData fareSearchRequestData = createFareSearchRequestData();
		bundleSeatsAvailabilityHandler.handle(null, fareSearchRequestData, fareSelectionData);
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void testAccommodationMapDataSetUpException()
	{
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(Boolean.TRUE);
		given(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).willReturn(null);
		final FareSelectionData fareSelectionData = createFareSelectionData();
		final FareSearchRequestData fareSearchRequestData = createFareSearchRequestData();
		bundleSeatsAvailabilityHandler.handle(null, fareSearchRequestData, fareSelectionData);
	}

	private FareSearchRequestData createFareSearchRequestData()
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();
		final List<PassengerTypeQuantityData> passengerTypes = new ArrayList<>();
		passengerTypes.add(createPassengerTypeQuantity("adult", 6));
		fareSearchRequestData.setPassengerTypes(passengerTypes);
		return fareSearchRequestData;
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void testForExceptionFromVehicle()
	{
		isForAccommodationMapDataSetUpExceptionException = true;
		final FareSelectionData fareSelectionData = createFareSelectionData();
		final FareSearchRequestData fareSearchRequestData = createFareSearchRequestData();
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(Boolean.TRUE);
		bundleSeatsAvailabilityHandler.handle(null, fareSearchRequestData, fareSelectionData);
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void testForExceptionFromVehicleInfoData()
	{
		isForAccommodationMapDataSetUpExceptionException = false;
		isForAccommodationMapDataSetUpExceptionExceptionFromVehicle = true;
		final FareSelectionData fareSelectionData = createFareSelectionData();
		final FareSearchRequestData fareSearchRequestData = createFareSearchRequestData();
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(Boolean.TRUE);
		bundleSeatsAvailabilityHandler.handle(null, fareSearchRequestData, fareSelectionData);
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void testForExceptionFromTransportOfferingModel()
	{
		isForAccommodationMapDataSetUpExceptionException = false;
		isForAccommodationMapDataSetUpExceptionExceptionFromVehicle = false;
		given(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).willReturn(null);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(Boolean.TRUE);
		final FareSelectionData fareSelectionData = createFareSelectionData();
		final FareSearchRequestData fareSearchRequestData = createFareSearchRequestData();
		bundleSeatsAvailabilityHandler.handle(null, fareSearchRequestData, fareSelectionData);
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void testForExceptionFromAccommodationMapModel()
	{
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBoolean(Matchers.anyString())).willReturn(Boolean.TRUE);
		isForAccommodationMapDataSetUpExceptionException = false;
		isForAccommodationMapDataSetUpExceptionExceptionFromVehicle = false;
		given(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).willReturn(accommodationMapModel);
		given(accommodationMapService.getAccommodationMapConfiguration(accommodationMapModel)).willReturn(null);
		final FareSelectionData fareSelectionData = createFareSelectionData();
		final FareSearchRequestData fareSearchRequestData = createFareSearchRequestData();
		bundleSeatsAvailabilityHandler.handle(null, fareSearchRequestData, fareSelectionData);
	}

	private FareSelectionData createFareSelectionData()
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		createPassengerTypes();
		fareSelectionData.setPricedItineraries(createPricedItineraries());
		return fareSelectionData;
	}

	private List<PassengerTypeQuantityData> createPassengerTypes()
	{
		final List<PassengerTypeQuantityData> passengerTypes = new ArrayList<>();
		passengerTypes.add(createPassengerTypeQuantity("adult", 2));
		passengerTypes.add(createPassengerTypeQuantity("child", 1));
		return passengerTypes;
	}

	private PassengerTypeQuantityData createPassengerTypeQuantity(final String code, final int quantity)
	{
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode(code);

		final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
		passengerTypeQuantityData.setPassengerType(passengerType);
		passengerTypeQuantityData.setQuantity(quantity);

		return passengerTypeQuantityData;
	}

	private List<PricedItineraryData> createPricedItineraries()
	{
		final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
		pricedItineraries.add(createPricedItinerary(true));
		pricedItineraries.add(createPricedItinerary(false));
		return pricedItineraries;
	}

	private PricedItineraryData createPricedItinerary(final boolean isAvailable)
	{
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setAvailable(isAvailable);
		pricedItinerary.setItinerary(createItineraryData());
		pricedItinerary.setItineraryPricingInfos(createItineraryPricingInfos());
		pricedItinerary.setId(0);
		return pricedItinerary;
	}

	private ItineraryData createItineraryData()
	{
		final ItineraryData itineraryData = new ItineraryData();
		itineraryData.setOriginDestinationOptions(createOriginDestinationOptions());
		itineraryData.setRoute(createTravelRouteData());
		return itineraryData;
	}

	private TravelRouteData createTravelRouteData()
	{
		final TravelRouteData travelRouteData = new TravelRouteData();
		travelRouteData.setCode("TEST_TRAVEL_ROUTE_CODE");
		return travelRouteData;
	}

	private List<OriginDestinationOptionData> createOriginDestinationOptions()
	{
		final List<OriginDestinationOptionData> originDestinationOptionDatas = new ArrayList<>();
		originDestinationOptionDatas.add(createOriginDestinationOption());
		return originDestinationOptionDatas;
	}

	private OriginDestinationOptionData createOriginDestinationOption()
	{
		final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
		originDestinationOption.setTransportOfferings(creatTransportOfferingDatas());
		return originDestinationOption;
	}

	private List<TransportOfferingData> creatTransportOfferingDatas()
	{
		final List<TransportOfferingData> transportOfferingDatas = new ArrayList<>();
		transportOfferingDatas.add(createTransportOfferingData(TEST_TRANSPORT_OFFERING_CODE_A,
				!isForAccommodationMapDataSetUpExceptionException, false));
		transportOfferingDatas.add(
				createTransportOfferingData(TEST_TRANSPORT_OFFERING_CODE_A, !isForAccommodationMapDataSetUpExceptionException, true));
		return transportOfferingDatas;
	}

	private TransportOfferingData createTransportOfferingData(final String code, final boolean hasTransportVehicle,
			final boolean hasVehicleInfo)
	{
		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode(code);
		if (hasTransportVehicle)
		{
			transportOfferingData.setTransportVehicle(
					createTransportVehicleData(!isForAccommodationMapDataSetUpExceptionExceptionFromVehicle, hasVehicleInfo));
		}
		transportOfferingData.setSector(createTravelSectorData());
		return transportOfferingData;
	}

	private TravelSectorData createTravelSectorData()
	{
		final TravelSectorData travelSectorData = new TravelSectorData();
		travelSectorData.setCode(TEST_TRAVEL_SECTOR_CODE);
		return travelSectorData;
	}

	private TransportVehicleData createTransportVehicleData(final boolean hasVehicle, final boolean hasVehicleInfo)
	{
		final TransportVehicleData transportVehicleData = new TransportVehicleData();
		if (hasVehicle)
		{
			transportVehicleData.setVehicleInfo(createTransportVehicleInfoData(hasVehicleInfo));
		}
		return transportVehicleData;
	}

	private TransportVehicleInfoData createTransportVehicleInfoData(final boolean hasVehicleInfo)
	{
		final TransportVehicleInfoData transportVehicleInfoData = new TransportVehicleInfoData();
		transportVehicleInfoData.setCode(hasVehicleInfo ? TEST_TRANSPORT_VEHICLE_INFO_CODE : StringUtils.EMPTY);
		return transportVehicleInfoData;
	}

	private List<ItineraryPricingInfoData> createItineraryPricingInfos()
	{
		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		itineraryPricingInfos.add(createItineraryPricingInfoData(false));
		itineraryPricingInfos.add(createItineraryPricingInfoData(true));
		return itineraryPricingInfos;
	}

	private ItineraryPricingInfoData createItineraryPricingInfoData(final boolean isAvailable)
	{
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setAvailable(isAvailable);
		itineraryPricingInfoData.setBundleTemplates(createTravelBundleTemplateDatas());
		return itineraryPricingInfoData;
	}

	private List<TravelBundleTemplateData> createTravelBundleTemplateDatas()
	{
		final List<TravelBundleTemplateData> bundleTemplates = new ArrayList<>();
		bundleTemplates.add(createTravelBundleTemplateData(false));
		bundleTemplates.add(createTravelBundleTemplateData(true));
		return bundleTemplates;
	}

	private TravelBundleTemplateData createTravelBundleTemplateData(final boolean hasFareProducts)
	{
		final TravelBundleTemplateData travelBundleTemplateData = new TravelBundleTemplateData();
		travelBundleTemplateData.setId("TEST_BUNDLE_TEMPLATE_ID");
		if (hasFareProducts)
		{
			travelBundleTemplateData.setFareProducts(createFareProducts());
		}
		return travelBundleTemplateData;
	}

	private List<FareProductData> createFareProducts()
	{
		final List<FareProductData> fareProducts = new ArrayList<>();
		fareProducts.add(createFareProduct());
		return fareProducts;
	}

	private FareProductData createFareProduct()
	{
		final FareProductData fareProductData = new FareProductData();
		fareProductData.setCode(TEST_FARE_PRODUCT_CODE);
		return fareProductData;
	}
}
