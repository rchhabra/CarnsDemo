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
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Class responsible to populate the propertyMap for product with addToCartCriteria PER_PAX
 */
public class PerLegPopulatePropertyMapStrategy implements PopulatePropertyMapStrategy
{

	private TravelRouteService travelRouteService;
	private TransportOfferingService transportOfferingService;

	@Override
	public Map<String, Object> populatePropertiesMap(final String travelRouteCode, final int originDestinationRefNumber,
			final List<String> transportOfferingCodes, final List<TravellerModel> travellersList, final Boolean active,
			final AmendStatus amendStatus)
	{
		final Map<String, Object> propertiesMap = new HashMap<>();

		if (CollectionUtils.isNotEmpty(transportOfferingCodes))
		{
			final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
			transportOfferingCodes.forEach(transportOffering -> transportOfferings
					.add(getTransportOfferingService().getTransportOffering(transportOffering)));
			propertiesMap.put(TravelOrderEntryInfoModel.TRANSPORTOFFERINGS, transportOfferings);
		}

		if (StringUtils.isNotEmpty(travelRouteCode))
		{
			final TravelRouteModel travelRoute = getTravelRouteService().getTravelRoute(travelRouteCode);
			propertiesMap.put(TravelOrderEntryInfoModel.TRAVELROUTE, travelRoute);
		}

		propertiesMap.put(TravelOrderEntryInfoModel.ORIGINDESTINATIONREFNUMBER, originDestinationRefNumber);
		propertiesMap.put(AbstractOrderEntryModel.ACTIVE, active);
		propertiesMap.put(AbstractOrderEntryModel.AMENDSTATUS, amendStatus);
		return propertiesMap;
	}

	/**
	 * @return the travelRouteService
	 */
	protected TravelRouteService getTravelRouteService()
	{
		return travelRouteService;
	}

	/**
	 * @param travelRouteService
	 *           the travelRouteService to set
	 */
	@Required
	public void setTravelRouteService(final TravelRouteService travelRouteService)
	{
		this.travelRouteService = travelRouteService;
	}

	/**
	 * @return the transportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * @param transportOfferingService
	 *           the transportOfferingService to set
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

}
