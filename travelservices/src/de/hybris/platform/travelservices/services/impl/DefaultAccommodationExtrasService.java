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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.dao.AccommodationExtrasDao;
import de.hybris.platform.travelservices.services.AccommodationExtrasService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;


/**
 * Default implementation of {@link AccommodationExtrasService}
 */
public class DefaultAccommodationExtrasService implements AccommodationExtrasService
{

	private AccommodationExtrasDao accommodationExtrasDao;

	@Override
	public List<ProductModel> getExtrasForAccommodationOffering(final String accommodationOfferingCode)
	{
		return getAccommodationExtrasDao().findExtras(accommodationOfferingCode);
	}

	/**
	 * @return the accommodationExtrasDao
	 */
	protected AccommodationExtrasDao getAccommodationExtrasDao()
	{
		return accommodationExtrasDao;
	}

	/**
	 * @param accommodationExtrasDao
	 *           the accommodationExtrasDao to set
	 */
	@Required
	public void setAccommodationExtrasDao(final AccommodationExtrasDao accommodationExtrasDao)
	{
		this.accommodationExtrasDao = accommodationExtrasDao;
	}

}
