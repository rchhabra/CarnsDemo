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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SolrConfigurableDateRangeStrategyTest
{

	@InjectMocks
	private final SolrConfigurableDateRangeStrategy strategy = new SolrConfigurableDateRangeStrategy();

	@Test
	public void testWithNoDateRange()
	{
		strategy.setDateRange("0");

		final String date = strategy
				.getSolrFormattedDate(TravelDateUtils.convertStringDateToDate("01/01/2020", TravelservicesConstants.DATE_PATTERN));

		Assert.assertTrue(StringUtils.isNotBlank(date));
		Assert.assertEquals("[2020-01-01T00:00:00Z TO 2020-01-01T23:59:59Z]", date);
	}

	@Test
	public void testWithDateRange()
	{
		strategy.setDateRange("2");

		final String date = strategy
				.getSolrFormattedDate(TravelDateUtils.convertStringDateToDate("01/01/2020", TravelservicesConstants.DATE_PATTERN));

		Assert.assertTrue(StringUtils.isNotBlank(date));
		Assert.assertEquals("[2019-12-30T00:00:00Z TO 2020-01-03T23:59:59Z]", date);
	}

	@Test
	public void testForNullDateRange()
	{
		strategy.setDateRange(null);

		final String date = strategy
				.getSolrFormattedDate(TravelDateUtils.convertStringDateToDate("01/01/2020", TravelservicesConstants.DATE_PATTERN));

		Assert.assertNull(date);
	}

}
