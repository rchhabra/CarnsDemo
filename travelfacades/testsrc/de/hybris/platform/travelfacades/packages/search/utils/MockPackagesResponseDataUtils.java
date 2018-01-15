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

package de.hybris.platform.travelfacades.packages.search.utils;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AwardData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.TimeSpanData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.packages.response.AccommodationPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackagesResponseData;
import de.hybris.platform.commercefacades.packages.response.StandardPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelProviderData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.enums.AwardType;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * MOCK DEALS RESPONSE DATA OBJECT SETUP
 */
public class MockPackagesResponseDataUtils
{
	private static final Logger LOG = Logger.getLogger(MockPackagesResponseDataUtils.class);

	private static final int OUT_REF_NUMBER = 0;
	private static final int RET_REF_NUMBER = 1;
	private static final String ECONOMY_BUNDLE = "ECONOMY";
	private static final String FARE_BASIS_CODE = "Y1234";

	public static PackagesResponseData createPackagesResponseData()
	{
		final PackageResponseData packageResponseData = new PackageResponseData();

		final AccommodationPackageResponseData accommodationPackageResponseData = new AccommodationPackageResponseData();
		accommodationPackageResponseData.setAccommodationAvailabilityResponse(createAccommodationAvailabilityResponseData());
		packageResponseData.setAccommodationPackageResponse(accommodationPackageResponseData);

		final TransportPackageResponseData transportPackageResponseData = new TransportPackageResponseData();
		transportPackageResponseData.setFareSearchResponse(prepareMockFareSelectionData(TripType.RETURN));
		packageResponseData.setTransportPackageResponse(transportPackageResponseData);

		packageResponseData.setStandardPackageResponses(createStandardPackageResponseDatas());

		packageResponseData.setPrice(createRateData(400.65d, 500.86d));

		packageResponseData.setStartingDatePattern("0 0 0 ? * MON,WED,SAT");

		final PackagesResponseData packagesResponseData = new PackagesResponseData();
		packagesResponseData.setPackageResponses(Arrays.asList(packageResponseData));


		return packagesResponseData;
	}

	protected static List<StandardPackageResponseData> createStandardPackageResponseDatas()
	{
		final List<StandardPackageResponseData> standardPackageResponseDatas = new ArrayList<>();

		final StandardPackageResponseData standardPackageResponseData1 = new StandardPackageResponseData();
		standardPackageResponseData1.setPackageProducts(Arrays.asList(createPackageProductData("Tour of City")));

		final StandardPackageResponseData standardPackageResponseData2 = new StandardPackageResponseData();
		standardPackageResponseData2.setPackageProducts(Arrays.asList(createPackageProductData("Sight Seeing")));

		standardPackageResponseDatas.add(standardPackageResponseData1);
		standardPackageResponseDatas.add(standardPackageResponseData2);

		return standardPackageResponseDatas;
	}

	protected static PackageProductData createPackageProductData(final String productName)
	{
		final PackageProductData packageProductData = new PackageProductData();
		packageProductData.setProduct(createProductData(productName));
		return packageProductData;
	}

	protected static ProductData createProductData(final String productName)
	{
		final ProductData productData = new ProductData();
		productData.setName(productName);
		return productData;
	}

	/**
	 * Creating a Fare Selection Data object to demonstrate various scenarios on Fare Selection page
	 *
	 * @param tripType
	 * @param priceDataFactory
	 * @param commonI18NService
	 * @param option
	 *           Same day return or different day return
	 * @return mock Fare Selection Data object
	 */
	protected static FareSelectionData prepareMockFareSelectionData(final TripType tripType)
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final List<PricedItineraryData> pricedItineraries = new ArrayList<PricedItineraryData>();

		/* TRAVEL PROVIDER */
		final TravelProviderData portaltechTravelProvider = new TravelProviderData();
		portaltechTravelProvider.setCode("PTRAIR");

		/* TRANSPORT VEHICLE */
		final TransportVehicleData boeing747 = createTransportVehicle("B747", "Boeing 747");

		/* LOCATIONS */
		final LocationData london = createLocation("LON", "London");
		final LocationData rome = createLocation("ROM", "Rome");

		/* TRANSPORT FACILITIES */
		final TransportFacilityData lhrAirport = createTransportFacility("LTN", london, "Luton Airport");
		final TransportFacilityData fcoAirport = createTransportFacility("FCO", rome, "Leonardo Da Vinci (Fiumicino) Airport");

