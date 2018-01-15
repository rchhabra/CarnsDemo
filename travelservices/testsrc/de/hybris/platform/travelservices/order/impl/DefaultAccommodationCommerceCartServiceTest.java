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

package de.hybris.platform.travelservices.order.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.payment.changedates.ChangeDatesPaymentActionStrategy;
import de.hybris.platform.travelservices.strategies.payment.changedates.impl.ChangeDatesRefundAction;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;




/**
 * unit test for {@link DefaultAccommodationCommerceCartService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationCommerceCartServiceTest
{
	@InjectMocks
	DefaultAccommodationCommerceCartService defaultAccommodationCommerceCartService;

	@Mock
	private CartService cartService;

	@Mock
	private BookingService bookingService;

	@Mock
	private ProductService productService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private AccommodationOfferingService accommodationOfferingService;

	@Mock
	private ModelService modelService;

	@Mock
	private ChangeDatesRefundAction changeDatesRefundAction;

	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	private Map<String, ChangeDatesPaymentActionStrategy> changeDatesPaymentActionStrategyMap;

	private final String TEST_ACCOMMODATION_CODE_A = "TEST_ACCOMMODATION_CODE_A";
	private final String TEST_ACCOMMODATION_OFFERING_CODE_A = "TEST_ACCOMMODATION_OFFERING_CODE_A";
	private final String TEST_RATE_PLAN_CODE_A = "TEST_RATE_PLAN_CODE_A";

	private final String TEST_ACCOMMODATION_CODE_B = "TEST_ACCOMMODATION_CODE_B";
	private final String TEST_RATE_PLAN_CODE_B = "TEST_RATE_PLAN_CODE_B";

	@Test
	public void testPopulateAccommodationDetailsOnRoomRateEntry()
	{
		final CartEntryModel cartEntryModel = new CartEntryModel();
		final AccommodationOrderEntryInfoModel orderEntryInfo = new AccommodationOrderEntryInfoModel();
		when(cartService.getSessionCart()).thenReturn(new CartModel());
		when(cartService.getEntryForNumber(Matchers.any(CartModel.class), Matchers.anyInt())).thenReturn(cartEntryModel);
		when(modelService.create(AccommodationOrderEntryInfoModel.class)).thenReturn(orderEntryInfo);

		defaultAccommodationCommerceCartService.populateAccommodationDetailsOnRoomRateEntry(0,
				Collections.singletonList(new Date()));
	}

	@Test
	public void testPopulateAccommodationDetailsOnRoomRateEntryForNullAccommodationOrderEntryInfoModel()
	{
		final AccommodationOrderEntryInfoModel orderEntryInfo = new AccommodationOrderEntryInfoModel();
		final CartEntryModel cartEntryModel = new CartEntryModel();
		cartEntryModel.setAccommodationOrderEntryInfo(orderEntryInfo);
		when(cartService.getSessionCart()).thenReturn(new CartModel());
		when(cartService.getEntryForNumber(Matchers.any(CartModel.class), Matchers.anyInt())).thenReturn(cartEntryModel);

		defaultAccommodationCommerceCartService.populateAccommodationDetailsOnRoomRateEntry(0,
				Collections.singletonList(new Date()));

	}

	@Test
	public void testCreateOrderEntryGroup()
	{
		final AccommodationOrderEntryGroupModel orderEntryGroup = new AccommodationOrderEntryGroupModel();
		when(modelService.create(AccommodationOrderEntryGroupModel.class)).thenReturn(orderEntryGroup);
		when(productService.getProductForCode(Matchers.anyString())).thenReturn(new AccommodationModel());
		when(accommodationOfferingService.getAccommodationOffering(Matchers.anyString()))
				.thenReturn(new AccommodationOfferingModel());
		when(categoryService.getCategoryForCode(Matchers.anyString())).thenReturn(new RatePlanModel());
		defaultAccommodationCommerceCartService.createOrderEntryGroup(Collections.singletonList(0), new Date(), new Date(),
				"TEST_ACC_OFF_CODE", "TEST_ACC_CODE", "TEST_RATE_PLAN_CODE", 1);
	}

	@Test
	public void testAmendOrderEntryGroup()
	{
		changeDatesPaymentActionStrategyMap = new HashMap<>();
		changeDatesPaymentActionStrategyMap.put("REFUND", changeDatesRefundAction);
		defaultAccommodationCommerceCartService.setChangeDatesPaymentActionStrategyMap(changeDatesPaymentActionStrategyMap);
		final AccommodationOrderEntryGroupModel aoegm = new AccommodationOrderEntryGroupModel();
		when(changeDatesRefundAction.takeAction(aoegm, Collections.singletonList(0))).thenReturn(Boolean.TRUE);
		Assert.assertTrue(
				defaultAccommodationCommerceCartService.amendOrderEntryGroup(aoegm, Collections.singletonList(0), "REFUND"));
	}

	@Test
	public void testRollbackAccommodationEntriesForEmptyGroups()
	{
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(Collections.emptyList());
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.rollbackAccommodationEntries(TEST_ACCOMMODATION_CODE_A,
				TEST_ACCOMMODATION_OFFERING_CODE_A, TEST_RATE_PLAN_CODE_A);
	}

	@Test
	public void testRollbackAccommodationEntries()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelB = new AccommodationOrderEntryGroupModel();

		final AccommodationModel accommodationA = new AccommodationModel();
		accommodationA.setCode(TEST_ACCOMMODATION_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodation(accommodationA);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final RatePlanModel ratePlanA = new RatePlanModel();
		ratePlanA.setCode(TEST_RATE_PLAN_CODE_A);
		accommodationOrderEntryGroupModelA.setRatePlan(ratePlanA);



		final AccommodationModel accommodationB = new AccommodationModel();
		accommodationB.setCode(TEST_ACCOMMODATION_CODE_B);
		accommodationOrderEntryGroupModelB.setAccommodation(accommodationB);

		final AccommodationOfferingModel accommodationOfferingModelB = new AccommodationOfferingModel();
		accommodationOfferingModelB.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelB.setAccommodationOffering(accommodationOfferingModelB);
		final RatePlanModel ratePlanB = new RatePlanModel();
		ratePlanB.setCode(TEST_RATE_PLAN_CODE_B);
		accommodationOrderEntryGroupModelB.setRatePlan(ratePlanB);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);
		accommodationOrderEntryGroupModelB.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelB);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.rollbackAccommodationEntries(TEST_ACCOMMODATION_CODE_A,
				TEST_ACCOMMODATION_OFFERING_CODE_A, TEST_RATE_PLAN_CODE_A);
	}


	@Test
	public void testCleanupCartBeforeAdditionForEmptyEntryGroup()
	{
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(Collections.emptyList());
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.cleanupCartBeforeAddition(TEST_ACCOMMODATION_OFFERING_CODE_A, "30/12/216",
				"30/12/216");

	}

	@Test
	public void testCleanupCartBeforeAdditionForNullCheckInDate()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final Date checkInDate = TravelDateUtils.convertStringDateToDate("30/12/2016", TravelservicesConstants.DATE_PATTERN);
		final Date checkOutDate = TravelDateUtils.convertStringDateToDate("31/12/2016", TravelservicesConstants.DATE_PATTERN);
		accommodationOrderEntryGroupModelA.setStartingDate(checkInDate);
		accommodationOrderEntryGroupModelA.setEndingDate(checkOutDate);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.cleanupCartBeforeAddition(TEST_ACCOMMODATION_OFFERING_CODE_A, "30/12/216",
				"30/12/216");

	}

	@Test
	public void testCleanupCartBeforeAdditionForNullCheckOutDate()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final Date checkInDate = TravelDateUtils.convertStringDateToDate("30/12/2016", TravelservicesConstants.DATE_PATTERN);
		final Date checkOutDate = TravelDateUtils.convertStringDateToDate("31/12/2016", TravelservicesConstants.DATE_PATTERN);
		accommodationOrderEntryGroupModelA.setStartingDate(checkInDate);
		accommodationOrderEntryGroupModelA.setEndingDate(checkOutDate);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.cleanupCartBeforeAddition(TEST_ACCOMMODATION_OFFERING_CODE_A, "30/12/2016",
				"30/12/216");

	}

	@Test
	public void testCleanupCartBeforeAdditionForDifferentAccOffCode()
	{

		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final Date checkInDate = TravelDateUtils.convertStringDateToDate("30/12/2016", TravelservicesConstants.DATE_PATTERN);
		final Date checkOutDate = TravelDateUtils.convertStringDateToDate("31/12/2016", TravelservicesConstants.DATE_PATTERN);
		accommodationOrderEntryGroupModelA.setStartingDate(checkInDate);
		accommodationOrderEntryGroupModelA.setEndingDate(checkOutDate);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.cleanupCartBeforeAddition("TEST_ACCOMMODATION_OFFERING_CODE_B", "30/12/2016",
				"31/12/2016");
	}

	@Test
	public void testCleanupCartBeforeAdditionForSameStartingDate()
	{

		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final Date checkInDate = TravelDateUtils.convertStringDateToDate("30/12/2016", TravelservicesConstants.DATE_PATTERN);
		final Date checkOutDate = TravelDateUtils.convertStringDateToDate("31/12/2016", TravelservicesConstants.DATE_PATTERN);
		accommodationOrderEntryGroupModelA.setStartingDate(checkInDate);
		accommodationOrderEntryGroupModelA.setEndingDate(checkOutDate);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.cleanupCartBeforeAddition(TEST_ACCOMMODATION_OFFERING_CODE_A, "30/12/2016",
				"31/12/2016");
	}

	@Test
	public void testCleanupCartBeforeAdditionForSameEndingDate()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final Date checkInDate = TravelDateUtils.convertStringDateToDate("29/12/2016", TravelservicesConstants.DATE_PATTERN);
		final Date checkOutDate = TravelDateUtils.convertStringDateToDate("31/12/2016", TravelservicesConstants.DATE_PATTERN);
		accommodationOrderEntryGroupModelA.setStartingDate(checkInDate);
		accommodationOrderEntryGroupModelA.setEndingDate(checkOutDate);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.cleanupCartBeforeAddition(TEST_ACCOMMODATION_OFFERING_CODE_A, "30/12/2016",
				"31/12/2016");
	}

	@Test
	public void testCleanupCartBeforeAdditionForDifferentEndingDate()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final Date checkInDate = TravelDateUtils.convertStringDateToDate("29/12/2016", TravelservicesConstants.DATE_PATTERN);
		final Date checkOutDate = TravelDateUtils.convertStringDateToDate("31/12/2016", TravelservicesConstants.DATE_PATTERN);
		accommodationOrderEntryGroupModelA.setStartingDate(checkInDate);
		accommodationOrderEntryGroupModelA.setEndingDate(checkOutDate);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.cleanupCartBeforeAddition(TEST_ACCOMMODATION_OFFERING_CODE_A, "29/12/2016",
				"30/12/2016");
	}

	@Test
	public void testCleanupCartBeforeAddition()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final Date checkInDate = TravelDateUtils.convertStringDateToDate("30/12/2016", TravelservicesConstants.DATE_PATTERN);
		final Date checkOutDate = TravelDateUtils.convertStringDateToDate("31/12/2016", TravelservicesConstants.DATE_PATTERN);
		accommodationOrderEntryGroupModelA.setStartingDate(checkInDate);
		accommodationOrderEntryGroupModelA.setEndingDate(checkOutDate);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		when(commerceCartCalculationStrategy.calculateCart(cart)).thenReturn(Boolean.TRUE);
		defaultAccommodationCommerceCartService.cleanupCartBeforeAddition("TEST_ACCOMMODATION_OFFERING_CODE_B", "20/12/2016",
				"30/12/2016");

	}

	@Test
	public void testEmptyCartForEmptyGroups()
	{
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(Collections.emptyList());
		defaultAccommodationCommerceCartService.emptyCart();
	}

	@Test
	public void testEmptyCart()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		defaultAccommodationCommerceCartService.emptyCart();
	}

	@Test
	public void testGetNumberOfEntryGroupsInCart()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(true);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		Assert.assertEquals(1, defaultAccommodationCommerceCartService.getNumberOfEntryGroupsInCart());
	}

	@Test
	public void testGetMaxRoomStayRefNumberForEmptyGroups()
	{
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(Collections.emptyList());
		Assert.assertEquals(-1, defaultAccommodationCommerceCartService.getMaxRoomStayRefNumber());
	}

	@Test
	public void testGetMaxRoomStayRefNumber()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModelA.setRoomStayRefNumber(0);
		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		Assert.assertEquals(0, defaultAccommodationCommerceCartService.getMaxRoomStayRefNumber());
	}

	@Test
	public void testRemoveAccommodationOrderEntryGroupForNoGroupWithRefNum()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModelA.setRoomStayRefNumber(0);
		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationOrderEntryGroup(1, cart)).thenReturn(null);
		Assert.assertFalse(defaultAccommodationCommerceCartService.removeAccommodationOrderEntryGroup(1));
	}

	@Test
	public void testRemoveAccommodationOrderEntryGroup()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModelA.setRoomStayRefNumber(0);
		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationOrderEntryGroup(0, cart)).thenReturn(accommodationOrderEntryGroupModelA);
		Assert.assertTrue(defaultAccommodationCommerceCartService.removeAccommodationOrderEntryGroup(0));
	}

	@Test
	public void testGetCurrentAccommodationOfferingForEmptyGroups()
	{
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(Collections.emptyList());
		Assert.assertNull(defaultAccommodationCommerceCartService.getCurrentAccommodationOffering());
	}

	@Test
	public void testGetCurrentAccommodationOffering()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModelA.setRoomStayRefNumber(0);
		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(cartService.hasSessionCart()).thenReturn(true);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		Assert.assertNotNull(defaultAccommodationCommerceCartService.getCurrentAccommodationOffering());
	}

	@Test
	public void testRemoveAccommodationOrderEntryGroups()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelA = new AccommodationOrderEntryGroupModel();
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModelB = new AccommodationOrderEntryGroupModel();

		final AccommodationModel accommodationA = new AccommodationModel();
		accommodationA.setCode(TEST_ACCOMMODATION_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodation(accommodationA);

		final AccommodationOfferingModel accommodationOfferingModelA = new AccommodationOfferingModel();
		accommodationOfferingModelA.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelA.setAccommodationOffering(accommodationOfferingModelA);
		final RatePlanModel ratePlanA = new RatePlanModel();
		ratePlanA.setCode(TEST_RATE_PLAN_CODE_A);
		accommodationOrderEntryGroupModelA.setRatePlan(ratePlanA);



		final AccommodationModel accommodationB = new AccommodationModel();
		accommodationB.setCode(TEST_ACCOMMODATION_CODE_B);
		accommodationOrderEntryGroupModelB.setAccommodation(accommodationB);

		final AccommodationOfferingModel accommodationOfferingModelB = new AccommodationOfferingModel();
		accommodationOfferingModelB.setCode(TEST_ACCOMMODATION_OFFERING_CODE_A);
		accommodationOrderEntryGroupModelB.setAccommodationOffering(accommodationOfferingModelB);
		final RatePlanModel ratePlanB = new RatePlanModel();
		ratePlanB.setCode(TEST_RATE_PLAN_CODE_B);
		accommodationOrderEntryGroupModelB.setRatePlan(ratePlanB);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(AmendStatus.NEW);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		accommodationOrderEntryGroupModelA.setEntries(entries);
		accommodationOrderEntryGroupModelB.setEntries(entries);

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = new ArrayList<>();
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelA);
		accommodationOrderEntryGroups.add(accommodationOrderEntryGroupModelB);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(bookingService.getAccommodationOrderEntryGroups(cart)).thenReturn(accommodationOrderEntryGroups);
		defaultAccommodationCommerceCartService.removeAccommodationOrderEntryGroups(accommodationOrderEntryGroups, 1);
	}

	@Test
	public void testGetNewAccommodationOrderEntryGroupsForEmptyScenrios()
	{
		when(cartService.hasSessionCart()).thenReturn(Boolean.FALSE);
		Assert.assertTrue(CollectionUtils
				.isEmpty(defaultAccommodationCommerceCartService.getNewAccommodationOrderEntryGroups(null, null, null)));

		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		Assert.assertTrue(CollectionUtils
				.isEmpty(defaultAccommodationCommerceCartService.getNewAccommodationOrderEntryGroups(null, null, null)));

		final AccommodationModel accommodationModel1 = createAccommodationModel("TEST_ACCOMMODATION_CODE");
		final AccommodationOfferingModel accommodationOfferingModel1 = createAccommodationOfferingModel(
				"TEST_ACCOMMODATION_OFFERING_CODE");

		final RatePlanModel ratePlan = createRatePlanModel("TEST_RATE_PLAN_CODE");

		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel1 = createAccommodationOrderEntryGroupModel(
				accommodationModel1, accommodationOfferingModel1, ratePlan,
				Collections.singletonList(createAbstractOrderEntryModel(true, AmendStatus.SAME, OrderEntryStatus.LIVING,
						new RoomRateProductModel(), 1, 10d, 10d, Collections.emptyList())));
		when(bookingService.getAccommodationOrderEntryGroups(cart))
				.thenReturn(Collections.singletonList(accommodationOrderEntryGroupModel1));
		Assert.assertTrue(CollectionUtils
				.isEmpty(defaultAccommodationCommerceCartService.getNewAccommodationOrderEntryGroups(null, null, null)));
		Assert.assertTrue(CollectionUtils.isEmpty(
				defaultAccommodationCommerceCartService.getNewAccommodationOrderEntryGroups(null, "TEST_ACCOMMODATION_CODE", null)));
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationCommerceCartService
				.getNewAccommodationOrderEntryGroups("TEST_ACCOMMODATION_OFFERING_CODE", "TEST_ACCOMMODATION_CODE", null)));
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationCommerceCartService.getNewAccommodationOrderEntryGroups(
				"TEST_ACCOMMODATION_OFFERING_CODE", "TEST_ACCOMMODATION_CODE", "TEST_RATE_PLAN_CODE")));

	}

	@Test
	public void testGetNewAccommodationOrderEntryGroups()
	{
		when(cartService.hasSessionCart()).thenReturn(Boolean.TRUE);
		final CartModel cart = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		final AccommodationModel accommodationModel1 = createAccommodationModel("TEST_ACCOMMODATION_CODE");
		final AccommodationOfferingModel accommodationOfferingModel1 = createAccommodationOfferingModel(
				"TEST_ACCOMMODATION_OFFERING_CODE");
		final RatePlanModel ratePlan = createRatePlanModel("TEST_RATE_PLAN_CODE");
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel1 = createAccommodationOrderEntryGroupModel(
				accommodationModel1, accommodationOfferingModel1, ratePlan,
				Stream.of(
						createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryStatus.LIVING, new RoomRateProductModel(), 1,
								10d, 10d, Collections.emptyList()))
						.collect(Collectors.toList()));
		when(bookingService.getAccommodationOrderEntryGroups(cart))
				.thenReturn(Collections.singletonList(accommodationOrderEntryGroupModel1));
		Assert.assertTrue(CollectionUtils.isNotEmpty(defaultAccommodationCommerceCartService.getNewAccommodationOrderEntryGroups(
				"TEST_ACCOMMODATION_OFFERING_CODE", "TEST_ACCOMMODATION_CODE", "TEST_RATE_PLAN_CODE")));

	}

	@Test
	public void testGetEntriesForProductAndAccommodationForEmptyListScenarios()
	{
		Assert.assertTrue(CollectionUtils
				.isEmpty(defaultAccommodationCommerceCartService.getEntriesForProductAndAccommodation(null, null, null)));
		Assert.assertTrue(CollectionUtils
				.isEmpty(defaultAccommodationCommerceCartService.getEntriesForProductAndAccommodation(new CartModel(), null, null)));
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationCommerceCartService
				.getEntriesForProductAndAccommodation(new CartModel(), new ProductModel(), null)));
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationCommerceCartService
				.getEntriesForProductAndAccommodation(new CartModel(), new ProductModel(), new CartEntryModel())));
		final CartEntryModel cartEntryModel = new CartEntryModel();
		cartEntryModel.setEntryGroup(new AbstractOrderEntryGroupModel());
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationCommerceCartService
				.getEntriesForProductAndAccommodation(new CartModel(), new ProductModel(), cartEntryModel)));
		final CartEntryModel cartEntryModel2 = new CartEntryModel();
		cartEntryModel2.setEntryGroup(new AccommodationOrderEntryGroupModel());
		Assert.assertTrue(CollectionUtils.isEmpty(defaultAccommodationCommerceCartService
				.getEntriesForProductAndAccommodation(new CartModel(), new ProductModel(), cartEntryModel2)));
	}

	@Test
	public void testGetEntriesForProductAndAccommodation()
	{
		final ProductModel product1 = new ProductModel();
		product1.setCode("TEST_ACCOMMODATION_OFFERING_CODE");
		final ProductModel product2 = new ProductModel();
		product2.setCode("TEST_ACCOMMODATION_OFFERING_CODE_2");

		final AccommodationModel accommodationModel1 = createAccommodationModel("TEST_ACCOMMODATION_CODE");
		final AccommodationOfferingModel accommodationOfferingModel1 = createAccommodationOfferingModel(
				"TEST_ACCOMMODATION_OFFERING_CODE");
		final AccommodationModel accommodationModel2 = createAccommodationModel("TEST_ACCOMMODATION_CODE_2");
		final AccommodationOfferingModel accommodationOfferingModel2 = createAccommodationOfferingModel(
				"TEST_ACCOMMODATION_OFFERING_CODE_2");

		final RatePlanModel ratePlan = createRatePlanModel("TEST_RATE_PLAN_CODE");
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel1 = createAccommodationOrderEntryGroupModel(
				accommodationModel1, accommodationOfferingModel1, ratePlan, Collections.emptyList());
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel2 = createAccommodationOrderEntryGroupModel(
				accommodationModel1, accommodationOfferingModel2, ratePlan, Collections.emptyList());
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel3 = createAccommodationOrderEntryGroupModel(
				accommodationModel2, accommodationOfferingModel1, ratePlan, Collections.emptyList());
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel4 = createAccommodationOrderEntryGroupModel(
				accommodationModel2, accommodationOfferingModel2, ratePlan, Collections.emptyList());
		final AbstractOrderEntryModel cartEntryModel1 = createCartEntryModel(accommodationOrderEntryGroupModel1, product1);
		final AbstractOrderEntryModel cartEntryModel2 = createCartEntryModel(accommodationOrderEntryGroupModel2, product1);
		final AbstractOrderEntryModel cartEntryModel3 = createCartEntryModel(accommodationOrderEntryGroupModel3, product1);
		final AbstractOrderEntryModel cartEntryModel4 = createCartEntryModel(accommodationOrderEntryGroupModel4, product2);
		final AbstractOrderEntryModel cartEntryModel5 = createCartEntryModel(new AbstractOrderEntryGroupModel(), product2);
		final AbstractOrderEntryModel cartEntryModel6 = createCartEntryModel(null, product2);
		final AbstractOrderEntryModel cartEntryModel7 = new AbstractOrderEntryModel();

		final CartModel cartModel = new CartModel();
		cartModel.setEntries(Stream.of(cartEntryModel1, cartEntryModel2, cartEntryModel3, cartEntryModel4, cartEntryModel5,
				cartEntryModel6, cartEntryModel7).collect(Collectors.toList()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(defaultAccommodationCommerceCartService
				.getEntriesForProductAndAccommodation(cartModel, product1, (CartEntryModel) cartEntryModel1)));
	}

	@Test
	public void testIsNewRoomInCart()
	{

	}

	private CartEntryModel createCartEntryModel(final AbstractOrderEntryGroupModel accommodationOrderEntryGroupModel,
			final ProductModel product)
	{
		final CartEntryModel entry = new CartEntryModel();
		entry.setEntryGroup(accommodationOrderEntryGroupModel);
		entry.setProduct(product);
		return entry;
	}

	private AbstractOrderEntryModel createAbstractOrderEntryModel(final boolean isActive, final AmendStatus amendStatus,
			final OrderEntryStatus orderEntryStatus, final ProductModel product, final int quantity, final double basePrice,
			final double totalPrice, final List<TaxValue> taxValues)
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel()
		{
			@Override
			public Collection<TaxValue> getTaxValues()
			{
				return taxValues;
			}

		};
		abstractOrderEntryModel.setActive(isActive);
		abstractOrderEntryModel.setProduct(product);
		abstractOrderEntryModel.setQuantityStatus(orderEntryStatus);
		abstractOrderEntryModel.setQuantity(Long.valueOf(quantity));
		abstractOrderEntryModel.setBasePrice(basePrice);
		abstractOrderEntryModel.setTotalPrice(totalPrice);
		abstractOrderEntryModel.setAmendStatus(amendStatus);
		return abstractOrderEntryModel;
	}

	private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroupModel(final AccommodationModel accommodationModel,
			final AccommodationOfferingModel accommodationOfferingModel, final RatePlanModel ratePlan,
			final List<AbstractOrderEntryModel> entries)
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModel.setAccommodation(accommodationModel);
		accommodationOrderEntryGroupModel.setAccommodationOffering(accommodationOfferingModel);
		accommodationOrderEntryGroupModel.setRatePlan(ratePlan);
		accommodationOrderEntryGroupModel.setEntries(entries);
		return accommodationOrderEntryGroupModel;

	}

	private AccommodationModel createAccommodationModel(final String code)
	{
		final AccommodationModel accommodationModel = new AccommodationModel();
		accommodationModel.setCode(code);
		return accommodationModel;
	}

	private AccommodationOfferingModel createAccommodationOfferingModel(final String code)
	{
		final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
		accommodationOfferingModel.setCode(code);
		return accommodationOfferingModel;
	}

	private RatePlanModel createRatePlanModel(final String code)
	{
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setCode(code);
		return ratePlan;
	}

}
