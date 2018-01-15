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

package de.hybris.platform.traveladdon.controllers.cms;

import de.hybris.platform.commercefacades.travel.BookingActionRequestData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.AbstractBookingDetailsComponentController;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractBookingDetailsComponentModel;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("TransportBookingDetailsComponentController")
@RequestMapping(value = TraveladdonControllerConstants.Actions.Cms.TransportBookingDetailsComponent)
public class TransportBookingDetailsComponentController extends AbstractBookingDetailsComponentController
{
	private static final String RESERVATION_DATA = "reservationData";

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AbstractBookingDetailsComponentModel component)
	{
		final String bookingReference = getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_REFERENCE);
		try
		{
			final ReservationData reservationData = getBookingFacade().getBookingByBookingReference(bookingReference);
			model.addAttribute(RESERVATION_DATA, reservationData);

			handleDisruptedReservation(model, bookingReference, reservationData);

			// BookingActions
			final BookingActionRequestData bookingActionRequest = createTransportBookingActionRequest(bookingReference);
			final BookingActionResponseData bookingActionResponse = getActionFacade().getBookingAction(bookingActionRequest,
					reservationData);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_ACTION_RESPONSE, bookingActionResponse);
		}
		catch (final JaloObjectNoLongerValidException ex)
		{
			model.addAttribute(PAGE_NOT_AVAILABLE, TravelacceleratorstorefrontWebConstants.PAGE_TEMPORARY_NOT_AVAILABLE);
		}

	}

	@Override
	protected BookingActionRequestData createTransportBookingActionRequest(final String bookingReference)
	{
		final BookingActionRequestData bookingActionRequestData = super.createTransportBookingActionRequest(bookingReference);
		bookingActionRequestData.getRequestActions().add(ActionTypeOption.CANCEL_BOOKING);
		return bookingActionRequestData;
	}

}