		/* TERMINALS */
		final TerminalData lhrTerminal1 = createTerminal("LTN1", "Luton Airport Terminal 1");
		final TerminalData fcoTerminal1 = createTerminal("FCO1", "Fiumicino Terminal 1");

		/* ROUTES */
		// LTN -> DXB
		final TravelRouteData lhrFCORoute = createTravelRoute("LTNFCO", lhrAirport, fcoAirport);

		// DXB -> LTN
		final TravelRouteData fcoLTNRoute = createTravelRoute("FCOLTN", fcoAirport, lhrAirport);

		/* SECTORS */
		// LTN -> DXB
		final TravelSectorData lhrFCOSector = createTravelSector("LTNFCO", lhrAirport, fcoAirport,
				Arrays.asList(new TravelRouteData[]
				{ lhrFCORoute }));

		// DXB -> LTN
		final TravelSectorData fcoLTNSector = createTravelSector("FCOLTN", fcoAirport, lhrAirport,
				Arrays.asList(new TravelRouteData[]
				{ fcoLTNRoute }));

		/* Bind sectors to routes */
		lhrFCORoute.setSectors(Arrays.asList(new TravelSectorData[]
		{ lhrFCOSector }));

		fcoLTNRoute.setSectors(Arrays.asList(new TravelSectorData[]
		{ fcoLTNSector }));

		/* TRANSPORT OFFERINGS */
		// FLIGHT 1 - LTN-FCO 01/01/2016 07:00-15:00
		final TransportOfferingData flight1 = createTransportOffering("EZY8323201512070810", "EZY8323",
				parseDateTimeFromString("2016-01-01 07:00"), parseDateTimeFromString("2016-01-01 15:00"), lhrTerminal1, fcoTerminal1,
				portaltechTravelProvider, lhrFCOSector, createDurationMap(0, 8, 0), boeing747);

		// FLIGHT 2 - FCO-LTN 01/01/2016 16:00-21:00
		final TransportOfferingData flight2 = createTransportOffering("EZY2345201512071120", "EZY2435",
				parseDateTimeFromString("2016-01-01 16:00"), parseDateTimeFromString("2016-01-01 21:00"), fcoTerminal1, lhrTerminal1,
				portaltechTravelProvider, fcoLTNSector, createDurationMap(0, 5, 0), boeing747);



