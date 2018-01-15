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

package de.hybris.platform.travelfacades.order.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelfacades.order.strategies.PopulatePropertyMapStrategy;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class responsible to populate the propertyMap for product with addToCartCriteria PER_PAX
 */
public class PerBookingPopulatePropertyMapStrategy implements PopulatePropertyMapStrategy
{

	@Override
	public Map<String, Object> populatePropertiesMap(final String travelRouteCode, final int originDestinationRefNumber,
			final List<String> transportOfferingCodes, final List<TravellerModel> travellersList, final Boolean active,
			final AmendStatus amendStatus)
	{
		final Map<String, Object> propertiesMap = new HashMap<>();

		propertiesMap.put(AbstractOrderEntryModel.ACTIVE, active);
		propertiesMap.put(AbstractOrderEntryModel.AMENDSTATUS, amendStatus);

		return propertiesMap;
	}

}
