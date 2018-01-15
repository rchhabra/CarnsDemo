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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.travelservices.dao.AccommodationOfferingGalleryDao;
import de.hybris.platform.travelservices.model.accommodation.AccommodationOfferingGalleryModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingGalleryService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link de.hybris.platform.travelservices.services.AccommodationOfferingGalleryService}
 */
public class DefaultAccommodationOfferingGalleryService implements AccommodationOfferingGalleryService
{
	private AccommodationOfferingGalleryDao accommodationOfferingGalleryDao;

	@Override
	public AccommodationOfferingGalleryModel getAccommodationOfferingGallery(final String code)
	{
		return getAccommodationOfferingGalleryDao().findAccommodationOfferingGallery(code);
	}

	@Override
	public AccommodationOfferingGalleryModel getAccommodationOfferingGallery(final String code,
			final CatalogVersionModel catalogVersionModel)
	{
		return getAccommodationOfferingGalleryDao().findAccommodationOfferingGallery(code, catalogVersionModel);
	}

	/**
	 * Gets accommodation offering gallery dao.
	 *
	 * @return the accommodation offering gallery dao
	 */
	protected AccommodationOfferingGalleryDao getAccommodationOfferingGalleryDao()
	{
		return accommodationOfferingGalleryDao;
	}

	/**
	 * Sets accommodation offering gallery dao.
	 *
	 * @param accommodationOfferingGalleryDao
	 * 		the accommodation offering gallery dao
	 */
	@Required
	public void setAccommodationOfferingGalleryDao(final AccommodationOfferingGalleryDao accommodationOfferingGalleryDao)
	{
		this.accommodationOfferingGalleryDao = accommodationOfferingGalleryDao;
	}
}
