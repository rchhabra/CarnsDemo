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

package de.hybris.platform.travelservices.strategies.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TransportVehicleType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.order.impl.DefaultTravelCartService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TravelProductBasedAutoAccommodationAllocationStrategyTest
{
	private TravelProductBasedAutoAccommodationAllocationStrategy autoAccommodationAllocationStrategy;
	@Mock
	private TravellerService travellerService;
	@Mock
	private AccommodationMapService accommodationMapService;
	@Mock
	private Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator;
	@Mock
	private TravelCommerceStockService commerceStockService;
	@Mock
	private ModelService modelService;
	@Mock
	private SelectedAccommodationModel seclecteAccomodation;

	private TravelCartService travelCartService;
	@Mock
	private ProductReferenceService productReferenceService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		autoAccommodationAllocationStrategy = new TravelProductBasedAutoAccommodationAllocationStrategy();
		travelCartService = new DefaultTravelCartService();
		autoAccommodationAllocationStrategy.setAccommodationMapService(accommodationMapService);
		autoAccommodationAllocationStrategy.setTravellerService(travellerService);
		autoAccommodationAllocationStrategy.setCommerceStockService(commerceStockService);
		autoAccommodationAllocationStrategy.setModelService(modelService);
		autoAccommodationAllocationStrategy.setConfiguredAccomNumberComparator(configuredAccomNumberComparator);
		autoAccommodationAllocationStrategy.setTravelCartService(travelCartService);
		autoAccommodationAllocationStrategy.setProductReferenceService(productReferenceService);
	}

	@Test
	public void testAutoAllocateSeatForAccommodationMapDataSetUpException()
	{
		final List<String> travellerReferences = new ArrayList<>();
		travellerReferences.add("00000001");

		final List<TravellerModel> travellers = new ArrayList<>();
		final TravellerModel traveller1 = new TravellerModel();
		traveller1.setUid("00000001");
		traveller1.setLabel("adult1");
		travellers.add(traveller1);
		final TravellerModel traveller2 = new TravellerModel();
		traveller2.setUid("00000002");
		traveller2.setLabel("adult2");
		travellers.add(traveller2);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setCode("EZY028456392");
		transportOffering.setNumber("EZY0284");

		final List<SelectedAccommodationModel> selectedAccomodationList = new ArrayList<>();
		final SelectedAccommodationModel seclectedaccom = new SelectedAccommodationModel();
		seclectedaccom.setTransportOffering(transportOffering);
		final ConfiguredAccommodationModel selConfiguredAccommodation = new ConfiguredAccommodationModel();
		selConfiguredAccommodation.setIdentifier("45A");
		selConfiguredAccommodation.setType(ConfiguredAccommodationType.SEAT);
		final ProductModel seatproduct = new ProductModel();
		seatproduct.setCode("ACPECOSEAT1");
		seatproduct.setProductType(ProductType.ACCOMMODATION);
		selConfiguredAccommodation.setProduct(seatproduct);
		seclectedaccom.setConfiguredAccommodation(selConfiguredAccommodation);
		seclectedaccom.setStatus(AccommodationStatus.OCCUPIED);
		seclectedaccom.setTraveller(traveller2);
		selectedAccomodationList.add(seclectedaccom);

		final Map<Integer, List<TravellerModel>> travellersPerLegMap = new HashMap<>();
		travellersPerLegMap.put(0, travellers);

		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<>();
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntry.setEntryNumber(1);
		orderEntryInfo.setOriginDestinationRefNumber(0);
		final ProductModel product = new ProductModel();
		product.setCode("ORT6");
		product.setProductType(ProductType.FARE_PRODUCT);
		orderEntry.setProduct(product);
		final List<TransportOfferingModel> transportOfferingList = new ArrayList<>();
		final TransportVehicleModel vehicleModel = new TransportVehicleModel();
		final TransportVehicleInfoModel vehicleInfo = new TransportVehicleInfoModel();
		vehicleInfo.setCode("AIR727");
		vehicleModel.setTransportVehicleInfo(null);
		transportOffering.setTransportVehicle(null);
		transportOfferingList.add(transportOffering);
		orderEntryInfo.setTransportOfferings(transportOfferingList);
		final TravelRouteModel travelRoute = new TravelRouteModel();
		travelRoute.setCode("LTN_CDG");
		orderEntryInfo.setTravelRoute(travelRoute);
		orderEntry.setActive(true);
		orderEntryList.add(orderEntry);
		orderEntry.setTravelOrderEntryInfo(orderEntryInfo);
		orderEntry.setType(OrderEntryType.TRANSPORT);
		abstractOrderModel.setSelectedAccommodations(selectedAccomodationList);
		abstractOrderModel.setEntries(orderEntryList);

		final AccommodationMapModel accommodationMap = new AccommodationMapModel();
		accommodationMap.setCode("config1");
		accommodationMap.setType(TransportVehicleType.AIRCRAFT);
		final List<ConfiguredAccommodationModel> configuredAccommodationList = new ArrayList<>();
		final ConfiguredAccommodationModel configuredAccommodation1 = new ConfiguredAccommodationModel();
		configuredAccommodation1.setIdentifier("44A");
		configuredAccommodation1.setType(ConfiguredAccommodationType.SEAT);
		configuredAccommodation1.setProduct(seatproduct);
		configuredAccommodationList.add(configuredAccommodation1);
		final ConfiguredAccommodationModel configuredAccommodation2 = new ConfiguredAccommodationModel();
		configuredAccommodation2.setIdentifier("45A");
		configuredAccommodation2.setType(ConfiguredAccommodationType.SEAT);
		configuredAccommodation2.setProduct(seatproduct);
		configuredAccommodationList.add(configuredAccommodation2);
		accommodationMap.setConfiguredAccommodation(configuredAccommodationList);

		final Collection<ProductReferenceModel> productReferences = Collections.singletonList(new ProductReferenceModel());



		when(autoAccommodationAllocationStrategy.getTravellerService().getTravellersPerLeg(abstractOrderModel))
				.thenReturn(travellersPerLegMap);
		when(autoAccommodationAllocationStrategy.getAccommodationMapService()
				.getAccommodationMapConfiguration(Matchers.any(AccommodationMapModel.class))).thenReturn(configuredAccommodationList);

		when(autoAccommodationAllocationStrategy.getAccommodationMapService().getAccommodationMap(Matchers.anyString(),
				Matchers.any(TransportOfferingModel.class), Matchers.anyString())).thenReturn(accommodationMap);

		when(autoAccommodationAllocationStrategy.getAccommodationMapService().getSelectedAccommodations(
				Matchers.any(TransportOfferingModel.class), Matchers.anyListOf(AccommodationStatus.class),
				Matchers.anyListOf(OrderStatus.class))).thenReturn(selectedAccomodationList);

		when(autoAccommodationAllocationStrategy.getCommerceStockService().getStockLevel(Matchers.any(ProductModel.class),
				Matchers.anyCollectionOf(TransportOfferingModel.class))).thenReturn(10L);

		when(autoAccommodationAllocationStrategy.getModelService().create(SelectedAccommodationModel.class))
				.thenReturn(seclecteAccomodation);
		when(productReferenceService.getProductReferencesForSourceAndTarget(product, configuredAccommodation1.getProduct(), true))
				.thenReturn(productReferences);


		doNothing().when(autoAccommodationAllocationStrategy.getModelService()).save(Matchers.any(AbstractOrderModel.class));

		autoAccommodationAllocationStrategy.autoAllocateSeat(abstractOrderModel, 0, travellerReferences);

	}

	@Test
	public void testAutoAllocateSeatForOutboundLeg()
	{
		final List<String> travellerReferences = new ArrayList<>();
		travellerReferences.add("00000001");

		final List<TravellerModel> travellers = new ArrayList<>();
		final TravellerModel traveller1 = new TravellerModel();
		traveller1.setUid("00000001");
		traveller1.setLabel("adult1");
		travellers.add(traveller1);
		final TravellerModel traveller2 = new TravellerModel();
		traveller2.setUid("00000002");
		traveller2.setLabel("adult2");
		travellers.add(traveller2);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setCode("EZY028456392");
		transportOffering.setNumber("EZY0284");

		final List<SelectedAccommodationModel> selectedAccomodationList = new ArrayList<>();
		final SelectedAccommodationModel seclectedaccom = new SelectedAccommodationModel();
		seclectedaccom.setTransportOffering(transportOffering);
		final ConfiguredAccommodationModel selConfiguredAccommodation = new ConfiguredAccommodationModel();
		selConfiguredAccommodation.setIdentifier("45A");
		selConfiguredAccommodation.setType(ConfiguredAccommodationType.SEAT);
		final ProductModel seatproduct = new ProductModel();
		seatproduct.setCode("ACPECOSEAT1");
		seatproduct.setProductType(ProductType.ACCOMMODATION);
		selConfiguredAccommodation.setProduct(seatproduct);
		seclectedaccom.setConfiguredAccommodation(selConfiguredAccommodation);
		seclectedaccom.setStatus(AccommodationStatus.OCCUPIED);
		seclectedaccom.setTraveller(traveller2);
		selectedAccomodationList.add(seclectedaccom);

		final Map<Integer, List<TravellerModel>> travellersPerLegMap = new HashMap<>();
		travellersPerLegMap.put(0, travellers);

		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<>();
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntry.setEntryNumber(1);
		orderEntryInfo.setOriginDestinationRefNumber(0);
		final ProductModel product = new ProductModel();
		product.setCode("ORT6");
		product.setProductType(ProductType.FARE_PRODUCT);
		orderEntry.setProduct(product);
		final List<TransportOfferingModel> transportOfferingList = new ArrayList<>();
		final TransportVehicleModel vehicleModel = new TransportVehicleModel();
		final TransportVehicleInfoModel vehicleInfo = new TransportVehicleInfoModel();
		vehicleInfo.setCode("AIR727");
		vehicleModel.setTransportVehicleInfo(vehicleInfo);
		transportOffering.setTransportVehicle(vehicleModel);
		transportOfferingList.add(transportOffering);
		orderEntryInfo.setTransportOfferings(transportOfferingList);
		final TravelRouteModel travelRoute = new TravelRouteModel();
		travelRoute.setCode("LTN_CDG");
		orderEntryInfo.setTravelRoute(travelRoute);
		orderEntry.setActive(true);
		orderEntryList.add(orderEntry);
		orderEntry.setTravelOrderEntryInfo(orderEntryInfo);
		orderEntry.setType(OrderEntryType.TRANSPORT);
		abstractOrderModel.setSelectedAccommodations(selectedAccomodationList);
		abstractOrderModel.setEntries(orderEntryList);

		final AccommodationMapModel accommodationMap = new AccommodationMapModel();
		accommodationMap.setCode("config1");
		accommodationMap.setType(TransportVehicleType.AIRCRAFT);
		final List<ConfiguredAccommodationModel> configuredAccommodationList = new ArrayList<>();
		final ConfiguredAccommodationModel configuredAccommodation1 = new ConfiguredAccommodationModel();
		configuredAccommodation1.setIdentifier("44A");
		configuredAccommodation1.setType(ConfiguredAccommodationType.SEAT);
		configuredAccommodation1.setProduct(seatproduct);
		configuredAccommodationList.add(configuredAccommodation1);
		final ConfiguredAccommodationModel configuredAccommodation2 = new ConfiguredAccommodationModel();
		configuredAccommodation2.setIdentifier("45A");
		configuredAccommodation2.setType(ConfiguredAccommodationType.SEAT);
		configuredAccommodation2.setProduct(seatproduct);
		configuredAccommodationList.add(configuredAccommodation2);
		accommodationMap.setConfiguredAccommodation(configuredAccommodationList);

		final Collection<ProductReferenceModel> productReferences = Collections.singletonList(new ProductReferenceModel());



		when(autoAccommodationAllocationStrategy.getTravellerService().getTravellersPerLeg(abstractOrderModel))
				.thenReturn(travellersPerLegMap);
		when(autoAccommodationAllocationStrategy.getAccommodationMapService()
				.getAccommodationMapConfiguration(Matchers.any(AccommodationMapModel.class)))
						.thenReturn(configuredAccommodationList);

		when(autoAccommodationAllocationStrategy.getAccommodationMapService().getAccommodationMap(Matchers.anyString(),
				Matchers.any(TransportOfferingModel.class), Matchers.anyString())).thenReturn(accommodationMap);

		when(autoAccommodationAllocationStrategy.getAccommodationMapService().getSelectedAccommodations(
				Matchers.any(TransportOfferingModel.class), Matchers.anyListOf(AccommodationStatus.class),
				Matchers.anyListOf(OrderStatus.class))).thenReturn(selectedAccomodationList);

		when(autoAccommodationAllocationStrategy.getCommerceStockService().getStockLevel(Matchers.any(ProductModel.class),
				Matchers.anyCollectionOf(TransportOfferingModel.class))).thenReturn(10L);

		when(autoAccommodationAllocationStrategy.getModelService().create(SelectedAccommodationModel.class))
				.thenReturn(seclecteAccomodation);
		when(productReferenceService.getProductReferencesForSourceAndTarget(product, configuredAccommodation1.getProduct(), true))
				.thenReturn(productReferences);


		doNothing().when(autoAccommodationAllocationStrategy.getModelService()).save(Matchers.any(AbstractOrderModel.class));

		autoAccommodationAllocationStrategy.autoAllocateSeat(abstractOrderModel, 0, travellerReferences);

	}
}