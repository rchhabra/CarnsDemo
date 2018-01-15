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

package de.hybris.platform.accommodationaddon.controllers.pages;


import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.forms.AccommodationAddToCartBookingForm;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationAvailabilityForm;
import de.hybris.platform.accommodationaddon.validators.AccommodationAvailabilityValidator;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayGroupData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.AccommodationSearchFacade;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
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
 * Controller for Accommodation Details page
 */
@Controller
@RequestMapping({ "/accommodation-details", "/manage-booking/accommodation-details" })
public class AccommodationDetailsPageController extends AbstractAccommodationPageController
{
	private static final Logger LOG = Logger.getLogger(AccommodationDetailsPageController.class);
	private static final String ACCOMMODATION_DETAILS_CMS_PAGE = "accommodationDetailsPage";
	private static final String CHECKOUT_LOGIN = "/login/checkout";
	private static final String BOOKING_DETAILS_URL = "/manage-booking/booking-details/";
	private static final String ERROR_AMEND_BOOKING_EMPTY = "empty";
	private static final String ERROR_AMEND_BOOKING_DATES = "dates";
	private static final String ERROR_AMEND_BOOKING_ORDER_ID = "booking.reference";
	private static final String ACCOMMODATION_DETAILS_ROOT = "/accommodation-details/";

