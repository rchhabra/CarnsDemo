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

package de.hybris.platform.accommodationaddon.controllers.cms;

import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationReviewForm;
import de.hybris.platform.accommodationaddon.forms.cms.AddRequestForm;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.BookingActionRequestData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.AbstractBookingDetailsComponentController;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractBookingDetailsComponentModel;
import de.hybris.platform.travelfacades.facades.accommodation.impl.DefaultAccommodationOfferingCustomerReviewFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for Accommodation Booking Details component
 */
@Controller("AccommodationBookingDetailsComponentController")
@RequestMapping(value = AccommodationaddonControllerConstants.Actions.Cms.AccommodationBookingDetailsComponent)
public class AccommodationBookingDetailsComponentController extends AbstractBookingDetailsComponentController
{

	private static final String ACCOMMODATION_RESERVATION_DATA = "accommodationReservationData";

	@Resource(name = "accommodationOfferingCustomerReviewFacade")
	private DefaultAccommodationOfferingCustomerReviewFacade accommodationOfferingCustomerReviewFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AbstractBookingDetailsComponentModel component)
	{
		final String bookingReference = getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_REFERENCE);
		try
		{
			final AccommodationReservationData reservationData = getBookingFacade().getFullAccommodationBooking(bookingReference);
			model.addAttribute(ACCOMMODATION_RESERVATION_DATA, reservationData);

			// BookingActions
			final BookingActionRequestData bookingActionRequest = createAccommodationBookingActionRequest(bookingReference);
			final BookingActionResponseData bookingActionResponse = getActionFacade()
					.getAccommodationBookingAction(bookingActionRequest, reservationData);

			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_ACTION_RESPONSE, bookingActionResponse);
			model.addAttribute(AccommodationaddonWebConstants.ADD_REQUEST_FORM, new AddRequestForm());
			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_REVIEW_FORM, new AccommodationReviewForm());
			model.addAttribute(AccommodationaddonWebConstants.SUBMITTED_REVIEWS,
					accommodationOfferingCustomerReviewFacade.retrieveCustomerReviewByBooking(bookingReference,
							reservationData.getAccommodationReference().getAccommodationOfferingCode()));
			final PriceData amountPaid = getTravelCommercePriceFacade().getPaidAmount(reservationData);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_PAID, amountPaid);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_DUE,
					getTravelCommercePriceFacade().getDueAmount(reservationData, amountPaid));
		}
		catch (final JaloObjectNoLongerValidException ex)
		{
			model.addAttribute(PAGE_NOT_AVAILABLE, TravelacceleratorstorefrontWebConstants.PAGE_TEMPORARY_NOT_AVAILABLE);
		}

	}

	@Override
	protected BookingActionRequestData createAccommodationBookingActionRequest(final String bookingReference)
	{
		final BookingActionRequestData bookingActionRequestData = super.createAccommodationBookingActionRequest(bookingReference);
		bookingActionRequestData.getRequestActions().add(ActionTypeOption.CANCEL_BOOKING);
		return bookingActionRequestData;
	}

}
