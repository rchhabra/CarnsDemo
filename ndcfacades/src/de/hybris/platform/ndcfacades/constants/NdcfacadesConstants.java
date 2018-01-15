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
package de.hybris.platform.ndcfacades.constants;

/**
 * Global class for all Ndcfacades constants. You can add global constants for your extension into this class.
 */
public final class NdcfacadesConstants extends GeneratedNdcfacadesConstants
{
	public static final String EXTENSIONNAME = "ndcfacades";
	public static final String CABIN_CLASS = "ndc.airshoppingrq.default.cabinClass";
	public static final String NDC_ORDER_PROCESS = "ndcfacades.orderprocess";

	public static final String CUSTOMER_NAME = "Guest";
	public static final String ALLOWED_PTC_VALUES = "ndcfacades.ndcpassegnertype.allowedvalues";

	public static final String DATE_CONVERSION_ERROR = "ndcfacades.conversion.error.dateconversionerror";
	public static final String DURATION_CONVERSION_ERROR = "ndcfacades.conversion.error.durationconversionerror";
	public static final String NO_RESULT = "ndcfacades.conversion.error.noresult";
	public static final String NO_SEATS_AVAILABLE = "ndcfacades.conversion.error.noseatsavailable";
	public static final String INVALID_PASSENGER_TYPE = "ndcfacades.conversion.error.invalidpassengertype";
	public static final String INVALID_CABIN_CODE = "ndcfacades.conversion.error.invalidcabincode";
	public static final String NO_PENDING_TRANSACTION = "ndcfacades.conversion.error.nopendingtransaction";
	public static final String INVALID_MANAGE_BOOKING_INFORMATION = "ndcfacades.conversion.error.invalidmanagebookinginformation";
	public static final String INVALID_ORDER_OR_USER = "ndcfacades.conversion.error.invalidorderoruser";
	public static final String INSUFFICIENT_STOCK_LEVEL = "ndcfacades.conversion.error.insufficientstocklevel";
	public static final String GENERIC_ERROR = "ndcfacades.conversion.error.genericerror";
	public static final String INSUFFICIENT_PAYMENT_AMOUNT = "ndcfacades.conversion.error.insufficientpaymentamount";
	public static final String BASE_PRICE_NOT_FOUND = "ndcfacades.conversion.error.basepricenotfound";
	public static final String INVALID_CARD_TYPE = "ndcfacades.conversion.error.invalidcardtype";
	public static final String INVALID_OFFER_COMBINATION = "ndcfacades.conversion.error.invalidoffercombination";
	public static final String NO_FARE_PRODUCT = "ndcfacades.conversion.error.nofareproduct";
	public static final String MISSING_TRAVELER_OFFER_ITEM_ID_ASSOCIATION = "ndcfacades.conversion.error.missingtravelerofferitemidassociation";
	public static final String INVALID_ORIGIN_DESTINATION_KEY = "ndcfacades.offers.error.invalidorigindestinationkey";
	public static final String SERVICE_UNAVAILABLE = "ndcfacades.offers.error.serviceunavailable";
	public static final String MISSING_PAYMENT_INFORMATION = "ndcfacades.offers.error.missingpaymentinformation";
	public static final String SEATS_UNAVAILABLE = "ndcfacades.offers.error.seatunavailable";
	public static final String INVALID_ACTION = "ndcfacades.validation.error.invalidaction";
	public static final String SERVICE_OUT_OF_STOCK = "ndcfacades.offers.error.serviceoutofstock";
	public static final String BAGGAGE_UNAVAILABLE = "ndcfacades.offers.error.baggageunavailable";
	public static final String BAGGAGE_OUT_OF_STOCK = "ndcfacades.offers.error.baggageoutofstock";
	public static final String ORDER_CREATE_SEAT_UNAVAILABLE = "ndcfacades.offers.error.ordercreateseatunavailable";
	public static final String ORDER_CREATE_SEAT_INVALID_BUNDLE = "ndcfacades.offers.error.ordercreateseatinvalidbundle";
	public static final String ORDER_CREATE_SEAT_INVALID = "ndcfacades.offers.error.ordercreateseatinvalid";
	public static final String SEAT_LOCATION_KEY_MISMATCH = "ndcfacades.conversion.error.seatlocationkeymismatch";
	public static final String ORDER_NOT_PAYED = "ndcfacades.offers.error.ordernotpayed";
	public static final String IMPOSSIBLE_TO_PERFORM_SPECIFIED_ACTION = "ndcfacades.offers.error.impossibletoperformspecifiedaction";
	public static final String MISSING_METADATA_CURRENCY = "ndcfacades.validation.error.missingmetadatacurrency";
	public static final String MISSING_SERVICE_ID = "ndcfacades.validation.error.missingserviceid";
	public static final String INVALID_SERVICE_ID = "ndcfacades.validation.error.invalidserviceid";
	public static final String ORDER_AMEND_INVALID_TRANSPORT_OFFERING = "ndcfacades.offers.error.orderamendinvalidtransportofferingcode";
	public static final String MISSING_TRAVELER_INFORMATION = "ndcfacades.offers.error.missingtravelerinformation";

	public static final String INVALID_COUNTRY_CODE = "ndcfacades.offers.error.invalidcountrycode";

	public static final String MISSING_ACTION_TYPE = "ndcfacades.offers.error.missingactiontype";
	public static final String MISSING_SEAT_ITEM = "ndcfacades.offers.error.missingseatitem";

	public static final String OFFER_ITEM_ID_PATTERN = "^([A-Z]{3})?\\|[0-1]\\|[A-Z]{3}(\\_[A-Z]{3}){1,}]*\\|[A-Za-z0-9\\_|\\#\\|]*$";

	public static final String BASE_PRICE = "ndcfacades.conversion.baseprice";

	public static final String DEFAULT_LOCATION_TYPE = "AIRPORTGROUP";

	public static final String OWNER = "ndcfacades.owner";

	public static final String FLIGHT = "FL";
	public static final String NDC_MESSAGE_NAME = "TRVACC NCD Gateway";
	public static final String NDC_REFERENCE_VERSION = "1";

	public static final int PRECISION = 2;

	public static final int ASSOCIATED_SERVICE_BUNDLE_NUMBER = 0;
	public static final int DEFAULT_ANCILLARY_QUANTITY = 1;

	public static final boolean SERVICE_TO_ALL_PTC = true;

	public static final String NDC_OFFER_ITEM_ID_MAPPING = "ndcfacades.offers.offeritemidmapping";

	public static final String SETTLEMENT_METHOD = "ES";
	public static final String SETTLEMENT_DEF = "EMD Standalone";

	public static final String CLASS_OF_SERVICE_CODE_VALUES = "ndcfacades.flight.classofservice.code.allowedvalues";
	public static final String CLASS_OF_SERVICE_CODE_DEFAULT_VALUES = "M,J";
	public static final String COMMA_SEPARATOR = ",";

	private NdcfacadesConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
