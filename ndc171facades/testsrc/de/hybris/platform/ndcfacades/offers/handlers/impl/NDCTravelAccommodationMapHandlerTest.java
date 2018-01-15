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

package de.hybris.platform.ndcfacades.offers.handlers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.CabinClassData;
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
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.commercefacades.travel.seatmap.data.CabinData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapRequestData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SegmentInfoData;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.enums.ProximityItemType;
import de.hybris.platform.travelservices.enums.ProximityRelativePosition;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.travel.ProximityItemModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NDCTravelAccommodationMapHandlerTest
{

	private static final String UNAVAILABLE_SEAT_INDICATOR = "UNAVAILABLE";


	@InjectMocks
	NDCTravelAccommodationMapHandler handler;

	@Mock
	private AccommodationMapService accommodationMapService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private TravelCommerceStockService commerceStockService;

	@Mock
	private TravelCommercePriceService travelCommercePriceService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator;

	@Mock
	private Comparator<SeatInfoData> accommodationInfoDataComparator;

	@Mock
	private Converter<TravellerModel, TravellerData> travellerDataConverter;

	@Mock
	private ProductReferenceService productReferenceService;

	@Mock
	private ProductService productService;


	@Test
	public void testCreateSeatForNoAvailability()
	{
		final TestSetUp testSetup = new TestSetUp();
		final CabinData cabinData = testSetup.createCabinData("TEST_CABIN_CLASS");
		final List<SeatInfoData> seatInfo = new ArrayList<>();
		handler.createSeat(testSetup.prepareOffersRequest(), seatInfo, 1,
				testSetup.createConfiguredAccommodationModel(Boolean.FALSE, "TEST_SEAT", ConfiguredAccommodationType.CABIN),
				cabinData, testSetup.createTransportOfferingModel("TEST_TRANSPORT_OFFERING"),
				null, null, new HashMap<>());
		assertTrue(Objects.nonNull(seatInfo.get(0).getTotalFare()));
	}

	@Test
	public void testCreateSeatForDifferentTransportOffering()
	{
		final TestSetUp testSetup = new TestSetUp();
		final CabinData cabinData = testSetup.createCabinData("TEST_CABIN_CLASS");
		final List<SeatInfoData> seatInfo = new ArrayList<>();
		handler.createSeat(testSetup.prepareOffersRequest(), seatInfo, 1,
				testSetup.createConfiguredAccommodationModel(Boolean.TRUE, "TEST_SEAT", ConfiguredAccommodationType.CABIN),
				cabinData, testSetup.createTransportOfferingModel("TEST_TRANSPORT_OFFERING"),
				null, null, new HashMap<>());
		assertTrue(Objects.isNull(seatInfo.get(0).getTotalFare()));
		assertEquals(UNAVAILABLE_SEAT_INDICATOR, cabinData.getSeatAvailability().get(0).getAvailabilityIndicator());
	}

	@Test
	public void testCreateSeatForDifferentCabin()
	{
		final TestSetUp testSetup = new TestSetUp();
		final CabinData cabinData = testSetup.createCabinData("TEST_CABIN_CLASS");
		final List<SeatInfoData> seatInfo = new ArrayList<>();
		handler.createSeat(testSetup.prepareOffersRequest(), seatInfo, 1,
				testSetup.createConfiguredAccommodationModel(Boolean.TRUE, "TEST_SEAT", ConfiguredAccommodationType.CABIN),
				cabinData, testSetup.createTransportOfferingModel("TEST_TRANSPORT_OFFERING_CODE"),
				null, null, new HashMap<>());
		assertTrue(Objects.isNull(seatInfo.get(0).getTotalFare()));
		assertEquals(UNAVAILABLE_SEAT_INDICATOR, cabinData.getSeatAvailability().get(0).getAvailabilityIndicator());
	}

	@Test
	public void testCreateSeat()
	{
		final TestSetUp testSetup = new TestSetUp();
		final CabinData cabinData = testSetup.createCabinData("ECONOMY");
		final List<SeatInfoData> seatInfo = new ArrayList<>();
		final List<SelectedAccommodationModel> selectedAccommodations = Arrays.asList(testSetup.createSelectedAccommodation());
		handler.createSeat(testSetup.prepareOffersRequest(), seatInfo, 1,
				testSetup.createConfiguredAccommodationModel(Boolean.TRUE, "TEST_SEAT", ConfiguredAccommodationType.CABIN),
				cabinData, testSetup.createTransportOfferingModel("TEST_TRANSPORT_OFFERING_CODE"),
				testSetup.createInboundIteneraryWithRoute(), selectedAccommodations, new HashMap<>());
		assertTrue(CollectionUtils.isNotEmpty(cabinData.getSeatAvailability()));
	}


	private class TestSetUp
	{

		public SelectedAccommodationModel createSelectedAccommodation()
		{
			final SelectedAccommodationModel selectedAccommodation = new SelectedAccommodationModel();
			selectedAccommodation.setConfiguredAccommodation(
					createConfiguredAccommodationModel(Boolean.TRUE, "TEST_SEAT", ConfiguredAccommodationType.CABIN));
			selectedAccommodation.setTransportOffering(createTransportOfferingModel("TEST_TRANSPORT_OFFERING_CODE"));
			return selectedAccommodation;
		}

		public CabinData createCabinData(final String code)
		{
			final CabinClassData cabinClassData = new CabinClassData();
			cabinClassData.setCode(code);
			final CabinData cabinData = new CabinData();
			cabinData.setCabinClass(cabinClassData);
			cabinData.setSeatAvailability(new ArrayList<>());
			return cabinData;
		}

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
			offerRequestData.setSeatMapRequest(createSeatMapRequestData());

			return offerRequestData;
		}

		private SeatMapRequestData createSeatMapRequestData()
		{
			final SeatMapRequestData seatMapRequestData = new SeatMapRequestData();
			seatMapRequestData.setSegmentInfoDatas(Collections.singletonList(createSegmentInfoData("TEST_TRANSPORT_OFFERING_CODE")));
			return seatMapRequestData;

		}

		private SegmentInfoData createSegmentInfoData(final String transportOfferingCode)
		{
			final SegmentInfoData segmentInfoData = new SegmentInfoData();
			segmentInfoData.setCabinClass("ECONOMY");
			segmentInfoData.setTransportOfferingCode(transportOfferingCode);
			return segmentInfoData;
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
