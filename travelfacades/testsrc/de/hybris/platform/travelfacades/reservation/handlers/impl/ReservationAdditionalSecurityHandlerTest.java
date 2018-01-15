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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link ReservationAdditionalSecurityHandler}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationAdditionalSecurityHandlerTest
{

	@InjectMocks
	ReservationAdditionalSecurityHandler handler;

	@Mock
	private SessionService sessionService;


	/**
	 * given: AbstractOrderModel
	 * when: AbstractOrderModel has no additional security
	 * then: reservationData has no additional security and no filtered travellers
	 */
	@Test
	public void testFalseAdditionalSecurity()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setAdditionalSecurity(Boolean.FALSE);
		final ReservationData reservationData = testDataSetUp.createReservationData();
		handler.handle(abstractOrderModel, reservationData);
		assertFalse(reservationData.getAdditionalSecurity());
		assertFalse(reservationData.getFilteredTravellers());
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().size(), 2);
	}

	/**
	 * given: AbstractOrderModel
	 * when: AbstractOrderModel has additional security but no Passenger Reference is set in session
	 * then: reservationData has additional security and no filtered travellers
	 */
	@Test
	public void testAdditionalSecurityWithNoPassengerReference()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setAdditionalSecurity(Boolean.TRUE);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.PASSENGER_REFERENCE)).thenReturn(null);
		final ReservationData reservationData = testDataSetUp.createReservationData();
		handler.handle(abstractOrderModel, reservationData);
		assertTrue(reservationData.getAdditionalSecurity());
		assertFalse(reservationData.getFilteredTravellers());
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().size(), 2);
	}

	/**
	 * given: AbstractOrderModel
	 * when: AbstractOrderModel has additional security Passenger Reference is set in session but there is no user
	 * then: reservationData has additional security and no filtered travellers
	 */
	@Test
	public void testAdditionalSecurityWithPassengerReferenceAndNoUser()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setAdditionalSecurity(Boolean.TRUE);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.PASSENGER_REFERENCE))
				.thenReturn(TestDataSetUp.PASSENGER_SIMPLE_UID_1);

		final UserModel customerModel = testDataSetUp.createCustomerModel(TestDataSetUp.USER_UID_1, CustomerType.GUEST);
		abstractOrderModel.setUser(customerModel);

		Mockito.when(sessionService.getAttribute(TravelservicesConstants.USER)).thenReturn(null);

		final ReservationData reservationData = testDataSetUp.createReservationData();
		handler.handle(abstractOrderModel, reservationData);

		assertTrue(reservationData.getAdditionalSecurity());
		assertFalse(reservationData.getFilteredTravellers());
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().size(), 2);
	}

	/**
	 * given: AbstractOrderModel
	 * when: AbstractOrderModel has additional security Passenger Reference is set in session but the logged user is the one who
	 * placed the booking
	 * then: reservationData has additional security and no filtered travellers
	 */
	@Test
	public void testAdditionalSecurityWithPassengerReferenceAndSameUser()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setAdditionalSecurity(Boolean.TRUE);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.PASSENGER_REFERENCE))
				.thenReturn(TestDataSetUp.PASSENGER_SIMPLE_UID_1);

		final UserModel customerModel = testDataSetUp.createCustomerModel(TestDataSetUp.USER_UID_1, CustomerType.GUEST);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.USER)).thenReturn(customerModel);
		abstractOrderModel.setUser(customerModel);

		final ReservationData reservationData = testDataSetUp.createReservationData();
		handler.handle(abstractOrderModel, reservationData);

		assertTrue(reservationData.getAdditionalSecurity());
		assertFalse(reservationData.getFilteredTravellers());
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().size(), 2);
	}

	/**
	 * given: AbstractOrderModel
	 * when: AbstractOrderModel has additional security Passenger Reference is set in session but the logged user is the one who
	 * did not place the booking and the booking was placed with a GUEST user
	 * then: reservationData has additional security and no filtered travellers
	 */
	@Test
	public void testAdditionalSecurityWithPassengerReferenceDifferentUserPlacedWithGuest()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setAdditionalSecurity(Boolean.TRUE);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.PASSENGER_REFERENCE))
				.thenReturn(TestDataSetUp.PASSENGER_SIMPLE_UID_1);

		final UserModel bookerCustomerModel = testDataSetUp.createCustomerModel(TestDataSetUp.USER_UID_1, CustomerType.GUEST);
		final UserModel customerModel = testDataSetUp.createCustomerModel(TestDataSetUp.USER_UID_2, CustomerType.REGISTERED);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.USER)).thenReturn(customerModel);
		abstractOrderModel.setUser(bookerCustomerModel);

		final ReservationData reservationData = testDataSetUp.createReservationData();
		handler.handle(abstractOrderModel, reservationData);

		assertTrue(reservationData.getAdditionalSecurity());
		assertFalse(reservationData.getFilteredTravellers());
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().size(), 2);
	}

	/**
	 * given: AbstractOrderModel
	 * when: AbstractOrderModel has additional security Passenger Reference is set in session but the logged user is the one who
	 * did not place the booking and the booking was placed with a REGISTERED user
	 * then: reservationData has additional security and filtered travellers
	 */
	@Test
	public void testAdditionalSecurityWithPassengerReferenceDifferentUserPlacedWithRegisteredUser()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setAdditionalSecurity(Boolean.TRUE);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.PASSENGER_REFERENCE))
				.thenReturn(TestDataSetUp.PASSENGER_SIMPLE_UID_1);

		final UserModel bookerCustomerModel = testDataSetUp.createCustomerModel(TestDataSetUp.USER_UID_1, CustomerType.REGISTERED);
		final UserModel customerModel = testDataSetUp.createCustomerModel(TestDataSetUp.USER_UID_2, CustomerType.GUEST);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.USER)).thenReturn(customerModel);
		abstractOrderModel.setUser(bookerCustomerModel);

		final ReservationData reservationData = testDataSetUp.createReservationData();
		handler.handle(abstractOrderModel, reservationData);

		assertTrue(reservationData.getAdditionalSecurity());
		assertTrue(reservationData.getFilteredTravellers());
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().size(), 1);
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().get(0).getSimpleUID(),
				TestDataSetUp.PASSENGER_SIMPLE_UID_1);
	}

	/**
	 * given: AbstractOrderModel
	 * when: AbstractOrderModel has additional security Passenger Reference is set in session but the logged user is the one who
	 * did not place the booking and the booking was placed with a GUEST user
	 * then: reservationData has additional security and filtered travellers
	 */
	@Test
	public void testOriginalAdditionalSecurityWithPassengerReferenceDifferentUserPlacedWithRegisteredUser()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		final OrderModel originalOrder = new OrderModel();

		final UserModel bookerCustomerModel = testDataSetUp.createCustomerModel(TestDataSetUp.USER_UID_1, CustomerType.REGISTERED);
		final UserModel customerModel = testDataSetUp.createCustomerModel(TestDataSetUp.USER_UID_2, CustomerType.GUEST);

		originalOrder.setUser(bookerCustomerModel);

		abstractOrderModel.setOriginalOrder(originalOrder);
		abstractOrderModel.setUser(bookerCustomerModel);
		abstractOrderModel.setAdditionalSecurity(Boolean.TRUE);

		Mockito.when(sessionService.getAttribute(TravelservicesConstants.PASSENGER_REFERENCE))
				.thenReturn(TestDataSetUp.PASSENGER_SIMPLE_UID_1);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.USER)).thenReturn(customerModel);

		final ReservationData reservationData = testDataSetUp.createReservationData();
		handler.handle(abstractOrderModel, reservationData);

		assertTrue(reservationData.getAdditionalSecurity());
		assertTrue(reservationData.getFilteredTravellers());
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().size(), 1);
		assertEquals(reservationData.getReservationItems().get(0).getReservationItinerary().getTravellers().get(0).getSimpleUID(),
				TestDataSetUp.PASSENGER_SIMPLE_UID_1);
	}


	private class TestDataSetUp
	{
		public static final String USER_UID_1 = "QBU3HVKWI26JAB3TD3ZI";
		public static final String USER_UID_2 = "LUGSDF8WI2KJH7KA873J";

		public static final String PASSENGER_UID_1 = "1_QBU3HVKWI26JAB3TD3ZI";
		public static final String PASSENGER_UID_2 = "2_G4VVPWORHO24QSSI9DV8";

		public static final String PASSENGER_SIMPLE_UID_1 = "1QBU3H";
		public static final String PASSENGER_SIMPLE_UID_2 = "2G4VVP";

		public static final String PASSENGER_LABEL_1 = "1QBU3H";
		public static final String PASSENGER_LABEL_2 = "2G4VVP";



		private ReservationData createReservationData()
		{
			final ReservationData reservationData = new ReservationData();
			final List<ReservationItemData> reservationItems = new LinkedList<>();

			final List<TravellerData> travellerModels = createTravellers();

			for (int leg = 0; leg < 2; leg++)
			{
				final ReservationItemData reservationItem = new ReservationItemData();
				final ItineraryData reservationItinerary = new ItineraryData();
				reservationItinerary.setTravellers(travellerModels);
				reservationItem.setReservationItinerary(reservationItinerary);
				reservationItems.add(reservationItem);
			}

			reservationData.setReservationItems(reservationItems);
			return reservationData;
		}


		private List<TravellerData> createTravellers()
		{
			final List<TravellerData> travellerModels = new LinkedList<>();

			travellerModels.add(createTravellerData(PASSENGER_UID_1, PASSENGER_SIMPLE_UID_1, PASSENGER_LABEL_1));
			travellerModels.add(createTravellerData(PASSENGER_UID_2, PASSENGER_SIMPLE_UID_2, PASSENGER_LABEL_2));

			return travellerModels;
		}


		private TravellerData createTravellerData(final String uid, final String simpleUID, final String label)
		{
			final TravellerData travellerData = new TravellerData();
			travellerData.setUid(uid);
			travellerData.setSimpleUID(simpleUID);
			travellerData.setLabel(label);
			return travellerData;
		}

		private UserModel createCustomerModel(final String UID, final CustomerType customerType)
		{
			final UserModel customerModel = new CustomerModel();
			customerModel.setUid(UID);
			((CustomerModel) customerModel).setType(customerType);
			return customerModel;
		}
	}


}
