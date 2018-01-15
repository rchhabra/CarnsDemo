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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;


/**
 * Interface to retrieve vehicle configuration based on vehicle info, transport offering, route and sector.
 */
public interface TransportVehicleConfigurationMappingDao
{

	/**
	 * This API finds and return accommodation map based on vehicle info and transport offering
	 *
	 * @param vehicle
	 * 		the vehicle
	 * @param transportOffering
	 * 		the transport offering
	 * @param catalogVersion
	 * 		the catalog version
	 * @return accommodation map model
	 */
	AccommodationMapModel findAccommodationMap(TransportVehicleInfoModel vehicle, TransportOfferingModel
			transportOffering, CatalogVersionModel catalogVersion);

	/**
	 * This API finds and return accommodation map based on vehicle info and travel sector
	 *
	 * @param vehicle
	 * 		the vehicle
	 * @param travelSector
	 * 		the travel sector
	 * @param catalogVersion
	 * 		the catalog version
	 * @return accommodation map model
	 */
	AccommodationMapModel findAccommodationMap(TransportVehicleInfoModel vehicle, TravelSectorModel travelSector,
			CatalogVersionModel catalogVersion);

	/**
	 * This API finds and return accommodation map based on vehicle info and travel route
	 *
	 * @param vehicle
	 * 		the vehicle
	 * @param travelRoute
	 * 		the travel route
	 * @param catalogVersion
	 * 		the catalog version
	 * @return accommodation map model
	 */
	AccommodationMapModel findAccommodationMap(TransportVehicleInfoModel vehicle, TravelRouteModel travelRoute,
			CatalogVersionModel catalogVersion);

	/**
	 * This API finds and return accommodation map based on vehicle info
	 *
	 * @param vehicle
	 * 		the vehicle
	 * @param catalogVersion
	 * 		the catalog version
	 * @return accommodation map model
	 */
	AccommodationMapModel findAccommodationMap(TransportVehicleInfoModel vehicle, CatalogVersionModel catalogVersion);
}
