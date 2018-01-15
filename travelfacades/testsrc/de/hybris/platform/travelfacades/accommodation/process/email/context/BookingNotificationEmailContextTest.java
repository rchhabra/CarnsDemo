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

package de.hybris.platform.travelfacades.accommodation.process.email.context;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BookingNotificationEmailContextTest
{
	@InjectMocks
	private BookingNotificationEmailContext bookingNotificationEmailContext;

	@Mock
	private ReservationFacade reservationFacade;

	@Mock
	private AccommodationReservationData accommodationReservationData;

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
	private OrderModel orderModel;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private BookingService bookingService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	private static final String STORE_NAME = "Hotels Store";

	private final TestDataSetUp testDataSetUp = new TestDataSetUp();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bookingNotificationEmailContext.setReservationFacade(reservationFacade);
		bookingNotificationEmailContext.setTravelCommercePriceFacade(travelCommercePriceFacade);
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("GBP");
		when(orderModel.getCurrency()).thenReturn(currency);
		Mockito.when(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).thenReturn(testDataSetUp.createPriceData(200d));
	}

	@Test
	public void testInitForNullOrder()
	{
		final AccommodationReservationData reservationData = testDataSetUp.createAccommodationReservationData(200d, 100d);
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getAccommodationReservationData(orderModel)).thenReturn(reservationData);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(AbstractOrderModel.class))).thenReturn(null);
		bookingNotificationEmailContext.init(orderProcessModel, emailPageModel);
	}

	@Test
	public void testInit()
	{
		final AccommodationReservationData reservationData = testDataSetUp.createAccommodationReservationData(200d, 100d);
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getAccommodationReservationData(orderModel)).thenReturn(reservationData);

		final AccommodationOrderEntryGroupModel aoegm1 = Mockito.mock(AccommodationOrderEntryGroupModel.class);
		final AccommodationOrderEntryGroupModel aoegm2 = Mockito.mock(AccommodationOrderEntryGroupModel.class);

		final List<AccommodationOrderEntryGroupModel> aoegms = new ArrayList<>(1);
		aoegms.add(aoegm1);
		aoegms.add(aoegm2);
		when(aoegm2.getContactEmail()).thenReturn("TEST_CONTACT_EMAIL");
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		when(bookingService.getAccommodationOrderEntryGroups(Matchers.any(AbstractOrderModel.class))).thenReturn(aoegms);
		bookingNotificationEmailContext.init(orderProcessModel, emailPageModel);
	}

	private class TestDataSetUp
	{
		private AccommodationReservationData createAccommodationReservationData(final double totalPrice, final double totalPaid)
		{
			final AccommodationReservationData reservationData = new AccommodationReservationData();
			reservationData.setTotalToPay(createPriceData(totalPrice - totalPaid));
			reservationData.setTotalRate(createRateData(totalPrice));
			return reservationData;
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
