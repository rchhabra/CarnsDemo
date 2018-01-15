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
package de.hybris.platform.travelfacades.tripfinder;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;

import java.util.List;
import java.util.Map;


/**
 * The Facade is used to delegate call to the TransportOfferingSearchFacade with the required object populated.
 */
public interface TripFinderFacade
{
	/**
	 * Gets destination locations.
	 *
	 * @param activity
	 *           the activity
	 * @param originLocation
	 *           the origin location
	 * @return List<TransportOfferingData> list of transport offering data
	 */
	List<TransportOfferingData> getDestinationLocations(String activity, String originLocation);

	/**
	 * Method to get the suggestions for origin location
	 *
	 * @param originLocationCode
	 *           the origin location code
	 * @param transportOfferings
	 *           the transport offerings
	 * @return map entry for location
	 */
	Map.Entry<String, Map<String, String>> getOriginLocationSuggestion(String originLocationCode,
			List<TransportOfferingData> transportOfferings);

	/**
	 * Method to get the suggestions for destination location
	 *
	 * @param originLocationCode
	 *           the origin location code
	 * @param transportOfferings
	 *           the transport offerings
	 * @return map entry for location
	 */
	Map<String, Map<String, String>> getDestinationLocationsSuggestion(String originLocationCode,
			List<TransportOfferingData> transportOfferings);

	/**
	 * Returns the list of transport offerings according with the location passed as a parameter. If the location is
	 * already a transport offering a singleton list will be returned otherwise a list of transport offering will be
	 * returned according with the hierarchy of the locationType
	 *
	 * @param locationCode
	 * @param locationType
	 * @param activity
	 * @return
	 */
	List<TransportOfferingData> getDestinationTransportOfferings(String locationCode, String locationType, String activity);
}
