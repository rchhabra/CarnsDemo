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
package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.travelservices.dao.GuestCountDao;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.services.GuestCountService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Class is responsible for providing concrete implementation of the GuestCountService interface. The class uses the
 * guestCountDao class to query the database and return the {@link GuestCountModel} corresponding to the search parameters.
 */
public class DefaultGuestCountService implements GuestCountService
{
	private GuestCountDao guestCountDao;

	@Override
	public GuestCountModel getGuestCount(final String passengerTypeCode, final int quantity)
	{
		return getGuestCountDao().findGuestCount(passengerTypeCode, quantity);
	}

	/**
	 * @return guestCountDao
	 */
	protected GuestCountDao getGuestCountDao()
	{
		return guestCountDao;
	}

	/**
	 * @param guestCountDao
	 *           the guestCountDao to set
	 */
	@Required
	public void setGuestCountDao(final GuestCountDao guestCountDao)
	{
		this.guestCountDao = guestCountDao;
	}

}
