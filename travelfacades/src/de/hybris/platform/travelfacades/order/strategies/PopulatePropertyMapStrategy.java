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

package de.hybris.platform.travelfacades.order.strategies;

import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.List;
import java.util.Map;


/**
 * Abstract class responsible to populate the propertyMap
 */
public interface PopulatePropertyMapStrategy
{

	/**
	 * Method that populates the propertyMap for a product
	 *
	 * @param travelRouteCode
	 * 		the travel route code
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 * @param transportOfferingCodes
	 * 		the transport offering codes
	 * @param travellersList
	 * 		the travellers list
	 * @param active
	 * 		the active
	 * @param amendStatus
	 * 		the amend status
	 * @return the propertyMap
	 */
	Map<String, Object> populatePropertiesMap(String travelRouteCode, int originDestinationRefNumber,
			List<String> transportOfferingCodes, List<TravellerModel> travellersList, Boolean active,
			AmendStatus amendStatus);

}
