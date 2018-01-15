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

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler for {@link RoomRatesHandler} and {@link DealRoomRatesHandler}
 */
public abstract class AbstractRoomRatesHandler implements AccommodationDetailsHandler
{
	private EnumerationService enumerationService;

	protected boolean validateRoomRateAgainstDate(final Date date, final ProductModel roomRate)
	{
		final RoomRateProductModel roomRateProduct = (RoomRateProductModel) roomRate;
		for (final DateRangeModel dateRange : roomRateProduct.getDateRanges())
		{
			if (isValidRange(date, dateRange, roomRateProduct))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isValidDayOfWeek(final Date date, final List<DayOfWeek> daysOfWeek)
	{
		final LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		final DayOfWeek dayOfWeek = getEnumerationService()
				.getEnumerationValue(DayOfWeek.class, localDate.getDayOfWeek().toString());
		return daysOfWeek.contains(dayOfWeek);
	}

	protected void createRoomRateData(final List<RoomRateData> roomRates, final RoomRateProductModel roomRateModel,
			final Date date)
	{
		final RoomRateData roomRateData = new RoomRateData();
		roomRateData.setCode(roomRateModel.getCode());
		final StayDateRangeData dateRange = new StayDateRangeData();
		dateRange.setStartTime(date);
		roomRateData.setStayDateRange(dateRange);
		roomRates.add(roomRateData);
	}

	protected boolean isValidRange(final Date date, final DateRangeModel dateRange, final RoomRateProductModel roomRateProduct)
	{
		return !date.before(dateRange.getStartingDate()) && !date.after(dateRange.getEndingDate()) && isValidDayOfWeek(date,
				roomRateProduct.getDaysOfWeek());
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 * 		the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
