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

package de.hybris.platform.travelfacades.ancillary.search.accommodation.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.populators.TransportOfferingPopulator;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.enums.ProximityItemType;
import de.hybris.platform.travelservices.enums.ProximityRelativePosition;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.seating.AircraftCabinModel;
import de.hybris.platform.travelservices.model.seating.AircraftDeckModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.ProximityItemModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationMapHandlerTest
{

	@Mock
	private AccommodationMapService accommodationMapService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private TransportOfferingPopulator transportOfferingPopulator;

	@Mock
	private TransportOfferingModel transportOfferingModel;

	@Mock
	private AccommodationMapModel accommodationMapModel;

	@Mock
	private ConfiguredAccommodationModel configuredAccommodationModel;

	@Mock
	private TravelCommercePriceService travelCommercePriceService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator;

	@Mock
	private TravelCommerceStockService commerceStockService;

	@Mock
	private Converter<TravellerModel, TravellerData> travellerDataConverter;

	@InjectMocks
	private final AccommodationMapHandler accommodationMapHandler = new AccommodationMapHandler();

	@Test
	public void accommodationMapItinerariesDoesNotExistTest()
	{
		final OfferRequestData offerRequestData = new OfferRequestData();
		final OfferResponseData offerResponseData = new OfferResponseData();
		accommodationMapHandler.handle(offerRequestData, offerResponseData);
		Assert.assertNull(offerResponseData.getItineraries());
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void accommodationMapVehicleConfigDoesNotExistTest()
	{
		final TestDataSetUp mockOfferRequestData = new TestDataSetUp();
		final OfferRequestData offerRequestData = mockOfferRequestData.prepareOffersRequest();
		final OfferResponseData offerResponseData = new OfferResponseData();
		Mockito.when(transportOfferingService.getTransportOffering(Matchers.anyString())).thenReturn(transportOfferingModel);
		accommodationMapHandler.handle(offerRequestData, offerResponseData);
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void accommodationMapConfigurationNotFoundThrowsExceptionTest()
	{
		final TestDataSetUp mockOfferRequestData = new TestDataSetUp();
		final OfferRequestData offerRequestData = mockOfferRequestData.prepareOffersRequest();
		final OfferResponseData offerResponseData = new OfferResponseData();
		Mockito.when(transportOfferingService.getTransportOffering(Matchers.anyString())).thenReturn(transportOfferingModel);
		Mockito.when(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).thenReturn(accommodationMapModel);
		Mockito.when(accommodationMapService.getAccommodationMapConfiguration(accommodationMapModel)).thenReturn(null);
		accommodationMapHandler.handle(offerRequestData, offerResponseData);

	}


	@Test
	public void accommodationMapsMatchNoOfTransportOfferingsForCorrectSetofDataTest()
	{
		final TestDataSetUp mockOfferRequestData = new TestDataSetUp();
		final OfferRequestData offerRequestData = mockOfferRequestData.prepareOffersRequest();
		final OfferResponseData offerResponseData = new OfferResponseData();
		Mockito.when(transportOfferingService.getTransportOffering(Matchers.anyString())).thenReturn(transportOfferingModel);
		Mockito.when(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).thenReturn(accommodationMapModel);
		final List<ConfiguredAccommodationModel> configuredAccommodations = new ArrayList<>();
		configuredAccommodations.add(configuredAccommodationModel);
		Mockito.when(accommodationMapService.getAccommodationMapConfiguration(accommodationMapModel))
				.thenReturn(configuredAccommodations);
		accommodationMapHandler.handle(offerRequestData, offerResponseData);

		final int noOfTransportOfferings = getNoOfTransportOfferings(offerRequestData);
		Assert.assertEquals(noOfTransportOfferings, offerResponseData.getSeatMap().getSeatMap().size());

	}

	@Test
	public void populateSeatMap()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferRequestData offerRequestData = testDataSetUp.prepareOffersRequest();
		final OfferResponseData offerResponseData = new OfferResponseData();
		Mockito.when(transportOfferingService.getTransportOffering(Matchers.anyString())).thenReturn(transportOfferingModel);
		Mockito.when(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).thenReturn(accommodationMapModel);
		final List<ConfiguredAccommodationModel> configuredAccommodations = new ArrayList<>();
		configuredAccommodations.add(testDataSetUp.createConfiguredAccommodationModel(true));
		Mockito.when(accommodationMapService.getAccommodationMapConfiguration(accommodationMapModel))
				.thenReturn(configuredAccommodations);
		Mockito.when(configuredAccomNumberComparator.compare(Matchers.any(ConfiguredAccommodationModel.class),
				Matchers.any(ConfiguredAccommodationModel.class))).thenReturn(0);
		accommodationMapHandler.setConfiguredAccomNumberComparator(configuredAccomNumberComparator);
		Mockito
				.when(travelCommercePriceService.getPriceInformation(Matchers.any(ProductModel.class),
						Matchers.eq(PriceRowModel.TRANSPORTOFFERINGCODE), Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceInformation());
		Mockito.when(
				travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
						Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceData());
		Mockito.when(commerceStockService.getStockLevel(Matchers.any(ProductModel.class), Matchers.any(Collection.class)))
				.thenReturn(100L);
		accommodationMapHandler.handle(offerRequestData, offerResponseData);

		final int noOfTransportOfferings = getNoOfTransportOfferings(offerRequestData);
		Assert.assertEquals(noOfTransportOfferings, offerResponseData.getSeatMap().getSeatMap().size());
	}

	@Test
	public void populateSeatMapPriceTravelSector()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferRequestData offerRequestData = testDataSetUp.prepareOffersRequest();
		final OfferResponseData offerResponseData = new OfferResponseData();
		Mockito.when(transportOfferingService.getTransportOffering(Matchers.anyString()))
				.thenReturn(testDataSetUp.createTransportOfferingModel("EZY4567060320160725"));
		Mockito.when(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).thenReturn(accommodationMapModel);
		final List<ConfiguredAccommodationModel> configuredAccommodations = new ArrayList<>();
		configuredAccommodations.add(testDataSetUp.createConfiguredAccommodationModel(true));
		Mockito.when(accommodationMapService.getAccommodationMapConfiguration(accommodationMapModel))
				.thenReturn(configuredAccommodations);
		Mockito.when(configuredAccomNumberComparator.compare(Matchers.any(ConfiguredAccommodationModel.class),
				Matchers.any(ConfiguredAccommodationModel.class))).thenReturn(0);
		accommodationMapHandler.setConfiguredAccomNumberComparator(configuredAccomNumberComparator);
		Mockito
				.when(travelCommercePriceService.getPriceInformation(Matchers.any(ProductModel.class),
						Matchers.eq(PriceRowModel.TRAVELSECTORCODE), Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceInformation());
		Mockito.when(
				travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
						Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceData());
		Mockito.when(commerceStockService.getStockLevel(Matchers.any(ProductModel.class), Matchers.any(Collection.class)))
				.thenReturn(100L);
		accommodationMapHandler.handle(offerRequestData, offerResponseData);

		final int noOfTransportOfferings = getNoOfTransportOfferings(offerRequestData);
		Assert.assertEquals(noOfTransportOfferings, offerResponseData.getSeatMap().getSeatMap().size());
	}

	@Test
	public void populateSeatMapPriceTravelRoute()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferRequestData offerRequestData = testDataSetUp.prepareOffersRequest();
		final OfferResponseData offerResponseData = new OfferResponseData();
		Mockito.when(transportOfferingService.getTransportOffering(Matchers.anyString()))
				.thenReturn(testDataSetUp.createTransportOfferingModel("EZY4567060320160725"));
		Mockito.when(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).thenReturn(accommodationMapModel);
		final List<ConfiguredAccommodationModel> configuredAccommodations = new ArrayList<>();
		configuredAccommodations.add(testDataSetUp.createConfiguredAccommodationModel(true));
		Mockito.when(accommodationMapService.getAccommodationMapConfiguration(accommodationMapModel))
				.thenReturn(configuredAccommodations);
		Mockito.when(configuredAccomNumberComparator.compare(Matchers.any(ConfiguredAccommodationModel.class),
				Matchers.any(ConfiguredAccommodationModel.class))).thenReturn(0);
		accommodationMapHandler.setConfiguredAccomNumberComparator(configuredAccomNumberComparator);
		Mockito
				.when(travelCommercePriceService.getPriceInformation(Matchers.any(ProductModel.class),
						Matchers.eq(PriceRowModel.TRAVELROUTECODE), Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceInformation());
		Mockito.when(
				travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
						Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceData());
		Mockito.when(commerceStockService.getStockLevel(Matchers.any(ProductModel.class), Matchers.any(Collection.class)))
				.thenReturn(100L);
		accommodationMapHandler.handle(offerRequestData, offerResponseData);

		final int noOfTransportOfferings = getNoOfTransportOfferings(offerRequestData);
		Assert.assertEquals(noOfTransportOfferings, offerResponseData.getSeatMap().getSeatMap().size());
	}

	@Test
	public void populateSeatMapSelectedAccommodations()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferRequestData offerRequestData = testDataSetUp.prepareOffersRequest();
		final OfferResponseData offerResponseData = new OfferResponseData();
		Mockito.when(transportOfferingService.getTransportOffering(Matchers.anyString()))
				.thenReturn(testDataSetUp.createTransportOfferingModel("EZY4567060320160725"));
		Mockito.when(accommodationMapService.getAccommodationMap(Matchers.anyString(), Matchers.any(TransportOfferingModel.class),
				Matchers.anyString(), Matchers.any(TravelSectorData.class))).thenReturn(accommodationMapModel);
		final List<ConfiguredAccommodationModel> configuredAccommodations = new ArrayList<>();
		configuredAccommodations.add(testDataSetUp.createConfiguredAccommodationModel(true));
		Mockito.when(accommodationMapService.getAccommodationMapConfiguration(accommodationMapModel))
				.thenReturn(configuredAccommodations);
		Mockito.when(configuredAccomNumberComparator.compare(Matchers.any(ConfiguredAccommodationModel.class),
				Matchers.any(ConfiguredAccommodationModel.class))).thenReturn(0);
		accommodationMapHandler.setConfiguredAccomNumberComparator(configuredAccomNumberComparator);
		Mockito
				.when(travelCommercePriceService.getPriceInformation(Matchers.any(ProductModel.class),
						Matchers.eq(PriceRowModel.TRANSPORTOFFERINGCODE), Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceInformation());
		Mockito.when(
				travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
						Matchers.anyString()))
				.thenReturn(testDataSetUp.createPriceData());
		Mockito.when(commerceStockService.getStockLevel(Matchers.any(ProductModel.class), Matchers.any(Collection.class)))
				.thenReturn(100L);
		Mockito
				.when(accommodationMapService.getSelectedAccommodations(Matchers.any(TransportOfferingModel.class),
						Matchers.any(List.class), Matchers.any(List.class)))
				.thenReturn(Stream.of(testDataSetUp.createSelectedAccommodationModel()).collect(Collectors.toList()));
		Mockito.when(travellerDataConverter.convert(Matchers.any(TravellerModel.class))).thenReturn(new TravellerData());
		accommodationMapHandler.handle(offerRequestData, offerResponseData);

		final int noOfTransportOfferings = getNoOfTransportOfferings(offerRequestData);
		Assert.assertEquals(noOfTransportOfferings, offerResponseData.getSeatMap().getSeatMap().size());
	}

	private int getNoOfTransportOfferings(final OfferRequestData offerRequestData)
	{
		int noOfTransportOfferings = 0;
		final List<ItineraryData> itineraries = offerRequestData.getItineraries();
		if (CollectionUtils.isEmpty(itineraries))
		{
			return noOfTransportOfferings;
		}
		for (final ItineraryData itineraryData : itineraries)
		{
			final List<OriginDestinationOptionData> originDestinationOptions = itineraryData.getOriginDestinationOptions();
			if (CollectionUtils.isEmpty(originDestinationOptions))
			{
				continue;
			}
			for (final OriginDestinationOptionData originDestinationOptionData : originDestinationOptions)
			{
				final List<TransportOfferingData> transportOfferings = originDestinationOptionData.getTransportOfferings();
				if (CollectionUtils.isEmpty(transportOfferings))
				{
					continue;
				}
				noOfTransportOfferings += transportOfferings.size();
			}
		}
		return noOfTransportOfferings;
	}


	/**
	 * Class to create mock offer request data which will be later replaced by creating real request data from cart
	 * entries
	 */
	private class TestDataSetUp
	{

		public OfferRequestData prepareOffersRequest()
		{
			final OfferRequestData offerRequestData = createOfferRequestData();
			final List<ItineraryData> itineraries = new ArrayList<>();
			final ItineraryData outboundItenerary = createOutboundIteneraryWithRoute();
			final TransportVehicleData transportVehicle = getTransportVehicle();
			final List<OriginDestinationOptionData> originDestinationOptionsOutbound = addOriginDestinationOptionsOutbound(
					outboundItenerary, transportVehicle);
			final ItineraryData inboundItenerary = createInboundIteneraryWithRoute();
			final List<OriginDestinationOptionData> originDestinationOptionsInbound = addOriginDestinationOptionsInbound(
					inboundItenerary, transportVehicle);

			offerRequestData.setItineraries(itineraries);

			final List<TravellerData> travellers = new ArrayList<>();
			final PassengerTypeData adultPassengerType = createAdultPassengerType();
			final TravellerData firstAdultPassenger = createPassenger(adultPassengerType, "John", "Doe");
			final TravellerData secondAdultPassenger = createPassenger(adultPassengerType, "Mary", "Doe");
			final PassengerTypeData childrenPassengerType = createChildrenPassengerType();
			final TravellerData childPassenger = createPassenger(childrenPassengerType, "Jack", "Doe");

			travellers.add(firstAdultPassenger);
			travellers.add(secondAdultPassenger);
			travellers.add(childPassenger);

			inboundItenerary.setTravellers(travellers);
			outboundItenerary.setTravellers(travellers);

			itineraries.add(outboundItenerary);
			itineraries.add(inboundItenerary);
			offerRequestData.setItineraries(itineraries);

			final SelectedOffersData selectedOffers = new SelectedOffersData();
			final List<OfferGroupData> offerGroups = new ArrayList<>();
			selectedOffers.setOfferGroups(offerGroups);

			final OfferGroupData outboundOfferingGroupDataAccomodation = new OfferGroupData();
			outboundOfferingGroupDataAccomodation.setCode("outboundOfferGroupDataAccomodation");
			outboundOfferingGroupDataAccomodation.setDescription("Free Seat as part of bundle selected");
			final List<OriginDestinationOfferInfoData> outboundOriginDestinationOptionOfferingsAccom = createOutboundOriginDestinationOptionAccomOfferings(
					outboundOfferingGroupDataAccomodation, originDestinationOptionsOutbound);
			setOfferPricingInfoData(outboundOriginDestinationOptionOfferingsAccom, firstAdultPassenger, secondAdultPassenger,
					childPassenger, "ACBIZZSEAT1");

			final OfferGroupData outboundOfferingGroupDataFreeLuggage = new OfferGroupData();
			outboundOfferingGroupDataFreeLuggage.setCode("outboundOfferGroupDataLuggageCheckin");
			outboundOfferingGroupDataFreeLuggage.setDescription("Free Luggage Checkin as part of bundle selected");
			final List<OriginDestinationOfferInfoData> outboundOriginDestinationOptionOfferingsLuggage = createOutboundOriginDestinationOptionLuggageOfferings(
					outboundOfferingGroupDataFreeLuggage, originDestinationOptionsOutbound);
			setOfferPricingInfoData(outboundOriginDestinationOptionOfferingsLuggage, firstAdultPassenger, secondAdultPassenger,
					childPassenger, "ACBIZZSEAT1");

			final OfferGroupData inboundOfferingGroupDataAccomodation = new OfferGroupData();
			inboundOfferingGroupDataAccomodation.setCode("inboundOfferGroupDataAccomodation");
			inboundOfferingGroupDataAccomodation.setDescription("Free Seat as part of bundle selected");
			final List<OriginDestinationOfferInfoData> inboundOriginDestinationOptionOfferingsAccom = createInboundOriginDestinationOptionAccomOfferings(
					inboundOfferingGroupDataAccomodation, originDestinationOptionsInbound);
			setOfferPricingInfoData(inboundOriginDestinationOptionOfferingsAccom, firstAdultPassenger, secondAdultPassenger,
					childPassenger, "ACBIZZSEAT1");

			final OfferGroupData inboundOfferingGroupDataFreeLuggage = new OfferGroupData();
			inboundOfferingGroupDataFreeLuggage.setCode("inboundOfferGroupDataLuggageCheckin");
			inboundOfferingGroupDataFreeLuggage.setDescription("Free Luggage Checkin as part of bundle selected");
			final List<OriginDestinationOfferInfoData> inboundOriginDestinationOfferInfosLuggage = createInboundOriginDestinationOptionLuggageOfferings(
					inboundOfferingGroupDataFreeLuggage, originDestinationOptionsInbound);
			setOfferPricingInfoData(inboundOriginDestinationOfferInfosLuggage, firstAdultPassenger, secondAdultPassenger,
					childPassenger, "ACBIZZSEAT1");

			offerGroups.add(outboundOfferingGroupDataAccomodation);
			offerGroups.add(outboundOfferingGroupDataFreeLuggage);
			offerGroups.add(inboundOfferingGroupDataAccomodation);
			offerGroups.add(inboundOfferingGroupDataFreeLuggage);
			offerRequestData.setSelectedOffers(selectedOffers);

			return offerRequestData;
		}

		private ConfiguredAccommodationModel createConfiguredAccommodationModel(final boolean deck)
		{
			//Seats
			final ConfiguredAccommodationModel seatConfigModel1 = createConfiguredAccommodationModel(true, "1A",
					ConfiguredAccommodationType.SEAT);
			final ConfiguredAccommodationModel seatConfigModel2 = createConfiguredAccommodationModel(true, "1B",
					ConfiguredAccommodationType.SEAT);

			//Columns
			final ConfiguredAccommodationModel colConfigModel1 = createConfiguredAccommodationModel(false, null,
					ConfiguredAccommodationType.COLUMN);
			colConfigModel1.setConfiguredAccommodation(
					Stream.of(seatConfigModel1).collect(Collectors.<ConfiguredAccommodationModel> toList()));
			final ConfiguredAccommodationModel colConfigModel2 = createConfiguredAccommodationModel(false, null,
					ConfiguredAccommodationType.COLUMN);
			colConfigModel2.setConfiguredAccommodation(
					Stream.of(seatConfigModel2).collect(Collectors.<ConfiguredAccommodationModel> toList()));

			//Row
			final ConfiguredAccommodationModel rowConfigModel = createConfiguredAccommodationModel(false, null,
					ConfiguredAccommodationType.ROW);
			rowConfigModel.setConfiguredAccommodation(
					Stream.of(colConfigModel1, colConfigModel2).collect(Collectors.<ConfiguredAccommodationModel> toList()));

			//cabin
			final ConfiguredAccommodationModel cabinConfigModel = new AircraftCabinModel();
			cabinConfigModel.setType(ConfiguredAccommodationType.CABIN);
			cabinConfigModel
					.setConfiguredAccommodation(Stream.of(rowConfigModel).collect(Collectors.<ConfiguredAccommodationModel> toList()));

			if (deck)
			{
				final ConfiguredAccommodationModel deckConfigModel = new AircraftDeckModel();
				deckConfigModel.setType(ConfiguredAccommodationType.DECK);
				deckConfigModel.setConfiguredAccommodation(
						Stream.of(cabinConfigModel).collect(Collectors.<ConfiguredAccommodationModel> toList()));
				return deckConfigModel;
			}

			return cabinConfigModel;

		}

		private ConfiguredAccommodationModel createConfiguredAccommodationModel(final boolean isSeat, final String identifier,
				final ConfiguredAccommodationType type)
		{
			final ConfiguredAccommodationModel seatConfigModel1 = new ConfiguredAccommodationModel();
			seatConfigModel1.setType(type);
			if (isSeat)
			{
				//Seats
				seatConfigModel1.setIdentifier(identifier);
				seatConfigModel1.setProduct(new AccommodationModel());
				final ProximityItemModel proximity = new ProximityItemModel();
				proximity.setType(ProximityItemType.GALLEY);
				proximity.setRelativePosition(ProximityRelativePosition.LEFT);
				seatConfigModel1.setProximityItem(Stream.of(proximity).collect(Collectors.toList()));
			}
			return seatConfigModel1;
		}

		private List<OriginDestinationOfferInfoData> createInboundOriginDestinationOptionLuggageOfferings(
				final OfferGroupData inboundOfferingGroupDataFreeLuggage,
				final List<OriginDestinationOptionData> originDestinationOptionsInbound)
		{
			final OriginDestinationOfferInfoData originDestinationOfferInfo1 = new OriginDestinationOfferInfoData();
			// Transport offering : EDI_LGW, Ancillary : free luggage checkin
			final TransportOfferingData transportOffering1 = originDestinationOptionsInbound.get(0).getTransportOfferings().get(0);
			final List<TransportOfferingData> inboundTransportOffering1 = new ArrayList<>();
			inboundTransportOffering1.add(transportOffering1);
			originDestinationOfferInfo1.setTransportOfferings(inboundTransportOffering1);

			final OriginDestinationOfferInfoData originDestinationOfferInfo2 = new OriginDestinationOfferInfoData();
			// Transport offering : LGW_CDG, Ancillary : free luggage checkin
			final List<TransportOfferingData> inboundTransportOffering2 = new ArrayList<>();
			final TransportOfferingData transportOffering2 = originDestinationOptionsInbound.get(0).getTransportOfferings().get(1);
			inboundTransportOffering2.add(transportOffering2);

			originDestinationOfferInfo2.setTransportOfferings(inboundTransportOffering2);


			final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<>();
			originDestinationOfferInfos.add(originDestinationOfferInfo1);
			originDestinationOfferInfos.add(originDestinationOfferInfo2);
			inboundOfferingGroupDataFreeLuggage.setOriginDestinationOfferInfos(originDestinationOfferInfos);

			return originDestinationOfferInfos;
		}

		private PriceInformation createPriceInformation()
		{
			final PriceValue priceValue = new PriceValue("GBP", 10.00d, false);
			final PriceInformation priceInfo = new PriceInformation(priceValue);
			return priceInfo;
		}

		private PriceData createPriceData()
		{
			final PriceData priceData = new PriceData();
			priceData.setValue(new BigDecimal(10d));
			priceData.setCurrencyIso("GBP");
			return priceData;
		}

		private TransportOfferingModel createTransportOfferingModel(final String code)
		{
			final TransportOfferingModel toModel = new TransportOfferingModel();
			toModel.setCode(code);
			toModel.setTravelSector(createTravelSector("LGW-CDG"));
			return toModel;
		}

		private TravelSectorModel createTravelSector(final String code)
		{
			final TravelSectorModel sector = new TravelSectorModel();
			sector.setCode(code);
			return sector;
		}

		private TravellerModel createTravellerModel()
		{
			final TravellerModel travellerModel = new TravellerModel();
			travellerModel.setUid("1234");
			return travellerModel;
		}

		private SelectedAccommodationModel createSelectedAccommodationModel()
		{
			final SelectedAccommodationModel selAccModel = new SelectedAccommodationModel();
			selAccModel.setConfiguredAccommodation(createConfiguredAccommodationModel(true, "1A", ConfiguredAccommodationType.SEAT));
			selAccModel.setTransportOffering(createTransportOfferingModel("EZY1234060320160725"));
			selAccModel.setTraveller(createTravellerModel());
			return selAccModel;
		}

		private List<OriginDestinationOfferInfoData> createInboundOriginDestinationOptionAccomOfferings(
				final OfferGroupData inboundOfferingGroupDataAccomodation,
				final List<OriginDestinationOptionData> originDestinationOptionsInbound)
		{
			final OriginDestinationOfferInfoData originDestinationOfferInfo1 = new OriginDestinationOfferInfoData();
			// Transport offering : EDI_LGW, Ancillary : Seat1
			final TransportOfferingData transportOffering1 = originDestinationOptionsInbound.get(0).getTransportOfferings().get(0);
			final List<TransportOfferingData> inboundTransportOffering1 = new ArrayList<>();
			inboundTransportOffering1.add(transportOffering1);
			originDestinationOfferInfo1.setTransportOfferings(inboundTransportOffering1);

			// Transport offering : LGW_CDG, Ancillary : Seat2
			final OriginDestinationOfferInfoData originDestinationOfferInfo2 = new OriginDestinationOfferInfoData();
			final TransportOfferingData transportOffering2 = originDestinationOptionsInbound.get(0).getTransportOfferings().get(1);
			final List<TransportOfferingData> inboundTransportOffering2 = new ArrayList<>();
			inboundTransportOffering2.add(transportOffering2);
			originDestinationOfferInfo2.setTransportOfferings(inboundTransportOffering2);

			final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<>();
			originDestinationOfferInfos.add(originDestinationOfferInfo1);
			originDestinationOfferInfos.add(originDestinationOfferInfo2);
			inboundOfferingGroupDataAccomodation.setOriginDestinationOfferInfos(originDestinationOfferInfos);

			return originDestinationOfferInfos;
		}

		private List<OriginDestinationOfferInfoData> createOutboundOriginDestinationOptionLuggageOfferings(
				final OfferGroupData outboundOfferingGroupDataFreeLuggage,
				final List<OriginDestinationOptionData> originDestinationOptionsOutbound)
		{
			final OriginDestinationOfferInfoData originDestinationOfferInfo1 = new OriginDestinationOfferInfoData();
			// Transport offering : CDG_LGW, Ancillary : free luggage checkin
			final TransportOfferingData transportOffering1 = originDestinationOptionsOutbound.get(0).getTransportOfferings().get(0);
			final List<TransportOfferingData> outboundTransportOffering1 = new ArrayList<>();
			outboundTransportOffering1.add(transportOffering1);
			originDestinationOfferInfo1.setTransportOfferings(outboundTransportOffering1);

			final OriginDestinationOfferInfoData originDestinationOfferInfo2 = new OriginDestinationOfferInfoData();
			// Transport offering : LGW_EDI, Ancillary : free luggage checkin
			final TransportOfferingData transportOffering2 = originDestinationOptionsOutbound.get(0).getTransportOfferings().get(1);
			final List<TransportOfferingData> outboundTransportOffering2 = new ArrayList<>();
			outboundTransportOffering2.add(transportOffering2);
			originDestinationOfferInfo2.setTransportOfferings(outboundTransportOffering2);

			final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<>();
			originDestinationOfferInfos.add(originDestinationOfferInfo1);
			originDestinationOfferInfos.add(originDestinationOfferInfo2);
			outboundOfferingGroupDataFreeLuggage.setOriginDestinationOfferInfos(originDestinationOfferInfos);

			return originDestinationOfferInfos;
		}

		private List<OriginDestinationOfferInfoData> createOutboundOriginDestinationOptionAccomOfferings(
				final OfferGroupData outboundOfferingGroupData,
				final List<OriginDestinationOptionData> originDestinationOptionsOutbound)
		{
			final OriginDestinationOfferInfoData originDestinationOfferInfo1 = new OriginDestinationOfferInfoData();
			// Transport offering : CDG_LGW, Ancillary : Seat1
			final TransportOfferingData transportOffering1 = originDestinationOptionsOutbound.get(0).getTransportOfferings().get(0);
			final List<TransportOfferingData> outboundTransportOffering1 = new ArrayList<>();
			outboundTransportOffering1.add(transportOffering1);
			originDestinationOfferInfo1.setTransportOfferings(outboundTransportOffering1);

			// Transport offering : LGW_EDI, Ancillary : Seat2
			final OriginDestinationOfferInfoData originDestinationOfferInfo2 = new OriginDestinationOfferInfoData();
			final TransportOfferingData transportOffering2 = originDestinationOptionsOutbound.get(0).getTransportOfferings().get(1);
			final List<TransportOfferingData> outboundTransportOffering2 = new ArrayList<>();
			outboundTransportOffering2.add(transportOffering2);
			originDestinationOfferInfo2.setTransportOfferings(outboundTransportOffering2);

			final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<>();
			originDestinationOfferInfos.add(originDestinationOfferInfo1);
			originDestinationOfferInfos.add(originDestinationOfferInfo2);
			outboundOfferingGroupData.setOriginDestinationOfferInfos(originDestinationOfferInfos);

			return originDestinationOfferInfos;
		}

		private PassengerTypeData createChildrenPassengerType()
		{
			final PassengerTypeData childrenPassengerType = new PassengerTypeData();
			childrenPassengerType.setCode("child");
			childrenPassengerType.setName("Child");
			return childrenPassengerType;
		}

		private TravellerData createPassenger(final PassengerTypeData passengerType, final String firstName, final String surName)
		{
			final TravellerData firstAdultPassengerData = new TravellerData();
			final PassengerInformationData firstAdultPassenger = new PassengerInformationData();
			firstAdultPassengerData.setTravellerInfo(firstAdultPassenger);
			firstAdultPassengerData.setTravellerType("Passenger");
			firstAdultPassenger.setFirstName(firstName);
			firstAdultPassenger.setSurname(surName);
			firstAdultPassenger.setPassengerType(passengerType);
			return firstAdultPassengerData;
		}

		private PassengerTypeData createAdultPassengerType()
		{
			final PassengerTypeData adultPassengerType = new PassengerTypeData();
			adultPassengerType.setCode("adult");
			adultPassengerType.setName("Adult");
			return adultPassengerType;
		}

		private List<OriginDestinationOptionData> addOriginDestinationOptionsInbound(final ItineraryData inboundItenerary,
				final TransportVehicleData transportVehicle)
		{
			final Calendar dayAfterTomorrow = Calendar.getInstance();
			dayAfterTomorrow.add(Calendar.DATE, 2);
			final int dayAfterTommDate = dayAfterTomorrow.get(Calendar.DATE);
			final int dayAfterTommMonth = (dayAfterTomorrow.get(Calendar.MONTH) + 1);
			final int currentYear = dayAfterTomorrow.get(Calendar.YEAR);
			final String dayMonthYearStr = (String.valueOf(dayAfterTommDate).length() == 1 ? ("0" + dayAfterTommDate)
					: String.valueOf(dayAfterTommDate))
					+ (String.valueOf(dayAfterTommMonth).length() == 1 ? ("0" + dayAfterTommMonth) : String.valueOf(dayAfterTommMonth))
					+ currentYear;
			final TransportOfferingData transportOfferingDataInbound1 = new TransportOfferingData();
			final String transportOfferingDataInboundCode1 = "EZY0802" + dayMonthYearStr + "0625";
			transportOfferingDataInbound1.setCode(transportOfferingDataInboundCode1);
			final Calendar departTimeCal1 = Calendar.getInstance();
			departTimeCal1.set(2016, 01, 03, 06, 25, 00);
			transportOfferingDataInbound1.setDepartureTime(departTimeCal1.getTime());
			transportOfferingDataInbound1.setNumber("EZY0802");
			final TravelSectorData sector1 = new TravelSectorData();
			sector1.setOrigin(createInboundOrigin("EDI"));
			sector1.setDestination(createInboundDestination("LGW"));
			transportOfferingDataInbound1.setSector(sector1);
			transportOfferingDataInbound1.setTransportVehicle(transportVehicle);

			final TransportOfferingData transportOfferingDataInbound2 = new TransportOfferingData();
			final String transportOfferingDataInboundCode2 = "EZY8323" + dayMonthYearStr + "0810";
			transportOfferingDataInbound2.setCode(transportOfferingDataInboundCode2);
			final Calendar departTimeCal2 = Calendar.getInstance();
			departTimeCal2.set(2016, 01, 03, 8, 10, 00);
			transportOfferingDataInbound2.setDepartureTime(departTimeCal2.getTime());
			transportOfferingDataInbound2.setNumber("EZY8323");
			final TravelSectorData sector2 = new TravelSectorData();
			sector2.setOrigin(createInboundOrigin("LGW"));
			sector2.setDestination(createInboundDestination("CDG"));
			transportOfferingDataInbound2.setSector(sector2);
			transportOfferingDataInbound2.setTransportVehicle(transportVehicle);

			final List<TransportOfferingData> transportOfferingsInbound = new ArrayList<>();
			transportOfferingsInbound.add(transportOfferingDataInbound1);
			transportOfferingsInbound.add(transportOfferingDataInbound2);
			final OriginDestinationOptionData originDestinationOptionDataInbound = new OriginDestinationOptionData();
			originDestinationOptionDataInbound.setTransportOfferings(transportOfferingsInbound);
			final List<OriginDestinationOptionData> originDestinationOptionsInbound = new ArrayList<>();
			originDestinationOptionsInbound.add(originDestinationOptionDataInbound);
			inboundItenerary.setOriginDestinationOptions(originDestinationOptionsInbound);
			return originDestinationOptionsInbound;
		}

		private ItineraryData createInboundIteneraryWithRoute()
		{
			final ItineraryData inboundItinerary = new ItineraryData();
			final TravelRouteData travelRoute = new TravelRouteData();
			travelRoute.setCode("EDI_LGW_CDG");
			final TransportFacilityData inboundDestination = createInboundDestination("CDG");
			final TransportFacilityData inboundOrigin = createInboundOrigin("EDI");
			travelRoute.setDestination(inboundDestination);
			travelRoute.setOrigin(inboundOrigin);
			inboundItinerary.setRoute(travelRoute);
			return inboundItinerary;
		}

		private TransportFacilityData createInboundOrigin(final String code)
		{
			final TransportFacilityData inboundOrigin = new TransportFacilityData();
			inboundOrigin.setCode(code);
			return inboundOrigin;
		}

		private TransportFacilityData createInboundDestination(final String code)
		{
			final TransportFacilityData inboundDestination = new TransportFacilityData();
			inboundDestination.setCode(code);
			return inboundDestination;
		}

		private TransportVehicleData getTransportVehicle()
		{
			final TransportVehicleData transportVehicleData = new TransportVehicleData();
			final TransportVehicleInfoData transportVehicleInfoData = new TransportVehicleInfoData();
			transportVehicleData.setVehicleInfo(transportVehicleInfoData);
			transportVehicleInfoData.setCode("A380-800");
			transportVehicleInfoData.setName("Hybris Airbus A380");
			return transportVehicleData;
		}

		private List<OriginDestinationOptionData> addOriginDestinationOptionsOutbound(final ItineraryData outboundItenerary,
				final TransportVehicleData transportVehicle)
		{
			final Calendar tomorrow = Calendar.getInstance();
			tomorrow.add(Calendar.DATE, 1);
			final int tommDate = tomorrow.get(Calendar.DATE);
			final int tommMonth = (tomorrow.get(Calendar.MONTH) + 1);
			final int currentYear = tomorrow.get(Calendar.YEAR);
			final String dayMonthYearStr = (String.valueOf(tommDate).length() == 1 ? ("0" + tommDate) : String.valueOf(tommDate))
					+ (String.valueOf(tommMonth).length() == 1 ? ("0" + tommMonth) : String.valueOf(tommMonth)) + currentYear;
			final TransportOfferingData transportOfferingDataOutbound1 = new TransportOfferingData();
			final String transportOfferingDataOutboundCode1 = "EZY8322" + dayMonthYearStr + "0730";
			transportOfferingDataOutbound1.setCode(transportOfferingDataOutboundCode1);
			final Calendar departTimeCal1 = Calendar.getInstance();
			departTimeCal1.set(2016, 01, 01, 07, 30, 00);
			transportOfferingDataOutbound1.setDepartureTime(departTimeCal1.getTime());
			transportOfferingDataOutbound1.setNumber("EZY8322");
			final TravelSectorData sectorOutbound1 = new TravelSectorData();
			sectorOutbound1.setOrigin(createOutboundOrigin("CDG"));
			sectorOutbound1.setDestination(createOutboundDestination("LGW"));
			transportOfferingDataOutbound1.setSector(sectorOutbound1);
			transportOfferingDataOutbound1.setTransportVehicle(transportVehicle);

			final TransportOfferingData transportOfferingDataOutbound2 = new TransportOfferingData();
			final String transportOfferingDataOutboundCode2 = "EZY0805" + dayMonthYearStr + "1530";
			transportOfferingDataOutbound2.setCode(transportOfferingDataOutboundCode2);
			final Calendar departTimeCal2 = Calendar.getInstance();
			departTimeCal2.set(2016, 01, 01, 15, 30, 00);
			transportOfferingDataOutbound2.setDepartureTime(departTimeCal2.getTime());
			transportOfferingDataOutbound2.setNumber("EZY0805");
			final TravelSectorData sectorOutbound2 = new TravelSectorData();
			sectorOutbound2.setOrigin(createOutboundOrigin("LGW"));
			sectorOutbound2.setDestination(createOutboundDestination("EDI"));
			transportOfferingDataOutbound2.setSector(sectorOutbound2);
			transportOfferingDataOutbound2.setTransportVehicle(transportVehicle);

			final List<TransportOfferingData> transportOfferingsOutbound = new ArrayList<>();
			transportOfferingsOutbound.add(transportOfferingDataOutbound1);
			transportOfferingsOutbound.add(transportOfferingDataOutbound2);
			final OriginDestinationOptionData originDestinationOptionDataOutbound = new OriginDestinationOptionData();
			originDestinationOptionDataOutbound.setTransportOfferings(transportOfferingsOutbound);
			final List<OriginDestinationOptionData> originDestinationOptionsOutbound = new ArrayList<>();
			originDestinationOptionsOutbound.add(originDestinationOptionDataOutbound);

			outboundItenerary.setOriginDestinationOptions(originDestinationOptionsOutbound);
			return originDestinationOptionsOutbound;
		}

		private ItineraryData createOutboundIteneraryWithRoute()
		{
			final ItineraryData outboundItenerary = new ItineraryData();
			final TravelRouteData travelRoute = new TravelRouteData();
			travelRoute.setCode("CDG_LGW_EDI");
			final TransportFacilityData origin = createOutboundOrigin("CDG");
			final TransportFacilityData destination = createOutboundDestination("EDI");
			travelRoute.setDestination(destination);
			travelRoute.setOrigin(origin);
			outboundItenerary.setRoute(travelRoute);
			return outboundItenerary;
		}

		private TransportFacilityData createOutboundDestination(final String code)
		{
			final TransportFacilityData destination = new TransportFacilityData();
			destination.setCode(code);
			return destination;
		}

		private TransportFacilityData createOutboundOrigin(final String code)
		{
			final TransportFacilityData origin = new TransportFacilityData();
			origin.setCode(code);
			return origin;
		}

		private OfferRequestData createOfferRequestData()
		{
			final OfferRequestData offerRequestData = new OfferRequestData();
			offerRequestData.setOfferStage(StringUtils.EMPTY);
			return offerRequestData;
		}

		private void setOfferPricingInfoData(final List<OriginDestinationOfferInfoData> originDestinationOfferInfos,
				final TravellerData firstAdultPassenger, final TravellerData secondAdultPassenger, final TravellerData childPassenger,
				final String ancillaryProductCode)
		{
			if (CollectionUtils.isNotEmpty(originDestinationOfferInfos))
			{
				for (final OriginDestinationOfferInfoData originDestinationOfferInfo : originDestinationOfferInfos)
				{
					final List<OfferPricingInfoData> pricingInfo = new ArrayList<>();
					final OfferPricingInfoData pricinginfoData = new OfferPricingInfoData();
					final ProductData ancillaryProduct = new ProductData();
					ancillaryProduct.setCode(ancillaryProductCode);
					pricinginfoData.setProduct(ancillaryProduct);
					pricinginfoData.setBundleIndicator(1);
					final List<TravellerBreakdownData> travellerBreakDowns = new ArrayList<>();

					final TravellerBreakdownData firstAdultTravellerBreakdown = new TravellerBreakdownData();
					firstAdultTravellerBreakdown.setTraveller(firstAdultPassenger);
					final PassengerFareData firstPassengerFare = new PassengerFareData();
					populatePassengerFare(firstPassengerFare);
					firstAdultTravellerBreakdown.setPassengerFare(firstPassengerFare);
					firstAdultTravellerBreakdown.setQuantity(1);

					final TravellerBreakdownData secondAdultTravellerBreakdown = new TravellerBreakdownData();
					secondAdultTravellerBreakdown.setTraveller(secondAdultPassenger);
					final PassengerFareData secondPassengerFare = new PassengerFareData();
					populatePassengerFare(secondPassengerFare);
					secondAdultTravellerBreakdown.setPassengerFare(secondPassengerFare);
					secondAdultTravellerBreakdown.setQuantity(1);

					final TravellerBreakdownData childTravellerBreakdown = new TravellerBreakdownData();
					childTravellerBreakdown.setTraveller(childPassenger);
					final PassengerFareData childPassengerFare = new PassengerFareData();
					populatePassengerFare(childPassengerFare);
					childTravellerBreakdown.setPassengerFare(childPassengerFare);
					childTravellerBreakdown.setQuantity(1);

					travellerBreakDowns.add(firstAdultTravellerBreakdown);
					travellerBreakDowns.add(secondAdultTravellerBreakdown);
					travellerBreakDowns.add(childTravellerBreakdown);
					pricinginfoData.setTravellerBreakdowns(travellerBreakDowns);
					pricingInfo.add(pricinginfoData);
					originDestinationOfferInfo.setOfferPricingInfos(pricingInfo);
				}
			}

		}

		private void populatePassengerFare(final PassengerFareData passengerFare)
		{
			final PriceData baseFare = new PriceData();
			baseFare.setFormattedValue("15.00");
			passengerFare.setBaseFare(baseFare);
			passengerFare.setDiscounts(null);
			passengerFare.setFees(null);
			passengerFare.setTaxes(null);
			final PriceData totalFare = new PriceData();
			totalFare.setFormattedValue("15.00");
			passengerFare.setTotalFare(totalFare);
		}

	}

}
