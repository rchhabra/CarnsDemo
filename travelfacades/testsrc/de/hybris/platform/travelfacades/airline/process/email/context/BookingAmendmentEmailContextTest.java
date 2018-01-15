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

package de.hybris.platform.travelfacades.airline.process.email.context;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.services.TravellerService;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link BookingAmendmentEmailContext}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BookingAmendmentEmailContextTest
{
	@InjectMocks
	private final BookingAmendmentEmailContext bookingAmendmentEmailContext = new BookingAmendmentEmailContext()
	{
		@Override
		protected void initTravellersEmails(final OrderProcessModel orderProcessModel, final Set<String> additionalEmails)
		{
			additionalEmails.add("abc@abc.com");
		}

	};

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
	private BaseStoreModel baseStoreModel;

	@Mock
	private PriceData priceData;

	@Mock
	private OrderModel orderModel;

	@Mock
	private OrderModel originalOrderModel;

	@Mock
	private TravellerService travellerService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	private static final String REFUND_AMOUNT = "£10.00";

	private static final String STORE_NAME = "Airline Store";

	private static final String TOTAL_TO_PAY = "£100.00";

	private static final String ORIGINAL_ORDER_CODE = "00001001";


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bookingAmendmentEmailContext.setReservationFacade(reservationFacade);
		bookingAmendmentEmailContext.setTravelCommercePriceFacade(travelCommercePriceFacade);

		when(originalOrderModel.getCode()).thenReturn(ORIGINAL_ORDER_CODE);
	}

	@Test
	public void testInitWithTotalToPay()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getReservationData(orderModel)).thenReturn(reservationData);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		when(orderModel.getOriginalOrder()).thenReturn(originalOrderModel);
		when(reservationData.getTotalToPay()).thenReturn(priceData);
		when(priceData.getValue()).thenReturn(BigDecimal.TEN);
		when(priceData.getFormattedValue()).thenReturn(TOTAL_TO_PAY);
		bookingAmendmentEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertEquals(bookingAmendmentEmailContext.getStoreName(), STORE_NAME);
		Assert.assertEquals(bookingAmendmentEmailContext.getTotalToPay(), TOTAL_TO_PAY);
		Assert.assertEquals(bookingAmendmentEmailContext.getReservationCode(), ORIGINAL_ORDER_CODE);
	}

	@Test
	public void testTotalToRefund()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getReservationData(orderModel)).thenReturn(reservationData);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		when(orderModel.getOriginalOrder()).thenReturn(originalOrderModel);
		when(reservationData.getTotalToPay()).thenReturn(priceData);
		when(priceData.getValue()).thenReturn(BigDecimal.valueOf(-10.00));
		when(priceData.getFormattedValue()).thenReturn(REFUND_AMOUNT);
		when(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.any(CurrencyModel.class))).thenReturn(priceData);

		bookingAmendmentEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertNotNull(bookingAmendmentEmailContext.getReservation());
		Assert.assertEquals(bookingAmendmentEmailContext.getStoreName(), STORE_NAME);
		Assert.assertEquals(bookingAmendmentEmailContext.getTotalToRefund(), REFUND_AMOUNT);
		Assert.assertEquals(bookingAmendmentEmailContext.getReservationCode(), ORIGINAL_ORDER_CODE);
	}

	@Test
	public void testNoRefund()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getReservationData(orderModel)).thenReturn(reservationData);
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		when(orderModel.getOriginalOrder()).thenReturn(null);
		when(reservationData.getTotalToPay()).thenReturn(null);
		when(priceData.getValue()).thenReturn(BigDecimal.valueOf(0.00));
		bookingAmendmentEmailContext.init(orderProcessModel, emailPageModel);
		Assert.assertEquals(bookingAmendmentEmailContext.getStoreName(), STORE_NAME);
		Assert.assertNotNull(bookingAmendmentEmailContext.getReservation());
	}

	private class TestDataSetUp
	{
		private TravellerModel createTravellerModel()
		{
			final TravellerModel traveller = new TravellerModel();
			return traveller;
		}
	}
}
