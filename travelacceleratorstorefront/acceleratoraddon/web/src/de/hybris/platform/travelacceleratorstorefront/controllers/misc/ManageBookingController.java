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
 */

package de.hybris.platform.travelacceleratorstorefront.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Manage Booking pages
 */
@Controller
@RequestMapping("/manage-booking")
public class ManageBookingController extends AbstractController
{
	private static final Logger LOG = Logger.getLogger(ManageBookingController.class);

	private static final String REFUND_ORDER_RESULT = "text.page.managemybooking.refund.order.result";
	private static final String CANCEL_ORDER_FAILED = "text.page.managemybooking.cancel.order.failed";
	private static final String CANCEL_ORDER_SUCCESSFUL = "text.page.managemybooking.cancel.order.successful";
	private static final String ACCOUNT_BOOKINGS_UNLINK_ERROR = "account.bookings.unlink.error";
	private static final String BOOKING_DETAILS_PAGE = "/manage-booking/booking-details/";

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	/**
	 * Method to get the total amount to be refunded in case of cancel order
	 *
	 * @param model
	 * @param order Code as the code of the order to be cancelled
	 * @return the string representing the formatted value of the refund
	 */
	@RequestMapping(value = "/cancel-booking-request/{orderCode}", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getRefundConfirmation(final Model model, @PathVariable final String orderCode)
	{
		final boolean isCancelPossible = bookingFacade.isCancelPossible(orderCode);
		if (isCancelPossible)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, true);
			try
			{
				final PriceData totalToRefund = bookingFacade.getRefundTotal(orderCode);
				model.addAttribute(TravelacceleratorstorefrontWebConstants.TOTAL_TO_REFUND, totalToRefund.getFormattedValue());
			}
			catch (final ModelNotFoundException e)
			{
				LOG.error("Order with orderGUID " + orderCode + " not found for current user in current BaseStore");
				model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, false);
			}
		}
		else
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, false);
		}
		return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.cancelOrderResponse;
	}

	/**
	 * Method to cancel an order
	 *
	 * @param redirectModel
	 * @param orderCode     as the code of the order to be cancelled
	 * @return the redirect to the Cancel Confirmation page
	 */
	@RequestMapping(value = "/cancel-booking/{orderCode}")
	@RequireHardLogIn
	public String cancelOrder(final RedirectAttributes redirectModel, @PathVariable final String orderCode)
	{
		final boolean result = bookingFacade.cancelOrder(orderCode);
		if (result)
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT, CANCEL_ORDER_SUCCESSFUL);
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.REFUND_RESULT, REFUND_ORDER_RESULT);

			final PriceData refundedAmount = bookingFacade.getRefundTotal(orderCode);
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.REFUNDED_AMOUNT, refundedAmount);
		}
		else
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT, CANCEL_ORDER_FAILED);
		}

		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
	}

	@RequestMapping(value = "/accept-booking/{orderCode}")
	@RequireHardLogIn
	public String acceptOrder(@PathVariable final String orderCode)
	{
		final boolean isStatusChanged = bookingFacade.acceptOrder(orderCode);
		if (!isStatusChanged)
		{
			return REDIRECT_PREFIX + ROOT;
		}
		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
	}

	@RequestMapping(value = "/unlink-booking/{orderCode}")
	@RequireHardLogIn
	public String deleteBooking(@PathVariable final String orderCode, final RedirectAttributes redirectModel)
	{
		final boolean isBookingDeleted = bookingFacade.unlinkBooking(orderCode);
		if (!isBookingDeleted)
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.UNLINK_RESULT, ACCOUNT_BOOKINGS_UNLINK_ERROR);
		}
		return REDIRECT_PREFIX + "/my-account/my-bookings";
	}

}
