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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.travelfacades.strategies.EncodeSavedSearchStrategy;

import java.util.List;


/**
 * Implementation class for EncodeSavedSearchStrategy interface.
 */
public class DefaultEncodeSavedSearchStrategy implements EncodeSavedSearchStrategy
{
	private static final String DEPARTINGDATETIME = "departingDateTime";
	private static final String RETURNDATETIME = "returnDateTime";
	private static final String DEPARTURELOCATION = "departureLocation";
	private static final String ARRIVALLOCATION = "arrivalLocation";
	private static final String DEPARTURELOCATIONNAME = "departureLocationName";
	private static final String ARRIVALLOCATIONNAME = "arrivalLocationName";
	private static final String ARRIVALLOCATIONSUGGESTIONTYPE = "arrivalLocationSuggestionType";
	private static final String DEPARTURELOCATIONSUGGESTIONTYPE = "departureLocationSuggestionType";
	private static final String CABINCLASS = "cabinClass";
	private static final String TRIPTYPE = "tripType";
	private static final String SEPARATOR = "|";
	private static final String ASSIGNMENT = "=";

	@Override
	public String getEncodedSearch(final SavedSearchData source)
	{
		final StringBuilder encodedSearch = new StringBuilder();
		encodedSearch.append(getKeyValue(DEPARTURELOCATION, source.getDepartureLocation()));
		encodedSearch.append(getKeyValue(ARRIVALLOCATION, source.getArrivalLocation()));
		encodedSearch.append(getKeyValue(DEPARTURELOCATIONNAME, source.getDepartureLocationName()));
		encodedSearch.append(getKeyValue(ARRIVALLOCATIONNAME, source.getArrivalLocationName()));
		encodedSearch.append(getKeyValue(DEPARTINGDATETIME, source.getDepartingDateTime()));
		encodedSearch.append(getKeyValue(RETURNDATETIME, source.getReturnDateTime()));
		encodedSearch.append(getKeyValue(CABINCLASS, source.getCabinClass()));
		encodedSearch.append(getKeyValue(TRIPTYPE, source.getTripType()));
		encodedSearch.append(getKeyValue(ARRIVALLOCATIONSUGGESTIONTYPE, source.getArrivalLocationSuggestionType()));
		encodedSearch.append(getKeyValue(DEPARTURELOCATIONSUGGESTIONTYPE, source.getDepartureLocationSuggestionType()));
		encodedSearch.append(getPassengerType(source.getPassengerTypeQuantities()));
		return encodedSearch.deleteCharAt(encodedSearch.length() - 1).toString();
	}

	/**
	 * Gets key value.
	 *
	 * @param key
	 * 		the key
	 * @param value
	 * 		the value
	 * @return the key value
	 */
	protected String getKeyValue(final String key, final String value)
	{
		String keyValue = "";
		if (value != null)
		{
			keyValue = key + ASSIGNMENT + value + SEPARATOR;
		}

		return keyValue;
	}

	/**
	 * Gets passenger type.
	 *
	 * @param passengerTypeQuantityDatas
	 * 		the passenger type quantity datas
	 * @return the passenger type
	 */
	protected String getPassengerType(final List<PassengerTypeQuantityData> passengerTypeQuantityDatas)
	{
		final StringBuilder keyValue = new StringBuilder();
		passengerTypeQuantityDatas.forEach(passengerTypeQuantityData -> keyValue.append(
				getKeyValue(passengerTypeQuantityData.getPassengerType().getCode(),
						Integer.toString(passengerTypeQuantityData.getQuantity()))));
		return keyValue.toString();
	}

}
