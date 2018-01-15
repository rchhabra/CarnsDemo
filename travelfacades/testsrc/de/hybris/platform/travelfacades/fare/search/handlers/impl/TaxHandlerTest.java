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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
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
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
public class TaxHandlerTest
{
	@InjectMocks
	private final TaxHandler handler = new TaxHandler();

	@Mock
	private TransportFacilityFacade transportFacilityFacade;

	@Mock
	private SessionService sessionService;

	@Mock
	private ProductService productService;

	@Mock
	private TravelCommercePriceService travelCommercePriceService;

	@Mock
	private BaseStoreService baseStoreService;

	private FareSelectionData fareSelectionData;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Before
	public void setup()
	{
		fareSelectionData = TestDataSetup.createFareSelection();

		// transport facility facade mock
		final LocationData country = new LocationData();
		country.setCode("UK");
		given(transportFacilityFacade.getCountry("LTN")).willReturn(country);

		// travel commerce price service mock
		given(travelCommercePriceService.getProductTaxInformations(Mockito.any(ProductModel.class)))
				.willReturn(TestDataSetup.createTaxes());

		// product service mock
		final FareProductModel loungProduct = mock(FareProductModel.class);
		given(productService.getProductForCode("loungeaccess")).willReturn(loungProduct);

		final PriceData taxPriceData = new PriceData();
		taxPriceData.setValue(new BigDecimal("10.00"));

		given(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt(), Matchers.anyString()))
				.willReturn(taxPriceData);

		// base store service mock
		final BaseStoreModel netBaseStore = new BaseStoreModel();
		netBaseStore.setNet(true);

		final BaseStoreModel notNetBaseStore = new BaseStoreModel();
		notNetBaseStore.setNet(false);

		given(baseStoreService.getCurrentBaseStore()).willReturn(notNetBaseStore, netBaseStore);

