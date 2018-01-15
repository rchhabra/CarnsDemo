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

package de.hybris.platform.ndcfacades.flight.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.NDCOfferItemIdBundle;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.CurrCode;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ.DataLists;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ.DataLists.PassengerList;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRS;
import de.hybris.platform.ndcfacades.ndc.FltPriceReqParamsType;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.MessageParamsBaseType;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCPassengerTypeService;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.TravelRouteFacade;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.services.CabinClassService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFlightPriceFacadeTest
{
	@InjectMocks
	DefaultFlightPriceFacade defaultFlightPriceFacade;

	@Mock
	private BundleTemplateService bundleTemplateService;

	@Mock
	private ProductService productService;

	@Mock
	private CabinClassService cabinClassService;

	@Mock
	private TravelRouteFacade travelRouteFacade;

	@Mock
	private StoreSessionFacade storeSessionFacade;

	@Mock
	private NDCTransportOfferingService ndcTransportOfferingService;

	@Mock
	private Converter<FlightPriceRQ, FlightPriceRS> ndcFlightPriceRSConverter;

	@Mock
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private TransportFacilityFacade transportFacilityFacade;

	@Mock
	private NDCPassengerTypeService ndcPassengerTypeService;

	@Mock
	private TravelCommercePriceService travelCommercePriceService;

	@Mock
	private SessionService sessionService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	private final TestSetup testSetup = new TestSetup();

	@Before
	public void setUp() throws NDCOrderException
	{
		Mockito.when(ndcOfferItemIdResolver.ndcOfferItemIdToString(Matchers.any(NDCOfferItemId.class)))
				.thenReturn("TEST_NDC_OFFER_ITEM");

		Mockito.when(ndcOfferItemIdResolver.getNDCOfferItemIdFromString(Matchers.anyString()))
				.thenReturn(testSetup.createNDCOfferItemId());

		Mockito.when(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString()))
				.thenReturn(testSetup.createBundleTemplateModel(BundleType.ECONOMY));
		final Date currentDate = new Date();
		final TransportOfferingModel transportOfferingModelOutbound = testSetup.createTransportOffering("OutBound", currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = testSetup.createTransportOffering("InBound",
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));
		Mockito.when(productService.getProductForCode(Matchers.anyString()))
				.thenReturn(testSetup.createProductModel("PRIORITYCHECKIN", ProductType.ANCILLARY));
		Mockito.when(travelCommercePriceFacade.getPriceInformationByProductPriceBundleRule(Matchers.any(BundleTemplateModel.class),
				Matchers.anyString())).thenReturn(testSetup.createPriceInformation());

		Mockito.when(travelCommercePriceService.getProductTaxInformations(Matchers.any(ProductModel.class)))
				.thenReturn(testSetup.createTaxes());

		Mockito.when(storeSessionFacade.getCurrentCurrency()).thenReturn(testSetup.createCurrencyData("GBP"));
		Mockito.doNothing().when(sessionService).setAttribute(Matchers.anyString(), Matchers.anyObject());
		final CabinClassModel cabinClassModel = new CabinClassModel();
		cabinClassModel.setCode("TEST_CABIN_CLASS");
		Mockito.when(cabinClassService.findCabinClassFromBundleTemplate(Matchers.anyString())).thenReturn(cabinClassModel);

		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(NdcfacadesConstants.OWNER)).thenReturn("TEST_OWNER");
	}

	@Test
	public void testRetrieveFlightPrice() throws NDCOrderException
	{
		final FlightPriceRS flightPriceRS = new FlightPriceRS();
		Mockito.when(ndcFlightPriceRSConverter.convert(Matchers.any(FlightPriceRQ.class), Matchers.any(FlightPriceRS.class)))
				.thenReturn(flightPriceRS);
		Assert.assertNotNull(defaultFlightPriceFacade.retrieveFlightPrice(testSetup.createFlightPriceRQ()));
	}

	private class TestSetup
	{
		public CurrencyData createCurrencyData(final String isoCode)
		{
			final CurrencyData currency = new CurrencyData();
			currency.setIsocode(isoCode);
			return currency;
		}

		private void createPassengerList(final FlightPriceRQ flightPriceRQ)
		{
			final DataLists dataLists = new DataLists();
			final PassengerList passengerList = new PassengerList();

			final PassengerType passengerType = new PassengerType();
			passengerType.setPTC("TEST_PTC_VALUE");

			passengerList.getPassenger().add(passengerType);
			flightPriceRQ.setDataLists(dataLists);
			dataLists.setPassengerList(passengerList);
			Mockito.when(ndcPassengerTypeService.getPassengerType("TEST_PTC_VALUE")).thenReturn(createPassengerTypeModel());
		}

		private PriceInformation createPriceInformation()
		{
			final PriceValue priceValue = new PriceValue("GBP", 10.00d, false);
			final PriceInformation priceInfo = new PriceInformation(priceValue);
			return priceInfo;
		}

		public FlightPriceRQ createFlightPriceRQ()
		{
			final FlightPriceRQ flightPriceRQ = new FlightPriceRQ();
			flightPriceRQ.setParameters(createFltPriceReqParamsType());
			flightPriceRQ.setQuery(createQuery());
			createPassengerList(flightPriceRQ);
			return flightPriceRQ;
		}

		private FltPriceReqParamsType createFltPriceReqParamsType()
		{
			final FltPriceReqParamsType fltPriceReqParamsType = new FltPriceReqParamsType();
			fltPriceReqParamsType.setCurrCodes(createCurrCodes());
			return fltPriceReqParamsType;
		}

		private MessageParamsBaseType.CurrCodes createCurrCodes()
		{
			final MessageParamsBaseType.CurrCodes currencyCodes = new MessageParamsBaseType.CurrCodes();
			return currencyCodes;
		}

		private CurrCode createCurrCode()
		{
			final CurrCode currCode = new CurrCode();
			currCode.setId("TEST_CURR_CODE");
			currCode.setValue("TEST_CURR_CODE");
			return currCode;
		}

		private FlightPriceRQ.Query createQuery()
		{
			final FlightPriceRQ.Query query = new FlightPriceRQ.Query();
			query.setOffers(createOffers());

			return query;
		}

		private FlightPriceRQ.Query.Offers createOffers()
		{
			final FlightPriceRQ.Query.Offers offers = new FlightPriceRQ.Query.Offers();
			offers.getOffer().add(createOffer());
			return offers;
		}

		private FlightPriceRQ.Query.Offers.Offer createOffer()
		{
			final FlightPriceRQ.Query.Offers.Offer offer = new FlightPriceRQ.Query.Offers.Offer();
			offer.setOfferID(createItemIDType("TEST_ITEM_ID_TYPE"));
			offer.setOfferItemIDs(createOfferItemIds());
			return offer;
		}

		private ItemIDType createItemIDType(final String value)
		{
			final ItemIDType itemIDType = new ItemIDType();
			itemIDType.setValue(value);
			return itemIDType;
		}

		private FlightPriceRQ.Query.Offers.Offer.OfferItemIDs createOfferItemIds()
		{
			final FlightPriceRQ.Query.Offers.Offer.OfferItemIDs offerItemIds = new FlightPriceRQ.Query.Offers.Offer.OfferItemIDs();
			offerItemIds.getOfferItemID().add(createItemIDType("TEST_ITEM_ID_TYPE"));
			return offerItemIds;

		}

		private NDCOfferItemId createNDCOfferItemId()
		{
			final NDCOfferItemId nDCOfferItemId = new NDCOfferItemId();
			nDCOfferItemId.setRouteCode("Test_Origin_Code_Test_Destination_Code");
			nDCOfferItemId.setBundleList(Collections.singletonList(createNDCOfferItemIdBundle()));
			nDCOfferItemId.setPtc("TEST_PTC_VALUE");
			nDCOfferItemId.setOriginDestinationRefNumber(0);
			return nDCOfferItemId;
		}

		private NDCOfferItemIdBundle createNDCOfferItemIdBundle()
		{
			final NDCOfferItemIdBundle nDCOfferItemIdBundle = new NDCOfferItemIdBundle();
			nDCOfferItemIdBundle.setFareProduct("product");
			nDCOfferItemIdBundle.setTransportOfferings(Arrays.asList("OutBound"));
			return nDCOfferItemIdBundle;
		}

		private BundleTemplateModel createBundleTemplateModel(final BundleType bundleType)
		{

			final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
			bundleTemplateModel.setType(bundleType);
			bundleTemplateModel.setName(bundleType.getCode());

			final BundleTemplateModel fareBundleTemplateModel = new BundleTemplateModel();
			final BundleTemplateModel ancillaryBundleTemplateModel = new BundleTemplateModel();

			fareBundleTemplateModel.setProducts(Collections.singletonList(createProductModel("outbound", ProductType
					.FARE_PRODUCT)));
			ancillaryBundleTemplateModel
					.setProducts(Collections.singletonList(createProductModel("PRIORITYCHECKIN", ProductType.ANCILLARY)));

			bundleTemplateModel.setChildTemplates(Collections.singletonList(fareBundleTemplateModel));

			return bundleTemplateModel;

		}

		private ProductModel createProductModel(final String code, final ProductType productType)
		{
			final ProductModel product = new ProductModel();
			product.setCode(code);
			product.setProductType(productType);
			product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
			return product;
		}

		private TransportOfferingModel createTransportOffering(final String code, final Date departureTime, final Date arrivalTime)
		{
			final TransportOfferingModel transportOffering = new TransportOfferingModel()
			{
				@Override
				public Long getDuration()
				{
					return arrivalTime.getTime() - departureTime.getTime();
				}

			};
			transportOffering.setNumber("00001");
			transportOffering.setDepartureTime(departureTime);
			transportOffering.setArrivalTime(arrivalTime);
			transportOffering.setCode(code);
			transportOffering.setTravelSector(createTravelSector("TEST_TRAVEL_SECTOR"));
			transportOffering.setTravelProvider(createTavelProviderModel());
			final VendorModel vendor = new VendorModel();
			vendor.setCode("vendorCode");
			transportOffering.setVendor(vendor);
			Mockito.when(ndcTransportOfferingService.getTransportOffering(code)).thenReturn(transportOffering);
			Mockito.when(ndcTransportOfferingService.isValidDate(transportOffering)).thenReturn(Boolean.TRUE);
			return transportOffering;
		}

		private TravelProviderModel createTavelProviderModel()
		{
			final TravelProviderModel travelProvider = new TravelProviderModel();
			travelProvider.setCode("TEST_TRAVEL_PROVIDER");
			return travelProvider;
		}

		private TravelSectorModel createTravelSector(final String code)
		{
			final TravelSectorModel sector = new TravelSectorModel();
			sector.setCode(code);
			final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
			final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);
			final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
			final TransportFacilityModel destination = createTransportFacilityModel("Test_Destination_Code", destinationlocation);
			final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

			sector.setTravelRoute(Collections.singletonList(travelRoute));
			sector.setOrigin(origin);
			sector.setDestination(destination);
			return sector;
		}

		private TransportFacilityModel createTransportFacilityModel(final String code, final LocationModel locationModel)
		{
			final TransportFacilityModel transportFacilityModel = new TransportFacilityModel()
			{
				@Override
				public String getName(final Locale loc)
				{
					return code;
				}

				@Override
				public String getName()
				{
					return code;
				}
			};
			transportFacilityModel.setCode(code);
			transportFacilityModel.setLocation(locationModel);
			Mockito.when(transportFacilityFacade.getCountry(code)).thenReturn(createLocationData(code));
			return transportFacilityModel;
		}

		private TransportFacilityData createTransportFacilityData(final String code, final LocationData location)
		{
			final TransportFacilityData transportFacility = new TransportFacilityData();
			transportFacility.setCode(code);
			transportFacility.setLocation(location);
			return transportFacility;
		}

		private TravelRouteModel createTravelRoute(final TransportFacilityModel origin, final TransportFacilityModel destination)
		{
			final TravelRouteModel travelRoute = new TravelRouteModel();
			travelRoute.setCode(origin.getCode() + "_" + destination.getCode());
			travelRoute.setOrigin(origin);
			travelRoute.setDestination(destination);

			final TransportFacilityData originData = createTransportFacilityData(origin.getCode(),
					createLocationData(origin.getCode()));
			final TransportFacilityData destinationData = createTransportFacilityData(destination.getCode(),
					createLocationData(destination.getCode()));
			Mockito.when(travelRouteFacade.getTravelRoute(origin.getCode() + "_" + destination.getCode()))
					.thenReturn(createTravelRouteData(origin.getCode() + "_" + destination.getCode(), originData, destinationData));
			return travelRoute;
		}

		private TravelRouteData createTravelRouteData(final String code, final TransportFacilityData origin,
				final TransportFacilityData destination)
		{
			final TravelRouteData travelRoute = new TravelRouteData();
			travelRoute.setCode(code);
			travelRoute.setName(code);
			travelRoute.setOrigin(origin);
			travelRoute.setDestination(destination);
			return travelRoute;
		}

		private LocationModel createLocationModel(final String code)
		{
			final LocationModel location = new LocationModel();
			location.setCode(code);
			return location;
		}

		private LocationData createLocationData(final String code)
		{
			final LocationData london = new LocationData();
			london.setCode(code);
			return london;
		}

		private PassengerTypeModel createPassengerTypeModel()
		{
			final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
			passengerTypeModel.setCode("adult");
			passengerTypeModel.setMinAge(16);
			return passengerTypeModel;
		}

		public List<TaxInformation> createTaxes()
		{
			final List<TaxInformation> taxInformations = new ArrayList<>();
			taxInformations.add(new TaxInformation(new TaxValue("APD", new Double("5.00"), false, "GB")));
			taxInformations.add(new TaxInformation(new TaxValue("VAT", new Double("10.00"), true, "GB")));
			return taxInformations;
		}

	}

}
