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

package de.hybris.platform.travelservices.utils;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.time.DateUtils;


/**
 * This class provides utility methods to help execution of the functionlaity related to RatePlan item
 */
public final class RatePlanUtils
{

	private RatePlanUtils()
	{
		//empty to avoid instantiating utils class
	}

	/**
	 * Retrieves the first RoomRateProduct which is available for given date in a given RatePlan
	 *
	 * @param ratePlan
	 * 		the rate plan
	 * @param currentDate
	 * 		the current date
	 * @return room rate product
	 */
	public static RoomRateProductModel getRoomRateForRatePlan(final RatePlanModel ratePlan, final Date currentDate)
	{
		for (final ProductModel roomRateProduct : ratePlan.getProducts())
		{
			final RoomRateProductModel roomRate = (RoomRateProductModel) roomRateProduct;
			final boolean roomRateAvailableOnDate = isRoomRateAvailableOnDate(roomRate, currentDate);
			if (roomRateAvailableOnDate)
			{
				return roomRate;
			}
		}
		return null;
	}

	/**
	 * Is room rate available on date boolean.
	 *
	 * @param roomRate
	 * 		the room rate
	 * @param currentDate
	 * 		the current date
	 * @return the boolean
	 */
	protected static boolean isRoomRateAvailableOnDate(final RoomRateProductModel roomRate, final Date currentDate)
	{
		for (final DateRangeModel dateRange : roomRate.getDateRanges())
		{
			if (!isWithinRange(currentDate, dateRange.getStartingDate(), dateRange.getEndingDate()))
			{
				continue;
			}
			final List<DayOfWeek> daysOfWeek = roomRate.getDaysOfWeek();

			// Using the LocalDateTime with English locale as the DayOfWeek hybris enum is English based.
			final String dayOfWeekFromCurrentDate = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault())
					.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

			for (final DayOfWeek dayOfWeek : daysOfWeek)
			{
				if (dayOfWeekFromCurrentDate.equalsIgnoreCase(dayOfWeek.getCode()))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Is within range boolean.
	 *
	 * @param testDate
	 * 		the test date
	 * @param startDate
	 * 		the start date
	 * @param endDate
	 * 		the end date
	 * @return the boolean
	 */
	protected static boolean isWithinRange(final Date testDate, final Date startDate, final Date endDate)
	{
		return DateUtils.isSameDay(testDate, startDate) || DateUtils.isSameDay(testDate, endDate) || (testDate.after(startDate)
				&& testDate.before(endDate));
	}

}
