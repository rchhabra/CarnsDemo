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
package de.hybris.platform.travelservices.search.strategies.impl;

import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.search.strategies.SolrDateRangeStrategy;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;


/**
 * The Configurable Solr Date range strategy. The input date will be converted to Solr specific format. Based on the
 * configurable date range, the start and end dates will be returned in solr format
 *
 */
public class SolrConfigurableDateRangeStrategy implements SolrDateRangeStrategy
{

	String dateRange;

	protected String getDateRange()
	{
		return dateRange;
	}

	public void setDateRange(final String dateRange)
	{
		this.dateRange = dateRange;
	}

	/*
	 * The input date will be converted to Solr specific format. Based on the configurable date range, the start and end
	 * dates will be returned in solr format
	 *
	 * @param date the date to be formatted
	 *
	 * @returns string the Solr formatted date.
	 */
	@Override
	public String getSolrFormattedDate(final Date date)
	{
		if (StringUtils.isNotBlank(getDateRange()))
		{
			final int parsedDateRange = Integer.parseInt(getDateRange());
			Date startDate;
			Date endDate;
			if (parsedDateRange == 0)
			{
				startDate = date;
				endDate = date;
			}
			else
			{
				startDate = getDateForRange(date, -parsedDateRange);
				endDate = getDateForRange(date, parsedDateRange);
			}
			final StringBuilder formattedDate = new StringBuilder();
			formattedDate.append("[");
			formattedDate
					.append(DateFormatUtils.format(getDateForTime(startDate, 0, 0, 0), TravelservicesConstants.SOLR_DATE_FORMAT));
			formattedDate.append("Z");
			formattedDate.append(" TO ");
			formattedDate
					.append(DateFormatUtils.format(getDateForTime(endDate, 23, 59, 59), TravelservicesConstants.SOLR_DATE_FORMAT));
			formattedDate.append("Z");
			formattedDate.append("]");
			return formattedDate.toString();
		}
		return null;
	}

	protected Date getDateForTime(final Date date, final int hour, final int min, final int sec)
	{
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, sec);
		return c.getTime();
	}

	protected Date getDateForRange(final Date date, final int range)
	{
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, range);
		return c.getTime();
	}

}
