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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.RoomPreferenceType;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationReservationRoomRateHandlerTest
{
	@InjectMocks
	AccommodationReservationRoomRateHandler handler;

	@Mock
	private BookingService bookingService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel;
	@Mock
	private AbstractOrderModel orderModel;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private RoomRateProductModel product;
	@Mock
	AccommodationOrderEntryInfoModel accommodationOrderEntryInfoModel;

	@Before
	public void setUp()
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		given(orderModel.getCurrency()).willReturn(currencyModel);
		given(product.getCode()).willReturn("product1");
	}

	@Test
	public void testPopulate() throws ParseException
	{
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		given(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.willReturn(Stream.of(accommodationOrderEntryGroupModel).collect(Collectors.toList()));
		given(accommodationOrderEntryGroupModel.getRoomStayRefNumber()).willReturn(0);
		given(accommodationOrderEntryGroupModel.getEntries()).willReturn(Stream.of(orderEntry).collect(Collectors.toList()));
		given(orderEntry.getQuantityStatus()).willReturn(OrderEntryStatus.LIVING);
		given(orderEntry.getActive()).willReturn(true);
		given(orderEntry.getAccommodationOrderEntryInfo()).willReturn(accommodationOrderEntryInfoModel);
		final SimpleDateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
		given(accommodationOrderEntryInfoModel.getDates())
				.willReturn(Stream.of(dateFormat.parse("14/12/2016")).collect(Collectors.toList()));
		given(orderEntry.getProduct()).willReturn(product);
		given(orderEntry.getBasePrice()).willReturn(20d);
		final PriceData priceData1 = new PriceData();
		priceData1.setValue(BigDecimal.valueOf(Double.valueOf(20)));
		when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(20d), "GBP")).thenReturn(priceData1);
		given(orderEntry.getQuantity()).willReturn(1L);
		final TaxValue tax = new TaxValue("APD", 5d, false, 5d, "GB");
		given(orderEntry.getTaxValues()).willReturn(Stream.of(tax).collect(Collectors.toList()));
		final PriceData taxPriceData = new PriceData();
		taxPriceData.setValue(BigDecimal.valueOf(5d));
		final TaxData taxData = new TaxData();
		taxData.setCode("APD");
		taxData.setPrice(taxPriceData);

		final PriceData priceData2 = new PriceData();
		priceData2.setValue(BigDecimal.valueOf(5d));
		when(travelCommercePriceFacade.createPriceData(PriceDataType.BUY, BigDecimal.valueOf(5d), "GBP")).thenReturn(priceData2);

		final ReservedRoomStayData roomStays = new ReservedRoomStayData();
		roomStays.setRoomStayRefNumber(0);
		final RatePlanData ratePlan = new RatePlanData();
		roomStays.setRatePlans(Stream.of(ratePlan).collect(Collectors.toList()));
		reservationData.setRoomStays(Stream.of(roomStays).collect(Collectors.toList()));
		handler.handle(orderModel, reservationData);
		Assert.assertNotNull(reservationData.getRoomStays().get(0).getRatePlans());
	}

	private class TestDataSetUp
	{
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
				//accOrderEntryGroup.setEntries(createOrderEntries());

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
