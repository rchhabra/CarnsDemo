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
package de.hybris.platform.travelservices.constants;

/**
 * Global class for all Travelacceleratorstorefront validator constants. You can add global constants for your extension
 * into this class.
 */
public final class TravelacceleratorstorefrontValidationConstants
{
	public static final String FORM_GLOBAL_ERROR = "form.global.error";

	public static final String REGEX_LETTERS_NUMBERS_DASHES = "^[a-zA-Z0-9\\-]+$";
	public static final String REGEX_LETTERS_DASHES_SPACES_FIRST_LETTER = "^[a-zA-Z]([a-z A-Z-\\']{0,34})$";
	public static final String REG_EX_DD_MM_YYY_WITH_FORWARD_SLASH = "^\\d{2}\\/\\d{2}\\/\\d{4}$";
	public static final String REGEX_LETTERS_AND_UNDERSCORES = "^[A-Za-z_]+$";
	public static final String REGEX_LETTERS_DASHES_PARENTHESES_SPACES = "^[a-zA-Z\\-\\(\\)\\s]+$";
	public static final String REGEX_LETTERS_ONLY = "^[a-zA-Z]+$";
	public static final String REGEX_SPECIAL_LETTERS_NUMBER_SPACES = "^[\\p{L}\\p{N}\\-'\\s]+$";
	public static final int MAX_LENGTH_35 = 35;
	public static final int MAX_LENGTH_30 = 30;
	public static final double MIN_LATITUDE = -90;
	public static final double MAX_LATITUDE = 90;
	public static final double MIN_LONGITUDE = -180;
	public static final double MAX_LONGITUDE = 180;
	public static final double MAX_RADIUS = 50000;
	public static final String ERROR_TYPE_EMPTY_FIELD = "EmptyField";
	public static final String ERROR_TYPE_INVALID_DATE = "InvalidDate";
	public static final String ERROR_TYPE_INVALID_DATE_FORMAT = "InvalidDateFormat";
	public static final String ERROR_TYPE_PAST_DATE = "PastDate";
	public static final String REGEX_PASSENGER_TYPE_QUANTITY = "\\b\\d+[-][a-zA-Z]+";
	public static final String ERROR_TYPE_OUT_OF_RANGE = "OutOfRange";
	public static final String ERROR_TYPE_INVALID_QUANTITY = "InvalidQuantity";
	public static final String ERROR_TYPE_MAXIMUM_QUANTITY_EXCEEDED = "Maximum.Quantity.Exceeded.Guests";
	public static final String ERROR_TYPE_NO_PASSENGER = "NoPassengerSelected.accommodationFinderForm.guest";
	public static final String ERROR_TYPE_INVALID_NUMBER_OF_ADULTS = "InvalidNumberOfAdults.accommodationFinderForm.guest";
	public static final String NUMBER_OF_ROOMS = "numberOfRooms";
	public static final String PART_HOTEL_STAY = "partHotelStay";
	public static final String CHECKIN_DATE = "checkInDateTime";
	public static final String CHECKOUT_DATE = "checkOutDateTime";
	public static final String ACCOMMODATION_FORM = "accommodationFinderForm";
	public static final String PASSENGER_TYPE_ADULT = "adult";
	public static final String PASSENGER_TYPE_CHILD = "child";
	public static final String ACCOMMODATION_OFFERING_CODE = "accommodationOfferingCode";
	public static final String STARTING_DATE = "startingDate";
	public static final String ENDING_DATE = "endingDate";

	public static final String REGEX_AT_LEAST_ONE_NUMBER = ".*[0-9]+";
	public static final String REGEX_NUMBER_AND_SPECIAL_CHARS = "^[\\+?[0-9\\(\\).\\s-]]{1,50}$";
	public static final String REGEX_LETTERS_PIPELINE = "^([a-zA-Z]+((\\|[a-zA-Z]+)?)*|[a-zA-Z0-9_]+)$";
	public static final String REGEX_LETTERS_COMMAS_SPACES_DASHES_PARENTHESIS = "[\\p{L}][\\p{L}\\p{N}\\,\\.\\s\\-\\(\\)\\:]+$";
	public static final String REGEX_LETTERS_DASHES = "^[a-zA-Z\\-]+$";
	public static final String REGEX_NUMBER_DASH_LETTERS_LIST = "^[0-9]+-([a-zA-Z]+)((\\,[0-9]+-([a-zA-Z]+))*)$";
	public static final String REGEX_QUERY_PARAMETER = "^(:?[\\p{L}\\p{N}\\-\\+\\/\\s\\p{Sc}\\|]*)*$";
	public static final String REGEX_ALPHANUMERIC = "^[a-zA-Z0-9_\\-]+$";

	private TravelacceleratorstorefrontValidationConstants()
	{
		// empty to avoid instantiating this constant class
	}

}
