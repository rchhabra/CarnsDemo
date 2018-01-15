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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.RoomPreferenceType;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.util.TaxValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationReservationPropertyHandlerTest
{

	@InjectMocks
	AccommodationReservationPropertyHandler accommodationReservationPropertyHandler;

	@Mock
	private AbstractOrderModel orderModel;

	@Mock
	private AccommodationOfferingModel accommodationOfferingModel;

	@Mock
	private BookingService bookingService;

	@Mock
	private AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> accommodationOfferingConverter;

	@Test
	public void testHandle()
	{
		final TestDataSetup testSetUp = new TestDataSetup();
		final AccommodationOrderEntryGroupModel accGroup = testSetUp.createAccommodationOrderEntryGroups();
		given(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.willReturn(Stream.of(accGroup).collect(Collectors.toList()));
		final PropertyData property = new PropertyData();
		property.setAccommodationOfferingCode("acc1");
		Mockito.when(accommodationOfferingConverter.convert(accGroup.getAccommodationOffering())).thenReturn(property);
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationPropertyHandler.handle(orderModel, accommodationReservationData);
		assertNotNull(accommodationReservationData.getAccommodationReference());

	}

	@Test(expected = AccommodationPipelineException.class)
	public void testHandleForException()
	{
		given(bookingService.getAccommodationOrderEntryGroups(orderModel)).willReturn(Collections.emptyList());
		final AccommodationReservationData accommodationReservationData = new AccommodationReservationData();
		accommodationReservationPropertyHandler.handle(orderModel, accommodationReservationData);
	}

	private class TestDataSetup
	{

		public List<AbstractOrderEntryModel> createOrderEntries()
		{
			final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
			final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
			final RoomRateProductModel product1 = new RoomRateProductModel();
			product1.setCode("product1");
			final RoomRateProductModel product2 = new RoomRateProductModel();
			product2.setCode("product2");
			entry1.setProduct(product1);
			entry1.setBasePrice(Double.valueOf(10));
			entry1.setQuantity(Long.valueOf(1));
			entry1.setTotalPrice(Double.valueOf(15));
			entry2.setProduct(product2);
			entry2.setBasePrice(Double.valueOf(10));
			entry2.setQuantity(Long.valueOf(1));
			entry2.setTotalPrice(Double.valueOf(15));

			final TaxValue testTax1 = new TaxValue("testTax1", 1, false, null);
			entry1.setTaxValues(Stream.of(testTax1).collect(Collectors.toList()));
			final TaxValue testTax2 = new TaxValue("testTax2", 1, false, null);
			entry2.setTaxValues(Stream.of(testTax2).collect(Collectors.toList()));

			entry1.setActive(true);
			entry1.setQuantityStatus(OrderEntryStatus.LIVING);
			entry1.setQuantity(1L);
			entry2.setActive(true);
			entry2.setQuantityStatus(OrderEntryStatus.LIVING);
			entry2.setQuantity(1L);

			return Arrays.asList(entry1, entry2);
		}


		public AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroups()
		{
			final AccommodationOrderEntryGroupModel accOrderEntryGroup = new AccommodationOrderEntryGroupModel();
			final SimpleDateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
			try
			{
				accOrderEntryGroup.setStartingDate(dateFormat.parse("09/12/2016"));
				accOrderEntryGroup.setEndingDate(dateFormat.parse("11/12/2016"));
				accOrderEntryGroup.setRoomStayRefNumber(0);

				final RatePlanModel ratePlanModel = new RatePlanModel();
				accOrderEntryGroup.setRatePlan(ratePlanModel);
				final AccommodationModel accommodationModel = new AccommodationModel();
				accOrderEntryGroup.setAccommodation(accommodationModel);
				accOrderEntryGroup.setEntries(createOrderEntries());

				accOrderEntryGroup.setFirstName("ABC");
				accOrderEntryGroup.setLastName("ABC");


				final GuestCountModel guestCount = new GuestCountModel();
				final PassengerTypeModel passengerType = new PassengerTypeModel();
				passengerType.setCode("adult");
				guestCount.setQuantity(2);
				accOrderEntryGroup.setGuestCounts(Stream.of(guestCount).collect(Collectors.toList()));

				final SpecialRequestDetailModel splReq = new SpecialRequestDetailModel();
				final SpecialServiceRequestModel splReqModel = new SpecialServiceRequestModel();
				splReqModel.setCode("req1");
				splReq.setSpecialServiceRequest(Stream.of(splReqModel).collect(Collectors.toList()));
				accOrderEntryGroup.setSpecialRequestDetail(splReq);

				final RoomPreferenceModel roomPrefModel = new RoomPreferenceModel();
				roomPrefModel.setPreferenceType(RoomPreferenceType.BED_PREFERENCE);
				accOrderEntryGroup.setRoomPreferences(Stream.of(roomPrefModel).collect(Collectors.toList()));
				final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
				accommodationOfferingModel.setCode("acc1");
				accOrderEntryGroup.setAccommodationOffering(accommodationOfferingModel);
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
			return accOrderEntryGroup;

		}

	}

}