	@Resource(name = "accommodationAvailabilityValidator")
	private AccommodationAvailabilityValidator accommodationAvailabilityValidator;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "accommodationCartFacade")
	private AccommodationCartFacade accommodationCartFacade;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Resource(name = "accommodationSearchFacade")
	private AccommodationSearchFacade accommodationSearchFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@RequestMapping(value = "/{accommodationOfferingCode}", method = RequestMethod.GET)
	public String getAccommodationDetailsPage(@PathVariable("accommodationOfferingCode") final String accommodationOfferingCode,
			@Valid @ModelAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_FORM) final
			AccommodationAvailabilityForm accommodationAvailabilityForm,
			final BindingResult bindingResult, final HttpServletRequest request, final HttpServletResponse response,
			final RedirectAttributes redirectModel, final Model model) throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);
		if (travelCartFacade.isAmendmentCart())
		{
			if (isAmendmentJourney(request))
			{
				//In case of amendments user should be prevented from selecting rooms from a different accommodation offering
				final String currentAccommodationOffering = accommodationCartFacade.getCurrentAccommodationOffering();
				if (!StringUtils.equalsIgnoreCase(currentAccommodationOffering, accommodationOfferingCode))
				{
					return REDIRECT_PREFIX + "/";
				}
				sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
						TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
			}
			else
			{
				travelCartFacade.deleteCurrentCart();
			}
		}
		else
		{
			adjustSessionBookingJourney();
		}

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
			if (StringUtils
					.equalsIgnoreCase(sessionBookingJourney, TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION))

			{
				final String checkInDate = accommodationAvailabilityForm.getCheckInDateTime();
				final String checkOutDate = accommodationAvailabilityForm.getCheckOutDateTime();

				final String newQueryString = checkDatesAndGetNewQueryString(checkInDate, checkOutDate, request.getQueryString());
				if (StringUtils.isNotBlank(newQueryString))
				{
					return REDIRECT_PREFIX + ACCOMMODATION_DETAILS_ROOT + accommodationOfferingCode + "?" + newQueryString;
				}
			}
		}
		else
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		}

		model.addAttribute(TravelacceleratorstorefrontWebConstants.AMEND, travelCartFacade.isAmendmentCart());

		accommodationAvailabilityForm.setRoomStayCandidates(createRoomStayCandidates(request));

		accommodationAvailabilityValidator.validate(accommodationAvailabilityForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			redirectModel
					.addFlashAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_FORM_BINDING_RESULT, bindingResult);
			return REDIRECT_PREFIX + "/";
		}

		if (!travelCartFacade.isAmendmentCart())
		{
			populateBestCombinationsForAccommodation(accommodationOfferingCode, accommodationAvailabilityForm, model, request);
		}

		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData =
				createAccommodationAvailabilityRequestData(
				accommodationAvailabilityForm, accommodationOfferingCode);

		final SearchPageData<ReviewData> customerReviewsSearchPageData = getPagedAccommodationOfferingCustomerReviews(
				accommodationOfferingCode, 0);
		model.addAttribute(AccommodationaddonWebConstants.CUSTOMER_REVIEW_SEARCH_PAGE_DATA, customerReviewsSearchPageData);

		try
		{
			// We need to set this is the session for reservation component where image will be defined, it will be then removed
			// from session in itinerary totals component controller
			getSessionService()
					.setAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE, accommodationOfferingCode);
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = getAccommodationOfferingFacade()
					.getAccommodationOfferingDetails(accommodationAvailabilityRequestData);
			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_RESPONSE,
					accommodationAvailabilityResponse);
			model.addAttribute(AccommodationaddonWebConstants.IS_ACCOMMODATION_AVAILABLE,
					getAccommodationOfferingFacade().checkAvailability(accommodationAvailabilityResponse));

			model.addAttribute(TravelacceleratorstorefrontWebConstants.GOOGLE_API_KEY,
					getConfigurationService().getConfiguration().getString(TravelfacadesConstants.GOOGLE_API_KEY));
			final int maxStockLevel = getConfigurationService().getConfiguration()
					.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_MAX_STOCK_LEVEL, maxStockLevel);
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_SALES_APPLICATION,
					SalesApplication.WEB);
			storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOMMODATION_DETAILS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOMMODATION_DETAILS_CMS_PAGE));
			return getViewForPage(model);
		}
		catch (final ModelNotFoundException MNFex)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"accommodation.offering.not.found.error.message");
			return REDIRECT_PREFIX + "/";
		}
	}

	private boolean isAmendmentJourney(final HttpServletRequest request)
	{
		return StringUtils
				.containsIgnoreCase(request.getRequestURL().toString(), TravelacceleratorstorefrontWebConstants.MANAGE_BOOKING_URL);
	}

	/**
	 * Redirects user to the next checkout page which is guest details (or checkout login)
	 *
	 * @return next page
	 */
	@RequestMapping(value = "/next", method = RequestMethod.GET)
	public String nextPage()
	{
		if (userFacade.isAnonymousUser() && !travelCartFacade.isAmendmentCart())
		{
			return REDIRECT_PREFIX + CHECKOUT_LOGIN;
		}
		if (travelCartFacade.isAmendmentCart())
		{
			if (!travelCartFacade.hasCartBeenAmended())
			{
				return REDIRECT_PREFIX + BOOKING_DETAILS_URL + travelCartFacade.getOriginalOrderCode();
			}
			if (userFacade.isAnonymousUser())
			{
				getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
			}
		}
		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (StringUtils.isNotEmpty(sessionBookingJourney) && StringUtils
				.equalsIgnoreCase(sessionBookingJourney, TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION))
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.TRAVELLER_DETAILS_PATH;
		}
		return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.GUEST_DETAILS_PATH;
	}

	/**
	 * Validates if the cart contains any AccommodationOrderEntryGroups
	 *
	 * @return
	 */
	@RequestMapping(value = "/validate-cart", method = RequestMethod.GET, produces = "application/json")
	public String validateCart(final Model model)
	{
		model.addAttribute(AccommodationaddonWebConstants.IS_VALID, accommodationCartFacade.validateAccommodationCart());
		return AccommodationaddonControllerConstants.Views.Pages.Hotel.ValidateAccommodationCartResponse;
	}

	@RequestMapping("/customer-review")
	public String getPagedCustomerReviewData(
			@RequestParam(value = "accommodationOfferingCode", required = true) final String accommodationOfferingCode,
			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") final int pageNumber, final Model model)
	{
		final SearchPageData<ReviewData> customerReviewsSearchPageData = getPagedAccommodationOfferingCustomerReviews(
				accommodationOfferingCode, pageNumber);

		model.addAttribute(AccommodationaddonWebConstants.CUSTOMER_REVIEW_SEARCH_PAGE_DATA, customerReviewsSearchPageData);

		return AccommodationaddonControllerConstants.Views.Pages.Hotel.CustomerReviewPagedJsonResponse;
	}

	protected void populateBestCombinationsForAccommodation(final String accommodationOfferingCode,
			final AccommodationAvailabilityForm accommodationAvailabilityForm, final Model model, final HttpServletRequest request)
	{
		final AccommodationSearchRequestData accommodationSearchRequestData = prepareAccommodationSearchRequestData(
				accommodationOfferingCode, accommodationAvailabilityForm);
		final AccommodationSearchResponseData accommodationSearchResponseData = accommodationSearchFacade
				.doSearch(accommodationSearchRequestData);

		if (CollectionUtils.isEmpty(accommodationSearchResponseData.getProperties()))
		{
			return;
		}

		final PropertyData bestCombinationProperty = accommodationSearchResponseData.getProperties().get(0);

		if (CollectionUtils.isEmpty(bestCombinationProperty.getRatePlanConfigs()))
		{
			return;
		}

		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData =
				createAccommodationAvailabilityRequestData(
				accommodationSearchResponseData);
		if (accommodationAvailabilityRequestData == null)
		{
			return;
		}
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = getAccommodationOfferingFacade()
				.getSelectedAccommodationOfferingDetails(accommodationAvailabilityRequestData);
		final boolean isAccommodationAvailableForQuickSelection = getAccommodationOfferingFacade()
				.isAccommodationAvailableForQuickSelection(accommodationAvailabilityResponse);
		if (!isAccommodationAvailableForQuickSelection || accommodationAvailabilityResponse.getRoomStays()
				.stream()
				.filter(roomStay -> roomStay.getRatePlans().stream().filter(ratePlan -> ratePlan
						.getAvailableQuantity() < accommodationAvailabilityRequestData.getCriterion().getRoomStayCandidates().size())
						.findAny().isPresent())
				.findAny().isPresent())
		{
			return;
		}
		final List<RoomStayGroupData> roomStayGroupDatas = createRoomStayGroupDatas(accommodationAvailabilityResponse);

		if (CollectionUtils.isEmpty(roomStayGroupDatas))
		{
			return;
		}
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_ROOM_STAY_GROUP_LIST, roomStayGroupDatas);

		final StringBuilder urlParameters = new StringBuilder();
		urlParameters.append(AccommodationaddonWebConstants.ACCOMMODATION_DETAILS_ROOT_URL);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.BACK_SLASH);
		urlParameters.append(accommodationOfferingCode);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.QUESTION_MARK);
		urlParameters.append(request.getQueryString());
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_BEST_COMBINATION_AVAILABILITY_RESPONSE,
				accommodationAvailabilityResponse);
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_BEST_COMBINATION_PRICE_DATA,
				getTotalPriceForBestOfferings(accommodationAvailabilityResponse));
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_ADD_TO_CART_BOOKING_FORM,
				new AccommodationAddToCartBookingForm());
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_DETAILS_PAGE_URL_DATA, urlParameters.toString());

	}

	protected PriceData getTotalPriceForBestOfferings(
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{
		BigDecimal totalPrice = BigDecimal.valueOf(0);
		for (final RoomStayData roomStay : accommodationAvailabilityResponse.getRoomStays())
		{
			final ReservedRoomStayData reservedRoomStay = (ReservedRoomStayData) roomStay;
			totalPrice = totalPrice.add(reservedRoomStay.getTotalRate().getActualRate().getValue());
		}
		final String currencyIso = accommodationAvailabilityResponse.getRoomStays().get(0).getFromPrice().getCurrencyIso();

		return priceDataFactory.create(PriceDataType.BUY, totalPrice, currencyIso);
	}

	protected AccommodationAvailabilityRequestData createAccommodationAvailabilityRequestData(
			final AccommodationAvailabilityForm accommodationAvailabilityForm, final String accommodationOfferingCode)
	{
		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new
				AccommodationAvailabilityRequestData();
		final CriterionData criterion = new CriterionData();
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		stayDateRange.setStartTime(TravelDateUtils
				.convertStringDateToDate(accommodationAvailabilityForm.getCheckInDateTime(), TravelservicesConstants.DATE_PATTERN));
		stayDateRange.setEndTime(TravelDateUtils
				.convertStringDateToDate(accommodationAvailabilityForm.getCheckOutDateTime(), TravelservicesConstants.DATE_PATTERN));
		criterion.setStayDateRange(stayDateRange);
		criterion.setRoomStayCandidates(accommodationAvailabilityForm.getRoomStayCandidates());
		final PropertyData accommodationReferenceData = new PropertyData();
		accommodationReferenceData.setAccommodationOfferingCode(accommodationOfferingCode);
		criterion.setAccommodationReference(accommodationReferenceData);
		accommodationAvailabilityRequestData.setCriterion(criterion);

		final List<ReservedRoomStayData> reservedRoomStays = bookingFacade.getNewReservedRoomStays();
		if (CollectionUtils.isNotEmpty(reservedRoomStays))
		{
			if (DateUtils.isSameDay(reservedRoomStays.get(0).getCheckInDate(), stayDateRange.getStartTime()) && DateUtils
					.isSameDay(reservedRoomStays.get(0).getCheckOutDate(), stayDateRange.getEndTime()))
			{
				accommodationAvailabilityRequestData.setReservedRoomStays(reservedRoomStays);
			}
		}

		return accommodationAvailabilityRequestData;
	}

	protected AccommodationAvailabilityRequestData createAccommodationAvailabilityRequestData(
			final AccommodationReservationData accommodationReservationData, final String checkInDate, final String checkOutDate)
	{
		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new
				AccommodationAvailabilityRequestData();
		final CriterionData criterion = new CriterionData();
		criterion.setAccommodationReference(accommodationReservationData.getAccommodationReference());
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		stayDateRange.setStartTime(TravelDateUtils.convertStringDateToDate(checkInDate, TravelservicesConstants.DATE_PATTERN));
		stayDateRange.setEndTime(TravelDateUtils.convertStringDateToDate(checkOutDate, TravelservicesConstants.DATE_PATTERN));
		criterion.setStayDateRange(stayDateRange);

		final List<RoomStayCandidateData> roomStayCandidateDatas = new ArrayList<>(
				accommodationReservationData.getRoomStays().size());
		accommodationReservationData.getRoomStays().forEach(roomStay ->
		{
			final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
			roomStayCandidateData.setRatePlanCode(roomStay.getRatePlans().get(0).getCode());
			roomStayCandidateData.setAccommodationCode(roomStay.getRoomTypes().get(0).getCode());
			roomStayCandidateData.setPassengerTypeQuantityList(roomStay.getGuestCounts());
			roomStayCandidateData.setRoomStayCandidateRefNumber(roomStay.getRoomStayRefNumber());
			roomStayCandidateData.setServices(roomStay.getServices());
			roomStayCandidateDatas.add(roomStayCandidateData);
		});
		criterion.setRoomStayCandidates(roomStayCandidateDatas);
		accommodationAvailabilityRequestData.setCriterion(criterion);

		return accommodationAvailabilityRequestData;
	}

	protected AccommodationAvailabilityRequestData createAccommodationAvailabilityRequestData(
			final AccommodationSearchResponseData accommodationSearchResponseData)
	{
		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new
				AccommodationAvailabilityRequestData();
		final CriterionData criterion = new CriterionData();
		criterion.setAccommodationReference(accommodationSearchResponseData.getCriterion().getAccommodationReference());
		criterion.setStayDateRange(accommodationSearchResponseData.getCriterion().getStayDateRange());

		final List<RoomStayCandidateData> roomStayCandidateDatas = new ArrayList<>();

		for (final PropertyData property : accommodationSearchResponseData.getProperties())
		{
			int refNum = 0;
			for (final String ratePlanConfig : property.getRatePlanConfigs())
			{
				final String[] ratePlanConfigSplit = ratePlanConfig.split("\\|", 3);

				final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
				roomStayCandidateData.setRatePlanCode(ratePlanConfigSplit[0]);
				roomStayCandidateData.setAccommodationCode(ratePlanConfigSplit[1]);
				roomStayCandidateData.setRoomStayCandidateRefNumber(refNum++);
				try
				{
					for (int i = 0; i < Integer.parseInt(ratePlanConfigSplit[2]); i++)
					{
						roomStayCandidateDatas.add(roomStayCandidateData);
					}
				}
				catch (final NumberFormatException ex)
				{
					LOG.error("Unable to parse quantity of rateplan config with code :\t" + ratePlanConfig);
					return null;
				}
			}
		}
		criterion.setRoomStayCandidates(roomStayCandidateDatas);
		accommodationAvailabilityRequestData.setCriterion(criterion);

		return accommodationAvailabilityRequestData;
	}

	protected String validateUpdateAccommodationBookingDates(final String reservationCode, final String checkInDate,
			final String checkOutDate, final AccommodationReservationData accommodationReservationData)
	{
		if (accommodationReservationData == null)
		{
			return ERROR_AMEND_BOOKING_ORDER_ID;
		}

		if (StringUtils.isEmpty(reservationCode) || StringUtils.isEmpty(checkInDate) || StringUtils.isEmpty(checkInDate))
		{
			return ERROR_AMEND_BOOKING_EMPTY;
		}
		final Date dCheckInDate = TravelDateUtils.convertStringDateToDate(checkInDate, TravelservicesConstants.DATE_PATTERN);
		final Date dCheckOutDate = TravelDateUtils.convertStringDateToDate(checkOutDate, TravelservicesConstants.DATE_PATTERN);
		final Date orderCheckInDate = accommodationReservationData.getRoomStays().get(0).getCheckInDate();
		final Date orderCheckOutDate = accommodationReservationData.getRoomStays().get(0).getCheckOutDate();
		final long maxAllowedDateDifference = getConfigurationService().getConfiguration()
				.getInt(TravelacceleratorstorefrontWebConstants.MAX_ALLOWED_CHECKIN_CHECKOUT_DATE_DIFFERENCE);
		if (dCheckInDate == null || dCheckOutDate == null || (orderCheckInDate.compareTo(dCheckInDate) == 0
				&& orderCheckOutDate.compareTo(dCheckOutDate) == 0) || dCheckInDate.compareTo(dCheckOutDate) > 0
				|| TravelDateUtils.getDaysBetweenDates(dCheckInDate, dCheckOutDate) > maxAllowedDateDifference)
		{
			return ERROR_AMEND_BOOKING_DATES;
		}

		return StringUtils.EMPTY;
	}

	protected BigDecimal getTotalPriceForBooking(final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{
		BigDecimal totalPayablePrice = BigDecimal.valueOf(0);
		for (final RoomStayData roomStayData : accommodationAvailabilityResponse.getRoomStays())
		{
			final ReservedRoomStayData reservedRoomStayData = (ReservedRoomStayData) roomStayData;
			if (reservedRoomStayData.getTotalRate() != null)
			{
				totalPayablePrice = totalPayablePrice.add(reservedRoomStayData.getTotalRate().getActualRate().getValue());
			}
		}
		return totalPayablePrice;
	}

	protected List<RoomStayGroupData> createRoomStayGroupDatas(
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{
		if (accommodationAvailabilityResponse == null || CollectionUtils.isEmpty(accommodationAvailabilityResponse.getRoomStays()))
		{
			return Collections.emptyList();
		}

		final List<RoomStayGroupData> roomStayGroupDatas = new ArrayList<>(accommodationAvailabilityResponse.getRoomStays().size
				());

		for (final RoomStayData roomStayData : accommodationAvailabilityResponse.getRoomStays())
		{
			final ReservedRoomStayData reservedRoomStayData = (ReservedRoomStayData) roomStayData;
			if (CollectionUtils.isEmpty(reservedRoomStayData.getRatePlans()) || CollectionUtils
					.isEmpty(reservedRoomStayData.getRoomTypes()))
			{
				return Collections.emptyList();
			}
			final String accommodationCode = reservedRoomStayData.getRoomTypes().get(0).getCode();
			final String ratePlanCode = reservedRoomStayData.getRatePlans().get(0).getCode();

			if (CollectionUtils.isEmpty(roomStayGroupDatas))
			{
				roomStayGroupDatas.add(createRoomStayGroupData(accommodationCode, ratePlanCode));
				reservedRoomStayData.setGroupIndex(0);
			}
			else
			{
				boolean isSuccess = false;
				for (final RoomStayGroupData roomStayGroupData : roomStayGroupDatas)
				{
					if (StringUtils.equals(roomStayGroupData.getAccommodationCode(), accommodationCode) && StringUtils
							.equals(roomStayGroupData.getRatePlanCode(), ratePlanCode))
					{
						roomStayGroupData.setQuantity(roomStayGroupData.getQuantity() + 1);
						isSuccess = true;
					}
				}

				if (!isSuccess)
				{
					final RoomStayGroupData roomStayGroupData = createRoomStayGroupData(accommodationCode, ratePlanCode);
					roomStayGroupDatas.add(roomStayGroupData);
					reservedRoomStayData.setGroupIndex(roomStayGroupDatas.indexOf(roomStayGroupData));
				}

			}
		}
		return roomStayGroupDatas;
	}

	protected RoomStayGroupData createRoomStayGroupData(final String accommodationCode, final String ratePlanCode)
	{
		final RoomStayGroupData roomStayGroupData = new RoomStayGroupData();
		roomStayGroupData.setRatePlanCode(ratePlanCode);
		roomStayGroupData.setAccommodationCode(accommodationCode);
		roomStayGroupData.setQuantity(1);

		return roomStayGroupData;
	}

	protected void disableCachingForResponse(final HttpServletResponse response)
	{
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	}
}
