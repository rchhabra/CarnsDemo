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

package de.hybris.platform.travelacceleratorstorefront.controllers.cms;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.travel.BookingActionRequestData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractBookingDetailsComponentModel;
import de.hybris.platform.travelfacades.facades.ActionFacade;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.packages.PackageFacade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;


/**
 * Abstract Controller for the Booking Details Components, that exposes the common methods called by its implementations
 */
public abstract class AbstractBookingDetailsComponentController
		extends SubstitutingCMSAddOnComponentController<AbstractBookingDetailsComponentModel>
{
	private static final Logger LOG = Logger.getLogger(AbstractBookingDetailsComponentController.class);
	protected static final String PAGE_NOT_AVAILABLE = "pageNotAvailable";

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "actionFacade")
	private ActionFacade actionFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "travelCommercePriceFacade")
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Resource(name = "packageFacade")
	private PackageFacade packageFacade;

	/**
	 * Method that handles the reservation in case of status disrupted
	 *
	 * @param model
	 * @param bookingReference
	 * @param reservationData
	 */
	protected void handleDisruptedReservation(final Model model, final String bookingReference,
			final ReservationData reservationData)
	{
		if (reservationData != null && (StringUtils.equalsIgnoreCase(OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode(),
				reservationData.getBookingStatusCode())
				|| StringUtils.equalsIgnoreCase(OrderStatus.ACTIVE_DISRUPTED.getCode(), reservationData.getBookingStatusCode())))
		{
			final ReservationData disruptedReservation = bookingFacade.getDisruptedReservation(bookingReference);
			if (disruptedReservation != null)
			{
				model.addAttribute("disruptedReservation", disruptedReservation);
			}
			else
			{
				LOG.warn("Order is in DISRUPTED status but there is no disrupted order in order history.");
			}
		}
	}

	/**
	 * This method will create a bookingActionRequestData for the Booking Details Page
	 *
	 * @param bookingReference
	 *           as the booking reference number
	 * @return the bookingActionRequestData to be used to get the BookingActionResponseData
	 */
	protected BookingActionRequestData createTransportBookingActionRequest(final String bookingReference)
	{
		final BookingActionRequestData bookingActionRequestData = new BookingActionRequestData();

		bookingActionRequestData.setBookingReference(bookingReference);

		final List<ActionTypeOption> requestActions = new ArrayList<>();
		requestActions.add(ActionTypeOption.AMEND_ANCILLARY);
		requestActions.add(ActionTypeOption.CHECK_IN_ALL);
		requestActions.add(ActionTypeOption.CHECK_IN);
		requestActions.add(ActionTypeOption.REMOVE_TRAVELLER);
		requestActions.add(ActionTypeOption.ACCEPT_BOOKING);
		requestActions.add(ActionTypeOption.REJECT_BOOKING);
		bookingActionRequestData.setRequestActions(requestActions);

		final CustomerData currentCustomer = customerFacade.getCurrentCustomer();
		bookingActionRequestData.setUserId(currentCustomer.getUid());

		return bookingActionRequestData;
	}

	/**
	 * This method will create a bookingActionRequestData for the Accommodation Booking Details Page
	 *
	 * @param bookingReference
	 *           as the booking reference number
	 * @return the bookingActionRequestData to be used to get the BookingActionResponseData
	 */
	protected BookingActionRequestData createAccommodationBookingActionRequest(final String bookingReference)
	{
		final BookingActionRequestData bookingActionRequestData = new BookingActionRequestData();

		bookingActionRequestData.setBookingReference(bookingReference);

		final List<ActionTypeOption> requestActions = new ArrayList<>();
		requestActions.add(ActionTypeOption.ADD_REQUEST);
		requestActions.add(ActionTypeOption.CANCEL_REQUEST);
		requestActions.add(ActionTypeOption.CHANGE_DATES);
		requestActions.add(ActionTypeOption.ADD_ROOM);
		requestActions.add(ActionTypeOption.PAY_NOW);
		requestActions.add(ActionTypeOption.WRITE_REVIEW);
		requestActions.add(ActionTypeOption.AMEND_EXTRAS);
		bookingActionRequestData.setRequestActions(requestActions);

		final CustomerData currentCustomer = customerFacade.getCurrentCustomer();
		bookingActionRequestData.setUserId(currentCustomer.getUid());

		return bookingActionRequestData;
	}

	/**
	 * @return the bookingFacade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	/**
	 * @return the customerFacade
	 */
	protected CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	/**
	 * @return the actionFacade
	 */
	protected ActionFacade getActionFacade()
	{
		return actionFacade;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @return the packageFacade
	 */
	protected PackageFacade getPackageFacade()
	{
		return packageFacade;
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

}
