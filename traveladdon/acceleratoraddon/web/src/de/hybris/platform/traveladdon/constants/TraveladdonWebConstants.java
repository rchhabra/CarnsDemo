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
package de.hybris.platform.traveladdon.constants;

/**
 * Global class for all Traveladdon web constants. You can add global constants for your extension into this class.
 */
public final class TraveladdonWebConstants // NOSONAR
{
	public static final String TRANSPORT_OFFERING_STATUS_FORM = "transportOfferingStatusForm";
	public static final String TRANSPORT_OFFERING_STATUS_FORM_BINDING_RESULT = "org.springframework.validation.BindingResult.transportOfferingStatusForm";
	public static final String TRANSPORT_OFFERING_DATA_LIST = "transportOfferingDataList";
	public static final String HAS_ERROR_FLAG = "hasErrorFlag";
	public static final String FIELD_ERRORS = "fieldErrors";
	public static final String FARE_FINDER_FORM = "fareFinderForm";
	public static final String ADD_BUNDLE_TO_CART_FORM = "addBundleToCartForm";
	public static final String FARE_SEARCH_REQUEST_DATA = "fareSearchRequestData";
	public static final String PRICED_ITINERARY_DATE_FORMAT = "HH:mm dd MMM";
	public static final String FARE_SELECTION_TAB_DATE_FORMAT = "dd MMMM";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String TIME_FORMAT = "HH:mm";
	public static final String DATE_FORMAT_LABEL = "dateFormat";
	public static final String TIME_FORMAT_LABEL = "timeFormat";
	public static final String ADD_TO_CART_SUCCESS = "addToCartSuccess";
	public static final String PASSENGER_TYPE_ADULT = "adult";
	public static final String FARE_SELECTION = "fareSelection";
	public static final String TRIP_TYPE = "tripType";
	public static final String OUTBOUND_DATES = "outboundDates";
	public static final String INBOUND_DATES = "inboundDates";
	public static final String OUTBOUND_LOW_PRICE = "outLowestPrice";
	public static final String INBOUND_LOW_PRICE = "inLowestPrice";
	public static final String TAB_DATE_FORMAT = "fareSelectionTabDateFormat";
	public static final String PI_DATE_FORMAT = "pricedItineraryDateFormat";
	public static final String SESSION_FARE_SELECTION_DATA = "sessionFareSelection";
	public static final String PTC_FARE_BREAKDOWN_SUMMARY = "ptcFareBreakdownSummary";
	public static final String SUMMARY_TAXES_FEES = "taxesFeesSummary";
	public static final String SUMMARY_DISCOUNTS = "discountsSummary";
	public static final String SUMMARY_EXTRAS = "extrasSummary";
	public static final String SUMMARY_TOTAL = "totalSummary";
	public static final String OUTBOUND_INCLUDED_EXTRAS = "outboundIncludedExtras";
	public static final String INBOUND_INCLUDED_EXTRAS = "inboundIncludedExtras";
	public static final String PRICE_DISPLAY_PASSENGER_TYPE = "fare.selection.itinerary.price.display.passengertype";
	public static final String MODEL_PRICE_DISPLAY_PASSENGER_TYPE = "priceDisplayPassengerType";
	public static final String FARE_FINDER_FORM_BINDING_RESULT = "org.springframework.validation.BindingResult.fareFinderForm";
	public static final String OUTBOUND_REF_NUMBER = "outboundRefNumber";
	public static final String INBOUND_REF_NUMBER = "inboundRefNumber";
	public static final String REF_NUMBER = "refNumber";
	public static final String SORTING_PARAMETERS = "sortingParameters";
	public static final String SELECTED_SORTING = "selectedSorting";
	public static final String NO_OF_OUTBOUND_OPTIONS = "noOfOutboundOptions";
	public static final String NO_OF_INBOUND_OPTIONS = "noOfInboundOptions";
	public static final String ECO_BUNDLE_TYPE = "ecoBundleType";
	public static final String ECO_PLUS_BUNDLE_TYPE = "ecoPlusBundleType";
	public static final String BUSINESS_BUNDLE_TYPE = "businessBundleType";
	public static final String OUTBOUND_TAB_LINKS = "outboundTabLinks";
	public static final String INBOUND_TAB_LINKS = "inboundTabLinks";
	public static final String ROUTE = "route";
	public static final String ORIGIN = "origin";
	public static final String DESTINATION = "destination";
	public static final String LOCATION_IMAGE = "locationImage";
	public static final String FARE_SELECTION_REDIRECT_URL = "fareSelectionRedirectUrl";
	public static final String CUSTOMER_FARE_SELECTION_REDIRECT_URL = "customerFareSearchRedirectUrl";
	public static final String REMAINING_SEATS = "remainingSeats";
	public static final String DISPLAY_ORDER = "displayOrder";

