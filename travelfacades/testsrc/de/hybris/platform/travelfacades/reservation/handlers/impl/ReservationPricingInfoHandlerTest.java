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
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TransportOfferingType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for ReservationPricingInfoHandler
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationPricingInfoHandlerTest
{
	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private TravelCartService travelCartService;

	@InjectMocks
	ReservationPricingInfoHandler handler = new ReservationPricingInfoHandler();

	/**
	 * given: empty reservationitems
	 *
	 * when: population of ReservationPricingInfo is attempted
	 *
	 * then: creates no ReservationPricingInfos
	 */
	@Test
	public void testEmptyReservationItems()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		handler.handle(null, testDataSetUp.createReservationData(Collections.EMPTY_LIST));
	}

	/**
	 * given: reservationitems for a single sector itinerary
	 *
	 * when: population of ReservationPricingInfo is attempted
	 *
	 * then: creates resepective ReservationPricingInfos
	 */
	@Test
	public void testPopulateReservationPricingInfoSingleSector()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final TransportOfferingModel transportOfferingModel = testDataSetUp.createTransportOffering("to1");
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1, 1l,
				transportOfferingModel);
		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1, 1l,
				transportOfferingModel);
		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 1, 1, 10d, 10d, null, null,
				transportOfferingModel);

		final ProductModel ancillaryProduct = testDataSetUp.createAncillaryProductModel();
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ancillaryProduct, 1, 1, 10d, 10d, null, 1l,
				transportOfferingModel);

		final ProductModel ancillaryProduct2 = testDataSetUp.createAncillaryProductModel();
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ancillaryProduct2, 1, 0, 10d, 10d, null, 1l,
				transportOfferingModel);

		final AbstractOrderModel abstractorderModel = testDataSetUp
				.createCartModel(Stream.of(entry1, entry2, entry3, entry4, entry5).collect(Collectors.toList()), true, null);

		//prepare ReservationItemData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT");
		final TravellerData travellerDataChild1 = testDataSetUp.createTravellerData("5678", "CHILD");
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1, travellerDataChild1).collect(Collectors.toList()));
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, itineraryData);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationItem).collect(Collectors.toList()));
		given(travelCartService.getFareProductEntries(abstractorderModel))
				.willReturn(Stream.of(entry1, entry2).collect(Collectors.toList()));
		given(travelCommercePriceFacade.createPriceData(10d, "GBP")).willReturn(testDataSetUp.createPriceData(10d));
		given(travelCommercePriceFacade.createPriceData(10d)).willReturn(testDataSetUp.createPriceData(10d));
		given(travelCommercePriceFacade.createPriceData(5d)).willReturn(testDataSetUp.createPriceData(5d));
		given(travelCommercePriceFacade.createPriceData(5d, "GBP")).willReturn(testDataSetUp.createPriceData(5d));
		handler.handle(abstractorderModel, reservationData);
		assertNotNull(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo());
	}

	/**
	 * given: reservationitems for a multi sector itinerary
	 *
	 * when: population of ReservationPricingInfo is attempted
	 *
	 * then: creates resepective ReservationPricingInfos
	 */
	@Test
	public void testPopulateReservationPricingInfoMultiSector()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final TransportOfferingModel transportOfferingModel = testDataSetUp.createTransportOffering("to1");
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1, 1l,
				transportOfferingModel);
		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult2 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 2, 1, 20d, 20d, travellerAdult2, 1l,
				transportOfferingModel);
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(fareProduct2, 2, 1, 20d, 20d, travellerAdult2, 1l,
				transportOfferingModel);
		entry4.setActive(false);
		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 0, 1, 10d, 10d, null, null,
				transportOfferingModel);
		final AbstractOrderModel abstractorderModel = testDataSetUp
				.createCartModel(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()), false, null);

		//prepare ReservationItemData
		final TravellerData travellerDataAdult1 = testDataSetUp.createTravellerData("1234", "ADULT");
		final TravellerData travellerDataAdult2 = testDataSetUp.createTravellerData("1234", "ADULT");
		final ItineraryData itineraryData = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult1).collect(Collectors.toList()));
		final ItineraryData itineraryData2 = testDataSetUp
				.createItineraryData(Stream.of(travellerDataAdult2).collect(Collectors.toList()));
		final ReservationItemData reservationItem = testDataSetUp.createReservationItemData(1, itineraryData);
		final ReservationItemData reservationItem2 = testDataSetUp.createReservationItemData(2, itineraryData2);
		final ReservationData reservationData = testDataSetUp
				.createReservationData(Stream.of(reservationItem, reservationItem2).collect(Collectors.toList()));
		given(travelCommercePriceFacade.createPriceData(10d, "GBP")).willReturn(testDataSetUp.createPriceData(10d));
		given(travelCommercePriceFacade.createPriceData(10d)).willReturn(testDataSetUp.createPriceData(10d));
		given(travelCommercePriceFacade.createPriceData(5d)).willReturn(testDataSetUp.createPriceData(5d));
		given(travelCommercePriceFacade.createPriceData(5d, "GBP")).willReturn(testDataSetUp.createPriceData(5d));
		handler.handle(abstractorderModel, reservationData);
		assertNotNull(reservationData.getReservationItems().stream().findFirst().get().getReservationPricingInfo());
	}

	private class TestDataSetUp
	{
		private ReservationData createReservationData(final List<ReservationItemData> reservationItems)
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setReservationItems(reservationItems);
			return reservationData;
		}

		private ReservationItemData createReservationItemData(final int odRefNumber, final ItineraryData reservationItinerary)
		{
			final ReservationItemData reservationItemData = new ReservationItemData();
			reservationItemData.setOriginDestinationRefNumber(odRefNumber);
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
				final Long qty, final TransportOfferingModel transportOfferingModel)
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
			cartEntry.setProduct(product);
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			orderEntryInfo.setTransportOfferings(Stream.of(transportOfferingModel).collect(Collectors.toList()));
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

		private TransportOfferingModel createTransportOffering(final String code)
		{
			final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
			transportOfferingModel.setType(TransportOfferingType.TRAINOFFERING);
			transportOfferingModel.setCode(code);
			return transportOfferingModel;
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

		private FareProductModel createAncillaryProductModel()
		{
			final FareProductModel product = new FareProductModel();
			product.setProductType(ProductType.ANCILLARY);
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

	}

}