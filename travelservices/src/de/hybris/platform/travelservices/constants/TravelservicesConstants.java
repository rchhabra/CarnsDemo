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
 */
package de.hybris.platform.travelservices.constants;

/**
 * Global class for all Travelservices constants. You can add global constants for your extension into this class.
 */
public final class TravelservicesConstants extends GeneratedTravelservicesConstants
{
	public static final String EXTENSIONNAME = "travelservices";

	// implement here constants used by this extension

	public static final int OUTBOUND_REFERENCE_NUMBER = 0;
	public static final int INBOUND_REFERENCE_NUMBER = 1;

	public static final String DATE_PATTERN = "dd/MM/yyyy";

	public static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

	public static final String TIME_PATTERN = "HH:mm:ss";

	public static final String REDIRECT_PREFIX = "redirect:";

	// SOLR Search Constants
	public static final String SEARCH_KEY_ORIGIN_TERMINAL_CODE = "originTerminalCode";

	public static final String SEARCH_KEY_DEPARTURE_DATE = "departureDate";

	public static final String SEARCH_KEY_DESTINATION_TERMINAL_CODE = "destinationTerminalCode";

	public static final String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static final String SOLR_FQ_NOT_OPERATOR = "-";

	public static final String ACTIVITY_SEARCH = "ACTIVITY";

	public static final String SEARCH_KEY_ORIGIN_TRANSPORTFACILITY_CODE = "originTransportFacility";

	public static final String SEARCH_KEY_DESTINATION_TRANSPORTFACILITY_CODE = "destinationTransportFacility";

	public static final String SEARCH_KEY_ORIGIN_LOCATION_CITY = "originLocationCity";

	public static final String SEARCH_KEY_DESTINATION_LOCATION_CITY = "destinationLocationCity";

	public static final String SEARCH_KEY_STATUS = "status";

	public static final String SEARCH_KEY_LATLON = "latlon_location_rpt";

	public static final String SOLR_GROUP_KEY_ORIGIN_LOCATION_HIERARCHY = "originLocationHierarchy";

	public static final String SOLR_GROUP_KEY_DESTINATION_LOCATION_HIERARCHY = "destinationLocationHierarchy";

	public static final String SOLR_FIELD_ACTIVITY = "activity";

	public static final String SOLR_FIELD_ORIGIN_LOCATION_DATA = "originLocationData";

	public static final String SOLR_FIELD_DESTINATION_LOCATION_DATA = "destinationLocationData";

	public static final String SOLR_FIELD_ALL_ORIGINS_TO_DESTINATION = "allOriginsToDestination";

	public static final String SOLR_SEARCH_TYPE_ACTIVITY = "ACTIVITY";

	public static final String SOLR_SEARCH_TYPE_SUGGESTIONS_ORIGIN = "SUGGESTIONS_ORIGIN";

	public static final String SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION = "SUGGESTIONS_DESTINATION";

	public static final String SOLR_FIELD_RADIUS = "radius";

	public static final String SOLR_FIELD_POSITION = "position";

	public static final String SOLR_FIELD_PROPERTY_CODE = "propertyCode";

	public static final String TRAVEL_KEY_GENERATOR_ATTEMPT_LIMIT = "travel.key.generator.attempt.limit";

	public static final String TRAVEL_KEY_GENERATOR_RANDOM_ALPHANUMERIC_COUNT = "travel.key.generator.random.alphanumeric.count";

	public static final int DEFAULT_ADULTS = 2;

	// PRICING constants
	public static final String PRICING_SEARCH_CRITERIA_MAP = "priceSearchCriteriaMap";

	public static final String PRICING_LEVEL_DEFAULT = "default";
	public static final String PRICING_LEVEL_ROUTE = "route";
	public static final String PRICING_LEVEL_SECTOR = "sector";
	public static final String PRICING_LEVEL_TRANSPORT_OFFERING = "transportOffering";

	public static final String SEARCH_TYPE_TRANSPORT_OFFERING = "TRANSPORT_OFFERING";

	public static final String TRAVELLER_TYPE_PASSENGER = "PASSENGER";

	public static final String TAX_SEARCH_CRITERIA_MAP = "taxSearchCriteriaMap";
	public static final String SEARCH_ORIGINTRANSPORTFACILITY = "originTransportFacility";
	public static final String SEARCH_ORIGINCOUNTRY = "originCountry";
	public static final String SEARCH_PASSENGERTYPE = "passengerType";
	public static final String ADDBUNDLE_TO_CART_PARAM_MAP = "addBundleToCartParam";
	public static final String TRAVEL_ROUTE = "TravelRoute";
	public static final String TRANSPORT_OFFERING = "TransportOffering";
	public static final Object DEFAULT_OFFER_GROUP_TO_OD_MAPPING = "DEFAULT";
	public static final String REDIRECT_URL_BOOKING_DETAILS = REDIRECT_PREFIX + "/manage-booking/booking-details/";


