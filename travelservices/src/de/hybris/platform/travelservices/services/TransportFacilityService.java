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

import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;


/**
 * Service which exposes methods relevant to TransportFacility
 */
public interface TransportFacilityService
{

	/**
	 * Returns TransportFacility for code.
	 *
	 * @param code
	 * 		the code
	 * @return TransportFacilityModel for given code
	 */
	TransportFacilityModel getTransportFacility(String code);

	/**
	 * Method to get the country for transport facility.
	 *
	 * @param transportFacility
	 * 		the transport facility
	 * @return Country model
	 */
	LocationModel getCountry(TransportFacilityModel transportFacility);

	/**
	 * Method to get the city for transport facility.
	 *
	 * @param transportFacility
	 * 		the transport facility
	 * @return City model
	 */
	LocationModel getCity(TransportFacilityModel transportFacility);
}