		/* Bind Transport Offerings to sectors */
		lhrFCOSector.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ flight1 }));

		fcoLTNSector.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ flight2 }));

		/* FARE PRODUCTS */
		final FareProductData fp1 = createFareProduct("ORT", 10d);


		/* BUNDLES */
		final List<TransportOfferingData> transportOfferingDatas = new ArrayList<TransportOfferingData>();
		transportOfferingDatas.addAll(Arrays.asList(new TransportOfferingData[]
		{ flight1, flight2 }));

		final List<FareProductData> economyFareProducts = new ArrayList<FareProductData>();
		economyFareProducts.addAll(Arrays.asList(new FareProductData[]
		{ fp1 }));

		final TravelBundleTemplateData economyBundle = createTravelBundle(ECONOMY_BUNDLE, "ecoBundle", ECONOMY_BUNDLE,
				economyFareProducts, transportOfferingDatas);

		/* PASSENGER TYPES */
		final PassengerTypeData adultType = createPassengerTypeData("adult");
		final PassengerTypeData childType = createPassengerTypeData("child");
		final PassengerTypeData infantType = createPassengerTypeData("infant");

		/* FARE INFO */
		// ECONOMY
		final FareInfoData ecoAdultInfo = createFareInfo(FARE_BASIS_CODE, fp1, Arrays.asList(new PassengerTypeData[]
		{ adultType }), tripType);

		final FareInfoData ecoChildInfo = createFareInfo(FARE_BASIS_CODE, fp1, Arrays.asList(new PassengerTypeData[]
		{ childType }), tripType);

		final FareInfoData ecoInfantInfo = createFareInfo(FARE_BASIS_CODE, fp1, Arrays.asList(new PassengerTypeData[]
		{ infantType }), tripType);

		/* PTC BREAKDOWNS */

		final PassengerTypeQuantityData ptqAD = createPTQ(adultType, 2);
		final PassengerTypeQuantityData ptqCH = createPTQ(childType, 1);
		final PassengerTypeQuantityData ptqIN = createPTQ(infantType, 0);

		// ECONOMY Adults PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownAD1 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqAD, 20d, 26d, Arrays.asList(new FareInfoData[]
		{ ecoAdultInfo }));

		// ECONOMY Child PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownCH1 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqCH, 10d, 12d, Arrays.asList(new FareInfoData[]
		{ ecoChildInfo }));

		// ECONOMY Infant PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownIN1 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqIN, 0d, 0d, Arrays.asList(new FareInfoData[]
		{ ecoInfantInfo }));

		/* ITINERARY PRICING INFOS */
		final ItineraryPricingInfoData ecoItineraryPricingInfo = createItineraryPricingInfo(
				Arrays.asList(new TravelBundleTemplateData[]
				{ economyBundle }), Arrays.asList(new PTCFareBreakdownData[]
				{ ptcFareBreakdownAD1, ptcFareBreakdownCH1, ptcFareBreakdownIN1 }), Boolean.TRUE, ECONOMY_BUNDLE);

		/* PRICED ITINERARIES */

		// PRICED ITINERARY 1 - LTN-FCO 01/01/2016 07:00-15:00
		final ItineraryData itineraryData1 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
		{ flight1 }), lhrFCORoute);
		final PricedItineraryData pricedItinerary1 = createPricedItinerary(1, RET_REF_NUMBER, Boolean.TRUE, itineraryData1,
				Arrays.asList(new ItineraryPricingInfoData[]
				{ ecoItineraryPricingInfo }));
		pricedItineraries.add(pricedItinerary1);


		/* RETURN PRICED ITINERARIES */

		// PRICED ITINERARY 2 - FCO-LTN 01/01/2016 16:00-21:00
		final ItineraryData itineraryData2 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
		{ flight2 }), fcoLTNRoute);
		final PricedItineraryData pricedItinerary2 = createPricedItinerary(2, OUT_REF_NUMBER, Boolean.TRUE, itineraryData2,
				Arrays.asList(new ItineraryPricingInfoData[]
				{ ecoItineraryPricingInfo }));
		pricedItineraries.add(pricedItinerary2);

		fareSelectionData.setPricedItineraries(pricedItineraries);
		return fareSelectionData;

	}

	private static PricedItineraryData createPricedItinerary(final int id, final int refNumber, final Boolean available,
			final ItineraryData itinerary, final List<ItineraryPricingInfoData> itineraryPricingInfos)
	{
		final PricedItineraryData pricedItinerary = new PricedItineraryData();
		pricedItinerary.setId(id);
		pricedItinerary.setOriginDestinationRefNumber(refNumber);
		pricedItinerary.setAvailable(available);

		pricedItinerary.setItinerary(itinerary);
		pricedItinerary.setItineraryPricingInfos(itineraryPricingInfos);
		return pricedItinerary;
	}

	private static ItineraryData createItinerary(final TripType tripType, final List<TransportOfferingData> transportOfferings,
			final TravelRouteData route)
	{
		final ItineraryData itinerary = new ItineraryData();
		itinerary.setTripType(tripType);
		itinerary.setRoute(route);

		final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<OriginDestinationOptionData>();
		final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
		originDestinationOption.setTransportOfferings(transportOfferings);
		originDestinationOptions.add(originDestinationOption);
		itinerary.setOriginDestinationOptions(originDestinationOptions);
		return itinerary;
	}

	protected static ItineraryPricingInfoData createItineraryPricingInfo(final List<TravelBundleTemplateData> bundles,
			final List<PTCFareBreakdownData> ptcFareBreakdowns, final Boolean available, final String bundleType)
	{
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		itineraryPricingInfo.setBundleTemplates(bundles);
		itineraryPricingInfo.setPtcFareBreakdownDatas(ptcFareBreakdowns);
		itineraryPricingInfo.setAvailable(available);
		itineraryPricingInfo.setBundleType(bundleType);
		return itineraryPricingInfo;
	}

	protected static PTCFareBreakdownData createPTCFareBreakdown(final List<String> fareBasisCodes,
			final PassengerTypeQuantityData ptq, final double basePrice, final double totalPrice, final List<FareInfoData> fareInfos)
	{
		final PTCFareBreakdownData ptcFareBreakdown = new PTCFareBreakdownData();
		ptcFareBreakdown.setFareBasisCodes(fareBasisCodes);
		ptcFareBreakdown.setPassengerTypeQuantity(ptq);
		ptcFareBreakdown.setFareInfos(fareInfos);
		return ptcFareBreakdown;
	}

	protected static PassengerTypeQuantityData createPTQ(final PassengerTypeData passengerType, final int quantity)
	{
		final PassengerTypeQuantityData ptq = new PassengerTypeQuantityData();
		ptq.setPassengerType(passengerType);
		ptq.setQuantity(quantity);
		return ptq;
	}

	protected static FareInfoData createFareInfo(final String fareBasisCode, final FareProductData fareProduct,
			final List<PassengerTypeData> passengerTypes, final TripType tripType)
	{
		final FareDetailsData fareDetails = new FareDetailsData();
		fareDetails.setFareBasisCode(fareBasisCode);
		fareDetails.setFareProduct(fareProduct);
		fareDetails.setPassengerTypes(passengerTypes);
		fareDetails.setTripType(tripType);
		final FareInfoData fareInfo = new FareInfoData();
		fareInfo.setFareDetails(Arrays.asList(new FareDetailsData[]
		{ fareDetails }));
		return fareInfo;
	}


	protected static TravelBundleTemplateData createTravelBundle(final String bundleType, final String id, final String name,
			final List<FareProductData> fareProducts, final List<TransportOfferingData> bundleTransportOfferings)
	{
		final TravelBundleTemplateData bundle = new TravelBundleTemplateData();
		bundle.setBundleType(bundleType);
		bundle.setId(id);
		bundle.setName(name);
		bundle.setFareProducts(fareProducts);
		bundle.setTransportOfferings(bundleTransportOfferings);
		return bundle;
	}

	protected static FareProductData createFareProduct(final String code, final double price)
	{
		final FareProductData fareProduct = new FareProductData();
		fareProduct.setCode(code);
		fareProduct.setFareBasisCode(code);
		fareProduct.setBookingClass("M");
		fareProduct.setPrice(createPriceData(price));
		return fareProduct;
	}

	protected static TransportOfferingData createTransportOffering(final String code, final String number,
			final Date departureDateTime, final Date arrivalDateTime, final TerminalData originTerminal,
			final TerminalData destinationTerminal, final TravelProviderData provider, final TravelSectorData sector,
			final Map<String, Integer> duration, final TransportVehicleData vehicle)
	{
		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setCode(code);
		transportOffering.setNumber(number);
		transportOffering.setDepartureTime(departureDateTime);
		transportOffering.setArrivalTime(arrivalDateTime);
		transportOffering.setOriginTerminal(originTerminal);
		transportOffering.setDestinationTerminal(destinationTerminal);
		transportOffering.setTravelProvider(provider);
		transportOffering.setSector(sector);
		transportOffering.setDuration(duration);
		transportOffering.setTransportVehicle(vehicle);
		return transportOffering;
	}

	protected static TravelSectorData createTravelSector(final String code, final TransportFacilityData origin,
			final TransportFacilityData destination, final List<TravelRouteData> routes)
	{
		final TravelSectorData sector = new TravelSectorData();
		sector.setCode(code);
		sector.setOrigin(origin);
		sector.setDestination(destination);
		sector.setRoutes(routes);
		return sector;
	}

	protected static TravelRouteData createTravelRoute(final String code, final TransportFacilityData origin,
			final TransportFacilityData destination)
	{
		final TravelRouteData route = new TravelRouteData();
		route.setCode(code);
		route.setOrigin(origin);
		route.setDestination(destination);
		return route;
	}

	protected static TerminalData createTerminal(final String code, final String name)
	{
		final TerminalData terminal = new TerminalData();
		terminal.setCode(code);
		terminal.setName(name);
		return terminal;
	}

	protected static TransportFacilityData createTransportFacility(final String code, final LocationData location,
			final String transportFacilityName)
	{
		final TransportFacilityData transportFacility = new TransportFacilityData();
		transportFacility.setCode(code);
		transportFacility.setLocation(location);
		transportFacility.setName(transportFacilityName);
		return transportFacility;
	}

	protected static LocationData createLocation(final String code, final String name)
	{
		final LocationData location = new LocationData();
		location.setCode(code);
		location.setName(name);
		return location;
	}

	protected static TransportVehicleData createTransportVehicle(final String code, final String name)
	{
		final TransportVehicleData transportVehicle = new TransportVehicleData();
		final TransportVehicleInfoData transportVehicleInfo = new TransportVehicleInfoData();
		transportVehicleInfo.setCode(code);
		transportVehicleInfo.setName(name);
		transportVehicle.setVehicleInfo(transportVehicleInfo);
		return transportVehicle;
	}

	private static Map<String, Integer> createDurationMap(final int days, final int hours, final int minutes)
	{
		final Map<String, Integer> durationMap = new HashMap<String, Integer>();
		durationMap.put("transport.offering.status.result.days", days);
		durationMap.put("transport.offering.status.result.hours", hours);
		durationMap.put("transport.offering.status.result.minutes", minutes);
		return durationMap;
	}

	private static Date parseDateTimeFromString(final String dateTimeString)
	{
		Date date = null;

		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm");

		try
		{
			date = formatter.parse(dateTimeString);
		}
		catch (final ParseException e)
		{
			LOG.error("Error parsing Date from follwing String value: " + dateTimeString);
		}

		return date;
	}

	/**
	 * Returns an AccommodationReservationData with mocked values
	 *
	 * @return an AccommodationReservationData with mocked values
	 */
	public static AccommodationAvailabilityResponseData createAccommodationAvailabilityResponseData()
	{
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData = new AccommodationAvailabilityResponseData();
		accommodationAvailabilityResponseData.setAccommodationReference(createAccommodationReference());
		accommodationAvailabilityResponseData.setRoomStays(createRoomStays());

		return accommodationAvailabilityResponseData;
	}

	protected static PropertyData createAccommodationReference()
	{
		final PropertyData propertyData = new PropertyData();

		propertyData.setAccommodationOfferingCode("PARK_PLAZA_HOTELS__AMP__RESORTS_PARK_PLAZA_SHERLOCK_HOLMES_LONDON");
		propertyData.setAccommodationOfferingName("Park Plaza Sherlock Holmes London");
		propertyData.setAddress(createAddressData());
		propertyData.setAwards(createAwardData());
		propertyData.setImages(createImageList());


		final PositionData position = createPositionData();
		propertyData.setRelativePosition(position);
		propertyData.setPosition(position);

		propertyData.setChainCode("PARK_PLAZA_HOTELS__AMP__RESORTS");
		propertyData.setChainName("Park Plaza Hotels &amp; Resorts");

		propertyData.setLocation(createPropertyLocation());
		propertyData.setPromoted(Boolean.FALSE);

		return propertyData;
	}

	protected static AddressData createAddressData()
	{
		final AddressData addressData = new AddressData();
		addressData.setFormattedAddress("Baker Street, 108, London, W1U 6LJ, United Kingdom");
		return addressData;
	}

	protected static List<ImageData> createImageList()
	{
		final ImageData imageData = new ImageData();
		imageData.setImageType(ImageDataType.PRIMARY);
		return Arrays.asList(imageData);
	}

	protected static List<AwardData> createAwardData()
	{
		final AwardData awardDataSR = new AwardData();
		awardDataSR.setType(AwardType.STAR_RATING);
		awardDataSR.setRating(Double.valueOf(4));

		final AwardData awardDataUR = new AwardData();
		awardDataUR.setType(AwardType.USER_RATING);
		awardDataUR.setRating(Double.valueOf(8.0));

		return Arrays.asList(awardDataSR, awardDataUR);
	}

	protected static PositionData createPositionData()
	{
		final PositionData position = new PositionData();
		position.setLatitude(Double.valueOf(51.5208982));
		position.setLongitude(Double.valueOf(-0.15681468));
		return position;
	}

	protected static LocationData createPropertyLocation()
	{
		final LocationData locationData = new LocationData();
		locationData.setCode("PARK_PLAZA_HOTELS__AMP__RESORTS_PARK_PLAZA_SHERLOCK_HOLMES_LONDON");
		return locationData;
	}

	protected static List<RoomStayData> createRoomStays()
	{
		final ReservedRoomStayData reservedRoomStays = new ReservedRoomStayData();

		reservedRoomStays.setRoomStayRefNumber(Integer.valueOf(0));
		reservedRoomStays.setRoomTypes(createRoomTypesList());
		reservedRoomStays.setGuestCounts(createGuestCountsList());
		reservedRoomStays
				.setCheckInDate(TravelDateUtils.convertStringDateToDate("01/10/2016", TravelservicesConstants.DATE_PATTERN));
		reservedRoomStays
				.setCheckOutDate(TravelDateUtils.convertStringDateToDate("03/10/2016", TravelservicesConstants.DATE_PATTERN));

		return Arrays.asList(reservedRoomStays);
	}

	protected static List<RoomTypeData> createRoomTypesList()
	{
		final RoomTypeData roomTypeData = new RoomTypeData();

		roomTypeData.setCode("PARK_PLAZA_HOTELS__AMP__RESORTS_PARK_PLAZA_SHERLOCK_HOLMES_LONDONSUPERIOR_DOUBLE_ROOM");
		roomTypeData.setName("Superior Double Room");
		roomTypeData.setSizeMeasurement("35 m2 (376 sq. ft.)");
		roomTypeData.setOccupancies(createOccupanciesList());

		return Arrays.asList(roomTypeData);
	}

	protected static List<GuestOccupancyData> createOccupanciesList()
	{

		final GuestOccupancyData guestOccupancyAD = new GuestOccupancyData();
		guestOccupancyAD.setCode("adult");
		guestOccupancyAD.setQuantityMax(Integer.valueOf(2));
		guestOccupancyAD.setQuantityMin(Integer.valueOf(1));
		guestOccupancyAD.setPassengerType(createPassengerTypeData("adult"));

		final GuestOccupancyData guestOccupancyCH = new GuestOccupancyData();
		guestOccupancyCH.setCode("child");
		guestOccupancyCH.setQuantityMax(Integer.valueOf(1));
		guestOccupancyCH.setQuantityMin(Integer.valueOf(0));
		guestOccupancyCH.setPassengerType(createPassengerTypeData("child"));

		final GuestOccupancyData guestOccupancyIN = new GuestOccupancyData();
		guestOccupancyIN.setCode("infant");
		guestOccupancyIN.setQuantityMax(Integer.valueOf(0));
		guestOccupancyIN.setQuantityMin(Integer.valueOf(0));
		guestOccupancyIN.setPassengerType(createPassengerTypeData("infant"));

		return Arrays.asList(guestOccupancyAD, guestOccupancyCH, guestOccupancyIN);
	}

	protected static RateData createRateData(final double actualValue, final double wasValue)
	{
		final RateData rate = new RateData();
		rate.setTotalDiscount(createPriceData(wasValue - actualValue));
		rate.setActualRate(createPriceData(actualValue));
		rate.setWasRate(createPriceData(wasValue));
		return rate;
	}

	protected static TimeSpanData createTimeSpan()
	{
		final TimeSpanData timeSpan = new TimeSpanData();
		timeSpan.setStartDate(TravelDateUtils.convertStringDateToDate("01/12/2016", TravelservicesConstants.DATE_PATTERN));
		timeSpan.setEndDate(TravelDateUtils.convertStringDateToDate("03/12/2016", TravelservicesConstants.DATE_PATTERN));
		return timeSpan;
	}

	protected static List<PassengerTypeQuantityData> createGuestCountsList()
	{
		final PassengerTypeQuantityData passengerTypeQuantity = new PassengerTypeQuantityData();
		passengerTypeQuantity.setPassengerType(createPassengerTypeData("adult"));
		passengerTypeQuantity.setQuantity(2);
		return Arrays.asList(passengerTypeQuantity);
	}

	protected static PassengerTypeData createPassengerTypeData(final String passengerTypeCode)
	{
		final PassengerTypeData passengerType = new PassengerTypeData();

		if (passengerTypeCode.equals("adult"))
		{
			passengerType.setCode("adult");
			passengerType.setName("Adult");
			passengerType.setMinAge(Integer.valueOf(16));
		}
		if (passengerTypeCode.equals("child"))
		{
			passengerType.setCode("child");
			passengerType.setName("Child");
			passengerType.setMaxAge(Integer.valueOf(16));
			passengerType.setMinAge(Integer.valueOf(0));
		}
		if (passengerTypeCode.equals("infant"))
		{
			passengerType.setCode("infant");
			passengerType.setName("Infant");
			passengerType.setMaxAge(Integer.valueOf(2));
			passengerType.setMinAge(Integer.valueOf(0));
		}

		return passengerType;
	}

	protected static PriceData createPriceData(final double value)
	{
		final PriceData priceData = new PriceData();
		priceData.setCurrencyIso("GBP");
		final BigDecimal valueData = BigDecimal.valueOf(value);
		valueData.setScale(2, RoundingMode.HALF_UP);
		priceData.setValue(valueData);
		priceData.setFormattedValue("£" + valueData);
		priceData.setPriceType(PriceDataType.BUY);
		return priceData;
	}

	public static void mockPackagesResponsePricePopulation(final PackagesResponseData packagesResponseData)
	{
		packagesResponseData.getPackageResponses().forEach(packageResponseData -> {
			final RateData rateData = new RateData();
			rateData.setActualRate(createPriceData(400d));
			rateData.setWasRate(createPriceData(500d));
			rateData.setTotalDiscount(createPriceData(100d));
			packageResponseData.setPrice(rateData);
		});
	}
}
