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
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.services.BaseStoreService;
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
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Collection;
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
 * Unit test class for ReservationTotalFareHandler.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationTotalFareHandlerTest
{

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private BaseStoreService baseStoreService;

	@InjectMocks
	ReservationTotalFareHandler handler = new ReservationTotalFareHandler();

	private TestDataSetUp testDataSetUp;

	@Before
	public void setUp()
	{
		testDataSetUp = new TestDataSetUp();
		given(travelCommercePriceFacade.createPriceData(80d)).willReturn(testDataSetUp.createPriceData(80d));
		given(travelCommercePriceFacade.createPriceData(60d)).willReturn(testDataSetUp.createPriceData(60d));
		given(travelCommercePriceFacade.createPriceData(5d)).willReturn(testDataSetUp.createPriceData(5d));
		given(travelCommercePriceFacade.createPriceData(0.0d)).willReturn(testDataSetUp.createPriceData(0.0d));
		given(travelCommercePriceFacade.createPriceData(110.0d)).willReturn(testDataSetUp.createPriceData(110.0d));
		given(travelCommercePriceFacade.createPriceData(20.0d)).willReturn(testDataSetUp.createPriceData(20.0d));
		given(travelCommercePriceFacade.createPriceData(80d, "GBP")).willReturn(testDataSetUp.createPriceData(80d));
		given(travelCommercePriceFacade.createPriceData(60d, "GBP")).willReturn(testDataSetUp.createPriceData(60d));
		given(travelCommercePriceFacade.createPriceData(5d, "GBP")).willReturn(testDataSetUp.createPriceData(5d));
		given(travelCommercePriceFacade.createPriceData(0.0d, "GBP")).willReturn(testDataSetUp.createPriceData(0.0d));
		given(travelCommercePriceFacade.createPriceData(110.0d, "GBP")).willReturn(testDataSetUp.createPriceData(110.0d));
		given(travelCommercePriceFacade.createPriceData(20.0d, "GBP")).willReturn(testDataSetUp.createPriceData(20.0d));
	}

	/**
	 * given: CartModel with selected products
	 *
	 * When: in Purchase flow
	 *
	 * Then: calculated total prices of the cart is set in ReservationData.totalFare.
	 */
	@Test
	public void testPurchaseFlow()
	{

		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");

		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1,
				Long.valueOf(1));

		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1,
				Long.valueOf(1));

		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 1, 1, 10d, 10d, null,
				Long.valueOf(1));

		final ProductModel ancillaryProduct = testDataSetUp.createAncillaryProduct();
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ancillaryProduct, 1, 0, 10d, 10d, null,
				Long.valueOf(1));
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ancillaryProduct, 1, 1, 10d, 10d, null,
				Long.valueOf(1));
		final AbstractOrderEntryModel entry6 = testDataSetUp.createCartEntryModel(ancillaryProduct, 1, 0, 10d, 10d, null,
				Long.valueOf(0));

		final AbstractOrderModel abstractorderModel = testDataSetUp.createCartModel(
				Stream.of(entry1, entry2, entry3, entry4, entry5, entry6).collect(Collectors.toList()), true, "0001");

		final ReservationData reservationData = new ReservationData();


		handler.handle(abstractorderModel, reservationData);
		assertEquals(BigDecimal.valueOf(80d), reservationData.getTotalFare().getBasePrice().getValue());
		assertEquals(BigDecimal.valueOf(0.0d), reservationData.getTotalToPay().getValue());
		assertEquals(BigDecimal.valueOf(110.0d), reservationData.getTotalFare().getTotalPrice().getValue());

		abstractorderModel.setOriginalOrder(null);
		handler.handle(abstractorderModel, reservationData);
		assertEquals(BigDecimal.valueOf(80d), reservationData.getTotalFare().getBasePrice().getValue());
		assertEquals(BigDecimal.valueOf(110.0d), reservationData.getTotalToPay().getValue());
		assertEquals(BigDecimal.valueOf(110.0d), reservationData.getTotalFare().getTotalPrice().getValue());
	}

	/**
	 * given: CartModel with selected products
	 *
	 * When: in Amendment flow
	 *
	 * Then: calculated extra amount to pay is set in ReservationData.totalToPay.
	 */
	@Test
	public void testAmendmentFlow()
	{
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
				.createCartModel(Stream.of(entry1, entry2, entry3).collect(Collectors.toList()), false, "00001");

		final ReservationData reservationData = new ReservationData();

		given(travelCommercePriceFacade.createPriceData(30d)).willReturn(testDataSetUp.createPriceData(30d));
		given(travelCommercePriceFacade.createPriceData(5d)).willReturn(testDataSetUp.createPriceData(5d));

		given(travelCommercePriceFacade.createPriceData(0d)).willReturn(testDataSetUp.createPriceData(0d));
		given(travelCommercePriceFacade.createPriceData(30d, "GBP")).willReturn(testDataSetUp.createPriceData(30d));
		given(travelCommercePriceFacade.createPriceData(5d, "GBP")).willReturn(testDataSetUp.createPriceData(5d));

		given(travelCommercePriceFacade.createPriceData(0d, "GBP")).willReturn(testDataSetUp.createPriceData(0d));
		handler.handle(abstractorderModel, reservationData);
		assertEquals(BigDecimal.valueOf(0d), reservationData.getTotalToPay().getValue());
	}

	private class TestDataSetUp
	{
		private CartModel createCartModel(final List<AbstractOrderEntryModel> cartEntries, final boolean isNet,
				final String orderCode)
		{
			final CartModel cart = new CartModel()
			{
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

			final OrderModel originalOrder = createOrderModel(orderCode, 30d, 20d);
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
				public List<DiscountValue> getDiscountValues()
				{
					return Stream.of(createDiscountValue("FFDiscount", 5d, true, 5d, "GBP")).collect(Collectors.toList());
				}

				@Override
				public Collection<TaxValue> getTaxValues()
				{
					return Stream.of(createTaxValue("FFTax", 5d, true, 5d, "GBP")).collect(Collectors.toList());
				}

			};
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			cartEntry.setProduct(product);
			if (product.getProductType().equals(ProductType.FARE_PRODUCT))
			{
				orderEntryInfo.setOriginDestinationRefNumber(odRefNumber);
				orderEntryInfo.setTravellers(Stream.of(traveller).collect(Collectors.toList()));
			}
			cartEntry.setBundleNo(bundleNumber);
			cartEntry.setActive(true);
			cartEntry.setBasePrice(basePrice);
			cartEntry.setTotalPrice(totalPrice);

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

		private ProductModel createAncillaryProduct()
		{
			final ProductModel product = new ProductModel()
			{
				@Override
				public String getName()
				{
					return "ANCILLARY";
				}
			};
			product.setProductType(ProductType.ANCILLARY);
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

		private OrderModel createOrderModel(final String code, final double totalPrice, final double totalTax)
		{
			final OrderModel orderModel = new OrderModel();
			orderModel.setCode(code);
			orderModel.setTotalPrice(totalPrice);
			orderModel.setTotalTax(totalTax);
			return orderModel;
		}

	}

}