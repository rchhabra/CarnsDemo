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

package de.hybris.platform.traveladdon.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.BookingJourneyType;

import java.util.StringJoiner;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class TransportManageBookingController extends AbstractController
{
	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "travelCommercePriceFacade")
	private TravelCommercePriceFacade travelCommercePriceFacade;

	private static final String CANCEL_TRAVELLER_FAILED = "text.page.managemybooking.cancel.traveller.failed";
	private static final String CANCEL_TRAVELLER_SUCCESSFUL = "text.page.managemybooking.cancel.traveller.successful";
	private static final String REFUND_ORDER_RESULT = "text.page.managemybooking.refund.order.result";
	private static final String BOOKING_DETAILS_PAGE = "/manage-booking/booking-details/";

	/**
	 * Starts the process of amending ancillaries by creating cart from order and attaching it to the session
	 *
	 * @param orderCode
	 * @return ancillary page for amendments
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/manage-booking/{orderCode}/amend-ancillaries", method = RequestMethod.GET)
	@RequireHardLogIn
	public String amendAncillaries(@PathVariable final String orderCode) throws CMSItemNotFoundException
	{
		final Boolean amend = bookingFacade.amendOrder(orderCode, bookingFacade.getCurrentUserUid());
		if (!amend)
		{
			// If cart was not created, return validation message
			return null;
		}

		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);

		final String redirectURL = bookingFacade.checkBookingJourneyType(orderCode, BookingJourneyType.BOOKING_PACKAGE)
				? TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_AMENDMENT_PATH
				: TravelacceleratorstorefrontWebConstants.ANCILLARY_AMENDMENT_PAGE;

		return REDIRECT_PREFIX + redirectURL;
	}

	/**
	 * Starts the process of traveller cancellation
	 *
	 * @param model
	 * @param redirectModel
	 * @param orderCode
	 * @param travellerUid
	 * @return traveller cancellation modal
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/manage-booking/cancel-traveller-request", method = RequestMethod.GET, produces = "application/json")
	@RequireHardLogIn
	public String beginCancelTraveller(final Model model, final RedirectAttributes redirectModel,
			@ModelAttribute(value = "orderCode") final String orderCode,
			@ModelAttribute(value = "travellerUid") final String travellerUid) throws CMSItemNotFoundException
	{
		final TravellerData cancelledTraveller = travellerFacade.getTraveller(travellerUid);
		if (!bookingFacade.atleastOneAdultTravellerRemaining(orderCode, cancelledTraveller.getLabel()))
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT, CANCEL_TRAVELLER_FAILED);
			return TraveladdonControllerConstants.Views.Pages.Cancel.CancelTravellerResponse;
		}

		final boolean travellerCancellationStarted = bookingFacade.beginTravellerCancellation(orderCode,
				cancelledTraveller.getLabel(), cancelledTraveller.getUid(), bookingFacade.getCurrentUserUid());

		if (travellerCancellationStarted)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.CANCELLED_TRAVELLER, cancelledTraveller);
			final PriceData totalToRefund = bookingFacade.getRefundForCancelledTraveller();
			model.addAttribute(TravelacceleratorstorefrontWebConstants.TOTAL_TO_PAY, totalToRefund);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, true);
		}
		else
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT, CANCEL_TRAVELLER_FAILED);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, false);
		}


		return TraveladdonControllerConstants.Views.Pages.Cancel.CancelTravellerResponse;
	}

	/**
	 * Proceeds with cancellation of traveller and places a new (amended) order
	 *
	 * @param redirectModel
	 * @param travellerUid
	 * @param orderCode
	 * @return the redirect to booking details page with result of cancellation process
	 */
	@RequestMapping(value = "/manage-booking/cancel-traveller")
	@RequireHardLogIn
	public String cancelTraveller(final RedirectAttributes redirectModel,
			@ModelAttribute(value = "travellerUid") final String travellerUid,
			@ModelAttribute(value = "orderCode") final String orderCode)
	{
		final PriceData totalToPay = bookingFacade.getTotalToPay();
		final PriceData totalToRefund = bookingFacade.getRefundForCancelledTraveller();
		final TravellerData traveller = travellerFacade.getTraveller(travellerUid);

		if (traveller == null)
		{
			return getCancellTravellerError(redirectModel, orderCode);
		}

		final boolean result = bookingFacade.cancelTraveller(totalToPay, totalToRefund, traveller);
		if (result)
		{
			final PriceData absTotalToPay = travelCommercePriceFacade
					.createPriceData(totalToRefund.getValue().abs().doubleValue(), totalToRefund.getCurrencyIso());
			if (bookingFacade.isAdditionalSecurityActive(orderCode) && !bookingFacade.isUserOrderOwner(orderCode))
			{
				final Object[] attributes = { buildFormattedTravellerName((PassengerInformationData) traveller.getTravellerInfo()),
						absTotalToPay.getFormattedValue() };
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
						"text.info.homepage.cancel.traveller.successful", attributes);
				sessionService.removeAttribute(TravelservicesConstants.PASSENGER_REFERENCE);
				return REDIRECT_PREFIX + "/";
			}
			else
			{
				redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT,
						CANCEL_TRAVELLER_SUCCESSFUL);
				redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.REFUND_RESULT, REFUND_ORDER_RESULT);
				redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_PARAMETER,
						buildFormattedTravellerName((PassengerInformationData) traveller.getTravellerInfo()));
				redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.REFUNDED_AMOUNT, absTotalToPay);
			}
		}
		else
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT, CANCEL_TRAVELLER_FAILED);
		}

		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
	}

	protected String getCancellTravellerError(final RedirectAttributes redirectModel, final String orderCode)
	{
		redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT, CANCEL_TRAVELLER_FAILED);
		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
	}

	protected String buildFormattedTravellerName(final PassengerInformationData info)
	{
		final StringJoiner formattedTravellerName = new StringJoiner(" ");
		formattedTravellerName.add(info.getTitle().getName()).add(info.getFirstName()).add(info.getSurname());
		return formattedTravellerName.toString();
	}

}
