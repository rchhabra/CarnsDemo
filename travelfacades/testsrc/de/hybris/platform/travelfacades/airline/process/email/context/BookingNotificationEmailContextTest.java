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
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
	private final BookingNotificationEmailContext bookingNotificationEmailContext = new BookingNotificationEmailContext()
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
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private OrderModel orderModel;

	@Mock
	private BaseStoreModel baseStoreModel;

	private static final String STORE_NAME = "Airline Store";


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bookingNotificationEmailContext.setReservationFacade(reservationFacade);
		bookingNotificationEmailContext.setTravelCommercePriceFacade(travelCommercePriceFacade);
	}

	@Test
	public void testInit()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(reservationFacade.getReservationData(orderModel)).thenReturn(bookingNotificationEmailContext.getReservation());
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getName()).thenReturn(STORE_NAME);
		bookingNotificationEmailContext.init(orderProcessModel, emailPageModel);

	}

}
