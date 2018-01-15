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

package de.hybris.platform.travelservices.strategies.payment.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.services.BookingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link PartiallyDelayedPaymentOptionStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartiallyDelayedPaymentOptionStrategyTest
{
	@InjectMocks
	PartiallyDelayedPaymentOptionStrategy partiallyDelayedPaymentOptionStrategy;

	@Mock
	private BookingService bookingService;

	@Mock
	TransportationPayInAdvancePaymentInfoStrategy transportationPayInAdvancePaymentInfoStrategy;

	@Mock
	AccommodationDelayedPaymentInfoStrategy accommodationDelayedPaymentInfoStrategy;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Before
	public void setUp()
	{
		partiallyDelayedPaymentOptionStrategy
				.setEntryTypePaymentInfoCreationStrategies(
						Arrays.asList(transportationPayInAdvancePaymentInfoStrategy, accommodationDelayedPaymentInfoStrategy));
		when(accommodationDelayedPaymentInfoStrategy.create(Matchers.any(AbstractOrderModel.class))).thenReturn(null);
	}

	@Test
	public void testCreateForTransportOrder()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(AmendStatus.NEW, OrderEntryType.TRANSPORT));
		final OrderModel orderModel = new OrderModel();
		orderModel.setEntries(entries);
		Assert.assertNull(partiallyDelayedPaymentOptionStrategy.create(orderModel));
	}

	@Test
	public void testCreateForOriginalOrderandPaidInAdvance()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(AmendStatus.NEW, OrderEntryType.ACCOMMODATION));
		final OrderModel orderModel = new OrderModel();
		orderModel.setEntries(entries);
		orderModel.setOriginalOrder(orderModel);
		when(bookingService.getOrderTotalPriceByType(orderModel.getOriginalOrder(), OrderEntryType.ACCOMMODATION)).thenReturn(100d);
		when(bookingService.getOrderTotalPaidForOrderEntryType(orderModel.getOriginalOrder(), OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.valueOf(100d));
		Assert.assertNull(partiallyDelayedPaymentOptionStrategy.create(orderModel));


	}


	@Test
	public void testCreateFoNullrAccommodationGroup()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(AmendStatus.NEW, OrderEntryType.ACCOMMODATION));
		final OrderModel orderModel = new OrderModel();
		orderModel.setEntries(entries);
		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(0);
		final RatePlanModel ratePlan = createRatePlan();
		ratePlan.setGuarantee(null);
		entryGroup.setRatePlan(ratePlan);
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();
		when(bookingService.getAccommodationOrderEntryGroups(orderModel)).thenReturn(entryGroups);
		final List<EntryTypePaymentInfo> entryTypePaymentInfo = new ArrayList<>();
		entryTypePaymentInfo.add(new EntryTypePaymentInfo());
		when(transportationPayInAdvancePaymentInfoStrategy.create(orderModel)).thenReturn(entryTypePaymentInfo);
		Assert.assertNull(partiallyDelayedPaymentOptionStrategy.create(orderModel));
	}

	@Test
	public void testCreateForAccommodationGroupWithoutGurantee()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(AmendStatus.NEW, OrderEntryType.ACCOMMODATION));
		final OrderModel orderModel = new OrderModel();
		orderModel.setEntries(entries);
		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(0);
		final RatePlanModel ratePlan = createRatePlan();
		ratePlan.setGuarantee(null);
		entryGroup.setRatePlan(ratePlan);
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();
		entryGroups.add(entryGroup);
		when(bookingService.getAccommodationOrderEntryGroups(orderModel)).thenReturn(entryGroups);
		final List<EntryTypePaymentInfo> entryTypePaymentInfo = new ArrayList<>();
		entryTypePaymentInfo.add(new EntryTypePaymentInfo());
		when(transportationPayInAdvancePaymentInfoStrategy.create(orderModel)).thenReturn(entryTypePaymentInfo);
		Assert.assertNull(partiallyDelayedPaymentOptionStrategy.create(orderModel));
	}

	@Test
	public void testCreateForTotalPaidAreUnequal()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(AmendStatus.NEW, OrderEntryType.ACCOMMODATION));
		final OrderModel orderModel = new OrderModel();
		orderModel.setEntries(entries);
		orderModel.setOriginalOrder(orderModel);
		when(bookingService.getOrderTotalPriceByType(orderModel.getOriginalOrder(), OrderEntryType.ACCOMMODATION)).thenReturn(100d);
		when(bookingService.getOrderTotalPaidForOrderEntryType(orderModel.getOriginalOrder(), OrderEntryType.ACCOMMODATION))
				.thenReturn(BigDecimal.valueOf(90d));
		Assert.assertNull(partiallyDelayedPaymentOptionStrategy.create(orderModel));
	}

	@Test
	public void testCreate()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getDouble(TravelservicesConstants.PAYMENT_TRANSACTION_AUTH_AMOUNT)).thenReturn(0.001d);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(createAbstractOrderEntryModel(AmendStatus.NEW, OrderEntryType.ACCOMMODATION));
		final OrderModel orderModel = new OrderModel();
		orderModel.setEntries(entries);
		final AccommodationOrderEntryGroupModel entryGroup = new AccommodationOrderEntryGroupModel();
		entryGroup.setRoomStayRefNumber(0);
		entryGroup.setRatePlan(createRatePlan());
		final List<AccommodationOrderEntryGroupModel> entryGroups = new ArrayList<>();
		entryGroups.add(entryGroup);
		when(bookingService.getAccommodationOrderEntryGroups(orderModel)).thenReturn(entryGroups);
		final List<EntryTypePaymentInfo> entryTypePaymentInfo = new ArrayList<>();
		entryTypePaymentInfo.add(new EntryTypePaymentInfo());
		when(transportationPayInAdvancePaymentInfoStrategy.create(orderModel)).thenReturn(entryTypePaymentInfo);
		Assert.assertNotNull(partiallyDelayedPaymentOptionStrategy.create(orderModel));
	}

	private RatePlanModel createRatePlan()
	{
		final List<GuaranteeModel> guarantees = new ArrayList<>();
		guarantees.add(createGuaranteeModel());
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setGuarantee(guarantees);
		return ratePlan;
	}

	private GuaranteeModel createGuaranteeModel()
	{
		final GuaranteeModel guaranteeModel = new GuaranteeModel();
		return guaranteeModel;
	}

	private AbstractOrderEntryModel createAbstractOrderEntryModel(final AmendStatus status, final OrderEntryType orderType)
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setAmendStatus(status);
		entry.setType(orderType);
		return entry;

	}

}
