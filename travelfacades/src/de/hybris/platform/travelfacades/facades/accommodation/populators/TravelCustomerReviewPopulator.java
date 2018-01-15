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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import org.springframework.beans.factory.annotation.Required;


/**
 * Travel customer review populator.
 */
public class TravelCustomerReviewPopulator implements Populator<CustomerReviewModel, ReviewData>
{
	private TimeService timeService;

	@Override
	public void populate(final CustomerReviewModel source, final ReviewData target) throws ConversionException
	{
		target.setNumOfDays(TravelDateUtils.getDaysBetweenDates(source.getCreationtime(), getTimeService().getCurrentTime()));
		target.setRoomStayRefNumber(source.getRoomStayRefNumber());
	}

	/**
	 *
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 *
	 * @param timeService
	 * 		the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}



}
