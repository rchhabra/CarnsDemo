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

import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
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
import de.hybris.platform.core.model.order.OrderModel;
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
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for ReservationOfferBreakdownHandler
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationPricingInfoOfferBreakdownHandlerTest
{
	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@InjectMocks
	private final ReservationPricingInfoOfferBreakdownHandler handler = new ReservationPricingInfoOfferBreakdownHandler();

	@Before
	public void setUp()
	{
		final List<ProductType> notAncillaryProductTypes = new ArrayList<>();

		notAncillaryProductTypes.add(ProductType.FARE_PRODUCT);
		notAncillaryProductTypes.add(ProductType.FEE);

		handler.setNotAncillaryProductTypes(notAncillaryProductTypes);
	}


	/**
	 * given: empty reservationItems
	 *
	 * when: population of ReservationOfferBreakdownData is attempted
	 *
	 * then: creates no ReservationOfferBreakdownData
	 */
	@Test
	public void testEmptyReservationItems()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		handler.handle(null, testDataSetUp.createReservationData(Collections.EMPTY_LIST));
	}

	/**
	 * given: null ReservationpricingInfo
	 *
	 * when: population of ReservationOfferBreakdownData is attempted
	 *
	 * then: creates no ReservationOfferBreakdownData
	 */

	@Test
	public void testNullReservationpricingInfo()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, null, null);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationItem).collect(Collectors.toList()));
		handler.handle(null, reservationData);
	}

	/**
	 * given: cart with fare products and ancillaries
	 *
	 * when: population of ReservationOfferBreakdownData is attempted
	 *
	 * then: creates ReservationOfferBreakdownData for the respecting ReservationItemData
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
		final TravellerModel travellerAdult2 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 2, 1, 20d, 20d, travellerAdult2,
				null);

		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 0, 1, 10d, 10d, null, null);


		//adding ancillaries
		final ProductModel ancillaryProduct1 = testDataSetUp.createAncillaryProduct("EXBAG32KG");
		given(productConverter.convert(ancillaryProduct1)).willReturn(testDataSetUp.createAncillaryProductData("EXBAG32KG"));
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ancillaryProduct1, 1, 1, 10d, 10d,
				travellerAdult1, 1l);
		final ProductModel ancillaryProduct11 = testDataSetUp.createAncillaryProduct("GOLFBAG");
		given(productConverter.convert(ancillaryProduct11)).willReturn(testDataSetUp.createAncillaryProductData("GOLFBAG"));
		final AbstractOrderEntryModel entry41 = testDataSetUp.createCartEntryModel(ancillaryProduct11, 1, 1, 10d, 10d,
				travellerAdult1, 1l);
		final ProductModel ancillaryProduct12 = testDataSetUp.createAncillaryProduct("GOLFBAG");
		given(productConverter.convert(ancillaryProduct12)).willReturn(testDataSetUp.createAncillaryProductData("GOLFBAG"));
		final AbstractOrderEntryModel entry42 = testDataSetUp.createCartEntryModel(ancillaryProduct12, 1, 1, 10d, 10d,
				travellerAdult1, 1l);
		final TransportOfferingModel transportOffering1 = testDataSetUp.createTransportOfferingModel("EZY1234090320160735");
		final TransportOfferingModel transportOffering2 = testDataSetUp.createTransportOfferingModel("EZY5678090320160735");
		entry4.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()));
		final TransportOfferingModel transportOffering3 = testDataSetUp.createTransportOfferingModel("EZY2222090320160735");
		final TransportOfferingModel transportOffering4 = testDataSetUp.createTransportOfferingModel("EZY4444090320160735");
		final ProductModel ancillaryProduct2 = testDataSetUp.createAncillaryProduct("EXBAG32KG");
		given(productConverter.convert(ancillaryProduct2)).willReturn(testDataSetUp.createAncillaryProductData("EXBAG32KG"));
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ancillaryProduct2, 2, 1, 10d, 10d,
				travellerAdult2, 1l);
		entry5.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOffering3, transportOffering4).collect(Collectors.toList()));

		final AbstractOrderModel abstractorderModel = testDataSetUp.createCartModel(
				Stream.of(entry1, entry2, entry3, entry4, entry41, entry42, entry5).collect(Collectors.toList()), false, null);
		//prepare ReservationItemData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT");
		final TravellerData travellerDataAdult2 = testDataSetUp.createTravellerData("1234", "ADULT");
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1).collect(Collectors.toList()));
		final ItineraryData itineraryData2 = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult2).collect(Collectors.toList()));
		final ReservationPricingInfoData resPricingInfo1 = testDataSetUp.createReservationPricingInfoData();
		final ReservationPricingInfoData resPricingInfo2 = testDataSetUp.createReservationPricingInfoData();
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, itineraryData, resPricingInfo1);
		final ReservationItemData reservationItem2 = testDataSetUp.createReservationItemData(2, itineraryData2, resPricingInfo2);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationItem, reservationItem2).collect(Collectors.toList()));

		given(travelCommercePriceFacade.createPriceData(10d, "GBP")).willReturn(testDataSetUp.createPriceData(10d));

		handler.handle(abstractorderModel, reservationData);
		assertFalse(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo()
				.getOfferBreakdowns().isEmpty());
	}

	private class TestDataSetUp
	{
		private ReservationData createReservationData(final List<ReservationItemData> reservationItems)
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setReservationItems(reservationItems);
			return reservationData;
		}

		private ReservationItemData createReservationItemData(final int odRefNumber, final ItineraryData reservationItinerary,
				final ReservationPricingInfoData reservationPricingInfo)
		{
			final ReservationItemData reservationItemData = new ReservationItemData();
			reservationItemData.setOriginDestinationRefNumber(odRefNumber);
			reservationItemData.setReservationItinerary(reservationItinerary);
			reservationItemData.setReservationPricingInfo(reservationPricingInfo);
			return reservationItemData;
		}

		private ReservationPricingInfoData createReservationPricingInfoData()
		{
			return new ReservationPricingInfoData();
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
			travellerData.setLabel(type);
			return travellerData;
		}

		private CartModel createCartModel(final List<AbstractOrderEntryModel> cartEntries, final boolean isNet,
				final String orderCode)
		{
			final CartModel cart = new CartModel()
			{
				@Override
				public Collection<TaxValue> getTotalTaxValues()
				{
					return Stream.of(createTaxValue("YQ", 10d, true, 10d, "GBP")).collect(Collectors.toList());
				}

				@Override
				public List<DiscountValue> getGlobalDiscountValues()
				{
					return Stream.of(createDiscountValue("FFDiscount", 5d, true, 5d, "GBP")).collect(Collectors.toList());
				}
			};
			cart.setEntries(cartEntries);
			cart.setSubtotal(30d);
			cart.setTotalDiscounts(10d);
			cart.setTotalPrice(30d);
			cart.setTotalTax(20d);
			cart.setNet(isNet);
			final OrderModel originalOrder = new OrderModel();
			originalOrder.setCode(orderCode);
			cart.setOriginalOrder(originalOrder);
			final CurrencyModel currency = new CurrencyModel();
			currency.setIsocode("GBP");
			cart.setCurrency(currency);
			if (CollectionUtils.isNotEmpty(cartEntries))
			{
				cartEntries.forEach(entry -> entry.setOrder(cart));
			}
			return cart;
		}

		private TaxValue createTaxValue(final String code, final double value, final boolean absolute, final double appliedValue,
				final String currencyCode)
		{
			final TaxValue tax = new TaxValue(code, value, absolute, appliedValue, currencyCode);
			return tax;
		}

		private DiscountValue createDiscountValue(final String code, final double value, final boolean absolute,
				final double appliedValue, final String currencyCode)
		{
			final DiscountValue discount = new DiscountValue(code, value, absolute, appliedValue, currencyCode);
			return discount;
		}

		private AbstractOrderEntryModel createCartEntryModel(final ProductModel product, final int odRefNumber,
				final int bundleNumber, final double basePrice, final double totalPrice, final TravellerModel traveller,
				final Long qty)
		{
			final AbstractOrderEntryModel cartEntry = new CartEntryModel()
			{
				@Override
				public Collection<TaxValue> getTaxValues()
				{
					return Stream.of(createTaxValue("YQ", 10d, true, 10d, "GBP")).collect(Collectors.toList());
				}

				@Override
				public List<DiscountValue> getDiscountValues()
				{
					return Stream.of(createDiscountValue("FFDiscount", 5d, true, 5d, "GBP")).collect(Collectors.toList());
				}
			};
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			cartEntry.setProduct(product);
			orderEntryInfo.setOriginDestinationRefNumber(odRefNumber);
			cartEntry.setBundleNo(bundleNumber);
			cartEntry.setActive(true);
			cartEntry.setBasePrice(basePrice);
			cartEntry.setTotalPrice(totalPrice);
			orderEntryInfo.setTravellers(Stream.of(traveller).collect(Collectors.toList()));
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

		private ProductModel createFeeProduct()
		{
			final ProductModel product = new ProductModel()
			{
				@Override
				public String getName()
				{
					return "FEE";
				}
			};
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

		private TransportOfferingModel createTransportOfferingModel(final String code)
		{
			final TransportOfferingModel transportOffering = new TransportOfferingModel();
			transportOffering.setCode(code);
			return transportOffering;
		}

		private ProductModel createAncillaryProduct(final String code)
		{
			final ProductModel product = new ProductModel();
			product.setProductType(ProductType.ANCILLARY);
			product.setCode(code);
			return product;
		}

		private ProductData createAncillaryProductData(final String code)
		{
			final ProductData product = new ProductData();
			product.setProductType(ProductType.ANCILLARY.getCode());
			product.setCode(code);
			return product;
		}

	}

}