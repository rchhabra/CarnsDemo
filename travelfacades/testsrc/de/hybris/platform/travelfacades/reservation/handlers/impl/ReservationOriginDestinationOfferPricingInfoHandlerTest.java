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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
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
import de.hybris.platform.servicelayer.dto.converter.Converter;
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
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for ReservationOriginDestinationOfferPricingInfoHandler.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationOriginDestinationOfferPricingInfoHandlerTest
{
	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter;

	@InjectMocks
	private final ReservationOriginDestinationOfferPricingInfoHandler handler = new ReservationOriginDestinationOfferPricingInfoHandler();

	@Before
	public void setUp()
	{
		final List<ProductType> notAncillaryProductTypes = new ArrayList<>();

		notAncillaryProductTypes.add(ProductType.FARE_PRODUCT);
		notAncillaryProductTypes.add(ProductType.FEE);
		notAncillaryProductTypes.add(ProductType.ACCOMMODATION);

		handler.setNotAncillaryProductTypes(notAncillaryProductTypes);

	}

	/**
	 * given: Cart with FareProducts and Ancillaries
	 *
	 * when: the itinerary has a single sector
	 *
	 * then: offer prices for the ancillaries selected by respective travelers are set in
	 * ReservationOfferPricingInfoData.
	 */
	@Test
	public void testPopulateSingleSector()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1,
				null);

		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1,
				null);

		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 1, 1, 10d, 10d, null, null);

		//adding ancillaries
		final ProductModel ancillaryProduct1 = testDataSetUp.createAncillaryProduct();
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ancillaryProduct1, 1, 1, 10d, 10d,
				travellerAdult1, 1l);
		final TransportOfferingModel transportOffering1 = testDataSetUp.createTransportOfferingModel("EZY1234090320160735");
		entry4.getTravelOrderEntryInfo().setTransportOfferings(Stream.of(transportOffering1).collect(Collectors.toList()));
		final ProductModel ancillaryProduct2 = testDataSetUp.createAncillaryProduct();
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ancillaryProduct2, 1, 1, 10d, 10d,
				travellerChild1, 1l);
		entry5.getTravelOrderEntryInfo().setTransportOfferings(
				Stream.of(testDataSetUp.createTransportOfferingModel("EZY1234090320160735")).collect(Collectors.toList()));

		//Duplicate entry for ancillaryProduct1
		final AbstractOrderEntryModel entry6 = testDataSetUp.createCartEntryModel(ancillaryProduct1, 1, 1, 10d, 10d,
				travellerAdult1, 1l);
		entry6.getTravelOrderEntryInfo().setTransportOfferings(Stream.of(transportOffering1).collect(Collectors.toList()));

		final ProductModel ancillaryProduct3 = testDataSetUp.createAncillaryProduct();
		ancillaryProduct3.setCode("PER_LEG_product");
		final AbstractOrderEntryModel entry7 = testDataSetUp.createCartEntryModel(ancillaryProduct3, 1, 1, 10d, 10d, null, 1l);
		entry7.getTravelOrderEntryInfo().setTransportOfferings(
				Stream.of(testDataSetUp.createTransportOfferingModel("EZY1234090320160735")).collect(Collectors.toList()));

		//Duplicate entry for ancillaryProduct3
		final AbstractOrderEntryModel entry8 = testDataSetUp.createCartEntryModel(ancillaryProduct3, 1, 1, 10d, 10d, null, 1l);
		entry8.getTravelOrderEntryInfo().setTransportOfferings(
				Stream.of(testDataSetUp.createTransportOfferingModel("EZY1234090320160735")).collect(Collectors.toList()));

		final AbstractOrderModel abstractorderModel = testDataSetUp.createCartModel(
				Stream.of(entry1, entry2, entry3, entry4, entry5, entry6, entry7, entry8).collect(Collectors.toList()));

		//prepare ReservationItemData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT");
		final TravellerData travellerDataChild1 = testDataSetUp.createTravellerData("5678", "CHILD");
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1, travellerDataChild1).collect(Collectors.toList()));
		final TransportOfferingData transportOfferingData1 = testDataSetUp.createTransportOfferingData("EZY1234090320160735");
		final OriginDestinationOfferInfoData odInfoData = testDataSetUp
				.createOriginDestinationOfferInfoData(Stream.of(transportOfferingData1).collect(Collectors.toList()));
		final ReservationPricingInfoData reservationPricingInfo = testDataSetUp
				.createReservationPricingInfoData(Stream.of(odInfoData).collect(Collectors.toList()));
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(reservationPricingInfo, 1,
				itineraryData);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationItem).collect(Collectors.toList()));
		given(transportOfferingConverter.convert(transportOffering1)).willReturn(transportOfferingData1);
		given(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyString()))
				.willReturn(testDataSetUp.createPriceData(10d));
		given(productConverter.convert(ancillaryProduct1)).willReturn(testDataSetUp.createAncillaryProductData());
		given(productConverter.convert(ancillaryProduct2)).willReturn(testDataSetUp.createAncillaryProductData());
		given(productConverter.convert(ancillaryProduct3)).willReturn(testDataSetUp.createAncillaryPerLegProductData());
		handler.handle(abstractorderModel, reservationData);
		assertNotNull(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo()
				.getOriginDestinationOfferInfos());
		assertFalse(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo()
				.getOriginDestinationOfferInfos().isEmpty());
		assertEquals("EXECBAG32KG",
				reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo()
						.getOriginDestinationOfferInfos().stream().findFirst().get().getOfferPricingInfos().stream().findFirst().get()
						.getProduct().getCode());
	}

	/**
	 * given: Cart with FareProducts and Ancillaries
	 *
	 * when: the itinerary has a multi sector
	 *
	 * then: offer prices for the ancillaries selected by respective travelers are set in
	 * ReservationOfferPricingInfoData.
	 */
	@Test
	public void testPopulateMultiSector()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1,
				null);

		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1,
				null);

		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 1, 1, 10d, 10d, null, null);

		//adding ancillaries
		final ProductModel ancillaryProduct1 = testDataSetUp.createAncillaryProduct();
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ancillaryProduct1, 1, 1, 10d, 10d,
				travellerAdult1, 1l);
		final TransportOfferingModel transportOffering1 = testDataSetUp.createTransportOfferingModel("EZY1234090320160735");
		final TransportOfferingModel transportOffering2 = testDataSetUp.createTransportOfferingModel("EZY5678090320160735");
		entry4.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()));
		final ProductModel ancillaryProduct2 = testDataSetUp.createAncillaryProduct();
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ancillaryProduct2, 1, 1, 10d, 10d,
				travellerChild1, 1l);
		entry5.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(testDataSetUp.createTransportOfferingModel("EZY1234090320160735"),
						testDataSetUp.createTransportOfferingModel("EZY5678090320160735")).collect(Collectors.toList()));

		final AbstractOrderEntryModel entry6 = testDataSetUp.createCartEntryModel(ancillaryProduct2, 1, 1, 10d, 10d,
				travellerChild1, 1l);
		entry6.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(testDataSetUp.createTransportOfferingModel("EZY1234090320160735"),
						testDataSetUp.createTransportOfferingModel("EZY5678090320160735"),
						testDataSetUp.createTransportOfferingModel("EZY5678090320160738")).collect(Collectors.toList()));

		final AbstractOrderModel abstractorderModel = testDataSetUp
				.createCartModel(Stream.of(entry1, entry2, entry3, entry4, entry5, entry6).collect(Collectors.toList()));

		//prepare ReservationItemData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT");
		final TravellerData travellerDataChild1 = testDataSetUp.createTravellerData("5678", "CHILD");
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1, travellerDataChild1).collect(Collectors.toList()));
		final TransportOfferingData transportOfferingData1 = testDataSetUp.createTransportOfferingData("EZY1234090320160735");
		final TransportOfferingData transportOfferingData2 = testDataSetUp.createTransportOfferingData("EZY5678090320160735");
		final OriginDestinationOfferInfoData odInfoData = testDataSetUp.createOriginDestinationOfferInfoData(
				Stream.of(transportOfferingData1, transportOfferingData2).collect(Collectors.toList()));
		final ReservationPricingInfoData reservationPricingInfo = testDataSetUp
				.createReservationPricingInfoData(Stream.of(odInfoData).collect(Collectors.toList()));
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(reservationPricingInfo, 1,
				itineraryData);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationItem).collect(Collectors.toList()));
		given(transportOfferingConverter.convert(transportOffering1)).willReturn(transportOfferingData1);
		given(transportOfferingConverter.convert(transportOffering2)).willReturn(transportOfferingData2);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.eq("GBP"))).willReturn(testDataSetUp.createPriceData(10d));
		given(productConverter.convert(ancillaryProduct1)).willReturn(testDataSetUp.createAncillaryProductData());
		given(productConverter.convert(ancillaryProduct2)).willReturn(testDataSetUp.createAncillaryProductData());
		handler.handle(abstractorderModel, reservationData);
		assertNotNull(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo()
				.getOriginDestinationOfferInfos());
		assertFalse(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo()
				.getOriginDestinationOfferInfos().isEmpty());
	}

	/**
	 * given: Cart with FareProducts and Ancillaries
	 *
	 * when: there is no ReservationPricingInfo
	 *
	 * then: offer prices aren't populated.
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
	 * given: Cart with FareProducts and Ancillaries
	 *
	 * when: there is no ReservationItems
	 *
	 * then: offer prices aren't populated.
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

	/**
	 * given: Cart with FareProducts only
	 *
	 * when: there is no Ancillary products
	 *
	 * then: offer prices aren't populated.
	 */
	@Test
	public void testNoAncillaries()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");

		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1,
				null);

		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1,
				null);

		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 1, 1, 10d, 10d, null, null);

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
		handler.handle(abstractorderModel, reservationData);
		assertTrue(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo()
				.getOriginDestinationOfferInfos().isEmpty());
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
			final CurrencyModel currency = new CurrencyModel();
			currency.setIsocode("GBP");
			cart.setCurrency(currency);
			cart.setEntries(cartEntries);
			if (CollectionUtils.isNotEmpty(cartEntries))
			{
				cartEntries.forEach(entry -> entry.setOrder(cart));
			}
			return cart;
		}

		private AbstractOrderEntryModel createCartEntryModel(final ProductModel product, final int odRefNumber,
				final int bundleNumber, final double basePrice, final double totalPrice, final TravellerModel traveller,
				final Long qty)
		{
			final AbstractOrderEntryModel cartEntry = new CartEntryModel();
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			cartEntry.setProduct(product);
			orderEntryInfo.setOriginDestinationRefNumber(odRefNumber);
			cartEntry.setBundleNo(bundleNumber);
			cartEntry.setActive(true);
			cartEntry.setBasePrice(basePrice);
			cartEntry.setTotalPrice(totalPrice);
			final List<TravellerModel> travellers = traveller != null ? Stream.of(traveller).collect(Collectors.toList())
					: Collections.emptyList();
			orderEntryInfo.setTravellers(travellers);
			cartEntry.setBundleTemplate(createBundleTemplateModel());
			cartEntry.setQuantity(qty);
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

		private ProductModel createAncillaryProduct()
		{
			final ProductModel product = new ProductModel();
			product.setProductType(ProductType.ANCILLARY);
			product.setCode("EXECBAG32KG");
			return product;
		}

		private TransportOfferingModel createTransportOfferingModel(final String code)
		{
			final TransportOfferingModel transportOffering = new TransportOfferingModel();
			transportOffering.setCode(code);
			return transportOffering;
		}

		private TransportOfferingData createTransportOfferingData(final String code)
		{
			final TransportOfferingData transportOffering = new TransportOfferingData();
			transportOffering.setCode(code);
			return transportOffering;
		}

		private ReservationPricingInfoData createReservationPricingInfoData(final List<OriginDestinationOfferInfoData> odOfferInfos)
		{
			final ReservationPricingInfoData reservationPricingInfo = new ReservationPricingInfoData();
			reservationPricingInfo.setOriginDestinationOfferInfos(odOfferInfos);
			return reservationPricingInfo;
		}

		private OriginDestinationOfferInfoData createOriginDestinationOfferInfoData(
				final List<TransportOfferingData> transportOfferings)
		{
			final OriginDestinationOfferInfoData odInfoData = new OriginDestinationOfferInfoData();
			odInfoData.setOriginDestinationRefNumber(1);
			odInfoData.setTransportOfferings(transportOfferings);
			return odInfoData;
		}

		private ProductData createAncillaryProductData()
		{
			final ProductData product = new ProductData();
			product.setProductType(ProductType.ANCILLARY.getCode());
			product.setCode("EXECBAG32KG");
			return product;
		}

		private ProductData createAncillaryPerLegProductData()
		{
			final ProductData product = new ProductData();
			product.setProductType(ProductType.ANCILLARY.getCode());
			product.setCode("PER_LEG_product");
			return product;
		}

	}
}