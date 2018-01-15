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
package de.hybris.platform.travelfacades.constants;

/**
 * Global class for all Travelfacades constants. You can add global constants for your extension into this class.
 */
public final class TravelfacadesConstants extends GeneratedTravelfacadesConstants
{
	public static final String EXTENSIONNAME = "travelfacades";

	// implement here constants used by this extension

	public static final int ARRIVAL_DATE_FROM_THRESHOLD = 2;
	public static final int ARRIVAL_DATE_TO_THRESHOLD = 9;

	public static final int OUTBOUND_REFERENCE_NUMBER = 0;
	public static final int INBOUND_REFERENCE_NUMBER = 1;

	public static final int DEFAULT_MAX_QUANTITY_RESTRICTION = -1;
	public static final int DEFAULT_MIN_QUANTITY_RESTRICTION = 0;
	public static final String DEFAULT_ADD_TO_CART_CRITERIA = "PER_LEG_PER_PAX";
	public static final String PER_LEG_ADD_TO_CART_CRITERIA = "PER_LEG";

	public static final int MIN_BOOKING_ADVANCE_TIME = 2;

	public static final int DEFAULT_QUANTITY_FOR_ADULTS = 1;
	public static final int DEFAULT_QUANTITY_FOR_NON_ADULTS = 0;

	// SOLR Fields
	public static final String SOLR_FIELD_NUMBER = "number";
	public static final String SOLR_FIELD_TRAVEL_PROVIDER = "travelProvider";
	public static final String SOLR_FIELD_DEPARTURE_TIME = "departureTime";
	public static final String SOLR_FIELD_ARRIVAL_TIME = "arrivalTime";
	public static final String SOLR_FIELD_DEPARTURE_TIMEZONE_OFFSET = "departureTimezoneOffset";
	public static final String SOLR_FIELD_ARRIVAL_TIMEZONE_OFFSET = "arrivalTimezoneOffset";
	public static final String SOLR_FIELD_DEPARTURE_TIME_ZONE_ID = "departureTimeZoneId";
	public static final String SOLR_FIELD_ARRIVAL_TIME_ZONE_ID = "arrivalTimeZoneId";
	public static final String SOLR_FIELD_CODE = "code";

	public static final String SOLR_FIELD_ORIGIN_TERMINAL_CODE = "originTerminalCode";
	public static final String SOLR_FIELD_DESTINATION_TERMINAL_CODE = "destinationTerminalCode";
	public static final String SOLR_FIELD_ORIGIN_TRANSPORT_FACILITY = "originTransportFacility";
	public static final String SOLR_FIELD_ORIGIN_TRANSPORT_FACILITY_NAME = "originTransportFacilityName";
	public static final String SOLR_FIELD_DESTINATION_TRANSPORT_FACILITY = "destinationTransportFacility";
	public static final String SOLR_FIELD_DESTINATION_TRANSPORT_FACILITY_NAME = "destinationTransportFacilityName";
	public static final String SOLR_FIELD_DEPARTURE_LOCATION = "departureLocation";
	public static final String SOLR_FIELD_ACTIVITY = "activity";
	public static final String SOLR_SEARCH_TYPE_ACTIVITY = "ACTIVITY";
	public static final String SOLR_SEARCH_TYPE_TRANSPORT_OFFERING = "TRANSPORT_OFFERING";
	public static final String SOLR_FIELD_ORIGIN_LOCATION_CITY = "originLocationCity";
	public static final String SOLR_FIELD_DESTINATION_LOCATION_CITY = "destinationLocationCity";
	public static final String SOLR_FIELD_ORIGIN_LOCATION_COUNTRY = "originLocationCountry";
	public static final String SOLR_FIELD_DESTINATION_LOCATION_COUNTRY = "destinationLocationCountry";
	public static final String SOLR_FIELD_ORIGIN_LOCATION_NAME = "originLocationName";
	public static final String SOLR_FIELD_ORIGIN_LOCATION_DATA = "originLocationData";
	public static final String SOLR_FIELD_DESTINATION_LOCATION_NAME = "destinationLocationName";
	public static final String SOLR_FIELD_DESTINATION_LOCATION_DATA = "destinationLocationData";
	public static final String SOLR_FIELD_DURATION = "duration";
	public static final String SOLR_FIELD_VEHICLE_INFORMATION_NAME = "vehicleInformationName";
	public static final String SOLR_FIELD_VEHICLE_INFORMATION_CODE = "vehicleInformationCode";
	public static final String SOLR_FIELD_TRAVEL_SECTOR_CODE = "travelSectorCode";
	public static final String SOLR_FIELD_STATUS = "status";

	public static final String TRAVELLER_TYPE_PASSENGER = "PASSENGER";
	public static final String PASSENGER_TYPE_NAME_INFANT = "Infant";
	public static final String PASSENGER_TYPE_NAME_CHILD = "Child";
	public static final String PASSENGER_TYPE_NAME_ADULT = "Adult";
	public static final String PASSENGER_TYPE_CODE_INFANT = "infant";
	public static final String PASSENGER_TYPE_CODE_CHILD = "child";
	public static final String PASSENGER_TYPE_CODE_ADULT = "adult";

	public static final String TRAVEL_ROUTE = "TravelRoute";
	public static final String TRANSPORT_OFFERING = "TransportOffering";

	public static final Object DEFAULT_OFFER_GROUP_TO_OD_MAPPING = "DEFAULT";

	public static final String MIN_CHECKIN_TIME_PROPERTY = "min.checkin.time";
	public static final String MAX_CHECKIN_TIME_PROPERTY = "max.checkin.time";

