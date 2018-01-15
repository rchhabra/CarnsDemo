/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.travelcommons.constants;

/**
 * Global class for all Travelcommons web constants. You can add global constants for your extension into this class.
 */
public final class TravelcommonsWebConstants // NOSONAR
{
	private TravelcommonsWebConstants()
	{
		//empty to avoid instantiating this constant class
	}

	public static final String TRAVEL_FINDER_FORM_BINDING_RESULT = "org.springframework.validation.BindingResult.travelFinderForm";
	// implement here constants used by this extension

	// implement here constants used by this extension
	public static final String MY_ACCOUNT_BOOKING = "myBookings";
	public static final String MY_ACCOUNT_BOOKING_IMAGES = "myBookingImages";
	public static final String MY_ACCOUNT_BOOKING_ACCOMMODATION_ROOM_MAPPING = "accommodationRoomMapping";
	public static final String DATE_PATTERN = "datePattern";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String MY_ACCOUNT_REMOVE_LINKS = "removeLinks";
	public static final String MY_BOOKINGS_PAGE_SIZE = "myaccount.mybookings.page.size";
	public static final String PAGE_SIZE = "pageSize";
	public static final String PACKAGE_RESPONSE_DATA = "packageResponseData";
	public static final String ADD_DEAL_TO_CART_FORM = "addDealToCartForm";
	public static final String DEAL_COMPONENT_ID = "dealComponentId";
	public static final String DEAL_BUNDLE_TEMPLATE_ID = "dealBundleTemplateId";
	public static final String PACKAGE_AVAILABILITY_RESPONSE = "packageAvailabilityResponse";
	public static final String IS_PACKAGE_UNAVAILABLE = "isPackageUnavailable";
	public static final String PACKAGE_DETAILS_URL_PARAMETERS = "urlParameters";
	public static final String PACKAGE_DETAILS_CHANGED_FLAG = "packageDetailsChangedFlag";
	public static final String PACKAGE_SEARCH_RESPONSE = "packageSearchResponse";
	public static final String IS_PACKAGE_IN_CART = "isPackageInCart";
	public static final String IS_DEAL_IN_CART = "isDealInCart";
	public static final String IS_PACKAGE_IN_ORDER = "isPackageInOrder";
	public static final String IS_DEAL_IN_ORDER = "isDealInOrder";
	public static final String IS_PACKAGE_BOOKING_JOURNEY = "isPackageBookingJourney";
	public static final String TRANSPORT_BOOKING_TOTAL_AMOUNT = "transportBookingTotalAmount";
	public static final String ACCOMMODATION_BOOKING_TOTAL_AMOUNT = "accommodationBookingTotalAmount";
	public static final String ADD_ROOM_PACKAGE_URL = "addRoomToPackageUrl";


	public static final String ERROR_DEAL_DEPARTURE_DATE_INVALID_FORMAT = "datepicker.departure.invalid.format";
	public static final String ERROR_DEAL_DEPARTURE_DATE_NOT_AVAILABLE = "datepicker.departure.deal.unavailable";
	public static final String ERROR_DEAL_COMPONENT_ID_EMPTY = "componentid";
	public static final String ERROR_DEAL_DEPARTURE_DATE_EMPTY = "datepicker.departure.empty";
	public static final String ERROR_DEAL_BUNDLE_ID_INVALID = "bundletemplateid.invalid";

	public static final String ERROR_DEAL_DETAILS_URL_INVALID = "error.page.dealDetails.url.invalid";

	// URL CONSTANTS
	public static final String PACKAGE_LISTING_PATH = "/package-listing";
	public static final String CHANGE_BUNDLE_URL = "/cart/package-change-transport";
	public static final String DEAL_VALID_DATES = "dealValidDates";
	public static final String DEAL_SELECTED_DEPARTURE_DATE = "dealSelectedDepartureDate";
	public static final String DEAL_CHANGE_DATE_VALIDATION_ERROR = "dealChangeDateValidationError";
	public static final String DEAL_CHANGE_DATE_VALID_RETURN_DATE = "dealChangeDateValidReturnDate";
	public static final String DEAL_RETURN_DATE = "dealReturnDate";

	public static final String FIELD_ERRORS = "fieldErrors";
	public static final String HAS_ERROR_FLAG = "hasErrorFlag";
	public static final String ERROR_MESSAGE = "errorMsg";

}
