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

package de.hybris.platform.travelfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.RoomRateCartData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelfacades.facades.accommodation.forms.AccommodationAddToCartForm;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.order.AccommodationCommerceCartService;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.BookingService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


/**
 * Unit Test for the implementation of {@link DefaultAccommodationCartFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationCartFacadeTest
{
	@InjectMocks
	DefaultAccommodationCartFacade defaultAccommodationCartFacade;
	@Mock
	private AccommodationCommerceCartService accommodationCommerceCartService;
	@Mock
	private BookingService bookingService;
	@Mock
	private TravelCartService travelCartService;
	@Mock
	private TravelCartFacade cartFacade;
	@Mock
	private BundleCartFacade bundleCartFacade;
	@Mock
	private ModelService modelService;
	@Mock
	private CartService cartService;
	@Mock
	private ProductService productService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel;
	@Mock
	private ProductModel productModel;
	@Mock
	private CartEntryModel cartEntryModel;
	@Mock
	EnumerationService enumerationService;

	@Test
	public void testAddAccommodationToCart() throws CommerceCartModificationException
	{
		defaultAccommodationCartFacade.setCartModificationConverter(cartModificationConverter);
		final Date checkInDate = new Date(2017, 01, 01);
		final Date checkOutDate = new Date(2017, 01, 02);
		final String accommodationOfferingCode = "accommodationOfferingCode";
		final String accommodationCode = "accommodationCode";
		final RoomRateCartData roomRateCartData = new RoomRateCartData();
		roomRateCartData.setCode("roomRateCartCode");
		roomRateCartData.setCardinality(1);
		final List<RoomRateCartData> rates = Collections.singletonList(roomRateCartData);
		final int numberOfRooms = 1;
		final String ratePlanCode = "ratePlanCode";
		final ProductModel product=new ProductModel();
		final CartModel cartModel = new CartModel();
		final CommerceCartModification commerceCartModification=new CommerceCartModification();
		final CartModificationData cartModificationData=new CartModificationData();

		given(accommodationCommerceCartService.getMaxRoomStayRefNumber()).willReturn(0);
		given(productService.getProductForCode("roomRateCartCode")).willReturn(product);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(commerceCartService.addToCart(Matchers.any(CommerceCartParameter.class))).willReturn(commerceCartModification);
		given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);

		cartModificationData.setQuantityAdded(0);
		try
		{
		defaultAccommodationCartFacade.addAccommodationToCart(checkInDate, checkOutDate, accommodationOfferingCode,
				accommodationCode, rates, numberOfRooms, ratePlanCode);
		}
		catch (final Exception e)
		{
			Assert.assertTrue(e instanceof CommerceCartModificationException);
		}

		cartModificationData.setQuantityAdded(1);
		final OrderEntryData oed = new OrderEntryData();
		oed.setEntryNumber(1);
		cartModificationData.setEntry(oed);
		BDDMockito.willDoNothing().given(accommodationCommerceCartService).rollbackAccommodationEntries(accommodationCode,
				accommodationOfferingCode, ratePlanCode);
		BDDMockito.willDoNothing().given(accommodationCommerceCartService).createOrderEntryGroup(Matchers.anyList(),
				Matchers.any(Date.class), Matchers.any(Date.class), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyInt());
		BDDMockito.willDoNothing().given(accommodationCommerceCartService)
				.populateAccommodationDetailsOnRoomRateEntry(Matchers.anyInt(), Matchers.anyListOf(Date.class));

		defaultAccommodationCartFacade.addAccommodationToCart(checkInDate, checkOutDate, accommodationOfferingCode,
				accommodationCode, rates,
				numberOfRooms, ratePlanCode);
	}

	@Test
	public void testAddAccommodationsToCart() throws CommerceCartModificationException
	{

		given(accommodationCommerceCartService.getNewAccommodationOrderEntryGroups(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString())).willReturn(Stream.of(accommodationOrderEntryGroupModel).collect(Collectors.toList()));
		given(accommodationCommerceCartService.getMaxRoomStayRefNumber()).willReturn(0);
		defaultAccommodationCartFacade.setCartModificationConverter(cartModificationConverter);
		final Date checkInDate = new Date(2017, 01, 01);
		final Date checkOutDate = new Date(2017, 01, 02);
		final String accommodationOfferingCode = "accommodationOfferingCode";
		final String accommodationCode = "accommodationCode";
		final RoomRateCartData roomRateCartData = new RoomRateCartData();
		roomRateCartData.setCode("roomRateCartCode");
		roomRateCartData.setCardinality(0);
		final List<RoomRateCartData> rates = Collections.singletonList(roomRateCartData);
		final int numberOfRooms = 2;
		final String ratePlanCode = "ratePlanCode";
		final ProductModel product = new ProductModel();
		final UnitModel unit = new UnitModel();
		unit.setUnitType("pieces");
		product.setUnit(unit);
		given(productService.getProductForCode("roomRateCartCode")).willReturn(product);
		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);
		final CommerceCartModification commerceCartModification = new CommerceCartModification();
		final CartModificationData cartModificationData = new CartModificationData();
		given(commerceCartService.addToCart(Matchers.any(CommerceCartParameter.class))).willReturn(commerceCartModification);
		given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);


		cartModificationData.setQuantityAdded(1);
		final OrderEntryData oed = new OrderEntryData();
		oed.setEntryNumber(1);
		cartModificationData.setEntry(oed);

		BDDMockito.willDoNothing().given(accommodationCommerceCartService)
				.populateAccommodationDetailsOnRoomRateEntry(Matchers.anyInt(), Matchers.anyListOf(Date.class));

		BDDMockito.willDoNothing().given(accommodationCommerceCartService).createOrderEntryGroup(Matchers.anyList(),
				Matchers.any(Date.class), Matchers.any(Date.class), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyInt());


		cartModificationData.setQuantityAdded(0);
		try
		{
			defaultAccommodationCartFacade.addAccommodationsToCart(checkInDate, checkOutDate, accommodationOfferingCode,
					accommodationCode, rates, numberOfRooms, ratePlanCode);
		}
		catch (final Exception e)
		{
			Assert.assertTrue(e instanceof CommerceCartModificationException);
		}

		defaultAccommodationCartFacade.addAccommodationsToCart(checkInDate, checkOutDate, accommodationOfferingCode,
				accommodationCode, rates, numberOfRooms, ratePlanCode);
	}

	@Test
	public void testRemoveAccommodationsToCart() throws CommerceCartModificationException
	{

		given(accommodationCommerceCartService.getNewAccommodationOrderEntryGroups(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString())).willReturn(Stream.of(accommodationOrderEntryGroupModel).collect(Collectors.toList()));
		given(accommodationCommerceCartService.getMaxRoomStayRefNumber()).willReturn(0);
		defaultAccommodationCartFacade.setCartModificationConverter(cartModificationConverter);
		final Date checkInDate = new Date(2017, 01, 01);
		final Date checkOutDate = new Date(2017, 01, 02);
		final String accommodationOfferingCode = "accommodationOfferingCode";
		final String accommodationCode = "accommodationCode";
		final RoomRateCartData roomRateCartData = new RoomRateCartData();
		roomRateCartData.setCode("roomRateCartCode");
		roomRateCartData.setCardinality(0);
		final List<RoomRateCartData> rates = Collections.singletonList(roomRateCartData);
		final int numberOfRooms = 0;
		final String ratePlanCode = "ratePlanCode";
		final ProductModel product = new ProductModel();
		final UnitModel unit = new UnitModel();
		unit.setUnitType("pieces");
		product.setUnit(unit);
		given(productService.getProductForCode("roomRateCartCode")).willReturn(product);
		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);
		final CommerceCartModification commerceCartModification = new CommerceCartModification();
		final CartModificationData cartModificationData = new CartModificationData();
		given(commerceCartService.addToCart(Matchers.any(CommerceCartParameter.class))).willReturn(commerceCartModification);
		given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);


		cartModificationData.setQuantityAdded(1);
		final OrderEntryData oed = new OrderEntryData();
		oed.setEntryNumber(1);
		cartModificationData.setEntry(oed);

		BDDMockito.willDoNothing().given(accommodationCommerceCartService)
				.populateAccommodationDetailsOnRoomRateEntry(Matchers.anyInt(), Matchers.anyListOf(Date.class));

		BDDMockito.willDoNothing().given(accommodationCommerceCartService).createOrderEntryGroup(Matchers.anyList(),
				Matchers.any(Date.class), Matchers.any(Date.class), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyInt());

		BDDMockito.willDoNothing().given(accommodationCommerceCartService).removeAccommodationOrderEntryGroups(Matchers.anyList(),
				Matchers.anyInt());


		cartModificationData.setQuantityAdded(0);

		defaultAccommodationCartFacade.addAccommodationsToCart(checkInDate, checkOutDate, accommodationOfferingCode,
				accommodationCode, rates, numberOfRooms, ratePlanCode);
	}

	@Test
	public void testCleanUpCartBeforeAddition()
	{
		BDDMockito.willDoNothing().given(accommodationCommerceCartService).cleanupCartBeforeAddition(Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString());
		final String accommodationOfferingCode = "accommodationOfferingCode";
		final String checkInDateTime = "26/12/2016";
		final String checkOutDateTime = "28/12/2016";
		defaultAccommodationCartFacade.cleanUpCartBeforeAddition(accommodationOfferingCode, checkInDateTime, checkOutDateTime);
		verify(accommodationCommerceCartService).cleanupCartBeforeAddition(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString());
	}

	@Test
	public void testValidateNumberOfRoomsToAdd()
	{

		given(accommodationCommerceCartService.getNumberOfEntryGroupsInCart()).willReturn(1);
		given(accommodationCommerceCartService.getNewAccommodationOrderEntryGroups(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString())).willReturn(Stream.of(accommodationOrderEntryGroupModel).collect(Collectors.toList()));

		final String accommodationOfferingCode = "accommodationOfferingCode";
		final String accommodationCode = "accommodationCode";
		final String ratePlanCode = "ratePlanCode";
		Assert.assertTrue(defaultAccommodationCartFacade.validateNumberOfRoomsToAdd(accommodationOfferingCode, accommodationCode,
				ratePlanCode, 1, 3));

	}

	@Test
	public void testIfccommodationsAddedToCart()
	{
		try
		{
		final String accommodationOfferingCode = "accommodationOfferingCode";
		final String accommodationCode = "accommodationCode";
		final RoomRateCartData roomRateCartData = new RoomRateCartData();
		roomRateCartData.setCode("roomRateCartCode");
		roomRateCartData.setCardinality(0);
		final List<RoomRateCartData> rates = Collections.singletonList(roomRateCartData);
		final String ratePlanCode = "ratePlanCode";
		final String paymentType = "card";

		given(accommodationCommerceCartService.amendOrderEntryGroup(Matchers.any(), Matchers.any(), Matchers.any()))
					.willReturn(true);


		final ProductModel product = new ProductModel();
		final UnitModel unit = new UnitModel();
		unit.setUnitType("pieces");
		product.setUnit(unit);
		given(productService.getProductForCode("roomRateCartCode")).willReturn(product);
		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);
		final CommerceCartModification commerceCartModification = new CommerceCartModification();
		final CartModificationData cartModificationData = new CartModificationData();
		given(commerceCartService.addToCart(Matchers.any(CommerceCartParameter.class))).willReturn(commerceCartModification);
			defaultAccommodationCartFacade.setCartModificationConverter(cartModificationConverter);
		given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);
			BDDMockito.willDoNothing().given(accommodationCommerceCartService)
					.populateAccommodationDetailsOnRoomRateEntry(Matchers.anyInt(), Matchers.anyListOf(Date.class));

		cartModificationData.setQuantityAdded(1);
		final OrderEntryData oed = new OrderEntryData();
		oed.setEntryNumber(1);
		cartModificationData.setEntry(oed);

			Assert.assertTrue(defaultAccommodationCartFacade.addAccommodationsToCart(accommodationOrderEntryGroupModel,
					accommodationOfferingCode, accommodationCode, rates, 1, ratePlanCode, paymentType));
		}
		catch (final CommerceCartModificationException e)
		{
			Assert.assertTrue(e instanceof CommerceCartModificationException);
		}

	}

	@Test
	public void testAddProductsToCartWithOrderEntries()
	{
		try
		{
			final CartModel cartModel = new CartModel();
			given(cartService.getSessionCart()).willReturn(cartModel);
			given(bookingService.getAccommodationOrderEntryGroup(Matchers.anyInt(), Matchers.any(AbstractOrderModel.class)))
					.willReturn(accommodationOrderEntryGroupModel);
			final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
			final ProductModel product = new ProductModel();
			product.setCode("product1");
			orderEntry.setProduct(product);
			orderEntry.setEntryNumber(1);
			given(accommodationOrderEntryGroupModel.getEntries()).willReturn(Stream.of(orderEntry).collect(Collectors.toList()));

			given(productService.getProductForCode("product1")).willReturn(productModel);
			given(productModel.getUnit()).willReturn(Mockito.mock(UnitModel.class));
			final CommerceCartModification commerceCartModification = new CommerceCartModification();

			given(commerceCartService.addToCart(Matchers.any(CommerceCartParameter.class))).willReturn(commerceCartModification);
			defaultAccommodationCartFacade.setCartModificationConverter(cartModificationConverter);
			final CartModificationData cartModificationData = new CartModificationData();
			given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);

			cartModificationData.setQuantityAdded(1);
			final OrderEntryData oed = new OrderEntryData();
			oed.setEntryNumber(1);
			cartModificationData.setEntry(oed);

			given(cartService.getEntryForNumber(cartModel, cartModificationData.getEntry().getEntryNumber()))
					.willReturn(cartEntryModel);

			defaultAccommodationCartFacade.addProductToCart("product1", 0, 1);

		}
		catch (final CommerceCartModificationException e)
		{
			Assert.assertTrue(e instanceof CommerceCartModificationException);
		}

	}


	@Test
	public void testAddProductsToCartWithoutOrderEntries()
	{
		try
		{
			final CartModel cartModel = new CartModel();
			given(cartService.getSessionCart()).willReturn(cartModel);
			given(bookingService.getAccommodationOrderEntryGroup(Matchers.anyInt(), Matchers.any(AbstractOrderModel.class)))
					.willReturn(accommodationOrderEntryGroupModel);
			final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
			final ProductModel product = new ProductModel();
			product.setCode("product1");
			orderEntry.setProduct(product);
			orderEntry.setEntryNumber(1);
			given(accommodationOrderEntryGroupModel.getEntries()).willReturn(Collections.emptyList());

			given(productService.getProductForCode("product1")).willReturn(productModel);
			given(productModel.getUnit()).willReturn(Mockito.mock(UnitModel.class));
			final CommerceCartModification commerceCartModification = new CommerceCartModification();

			given(commerceCartService.addToCart(Matchers.any(CommerceCartParameter.class))).willReturn(commerceCartModification);
			defaultAccommodationCartFacade.setCartModificationConverter(cartModificationConverter);
			final CartModificationData cartModificationData = new CartModificationData();
			given(cartModificationConverter.convert(commerceCartModification)).willReturn(cartModificationData);

			cartModificationData.setQuantityAdded(1);
			final OrderEntryData oed = new OrderEntryData();
			oed.setEntryNumber(1);
			cartModificationData.setEntry(oed);

			given(cartService.getEntryForNumber(cartModel, cartModificationData.getEntry().getEntryNumber()))
					.willReturn(cartEntryModel);

			BDDMockito.willDoNothing().given(modelService).save(accommodationOrderEntryGroupModel);

			defaultAccommodationCartFacade.addProductToCart("product1", 0, 1);

		}
		catch (final CommerceCartModificationException e)
		{
			Assert.assertTrue(e instanceof CommerceCartModificationException);
		}

	}

	@Test
	public void testCollectRoomRates()
	{
		final AccommodationAddToCartForm form = new AccommodationAddToCartForm();
		form.setRoomRateCodes(Stream.of("roomRate1", "roomRate2", "roomRate1").collect(Collectors.toList()));
		form.setRoomRateDates(Stream.of("26/12/2016", "27/12/2016", "28/12/2016").collect(Collectors.toList()));
		Assert.assertNotNull(defaultAccommodationCartFacade.collectRoomRates(form));
	}

	@Test
	public void testCleanUpCartBeforeAdditionWithRoomStay()
	{
		final String accommodationOfferingCode = "accommodationOfferingCode";
		final String checkInDateTime = "26/12/2016";
		final String checkOutDateTime = "28/12/2016";
		final RoomStayCandidateData roomStayCandidate = new RoomStayCandidateData();
		roomStayCandidate.setRoomStayCandidateRefNumber(0);
		final List<RoomStayCandidateData> roomStayCandidates = Collections.singletonList(roomStayCandidate);

		given(cartService.hasSessionCart()).willReturn(false);

		defaultAccommodationCartFacade.cleanUpCartBeforeAddition(accommodationOfferingCode, checkInDateTime, checkOutDateTime,
				roomStayCandidates);

		given(cartService.hasSessionCart()).willReturn(true);
		final CartModel cart = new CartModel();
		given(cartService.getSessionCart()).willReturn(cart);
		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(1);
		final List<AccommodationOrderEntryGroupModel> entryGroups=Collections.singletonList(entryGroup);
		given(bookingService.getAccommodationOrderEntryGroups(cart)).willReturn(entryGroups);
		given(accommodationCommerceCartService.validateCart(accommodationOfferingCode, checkInDateTime, checkOutDateTime,
				entryGroups)).willReturn(true);

		defaultAccommodationCartFacade.cleanUpCartBeforeAddition(accommodationOfferingCode, checkInDateTime, checkOutDateTime,
				roomStayCandidates);
	}

	@Test
	public void testIsAmendmentForServices()
	{
		final CartModel cart=new CartModel();
		given(travelCartService.getSessionCart()).willReturn(cart);
		final List<AccommodationOrderEntryGroupModel> currentCartAccommodationOrderEntryGroupModels=Collections.singletonList(accommodationOrderEntryGroupModel);
		given(bookingService.getAccommodationOrderEntryGroups(cart)).willReturn(currentCartAccommodationOrderEntryGroupModels);

		Assert.assertFalse(defaultAccommodationCartFacade.isAmendmentForServices());

		final OrderModel order = new OrderModel();
		cart.setOriginalOrder(order);

		given(bookingService.getAccommodationOrderEntryGroups(order)).willReturn(currentCartAccommodationOrderEntryGroupModels);

		Assert.assertTrue(defaultAccommodationCartFacade.isAmendmentForServices());
	}

	@Test
	public void testAddAccommodationBundleToCart() throws CommerceCartModificationException
	{
		final AccommodationBundleTemplateModel accommodationBundleTemplateModel=new AccommodationBundleTemplateModel();
		final AddDealToCartData addDealToCartData=new AddDealToCartData();

		addDealToCartData.setStartingDate(new GregorianCalendar(2017, 6, 26).getTime());
		addDealToCartData.setEndingDate(new GregorianCalendar(2017, 6, 28).getTime());

		final RoomRateProductModel roomRate = new RoomRateProductModel();
		roomRate.setCode("RoomRateCode");
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(new GregorianCalendar(2017, 6, 25).getTime());
		dateRange.setEndingDate(new GregorianCalendar(2017, 6, 30).getTime());
		final List<DateRangeModel> dateRanges = Collections.singletonList(dateRange);
		roomRate.setDateRanges(dateRanges);
		final List<DayOfWeek> dayOfWeeks = Collections.singletonList(DayOfWeek.MONDAY);
		roomRate.setDaysOfWeek(dayOfWeeks);

		final List<ProductModel> products = Collections.singletonList(roomRate);
		accommodationBundleTemplateModel.setProducts(products);

		final AccommodationOfferingModel accommodationOffering = new AccommodationOfferingModel();
		accommodationOffering.setCode("accommodationOfferingCode");
		accommodationBundleTemplateModel.setAccommodationOffering(accommodationOffering);

		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setCode("ratePlanCode");
		accommodationBundleTemplateModel.setRatePlan(ratePlan);

		final AccommodationModel accommodation = new AccommodationModel();
		accommodation.setCode("accommodationCode");
		accommodationBundleTemplateModel.setAccommodation(accommodation);

		given(enumerationService.getEnumerationValue(Mockito.eq(DayOfWeek.class), Mockito.anyString()))
				.willReturn(DayOfWeek.MONDAY);
		final CartModificationData cartModification = new CartModificationData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setEntryNumber(1);
		cartModification.setEntry(entry);
		final List<CartModificationData> cartModifications = Collections.singletonList(cartModification);
		given(bundleCartFacade.startBundle(Matchers.anyString(), Matchers.anyString(), Matchers.anyLong()))
				.willReturn(cartModification);
		given(bundleCartFacade.addToCart(Matchers.anyString(), Matchers.anyLong(), Matchers.anyInt())).willReturn(cartModification);
		Assert.assertTrue(CollectionUtils.isNotEmpty(
				defaultAccommodationCartFacade.addAccommodationBundleToCart(accommodationBundleTemplateModel, addDealToCartData)));
	}

	@Test(expected=CommerceCartModificationException.class)
	public void testAddAccommodationBundleToCartWithException() throws CommerceCartModificationException
	{
		final AccommodationBundleTemplateModel accommodationBundleTemplateModel=new AccommodationBundleTemplateModel();
		final AddDealToCartData addDealToCartData=new AddDealToCartData();

		addDealToCartData.setStartingDate(new GregorianCalendar(2017, 6, 26).getTime());
		addDealToCartData.setEndingDate(new GregorianCalendar(2017, 6, 28).getTime());

		final RoomRateProductModel roomRate = new RoomRateProductModel();
		roomRate.setCode("RoomRateCode");
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(new GregorianCalendar(2017, 6, 25).getTime());
		dateRange.setEndingDate(new GregorianCalendar(2017, 6, 30).getTime());
		final List<DateRangeModel> dateRanges = Collections.singletonList(dateRange);
		roomRate.setDateRanges(dateRanges);
		final List<DayOfWeek> dayOfWeeks = Collections.singletonList(DayOfWeek.MONDAY);
		roomRate.setDaysOfWeek(dayOfWeeks);

		final List<ProductModel> products = Collections.singletonList(roomRate);
		accommodationBundleTemplateModel.setProducts(products);

		defaultAccommodationCartFacade.addAccommodationBundleToCart(accommodationBundleTemplateModel, addDealToCartData);
	}

	@Test
	public void testIsNewRoomInCart()
	{
		given(accommodationCommerceCartService.isNewRoomInCart()).willReturn(true);
		Assert.assertTrue(defaultAccommodationCartFacade.isNewRoomInCart());
	}

	@Test
	public void testRemoveAccommodationOrderEntryGroup()
	{
		final int roomStayReference = 1;
		given(accommodationCommerceCartService.removeAccommodationOrderEntryGroup(roomStayReference)).willReturn(true);
		Assert.assertTrue(defaultAccommodationCartFacade.removeAccommodationOrderEntryGroup(roomStayReference));
	}

	@Test
	public void testValidateAccommodationCart()
	{
		given(accommodationCommerceCartService.getNumberOfEntryGroupsInCart()).willReturn(1);
		Assert.assertTrue(defaultAccommodationCartFacade.validateAccommodationCart());
	}

	@Test
	public void testGetCurrentAccommodationOffering()
	{
		given(accommodationCommerceCartService.getCurrentAccommodationOffering()).willReturn("accommodationOffering");
		Assert.assertEquals("accommodationOffering", defaultAccommodationCartFacade.getCurrentAccommodationOffering());
	}

}
