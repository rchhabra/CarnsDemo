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
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;

import java.util.List;


/**
 * Interface that exposes Configured Accommodation specific DAO services
 */
public interface ConfiguredAccommodationDao
{

	/**
	 * this API takes an accommodation map as parameter and returns list of configured accommodation(decks, cabins, seats
	 * etc.) associated with a transport vehicle.
	 *
	 * @param accommodationMap
	 *           the accommodation map
	 * @param catalogVersion
	 *           the catalog version
	 * @return list
	 */
	List<ConfiguredAccommodationModel> findAccommodationMapConfiguration(AccommodationMapModel accommodationMap,
			CatalogVersionModel catalogVersion);

	/**
	 * This API takes an uid(of the seat selected on the seat map) and returns configured accommodation (seat) associated
	 * with that uid
	 *
	 * @param uid
	 *           the uid
	 * @param catalogVersion
	 *           the catalog version
	 * @return configured accommodation model
	 */
	ConfiguredAccommodationModel findAccommodation(String uid, CatalogVersionModel catalogVersion)
			throws AccommodationMapDataSetUpException;

}