	public static final String PRICE_ROW_ROUTE = "travelRouteCode";
	public static final String PRICE_ROW_SECTOR = "travelSectorCode";
	public static final String PRICE_ROW_TRANSPORT_OFFERING = "transportOfferingCode";

	public static final String CART_ENTRY_ORIG_DEST_REF_NUMBER = "cartEntryOrigDestRefNo";
	public static final String CART_ENTRY_PRICELEVEL = "cartEntryPriceLevel";
	public static final String CART_ENTRY_TRANSPORT_OFFERINGS = "cartEntryTransportOfferings";
	public static final String CART_ENTRY_TRAVEL_ROUTE = "cartEntryTravelRoute";
	public static final String CART_ENTRY_TRAVELLER = "cartEntryTraveller";
	public static final String CART_ENTRY_ACTIVE = "cartEntryActive";
	public static final String CART_ENTRY_AMEND_STATUS = "cartEntryAmendStatus";

	public static final String SESSION_ORIGIN_DESTINATION_REF_NUMBER = "sessionOriginDestinationRefNumber";
	public static final String SESSION_TRAVELLERS_TO_CHECK_IN = "sessionTravellersToCheckIn";

	public static final String SESSION_BOOKING_JOURNEY = "sessionBookingJourney";
	public static final String SESSION_TRIP_TYPE = "tripType";

	public static final String ADMIN_FEE_PRODUCT_CODE = "AdminFee";

	public static final String CONFIG_DROOLS_FAREFILTER_ENABLED = "drools.farefilter.enabled";

	public static final String DEFAULT_ADD_TO_CART_CRITERIA = "PER_LEG_PER_PAX";
	public static final String ACCOMMODATION_DAYS_TO_INDEX = "accommodation.index.days";

	public static final String SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_LOCATION = "SUGGESTIONS_ACCOMMODATION_LOCATION";
	public static final String SOLR_SEARCH_TYPE_SUGGESTIONS_ACCOMMODATION_PROPERTY = "SUGGESTIONS_ACCOMMODATION_PROPERTY";
	public static final String SOLR_SEARCH_TYPE_SPATIAL = "SPATIAL";
	public static final String SOLR_SEARCH_TYPE_ACCOMMODATION = "ACCOMMODATION_SEARCH";

	public static final String SEARCH_KEY_LOCATION_CODES = "locationCodes";
	public static final String SEARCH_KEY_PROPERTY_CODE = "propertyCode";

	public static final String SEARCH_KEY_LOCATION_NAMES = "locationNames";
	public static final String SEARCH_KEY_PROPERTY_NAME = "propertyName";

	public static final String PASSENGER_TYPE_CODE_INFANT = "infant";
	public static final String PASSENGER_TYPE_CODE_CHILD = "child";

	public static final String LOCATION_TYPE_COORDINATES_SEPARATOR = ",";

	public static final String ACCOMMODATION_LISTING_CUSTOM_SORT_ORDERS = "accommodation.listing.custom.sort.orders";
	public static final String ACCOMMODATION_LISTING_FACET_CODES = "accommodation.listing.facet.codes";

	public static final String ACCOMMODATION_OFFERING_CODE = "accommodationOfferingCode";
	public static final String CHECK_IN_DATE_TIME = "checkInDateTime";
	public static final String CHECK_OUT_DATE_TIME = "checkOutDateTime";

	public static final String PAYMENT_TRANSACTION_AUTH_AMOUNT = "payment.transaction.authorization.amount";

	public static final String ORDER_AMOUNT_PAYABLE_STATUS_REFUND = "REFUND";
	public static final String ORDER_AMOUNT_PAYABLE_STATUS_SAME = "SAME";
	public static final String ORDER_AMOUNT_PAYABLE_STATUS_PAYABLE = "PAYABLE";

	public static final String BOOKING_PAYABLE_STATUS = "paymentType";
	public static final String BOOKING_AMOUNT_PAYABLE = "payAmount";
	public static final String BOOKING_AMOUNT_PAID = "paidAmount";
	public static final String BOOKING_AMOUNT_CURRENCY_ISO = "currencyISO";
	public static final String BOOKING_IS_PAYABLE = "isPayable";

	public static final String PRODUCT_PRICE_ROW_ADD_CURRENCY_FILTER = "travel.product.price.row.add.currency.filter";

	public static final String SEARCH_SEED = "searchSeed";

	public static final String IP_ADDRESS = "ipAddress";

	public static final String PASSENGER_REFERENCE = "passengerReference";
	public static final String USER = "user";

	public static final String FILTER_TRAVELLERS_BY_RECIPIENT = "filterTravellersByRecipient";
	public static final String EMAIL = "email";

	private TravelservicesConstants()
	{
		// empty to avoid instantiating this constant class
	}

}
