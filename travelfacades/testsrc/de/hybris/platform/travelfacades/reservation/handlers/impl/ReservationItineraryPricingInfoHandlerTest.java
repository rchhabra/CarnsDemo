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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for ReservationItineraryPricingInfoHandler.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationItineraryPricingInfoHandlerTest
{
	@Mock
	private Converter<FareProductModel, FareProductData> fareProductConverter;

	@Mock
	private PriceDataFactory priceDataFactory;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private EnumerationService enumerationService;

	@InjectMocks
	private final ReservationItineraryPricingInfoHandler handler = new ReservationItineraryPricingInfoHandler();

	/**
	 * given: Cart with FareProducts
	 *
	 * when: population of reservationData from cart is initiated
	 *
	 * then: itinerary prices for the fares for respective travelers are set in ReservationItineraryPricingInfoData.
	 */
	@Test
	public void testPopulateWithFareProducts()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1);

		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1);

		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 1, 1, 10d, 10d, null);

		final AbstractOrderModel abstractorderModel = testDataSetUp
				.createCartModel(Stream.of(entry1, entry2, entry3).collect(Collectors.toList()));

		//prepare ReservationItemData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT");
		final TravellerData travellerDataChild1 = testDataSetUp.createTravellerData("5678", "CHILD");
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1, travellerDataChild1).collect(Collectors.toList()));
		final ReservationPricingInfoData reservationPricingInfo = new ReservationPricingInfoData();
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(reservationPricingInfo, 1,
				itineraryData);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationItem).collect(Collectors.toList()));

		given(fareProductConverter.convert(fareProduct1)).willReturn(testDataSetUp.createFareProductData());
		given(fareProductConverter.convert(fareProduct2)).willReturn(testDataSetUp.createFareProductData());
		given(commonI18NService.getCurrentCurrency()).willReturn(testDataSetUp.createCurrency());
		given(travelCommercePriceFacade.createPriceData(Matchers.anyDouble())).willReturn(testDataSetUp.createPriceData(20d));
		given(enumerationService.getEnumerationName(Matchers.any(BundleType.class))).willReturn("Economy");
		handler.handle(abstractorderModel, reservationData);
		assertNotNull(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo());
	}

	/**
	 * given: Cart with FareProducts but no reservationPricingInfo
	 *
	 * when: population of reservationData from cart is initiated
	 *
	 * then: itinerary prices are not populated for respective travelers are set in ReservationItineraryPricingInfoData.
	 */
	@Test
	public void testNoReservationPricingInfo()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractorderModel = testDataSetUp.createCartModel(null);
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(null, 1, null);//TODO
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationItem).collect(Collectors.toList()));
		handler.handle(abstractorderModel, reservationData);
		assertNull(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo());
	}

	/**
	 * given: Cart with FareProducts but no reservationItems
	 *
	 * when: population of reservationData from cart is initiated
	 *
	 * then: itinerary prices are not populated for respective travelers are set in ReservationItineraryPricingInfoData.
	 */
	@Test
	public void testNoReservationItems()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractorderModel = testDataSetUp.createCartModel(null);
		final ReservationData reservationData = new ReservationData();
		reservationData.setReservationItems(Collections.EMPTY_LIST);
		handler.handle(abstractorderModel, reservationData);
		assertTrue(reservationData.getReservationItems().isEmpty());
	}

	private class TestDataSetUp
	{
		private ReservationData createReservationData(final List<ReservationItemData> reservationItems)
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setReservationItems(reservationItems);
			return reservationData;
		}

		private ReservationItemData createReservationItemData(final ReservationPricingInfoData reservationPricingInfo,
				final int odRefNumber, final ItineraryData reservationItinerary)
		{
			final ReservationItemData reservationItemData = new ReservationItemData();
			reservationItemData.setOriginDestinationRefNumber(odRefNumber);
			reservationItemData.setReservationPricingInfo(reservationPricingInfo);
			reservationItemData.setReservationItinerary(reservationItinerary);
			return reservationItemData;
		}

		private ItineraryData createItineraryData(final List<TravellerData> travellers)
		{
			final ItineraryData itineraryData = new ItineraryData();
			itineraryData.setTravellers(travellers);
			return itineraryData;
		}

		private TravellerData createTravellerData(final String uid, final String type)
		{
			final TravellerData travellerData = new TravellerData();
			travellerData.setUid(uid);
			travellerData.setTravellerType(TravellerType.PASSENGER.getCode());
			travellerData.setTravellerInfo(createPassengerInformationData(type));
			travellerData.setLabel(type);
			return travellerData;
		}

		private PassengerInformationData createPassengerInformationData(final String type)
		{
			final PassengerInformationData paxInfo = new PassengerInformationData();
			final PassengerTypeData paxTypeData = new PassengerTypeData();
			paxTypeData.setCode(type);
			paxInfo.setPassengerType(paxTypeData);
			return paxInfo;
		}

		private CartModel createCartModel(final List<AbstractOrderEntryModel> cartEntries)
		{
			final CartModel cart = new CartModel();
			cart.setEntries(cartEntries);
			return cart;
		}

		private AbstractOrderEntryModel createCartEntryModel(final ProductModel product, final int odRefNumber,
				final int bundleNumber, final double basePrice, final double totalPrice, final TravellerModel traveller)
		{
			final AbstractOrderEntryModel cartEntry = new CartEntryModel();
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			cartEntry.setProduct(product);
			orderEntryInfo.setOriginDestinationRefNumber(odRefNumber);
			cartEntry.setBundleNo(bundleNumber);
			cartEntry.setActive(true);
			cartEntry.setBasePrice(basePrice);
			cartEntry.setTotalPrice(totalPrice);
			orderEntryInfo.setTravellers(Stream.of(traveller).collect(Collectors.toList()));
			cartEntry.setBundleTemplate(createBundleTemplateModel());
			cartEntry.setTravelOrderEntryInfo(orderEntryInfo);
			cartEntry.setType(OrderEntryType.TRANSPORT);
			return cartEntry;
		}

		private TravellerModel createTravellerModel(final String uid, final String type)
		{
			final TravellerModel travellerModel = new TravellerModel();
			travellerModel.setUid(uid);
			travellerModel.setType(TravellerType.PASSENGER);
			travellerModel.setInfo(createPassengerInformationModel(type));
			travellerModel.setLabel(type);
			return travellerModel;
		}

		private PassengerInformationModel createPassengerInformationModel(final String type)
		{
			final PassengerInformationModel paxInfo = new PassengerInformationModel();
			final PassengerTypeModel paxType = new PassengerTypeModel();
			paxType.setCode(type);
			paxInfo.setPassengerType(paxType);
			return paxInfo;
		}

		private FareProductModel createFareProductModel()
		{
			final FareProductModel product = new FareProductModel();
			product.setProductType(ProductType.FARE_PRODUCT);
			product.setBookingClass("O");
			product.setFareBasisCode("OHMX21");
			product.setBundleTemplates(Stream.of(createBundleTemplateModel()).collect(Collectors.toList()));
			return product;
		}

		private FareProductData createFareProductData()
		{
			final FareProductData product = new FareProductData();
			product.setProductType(ProductType.FARE_PRODUCT.toString());
			product.setBookingClass("O");
			product.setFareBasisCode("OHMX21");
			return product;
		}

		private ProductModel createFeeProduct()
		{
			final ProductModel product = new ProductModel();
			product.setProductType(ProductType.FEE);
			return product;
		}

		private CurrencyModel createCurrency()
		{
			final CurrencyModel currency = new CurrencyModel();
			currency.setIsocode("GBP");
			return currency;
		}

		private PriceData createPriceData(final double value)
		{
			final PriceData priceData = new PriceData();
			priceData.setCurrencyIso("GBP");
			priceData.setValue(BigDecimal.valueOf(value));
			return priceData;
		}

		private BundleTemplateModel createBundleTemplateModel()
		{
			final BundleTemplateModel bundleModel = new BundleTemplateModel();
			bundleModel.setType(BundleType.ECONOMY);
			return bundleModel;
		}
	}

}