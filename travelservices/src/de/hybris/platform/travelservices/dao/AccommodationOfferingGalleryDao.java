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
 */

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.accommodation.AccommodationOfferingGalleryModel;


/**
 * Interface which provides functionality to manage AccommodationOfferingGallery item
 */
public interface AccommodationOfferingGalleryDao extends Dao
{
	/**
	 * Returns AccommodationOfferingGallery for given code
	 *
	 * @param code
	 * 		the code
	 * @return accommodation offering gallery
	 */
	AccommodationOfferingGalleryModel findAccommodationOfferingGallery(final String code);

	/**
	 * Returns AccommodationOfferingGallery for given code and catalog version
	 *
	 * @param code
	 * 		the code
	 * @param catalogVersionModel
	 * 		the catalog version model
	 * @return accommodation offering gallery
	 */
	AccommodationOfferingGalleryModel findAccommodationOfferingGallery(final String code,
			final CatalogVersionModel catalogVersionModel);
}
