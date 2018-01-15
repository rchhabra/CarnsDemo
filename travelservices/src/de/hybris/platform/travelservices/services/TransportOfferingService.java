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
package de.hybris.platform.travelservices.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * TransportOffering Service interface which provides functionality to manage Transport Offering.
 */
public interface TransportOfferingService
{

	/**
	 * Get a TransportOfferingModel by code.
	 *
	 * @param code
	 * 		the unique code for a transport offering
	 * @return TransportOfferingModel transport offering
	 */
	TransportOfferingModel getTransportOffering(String code);

	/**
	 * Get a list of all TransportOfferingModel configured in the system.
	 *
	 * @return List<TransportOfferingModel> transport offerings
	 */
	List<TransportOfferingModel> getTransportOfferings();

	/**
	 * Get a list of TransportOfferingModel by number and departureDate.
	 *
	 * @param number
	 * 		the number of requested transport offering
	 * @param departureDate
	 * 		the departure date of requested transport offering
	 * @return List<TransportOfferingModel> transport offerings
	 */
	List<TransportOfferingModel> getTransportOfferings(String number, Date departureDate);

	/**
	 * Returns a list of TransportOfferingModel based on the given list of codes.
	 *
	 * @param transportOfferingCodes
	 * 		the collection of codes
	 * @return the list of Transport Offerings found
	 */
	List<TransportOfferingModel> getTransportOfferings(Collection<String> transportOfferingCodes);

	/**
	 * Returns a map with keys being Transport Offering codes and as values the respective TransportOfferingModel.
	 *
	 * @param transportOfferingCodes
	 * 		the collection of codes
	 * @return the map of Transport Offering
	 */
	Map<String, TransportOfferingModel> getTransportOfferingsMap(Collection<String> transportOfferingCodes);

	/**
	 * Method takes a list of AbstractOrderEntryModel and turns a unique list of transport offering models
	 *
	 * @param orderEntryList
	 * 		the order entry list
	 * @return transport offerings from order entries
	 */
	List<TransportOfferingModel> getTransportOfferingsFromOrderEntries(List<AbstractOrderEntryModel> orderEntryList);

	/**
	 * Returns a list of {@link TransportOfferingModel}s generated from a given {@link ScheduleConfigurationModel}.
	 *
	 * @param scheduleConfiguration
	 * @return
	 */
	List<TransportOfferingModel> createTransportOfferingForScheduleConfiguration(ScheduleConfigurationModel scheduleConfiguration);
}
