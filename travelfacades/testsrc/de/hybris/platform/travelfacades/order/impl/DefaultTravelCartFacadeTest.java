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

package de.hybris.platform.travelfacades.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.order.PaymentOptionData;
import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.order.strategies.PopulatePropertyMapStrategy;
import de.hybris.platform.travelfacades.order.strategies.impl.PerBookingPopulatePropertyMapStrategy;
import de.hybris.platform.travelfacades.order.strategies.impl.PerLegPerPaxPopulatePropertyMapStrategy;
import de.hybris.platform.travelfacades.order.strategies.impl.PerLegPopulatePropertyMapStrategy;
import de.hybris.platform.travelfacades.order.strategies.impl.PerPaxPopulatePropertyMapStrategy;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.order.TravelCommerceCartService;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Junit Test Suite for {@link DefaultTravelCartFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCartFacadeTest
{
	@Mock
	TravelRulesService travelRulesService;
	@Mock
	BookingFacade bookingFacade;
	@Mock
	BundleCartFacade bundleCartFacade;
	@Mock
	Converter<PaymentOptionInfo, PaymentOptionData> paymentOptionConverter;
	@InjectMocks
	private DefaultTravelCartFacade travelCartFacade;
	@Mock
	private TravelCommerceCartService travelCommerceCartService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private CartService cartService;
	@Mock
	private TravelCartService travelCartService;
	@Mock
	private ProductService productService;
	@Mock
	private TravellerService travellerService;
	@Mock
	private TravelRouteService travelRouteService;
	@Mock
	private TransportOfferingService transportOfferingService;
	@Mock
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private AccommodationMapService accommodationMapService;
	@Mock
	private BookingService bookingService;
	@Mock
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	@Mock
	private VoucherFacade voucherFacade;
	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;
	@Mock
	private TravellerFacade travellerFacade;
	@Mock
	private SessionService sessionService;
	@Mock
	private PerLegPerPaxPopulatePropertyMapStrategy perLegPerPaxPopulatePropertyMapStrategy;
	@Mock
	private PerBookingPopulatePropertyMapStrategy perBookingPopulatePropertyMapStrategy;
	@Mock
	private PerLegPopulatePropertyMapStrategy perLegPopulatePropertyMapStrategy;
	@Mock
	private PerPaxPopulatePropertyMapStrategy perPaxPopulatePropertyMapStrategy;
	@Mock
	private ModelService modelService;
	private CurrencyModel currencyModel;


	@Before
	public void setUp()
	{
		travelCartFacade.setCommerceCartService(commerceCartService);
		travelCartFacade.setCartModificationConverter(cartModificationConverter);

		final Map<String, PopulatePropertyMapStrategy> populateCartEntryPropertyStrategyMap = new HashMap<>();
		populateCartEntryPropertyStrategyMap
				.put(AddToCartCriteriaType.PER_LEG_PER_PAX.getCode(), perLegPerPaxPopulatePropertyMapStrategy);
		populateCartEntryPropertyStrategyMap
				.put(AddToCartCriteriaType.PER_BOOKING.getCode(), perBookingPopulatePropertyMapStrategy);
		populateCartEntryPropertyStrategyMap.put(AddToCartCriteriaType.PER_LEG.getCode(), perLegPopulatePropertyMapStrategy);
		populateCartEntryPropertyStrategyMap.put(AddToCartCriteriaType.PER_PAX.getCode(), perPaxPopulatePropertyMapStrategy);

		travelCartFacade.setPopulateCartEntryPropertyStrategyMap(populateCartEntryPropertyStrategyMap);
		currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
	}

	/**
	 * Test method for addtransportOfferingToCartEntryForBundle
	 */
	@Test
	public void testAddTransportOfferingCodeToCartEntryForBundle()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(cartService.getSessionCart()).thenReturn(testDataSetUp.createCartModel(null, null));
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(testDataSetUp.createProductModel());
		when(travellerService.getExistingTraveller(Matchers.anyString())).thenReturn(testDataSetUp.createTravellerModel());
		when(transportOfferingService.getTransportOffering(Matchers.anyString()))
				.thenReturn(testDataSetUp.createTransportOfferingModel(null));
		when(travelRouteService.getTravelRoute(Matchers.anyString())).thenReturn(testDataSetUp.createTravelRouteModel());
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add("TestOfferingCodeLHR_MUN");
		travelCartFacade
				.addPropertiesToCartEntryForBundle("TestProduct123", 1, "TestRouteLHR_CDG", 0, "travellerUniqueId", Boolean.TRUE,
						AmendStatus.NEW, AddToCartCriteriaType.PER_LEG_PER_PAX.getCode());
		verify(travelCommerceCartService, times(1))
				.addPropertiesToCartEntryForBundle(Matchers.any(CartModel.class), Matchers.anyInt(), Matchers.any(ProductModel.class),
						Matchers.anyMap());
	}

	/**
	 * Test method for addtransportOfferingToCartEntryForBundle
	 */
	@Test
	public void testAddTransportOfferingCodeToCartEntryForBundleNullTravelRoute()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(testDataSetUp.createCartModel(null, null));
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(testDataSetUp.createProductModel());
		when(travellerService.getExistingTraveller(Matchers.anyString())).thenReturn(testDataSetUp.createTravellerModel());
		when(transportOfferingService.getTransportOffering(Matchers.anyString()))
				.thenReturn(testDataSetUp.createTransportOfferingModel(null));
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add("TestOfferingCodeLHR_MUN");
		travelCartFacade
				.addPropertiesToCartEntryForBundle("TestProduct123", 1, null, 0, "travellerUniqueId", Boolean.TRUE, AmendStatus.NEW,
						AddToCartCriteriaType.PER_LEG_PER_PAX.getCode());
		verify(travelCommerceCartService, times(1))
				.addPropertiesToCartEntryForBundle(Matchers.any(CartModel.class), Matchers.anyInt(), Matchers.any(ProductModel.class),
						Matchers.anyMap());
	}

	/**
	 * Test method for addtransportOfferingToCartEntry
	 */
	@Test
	public void testAddTransportOfferingCodeToCartEntry()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		when(cartService.getSessionCart()).thenReturn(testDataSetUp.createCartModel(null, null));
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(testDataSetUp.createProductModel());
		when(travellerService.getExistingTraveller(Matchers.anyString())).thenReturn(testDataSetUp.createTravellerModel());
		when(transportOfferingService.getTransportOffering(Matchers.anyString()))
				.thenReturn(testDataSetUp.createTransportOfferingModel(null));
		when(travelRouteService.getTravelRoute(Matchers.anyString())).thenReturn(testDataSetUp.createTravelRouteModel());
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add("TestOfferingCodeLHR_MUN");
		travelCartFacade
				.addPropertiesToCartEntry("HoldItemProduct123", 1, transportOfferingCodes, "TestRouteLHR_CDG", 0, "travellerUniqueId",
						Boolean.TRUE, AmendStatus.NEW, AddToCartCriteriaType.PER_LEG_PER_PAX.getCode());
		verify(travelCommerceCartService, times(1))
				.addPropertiesToCartEntry(Matchers.any(CartModel.class), Matchers.anyInt(), Matchers.any(ProductModel.class),
						Matchers.anyMap());
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(testDataSetUp.createProductModel());
		when(cartService.getSessionCart()).thenReturn(testDataSetUp.createCartModel(null, null));
		final CommerceCartModification cartModification = testDataSetUp.createCommerceCartModification();
		when(commerceCartService.addToCart(Matchers.any(CommerceCartParameter.class))).thenReturn(cartModification);
		when(cartModificationConverter.convert(cartModification)).thenReturn(testDataSetUp.createCommerceCartModificationData());
		travelCartFacade.addToCart("ORTC5", 1);
		verify(cartModificationConverter).convert(cartModification);
	}

	@Test
	public void testAddPropertiesToCartEntry()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null, null);
		when(cartService.getSessionCart()).thenReturn(cart);
		final ProductModel product = testDataSetUp.createProductModel();
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(product);
		final Map<String, Object> params = new HashMap<>();
		params.put("code", "ORTC5");
		doNothing().when(travelCommerceCartService).addPropertiesToCartEntry(cart, 1, product, params);
		travelCartFacade.addPropertiesToCartEntry("ORTC5", 1, params);
		verify(travelCommerceCartService).addPropertiesToCartEntry(cart, 1, product, params);
	}

	@Test
	public void testAddPropertiesToCartEntryEmptyParams()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null, null);
		when(cartService.getSessionCart()).thenReturn(cart);
		final ProductModel product = testDataSetUp.createProductModel();
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(product);
		final Map<String, Object> params = Collections.EMPTY_MAP;
		travelCartFacade.addPropertiesToCartEntry("ORTC5", 1, params);
	}

	@Test
	public void testaddPropertiesToCartEntryEmptyAddToCartCriteria()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		when(cartService.getSessionCart()).thenReturn(testDataSetUp.createCartModel(null, null));
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(testDataSetUp.createProductModel());
		when(travellerService.getExistingTraveller(Matchers.anyString())).thenReturn(testDataSetUp.createTravellerModel());
		when(transportOfferingService.getTransportOffering(Matchers.anyString()))
				.thenReturn(testDataSetUp.createTransportOfferingModel(null));
		when(travelRouteService.getTravelRoute(Matchers.anyString())).thenReturn(testDataSetUp.createTravelRouteModel());
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add("TestOfferingCodeLHR_MUN");
		travelCartFacade
				.addPropertiesToCartEntry("HoldItemProduct123", 1, transportOfferingCodes, "TestRouteLHR_CDG", 0, "travellerUniqueId",
						Boolean.TRUE, AmendStatus.NEW, null);
		verify(travelCommerceCartService, times(1))
				.addPropertiesToCartEntry(Matchers.any(CartModel.class), Matchers.anyInt(), Matchers.any(ProductModel.class),
						Matchers.anyMap());
	}

	@Test
	public void testAddSelectedAccommodationToCart()
	{
		final ConfiguredAccommodationModel configuredAccommodation = new ConfiguredAccommodationModel();
		when(accommodationMapService.getAccommodation("1A")).thenReturn(configuredAccommodation);
		doNothing().when(travelCommerceCartService)
				.addSelectedAccommodationToCart("EZY1234050520160735", "1234", configuredAccommodation);
		travelCartFacade.addSelectedAccommodationToCart("EZY1234050520160735", "1234", "1A");
		verify(travelCommerceCartService).addSelectedAccommodationToCart("EZY1234050520160735", "1234", configuredAccommodation);
	}

	@Test
	public void testRemoveSelectedAccommodationFromCart()
	{
		doNothing().when(travelCommerceCartService).removeSelectedAccommodationFromCart("EZY1234050520160735", "1234", "1A");
		travelCartFacade.removeSelectedAccommodationFromCart("EZY1234050520160735", "1234", "1A");
		verify(travelCommerceCartService).removeSelectedAccommodationFromCart("EZY1234050520160735", "1234", "1A");
	}

	@Test
	public void testGetOrderEntry()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null, null);
		when(cartService.getSessionCart()).thenReturn(cart);
		final AbstractOrderEntryModel orderEntryModel = testDataSetUp.createOrderEntryModel();
		final List<String> toCodes = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		when(bookingService.getOrderEntry(Matchers.eq(cart), Matchers.eq("ORTC5"), Matchers.eq("LHR_DXB"), Matchers.eq(toCodes),
				Matchers.any(List.class), Matchers.eq(true))).thenReturn(orderEntryModel);
		when(orderEntryConverter.convert(orderEntryModel)).thenReturn(new OrderEntryData());
		travelCartFacade.getOrderEntry("ORTC5", "LHR_DXB", toCodes, "1234", true);
		verify(bookingService).getOrderEntry(Matchers.eq(cart), Matchers.eq("ORTC5"), Matchers.eq("LHR_DXB"), Matchers.eq(toCodes),
				Matchers.any(List.class), Matchers.eq(true));
		verify(orderEntryConverter).convert(orderEntryModel);
	}

	@Test
	public void testGetOrderEntryNullOrderModel()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null, null);
		when(cartService.getSessionCart()).thenReturn(cart);
		final List<String> toCodes = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		when(bookingService.getOrderEntry(Matchers.eq(cart), Matchers.eq("ORTC5"), Matchers.eq("LHR_DXB"), Matchers.eq(toCodes),
				Matchers.any(List.class), Matchers.eq(true))).thenReturn(null);
		assertNull(travelCartFacade.getOrderEntry("ORTC5", "LHR_DXB", toCodes, "1234", true));
	}

	@Test
	public void testIsAmendmentCartAmendment()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final OrderModel originalOrder = new OrderModel();
		originalOrder.setCode("0001");

		final CartModel cart = testDataSetUp.createCartModel(null, null);
		cart.setOriginalOrder(originalOrder);
		when(travelCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		assertTrue(travelCartFacade.isAmendmentCart());
	}

	@Test
	public void testIsAdditionalSecurityActive()
	{
		final CartModel cart = new CartModel();
		cart.setAdditionalSecurity(Boolean.TRUE);

		when(travelCartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		assertTrue(travelCartFacade.isAdditionalSecurityActive());
	}

	@Test
	public void testIsAdditionalSecurityActiveWithNoSessionCart()
	{
		when(travelCartService.hasSessionCart()).thenReturn(Boolean.FALSE);
		assertFalse(travelCartFacade.isAdditionalSecurityActive());
	}

	@Test
	public void testIsAmendmentCartPurchase()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null, null);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		assertFalse(travelCartFacade.isAmendmentCart());
	}

	@Test
	public void testRemoveDeliveryAddress()
	{
		doNothing().when(travelCartService).removeDeliveryAddress();
		travelCartFacade.removeDeliveryAddress();
		verify(travelCartService).removeDeliveryAddress();
	}

	@Test
	public void testGetOriginalorderCode()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();

		final OrderModel originalOrder = new OrderModel();
		originalOrder.setCode("0001");

		final CartModel cart = testDataSetUp.createCartModel(null, null);
		cart.setOriginalOrder(originalOrder);
		when(travelCartService.hasSessionCart()).thenReturn(true);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		assertEquals("0001", travelCartFacade.getOriginalOrderCode());
	}

	@Test
	public void testApplyVoucher() throws VoucherOperationException, CalculationException
	{
		doNothing().when(voucherFacade).applyVoucher("ABC123");
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null, null);
		when(cartService.getSessionCart()).thenReturn(cart);
		doNothing().when(travelCommerceCartService).recalculateCart(cart);
		travelCartFacade.applyVoucher("ABC123");
		verify(voucherFacade).applyVoucher("ABC123");
		verify(cartService).getSessionCart();
		verify(travelCommerceCartService).recalculateCart(cart);
	}

	@Test
	public void testApplyVoucherException() throws VoucherOperationException, CalculationException
	{
		doNothing().when(voucherFacade).applyVoucher("ABC123");
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null, null);
		when(cartService.getSessionCart()).thenReturn(cart);
		doThrow(CalculationException.class).when(travelCommerceCartService).recalculateCart(cart);
		travelCartFacade.applyVoucher("ABC123");
		verify(voucherFacade).applyVoucher("ABC123");
		verify(cartService).getSessionCart();
		verify(travelCommerceCartService).recalculateCart(cart);
	}

	@Test
	public void testRemoveVoucher() throws VoucherOperationException, CalculationException
	{
		doNothing().when(voucherFacade).releaseVoucher("ABC123");
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartModel cart = testDataSetUp.createCartModel(null, null);
		when(cartService.getSessionCart()).thenReturn(cart);
		doNothing().when(travelCommerceCartService).recalculateCart(cart);
		travelCartFacade.removeVoucher("ABC123");
		verify(voucherFacade).releaseVoucher("ABC123");
		verify(cartService).getSessionCart();
		verify(travelCommerceCartService).recalculateCart(cart);
	}

	@Test
	public void testAddToCartBundle() throws CommerceCartModificationException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartEntryModel entry1 = testDataSetUp.createCartEntryModel(null, null);
		final CartModel cart = testDataSetUp.createCartModel("0001", Stream.of(entry1).collect(Collectors.toList()));
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cart);
		doNothing().when(cartService).removeSessionCart();

		//Setup AddBundleToCartRequestData
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final AddBundleToCartData addBundleToCartData = testDataSetUp
				.createAddBundleToCartData("ORTC5", transportOfferings, "LGW_CDG");
		final PriceLevel priceLevel = testDataSetUp.createPriceLevel("1234");
		when(travelCommercePriceFacade.getPriceLevelInfo("ORTC5", transportOfferings, "LGW_CDG")).thenReturn(priceLevel);
		final PassengerTypeQuantityData paxQuantityData = testDataSetUp.createPassengerTypeQuantityData(2, "ADULT");
		final AddBundleToCartRequestData addBundleToCartRequestData = testDataSetUp
				.createAddBundleToCartRequestData(Stream.of(addBundleToCartData).collect(Collectors.toList()),
						Stream.of(paxQuantityData).collect(Collectors.toList()));
		final TravellerData travellerData1 = testDataSetUp.createTravellerData("1111");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(1), Matchers.eq("0001"))).thenReturn(travellerData1);
		final TravellerData travellerData2 = testDataSetUp.createTravellerData("2222");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(2), Matchers.eq("0001"))).thenReturn(travellerData2);
		doNothing().when(sessionService)
				.setAttribute(Matchers.eq(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP), Matchers.anyMap());

		when(cartService.getEntryForNumber(cart, 1)).thenReturn(entry1);
		travelCartFacade.addToCartBundle(addBundleToCartRequestData);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddToCartBundleCommerceCartException() throws CommerceCartModificationException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartEntryModel entry1 = testDataSetUp.createCartEntryModel(null, null);
		final CartModel cart = testDataSetUp.createCartModel("0001", Stream.of(entry1).collect(Collectors.toList()));
		when(cartService.getSessionCart()).thenReturn(cart);
		doNothing().when(cartService).removeSessionCart();

		//Setup AddBundleToCartRequestData
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final AddBundleToCartData addBundleToCartData = testDataSetUp
				.createAddBundleToCartData("ORTC5", transportOfferings, "LGW_CDG");
		final PriceLevel priceLevel = testDataSetUp.createPriceLevel("1234");
		when(travelCommercePriceFacade.getPriceLevelInfo("ORTC5", transportOfferings, "LGW_CDG"))
				.thenThrow(CommerceCartModificationException.class);
		final PassengerTypeQuantityData paxQuantityData = testDataSetUp.createPassengerTypeQuantityData(2, "ADULT");
		final AddBundleToCartRequestData addBundleToCartRequestData = testDataSetUp
				.createAddBundleToCartRequestData(Stream.of(addBundleToCartData).collect(Collectors.toList()),
						Stream.of(paxQuantityData).collect(Collectors.toList()));

		travelCartFacade.addToCartBundle(addBundleToCartRequestData);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddToCartBundlePriceLevelNull() throws CommerceCartModificationException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartEntryModel entry1 = testDataSetUp.createCartEntryModel(null, null);
		final CartModel cart = testDataSetUp.createCartModel("0001", Stream.of(entry1).collect(Collectors.toList()));
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cart);
		doNothing().when(cartService).removeSessionCart();

		//Setup AddBundleToCartRequestData
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final AddBundleToCartData addBundleToCartData = testDataSetUp
				.createAddBundleToCartData("ORTC5", transportOfferings, "LGW_CDG");
		final PriceLevel priceLevel = testDataSetUp.createPriceLevel("1234");
		when(travelCommercePriceFacade.getPriceLevelInfo("ORTC5", transportOfferings, "LGW_CDG")).thenReturn(null);
		final PassengerTypeQuantityData paxQuantityData = testDataSetUp.createPassengerTypeQuantityData(2, "ADULT");
		final AddBundleToCartRequestData addBundleToCartRequestData = testDataSetUp
				.createAddBundleToCartRequestData(Stream.of(addBundleToCartData).collect(Collectors.toList()),
						Stream.of(paxQuantityData).collect(Collectors.toList()));
		final TravellerData travellerData1 = testDataSetUp.createTravellerData("1111");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(1), Matchers.eq("0001"))).thenReturn(travellerData1);
		final TravellerData travellerData2 = testDataSetUp.createTravellerData("2222");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(2), Matchers.eq("0001"))).thenReturn(travellerData2);
		doNothing().when(sessionService)
				.setAttribute(Matchers.eq(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP), Matchers.anyMap());

		travelCartFacade.addToCartBundle(addBundleToCartRequestData);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddToCartBundleProductNotFound() throws CommerceCartModificationException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartEntryModel entry1 = testDataSetUp.createCartEntryModel(null, null);
		final CartModel cart = testDataSetUp.createCartModel("0001", Stream.of(entry1).collect(Collectors.toList()));
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cart);
		doNothing().when(cartService).removeSessionCart();

		//Setup AddBundleToCartRequestData
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final AddBundleToCartData addBundleToCartData = testDataSetUp
				.createAddBundleToCartData("ORTC6", transportOfferings, "LGW_CDG");
		final PriceLevel priceLevel = testDataSetUp.createPriceLevel("1234");
		when(travelCommercePriceFacade.getPriceLevelInfo("ORTC6", transportOfferings, "LGW_CDG")).thenReturn(priceLevel);
		final PassengerTypeQuantityData paxQuantityData = testDataSetUp.createPassengerTypeQuantityData(2, "ADULT");
		final AddBundleToCartRequestData addBundleToCartRequestData = testDataSetUp
				.createAddBundleToCartRequestData(Stream.of(addBundleToCartData).collect(Collectors.toList()),
						Stream.of(paxQuantityData).collect(Collectors.toList()));
		final TravellerData travellerData1 = testDataSetUp.createTravellerData("1111");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(1), Matchers.eq("0001"))).thenReturn(travellerData1);
		final TravellerData travellerData2 = testDataSetUp.createTravellerData("2222");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(2), Matchers.eq("0001"))).thenReturn(travellerData2);
		doNothing().when(sessionService)
				.setAttribute(Matchers.eq(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP), Matchers.anyMap());
		Mockito.when(bundleCartFacade
				.addToCart(Matchers.anyString(), Matchers.anyLong(), Matchers.anyInt(), Matchers.anyString(), Matchers.anyBoolean()))
				.thenThrow(new CommerceCartModificationException("CommerceCartModificationException"));

		travelCartFacade.addToCartBundle(addBundleToCartRequestData);
	}

	@Test
	public void testIsProductAvailable()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ProductModel product = testDataSetUp.createProductModel();
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(product);
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final TransportOfferingModel transportOfferingModel = testDataSetUp.createTransportOfferingModel("EZY1234050620160735");
		when(transportOfferingService.getTransportOffering("EZY1234050620160735")).thenReturn(transportOfferingModel);

		final CartEntryModel entry1 = testDataSetUp
				.createCartEntryModel(Stream.of(transportOfferingModel).collect(Collectors.toList()), 1l);
		final List<AbstractOrderEntryModel> entries = Stream.of(entry1).collect(Collectors.toList());
		final List<CartEntryModel> cartEntries = Stream.of(entry1).collect(Collectors.toList());
		final CartModel cart = testDataSetUp.createCartModel("0001", entries);
		when(cartService.getSessionCart()).thenReturn(cart);
		when(travelCartService.getEntriesForProduct(cart, product)).thenReturn(cartEntries);
		when(travelCartService.getAvailableStock(product, transportOfferingModel)).thenReturn(5l);
		assertTrue(travelCartFacade.isProductAvailable("ORTC5", transportOfferings, 1l));
	}

	@Test
	public void testIsProductAvailableStockUnavailable()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ProductModel product = testDataSetUp.createProductModel();
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(product);
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final TransportOfferingModel transportOfferingModel = testDataSetUp.createTransportOfferingModel("EZY1234050620160735");
		when(transportOfferingService.getTransportOffering("EZY1234050620160735")).thenReturn(transportOfferingModel);

		final CartEntryModel entry1 = testDataSetUp
				.createCartEntryModel(Stream.of(transportOfferingModel).collect(Collectors.toList()), 1l);
		final List<AbstractOrderEntryModel> entries = Stream.of(entry1).collect(Collectors.toList());
		final List<CartEntryModel> cartEntries = Stream.of(entry1).collect(Collectors.toList());
		final CartModel cart = testDataSetUp.createCartModel("0001", entries);
		when(travelCartService.getSessionCart()).thenReturn(cart);
		when(travelCartService.getEntriesForProduct(cart, product)).thenReturn(cartEntries);
		when(travelCartService.getAvailableStock(product, transportOfferingModel)).thenReturn(1l);
		assertFalse(travelCartFacade.isProductAvailable("ORTC5", transportOfferings, 1l));
	}

	@Test
	public void testHasCartBeenAmended()
	{
		when(bookingService.hasCartBeenAmended()).thenReturn(true);
		assertTrue(travelCartFacade.hasCartBeenAmended());
	}

	@Test
	public void testBookingDueAmountWhenTotalAmountAndAmountPaidNull()
	{
		assertNull(travelCartFacade.getBookingDueAmount(null, null));
	}

	@Test
	public void testBookingDueAmountWhenTotalAmountAndAmountPaidNotNull()
	{
		final PriceData totalAmount = new PriceData();
		totalAmount.setValue(new BigDecimal(100));

		final PriceData amountPaid = new PriceData();
		amountPaid.setValue(new BigDecimal(70));

		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal(30));

		when(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt(), Matchers.anyString()))
				.thenReturn(priceData);
		final PriceData amountDue = travelCartFacade.getBookingDueAmount(totalAmount, amountPaid);
		assertEquals(new BigDecimal(30), amountDue.getValue());
	}

	@Test
	public void testEvaluateCart()
	{
		final CartModel cartModel = new CartModel();
		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setEntryNumber(1);
		final ProductModel product = new ProductModel();
		cartEntry.setProduct(product);
		final List<CartEntryModel> cartEntries = Collections.singletonList(cartEntry);

		when(travelRulesService.evaluateCart(cartModel)).thenReturn(Collections.emptyList());
		travelCartFacade.evaluateCart();

		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(travelRulesService.evaluateCart(cartModel)).thenReturn(cartEntries);
		Mockito.doNothing().when(travelCommerceCartService)
				.addPropertiesToCartEntry(Mockito.any(CartModel.class), Mockito.anyInt(), Mockito.any(ProductModel.class),
						Mockito.anyMap());

		travelCartFacade.evaluateCart();
	}

	@Test
	public void testIsCurrentCartValid()
	{
		when(cartService.hasSessionCart()).thenReturn(false);
		Assert.assertFalse(travelCartFacade.isCurrentCartValid());

		when(cartService.hasSessionCart()).thenReturn(true);
		final CartModel cart = new CartModel();
		final OrderModel order = new OrderModel();
		order.setStatus(OrderStatus.CANCELLING);
		cart.setOriginalOrder(order);
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		final List<AbstractOrderEntryModel> entries = Collections.singletonList(cartEntry);
		cart.setEntries(entries);
		when(cartService.getSessionCart()).thenReturn(cart);
		Assert.assertFalse(travelCartFacade.isCurrentCartValid());

		order.setStatus(OrderStatus.ACTIVE);
		Assert.assertTrue(travelCartFacade.isCurrentCartValid());
	}

	@Test
	public void testGetTotalToPayPrice()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal(0));

		when(cartService.hasSessionCart()).thenReturn(false);
		when(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt())).thenReturn(priceData);

		Assert.assertEquals(priceData, travelCartFacade.getTotalToPayPrice());

		when(cartService.hasSessionCart()).thenReturn(true);
		final CartModel cart = new CartModel();
		cart.setTotalPrice(150.0);
		cart.setTotalTax(15.0);
		final OrderModel order = new OrderModel();
		order.setTotalPrice(100.0);
		order.setTotalTax(10.0);
		order.setStatus(OrderStatus.CANCELLING);
		cart.setOriginalOrder(order);
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		final List<AbstractOrderEntryModel> entries = Collections.singletonList(cartEntry);
		cart.setEntries(entries);
		when(cartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getOrderTotalPaid(order)).thenReturn(BigDecimal.valueOf(200));

		Assert.assertEquals(priceData, travelCartFacade.getTotalToPayPrice());

		cart.setOriginalOrder(null);
		cart.setNet(true);
		Assert.assertEquals(priceData, travelCartFacade.getTotalToPayPrice());

	}

	@Test
	public void getTotalToPayPriceAfterChangeDates()
	{
		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		cartEntry.setActive(true);
		cartEntry.setType(OrderEntryType.ACCOMMODATION);
		cartEntry.setTotalPrice(100.0);

		final TaxValue tax = new TaxValue("VAT", 10.0, false, "GBP");
		final Collection<TaxValue> taxes = Collections.singletonList(tax);
		cartEntry.setTaxValues(taxes);

		final List<AbstractOrderEntryModel> entries = Collections.singletonList(cartEntry);
		cart.setEntries(entries);
		when(cartService.getSessionCart()).thenReturn(cart);

		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal(100));
		when(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt())).thenReturn(priceData);

		Assert.assertEquals(priceData, travelCartFacade.getTotalToPayPriceAfterChangeDates());
	}

	@Test
	public void testGetPartialPaymentAmount()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal(100));
		when(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt())).thenReturn(priceData);

		Assert.assertNull(travelCartFacade.getPartialPaymentAmount());

		final PaymentTransactionData paymentTransaction = new PaymentTransactionData();
		paymentTransaction.setTransactionAmount(100.0);
		final List<PaymentTransactionData> transactions = Collections.singletonList(paymentTransaction);

		when(sessionService.getAttribute("paymentTransactions")).thenReturn(transactions);

		Assert.assertEquals(priceData, travelCartFacade.getPartialPaymentAmount());
	}

	@Test
	public void testGetBookingTotal()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal(100));

		when(cartService.hasSessionCart()).thenReturn(false);
		when(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt())).thenReturn(priceData);
		final String originalOrderCode = "1001";

		Assert.assertEquals(priceData, travelCartFacade.getBookingTotal(originalOrderCode));

		when(sessionService.getAttribute(TravelfacadesConstants.SESSION_CHANGE_DATES)).thenReturn(originalOrderCode);

		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		cartEntry.setActive(true);
		cartEntry.setType(OrderEntryType.ACCOMMODATION);
		cartEntry.setTotalPrice(100.0);

		final TaxValue tax = new TaxValue("VAT", 10.0, false, "GBP");
		final Collection<TaxValue> taxes = Collections.singletonList(tax);
		cartEntry.setTaxValues(taxes);

		final List<AbstractOrderEntryModel> entries = Collections.singletonList(cartEntry);
		cart.setEntries(entries);
		when(cartService.getSessionCart()).thenReturn(cart);

		Assert.assertEquals(priceData, travelCartFacade.getBookingTotal(originalOrderCode));

		when(sessionService.getAttribute(TravelfacadesConstants.SESSION_PAY_NOW)).thenReturn(originalOrderCode);
		when(bookingService.hasCartBeenAmended()).thenReturn(false);
		when(bookingFacade.getOrderTotalToPayForOrderEntryType(originalOrderCode, OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.valueOf(50));
		when(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt())).thenReturn(priceData);

		Assert.assertEquals(priceData, travelCartFacade.getBookingTotal(originalOrderCode));
	}

	@Test
	public void testGetCartTotal()
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal(100));

		when(cartService.hasSessionCart()).thenReturn(false);
		when(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt())).thenReturn(priceData);
		final CartModel cart = new CartModel();
		cart.setTotalPrice(100.0);
		cart.setTotalTax(10.0);
		when(cartService.getSessionCart()).thenReturn(cart);

		Assert.assertEquals(priceData, travelCartFacade.getCartTotal());
	}

	@Test
	public void testGetPaymentOptions()
	{
		final PaymentOptionInfo paymentOption = new PaymentOptionInfo();
		final List<PaymentOptionInfo> paymentOptions = Collections.singletonList(paymentOption);
		when(travelCartService.getPaymentOptions()).thenReturn(paymentOptions);

		final PaymentOptionData paymentOptionData = new PaymentOptionData();
		when(paymentOptionConverter.convert(paymentOption)).thenReturn(paymentOptionData);

		Assert.assertTrue(travelCartFacade.getPaymentOptions().contains(paymentOptionData));
	}

	@Test
	public void testGetPaymentOptionsOrderEntryType()
	{
		final OrderEntryType orderEntryType = OrderEntryType.TRANSPORT;

		final PaymentOptionInfo paymentOption = new PaymentOptionInfo();
		final List<PaymentOptionInfo> paymentOptions = Collections.singletonList(paymentOption);
		when(travelCartService.getPaymentOptions(orderEntryType)).thenReturn(paymentOptions);

		final PaymentOptionData paymentOptionData = new PaymentOptionData();
		when(paymentOptionConverter.convert(paymentOption)).thenReturn(paymentOptionData);
		Assert.assertTrue(travelCartFacade.getPaymentOptions(orderEntryType).contains(paymentOptionData));
	}

	@Test
	public void testIsValidPaymentOption()
	{
		final PaymentTransactionData paymentTransaction = new PaymentTransactionData();
		paymentTransaction.setTransactionAmount(100.0);
		paymentTransaction.setEntryNumbers(Arrays.asList(1, 2));
		final List<PaymentTransactionData> transactions = Collections.singletonList(paymentTransaction);

		final PaymentOptionInfo paymentOption = new PaymentOptionInfo();
		final List<PaymentOptionInfo> paymentOptions = Collections.singletonList(paymentOption);
		when(travelCartService.getPaymentOptions()).thenReturn(paymentOptions);

		final PaymentOptionData paymentOptionData = new PaymentOptionData();
		final List<PaymentTransactionData> associatedTransactions = Collections.singletonList(paymentTransaction);
		paymentOptionData.setAssociatedTransactions(associatedTransactions);

		when(paymentOptionConverter.convert(paymentOption)).thenReturn(paymentOptionData);

		Assert.assertTrue(travelCartFacade.isValidPaymentOption(transactions));
	}

	@Test
	public void testGetNextBundleNumberToUse()
	{
		final Integer nextBundlenumber = 5;
		when(travelCartService.getNextBundleNumberToUse()).thenReturn(nextBundlenumber);
		Assert.assertEquals(nextBundlenumber, travelCartFacade.getNextBundleNumberToUse());
	}

	@Test
	public void testUpdateBundleEntriesWithBundleNumber()
	{
		final List<Integer> entryNumbers = Arrays.asList(1, 2);
		final Integer forcedBundleNumber = 2;
		travelCartFacade.updateBundleEntriesWithBundleNumber(entryNumbers, forcedBundleNumber);
	}

	@Test
	public void testValidateOriginDestinationRefNumbersInCart()
	{
		when(cartService.hasSessionCart()).thenReturn(false);
		Assert.assertFalse(travelCartFacade.validateOriginDestinationRefNumbersInCart());

		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		final ProductModel product = new ProductModel();
		product.setProductType(ProductType.FARE_PRODUCT);
		cartEntry.setProduct(product);
		cartEntry.setType(OrderEntryType.TRANSPORT);

		final TravelOrderEntryInfoModel travelOrderEntryInfo = new TravelOrderEntryInfoModel();
		travelOrderEntryInfo.setOriginDestinationRefNumber(0);
		cartEntry.setTravelOrderEntryInfo(travelOrderEntryInfo);

		final List<AbstractOrderEntryModel> entries = Collections.singletonList(cartEntry);
		cart.setEntries(entries);
		when(travelCartService.getSessionCart()).thenReturn(cart);

		when(cartService.hasSessionCart()).thenReturn(true);
		Assert.assertTrue(travelCartFacade.validateOriginDestinationRefNumbersInCart());
	}

	//------------------------//

	@Test
	public void testAddBundleToCart() throws CommerceCartModificationException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartEntryModel entry1 = testDataSetUp.createCartEntryModel(null, null);
		final CartModel cart = testDataSetUp.createCartModel("0001", Stream.of(entry1).collect(Collectors.toList()));
		final ProductModel product = testDataSetUp.createProductModel();
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cart);
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(testDataSetUp.createProductModel());
		doNothing().when(cartService).removeSessionCart();
		when(travelCommerceCartService.addAutoPickProductsToCart(Matchers.eq(product), Matchers.anyString(), Matchers.anyInt()))
				.thenReturn(Collections.emptyList());


		//Setup AddBundleToCartRequestData
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final AddBundleToCartData addBundleToCartData = testDataSetUp
				.createAddBundleToCartData("ORTC5", transportOfferings, "LGW_CDG");
		final PriceLevel priceLevel = testDataSetUp.createPriceLevel("1234");
		when(travelCommercePriceFacade.getPriceLevelInfo("ORTC5", transportOfferings, "LGW_CDG")).thenReturn(priceLevel);
		final PassengerTypeQuantityData paxQuantityData = testDataSetUp.createPassengerTypeQuantityData(2, "ADULT");
		final AddBundleToCartRequestData addBundleToCartRequestData = testDataSetUp
				.createAddBundleToCartRequestData(Stream.of(addBundleToCartData).collect(Collectors.toList()),
						Stream.of(paxQuantityData).collect(Collectors.toList()));
		final TravellerData travellerData1 = testDataSetUp.createTravellerData("1111");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(1), Matchers.eq("0001"))).thenReturn(travellerData1);
		final TravellerData travellerData2 = testDataSetUp.createTravellerData("2222");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(2), Matchers.eq("0001"))).thenReturn(travellerData2);
		doNothing().when(sessionService)
				.setAttribute(Matchers.eq(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP), Matchers.anyMap());

		when(cartService.getEntryForNumber(cart, 1)).thenReturn(entry1);
		final OrderEntryData orderEntryData = testDataSetUp.createOrderEntryData(1);
		final CartModificationData cartModificationData = testDataSetUp.createCartModificationData(orderEntryData);
		when(travelCartFacade
				.addProduct("ORTC5", 1, -1, addBundleToCartData, travellerData1, priceLevel, Boolean.TRUE, AmendStatus.NEW))
				.thenReturn(null);
		travelCartFacade.addBundleToCart(addBundleToCartRequestData);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddBundleToCartCommerceCartException() throws CommerceCartModificationException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartEntryModel entry1 = testDataSetUp.createCartEntryModel(null, null);
		final CartModel cart = testDataSetUp.createCartModel("0001", Stream.of(entry1).collect(Collectors.toList()));
		when(cartService.getSessionCart()).thenReturn(cart);
		doNothing().when(cartService).removeSessionCart();

		//Setup AddBundleToCartRequestData
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final AddBundleToCartData addBundleToCartData = testDataSetUp
				.createAddBundleToCartData("ORTC5", transportOfferings, "LGW_CDG");
		final PriceLevel priceLevel = testDataSetUp.createPriceLevel("1234");
		when(travelCommercePriceFacade.getPriceLevelInfo("ORTC5", transportOfferings, "LGW_CDG"))
				.thenThrow(CommerceCartModificationException.class);
		final PassengerTypeQuantityData paxQuantityData = testDataSetUp.createPassengerTypeQuantityData(2, "ADULT");
		final AddBundleToCartRequestData addBundleToCartRequestData = testDataSetUp
				.createAddBundleToCartRequestData(Stream.of(addBundleToCartData).collect(Collectors.toList()),
						Stream.of(paxQuantityData).collect(Collectors.toList()));

		travelCartFacade.addBundleToCart(addBundleToCartRequestData);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddBundleToCartPriceLevelNull() throws CommerceCartModificationException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final CartEntryModel entry1 = testDataSetUp.createCartEntryModel(null, null);
		final CartModel cart = testDataSetUp.createCartModel("0001", Stream.of(entry1).collect(Collectors.toList()));
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cart);
		doNothing().when(cartService).removeSessionCart();

		//Setup AddBundleToCartRequestData
		final List<String> transportOfferings = Stream.of("EZY1234050620160735").collect(Collectors.toList());
		final AddBundleToCartData addBundleToCartData = testDataSetUp
				.createAddBundleToCartData("ORTC5", transportOfferings, "LGW_CDG");
		final PriceLevel priceLevel = testDataSetUp.createPriceLevel("1234");
		when(travelCommercePriceFacade.getPriceLevelInfo("ORTC5", transportOfferings, "LGW_CDG")).thenReturn(null);
		final PassengerTypeQuantityData paxQuantityData = testDataSetUp.createPassengerTypeQuantityData(2, "ADULT");
		final AddBundleToCartRequestData addBundleToCartRequestData = testDataSetUp
				.createAddBundleToCartRequestData(Stream.of(addBundleToCartData).collect(Collectors.toList()),
						Stream.of(paxQuantityData).collect(Collectors.toList()));
		final TravellerData travellerData1 = testDataSetUp.createTravellerData("1111");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(1), Matchers.eq("0001"))).thenReturn(travellerData1);
		final TravellerData travellerData2 = testDataSetUp.createTravellerData("2222");
		when(travellerFacade.createTraveller(Matchers.eq(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER), Matchers.eq("ADULT"),
				Matchers.anyString(), Matchers.eq(2), Matchers.eq("0001"))).thenReturn(travellerData2);
		doNothing().when(sessionService)
				.setAttribute(Matchers.eq(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP), Matchers.anyMap());

		travelCartFacade.addBundleToCart(addBundleToCartRequestData);
	}

	private class TestDataSetUp
	{
		private CartModel createCartModel(final String code, final List<AbstractOrderEntryModel> entries)
		{
			final CartModel cart = new CartModel();
			cart.setCode(code);
			cart.setEntries(entries);
			return cart;
		}

		private CartEntryModel createCartEntryModel(final List<TransportOfferingModel> toCodes, final Long quantity)
		{
			final CartEntryModel entry = new CartEntryModel();
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			orderEntryInfo.setTransportOfferings(toCodes);
			entry.setQuantity(quantity);
			entry.setTravelOrderEntryInfo(orderEntryInfo);
			return entry;
		}

		private ProductModel createProductModel()
		{
			final ProductModel product = new ProductModel();
			return product;
		}

		private TravellerModel createTravellerModel()
		{
			final TravellerModel traveller = new TravellerModel();
			return traveller;
		}

		private TravelRouteModel createTravelRouteModel()
		{
			final TravelRouteModel travelRoute = new TravelRouteModel();
			return travelRoute;
		}

		private CommerceCartModification createCommerceCartModification()
		{
			final CommerceCartModification cartModification = new CommerceCartModification();
			return cartModification;
		}

		private CartModificationData createCommerceCartModificationData()
		{
			final CartModificationData cartModification = new CartModificationData();
			return cartModification;
		}

		private AbstractOrderEntryModel createOrderEntryModel()
		{
			final OrderEntryModel orderEntryModel = new OrderEntryModel();
			return orderEntryModel;
		}

		private AddBundleToCartRequestData createAddBundleToCartRequestData(final List<AddBundleToCartData> addBundleToCartData,
				final List<PassengerTypeQuantityData> passengerTypes)
		{
			final AddBundleToCartRequestData addBundleToCart = new AddBundleToCartRequestData();
			addBundleToCart.setAddBundleToCartData(addBundleToCartData);
			addBundleToCart.setPassengerTypes(passengerTypes);
			return addBundleToCart;
		}

		private AddBundleToCartData createAddBundleToCartData(final String productId, final List<String> transportOfferings,
				final String routeCode)
		{
			final AddBundleToCartData addBundleToCartData = new AddBundleToCartData();
			addBundleToCartData.setProductCode(productId);
			addBundleToCartData.setTransportOfferings(transportOfferings);
			addBundleToCartData.setTravelRouteCode(routeCode);
			addBundleToCartData.setOriginDestinationRefNumber(1);
			return addBundleToCartData;
		}

		private PassengerTypeQuantityData createPassengerTypeQuantityData(final int quantity, final String code)
		{
			final PassengerTypeQuantityData paxQuantityTypeData = new PassengerTypeQuantityData();
			paxQuantityTypeData.setQuantity(quantity);
			paxQuantityTypeData.setPassengerType(createPassengerTypeData(code));
			return paxQuantityTypeData;
		}

		private PassengerTypeData createPassengerTypeData(final String code)
		{
			final PassengerTypeData paxType = new PassengerTypeData();
			paxType.setCode(code);
			return paxType;
		}

		private PriceLevel createPriceLevel(final String code)
		{
			final PriceLevel priceLevel = new PriceLevel();
			priceLevel.setCode(code);
			return priceLevel;
		}

		private TravellerData createTravellerData(final String code)
		{
			final TravellerData traveller = new TravellerData();
			traveller.setLabel(code);
			return traveller;
		}

		private CartModificationData createCartModificationData(final OrderEntryData entry)
		{
			final CartModificationData cartModificationData = new CartModificationData();
			cartModificationData.setEntry(entry);
			return cartModificationData;
		}

		private OrderEntryData createOrderEntryData(final int bundleNumber)
		{
			final OrderEntryData entry = new OrderEntryData();
			entry.setEntryNumber(1);
			entry.setBundleNo(bundleNumber);
			return entry;
		}

		private TransportOfferingModel createTransportOfferingModel(final String code)
		{
			final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
			transportOfferingModel.setCode(code);
			return transportOfferingModel;
		}

	}


}