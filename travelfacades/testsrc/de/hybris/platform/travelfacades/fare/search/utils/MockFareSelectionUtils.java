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

package de.hybris.platform.travelfacades.fare.search.utils;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.DiscountData;
import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.FeeData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelProviderData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
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
 * MOCK FARE SELECTION DATA OBJECT SETUP
 */
public class MockFareSelectionUtils
{
	private static final Logger LOG = Logger.getLogger(MockFareSelectionUtils.class);

	private static final int SAME_DAY_RETURN = 1;
	private static final int OUT_REF_NUMBER = 0;
	private static final int RET_REF_NUMBER = 1;
	private static final String ECONOMY_BUNDLE = "ECONOMY";
	private static final String ECONOMY_PLUS_BUNDLE = "ECONOMY PLUS";
	private static final String BUSINESS_BUNDLE = "BUSINESS";
	private static final String FARE_BASIS_CODE = "Y1234";

	private MockFareSelectionUtils()
	{
		//empty to avoid instantiating utils class
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
	public static FareSelectionData prepareMockFareSelectionData(final TripType tripType, final PriceDataFactory priceDataFactory,
			final CommonI18NService commonI18NService, final int option)
	{
		final FareSelectionData fareSelectionData = new FareSelectionData();
		final List<PricedItineraryData> pricedItineraries = new ArrayList<PricedItineraryData>();

		/* TRAVEL PROVIDER */
		final TravelProviderData portaltechTravelProvider = new TravelProviderData();
		portaltechTravelProvider.setCode("PTRAIR");

		/* TRANSPORT VEHICLE */
		final TransportVehicleData boeing747 = createTransportVehicle("B747", "Boeing 747");

		/* LOCATIONS */
		final LocationData london = createLocation("London");
		final LocationData dubai = createLocation("Dubai");
		final LocationData munich = createLocation("Munich");

		/* TRANSPORT FACILITIES */
		final TransportFacilityData lhrAirport = createTransportFacility("LHR", london);
		final TransportFacilityData dxbAirport = createTransportFacility("DXB", dubai);
		final TransportFacilityData mucAirport = createTransportFacility("MUC", munich);

		/* TERMINALS */
		final TerminalData lhrTerminal1 = createTerminal("LHR1", "Heathrow Terminal 1");
		final TerminalData dxbTerminal1 = createTerminal("DXB1", "Dubai Terminal 1");
		final TerminalData mucTerminal1 = createTerminal("LHR1", "Munich Terminal 1");

		/* ROUTES */
		// LHR -> DXB
		final TravelRouteData lhrDXBRoute = createTravelRoute("lhrDXB", lhrAirport, dxbAirport);

		// LHR -> MUC -> DXB
		final TravelRouteData lhrMUCdxbRoute = createTravelRoute("lhrMUCdxb", lhrAirport, dxbAirport);

		// DXB -> LHR
		final TravelRouteData dxbLHRRoute = createTravelRoute("dxbLHR", dxbAirport, lhrAirport);

		// DXB -> MUC -> LHR
		final TravelRouteData dxbMUClhrRoute = createTravelRoute("dxbMUClhr", dxbAirport, lhrAirport);

		/* SECTORS */
		// LHR -> DXB
		final TravelSectorData lhrDXBSector = createTravelSector("lhrDXB", lhrAirport, dxbAirport,
				Arrays.asList(new TravelRouteData[]
		{ lhrDXBRoute }));

		// LHR -> MUC
		final TravelSectorData lhrMUCSector = createTravelSector("lhrMUC", lhrAirport, mucAirport,
				Arrays.asList(new TravelRouteData[]
		{ lhrMUCdxbRoute }));

		// MUC -> DXB
		final TravelSectorData mucDXBSector = createTravelSector("mucDXB", mucAirport, dxbAirport,
				Arrays.asList(new TravelRouteData[]
		{ lhrMUCdxbRoute }));

		// DXB -> LHR
		final TravelSectorData dxbLHRSector = createTravelSector("dxbLHR", dxbAirport, lhrAirport,
				Arrays.asList(new TravelRouteData[]
		{ dxbLHRRoute }));

		// DXB -> MUC
		final TravelSectorData dxbMUCSector = createTravelSector("dxbMUC", dxbAirport, mucAirport,
				Arrays.asList(new TravelRouteData[]
		{ dxbMUClhrRoute }));

		// MUC -> LHR
		final TravelSectorData mucLHRSector = createTravelSector("mucLHR", mucAirport, lhrAirport,
				Arrays.asList(new TravelRouteData[]
		{ dxbMUClhrRoute }));

		/* Bind sectors to routes */
		lhrDXBRoute.setSectors(Arrays.asList(new TravelSectorData[]
		{ lhrDXBSector }));

		lhrMUCdxbRoute.setSectors(Arrays.asList(new TravelSectorData[]
		{ lhrMUCSector, mucDXBSector }));

		dxbLHRRoute.setSectors(Arrays.asList(new TravelSectorData[]
		{ dxbLHRSector }));

		dxbMUClhrRoute.setSectors(Arrays.asList(new TravelSectorData[]
		{ dxbMUCSector, mucLHRSector }));

		/* TRANSPORT OFFERINGS */
		// FLIGHT 1 - LHR-DXB 01/01/2016 07:00-15:00
		final TransportOfferingData flight1 = createTransportOffering("EZY8323201512070810", "EZY8323",
				parseDateTimeFromString("2016-01-01 07:00"), parseDateTimeFromString("2016-01-01 15:00"), lhrTerminal1, dxbTerminal1,
				portaltechTravelProvider, lhrDXBSector, createDurationMap(0, 8, 0), boeing747);

		// FLIGHT 2 - LHR-MUC 01/01/2016 07:30-09:00
		final TransportOfferingData flight2 = createTransportOffering("EZY8325201512071355", "EZY8325",
				parseDateTimeFromString("2016-01-01 07:30"), parseDateTimeFromString("2016-01-01 09:00"), lhrTerminal1, mucTerminal1,
				portaltechTravelProvider, lhrMUCSector, createDurationMap(0, 1, 30), boeing747);

		// FLIGHT 3 - MUC-DXB 01/01/2016 10:00-17:00
		final TransportOfferingData flight3 = createTransportOffering("EZY8327201512071645", "EZY8327",
				parseDateTimeFromString("2016-01-01 10:00"), parseDateTimeFromString("2016-01-01 17:00"), mucTerminal1, dxbTerminal1,
				portaltechTravelProvider, mucDXBSector, createDurationMap(0, 7, 0), boeing747);

		// FLIGHT 4 - LHR-DXB 01/01/2016 12:00-22:00 (refueling in MUC
		// 14:00-15:30)
		final TransportOfferingData flight4 = createTransportOffering("EZY2431201512070710", "EZY2431",
				parseDateTimeFromString("2016-01-01 12:00"), parseDateTimeFromString("2016-01-01 22:00"), lhrTerminal1, dxbTerminal1,
				portaltechTravelProvider, lhrDXBSector, createDurationMap(0, 10, 0), boeing747);
		flight4.setStopLocations(Arrays.asList(new TransportFacilityData[]
		{ mucAirport }));

		// FLIGHT 5 - DXB-LHR 01/01/2016 16:00-21:00
		final TransportOfferingData flight5 = createTransportOffering("EZY2345201512071120", "EZY2435",
				parseDateTimeFromString("2016-01-01 16:00"), parseDateTimeFromString("2016-01-01 21:00"), dxbTerminal1, lhrTerminal1,
				portaltechTravelProvider, dxbLHRSector, createDurationMap(0, 5, 0), boeing747);

		// FLIGHT 6 - DXB-LHR 01/01/2016 18:00-23:30
		final TransportOfferingData flight6 = createTransportOffering("EZY2441201512071840", "EZY2441",
				parseDateTimeFromString("2016-01-01 18:00"), parseDateTimeFromString("2016-01-01 23:30"), dxbTerminal1, lhrTerminal1,
				portaltechTravelProvider, dxbLHRSector, createDurationMap(0, 5, 30), boeing747);

		// FLIGHT 7 - DXB-LHR 02/01/2016 08:00-15:00
		final TransportOfferingData flight7 = createTransportOffering("EZY8322201512140730", "EZY8322",
				parseDateTimeFromString("2016-01-02 08:00"), parseDateTimeFromString("2016-01-02 15:00"), dxbTerminal1, lhrTerminal1,
				portaltechTravelProvider, dxbLHRSector, createDurationMap(0, 7, 0), boeing747);

		// FLIGHT 8 - DXB-MUC 02/01/2016 10:00-16:00
		final TransportOfferingData flight8 = createTransportOffering("EZY8324201512141640", "EZY8324",
				parseDateTimeFromString("2016-01-02 10:00"), parseDateTimeFromString("2016-01-02 16:00"), dxbTerminal1, mucTerminal1,
				portaltechTravelProvider, dxbMUCSector, createDurationMap(0, 6, 0), boeing747);

		// FLIGHT 9 - MUC-LHR 02/01/2016 18:00-19:30
		final TransportOfferingData flight9 = createTransportOffering("EZY8326201512141930", "EZY8326",
				parseDateTimeFromString("2016-01-02 18:00"), parseDateTimeFromString("2016-01-02 19:30"), mucTerminal1, lhrTerminal1,
				portaltechTravelProvider, mucLHRSector, createDurationMap(0, 1, 30), boeing747);

		/* Bind Transport Offerings to sectors */
		// lhrDXBSector lhrMUCSector, mucDXBSector dxbLHRSector dxbMUCSector,
		// mucLHRSector
		lhrDXBSector.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ flight1, flight4 }));
		lhrMUCSector.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ flight2 }));
		mucDXBSector.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ flight3 }));
		dxbLHRSector.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ flight5, flight6, flight7 }));
		dxbMUCSector.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ flight8 }));
		mucLHRSector.setTransportOfferings(Arrays.asList(new TransportOfferingData[]
		{ flight9 }));

		/* FARE PRODUCTS */
		final FareProductData fp1 = createFareProduct("FPJCAT1", 10d, priceDataFactory, commonI18NService);
		final FareProductData fp2 = createFareProduct("FPDCAT1", 15d, priceDataFactory, commonI18NService);
		final FareProductData fp3 = createFareProduct("FPCCAT1", 25d, priceDataFactory, commonI18NService);
		final FareProductData fp4 = createFareProduct("FPRCAT1", 50d, priceDataFactory, commonI18NService);
		final FareProductData fp5 = createFareProduct("FPICAT4", 60d, priceDataFactory, commonI18NService);
		final FareProductData fp6 = createFareProduct("FPICAT3", 70d, priceDataFactory, commonI18NService);
		final FareProductData fp7 = createFareProduct("FPICAT2", 100d, priceDataFactory, commonI18NService);
		final FareProductData fp8 = createFareProduct("FPICAT1", 120d, priceDataFactory, commonI18NService);
		final FareProductData fp9 = createFareProduct("FPWCAT1", 140d, priceDataFactory, commonI18NService);

		/* ANCILLARY PRODUCTS (INCLUDED IN BUNDLES) */
		final ProductData priorityAncillary = createAncillaryProduct("priority", "Priority Boarding", 5d, priceDataFactory,
				commonI18NService);

		final ProductData skisAncillary = createAncillaryProduct("skis", "Skis", 5d, priceDataFactory, commonI18NService);

		/* BUNDLES */
		final List<TransportOfferingData> transportOfferingDatas = new ArrayList<TransportOfferingData>();
		transportOfferingDatas.addAll(Arrays.asList(new TransportOfferingData[]
		{ flight1, flight2, flight3, flight4, flight5, flight6, flight7, flight8, flight9 }));

		final List<FareProductData> economyFareProducts = new ArrayList<FareProductData>();
		economyFareProducts.addAll(Arrays.asList(new FareProductData[]
		{ fp1, fp2, fp3 }));

		final TravelBundleTemplateData economyBundle = createTravelBundle(ECONOMY_BUNDLE, "ecoBundle", ECONOMY_BUNDLE,
				economyFareProducts, transportOfferingDatas);

		final TravelBundleTemplateData economyEuropeBundle = createTravelBundle(ECONOMY_BUNDLE, "ecoEuropeBundle", ECONOMY_BUNDLE,
				economyFareProducts, transportOfferingDatas);
		economyEuropeBundle.setProducts(Arrays.asList(new ProductData[]
		{ priorityAncillary, skisAncillary }));

		final List<FareProductData> economyPlusFareProducts = new ArrayList<FareProductData>();
		economyFareProducts.addAll(Arrays.asList(new FareProductData[]
		{ fp4, fp5, fp6 }));

		final TravelBundleTemplateData economyPlusBundle = createTravelBundle(ECONOMY_PLUS_BUNDLE, "ecoPlusBundle",
				ECONOMY_PLUS_BUNDLE, economyPlusFareProducts, transportOfferingDatas);
		economyPlusBundle.setProducts(Arrays.asList(new ProductData[]
		{ priorityAncillary, priorityAncillary, priorityAncillary }));

		final List<FareProductData> businessFareProducts = new ArrayList<FareProductData>();
		economyFareProducts.addAll(Arrays.asList(new FareProductData[]
		{ fp7, fp8, fp9 }));

		final TravelBundleTemplateData businessBundle = createTravelBundle(BUSINESS_BUNDLE, "businessBundle", "BUSINESS",
				businessFareProducts, transportOfferingDatas);
		businessBundle.setProducts(Arrays.asList(new ProductData[]
		{ priorityAncillary, priorityAncillary, priorityAncillary, skisAncillary, skisAncillary }));

		/* PASSENGER TYPES */
		final PassengerTypeData adultType = createPassengerType("AD", "ADULT");
		final PassengerTypeData childType = createPassengerType("CH", "CHILD");

		/* FARE INFO */
		// ECONOMY
		final FareInfoData ecoAdultInfo = createFareInfo(FARE_BASIS_CODE, fp1, Arrays.asList(new PassengerTypeData[]
		{ adultType }), tripType);

		final FareInfoData ecoChildInfo = createFareInfo(FARE_BASIS_CODE, fp1, Arrays.asList(new PassengerTypeData[]
		{ childType }), tripType);

		// ECONOMY PLUS
		final FareInfoData ecoPlusAdultInfo = createFareInfo(FARE_BASIS_CODE, fp4, Arrays.asList(new PassengerTypeData[]
		{ adultType }), tripType);

		final FareInfoData ecoPlusChildInfo = createFareInfo(FARE_BASIS_CODE, fp4, Arrays.asList(new PassengerTypeData[]
		{ childType }), tripType);

		// BUSINESS
		final FareInfoData businessAdultInfo = createFareInfo(FARE_BASIS_CODE, fp7, Arrays.asList(new PassengerTypeData[]
		{ adultType }), tripType);

		final FareInfoData businessChildInfo = createFareInfo(FARE_BASIS_CODE, fp7, Arrays.asList(new PassengerTypeData[]
		{ childType }), tripType);

		/* DISCOUNTS, TAXES, FEES */
		final DiscountData discount = new DiscountData();
		discount.setPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1d),
				commonI18NService.getCurrentCurrency().getIsocode()));
		discount.setPurpose("Christmas Sale");

		final TaxData tax = new TaxData();
		tax.setCode("taxCode");
		tax.setPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(2d),
				commonI18NService.getCurrentCurrency().getIsocode()));

		final FeeData fee = new FeeData();
		fee.setName("feeName");
		fee.setPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(2d),
				commonI18NService.getCurrentCurrency().getIsocode()));

		/* PTC BREAKDOWNS */

		final PassengerTypeQuantityData ptqAD = createPTQ(adultType, 2);
		final PassengerTypeQuantityData ptqCH = createPTQ(childType, 1);

		// ECONOMY Adults PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownAD1 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqAD, 20d, 26d, Arrays.asList(new FareInfoData[]
		{ ecoAdultInfo }), priceDataFactory, commonI18NService);

		ptcFareBreakdownAD1.getPassengerFare().setDiscounts(Arrays.asList(new DiscountData[]
		{ discount, discount }));
		ptcFareBreakdownAD1.getPassengerFare().setTaxes(Arrays.asList(new TaxData[]
		{ tax, tax }));
		ptcFareBreakdownAD1.getPassengerFare().setFees(Arrays.asList(new FeeData[]
		{ fee, fee }));

		// ECONOMY Child PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownCH1 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqCH, 10d, 12d, Arrays.asList(new FareInfoData[]
		{ ecoChildInfo }), priceDataFactory, commonI18NService);

		ptcFareBreakdownCH1.getPassengerFare().setTaxes(Arrays.asList(new TaxData[]
		{ tax }));

		// ECONOMY PLUS Adults PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownAD2 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqAD, 100d, 106d, Arrays.asList(new FareInfoData[]
		{ ecoPlusAdultInfo }), priceDataFactory, commonI18NService);

		ptcFareBreakdownAD2.getPassengerFare().setDiscounts(Arrays.asList(new DiscountData[]
		{ discount, discount }));
		ptcFareBreakdownAD2.getPassengerFare().setTaxes(Arrays.asList(new TaxData[]
		{ tax, tax }));
		ptcFareBreakdownAD2.getPassengerFare().setFees(Arrays.asList(new FeeData[]
		{ fee, fee }));

		// ECONOMY PLUS Child PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownCH2 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqCH, 50d, 52d, Arrays.asList(new FareInfoData[]
		{ ecoPlusChildInfo }), priceDataFactory, commonI18NService);

		ptcFareBreakdownCH2.getPassengerFare().setTaxes(Arrays.asList(new TaxData[]
		{ tax }));

		// BUSINESS Adults PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownAD3 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqAD, 200d, 206d, Arrays.asList(new FareInfoData[]
		{ businessAdultInfo }), priceDataFactory, commonI18NService);

		ptcFareBreakdownAD3.getPassengerFare().setDiscounts(Arrays.asList(new DiscountData[]
		{ discount, discount }));
		ptcFareBreakdownAD3.getPassengerFare().setTaxes(Arrays.asList(new TaxData[]
		{ tax, tax }));
		ptcFareBreakdownAD3.getPassengerFare().setFees(Arrays.asList(new FeeData[]
		{ fee, fee }));

		// BUSINESS Child PTC Breakdown
		final PTCFareBreakdownData ptcFareBreakdownCH3 = createPTCFareBreakdown(Arrays.asList(new String[]
		{ FARE_BASIS_CODE }), ptqCH, 100d, 102d, Arrays.asList(new FareInfoData[]
		{ businessChildInfo }), priceDataFactory, commonI18NService);

		ptcFareBreakdownCH3.getPassengerFare().setTaxes(Arrays.asList(new TaxData[]
		{ tax }));

		/* ITINERARY TOTAL FARES */

		final TotalFareData ecoItineraryTotalFare = new TotalFareData();
		ecoItineraryTotalFare.setBasePrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(30d),
				commonI18NService.getCurrentCurrency().getIsocode()));
		ecoItineraryTotalFare.setDiscounts(Arrays.asList(new DiscountData[]
		{ discount, discount }));
		ecoItineraryTotalFare.setTaxes(Arrays.asList(new TaxData[]
		{ tax, tax, tax }));
		ecoItineraryTotalFare.setFees(Arrays.asList(new FeeData[]
		{ fee, fee }));
		ecoItineraryTotalFare.setTotalPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(38d),
				commonI18NService.getCurrentCurrency().getIsocode()));

		final TotalFareData ecoPlusItineraryTotalFare = new TotalFareData();
		ecoPlusItineraryTotalFare.setBasePrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(150d),
				commonI18NService.getCurrentCurrency().getIsocode()));
		ecoPlusItineraryTotalFare.setDiscounts(Arrays.asList(new DiscountData[]
		{ discount, discount }));
		ecoPlusItineraryTotalFare.setTaxes(Arrays.asList(new TaxData[]
		{ tax, tax, tax }));
		ecoPlusItineraryTotalFare.setFees(Arrays.asList(new FeeData[]
		{ fee, fee }));
		ecoPlusItineraryTotalFare.setTotalPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(158d),
				commonI18NService.getCurrentCurrency().getIsocode()));

		final TotalFareData businessItineraryTotalFare = new TotalFareData();
		businessItineraryTotalFare.setBasePrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(300d),
				commonI18NService.getCurrentCurrency().getIsocode()));
		businessItineraryTotalFare.setDiscounts(Arrays.asList(new DiscountData[]
		{ discount, discount }));
		businessItineraryTotalFare.setTaxes(Arrays.asList(new TaxData[]
		{ tax, tax, tax }));
		businessItineraryTotalFare.setFees(Arrays.asList(new FeeData[]
		{ fee, fee }));
		businessItineraryTotalFare.setTotalPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(308d),
				commonI18NService.getCurrentCurrency().getIsocode()));

		/* ITINERARY PRICING INFOS */
		final ItineraryPricingInfoData ecoItineraryPricingInfo = createItineraryPricingInfo(
				Arrays.asList(new TravelBundleTemplateData[]
		{ economyBundle }), Arrays.asList(new PTCFareBreakdownData[]
		{ ptcFareBreakdownAD1, ptcFareBreakdownCH1 }), ecoItineraryTotalFare, Boolean.TRUE, ECONOMY_BUNDLE);

		final ItineraryPricingInfoData ecoPlusItineraryPricingInfo = createItineraryPricingInfo(
				Arrays.asList(new TravelBundleTemplateData[]
		{ economyPlusBundle }), Arrays.asList(new PTCFareBreakdownData[]
		{ ptcFareBreakdownAD2, ptcFareBreakdownCH2 }), ecoPlusItineraryTotalFare, Boolean.TRUE, ECONOMY_PLUS_BUNDLE);

		final ItineraryPricingInfoData businessItineraryPricingInfo = createItineraryPricingInfo(
				Arrays.asList(new TravelBundleTemplateData[]
		{ businessBundle }), Arrays.asList(new PTCFareBreakdownData[]
		{ ptcFareBreakdownAD3, ptcFareBreakdownCH3 }), businessItineraryTotalFare, Boolean.TRUE, BUSINESS_BUNDLE);

		/* PRICED ITINERARIES */

		// PRICED ITINERARY 1 - LHR-DXB 01/01/2016 07:00-15:00
		final ItineraryData itineraryData1 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
		{ flight1 }), lhrDXBRoute);
		final PricedItineraryData pricedItinerary1 = createPricedItinerary(1, OUT_REF_NUMBER, Boolean.TRUE, itineraryData1,
				Arrays.asList(new ItineraryPricingInfoData[]
		{ ecoItineraryPricingInfo, ecoPlusItineraryPricingInfo, businessItineraryPricingInfo }));
		pricedItineraries.add(pricedItinerary1);

		// PRICED ITINERARY 2 - LHR-MUC-DXB 01/01/2016 07:30-17:00 (break at MUC
		// 09:00 - 10:00)
		final ItineraryData itineraryData2 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
		{ flight2, flight3 }), lhrMUCdxbRoute);
		final PricedItineraryData pricedItinerary2 = createPricedItinerary(2, OUT_REF_NUMBER, Boolean.TRUE, itineraryData2,
				Arrays.asList(new ItineraryPricingInfoData[]
		{ ecoItineraryPricingInfo, ecoPlusItineraryPricingInfo, businessItineraryPricingInfo }));
		pricedItineraries.add(pricedItinerary2);

		// PRICED ITINERARY 3 - LHR-DXB 01/01/2016 12:00-22:00 (refueling in MUC
		// 2h)
		final ItineraryData itineraryData3 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
		{ flight4 }), lhrDXBRoute);

		final ItineraryPricingInfoData unavailableEcoPlusItineraryPricingInfo = createItineraryPricingInfo(
				Arrays.asList(new TravelBundleTemplateData[]
		{ economyPlusBundle }), null, null, Boolean.FALSE, ECONOMY_PLUS_BUNDLE);
		final ItineraryPricingInfoData unavailableBusinessItineraryPricingInfo = createItineraryPricingInfo(
				Arrays.asList(new TravelBundleTemplateData[]
		{ businessBundle }), null, null, Boolean.FALSE, BUSINESS_BUNDLE);

		final PricedItineraryData pricedItinerary3 = createPricedItinerary(3, OUT_REF_NUMBER, Boolean.TRUE, itineraryData3,
				Arrays.asList(new ItineraryPricingInfoData[]
		{ ecoItineraryPricingInfo, unavailableEcoPlusItineraryPricingInfo, unavailableBusinessItineraryPricingInfo }));
		pricedItineraries.add(pricedItinerary3);

		/* RETURN PRICED ITINERARIES */
		if (tripType.equals(TripType.RETURN))
		{
			if (option == SAME_DAY_RETURN)
			{
				// PRICED ITINERARY 4 - DXB-LHR 01/01/2016 16:00-21:00
				final ItineraryData itineraryData4 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
				{ flight5 }), dxbLHRRoute);
				final PricedItineraryData pricedItinerary4 = createPricedItinerary(4, RET_REF_NUMBER, Boolean.TRUE, itineraryData4,
						Arrays.asList(new ItineraryPricingInfoData[]
				{ ecoItineraryPricingInfo, ecoPlusItineraryPricingInfo, businessItineraryPricingInfo }));
				pricedItineraries.add(pricedItinerary4);

				// PRICED ITINERARY 5 - DXB-LHR 01/01/2016 18:00-23:30
				final ItineraryData itineraryData5 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
				{ flight6 }), dxbLHRRoute);
				final PricedItineraryData pricedItinerary5 = createPricedItinerary(5, RET_REF_NUMBER, Boolean.TRUE, itineraryData5,
						Arrays.asList(new ItineraryPricingInfoData[]
				{ ecoItineraryPricingInfo, ecoPlusItineraryPricingInfo, businessItineraryPricingInfo }));
				pricedItineraries.add(pricedItinerary5);
			}
			else
			{
				// PRICED ITINERARY 6 - DXB-LHR 02/01/2016 08:00-15:00
				final ItineraryData itineraryData6 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
				{ flight7 }), dxbLHRRoute);
				final PricedItineraryData pricedItinerary6 = createPricedItinerary(6, RET_REF_NUMBER, Boolean.TRUE, itineraryData6,
						Arrays.asList(new ItineraryPricingInfoData[]
				{ ecoItineraryPricingInfo, ecoPlusItineraryPricingInfo, businessItineraryPricingInfo }));
				pricedItineraries.add(pricedItinerary6);

				// PRICED ITINERARY 7 - DXB-MUC-LHR 02/01/2016 10:00-19:30
				// (break in MUC 16:00-18:00)
				final ItineraryData itineraryData7 = createItinerary(tripType, Arrays.asList(new TransportOfferingData[]
				{ flight8, flight9 }), dxbLHRRoute);
				final PricedItineraryData pricedItinerary7 = createPricedItinerary(7, RET_REF_NUMBER, Boolean.TRUE, itineraryData7,
						Arrays.asList(new ItineraryPricingInfoData[]
				{ ecoItineraryPricingInfo, ecoPlusItineraryPricingInfo, businessItineraryPricingInfo }));
				pricedItineraries.add(pricedItinerary7);
			}
		}
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
			final List<PTCFareBreakdownData> ptcFareBreakdowns, final TotalFareData itineraryTotalFare, final Boolean available,
			final String bundleType)
	{
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		itineraryPricingInfo.setBundleTemplates(bundles);
		itineraryPricingInfo.setPtcFareBreakdownDatas(ptcFareBreakdowns);
		itineraryPricingInfo.setTotalFare(itineraryTotalFare);
		itineraryPricingInfo.setAvailable(available);
		itineraryPricingInfo.setBundleType(bundleType);
		return itineraryPricingInfo;
	}

	protected static PTCFareBreakdownData createPTCFareBreakdown(final List<String> fareBasisCodes,
			final PassengerTypeQuantityData ptq, final double basePrice, final double totalPrice, final List<FareInfoData> fareInfos,
			final PriceDataFactory priceDataFactory, final CommonI18NService commonI18NService)
	{
		final PTCFareBreakdownData ptcFareBreakdown = new PTCFareBreakdownData();
		ptcFareBreakdown.setFareBasisCodes(fareBasisCodes);
		ptcFareBreakdown.setPassengerTypeQuantity(ptq);

		final PassengerFareData passengerFare = new PassengerFareData();
		passengerFare.setBaseFare(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(basePrice),
				commonI18NService.getCurrentCurrency().getIsocode()));
		passengerFare.setTotalFare(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(totalPrice),
				commonI18NService.getCurrentCurrency().getIsocode()));
		ptcFareBreakdown.setPassengerFare(passengerFare);

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

	protected static PassengerTypeData createPassengerType(final String code, final String name)
	{
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode(code);
		passengerType.setName(name);
		return passengerType;
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

	protected static FareProductData createFareProduct(final String code, final double price,
			final PriceDataFactory priceDataFactory, final CommonI18NService commonI18NService)
	{
		final FareProductData fareProduct = new FareProductData();
		fareProduct.setCode(code);
		fareProduct.setPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(price),
				commonI18NService.getCurrentCurrency().getIsocode()));
		return fareProduct;
	}

	protected static ProductData createAncillaryProduct(final String code, final String name, final double price,
			final PriceDataFactory priceDataFactory, final CommonI18NService commonI18NService)
	{
		final ProductData ancillaryProduct = new ProductData();
		ancillaryProduct.setCode(code);
		ancillaryProduct.setName(name);
		ancillaryProduct.setPrice(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(price),
				commonI18NService.getCurrentCurrency().getIsocode()));
		return ancillaryProduct;
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

	protected static TransportFacilityData createTransportFacility(final String code, final LocationData location)
	{
		final TransportFacilityData transportFacility = new TransportFacilityData();
		transportFacility.setCode(code);
		transportFacility.setLocation(location);
		return transportFacility;
	}

	protected static LocationData createLocation(final String code)
	{
		final LocationData location = new LocationData();
		location.setCode(code);
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
}
