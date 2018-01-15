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
package de.hybris.platform.accommodationaddon.constants;

/**
 * Global class for all Accommodationaddon web constants. You can add global constants for your extension into this
 * class.
 */
public final class AccommodationaddonWebConstants // NOSONAR
{
	private AccommodationaddonWebConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
	public static final String ACCOMMODATION_FINDER_FORM = "accommodationFinderForm";
	public static final String ACCOMMODATION_AVAILABILITY_FORM = "accommodationAvailabilityForm";
	public static final String ACCOMMODATION_FINDER_FORM_BINDING_RESULT = "org.springframework.validation.BindingResult.accommodationFinderForm";
	public static final String ACCOMMODATION_AVAILABILITY_FORM_BINDING_RESULT = "org.springframework.validation.BindingResult.accommodationAvailabilityForm";
	public static final String ACCOMMODATION_ADD_REQUEST_FORM_RESULT = "org.springframework.validation.BindingResult.addRequestForm";
	public static final String FIELD_ERRORS = "fieldErrors";
	public static final String HAS_ERROR_FLAG = "hasErrorFlag";

	public static final String ACCOMMODATION_SELECTION_ROOT_URL = "/accommodation-search";
	public static final String ACCOMMODATION_SEARCH_RESPONSE = "accommodationSearchResponse";
	public static final String ACCOMMODATION_OFFERING_DETAILS_URL_PARAMETERS = "urlParameters";

	public static final String ACCOMMODATION_DETAILS_ROOT_URL = "/accommodation-details";

	public static final String DESTINATION_LOCATION_NAME = "destinationLocationName";
	public static final String DESTINATION_LOCATION = "destinationLocation";
	public static final String SUGGESTION_TYPE = "suggestionType";

	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String RADIUS = "radius";

	public static final String FILTER_PROPERTY_NAME = "filterPropertyName";

	public static final String ACCOMMODATION_SEARCH_PARAMS = "accommodationSearchParams";
	public static final String ACCOMMODATION_SEARCH_PARAMS_ERROR = "accommodationSearchParamsError";
	public static final String FILTER_PROPERTY_ERROR_MESSAGE = "accommodationsearch.refinement.propertyname.error";
	public static final String SORT_CODE_ERROR_MESSAGE = "accommodationsearch.refinement.sortcode.error";
	public static final String FILTER_QUERY_ERROR_MESSAGE = "accommodationsearch.refinement.query.error";

	public static final String ACCOMMODATION_RESERVATION_DATA = "reservationData";
	public static final String AVAILABLE_SERVICES = "availableServices";

	public static final String BASKET_ERROR_OCCURRED = "basket.error.occurred";
	public static final String QUANTITY_EXCEEDED = "accommodation.add.to.cart.quantity.exceeded";
	public static final String ALLOWED_NUMBER_OF_ROOMS = "allowedNumberOfRooms";
	public static final String ACCOMMODATION_SEARCH_PROPERTIES = "accommodationSearchProperties";

	public static final String PROPERTY = "property";
	public static final String CUSTOMER_REVIEW_SEARCH_PAGE_DATA = "customerReviewSearchPageData";

	public static final String ACCOMMODATION_ROOM_PREFERENCES = "roomPreferences";
	public static final String ACCOMMODATION_ROOM_PREFERENCE_TYPE = "BED_PREFERENCE";
	public static final String ACCOMMODATION_ROOM_PREFERENCE_MAP = "accommodationRoomPreferenceMap";

	public static final String REMOVE_ROOM_VALIDATION_ERROR = "guest.details.remove.room.error";
	public static final String REMOVE_ROOM_CONFIRMATION = "guest.details.remove.room.confirmation";

	public static final String IS_VALID = "isValid";

	public static final String MY_ACCOUNT_BOOKING = "myBookings";
	public static final String MY_ACCOUNT_BOOKING_IMAGES = "myBookingImages";