		// session service mock
		Mockito.doNothing().when(sessionService).setAttribute(Mockito.anyString(), Mockito.anyMap());

	}

	@Test
	public void populateTaxesTest()
	{
		// run test
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getString(Matchers.anyString())).willReturn("adult");
		handler.handle(null, null, fareSelectionData);

		// access results
		final List<TaxData> taxes = fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0)
				.getPtcFareBreakdownDatas().get(0).getPassengerFare().getTaxes();

		Assert.assertTrue(CollectionUtils.isNotEmpty(taxes));
		Assert.assertEquals(2, CollectionUtils.size(taxes));

		Assert.assertEquals("APD", taxes.get(0).getCode());
		Assert.assertNotNull(taxes.get(0).getPrice());
		Assert.assertEquals(new BigDecimal("10.00"), taxes.get(0).getPrice().getValue());

		Assert.assertEquals("VAT", taxes.get(1).getCode());
		Assert.assertNotNull(taxes.get(1).getPrice());
		Assert.assertEquals(new BigDecimal("10.00"), taxes.get(1).getPrice().getValue());
	}

	/**
	 * Inner class to set-up test data
	 */
	private static class TestDataSetup
	{
		public static FareSelectionData createFareSelection()
		{
			final FareSelectionData fareSelection = new FareSelectionData();
			fareSelection.setPricedItineraries(createpricedItineraries());

			return fareSelection;
		}

		public static List<TaxInformation> createTaxes()
		{
			final List<TaxInformation> taxInformations = new ArrayList<>();
			taxInformations.add(new TaxInformation(new TaxValue("APD", new Double("5.00"), false, "GB")));
			taxInformations.add(new TaxInformation(new TaxValue("VAT", new Double("10.00"), true, "GB")));
			return taxInformations;
		}

		private static TransportFacilityData createTransportFacility(final String code, final LocationData location)
		{
			final TransportFacilityData transportFacility = new TransportFacilityData();
			transportFacility.setCode(code);
			transportFacility.setLocation(location);
			return transportFacility;
		}

		private static LocationData createLocationData(final String code)
		{
			final LocationData london = new LocationData();
			london.setCode(code);
			return london;
		}

		private static List<TransportOfferingData> createTransportOfferings(final String sectorCode)
		{
			final TravelSectorData sector = new TravelSectorData();
			sector.setCode(sectorCode);
			sector.setOrigin(createTransportFacility("LTN", createLocationData("LON")));
			sector.setDestination(createTransportFacility("CDG", createLocationData("PAR")));

			final TransportOfferingData transportOffering = new TransportOfferingData();
			transportOffering.setSector(sector);

			final List<TransportOfferingData> transportOfferings = new ArrayList<>();
			transportOfferings.add(transportOffering);

			return transportOfferings;
		}

		private static List<PricedItineraryData> createpricedItineraries()
		{
			final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
			originDestinationOption.setTransportOfferings(createTransportOfferings("LTN"));

			final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<>();
			originDestinationOptions.add(originDestinationOption);

			final ItineraryData itinerary = new ItineraryData();
			itinerary.setOriginDestinationOptions(originDestinationOptions);

			// available
			final PricedItineraryData availablePricedItinerary = new PricedItineraryData();
			availablePricedItinerary.setItinerary(itinerary);
			availablePricedItinerary.setItineraryPricingInfos(createItineraryPricingInfos());
			availablePricedItinerary.setAvailable(true);

			// unavailable
			final PricedItineraryData unavailablePricedItinerary = new PricedItineraryData();
			unavailablePricedItinerary.setAvailable(false);

			final List<PricedItineraryData> pricedItineraries = new ArrayList<>();
			pricedItineraries.add(availablePricedItinerary);
			pricedItineraries.add(unavailablePricedItinerary);

			return pricedItineraries;
		}

		private static List<ItineraryPricingInfoData> createItineraryPricingInfos()
		{
			final ItineraryPricingInfoData availableItineraryPricingInfo = new ItineraryPricingInfoData();
			availableItineraryPricingInfo.setBundleTemplates(new ArrayList<TravelBundleTemplateData>());
			availableItineraryPricingInfo.setAvailable(true);
			availableItineraryPricingInfo.setPtcFareBreakdownDatas(createPTCBreakdown());

			// available
			final ItineraryPricingInfoData unavailableItineraryPricingInfo = new ItineraryPricingInfoData();
			unavailableItineraryPricingInfo.setBundleTemplates(new ArrayList<TravelBundleTemplateData>());
			unavailableItineraryPricingInfo.setAvailable(false);
			unavailableItineraryPricingInfo.setPtcFareBreakdownDatas(createPTCBreakdown());

			// unavailable
			final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
			itineraryPricingInfos.add(availableItineraryPricingInfo);
			itineraryPricingInfos.add(unavailableItineraryPricingInfo);

			return itineraryPricingInfos;
		}

		private static List<PTCFareBreakdownData> createPTCBreakdown()
		{
			final List<PTCFareBreakdownData> ptcFareBreakdowns = new ArrayList<>();
			ptcFareBreakdowns.add(adultPTCBreakdown("adult", 1, "25.00"));
			ptcFareBreakdowns.add(adultPTCBreakdown("child", 1, "10.00"));

			return ptcFareBreakdowns;
		}

		private static PTCFareBreakdownData adultPTCBreakdown(final String passengerTypeCode, final int qty, final String farePrice)
		{
			final PassengerTypeData passengerType = new PassengerTypeData();
			passengerType.setCode(passengerTypeCode);

			final PassengerTypeQuantityData PassengerTypeQuantity = new PassengerTypeQuantityData();
			PassengerTypeQuantity.setQuantity(qty);
			PassengerTypeQuantity.setPassengerType(passengerType);

			final FareInfoData fareInfo = new FareInfoData();
			fareInfo.setFareDetails(createFareDetails("ECO001"));

			final List<FareInfoData> fareInfos = new ArrayList<>();
			fareInfos.add(fareInfo);

			final PassengerFareData passengerFare = new PassengerFareData();
			passengerFare.setBaseFare(new PriceData());
			passengerFare.getBaseFare().setValue(new BigDecimal(farePrice));

			final PTCFareBreakdownData ptcBreakdown = new PTCFareBreakdownData();
			ptcBreakdown.setPassengerTypeQuantity(PassengerTypeQuantity);
			ptcBreakdown.setFareInfos(fareInfos);
			ptcBreakdown.setPassengerFare(passengerFare);

			return ptcBreakdown;
		}

		private static List<FareDetailsData> createFareDetails(final String fareCode)
		{
			final PriceData farePrice = new PriceData();
			farePrice.setValue(new BigDecimal("10.00"));

			final FareProductData fareProduct = new FareProductData();
			fareProduct.setCode(fareCode);
			fareProduct.setPrice(farePrice);

			final FareDetailsData fareDetail = new FareDetailsData();
			fareDetail.setFareProduct(fareProduct);

			final List<FareDetailsData> fareDetails = new ArrayList<>();
			fareDetails.add(fareDetail);

			return fareDetails;
		}
	}
}
