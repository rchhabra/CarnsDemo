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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingOption;
import de.hybris.platform.travelfacades.populators.TravelRoutePopulator;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Facade that exposes Transport Offering specific services
 */
public interface TransportOfferingFacade
{

	/**
	 * Get a TransportOfferingData by code.
	 *
	 * @param code
	 *           the unique code for a transport offering
	 * @return TransportOfferingData transport offering
	 */
	TransportOfferingData getTransportOffering(String code);

	/**
	 * Get a list of TransportOfferingData by number and departureDate.
	 *
	 * @param number
	 *           the number of requested transport offering
	 * @param departureDate
	 *           the departure date of requested transport offering
	 * @param options
	 *           the options for populating the TransportOfferingData
	 * @return List<TransportOfferingData> transport offerings
	 */
	List<TransportOfferingData> getTransportOfferings(String number, Date departureDate, List<TransportOfferingOption> options);

	/**
	 * Get a list of TransportOfferingData for TransportOfferingModels.
	 *
	 * @param options
	 *           the options for populating the TransportOfferingData
	 * @param transportOfferingModelList
	 *           list of TransportOfferingModels
	 * @return List<TransportOfferingData> transport offerings
	 */
	List<TransportOfferingData> getTransportOfferings(List<TransportOfferingOption> options,
			List<TransportOfferingModel> transportOfferingModelList);

	/**
	 * Method uses the arrivalLocation and departureLocation form {@link OriginDestinationInfoData} on
	 * {@link FareSearchRequestData} to get a list of {@link TravelRouteModel} from the Database. Each
	 * {@link TravelRouteModel} is then converted to a list of {@link TravelRouteData} by the
	 * {@link TravelRoutePopulator}. The method then queries Solr for a list of {@link TransportOfferingData} for each
	 * {@link TravelSectorData} within each {@link TravelRouteData} and then runs an algorithm to match
	 * {@link TransportOfferingData} for each {@link TravelSectorData} before building the {@link ScheduledRouteData}
	 * consisting of valid {@link TransportOfferingData} combinations per {@link TravelRouteModel}
	 *
	 * @param fareSearchRequestData
	 *           the request made by the user
	 * @return List<ScheduledRouteData> scheduled routes
	 */
	List<ScheduledRouteData> getScheduledRoutes(FareSearchRequestData fareSearchRequestData);

	/**
	 * Method to retrieve suggestions for the origin search text
	 *
	 * @param text
	 *           the text
	 * @return Map of suggestions results
	 */
	Map<String, Map<String, String>> getOriginSuggestions(String text);

	/**
	 * Method to retrieve suggestions for the destination search text and origin code
	 *
	 * @param originLocation
	 *           the origin location
	 * @param text
	 *           the text
	 * @return Map of suggestions results
	 */
	Map<String, Map<String, String>> getDestinationSuggestions(String originLocation, String text);

	/**
	 * Method to retrieve suggestions for the origin search text
	 *
	 * @param text
	 *           the text
	 * @return Map of suggestions results using GlobalSuggestionData objects
	 */
	Map<GlobalSuggestionData, List<GlobalSuggestionData>> getOriginSuggestionData(String text);

	/**
	 * Method to retrieve suggestions for the destination search text and origin code
	 *
	 * @param originLocation
	 *           the origin location
	 * @param text
	 *           the text
	 * @return Map of suggestions results using GlobalSuggestionData objects
	 */
	Map<GlobalSuggestionData, List<GlobalSuggestionData>> getDestinationSuggestionData(String originLocation, String text);

	/**
	 * Method to check if the route is multi sector
	 *
	 * @param transportOfferings
	 *           the transport offerings
	 * @return boolean boolean
	 */
	boolean isMultiSectorRoute(List<String> transportOfferings);

}
