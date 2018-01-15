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

package de.hybris.platform.travelfacades.travel.process.email.context;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link BookingNotificationEmailContext}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BookingNotificationEmailContextTest
{
	@InjectMocks
	private BookingNotificationEmailContext bookingNotificationEmailContext;

	@Mock
	private ReservationFacade reservationFacade;

	@Mock
	private ReservationData reservationData;

	@Mock
	private OrderProcessModel orderProcessModel;

	@Mock
	private EmailPageModel emailPageModel;

	@Mock
	private LanguageModel languageModel;

	@Mock
	private CustomerModel customerModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private OrderModel orderModel;

	@Mock
	private BaseStoreModel baseStoreModel;

	private static final String STORE_NAME = "Travel Store";

	@Mock
	private TravellerService travellerService;

	@Mock
	private BookingService bookingService;

	private GlobalTravelReservationData travelReservationData;

	private List<TravellerModel> travellers;
	private final TestDataSetUp testDataSetUp = new TestDataSetUp();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bookingNotificationEmailContext.setReservationFacade(reservationFacade);
		bookingNotificationEmailContext.setTravelCommercePriceFacade(travelCommercePriceFacade);

		final TravellerModel travelModel1 = Mockito.mock(TravellerModel.class);
		final TravellerInfoModel travellerInfoModel1 = Mockito.mock(PassengerInformationModel.class);
		when(travelModel1.getInfo()).thenReturn(travellerInfoModel1);
		when(((PassengerInformationModel) travellerInfoModel1).getEmail()).thenReturn("TEST_EMAIL_1");

		final TravellerModel travelModel2 = Mockito.mock(TravellerModel.class);
		final TravellerInfoModel travellerInfoModel2 = Mockito.mock(PassengerInformationModel.class);
		when(travelModel2.getInfo()).thenReturn(travellerInfoModel2);
		when(((PassengerInformationModel) travellerInfoModel2).getEmail()).thenReturn("TEST_EMAIL_2");
		travellers = new ArrayList<>();
		travellers.add(travelModel2);
		travellers.add(travelModel1);
		Mockito.when(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).thenReturn(testDataSetUp.createPriceData(200d));
		travelReservationData = new GlobalTravelReservationData();
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		when(orderModel.getCurrency()).thenReturn(currencyModel);
	}

	@Test
	public void testInitForTransportOnlyBooking()
	{
		final ReservationData reservationData = testDataSetUp.createReservationData(200d);
		travelReservationData.setReservationData(reservationData);

		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(travelReservationData);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		when(orderModel.getEntries()).thenReturn(entries);
		when(travellerService.getTravellers(entries)).thenReturn(travellers);
		when(bookingService.getAccommodationOrderEntryGroups(orderModel)).thenReturn(null);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		bookingNotificationEmailContext.init(orderProcessModel, emailPageModel);

		Assert.assertEquals(travelReservationData, bookingNotificationEmailContext.getTravelReservationData());
	}

	@Test
	public void testInitForTransportOnlyAccommodationOnlyBooking()
	{
		final AccommodationReservationData reservationData = testDataSetUp.createAccommodationReservationData(200d, 100d);
		travelReservationData.setAccommodationReservationData(reservationData);

		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(travelReservationData);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		when(orderModel.getEntries()).thenReturn(entries);
		when(travellerService.getTravellers(entries)).thenReturn(travellers);
		when(bookingService.getAccommodationOrderEntryGroups(orderModel)).thenReturn(null);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		bookingNotificationEmailContext.init(orderProcessModel, emailPageModel);

		Assert.assertEquals(travelReservationData, bookingNotificationEmailContext.getTravelReservationData());
	}

	@Test
	public void testInitForTransportAndAccommodationOnlyBooking()
	{
		final AccommodationReservationData accommodationReservationData = testDataSetUp.createAccommodationReservationData(200d,
				100d);
		travelReservationData.setAccommodationReservationData(accommodationReservationData);

		final ReservationData reservationData = testDataSetUp.createReservationData(200d);
		travelReservationData.setReservationData(reservationData);

		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getGlobalTravelReservationData(orderModel)).thenReturn(travelReservationData);
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		when(orderModel.getEntries()).thenReturn(entries);
		when(travellerService.getTravellers(entries)).thenReturn(travellers);
		when(bookingService.getAccommodationOrderEntryGroups(orderModel)).thenReturn(null);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		bookingNotificationEmailContext.init(orderProcessModel, emailPageModel);

		Assert.assertEquals(travelReservationData, bookingNotificationEmailContext.getTravelReservationData());
	}

	private class TestDataSetUp
	{
		private ReservationData createReservationData(final double totalPrice)
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setTotalFare(createTotalFareData(totalPrice));
			return reservationData;
		}

		private AccommodationReservationData createAccommodationReservationData(final double totalPrice, final double totalPaid)
		{
			final AccommodationReservationData reservationData = new AccommodationReservationData();
			reservationData.setTotalToPay(createPriceData(totalPrice - totalPaid));
			reservationData.setTotalRate(createRateData(totalPrice));
			return reservationData;
		}

		private TotalFareData createTotalFareData(final double totalPrice)
		{
			final TotalFareData totalFareData = new TotalFareData();
			totalFareData.setTotalPrice(createPriceData(totalPrice));
			return totalFareData;
		}

		private RateData createRateData(final double price)
		{
			final RateData rateData = new RateData();
			rateData.setActualRate(createPriceData(price));
			return rateData;
		}

		private PriceData createPriceData(final double price)
		{
			final PriceData priceData = new PriceData();
			priceData.setValue(BigDecimal.valueOf(price));
			priceData.setCurrencyIso("GBP");
			priceData.setFormattedValue("GBP " + price);
			return priceData;
		}

	}

}
