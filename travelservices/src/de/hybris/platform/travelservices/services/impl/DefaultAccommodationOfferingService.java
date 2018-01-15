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

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.AccommodationOfferingDao;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AccommodationOfferingService}
 */
public class DefaultAccommodationOfferingService implements AccommodationOfferingService
{
	private AccommodationOfferingDao accommodationOfferingDao;

	@Override
	public AccommodationOfferingModel getAccommodationOffering(final String code) throws ModelNotFoundException
	{
		return getAccommodationOfferingDao().findAccommodationOffering(code);
	}

	@Override
	public List<AccommodationOfferingModel> getAccommodationOfferings()
	{
		return getAccommodationOfferingDao().findAccommodationOfferings();
	}

	@Override
	public SearchResult<AccommodationOfferingModel> getAccommodationOfferings(final int batchSize, final int offset)
	{
		return getAccommodationOfferingDao().findAccommodationOfferings(batchSize, offset);
	}

	/**
	 * @return the accommodationOfferingDao
	 */
	protected AccommodationOfferingDao getAccommodationOfferingDao()
	{
		return accommodationOfferingDao;
	}

	/**
	 * @param accommodationOfferingDao
	 *           the accommodationOfferingDao to set
	 */
	@Required
	public void setAccommodationOfferingDao(final AccommodationOfferingDao accommodationOfferingDao)
	{
		this.accommodationOfferingDao = accommodationOfferingDao;
	}

}
