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

import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.exception.PointOfServiceDaoException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Collection;
import java.util.Map;


/**
 * The interface Travel point of service dao.
 */
public interface TravelPointOfServiceDao extends PointOfServiceDao
{

	/**
	 * Get a size limited POS items which have been geocoded and have offsets null
	 *
	 * @param size
	 * 		determines how many entries are taken
	 * @return Collection of {@link PointOfServiceModel}
	 * @throws PointOfServiceDaoException
	 * 		the point of service dao exception
	 */
	Collection<PointOfServiceModel> getGeocodedPOS(int size) throws PointOfServiceDaoException;

	/**
	 * Get all POS items which have been geocoded and have offsets null
	 *
	 * @return Collection of {@link PointOfServiceModel}
	 * @throws PointOfServiceDaoException
	 * 		the point of service dao exception
	 */
	Collection<PointOfServiceModel> getGeocodedPOS() throws PointOfServiceDaoException;

	/**
	 * Get all POS items which are linked to TransportFacilities
	 *
	 * @param filterParams
	 * 		to filter the search query for PointOfService
	 * @return Collection of {@link PointOfServiceModel}
	 */
	Collection<PointOfServiceModel> getPointOfService(final Map<String, ? extends Object> filterParams);

}
