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
 *
 */

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.travelservices.dao.AccommodationDao;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.AccommodationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AccommodationService}
 */
public class DefaultAccommodationService implements AccommodationService
{
	private AccommodationDao accommodationDao;

	@Override
	public List<AccommodationModel> getAccommodationForAccommodationOffering(final String accommodationOfferingCode)
	{
		return getAccommodationDao().findAccommodationForAccommodationOffering(accommodationOfferingCode);
	}

	@Override
	public AccommodationModel getAccommodationForAccommodationOffering(final String accommodationOfferingCode,
			final String accommodationCode)
	{
		return getAccommodationDao().findAccommodationForAccommodationOffering(accommodationOfferingCode, accommodationCode);
	}

	/**
	 * @return the accommodationDao
	 */
	protected AccommodationDao getAccommodationDao()
	{
		return accommodationDao;
	}

	/**
	 * @param accommodationDao
	 *           the accommodationDao to set
	 */
	@Required
	public void setAccommodationDao(final AccommodationDao accommodationDao)
	{
		this.accommodationDao = accommodationDao;
	}

}
