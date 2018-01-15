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

package de.hybris.platform.travelfacades.facades.accommodation.search.handlers;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.travelservices.jalo.warehouse.AccommodationOffering;

import java.util.List;
import java.util.Map;


/**
 * Interface for handlers classes that will be updating the {@link PropertyData} based on the map entry related to an
 * {@link AccommodationOffering} and {@link AccommodationSearchRequestData}
 */
public interface PropertyHandler
{
	/**
	 * Handle method.
	 *
	 * @param dayRatesForRoomStayCandidate
	 * 		the day rates for room stay candidate
	 * @param accommodationSearchRequest
	 * 		the accommodation search request
	 * @param propertyData
	 * 		the property data
	 */
	void handle(Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			AccommodationSearchRequestData accommodationSearchRequest, PropertyData propertyData);
}
