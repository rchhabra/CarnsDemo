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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test class to test TravellerBreakdownHandler.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravellerBreakdownHandlerTest
{

	@Mock
	private ProductService productService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private Map<String, String> offerGroup2OriginDestinationMapping;

	@Mock
	private TravelCommercePriceService travelCommercePriceService;

	@InjectMocks
	TravellerBreakdownHandler handler = new TravellerBreakdownHandler();

	private final Function<PriceInformation, PriceData> priceDataMapper = t -> {
		final PriceData priceData = new PriceData();
		priceData.setPriceType(PriceDataType.BUY);
		priceData.setValue(BigDecimal.valueOf(t.getPriceValue().getValue()));
		priceData.setCurrencyIso(t.getPriceValue().getCurrencyIso());
		return priceData;
	};

	/**
	 * given: an Itinerary with ancillary products
	 *
	 * Meal1 offered at 10 GBP each and was included in the bundle for Adult1, Adult2, Child1 and Adult1 added another
	 * Meal1 in cart.
	 *
	 * Meal2 offered at 15 GBP each and was added by Adult2 in cart
	 *
	 * Bag23Kg offered at 25 GBP each.
	 *
	 * When: Ancillary products are offered in the offerResponse.
	 *
	 * Then: following is expected:
	 *
	 * Response should contain 2 offerGroups, one for Meal and other for HoldItem
	 *
	 * OfferGroup of Meal should contain 2 OfferPricingInfo one for Meal1 and other for Meal 2
	 *
	 * All products TravellerBreakDown should have 3 elements for Adult1, Adult2, Child1
	 *
	 * Price for Meal1 product for Adult1 should be 20 GBP
	 *
	 * quantity for Meal1 product for Adult1 is 2 (one from bundle and one as selected)
	 *
	 * Price for Meal1 product for Adult2 should be 10 GBP
	 *
	 * quantity for Meal1 product for Adult2 is 1
	 *
	 * Price for Meal1 product for Child1 should be 10 GBP
	 *
	 * quantity for Meal1 product for Child1 is 1
	 *
	 * Price for Meal2 product for Adult1 should be 0.0 GBP
	 *
	 * quantity for Meal2 product for Adult1 is 0
	 *
	 * Price for Meal2 product for Adult2 should be 15 GBP
	 *
	 * quantity for Meal2 product for Adult2 is 1
	 *
	 * Price for Meal2 product for Child1 should be 0.0 GBP
	 *
	 * quantity for Meal2 product for Child1 is 0
	 *
	 * Price for Bag23Kg product for Adult1 should be 0.0 GBP
	 *
	 * quantity for Bag23Kg product for Adult1 is 0
	 *
	 * Price for Bag23Kg product for Adult2 should be 0.0 GBP
	 *
	 * quantity for Bag23Kg product for Adult2 is 0
	 *
	 * Price for Bag23Kg product for Child1 should be 0.0 GBP
	 *
	 * quantity for Bag23Kg product for Child1 is 0
	 *
	 */
	@Test
	public void testPopulate()
	{
		//OfferRequest creation
		final TestDataSetup testDataSetup = new TestDataSetup();
		final TravellerData travellerAdult1 = testDataSetup.createTravellerData("Adult1");
		final TravellerData travellerAdult2 = testDataSetup.createTravellerData("Adult2");
		final TravellerData travellerChild1 = testDataSetup.createTravellerData("Child1");

		final List<TravellerData> travellers = Stream.of(travellerAdult1, travellerAdult2, travellerChild1)
				.collect(Collectors.<TravellerData> toList());

		final OfferPricingInfoData offerPricingInforMeal1Bundled = testDataSetup.createOfferPricingInfoData("Meal1",
				Stream.of(testDataSetup.createTravellerBreakdownData(travellerAdult1, 1),
						testDataSetup.createTravellerBreakdownData(travellerAdult2, 1),
						testDataSetup.createTravellerBreakdownData(travellerChild1, 1))
						.collect(Collectors.<TravellerBreakdownData> toList()),
				1);
		final OfferPricingInfoData offerPricingInforMeal1Selected = testDataSetup.createOfferPricingInfoData("Meal1",
				Stream.of(testDataSetup.createTravellerBreakdownData(travellerAdult1, 1))
						.collect(Collectors.<TravellerBreakdownData> toList()),
				0);
		final OfferPricingInfoData offerPricingInforMeal2Selected = testDataSetup.createOfferPricingInfoData("Meal2",
				Stream.of(testDataSetup.createTravellerBreakdownData(travellerAdult2, 1))
						.collect(Collectors.<TravellerBreakdownData> toList()),
				0);

		final OriginDestinationOfferInfoData odOfferInfoLGWCDG = testDataSetup.createOrignDestinationOfferInfo(
				Stream.of(testDataSetup.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()),
				Stream.of(offerPricingInforMeal1Bundled, offerPricingInforMeal1Selected, offerPricingInforMeal2Selected)
						.collect(Collectors.<OfferPricingInfoData> toList()),
				"LGW-CDG");

		final OfferGroupData selectedOffers = testDataSetup.createOfferGroupData("Meal",
				Stream.of(odOfferInfoLGWCDG).collect(Collectors.<OriginDestinationOfferInfoData> toList()));

		final OfferRequestData offerReqData = testDataSetup.createOfferRequestData(
				testDataSetup.createSelectedOffersData(Stream.of(selectedOffers).collect(Collectors.<OfferGroupData> toList())));

		offerReqData.setItineraries(
				Stream.of(testDataSetup.createItineraryData("LGW-CDG", travellers)).collect(Collectors.<ItineraryData> toList()));

		//OfferResponse creation
		final OfferPricingInfoData offerPricingInforResMeal1 = testDataSetup.createOfferPricingInfoData("Meal1", null, 0);

		final OfferPricingInfoData offerPricingInforResMeal2 = testDataSetup.createOfferPricingInfoData("Meal2", null, 0);

		final OriginDestinationOfferInfoData odOfferInfoLGWCDGRes1 = testDataSetup.createOrignDestinationOfferInfo(
				Stream.of(testDataSetup.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()),
				Stream.of(offerPricingInforResMeal1, offerPricingInforResMeal2).collect(Collectors.<OfferPricingInfoData> toList()),
				"LGW-CDG");

		final OfferGroupData offerGroupsMealRes = testDataSetup.createOfferGroupData("Meal",
				Stream.of(odOfferInfoLGWCDGRes1).collect(Collectors.<OriginDestinationOfferInfoData> toList()));

		final OfferPricingInfoData offerPricingInforResBag1 = testDataSetup.createOfferPricingInfoData("Bag23Kg", null, 0);

		final OriginDestinationOfferInfoData odOfferInfoLGWCDGRes2 = testDataSetup.createOrignDestinationOfferInfo(
				Stream.of(testDataSetup.createTransportOfferingData("EZY1234010120160730"))
						.collect(Collectors.<TransportOfferingData> toList()),
				Stream.of(offerPricingInforResBag1).collect(Collectors.<OfferPricingInfoData> toList()), "LGW-CDG");

		final OfferGroupData offerGroupsBagRes = testDataSetup.createOfferGroupData("HoldItem",
				Stream.of(odOfferInfoLGWCDGRes2).collect(Collectors.<OriginDestinationOfferInfoData> toList()));

		final OfferResponseData offerResData = testDataSetup.createOfferResponseData(
				Stream.of(offerGroupsMealRes, offerGroupsBagRes).collect(Collectors.<OfferGroupData> toList()));

		//Setup mock responses
		final ProductModel productMeal1 = new ProductModel();
		productMeal1.setCode("Meal1");
		given(productService.getProductForCode("Meal1")).willReturn(productMeal1);

		final ProductModel productMeal2 = new ProductModel();
		productMeal2.setCode("Meal2");
		given(productService.getProductForCode("Meal2")).willReturn(productMeal2);

		final ProductModel productBag23Kg = new ProductModel();
		productBag23Kg.setCode("Bag23Kg");
		given(productService.getProductForCode("Bag23Kg")).willReturn(productBag23Kg);

		final PriceValue priceValueMeal1 = new PriceValue("GBP", 10, false);
		final PriceInformation pricInfoMeal1 = new PriceInformation(priceValueMeal1);

		final PriceValue priceValueMeal2 = new PriceValue("GBP", 15, false);
		final PriceInformation pricInfoMeal2 = new PriceInformation(priceValueMeal2);

		final PriceValue priceValueBag23Kg = new PriceValue("GBP", 25, false);
		final PriceInformation pricInfoBag23Kg = new PriceInformation(priceValueBag23Kg);

		given(travelCommercePriceService.getProductWebPrice(Mockito.eq(productMeal1), Mockito.anyMapOf(String.class, String.class)))
				.willReturn(pricInfoMeal1);

		given(travelCommercePriceService.getProductWebPrice(Mockito.eq(productMeal2), Mockito.anyMapOf(String.class, String.class)))
				.willReturn(pricInfoMeal2);

		given(travelCommercePriceService.getProductWebPrice(Mockito.eq(productBag23Kg),
				Mockito.anyMapOf(String.class, String.class))).willReturn(pricInfoBag23Kg);

		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(pricInfoMeal1.getPriceValue().getValue()), pricInfoMeal1.getPriceValue().getCurrencyIso()))
						.willReturn(priceDataMapper.apply(pricInfoMeal1));

		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(pricInfoMeal2.getPriceValue().getValue()), pricInfoMeal2.getPriceValue().getCurrencyIso()))
						.willReturn(priceDataMapper.apply(pricInfoMeal2));

		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(pricInfoBag23Kg.getPriceValue().getValue()), pricInfoBag23Kg.getPriceValue().getCurrencyIso()))
						.willReturn(priceDataMapper.apply(pricInfoBag23Kg));

		final PriceData priceDataMealAdult1 = new PriceData();
		priceDataMealAdult1.setPriceType(PriceDataType.BUY);
		priceDataMealAdult1.setValue(BigDecimal.valueOf(20.0));
		priceDataMealAdult1.setCurrencyIso("GBP");

		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20.0), "GBP"))
				.willReturn(priceDataMealAdult1);

		final PriceData priceDataZero = new PriceData();
		priceDataZero.setPriceType(PriceDataType.BUY);
		priceDataZero.setValue(BigDecimal.ZERO);
		priceDataZero.setCurrencyIso("GBP");

		given(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(0.0), "GBP"))
				.willReturn(priceDataZero);

		given(offerGroup2OriginDestinationMapping.get("Meal")).willReturn(TravelfacadesConstants.TRANSPORT_OFFERING);

		given(offerGroup2OriginDestinationMapping.get("HoldItem")).willReturn(TravelfacadesConstants.TRAVEL_ROUTE);

		handler.handle(offerReqData, offerResData);

		final List<OfferGroupData> mealOffers = offerResData.getOfferGroups().stream()
				.filter(offerGroup -> offerGroup.getCode().equals("Meal")).collect(Collectors.<OfferGroupData> toList());

		assertEquals(2, mealOffers.stream().findFirst().get().getOriginDestinationOfferInfos().stream().findFirst().get()
				.getOfferPricingInfos().size());

		final List<OfferPricingInfoData> meal1OfferInfo = mealOffers.stream().findFirst().get().getOriginDestinationOfferInfos()
				.stream().findFirst().get().getOfferPricingInfos().stream()
				.filter(offerPricingInfoData -> offerPricingInfoData.getProduct().getCode().equals("Meal1"))
				.collect(Collectors.<OfferPricingInfoData> toList());

		final OfferPricingInfoData meal1offer = meal1OfferInfo.stream().findFirst().get();
		assertEquals(3, meal1offer.getTravellerBreakdowns().size());

		//Meal1 for Adult1
		final TravellerBreakdownData adult1Meal1Data = meal1offer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Adult1")).findFirst().get();
		assertEquals(BigDecimal.valueOf(20.0), adult1Meal1Data.getPassengerFare().getTotalFare().getValue());
		assertEquals(2, adult1Meal1Data.getQuantity().intValue());

		//Meal1 for Adult2
		final TravellerBreakdownData adult2Meal1Data = meal1offer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Adult2")).findFirst().get();
		assertEquals(BigDecimal.valueOf(10.0), adult2Meal1Data.getPassengerFare().getTotalFare().getValue());
		assertEquals(1, adult2Meal1Data.getQuantity().intValue());

		//Meal1 for Child1
		final TravellerBreakdownData child1Meal1Data = meal1offer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Child1")).findFirst().get();
		assertEquals(BigDecimal.valueOf(10.0), child1Meal1Data.getPassengerFare().getTotalFare().getValue());
		assertEquals(1, child1Meal1Data.getQuantity().intValue());


		final List<OfferPricingInfoData> meal2OfferInfo = mealOffers.stream().findFirst().get().getOriginDestinationOfferInfos()
				.stream().findFirst().get().getOfferPricingInfos().stream()
				.filter(offerPricingInfoData -> offerPricingInfoData.getProduct().getCode().equals("Meal2"))
				.collect(Collectors.<OfferPricingInfoData> toList());

		final OfferPricingInfoData meal2offer = meal2OfferInfo.stream().findFirst().get();
		assertEquals(3, meal2offer.getTravellerBreakdowns().size());

		//Meal2 for Adult1
		final TravellerBreakdownData adult1Meal2Data = meal2offer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Adult1")).findFirst().get();
		assertEquals(0, adult1Meal2Data.getQuantity().intValue());

		//Meal2 for Adult2
		final TravellerBreakdownData adult2Meal2Data = meal2offer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Adult2")).findFirst().get();
		assertEquals(BigDecimal.valueOf(15.0), adult2Meal2Data.getPassengerFare().getTotalFare().getValue());
		assertEquals(1, adult2Meal2Data.getQuantity().intValue());

		//Meal2 for Child1
		final TravellerBreakdownData child1Meal2Data = meal2offer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Child1")).findFirst().get();
		assertEquals(0, child1Meal2Data.getQuantity().intValue());

		//HoldItem Offers
		final List<OfferGroupData> holdItemsOffers = offerResData.getOfferGroups().stream()
				.filter(offerGroup -> offerGroup.getCode().equals("HoldItem")).collect(Collectors.<OfferGroupData> toList());

		assertEquals(1, holdItemsOffers.stream().findFirst().get().getOriginDestinationOfferInfos().stream().findFirst().get()
				.getOfferPricingInfos().size());

		final OfferPricingInfoData bag23kgOffer = holdItemsOffers.stream().findFirst().get().getOriginDestinationOfferInfos()
				.stream().findFirst().get().getOfferPricingInfos().stream().findFirst().get();

		assertEquals(3, bag23kgOffer.getTravellerBreakdowns().size());

		//Meal2 for Adult1
		final TravellerBreakdownData adult1Bag1Data = bag23kgOffer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Adult1")).findFirst().get();
		assertEquals(0, adult1Bag1Data.getQuantity().intValue());

		//Meal2 for Adult2
		final TravellerBreakdownData adult2Bag1Data = bag23kgOffer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Adult2")).findFirst().get();
		assertEquals(0, adult2Bag1Data.getQuantity().intValue());

		//Meal2 for Child1
		final TravellerBreakdownData child1Bag1Data = bag23kgOffer.getTravellerBreakdowns().stream()
				.filter(travelBreakDown -> travelBreakDown.getTraveller().getLabel().equals("Child1")).findFirst().get();
		assertEquals(0, child1Bag1Data.getQuantity().intValue());
	}

	/**
	 * Test data setup
	 */
	private class TestDataSetup
	{

		private OfferResponseData createOfferResponseData(final List<OfferGroupData> offerGroups)
		{
			final OfferResponseData offerResponseData = new OfferResponseData();
			offerResponseData.setOfferGroups(offerGroups);
			return offerResponseData;

		}

		private OfferRequestData createOfferRequestData(final SelectedOffersData selectedOffers)
		{
			final OfferRequestData offerReqData = new OfferRequestData();
			offerReqData.setSelectedOffers(selectedOffers);
			return offerReqData;

		}

		private SelectedOffersData createSelectedOffersData(final List<OfferGroupData> offerGroups)
		{
			final SelectedOffersData selectedOffersData = new SelectedOffersData();
			selectedOffersData.setOfferGroups(offerGroups);
			return selectedOffersData;
		}

		private OfferGroupData createOfferGroupData(final String categoryCode,
				final List<OriginDestinationOfferInfoData> odOfferInfos)
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
			offerGroupData.setCode(categoryCode);
			offerGroupData.setOriginDestinationOfferInfos(odOfferInfos);
			return offerGroupData;
		}

		private OriginDestinationOfferInfoData createOrignDestinationOfferInfo(
				final List<TransportOfferingData> transportOfferingData, final List<OfferPricingInfoData> offerPricingInfos,
				final String routeCode)
		{
			final OriginDestinationOfferInfoData offerInfoData = new OriginDestinationOfferInfoData();
			offerInfoData.setTransportOfferings(transportOfferingData);
			offerInfoData.setOfferPricingInfos(offerPricingInfos);
			offerInfoData.setTravelRouteCode(routeCode);
			return offerInfoData;
		}

		private OfferPricingInfoData createOfferPricingInfoData(final String productCode,
				final List<TravellerBreakdownData> travellerBreakDown, final Integer bundleIndicator)
		{
			final OfferPricingInfoData offerPriceInfo = new OfferPricingInfoData();
			offerPriceInfo.setTravellerBreakdowns(travellerBreakDown);
			offerPriceInfo.setBundleIndicator(bundleIndicator);
			final ProductData productData = new ProductData();
			productData.setCode(productCode);
			offerPriceInfo.setProduct(productData);
			return offerPriceInfo;
		}

		private TravellerBreakdownData createTravellerBreakdownData(final TravellerData travellerData, final Integer quantity)
		{
			final TravellerBreakdownData travellerBreakdownData = new TravellerBreakdownData();
			travellerBreakdownData.setTraveller(travellerData);
			travellerBreakdownData.setQuantity(quantity);
			return travellerBreakdownData;
		}

		private TravellerData createTravellerData(final String code)
		{
			final TravellerData travellerData = new TravellerData();
			travellerData.setLabel(code);
			return travellerData;
		}

		private TransportOfferingData createTransportOfferingData(final String code)
		{
			final TransportOfferingData toData = new TransportOfferingData();
			toData.setCode(code);
			return toData;
		}

		private ItineraryData createItineraryData(final String routeCode, final List<TravellerData> travellers)
		{
			final ItineraryData itineraryData = new ItineraryData();
			final TravelRouteData travelRouteData = new TravelRouteData();
			travelRouteData.setCode(routeCode);
			itineraryData.setRoute(travelRouteData);
			itineraryData.setTravellers(travellers);
			return itineraryData;
		}

	}


}