	public static final String ORDER_CODE = "orderCode";
	public static final String ACCOMMODATION_AVAILABILITY_RESPONSE = "accommodationAvailabilityResponse";
	public static final String IS_ACCOMMODATION_AVAILABLE = "isAccommodationAvailable";
	public static final String ERROR_UPDATE_BOOKING_DATES = "errors";
	public static final String ACCOMMODATION_ADD_TO_CART_BOOKING_FORM = "accommodationAddToCartBookingForm";
	public static final String ACCOMMODATION_BOOKING_CHANGE_DATE_FORM = "accommodationBookingChangeDateForm";
	public static final String ACCOMMODATION_ADD_TO_CART_FORM = "accommodationAddToCartForm";
	public static final String ACCOMMODATION_BEST_COMBINATION_AVAILABILITY_RESPONSE = "bestCombinationAccommodationAvailabilityResponse";
	public static final String ACCOMMODATION_BEST_COMBINATION_PRICE_DATA = "bestCombinationAccommodationPriceData";
	public static final String ACCOMMODATION_ROOM_STAY_GROUP_LIST = "roomStayGroupList";
	public static final String ACCOMMODATION_DETAILS_PAGE_URL_DATA = "accommodationDetailsPageURL";
	public final static String REQUEST_MESSAGE = "requestMessage";
	public static final String REQUEST_MESSAGE_ERROR_CODE = "accommodation.request.max.characters.exceeded";
	public static final String ADD_REQUEST_FORM = "addRequestForm";
	public static final String ACCOMMODATION_REVIEW_FORM = "accommodationReviewForm";
	public static final String REQUEST_SUBMITTED_MESSAGE = "accommodation.booking.details.page.request.successfully.added";
	public static final String REQUEST_NOT_SUBMITTED_MESSAGE = "accommodation.booking.details.page.request.unsuccessfully.added";
	public static final String REQUEST_EXCEEDED_LIMIT_MESSAGE = "accommodation.booking.details.page.request.exceeded.limit";
	public static final String REQUEST_REMOVED_MESSAGE = "accommodation.booking.details.page.request.successfully.removed";
	public static final String REQUEST_NOT_REMOVED_MESSAGE = "accommodation.booking.details.page.request.unsuccessfully.removed";

	public static final String REVIEW_HEADLINE = "headline";
	public static final String REVIEW_HEADLINE_ERROR_CODE = "accommodation.review.headline.error";
	public static final String REVIEW_BLANK_HEADLINE_ERROR_CODE = "accommodation.review.blank.headline.error";
	public static final String REVIEW_COMMENT = "comment";
	public static final String REVIEW_COMMENT_ERROR_CODE = "accommodation.review.comment.error";
	public static final String REVIEW_BLANK_COMMENT_ERROR_CODE = "accommodation.review.blank.comment.error";
	public static final String REVIEW_RATING = "comment";
	public static final String REVIEW_RATING_ERROR_CODE = "accommodation.review.rating.error";
	public static final String REVIEW_GENERIC_ERROR_CODE = "accommodation.review.generic.error";
	public static final String REVIEW_SUCCESS_CODE = "accommodation.review.success";
	public static final String SUBMITTED_REVIEWS = "customerReviews";

	public static final String CHANGE_DATE_ERROR_MESSAGE = "amendBookingErrorResult";


	public static final String PAY_NOW_GENERIC_ERROR_CODE = "accommodation.pay.now.generic.error";

	public static final String MY_ACCOUNT_REMOVE_LINKS = "removeLinks";

	public static final String MY_BOOKINGS_PAGE_SIZE = "myaccount.mybookings.page.size";
	public static final String PAGE_SIZE = "pageSize";

	public static final String AMEND_EXTRAS = "isAmendExtras";
	public static final String ERROR_ROOM_BED_PREFERENCE_ADD = "error.accommodation.room.preference.add";

}
