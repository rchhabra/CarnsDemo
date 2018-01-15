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

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link JourneyCheckInRestrictionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class JourneyCheckInRestrictionStrategyTest
{
	private static final String ALTERNATIVE_MESSAGE = "booking.action.journey.check.in.alternative.message";

	@InjectMocks
	JourneyCheckInRestrictionStrategy journeyCheckInRestrictionStrategy;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private TimeService timeService;
	private List<ConsignmentStatus> consignmentStatusList;

	@Before
	public void setUp()
	{
		consignmentStatusList = new ArrayList<ConsignmentStatus>();
		consignmentStatusList.add(ConsignmentStatus.CHECKED_IN);
		consignmentStatusList.add(ConsignmentStatus.CANCELLED);
		journeyCheckInRestrictionStrategy.setConsignmentStatusList(this.consignmentStatusList);
	}

	@Test
	public void testPassengerAlreadyCheckedinRestriction()
	{
		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");

		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);

		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ConsignmentModel consignmentModel = new ConsignmentModel();
		consignmentModel.setStatus(ConsignmentStatus.CHECKED_IN);
		consignmentModel.setWarehouse(testDataSetUp.createTransportOffering(4));

		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setType(OrderEntryType.TRANSPORT);
		consignmentEntryModel.setOrderEntry(orderEntryModel);
		consignmentModel.setConsignmentEntries(Stream.of(consignmentEntryModel).collect(Collectors.toSet()));

		final OrderModel orderModel = new OrderModel();
		orderModel.setConsignments(Stream.of(consignmentModel).collect(Collectors.toSet()));
		given(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any())).willReturn(orderModel);
		given(timeService.getCurrentTime()).willReturn(new Date());

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setOriginDestinationRefNumber(0);
		bookingActionData.setAlternativeMessages(new ArrayList<String>());

		final List<BookingActionData> bookingActionDataList = new ArrayList<BookingActionData>();
		bookingActionDataList.add(bookingActionData);

		journeyCheckInRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

	@Test
	public void testPassengerPastDepartureTimeRestriction()
	{
		final ReservationData reservationData = new ReservationData();
		reservationData.setCode("BOOK0004");

		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);


		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ConsignmentModel consignmentModel = new ConsignmentModel();
		consignmentModel.setStatus(ConsignmentStatus.READY);
		consignmentModel.setWarehouse(testDataSetUp.createTransportOffering(-1));

		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setType(OrderEntryType.TRANSPORT);
		consignmentEntryModel.setOrderEntry(orderEntryModel);
		consignmentModel.setConsignmentEntries(Stream.of(consignmentEntryModel).collect(Collectors.toSet()));


		final OrderModel orderModel = new OrderModel();
		orderModel.setConsignments(
				Stream.of(consignmentModel)
						.collect(Collectors.toSet()));
		given(customerAccountService.getOrderForCode(Matchers.anyString(), Matchers.any())).willReturn(orderModel);
		given(timeService.getCurrentTime()).willReturn(new Date());

		final BookingActionData bookingActionData = new BookingActionData();
		bookingActionData.setEnabled(true);
		bookingActionData.setOriginDestinationRefNumber(0);
		bookingActionData.setAlternativeMessages(new ArrayList<String>());

		final List<BookingActionData> bookingActionDataList = new ArrayList<BookingActionData>();
		bookingActionDataList.add(bookingActionData);

		journeyCheckInRestrictionStrategy.applyStrategy(bookingActionDataList, reservationData);
		Assert.assertFalse(bookingActionDataList.get(0).isEnabled());
		Assert.assertEquals(ALTERNATIVE_MESSAGE, bookingActionDataList.get(0).getAlternativeMessages().get(0));
	}

	private class TestDataSetUp
	{
		public TransportOfferingModel createTransportOffering(final int hours)
		{
			final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
			transportOfferingModel.setDepartureTime(DateUtils.addHours(new Date(), hours));
			final TravelSectorModel sector = new TravelSectorModel();
			transportOfferingModel.setTravelSector(sector);
			final TransportFacilityModel transportFacility = new TransportFacilityModel();
			sector.setOrigin(transportFacility);
			sector.setDestination(transportFacility);
			final PointOfServiceModel pos = new PointOfServiceModel();
			pos.setTimeZoneId(ZoneId.systemDefault().toString());
			transportFacility.setPointOfService(Arrays.asList(pos));
			return transportOfferingModel;
		}
	}

}
