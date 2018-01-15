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
package de.hybris.platform.travelservices.service.keygenerator;

import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;


/**
 * Interface that exposes specific services for the Travel Key Generator.
 */
public interface TravelKeyGeneratorService
{
	/**
	 * <p>
	 * Method responsible for generating a unique Traveller Id. The key is made up of the following:
	 * </p>
	 * <p>
	 * key = <strong>prefix</strong> _ <strong>passengerNumber</strong> _ <strong>random generated alphanumeric</strong>
	 * </p>
	 * <ul>
	 * <li><strong>prefix</strong> - only included in key if provided</li>
	 * <li><strong>passengerNumber</strong> - only included in key if provided</li>
	 * <li><strong>random generated alphanumeric</strong> - This is configurable which can be set using setCount().
	 * Default is 8.</li>
	 * </ul>
	 * <p>
	 * If no <strong>prefix</strong> or <strong>passengerNumber</strong> is provided then the keygen will return the
	 * <strong>random generated alphanumeric</strong>
	 * </p>
	 *
	 * @param prefix
	 * 		the prefix
	 * @param passengerNumber
	 * 		the passenger number
	 * @return traveller uid
	 */
	String generateTravellerUid(String prefix, String passengerNumber);

	/**
	 * This method generates an unique code for an accommodation request
	 *
	 * @param roomStayRefNumber
	 * 		the room stay ref number
	 * @param bookingReference
	 * 		the booking reference
	 * @return string
	 */
	String generateAccommodationRequestCode(int roomStayRefNumber, String bookingReference);

	/**
	 * Generates an unique code for the given {@link TransportOfferingModel}
	 * @param transportOffering
	 * @return
	 */
	String generateTransportOfferingCode(TransportOfferingModel transportOffering);
}
