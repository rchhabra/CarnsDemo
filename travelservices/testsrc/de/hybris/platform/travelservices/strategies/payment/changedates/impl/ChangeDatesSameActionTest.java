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

package de.hybris.platform.travelservices.strategies.payment.changedates.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link ChangeDatesSameAction}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ChangeDatesSameActionTest
{
	@InjectMocks
	ChangeDatesSameAction changeDatesSameAction;

	@Mock
	private CartService cartService;

	@Mock
	private BookingService bookingService;

	@Mock
	private ModelService modelService;

	@Test
	public void testTakeActionForEmptyArguments()
	{
		Assert.assertFalse(changeDatesSameAction.takeAction(null, Collections.emptyList()));
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = new AccommodationOrderEntryGroupModel();
		Assert.assertFalse(changeDatesSameAction.takeAction(accommodationOrderEntryGroup, Collections.emptyList()));
	}

	@Test
	public void testTakeActionForEmptySessionCart()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = new AccommodationOrderEntryGroupModel();
		final List<Integer> entryNumbers = new ArrayList<>();
		entryNumbers.add(new Integer(2));
		Assert.assertFalse(changeDatesSameAction.takeAction(accommodationOrderEntryGroup, entryNumbers));
	}

	@Test
	public void testTakeActionForEntriesNotInSession()
	{
		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartService.getEntryForNumber(cartModel, 2)).willReturn(null);
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroup.setEntries(Collections.emptyList());
		final List<Integer> entryNumbers = new ArrayList<>();
		entryNumbers.add(new Integer(2));
		Assert.assertFalse(changeDatesSameAction.takeAction(accommodationOrderEntryGroup, entryNumbers));
	}

	@Test
	public void testTakeActionForEntries()
	{
		final TaxValue tax = new TaxValue("TEST_TAX_CODE", 10d, true, "TEST_CURRENCY_CODE");
		final CartEntryModel entry = new CartEntryModel()
		{
			@Override
			public Collection<TaxValue> getTaxValues()
			{
				return Stream.of(tax).collect(Collectors.toList());
			}
		};
		entry.setTotalPrice(100d);
		final CartModel cartModel = new CartModel();

		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartService.getEntryForNumber(cartModel, 2)).willReturn(entry);
		final AbstractOrderEntryModel orderGroupEntry1 = new AbstractOrderEntryModel();
		orderGroupEntry1.setProduct(new RoomRateProductModel());
		orderGroupEntry1.setActive(Boolean.TRUE);
		orderGroupEntry1.setTotalPrice(20d);
		final List<AbstractOrderEntryModel> orderGroupEntries = new ArrayList<>();
		orderGroupEntries.add(orderGroupEntry1);
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroup.setEntries(orderGroupEntries);
		final List<Integer> entryNumbers = new ArrayList<>();
		entryNumbers.add(new Integer(2));
		given(bookingService.linkEntriesToOldPaymentTransactions(Matchers.any(), Matchers.any())).willReturn(Boolean.TRUE);
		Assert.assertTrue(changeDatesSameAction.takeAction(accommodationOrderEntryGroup, entryNumbers));
	}

	@Test
	public void testTakeActionForErrorInLinkingEntries()
	{
		final TaxValue tax = new TaxValue("TEST_TAX_CODE", 10d, true, "TEST_CURRENCY_CODE");
		final CartEntryModel entry = new CartEntryModel()
		{
			@Override
			public Collection<TaxValue> getTaxValues()
			{
				return Stream.of(tax).collect(Collectors.toList());
			}
		};
		entry.setTotalPrice(100d);
		final CartModel cartModel = new CartModel();

		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartService.getEntryForNumber(cartModel, 2)).willReturn(entry);
		final AbstractOrderEntryModel orderGroupEntry1 = new AbstractOrderEntryModel();
		orderGroupEntry1.setProduct(new RoomRateProductModel());
		orderGroupEntry1.setActive(Boolean.TRUE);
		orderGroupEntry1.setTotalPrice(20d);
		final List<AbstractOrderEntryModel> orderGroupEntries = new ArrayList<>();
		orderGroupEntries.add(orderGroupEntry1);
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroup.setEntries(orderGroupEntries);
		final List<Integer> entryNumbers = new ArrayList<>();
		entryNumbers.add(new Integer(2));
		given(bookingService.linkEntriesToOldPaymentTransactions(Matchers.any(), Matchers.any())).willReturn(Boolean.FALSE);
		Assert.assertFalse(changeDatesSameAction.takeAction(accommodationOrderEntryGroup, entryNumbers));
	}
}
