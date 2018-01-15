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

import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;

import java.util.List;


/**
 * Facade which provides methods relevant to TransportFacility
 */
public interface TransportFacilityFacade
{

	/**
	 * Method to find the nearest transport facility for the geopoint.
	 *
	 * @param geoPoint
	 * 		the geo point
	 * @param pageableData
	 * 		the pageable data
	 * @return nearest transport facility
	 */
	TransportFacilityData findNearestTransportFacility(final GeoPoint geoPoint, final PageableData pageableData);

	/**
	 * Method to find the nearest transport facility for the geopoint providing transportOffering destination based on
	 * the activity.
	 *
	 * @param geoPoint
	 * 		the geo point
	 * @param activity
	 * 		the activity
	 * @param pageableData
	 * 		the pageable data
	 * @return nearest transport facility
	 */
	TransportFacilityData findNearestTransportFacility(final GeoPoint geoPoint, final String activity,
			final PageableData pageableData);

	/**
	 * Method to find the country of a transport facility.
	 *
	 * @param transportFacilityCode
	 * 		the transport facility code
	 * @return country of transport facility
	 */
	LocationData getCountry(final String transportFacilityCode);

	/**
	 * Method to find the location of a transport facility.
	 *
	 * @param transportFacilityCode
	 * 		the transport facility code
	 * @return location of transport facility
	 */
	LocationData getLocation(final String transportFacilityCode);

	/**
	 * Method to return the TransportFacilityData based on the transportFacilityCode
	 *
	 * @param transportFacilityCode
	 * 		the transport facility code
	 * @return TransportFacilityData transport facility
	 */
	TransportFacilityData getTransportFacility(final String transportFacilityCode);

	/**
	 * Method to return the list of TransportFacilityData based on the activity
	 *
	 * @param activity
	 * 		the activity
	 * @return List<TransportFacilityData> origin transport facility
	 */
	List<TransportFacilityData> getOriginTransportFacility(String activity);
}
