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

package de.hybris.platform.accommodationaddon.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.controllers.pages.AbstractAccommodationPageController;
import de.hybris.platform.accommodationaddon.forms.AccommodationAddToCartBookingForm;
import de.hybris.platform.accommodationaddon.forms.AccommodationBookingChangeDateForm;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationReviewForm;
import de.hybris.platform.accommodationaddon.forms.cms.AddRequestForm;
import de.hybris.platform.accommodationaddon.validators.AccommodationRequestValidator;
import de.hybris.platform.accommodationaddon.validators.AccommodationReviewValidator;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.AccommodationAmendmentFacade;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.customer.TravelCustomerFacade;
import de.hybris.platform.travelfacades.facades.packages.PackageFacade;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for manage accommodation bookings
 */
@Controller
@RequestMapping("/manage-booking")
public class AccommodationManageBookingController extends AbstractAccommodationPageController
{
	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "travelCartService")
	private TravelCartService travelCartService;

	@Resource(name = "accommodationRequestValidator")
	private AccommodationRequestValidator accommodationRequestValidator;

	@Resource(name = "accommodationReviewValidator")
	private AccommodationReviewValidator accommodationReviewValidator;

	@Resource(name = "accommodationCartFacade")
	private AccommodationCartFacade accommodationCartFacade;

	@Resource(name = "travelCustomerFacade")
	private TravelCustomerFacade travelCustomerFacade;

	@Resource(name = "packageFacade")
	private PackageFacade packageFacade;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "accommodationAmendmentFacade")
	private AccommodationAmendmentFacade accommodationAmendmentFacade;

	private static final String BOOKING_DETAILS_PAGE = "/manage-booking/booking-details/";
	private static final String ACCOMMODATION_DETAILS_PAGE = "/manage-booking/accommodation-details/";
	private static final String GUEST_DETAILS_PAGE = "/checkout/guest-details";
	private static final String ROOM_STAY_REQ_PARAM = "?roomStay=";


	/**
	 * This method adds a request against a specific AccommodationOrderEntryGroup, populating the relative object
	 *
	 * @param bookingReference
	 * @param addRequestForm
	 * @param bindingResult
	 * @param redirectModel
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/add-request/{bookingReference}", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addRequest(@PathVariable("bookingReference") final String bookingReference,
			@ModelAttribute(AccommodationaddonWebConstants.ADD_REQUEST_FORM) final AddRequestForm addRequestForm,
			final BindingResult bindingResult, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		accommodationRequestValidator.validate(addRequestForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.REQUEST_EXCEEDED_LIMIT_MESSAGE);
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
		}

		if (bookingFacade.addRequestToRoomStayBooking(addRequestForm.getRequestMessage(), addRequestForm.getRoomStayRefNumber(),
				bookingReference))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.REQUEST_SUBMITTED_MESSAGE);
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.REQUEST_NOT_SUBMITTED_MESSAGE);
		}
		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
	}

	/**
	 * This method cancels a request from a specific AccommodationOrderEntryGroup, removing it from the relative requests
	 * container
	 *
	 * @param bookingReference
	 * @param requestCode
	 * @param roomStayRefNumber
	 * @param redirectModel
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/cancel-request/{bookingReference}/{roomStayRefNumber}/{requestCode}", method = RequestMethod.GET)
	@RequireHardLogIn
	public String cancelRequest(@PathVariable("bookingReference") final String bookingReference,
			@PathVariable("requestCode") final String requestCode, @PathVariable("roomStayRefNumber") final String roomStayRefNumber,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bookingFacade.removeRequestFromRoomStayBooking(requestCode, Integer.valueOf(roomStayRefNumber), bookingReference))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.REQUEST_REMOVED_MESSAGE);
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.REQUEST_NOT_REMOVED_MESSAGE);
		}
		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
	}

	@RequestMapping(value = "/add-room/{orderCode}")
	@RequireHardLogIn
	public String addRoom(@PathVariable final String orderCode)
	{
		final boolean isAddRoomStarted = accommodationAmendmentFacade.startAmendment(orderCode);
		if (!isAddRoomStarted)
		{
			// If there was any error while creating cart from order, do not redirect to accommodation details
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
		}
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		final String queryString = bookingFacade.buildAccommodationDetailsQueryFromCart();
		return REDIRECT_PREFIX + ACCOMMODATION_DETAILS_PAGE + queryString;
	}

	@RequestMapping(value = "/change-dates", method = RequestMethod.POST)
	@RequireHardLogIn
	public String changeDates(
			@ModelAttribute(AccommodationaddonWebConstants.ACCOMMODATION_BOOKING_CHANGE_DATE_FORM) final AccommodationBookingChangeDateForm accommodationBookingChangeDateForm,
			final BindingResult bindingResult, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final String bookingReference = accommodationBookingChangeDateForm.getBookingReference();

		final Boolean amend = bookingFacade.amendOrder(bookingReference, bookingFacade.getCurrentUserUid());
		if (!amend)
		{
			return getChangeDateCartError(bookingReference, redirectModel);
		}

		final AccommodationReservationData accommodationReservationData = bookingFacade
				.getFullAccommodationBookingForAmendOrder(bookingReference);

		final String checkInDate = accommodationBookingChangeDateForm.getCheckInDateTime();
		final String checkOutDate = accommodationBookingChangeDateForm.getCheckOutDateTime();

		final String error = validateUpdateAccommodationBookingDates(bookingReference, checkInDate, checkOutDate,
				accommodationReservationData);

		if (StringUtils.isNotEmpty(error))
		{
			redirectModel.addFlashAttribute(AccommodationaddonWebConstants.CHANGE_DATE_ERROR_MESSAGE, error);
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
		}

		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = createAccommodationAvailabilityRequestData(
				accommodationReservationData, checkInDate, checkOutDate);

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = getAccommodationOfferingFacade()
				.getSelectedAccommodationOfferingDetails(accommodationAvailabilityRequestData);

		final boolean isAccommodationAvailableForAmendment = getAccommodationOfferingFacade()
				.isAccommodationAvailableForQuickSelection(accommodationAvailabilityResponse);

		if (!isAccommodationAvailableForAmendment)
		{
			redirectModel.addFlashAttribute(AccommodationaddonWebConstants.IS_ACCOMMODATION_AVAILABLE,
					isAccommodationAvailableForAmendment);
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
		}

		final boolean isChangeDateSuccess = bookingFacade.changeDatesForAccommodationBooking(checkInDate, checkOutDate,
				accommodationAvailabilityResponse, accommodationReservationData);

		if (!isChangeDateSuccess)
		{
			return getChangeDateCartError(bookingReference, redirectModel);
		}

		final Map<String, String> changeDatePaymentResults = bookingFacade
				.getChangeDatePaymentResultsMap(accommodationReservationData, accommodationAvailabilityResponse);

		if (MapUtils.isEmpty(changeDatePaymentResults))
		{
			return getChangeDateCartError(bookingReference, redirectModel);
		}

		final String paymentType = changeDatePaymentResults.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS);
		final String payableAmount = changeDatePaymentResults.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE);
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);

		if (!StringUtils.equalsIgnoreCase(paymentType, TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE))
		{
			return placeOrder(paymentType, payableAmount, bookingReference, redirectModel);
		}

		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_CHANGE_DATES, bookingReference);

		return nextPage();
	}

	protected String placeOrder(final String paymentType, final String refundAmount, final String orderCode,
			final RedirectAttributes redirectModel)
	{
		final boolean isOrderPlaced = bookingFacade.placeOrder();

		if (isOrderPlaced)
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.AMEND_BOOKING_REFUNDED_AMOUNT, refundAmount);
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.AMEND_BOOKING_RESULT, paymentType);
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
		}

		return getChangeDateCartError(orderCode, redirectModel);
	}

	protected String getChangeDateCartError(final String orderCode, final RedirectAttributes redirectModel)
	{
		travelCartService.deleteCurrentCart();
		redirectModel.addFlashAttribute(AccommodationaddonWebConstants.CHANGE_DATE_ERROR_MESSAGE, ERROR_AMEND_BOOKING_CART);
		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
	}

	/**
	 * Redirects user to the next checkout page which is payment details
	 *
	 * @return payment details page or payment type page
	 */
	protected String nextPage()
	{
		if (travelCustomerFacade.isCurrentUserB2bCustomer())
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_TYPE_PATH;
		}

		final String paymentFlowProperty = getConfigurationService().getConfiguration().getString("payment.flow");
		if (StringUtils.isNotBlank(paymentFlowProperty))
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH + paymentFlowProperty;
		}
		return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH
				+ TravelacceleratorstorefrontWebConstants.PAYMENT_FLOW;
	}

	/**
	 * Save the submitted review against an accommodation offering, for the given order
	 *
	 * @param bookingReference
	 * @param accommodationReviewForm
	 * @param bindingResult
	 * @param redirectModel
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/write-review/{bookingReference}", method = RequestMethod.POST)
	@RequireHardLogIn
	public String writeReview(@PathVariable("bookingReference") final String bookingReference,
			@ModelAttribute(AccommodationaddonWebConstants.ACCOMMODATION_REVIEW_FORM) final AccommodationReviewForm accommodationReviewForm,
			final BindingResult bindingResult, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		accommodationReviewValidator.validate(accommodationReviewForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			bindingResult.getAllErrors().forEach(
					error -> GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, error.getCode()));
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
		}
		if (Objects
				.nonNull(getAccommodationOfferingCustomerReviewFacade().retrieveCustomerReviewByRefNumber(bookingReference,
						accommodationReviewForm.getRoomStayRefNumber(), accommodationReviewForm.getAccommodationOfferingCode()))
				|| !bookingFacade.validateUserForBooking(bookingReference))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.REVIEW_GENERIC_ERROR_CODE);
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
		}

		if (!getAccommodationOfferingCustomerReviewFacade().postReview(buildReviewData(accommodationReviewForm, bookingReference)))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.REVIEW_GENERIC_ERROR_CODE);
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
		}

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
				AccommodationaddonWebConstants.REVIEW_SUCCESS_CODE);

		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
	}


	protected ReviewData buildReviewData(final AccommodationReviewForm accommodationReviewForm, final String bookingReference)
	{
		final ReviewData reviewData = new ReviewData();
		reviewData.setHeadline(accommodationReviewForm.getHeadline());
		reviewData.setComment(accommodationReviewForm.getComment());
		reviewData.setRating(accommodationReviewForm.getRating());
		reviewData.setRoomStayRefNumber(accommodationReviewForm.getRoomStayRefNumber());
		reviewData.setAccommodationOfferingCode(accommodationReviewForm.getAccommodationOfferingCode());
		reviewData.setProductCode(accommodationReviewForm.getAccommodationCode());
		reviewData.setBookingReference(bookingReference);
		return reviewData;
	}

	@RequestMapping(value = "/get-new-dates/{bookingReference}", method = RequestMethod.POST)
	@RequireHardLogIn
	public String getUpdatedAccommodationBookingData(@PathVariable(value = "bookingReference") final String bookingReference,
			@RequestParam(value = "checkInDate", required = true) final String checkInDate,
			@RequestParam(value = "checkOutDate", required = true) final String checkOutDate, final Model model)
	{
		final AccommodationReservationData accommodationReservationData = bookingFacade
				.getFullAccommodationBookingForAmendOrder(bookingReference);

		final String errors = validateUpdateAccommodationBookingDates(bookingReference, checkInDate, checkOutDate,
				accommodationReservationData);

		if (StringUtils.isNotEmpty(errors))
		{
			model.addAttribute(AccommodationaddonWebConstants.ERROR_UPDATE_BOOKING_DATES, errors);
			return AccommodationaddonControllerConstants.Views.Pages.Hotel.UpdateBookingDatesPageJsonResponse;
		}

		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = createAccommodationAvailabilityRequestData(
				accommodationReservationData, checkInDate, checkOutDate);

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = getAccommodationOfferingFacade()
				.getSelectedAccommodationOfferingDetails(accommodationAvailabilityRequestData);

		final boolean isAccommodationAvailableForAmendment = getAccommodationOfferingFacade()
				.isAccommodationAvailableForQuickSelection(accommodationAvailabilityResponse);

		model.addAttribute(AccommodationaddonWebConstants.IS_ACCOMMODATION_AVAILABLE, isAccommodationAvailableForAmendment);

		if (!isAccommodationAvailableForAmendment)
		{
			return AccommodationaddonControllerConstants.Views.Pages.Hotel.UpdateBookingDatesPageJsonResponse;
		}


		final Map<String, String> changeDatePaymentResults = bookingFacade
				.getChangeDatePaymentResultsMap(accommodationReservationData, accommodationAvailabilityResponse);

		if (MapUtils.isEmpty(changeDatePaymentResults))
		{
			model.addAttribute(AccommodationaddonWebConstants.ERROR_UPDATE_BOOKING_DATES,
					ERROR_AMEND_BOOKING_ORDER_PRICE_CALCULATION);
			return AccommodationaddonControllerConstants.Views.Pages.Hotel.UpdateBookingDatesPageJsonResponse;
		}

		final String paymentType = changeDatePaymentResults.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS);
		model.addAttribute(TravelservicesConstants.BOOKING_PAYABLE_STATUS, paymentType);
		model.addAttribute(TravelservicesConstants.BOOKING_IS_PAYABLE,
				!StringUtils.equals(paymentType, TravelservicesConstants.ORDER_AMOUNT_PAYABLE_STATUS_REFUND));
		model.addAttribute(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE,
				changeDatePaymentResults.get(TravelservicesConstants.BOOKING_AMOUNT_PAYABLE));

		model.addAttribute(TravelservicesConstants.BOOKING_AMOUNT_PAID,
				changeDatePaymentResults.get(TravelservicesConstants.BOOKING_AMOUNT_PAID));
		model.addAttribute(AccommodationaddonWebConstants.ORDER_CODE, bookingReference);
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_RESPONSE, accommodationAvailabilityResponse);
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_ADD_TO_CART_BOOKING_FORM,
				new AccommodationAddToCartBookingForm());
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_BOOKING_CHANGE_DATE_FORM,
				new AccommodationBookingChangeDateForm());

		return AccommodationaddonControllerConstants.Views.Pages.Hotel.UpdateBookingDatesPageJsonResponse;
	}

	/**
	 * Validate Update Accommodation Booking Dates
	 *
	 * @param reservationCode
	 *           as the reservationCode
	 * @param checkInDate
	 *           as the checkInDate
	 * @param checkOutDate
	 *           as the checkOutDate
	 * @param accommodationReservationData
	 *           as the accommodationReservationData
	 * @return
	 */
	protected String validateUpdateAccommodationBookingDates(final String reservationCode, final String checkInDate,
			final String checkOutDate, final AccommodationReservationData accommodationReservationData)
	{
		if (accommodationReservationData == null)
		{
			return ERROR_AMEND_BOOKING_ORDER_ID;
		}

		if (StringUtils.isEmpty(checkInDate) || StringUtils.isEmpty(checkOutDate))
		{
			return ERROR_AMEND_BOOKING_EMPTY;
		}

		final Date dCheckInDate = TravelDateUtils.convertStringDateToDate(checkInDate, TravelservicesConstants.DATE_PATTERN);

		final Date dCheckOutDate = TravelDateUtils.convertStringDateToDate(checkOutDate, TravelservicesConstants.DATE_PATTERN);

		if (dCheckInDate == null || dCheckOutDate == null)
		{
			return ERROR_AMEND_BOOKING_DATES_PARSE;
		}

		final Date orderCheckInDate = accommodationReservationData.getRoomStays().get(0).getCheckInDate();
		final Date orderCheckOutDate = accommodationReservationData.getRoomStays().get(0).getCheckOutDate();

		final long maxAllowedDateDifference = getConfigurationService().getConfiguration()
				.getInt(TravelacceleratorstorefrontWebConstants.MAX_ALLOWED_CHECKIN_CHECKOUT_DATE_DIFFERENCE);
		if ((orderCheckInDate.compareTo(dCheckInDate) == 0 && orderCheckOutDate.compareTo(dCheckOutDate) == 0)
				|| dCheckInDate.compareTo(dCheckOutDate) > 0
				|| TravelDateUtils.getDaysBetweenDates(dCheckInDate, dCheckOutDate) > maxAllowedDateDifference)
		{
			return ERROR_AMEND_BOOKING_DATES;
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Create AccommodationAvailabilityRequestData
	 *
	 * @param accommodationReservationData
	 *           as the accommodationReservationData
	 * @param checkInDate
	 *           as the checkInDate
	 * @param checkOutDate
	 *           as the checkOutDate
	 * @return the AccommodationAvailabilityRequestData
	 */
	protected AccommodationAvailabilityRequestData createAccommodationAvailabilityRequestData(
			final AccommodationReservationData accommodationReservationData, final String checkInDate, final String checkOutDate)
	{
		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new AccommodationAvailabilityRequestData();
		final CriterionData criterion = new CriterionData();
		criterion.setAccommodationReference(accommodationReservationData.getAccommodationReference());
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		stayDateRange.setStartTime(TravelDateUtils.convertStringDateToDate(checkInDate, TravelservicesConstants.DATE_PATTERN));
		stayDateRange.setEndTime(TravelDateUtils.convertStringDateToDate(checkOutDate, TravelservicesConstants.DATE_PATTERN));
		criterion.setStayDateRange(stayDateRange);

		final List<RoomStayCandidateData> roomStayCandidateDatas = new ArrayList<>(
				accommodationReservationData.getRoomStays().size());
		accommodationReservationData.getRoomStays().forEach(roomStay -> {
			final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
			roomStayCandidateData.setRatePlanCode(roomStay.getRatePlans().get(0).getCode());
			roomStayCandidateData.setAccommodationCode(roomStay.getRoomTypes().get(0).getCode());
			roomStayCandidateData.setPassengerTypeQuantityList(roomStay.getGuestCounts());
			roomStayCandidateData.setRoomStayCandidateRefNumber(roomStay.getRoomStayRefNumber());
			roomStayCandidateData.setServices(roomStay.getServices());
			roomStayCandidateDatas.add(roomStayCandidateData);
		});
		criterion.setRoomStayCandidates(roomStayCandidateDatas);
		criterion.setCurrencyIso(accommodationReservationData.getCurrencyIso());
		accommodationAvailabilityRequestData.setCriterion(criterion);

		return accommodationAvailabilityRequestData;
	}


	@RequestMapping(value = "/pay-now/{bookingReference}", method = RequestMethod.GET)
	@RequireHardLogIn
	public String payNow(@PathVariable("bookingReference") final String bookingReference, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		if (bookingFacade.getOrderTotalToPayForOrderEntryType(bookingReference, OrderEntryType.ACCOMMODATION).doubleValue() <= 0)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.PAY_NOW_GENERIC_ERROR_CODE);
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
		}

		final Boolean amend = bookingFacade.amendOrder(bookingReference, bookingFacade.getCurrentUserUid());
		if (!amend)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					AccommodationaddonWebConstants.PAY_NOW_GENERIC_ERROR_CODE);
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
		}

		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PAY_NOW, bookingReference);

		final String paymentFlowProperty = getConfigurationService().getConfiguration().getString("payment.flow");
		if (StringUtils.isNotBlank(paymentFlowProperty))
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH + paymentFlowProperty;
		}
		return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH
				+ TravelacceleratorstorefrontWebConstants.PAYMENT_FLOW;
	}

	@RequestMapping(value = "/amend-extras/{orderCode}")
	@RequireHardLogIn
	public String amendExtras(@PathVariable final String orderCode,
			@RequestParam(value = "roomStay", required = false) final Integer roomStayRefNumberToUpdate,
			final RedirectAttributes redirectModel)
	{
		final boolean isCartCreated = accommodationAmendmentFacade.startAmendment(orderCode);
		if (!isCartCreated)
		{
			// If there was any error while creating cart from order, do not redirect to accommodation details
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
		}

		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		if (bookingFacade.checkBookingJourneyType(orderCode, BookingJourneyType.BOOKING_PACKAGE))
		{
			return Objects.nonNull(roomStayRefNumberToUpdate)
					? REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_AMENDMENT_PATH + ROOM_STAY_REQ_PARAM
							+ roomStayRefNumberToUpdate
					: REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_AMENDMENT_PATH;
		}

		if (userFacade.isAnonymousUser())
		{
			getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
		}

		return Objects.nonNull(roomStayRefNumberToUpdate)
				? REDIRECT_PREFIX + GUEST_DETAILS_PAGE + ROOM_STAY_REQ_PARAM + roomStayRefNumberToUpdate
				: REDIRECT_PREFIX + GUEST_DETAILS_PAGE;

	}

}