	public static final String RESERVATION = "reservation";
	public static final String EXTRAS_SUMMARY = "extrasSummary";
	public static final String NEXT_URL = "nextURL";
	public static final String OUTBOUND_SECTION_START = "#y_outbound_start";
	public static final String INBOUND_SECTION_START = "#y_inbound_start";
	public static final String AMEND = "amend";

	public static final String ORDER_CODE = "orderCode";
	public static final String HIDE_BUTTON = "hideButton";

	public static final String HIDE_CONTINUE = "hideContinue";
	public static final String HIDE = "HIDE";

	public static final String SESSION_SELECTED_OUTBOUND = "sessionSelectedOutbound";
	public static final String SESSION_SELECTED_INBOUND = "sessionSelectedInbound";
	public static final String SELECTED_OUTBOUND_ID = "selectedOutboundId";
	public static final String SELECTED_OUTBOUND_BUNDLE = "selectedOutboundBundle";
	public static final String SELECTED_INBOUND_ID = "selectedInboundId";
	public static final String SELECTED_INBOUND_BUNDLE = "selectedInboundBundle";
	public static final String TRANSPORT_OFFERING_CODES = "transportOfferingCodes";
	public static final String ROUTE_CODE = "routeCode";
	public static final String BUNDLE_TYPE = "bundleType";

	public static final String SESSION_BOOKING_REFERENCE = "sessionBookingReference";
	public static final String SESSION_ORIGIN_DESTINATION_REF_NUMBER = "sessionOriginDestinationRefNumber";
	public static final String SESSION_TRAVELLERS_TO_CHECK_IN = "sessionTravellersToCheckIn";

	public static final String BOOKING_REFERENCE = "bookingReference";
	public static final String ORIGIN_DESTINATION_REF_NUMBER = "originDestinationRefNumber";
	public static final String TRAVELLERS = "travellers";
	public static final String NATIONALITIES = "nationalities";
	public static final String COUNTRIES = "countries";

	public static final String ADDITIONAL_SECURITY = "additionalSecurity";

	// URL CONSTANTS

	public static final String TRAVELLER_DETAILS_PATH = "/checkout/traveller-details";
	public static final String PAYMENT_DETAILS_PATH = "/checkout/multi/payment-method/select-flow?pci=";
	public static final String FARE_SELECTION_PATH = "/yacceleratorstorefront/fare-selection";
	public static final String FARE_SELECTION_ROOT_URL = "/fare-selection";
	public static final String ANCILLARY_ROOT_URL = "/ancillary";
	public static final String CHECK_IN_URL = "/manage-booking/check-in";
	public static final String CHECK_IN_SUCCESS_URL = "/manage-booking/check-in/success";
	public static final String CHECK_IN_FAILED_URL = "/manage-booking/check-in/failed";
	public static final String ADD_BUNDLE_URL = "/cart/addBundle";
	public static final String UPGRADE_BUNDLE_URL = "/cart/upgradeBundle";

	public static final String CANCEL_TRAVELLER_RESULT = "cancelTravellerResult";
	public static final String CANCEL_TRAVELLER_REFUND_RESULT = "cancelTravellerRefundResult";
	public static final String CANCEL_TRAVELLER_REFUND_AMOUNT = "cancelTravellerRefundAmount";
	public static final String ORIGINAL_ORDER_CODE = "originalOrderCode";

	public static final String BOOKING_DETAILS_USER_VALIDATION_ERROR = "booking.details.user.validation.error";
	public static final String DISABLE_CURRENCY_SELECTOR = "disableCurrencySelector";

	// Upgrade Bundle Section Constants
	public static final String UPGRADE_BUNDLE_PRICED_ITINERARIES = "pricedItineraries";
	public static final String IS_UPGRADE_OPTION_AVAILABLE = "isUpgradeOptionAvailable";

	public static final String MY_ACCOUNT_BOOKING = "myBookings";
	public static final String DATE_PATTERN = "datePattern";

	public static final String MY_BOOKINGS_PAGE_SIZE = "myaccount.mybookings.page.size";
	public static final String PAGE_SIZE = "pageSize";

	private TraveladdonWebConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