	public static final String PRICE_ROW_ROUTE = "travelRouteCode";
	public static final String PRICE_ROW_SECTOR = "travelSectorCode";
	public static final String PRICE_ROW_TRANSPORT_OFFERING = "transportOfferingCode";

	public static final String ORIGIN = "origin";
	public static final String DESTINATION = "destination";

	public static final String ACCOMMODATION = "ACCOMMODATION";
	public static final String TRANSPORT = "TRANSPORT";

	public static final String BOOKING_TRANSPORT_ONLY = "BOOKING_TRANSPORT_ONLY";
	public static final String BOOKING_ACCOMMODATION_ONLY = "BOOKING_ACCOMMODATION_ONLY";
	public static final String BOOKING_TRANSPORT_ACCOMMODATION = "BOOKING_TRANSPORT_ACCOMMODATION";
	public static final String BOOKING_PACKAGE = "BOOKING_PACKAGE";

	public static final String FILTER_TRANSPORTOFFERING_ENABLED = "filtering.transportoffering.enabled";

	public static final String PRICE_DISPLAY_PASSENGER_TYPE = "fare.selection.itinerary.price.display.passengertype";
	public static final String MODEL_PRICE_DISPLAY_PASSENGER_TYPE = "priceDisplayPassengerType";

	public static final String CUSTOMER_SAVED_SEARCH_MAX_NUMBER = "customer.saved.search.max.number";
	public static final String MAX_TRANSPORT_GUEST_QUANTITY = "finder.transport.max.guests";
	public static final String MAX_ACCOMMODATION_GUEST_QUANTITY = "finder.accommodation.max.guests";
	public static final String MAX_TRANSPORT_ACCOMMODATION_QUANTITY = "finder.transport.accommodation.max.guests";
	public static final String MAX_PACKAGE_GUEST_QUANTITY = "finder.package.max.guests";
	public static final String MAX_ACCOMMODATION_QUANTITY = "accommodationfinder.max.accommodations";
	public static final String GOOGLE_API_KEY = "google.api.key";

	public static final String SOLR_FIELD_PROPERTY_CODE = "propertyCode";
	public static final String SOLR_FIELD_PROPERTY_NAME = "propertyName";
	public static final String SOLR_FIELD_NUMBER_OF_ADULTS = "numberOfAdults";
	public static final String SOLR_FIELD_DATE_OF_STAY = "dateOfStay";
	public static final String SOLR_FIELD_PRICE_VALUE = "priceValue";
	public static final String SOLR_FIELD_TAX_VALUE = "taxValue";
	public static final String SOLR_FIELD_LOCATION_CODES = "locationCodes";
	public static final String SOLR_FIELD_LOCATION_NAMES = "locationNames";
	public static final String SOLR_FIELD_POSITION_COORDINATES = "latlon";
	public static final String SOLR_FIELD_RATE_PLAN_CONFIGS = "ratePlanConfigs";
	public static final String SOLR_FIELD_ADDRESS = "address";
	public static final String SOLR_FIELD_CONTACT_NUMBER = "contactNumber";
	public static final String SOLR_FIELD_STAR_RATING = "starRating";
	public static final String SOLR_FIELD_AVERAGE_USER_RATING = "averageUserRating";
	public static final String SOLR_FIELD_NUMBER_OF_REVIEWS = "numberOfReviews";
	public static final String SOLR_FIELD_IMAGE_URL = "imageUrl";
	public static final String SOLR_FIELD_ACCOMMODATION_INFOS = "accommodationInfos";
	public static final String SOLR_FIELD_MIN_CHILDREN_COUNT = "minChildrenCount";
	public static final String SOLR_FIELD_MAX_CHILDREN_COUNT = "maxChildrenCount";
	public static final String SOLR_FIELD_BOOSTED = "boosted";

	public static final String MULTI_VALUE_FILTER_TERM_SEPARATOR = "#";
	public static final String SOLR_LATITUDE_LONGITUDE_POSITION_SEPARATOR = ",";

	public static final String ACCOMMODATION_SUGGESTIONS_PAGE_SIZE = "accommodationfinder.suggestions.max.result";
	public static final String ACCOMMODATION_SUGGESTIONS_MAX_LOCATION_SIZE = "accommodationfinder.suggestions.max.location";
	public static final String ACCOMMODATION_AUTOSUGGESTION_RADIUS = "accommodation.autosuggestion.radius.";
	public static final String ACCOMMODATION_OFFERING_NUMBER_OF_REVIEWS = "accommodation.offering.review.max.number";
	public static final String ACCOMMODATION_DETAILS_REVIEWS_PAGE_SIZE = "accommodation.details.reviews.page.size";
	public static final int DEFAULT_GUEST_QUANTITY = 0;
	public static final int DEFAULT_ADULT_QUANTITY = 1;
	public static final String ACCOMMODATION_SEARCH_RESULT_PAGE_SIZE = "accommodation.search.results.page.size";
	public static final String ACCOMMODATION_SEARCH_DEFAULT_SORT_KEY = "DEFAULT";

	public static final String CUSTOMER_REVIEW_DEFAULT_STATUS = "accommodation.customer.review.default.status";

	public static final String DEFAULT_LOCATION_TYPE = "search.default.location.type";

	public static final String SESSION_PAY_NOW = "sessionPayNow";
	public static final String SESSION_CHANGE_DATES = "sessionChangeDates";

	public static final String DEFAULT_PRODUCT_UNIT_CODE = "travel.default.product.unit";
	public static final String CONFIG_BUNDLE_SEAT_AVAILABILITY_CHECK = "bundle.seat.stock.availability.check";

	private TravelfacadesConstants()
	{
		// empty to avoid instantiating this constant class
	}
}
